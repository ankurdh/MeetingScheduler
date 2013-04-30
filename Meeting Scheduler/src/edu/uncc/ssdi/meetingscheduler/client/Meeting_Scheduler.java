package edu.uncc.ssdi.meetingscheduler.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

import edu.uncc.ssdi.meetingscheduler.client.panels.PanelHelper;
import edu.uncc.ssdi.meetingscheduler.client.state.State;
import edu.uncc.ssdi.meetingscheduler.client.state.StateHelper;
import edu.uncc.ssdi.meetingscheduler.client.state.StateListener;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Meeting_Scheduler implements EntryPoint, StateListener {

	public void onModuleLoad() {
		
		StateHelper.registerStateListener(this);
		StateHelper.setState(State.MAIN);
		
	}

	@Override
	public void onStateChanged(int state) {
		
		RootPanel rp = RootPanel.get("panel");
		if(rp == null)
			return;
		
		switch(state){
		case State.MAIN:
			RootPanel.get("panel").clear();
			RootPanel.get("panel").add(PanelHelper.getPanel(State.MAIN));
			break;
			
		case State.LOGGED_IN:
			RootPanel.get("panel").clear();
			RootPanel.get("panel").add(PanelHelper.getPanel(State.LOGGED_IN));
			break;
			//TODO do the login page.
			
			
		case State.START_POLL:
			//TODO do the start poll page
			RootPanel.get("panel").clear();
			RootPanel.get("panel").add(PanelHelper.getPanel(State.START_POLL));
		}
		
	}
}
