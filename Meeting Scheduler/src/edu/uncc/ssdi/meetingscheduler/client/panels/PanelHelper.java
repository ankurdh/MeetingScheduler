package edu.uncc.ssdi.meetingscheduler.client.panels;

import edu.uncc.ssdi.meetingscheduler.client.state.State;
import edu.uncc.ssdi.meetingscheduler.client.state.StateHelper;

public class PanelHelper {
	
	public static com.google.gwt.user.client.ui.Panel getPanel(int state){

		switch(StateHelper.getState()){
		case State.MAIN:
			return new LoginPanel().getPanel();
			
		case State.START_POLL:
			return new CreatePollPanel().getPanel();
			
		default:
			throw new IllegalArgumentException("Unknown or Unimplemented State: " + StateHelper.getState());
		}	
	}
}
