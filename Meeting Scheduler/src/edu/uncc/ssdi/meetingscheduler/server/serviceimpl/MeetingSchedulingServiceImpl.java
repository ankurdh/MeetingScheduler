package edu.uncc.ssdi.meetingscheduler.server.serviceimpl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.uncc.ssdi.meetingscheduler.client.services.MeetingSchedulingService;
import edu.uncc.ssdi.meetingscheduler.server.db.QueryHelper;
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
					(prmjc.getIsUnavailable() ? "FALSE" : "TRUE") +" )";
			
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
	 * @param  
	 * @return 
	 * @see 
	 */
	@Override
	public String getAvailableParticipants(int pollId) {
		
		return null;
	}

	@Override
	public boolean scheduleMeeting(int pollId, String scheduleDateTimeJSON) {
		return false;
	}

	@Override
	public String getUnavailableParticipants(int pollId) {
		return null;
	}

	@Override
	public String getBestScheduleTimes(int trackingPollId) {
		
		//first, get the poll id corresponding to the tracking poll id;
		ResultSet rs = null; 
		int pollId = -1;
		
		try {
			rs = QueryHelper.getResultSet("select mpl_id from test.meeting_poll where mpl_tracking_id = " + trackingPollId);
			
			while(rs.next())
				pollId = rs.getInt(1);
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		if(pollId == -1)
			return null;

		StringBuffer bestTimesString = new StringBuffer();
		String query = "select mprt.mprt_date_time, count(mpr.mpr_id)" + 
				" from test.meeting_poll_response mpr inner join test.meeting_poll_response_times mprt " + 
				"on mprt.mpr_id = mpr.mpr_id where mpr.mpr_available = true and mpr.mpl_id = " + pollId + 
				" group by mprt.mprt_date_time order by count(mpr.mpr_id) desc";
		
		bestTimesString.append("[\n");
		try {
			rs = QueryHelper.getResultSet(query);
			while(rs.next()){
				bestTimesString.append("{\n").append("\"datetime\":").append("\"" + rs.getString(1) + "\",\n");
				bestTimesString.append("\"count\":\"" + rs.getInt(2) + "\"\n},");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		bestTimesString.setCharAt(bestTimesString.lastIndexOf(","), ']');
		
		return bestTimesString.toString();
	}
}
