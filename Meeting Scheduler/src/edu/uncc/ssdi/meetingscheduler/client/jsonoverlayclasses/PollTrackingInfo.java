package edu.uncc.ssdi.meetingscheduler.client.jsonoverlayclasses;

import com.google.gwt.core.client.JavaScriptObject;

public class PollTrackingInfo extends JavaScriptObject {
	
	protected PollTrackingInfo(){}
	
	public final native String getDate() /*-{ return this.datetime; }-*/;
	public final native String getCount() /*-{ return this.count; }-*/;

}
