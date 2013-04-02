package edu.uncc.ssdi.meetingscheduler.client.panels;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RegisteredUserPanel implements Panel {

	private HorizontalPanel mainPanel;
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize() {
		mainPanel = new HorizontalPanel();
		
		VerticalPanel vp = getUserOptionsPanel();
		
	}

	private VerticalPanel getUserOptionsPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public com.google.gwt.user.client.ui.Panel getPanel() {
		// TODO Auto-generated method stub
		return null;
	}

}
