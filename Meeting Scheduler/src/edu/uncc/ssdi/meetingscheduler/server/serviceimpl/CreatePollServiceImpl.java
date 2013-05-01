package edu.uncc.ssdi.meetingscheduler.server.serviceimpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.uncc.ssdi.meetingscheduler.client.services.CreatePollService;
import edu.uncc.ssdi.meetingscheduler.server.db.QueryHelper;
import edu.uncc.ssdi.meetingscheduler.server.email.Email;
import edu.uncc.ssdi.meetingscheduler.server.email.EmailFactory;
import edu.uncc.ssdi.meetingscheduler.server.email.EmailHelper;
import edu.uncc.ssdi.meetingscheduler.server.jsonclasses.NameEmailIdJSONClass;
import edu.uncc.ssdi.meetingscheduler.server.jsonclasses.PollCreationDateTimeJSONClass;
import edu.uncc.ssdi.meetingscheduler.server.jsonclasses.PollCreationMetadataJSONClass;

public class CreatePollServiceImpl extends RemoteServiceServlet implements CreatePollService {

	private static final long serialVersionUID = 4822941669365738877L;
	private Log log = LogFactory.getLog(CreatePollServiceImpl.class);

	@Override
	public Integer setPollDetails(String metaDataJSONString, String dateTimeJSONString) {
		
		JsonArray pollDateTimeArray = new JsonParser().parse(dateTimeJSONString).getAsJsonArray();

		int nextMplId = QueryHelper.getNextIndex("select max(mpl_id) from test.meeting_poll");
		int nextPollDateTimeId = QueryHelper.getNextIndex("select max(mpt_id) from test.meeting_poll_times");
		int nextMeetingId = QueryHelper.getNextIndex("select max(mtg_id) from test.meeting");
		
		try{
			
			QueryHelper.setAutoCommit(false);
			
			//fire the insert into the meeting_poll table.
			QueryHelper.fireInsert("insert into test.meeting_poll values (" + nextMplId + ", 0, NOW(), NOW()," + new Random().nextInt(10000) + nextMplId + ")");
			
			//fire the insert into the meeting table.
			PollCreationMetadataJSONClass pcmjs = new Gson().fromJson(metaDataJSONString, PollCreationMetadataJSONClass.class);
			String s = "insert into test.meeting values (" + nextMeetingId + " ," 
					+ nextMplId + ", '" + pcmjs.getSubject() + "', " + pcmjs.getDuration()
					+ ", '" + pcmjs.getDesc() + "', FALSE, '" + pcmjs.getOrganizer() + "', '"+ pcmjs.getLocation() +"', NOW())";
			QueryHelper.fireInsert(s);			
			
			for(int i = 0; i < pollDateTimeArray.size() ; i ++){
				PollCreationDateTimeJSONClass jo = new Gson().fromJson(pollDateTimeArray.get(i), PollCreationDateTimeJSONClass.class);
				QueryHelper.fireInsert("insert into test.meeting_poll_times values (" + (nextPollDateTimeId++) + ", " + nextMplId + ", '" + jo.getDateTime() +"')");
				
			}
			
			QueryHelper.makeCommit();
			
		} catch (SQLException se){
			se.printStackTrace();
			return -1;
		} finally {
			try{
				QueryHelper.setAutoCommit(true);
			} catch (SQLException se){
				log.error("Failed to set autocommit to true.", se);
			}
		}
		
		return nextMplId;
	}
	
