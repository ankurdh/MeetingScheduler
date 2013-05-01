package edu.uncc.ssdi.meetingscheduler.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("meetingschedulingservice")
public interface MeetingSchedulingService extends RemoteService  {
	
	public boolean registerPollResponse(int pollId, String pollMetadataJSON, String dateTimesJSONString);
	public String getAvailableParticipantsNameComments(int trackingPollId);
	public boolean scheduleMeeting(int pollId, String scheduleDateTimeJSON);
	public String getUnavailableParticipants(int trackingPollId);
	public String getBestScheduleTimes(int trackingPollId);
	public String getDateTimeParticipants(Integer trackingPollId);
	public String getAvailableParticipantsFor(int trackingPollId, String dateTime);
	public String getMeetingMetadata(int pollId);
	public boolean remindParticipants(int pollId);
	
}
