package edu.uncc.ssdi.meetingscheduler.server.serviceimpl;

import java.sql.ResultSet;
import java.sql.SQLException;

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
		
		try{
			
			QueryHelper.setAutoCommit(false);
			
			QueryHelper.fireInsert("insert into test.meeting_poll values (" + nextMplId + ", 0, NOW(), NOW())");
			
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

	@Override
	public String getPollDetails(Integer pollId) {
		log.info("Requesting details of meeting poll id: " + pollId);
		String s = ""; 
		
		try {
			ResultSet rs = QueryHelper.getResultSet("select mpt_date_time from test.meeting_poll_times where mpl_id = " + pollId);
			
			while(rs.next()){
				s+=rs.getTimestamp("mpt_date_time").toString();
				s+="\n";
			}
			
			return s;
			
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
