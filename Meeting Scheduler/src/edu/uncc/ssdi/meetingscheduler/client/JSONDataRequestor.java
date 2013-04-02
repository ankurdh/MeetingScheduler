package edu.uncc.ssdi.meetingscheduler.client;

import edu.uncc.ssdi.meetingscheduler.client.communication.ServerHandle;

public interface JSONDataRequestor {

	void requestData(ServerHandle s);
	void registerWithServerHandle(ServerHandle s);
	void onDataRecieved(String jsonData);
	
}
