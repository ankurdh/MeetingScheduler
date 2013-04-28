package edu.uncc.ssdi.meetingscheduler.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("meetingschedulingservice")
public interface MeetingSchedulingService extends RemoteService  {
	
	public boolean registerPollResponse(int pollId, String pollMetadataJSON, String dateTimesJSONString);
	public String getAvailableParticipants(int pollId);
	public boolean scheduleMeeting(int pollId, String scheduleDateTimeJSON);
	public String getUnavailableParticipants(int pollId);
	public String getBestScheduleTimes(int pollId);
}
