package edu.uncc.ssdi.meetingscheduler.server.serviceimpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.uncc.ssdi.meetingscheduler.client.services.CreatePollService;
import edu.uncc.ssdi.meetingscheduler.server.db.QueryHelper;
import edu.uncc.ssdi.meetingscheduler.server.jsonclasses.PollCreationDateTimeJSONClass;
import edu.uncc.ssdi.meetingscheduler.server.jsonclasses.PollCreationMetadataJSONClass;

public class CreatePollServiceImpl extends RemoteServiceServlet implements CreatePollService {

	private static final long serialVersionUID = 4822941669365738877L;
	private Log log = LogFactory.getLog(CreatePollServiceImpl.class);

	@Override
	public Integer setPollDetails(String metaDataJSONString, String dateTimeJSONString) {
		log.debug("JSON String from client: " + dateTimeJSONString);
		log.debug("JSON string for poll details: " + metaDataJSONString);
		
		PollCreationMetadataJSONClass pcmjc = new Gson().fromJson(metaDataJSONString, PollCreationMetadataJSONClass.class);
		JsonArray pollDateTimeArray = new JsonParser().parse(dateTimeJSONString).getAsJsonArray();
		
		log.debug(pcmjc.getDuration());
		for(int i = 0; i < pollDateTimeArray.size() ; i ++){
			PollCreationDateTimeJSONClass jo = new Gson().fromJson(pollDateTimeArray.get(i), PollCreationDateTimeJSONClass.class);
			log.debug(jo.getDateTime());
		}

		//TODO now we've parsed the JSON data sent from the client. Now fire the update queries.
		int nextMplId = QueryHelper.getNextIndex("select max(mpl_id) from test.meeting_poll");
		int nextPollDateTimeId = QueryHelper.getNextIndex("select max(mpt_id) from test.meeting_poll_times");
		int nextMeetingId = QueryHelper.getNextIndex("select max(mtg_id) from test.meeting");
		
		try{
			
			QueryHelper.setAutoCommit(false);
			
			//fire the insert into the meeting_poll table.
			QueryHelper.fireInsert("insert into test.meeting_poll values (" + nextMplId + ", 0, NOW(), NOW())");
			
			//fire the insert into the meeting table.
			PollCreationMetadataJSONClass pcmjs = new Gson().fromJson(metaDataJSONString, PollCreationMetadataJSONClass.class);
			String s = "insert into test.meeting values (" + nextMeetingId + " ," 
					+ nextMplId + ", '" + pcmjs.getSubject() + "', " + pcmjs.getDuration()
					+ ", '" + pcmjs.getDesc() + "', FALSE, '" + pcmjs.getOrganizer() + "', '"+ pcmjs.getLocation() +"')";
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
			QueryHelper.setAutoCommit(true);
		}
		
		return nextMplId;
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getPollDetails(Integer pollId) {
		log.info("Requesting details of meeting poll id: " + pollId);
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

	@Override
	public int setPollDetails(String metaDataJSONString,
			String dateTimeJSONString, String participantsJSONString) {
		// TODO Auto-generated method stub
		return 0;
	}
}
