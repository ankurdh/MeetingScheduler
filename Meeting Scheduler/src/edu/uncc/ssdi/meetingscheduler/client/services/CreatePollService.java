package edu.uncc.ssdi.meetingscheduler.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("createPoll")
public interface CreatePollService extends RemoteService {
	
	String getPollDetails(Integer pollId);
	Integer setPollDetails(String metaDataJSONString, String dateTimeJSONString);
	Integer getPollTrackingId(Integer pollId);
	int setPollDetails(String metaDataJSONString, String dateTimeJSONString, String participantsJSONString);

}