	private String getPollDetailsForPollId(Integer pollId){
		
		StringBuffer jsonString = new StringBuffer();
		jsonString.append("{\n");
		
		try {
			ResultSet rs = QueryHelper.getResultSet("select mtg_subject, mtg_duration, mtg_desc, mtg_scheduled_by, mtg_location" + 
						" from test.meeting where mpl_id = " + pollId);
			
			while(rs.next()){
				jsonString.append("\"subject\":\"" + rs.getString(1) + "\",\n");
				jsonString.append("\"duration\":\"" + rs.getInt(2) + "\",\n");
				jsonString.append("\"desc\":\"" + rs.getString(3) + "\",\n");
				jsonString.append("\"scheduled_by\":\"" + rs.getString(4) + "\",\n");
				jsonString.append("\"location\":\"" + rs.getString(5) + "\",\n");
			}
			
			jsonString.append("\"datetimes\": [");
			
			rs = QueryHelper.getResultSet("select DATE(mpt_date_time) as date, HOUR(mpt_date_time) as hour, MINUTE(mpt_date_time) as min from test.meeting_poll_times where mpl_id = " + pollId);
			
			while(rs.next()){
				java.sql.Date date = rs.getDate("date");
				String hour = rs.getString("hour");
				String min = rs.getString("min");
				
				if(hour.length() == 1)
					hour = "0"+hour;
				
				if(min.length() == 1)
					min = "0"+min;
				
				jsonString.append("\"" + date.toString() + " " + hour + ":" + min + ":00"+ "\",");
				jsonString.append("\n");
			}
			
			jsonString.setCharAt(jsonString.lastIndexOf(","), ']');
			
			return jsonString.append("\n}").toString();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}

	/**
	 * This method evaluates if the passed pollId is a Poll ID or a tracking Poll Id and accordingly returns 
	 * a JSON string for the requested data. 
	 * 
	 * @param pollId : the poll id or the tracking poll id entered by the user. 
	 * @return returns the requested JSON string or if the parameter is neither a poll id or a tracking poll id, 
	 * an error message is returned.
	 */
	@Override
	public String getPollDetails(Integer pollId) {
				
		if(QueryHelper.doesRowExistAs("select mpl_id from test.meeting_poll where mpl_id = " + pollId)){
			//this is a poll id
			log.info("Requesting details of meeting poll id: " + pollId);		
			return getPollDetailsForPollId(pollId);
		} else if (QueryHelper.doesRowExistAs("select mpl_id from test.meeting_poll where mpl_tracking_id = " + pollId)){
			//this is a tracking poll id.
			log.info("Requesting tracking details of meeting poll tracking id: " + pollId);		
			return getPollDetalisForTracking(pollId);
		} else {
			//invalid poll_id number
			return "Invalid Poll Id";
		}
	}

	/**
	 * Prepares a JSON string of participant name and his available dates. Example: 
	 * <br>[<br>
	 * 		{<br>
	 *			"name":"Participant 1",<br> 
	 *			"available":"true",<br>
	 *			"comments":"comments",<br>
	 * 			"availableDates":["date-time1", "date-time2"]<br>
	 * 		},<br>
	 * 		{<br>
	 *			"name":"Participant 2",<br>
	 *			"available":"true",<br>
	 *			"comments":"comments",<br> 
	 * 			"availableDates":["date-time1", "date-time2"]<br>
	 * 		}<br>
	 * ]<br>
	 * @param trackingPollId the tracking poll id 
	 * @return the JSON string. 
	 */
	private String getPollDetalisForTracking(Integer trackingPollId) {
		
		StringBuffer trackingJSONString = new StringBuffer();
		Map<Integer , String> responderIdNameMap = new HashMap<Integer, String>();
		int pollId = -1;
		
		try {
			
			//first, get the poll id corresponding to the tracking poll id;
			ResultSet rs = QueryHelper.getResultSet("select mpl_id from test.meeting_poll where mpl_tracking_id = " + trackingPollId);
			
			while(rs.next())
				pollId = rs.getInt(1);
			
			if(pollId == -1)
				return null;
			
			String query = "select mpr.mpr_id, mpr.mpr_responder_name from test.meeting_poll_response mpr " + 
					" where mpr.mpr_responder_name in (select distinct(mpr.mpr_responder_name) from test.meeting_poll_response mpr " +
					" where mpr.mpl_id = " + pollId + ") and mpr.mpl_id = " + pollId;
			
			rs = QueryHelper.getResultSet(query);
			
			while(rs.next())
				responderIdNameMap.put(rs.getInt(1), rs.getString(2));
			
			/* 
			 * ok, now we have populated the id-name map. If there are no responses, then return null. 
			 * Otherwise, fire query for each of the participant and fetch his available times.
			 */ 
			
			if(responderIdNameMap.size() == 0)
				return null;
			
			trackingJSONString.append("TrackingInfo-[\n");
			
			for(Integer participantId : responderIdNameMap.keySet()){
				trackingJSONString.append("{\n");
				boolean userAvailable = false;
				
				//first get his availability and comments.
				ResultSet responderPollInfoRS = QueryHelper.getResultSet("select mpr.mpr_available, mpr.mpr_responder_name, mpr.mpr_comments " +
						" from test.meeting_poll_response mpr " + 
						" where mpr.mpr_id = " + participantId + " and mpr.mpl_id = " + pollId);
				
				while(responderPollInfoRS.next()){
					trackingJSONString.append("\"name\":\"" + responderIdNameMap.get(participantId) + "\",\n");
					trackingJSONString.append("\"available\":\"" + responderPollInfoRS.getBoolean(1) + "\",\n");
					trackingJSONString.append("\"comments\":\"" + responderPollInfoRS.getString(3) + "\",\n");
					
					userAvailable = responderPollInfoRS.getBoolean(1);
					
				}
				
				//if he's not available, then populate null for date-times. else fire another query and get his date-time details.				
				if(!userAvailable){
					//user unavailable
					trackingJSONString.append("\"dateTimes\":[]");
				} else {
					//user available. Get the date-times he's available for.
					ResultSet availableDateTimesRS = QueryHelper.getResultSet("select mprt.mprt_date_time " +
							" from test.meeting_poll_response_times mprt inner join test.meeting_poll_response mpr " +
							" on mprt.mpr_id = mpr.mpr_id " + 
							" where mpr.mpl_id = " + pollId + " and mpr.mpr_id = " + participantId);
					trackingJSONString.append("\"dateTimes\":[\n");
					
					while(availableDateTimesRS.next()){
						trackingJSONString.append("\"" + availableDateTimesRS.getString(1) + "\",");
					}
					
					trackingJSONString.setCharAt(trackingJSONString.lastIndexOf(","), ']');
					trackingJSONString.append("\n");
				}
				
				trackingJSONString.append("},\n");
			}
			
			trackingJSONString.setCharAt(trackingJSONString.lastIndexOf(","), ']');
			return  trackingJSONString.append("\n").toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public int setPollDetails(String metaDataJSONString, String dateTimeJSONString, String participantsJSONString, Integer userId) {
		int pollId = setPollDetails(metaDataJSONString, dateTimeJSONString);
		int nextInviteeId = QueryHelper.getNextIndex("select max(mpi_id) from test.meeting_poll_invitees");
		
		JsonArray nameEmailIdJSONArray = new JsonParser().parse(participantsJSONString).getAsJsonArray();
		
		try {
			QueryHelper.setAutoCommit(false);
			
			for(int i = 0 ; i < nameEmailIdJSONArray.size(); i++){
				NameEmailIdJSONClass neijc = new Gson().fromJson(nameEmailIdJSONArray.get(i), NameEmailIdJSONClass.class);
				
				String query = "insert into test.meeting_poll_invitees values (" + nextInviteeId++ +
						"," + userId + "," + pollId + ",'" + neijc.getName() + "','" + neijc.getEmailId() + "')";
				
				QueryHelper.fireInsert(query);
			}
			
			QueryHelper.makeCommit();
			QueryHelper.setAutoCommit(true);
			
			//DB update successful. Sendout emails to the members.
			ArrayList<String> subjectAndBody = EmailHelper.getSubjectAndBodyForPollCreation(pollId, getPollStarterName(metaDataJSONString), "http://127.0.0.1:8888/RespondToRequest.html?gwt.codesvr=127.0.0.1:9997");
			Email emailer = EmailFactory.getInstance();
			
			for(int i = 0 ; i < nameEmailIdJSONArray.size(); i++){
				NameEmailIdJSONClass neijc = new Gson().fromJson(nameEmailIdJSONArray.get(i), NameEmailIdJSONClass.class);
				
				try {
					emailer.send(neijc.getEmailId(), subjectAndBody.get(0), subjectAndBody.get(1));
					log.debug("email sent successfully to " + neijc.getEmailId());
				} catch (AddressException e) {
					log.error("Failed to send email.", e);
				} catch (MessagingException e) {
					log.error("Failed to send email.", e);
				}
			}
			
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			return -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
		
		return pollId;
	}

	private String getPollStarterName(String metaDataJSONString) {
		return new Gson().fromJson(metaDataJSONString, PollCreationMetadataJSONClass.class).getOrganizer();		
	}

	@Override
	public Integer getPollTrackingId(Integer pollId) {
		Integer trackingId = -1;
		try {
			ResultSet rs = QueryHelper.getResultSet("select mpl_tracking_id from test.meeting_poll where mpl_id = " + pollId);
			if (rs.next()){
				trackingId = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return trackingId;
	}
}
