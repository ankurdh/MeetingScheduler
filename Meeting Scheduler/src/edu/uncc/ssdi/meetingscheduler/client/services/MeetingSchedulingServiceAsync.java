package edu.uncc.ssdi.meetingscheduler.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MeetingSchedulingServiceAsync {

	void registerPollResponse(int pollId, String pollMetadataJSON, String dateTimesJSONString, AsyncCallback<Boolean> callback);
	void scheduleMeeting(int pollId, String scheduleDateTimeJSON, AsyncCallback<Boolean> callback);
	void getAvailableParticipants(int pollId, AsyncCallback<String> callback);
	void getUnavailableParticipants(int pollId, AsyncCallback<String> callback);
	void getBestScheduleTimes(int pollId, AsyncCallback<String> callback);
}
