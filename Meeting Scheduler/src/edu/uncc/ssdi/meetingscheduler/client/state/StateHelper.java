package edu.uncc.ssdi.meetingscheduler.client.state;

public class StateHelper {
	
	public static int STATE;
	public static StateListener stateListener;	
	
	static 
	{
		STATE = State.MAIN;
	}
	
	public static void registerStateListener(StateListener sl){
		stateListener = sl;
	}
	
	public static int getState(){
		return STATE;
	}
	
	public static void setState(int state) throws IllegalArgumentException{
		if(State.isValidState(state)){
			STATE = state;
			stateListener.onStateChanged(state);
		} else 
			throw new IllegalArgumentException("Invalid state: " + state + " passed.");
	}
}
