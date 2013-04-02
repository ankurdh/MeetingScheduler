package edu.uncc.ssdi.meetingscheduler.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("login")
public interface LoginService extends RemoteService {

	public boolean verifyRegisteredUser(String username, String password);
	
}
