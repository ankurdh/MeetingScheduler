package edu.uncc.ssdi.meetingscheduler.client.panels;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.api.gwt.client.GoogleApiRequestTransport;
import com.google.api.gwt.client.OAuth2Login;
import com.google.api.gwt.services.calendar.shared.Calendar;
import com.google.api.gwt.services.calendar.shared.Calendar.CalendarAuthScope;
import com.google.api.gwt.services.calendar.shared.Calendar.CalendarListContext.ListRequest.MinAccessRole;
import com.google.api.gwt.services.calendar.shared.model.CalendarList;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.requestfactory.shared.Receiver;

import edu.uncc.ssdi.meetingscheduler.client.state.State;
import edu.uncc.ssdi.meetingscheduler.client.state.StateHelper;
import edu.uncc.ssdi.meetingscheduler.client.state.UserLoginState;

public class CreateLoginPagePanel implements Panel{

	//CreatePollServiceAsync createPollService = GWT.create(CreatePollService.class);

	private HorizontalPanel horizontalPanel;
	private Button startPollregistered = new Button();
	static final String CLIENT_ID = "545574083114.apps.googleusercontent.com";
	static final String API_KEY = "AIzaSyAHvcrUNGmQ3ffesG-eQs35yhD5AfozzoU";
	static final String APPLICATION_NAME = "SSDI/1.0";

	private static final Calendar calendar = GWT.create(Calendar.class);

	HorizontalPanel controlsPanel = new HorizontalPanel(); 

	public CreateLoginPagePanel()
	{
		horizontalPanel = new HorizontalPanel();
		initialize();
	}
	@Override
	public void update() {

	}
	
	//google cal panel
	//login
	   void login() {
		    OAuth2Login.get().authorize(CLIENT_ID, CalendarAuthScope.CALENDAR,
		        new Callback<Void, Exception>() {
		          @Override
		          public void onSuccess(Void v) {
		        	  UserLoginState.setUserLoginState(true);
		            getCalendarId();
		          }

		          @Override
		          public void onFailure(Exception e) {
		            GWT.log("Auth failed:", e);
		          }
		        });
		  }
	  //login ends
	  
	  // get calendar begins
	   void getCalendarId() {
		    // We need to find an ID of a calendar that we have permission to write events to. We'll just
		    // pick the first one that gets returned, and we will delete the event when we're done.
		    calendar.calendarList().list().setMinAccessRole(MinAccessRole.OWNER)
		        .fire(new Receiver<CalendarList>() {
		          @Override
		          public void onSuccess(CalendarList list) {
		            String calendarId = list.getItems().get(0).getId();
		            String [] a = calendarId.split("@");
		            String pcalendarUrl = "https://www.google.com/calendar/embed?src="+a[0].toString()+"%40gmail.com";
		            System.out.println("calendar id is "+calendarId);
		            System.out.println(pcalendarUrl);
		            show(pcalendarUrl);
		          }

				private void show(String pcalendarUrl) {
					Frame googleCalendar = new Frame(pcalendarUrl);
					startPollregistered.setVisible(true);
					googleCalendar.setWidth("500px");
					googleCalendar.setHeight("500px");
					controlsPanel.add(googleCalendar);
					controlsPanel.setSpacing(10);
				}
	        });
	  }
	
	//google cal panel
	
	   private Button getStartPollButton() {
			Button btn = new Button("Start Poll", new SelectionListener<ButtonEvent>(){

				@Override
				public void componentSelected(ButtonEvent ce) {
					StateHelper.setState(State.START_POLL);
				}
				
			});
			
			btn.setAutoWidth(true);
			btn.setAutoHeight(true);
			return btn;
		}
	   
	@Override
	public void initialize() {
		calendar.initialize(new SimpleEventBus(),
		new GoogleApiRequestTransport(APPLICATION_NAME, API_KEY));
		login();
		horizontalPanel = new HorizontalPanel();
		
		startPollregistered = getStartPollButton();
		startPollregistered.setVisible(false);
		horizontalPanel.add(startPollregistered);
		horizontalPanel.setSpacing(5);
		
		horizontalPanel.add(controlsPanel);
		horizontalPanel.add(horizontalPanel);
	}

	@Override
	public com.google.gwt.user.client.ui.Panel getPanel() {
		return horizontalPanel;
	}

}
