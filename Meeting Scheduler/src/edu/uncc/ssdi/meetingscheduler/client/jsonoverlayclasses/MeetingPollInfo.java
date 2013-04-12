package edu.uncc.ssdi.meetingscheduler.client.jsonoverlayclasses;

import com.google.gwt.core.client.JavaScriptObject;

public class MeetingPollInfo extends JavaScriptObject {

	protected MeetingPollInfo(){ }
	
	public final native String getSubject() /*-{ return this.subject; }-*/;
	public final native String getDuration() /*-{ return this.duration }-*/;
	public final native String getDesc() /*-{ return this.desc }-*/;
	public final native String getOrganizer() /*-{ return this.scheduled_by}-*/;
	public final native JavaScriptObject getDates() /*-{ return this.datetimes }-*/;
	public final native String getLocation() /*-{ return this.location }-*/;
	
}
