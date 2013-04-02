package edu.uncc.ssdi.meetingscheduler.client.communication;

import edu.uncc.ssdi.meetingscheduler.client.JSONDataRequestor;

public class ServerHandle {
	
	private JSONDataRequestor r = null;
	
	public void registerDataRequestor(JSONDataRequestor r){
		this.r = r;
	}

}
