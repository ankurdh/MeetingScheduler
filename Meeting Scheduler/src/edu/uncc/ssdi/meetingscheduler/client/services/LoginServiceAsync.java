package edu.uncc.ssdi.meetingscheduler.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync {

	void verifyRegisteredUser(String username, String password, AsyncCallback<Boolean> callback);

}

