package edu.uncc.ssdi.meetingscheduler.client.state;

public class State {

	public static final int MAIN = 1;
	public static final int LOGIN_PAGE = 2;
	public static final int LOGGED_IN = 3;
	public static final int START_POLL = 4;
	
	public static boolean isValidState(int state){
		if(state > 0 && state < 10) 
			return true; 
		return false;
	}
	
}
