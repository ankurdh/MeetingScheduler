package edu.uncc.ssdi.meetingscheduler.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MeetingSchedulingServiceAsync {

	void registerPollResponse(int pollId, String pollMetadataJSON, String dateTimesJSONString, AsyncCallback<Boolean> callback);
	void scheduleMeeting(int trackingPollId, String scheduleDateTimeJSON, AsyncCallback<Boolean> callback);
	void getAvailableParticipantsNameComments(int trackingPollId, AsyncCallback<String> callback);
	void getUnavailableParticipants(int trackingPollId, AsyncCallback<String> callback);
	void getBestScheduleTimes(int trackingPollId, AsyncCallback<String> callback);
	void getDateTimeParticipants(Integer trackingPollId, AsyncCallback<String> asyncCallback);
	void getAvailableParticipantsFor(int trackingPollId, String dateTime, AsyncCallback<String> callback);
}
