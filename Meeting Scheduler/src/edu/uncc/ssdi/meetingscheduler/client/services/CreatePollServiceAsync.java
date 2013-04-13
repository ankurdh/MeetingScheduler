package edu.uncc.ssdi.meetingscheduler.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CreatePollServiceAsync {

	void setPollDetails(String metaDataJSONString, String dateTimeJSONString, AsyncCallback<Integer> callback);
	void setPollDetails(String metaDataJSONString, String dateTimeJSONString, String participantsJSONString, AsyncCallback<Integer> callback);
	void getPollDetails(Integer pollId, AsyncCallback<String> callback);
	void getPollTrackingId(Integer pollId, AsyncCallback<Integer> callback);

}