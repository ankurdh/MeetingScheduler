package edu.uncc.ssdi.meetingscheduler.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MeetingSchedulingServiceAsync {

	void registerPollResponse(int pollId, String pollMetadataJSON, String dateTimesJSONString, AsyncCallback<Boolean> callback);
	void scheduleMeetingAndGetBestMeetingTimes(int pollId, AsyncCallback<String> callback);
	void scheduleMeeting(int pollId, String scheduleDateTimeJSON, AsyncCallback<Boolean> callback);
	;
}
