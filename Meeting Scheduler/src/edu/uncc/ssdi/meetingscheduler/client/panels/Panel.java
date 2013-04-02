package edu.uncc.ssdi.meetingscheduler.client.panels;

import edu.uncc.ssdi.meetingscheduler.client.Updatable;

public interface Panel extends Updatable {
	
	void initialize();
	com.google.gwt.user.client.ui.Panel getPanel();
}
