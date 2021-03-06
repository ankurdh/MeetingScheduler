package edu.uncc.ssdi.meetingscheduler.server.serviceimpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.uncc.ssdi.meetingscheduler.client.services.MeetingSchedulingService;
import edu.uncc.ssdi.meetingscheduler.server.db.QueryHelper;
import edu.uncc.ssdi.meetingscheduler.server.email.EmailFactory;
import edu.uncc.ssdi.meetingscheduler.server.email.EmailHelper;
import edu.uncc.ssdi.meetingscheduler.server.jsonclasses.PollCreationDateTimeJSONClass;
import edu.uncc.ssdi.meetingscheduler.server.jsonclasses.PollResponseMetadataJSONClass;


/**
 * Desc: <br>
 * This class is the server handle for the Meeting Scheduling Service. This class is invoked by the 
 * client to update the database about<br>
 * 
 * -> Response from potential attendees <br>
 * -> Calculate the best possible date-time(s) pair(s) for the meeting to be scheduled.<br>
 * -> Schedule the meeting <br>
 * <br>
 * This class invokes the services offered by {@link QueryHelper} for database updations.  
 * @author Ankur Huralikoppi
 * 
 */

public class MeetingSchedulingServiceImpl extends RemoteServiceServlet implements MeetingSchedulingService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Log log = LogFactory.getLog(MeetingSchedulingServiceImpl.class);
	
	/**
	 * @author Ankur Huralikoppi
	 * @param pollId Poll Id for which the response is sent
	 * @param pollMetadataJSON Poll responder meta data. Has Responder name, availability and comments.
	 * @param dateTimesJSONString Has the list of date-times the responder is available for the meeting. 
	 * @return true if database updated successfully, else false.
	 * @see {@link QueryHelper}
	 * @category Server's implementation of the MeetingSchedulingService.
	 */
	
	@Override
	public boolean registerPollResponse(int pollId, String pollMetadataJSON, String dateTimesJSONString) {
		
		//update the poll meta data and the response in the database. 
		log.info("Request to submit poll responses for poll: " + pollId);
		log.debug("Poll Metadata String: " + pollMetadataJSON);
		log.debug("Poll Datetimes String: " + dateTimesJSONString);
		
		//Parse the JSON string here. 
		PollResponseMetadataJSONClass prmjc = new Gson().fromJson(pollMetadataJSON, PollResponseMetadataJSONClass.class);
		JsonArray pollDateTimeArray = new JsonParser().parse(dateTimesJSONString).getAsJsonArray();
		
		try {
			QueryHelper.setAutoCommit(false);
			
			int currentPollResponseId = QueryHelper.getNextIndex("select max(mpr_id) from test.meeting_poll_response");
			int currentPollResponseTimeId = QueryHelper.getNextIndex("select max(mprt_id) from test.meeting_poll_response_times");
			
			String meetingPollResponseTableQuery = "insert into test.meeting_poll_response values (" +  
					currentPollResponseId + ", " + pollId + ",'" + prmjc.getName() + "','" +
					prmjc.getComments() + "'," + 
					(prmjc.getIsUnavailable() ? "FALSE" : "TRUE") +" , '" + prmjc.getEmail() + "')";
			
			//first update the poll response table.
			QueryHelper.fireInsert(meetingPollResponseTableQuery);
			log.debug("Inserted into meeting poll response table successfully.");
			
			for(int i = 0; i < pollDateTimeArray.size() ; i ++){
				PollCreationDateTimeJSONClass jo = new Gson().fromJson(pollDateTimeArray.get(i), PollCreationDateTimeJSONClass.class);
				QueryHelper.fireInsert("insert into test.meeting_poll_response_times values (" + (currentPollResponseTimeId++) + ", " + currentPollResponseId + ", '" + jo.getDateTime() +"')");
			}
			log.debug("Inserted into meeting poll response times table successfully.");
			
			QueryHelper.makeCommit();
			
		} catch (SQLException se){
			log.error("Failed to save poll response in the database", se);
			return false;
		} finally {
			try{
				QueryHelper.setAutoCommit(true);
			} catch (SQLException se){
				log.error("Failed to set autocommit to true.", se);
			}
		}
		
		return true;
	}

	/**
	 * Desc: <br>
	 * @author Ankur Huralikoppi
	 * @param  tracking poll id: the tracking id for the poll
	 * @return the JSON string of the available participant name and comments.
	 */
	@Override
	public String getAvailableParticipantsNameComments(int trackingPollId) {
		Integer pollId = getPollIdFromTrackingPollId(trackingPollId);
		StringBuffer jsonString = new StringBuffer();
		String query = "select mpr.mpr_responder_name, mpr.mpr_comments from test.meeting_poll_response mpr " +
				" where mpr.mpl_id = " + pollId + " and mpr.mpr_available = TRUE";
		ResultSet rs = null;
		boolean valueExist = false;
		
		try {
			rs = QueryHelper.getResultSet(query);
			jsonString.append("[\n");
			
			while(rs.next()){
				valueExist = true;
				jsonString.append("{\n").append("\"name\":\"" + rs.getString(1) + "\",\n\"comments\":\"" + rs.getString(2) + "\"\n},");
			}
			
			if(valueExist)
				jsonString.setCharAt(jsonString.lastIndexOf(","), ']');
			else 
				return null;
			
			return jsonString.toString();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean scheduleMeeting(int trackingPollId, String scheduleDateTime) {
		Integer pollId = getPollIdFromTrackingPollId(trackingPollId);
		String query = "update test.meeting set mtg_scheduled = TRUE, mtg_scheduled_dttm = '" +  scheduleDateTime + "' where mpl_id = " + pollId;
		
		try {
			QueryHelper.fireUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		String participantsQuery = "select mpr.mpr_email from test.meeting_poll_response mpr " +
				" where mpr.mpl_id = " + pollId + " and mpr.mpr_available = TRUE";
		
		String meetingMetadataQuery = "select mtg_subject, mtg_duration, mtg_desc, mtg_scheduled_by, mtg_location" + 
				" from test.meeting where mpl_id = " + pollId;
		ArrayList<String> subjectAndBody = null, 
				participantList = new ArrayList<String>();
		
		try {
			ResultSet rs = QueryHelper.getResultSet(meetingMetadataQuery);
			
			while (rs.next()){
				subjectAndBody = EmailHelper.getSubjectAndBodyForScheduledMeeting(rs.getString(1)
						, rs.getInt(2),rs.getString(5), rs.getString(4), rs.getString(3), scheduleDateTime);
			}
			
			rs = QueryHelper.getResultSet(participantsQuery);
			
			while(rs.next())
				participantList.add(rs.getString(1));
			
			try {
				EmailFactory.getInstance().send(participantList, subjectAndBody.get(0), subjectAndBody.get(1));
			} catch (AddressException e) {
				log.error("Failed to send email: ", e);
			} catch (MessagingException e) {
				log.error("Failed to send email: ", e);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
				
		return true;
	}

	@Override
	public String getUnavailableParticipants(int trackingPollId) {
		Integer pollId = getPollIdFromTrackingPollId(trackingPollId);
		String query = "select mpr.mpr_responder_name, mpr.mpr_comments from test.meeting_poll_response mpr " +
						" where mpr.mpr_responder_name in (select distinct(mpr.mpr_responder_name) from test.meeting_poll_response mpr " + 
						" where mpr.mpl_id = " + pollId + " and mpr.mpr_available = FALSE) and mpr.mpl_id = " + pollId;
		StringBuffer jsonString = new StringBuffer();
		
		ResultSet rs = null;
		Boolean valueExist = false;
		
		try {
			rs = QueryHelper.getResultSet(query);
			jsonString.append("[\n");
			
			while(rs.next()){
				valueExist = true;
				jsonString.append("{\n").append("\"name\":\"" + rs.getString(1) + "\",\n\"comments\":\"" + rs.getString(2) + "\"\n},");
			}
			
			if(valueExist)
				jsonString.setCharAt(jsonString.lastIndexOf(","), ']');
			else 
				return null;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		return jsonString.toString();
	}

	private Integer getPollIdFromTrackingPollId(int trackingPollId){
		ResultSet rs = null; 
		int pollId = -1;
		String query = "select mpl_id from test.meeting_poll where mpl_tracking_id = " + trackingPollId;
		try {
			rs = QueryHelper.getResultSet(query);
			
			while(rs.next()){
				log.debug("Requested poll ID : " + rs.getInt(1));
				pollId = rs.getInt(1);
			}
			
			return pollId;
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (NullPointerException e2){
			//that's okay!
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if(pollId == -1)
			return null;

		return null;
	}
	
	@Override
	public String getBestScheduleTimes(int trackingPollId) {
		ResultSet rs = null;
		int pollId = getPollIdFromTrackingPollId(trackingPollId);
		StringBuffer bestTimesString = new StringBuffer();
		String query = "select DATE(mprt.mprt_date_time) as date, HOUR(mprt.mprt_date_time) as hour, MINUTE(mprt.mprt_date_time) as min, count(mpr.mpr_id)" + 
				" from test.meeting_poll_response mpr inner join test.meeting_poll_response_times mprt " + 
				" on mprt.mpr_id = mpr.mpr_id where mpr.mpr_available = true and mpr.mpl_id = " + pollId + 
				" group by mprt.mprt_date_time order by count(mpr.mpr_id) desc";
		
		bestTimesString.append("[\n");
		try {
			rs = QueryHelper.getResultSet(query);
			while(rs.next()){
				java.sql.Date date = rs.getDate("date");
				String hour = rs.getString("hour");
				String min = rs.getString("min");
				
				if(hour.length() == 1)
					hour = "0"+hour;
				
				if(min.length() == 1)
					min = "0"+min;
				
				bestTimesString.append("{\n").append("\"datetime\":").append("\"" + date.toString() + " " + hour + ":" + min + ":00" + "\",\n");
				bestTimesString.append("\"count\":\"" + rs.getInt(4) + "\"\n},");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		bestTimesString.setCharAt(bestTimesString.lastIndexOf(","), ']');
		
		return bestTimesString.toString();
	}

	@Override
	public String getDateTimeParticipants(Integer trackingPollId) {
		StringBuffer jsonString = new StringBuffer();
		Integer pollId = getPollIdFromTrackingPollId(trackingPollId);		
		String query = "select mprt.mprt_date_time, mpr.mpr_responder_name " + 
				" from test.meeting_poll_response mpr inner join test.meeting_poll_response_times mprt " + 
				" on mprt.mpr_id = mpr.mpr_id where mpr.mpl_id = " + pollId + " and mpr.mpr_available = TRUE" +
				" order by mprt.mprt_date_time";
		ResultSet rs = null;
		boolean rowExist = false;
		
		try {
			rs = QueryHelper.getResultSet(query);
			jsonString.append("[\n");
			while(rs.next()){
				rowExist = true;
				jsonString.append("{\n\"datetime\":\"" + rs.getString(1) + "\",\n\"name\":\"" + rs.getString(2) + "\"\n},");
				
			}
			
			if(rowExist)
				jsonString.setCharAt(jsonString.lastIndexOf(","), ']');
			
			return jsonString.toString();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getAvailableParticipantsFor(int trackingPollId, String dateTime) {
		StringBuffer str = new StringBuffer();
		int pollId = getPollIdFromTrackingPollId(trackingPollId);
		String query = "select distinct(mpr.mpr_responder_name), mpr.mpr_comments " + 
				" from test.meeting_poll_response mpr, test.meeting_poll_times mpt , test.meeting_poll_response_times mprt " + 
				" where mpt.mpl_id = mpr.mpl_id and mpr.mpr_id = mprt.mpr_id " +
				" and mpr.mpl_id = " + pollId + " and mpr.mpr_available = TRUE and mprt.mprt_date_time = '" + dateTime + "'";
		
		ResultSet rs = null;
		
		try {
			rs = QueryHelper.getResultSet(query);
			boolean exists = false;
			while(rs.next()){
				exists = true;
				str.append(rs.getString(1) + ":" + rs.getString(2) + ",");
			}
			
			if(exists)
				str.setCharAt(str.lastIndexOf(","), ' ');
			
			return str.toString();
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getMeetingMetadata(int pollId) {
		//TODO 
		
		return null;
	}

	@Override
	public boolean remindParticipants(int trackingPollId) {
		Integer pollId = getPollIdFromTrackingPollId(trackingPollId);
		String subject = "";
		
		ResultSet rs = null;
		
		try {
			rs = QueryHelper.getResultSet("select mtg_subject from test.meeting where mpl_id = " + pollId);
			
			while(rs.next())
				subject = rs.getString(1);
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		
		ArrayList<String> unresponsiveParticipants = new ArrayList<String>();
		ArrayList<String> subjectAndBody = EmailHelper.getSubjectAndBodyForPollReminder(subject, pollId);
		String query = "select mpi.mpi_invitee_mailid " +  
		" from test.meeting_poll_invitees mpi where mpi.mpl_id = " + pollId + " and mpi.mpi_invitee_mailid not in " + 
		" ( select mpr.mpr_email from test.meeting_poll_response mpr where mpr.mpl_id = " + pollId + ")";

		rs = null;
		
		try {
			rs = QueryHelper.getResultSet(query);
			
			while(rs.next())
				unresponsiveParticipants.add(rs.getString(1));
			
			try {
				EmailFactory.getInstance().send(unresponsiveParticipants, subjectAndBody.get(0), subjectAndBody.get(1));
			} catch (AddressException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			}			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
