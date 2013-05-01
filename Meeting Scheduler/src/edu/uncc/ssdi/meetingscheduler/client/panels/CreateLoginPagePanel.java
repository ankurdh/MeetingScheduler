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

	private VerticalPanel verticalPanel;
	private HorizontalPanel hp1;
	private HorizontalPanel hp2;
	private Button startPollregistered = new Button();
	private HorizontalPanel horizontalPanel;
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
		// TODO Auto-generated method stub

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
		          // String calendarUrl = "http://www.google.com/calendar/feeds/"+calendarId+"/private/full";
		            String calendarUrl = "https://www.google.com/calendar/feeds/saurabh.mahamine@gmail.com/public/full";
		            String pcalendarUrl = "https://www.google.com/calendar/embed?src="+a[0].toString()+"%40gmail.com";
		            System.out.println("calendar id is "+calendarId);
		            System.out.println(pcalendarUrl);
		            show(pcalendarUrl);
		           // insertEvent(calendarId);
		          }

				private void show(String pcalendarUrl) {
					// TODO Auto-generated method stub
					Frame googleCalendar = new Frame(pcalendarUrl);
					startPollregistered.setVisible(true);
					googleCalendar.setWidth("500px");
					googleCalendar.setHeight("500px");
					controlsPanel.add(googleCalendar);
					//controlsPanel.add(createNewEventPanel());
					//controlsPanel.add(createEventEditPanel());
					controlsPanel.setSpacing(10);
					 
					//RootPanel.get().add(controlsPanel);
				}
		        });
	  }
	   
	   //
	   
//	   HorizontalPanel getPanel()
//	   {
//		   initialize();
//		   return controlsPanel;
//	   }
	   
	
	//google cal panel
	
	   private Button gettSartPollregisteredButton() {
			Button btn = new Button("Start Poll", new SelectionListener<ButtonEvent>(){

				@Override
				public void componentSelected(ButtonEvent ce) {
					// TODO Auto-generated method stub -- 
					StateHelper.setState(State.START_POLL);
					//UserLoginState.setUserLoginState(true);
				}
				
			});
			
			btn.setAutoWidth(true);
			btn.setAutoHeight(true);
			return btn;
		}

	   
	   private Button gettTrackPollButton() {
			Button btn = new Button("Track Poll", new SelectionListener<ButtonEvent>(){

				@Override
				public void componentSelected(ButtonEvent ce) {
					// TODO Auto-generated method stub -- 
					//StateHelper.setState(State.START_POLL);
					//UserLoginState.setUserLoginState(true);
				}
				
			});
			
			btn.setAutoWidth(true);
			btn.setAutoHeight(true);
			return btn;
		}
	   
	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		calendar.initialize(new SimpleEventBus(),
		new GoogleApiRequestTransport(APPLICATION_NAME, API_KEY));
		login();
		hp1 = new HorizontalPanel();
		hp2 = new HorizontalPanel();
		//Button startPollregistered = gettSartPollregisteredButton();
		startPollregistered = gettSartPollregisteredButton();
		startPollregistered.setVisible(false);
		hp2.add(startPollregistered);
		hp2.setSpacing(5);
		//hp2.add(b);
		//hp1 = controlsPanel;
		//verticalPanel.add(controlsPanel);
		//RootPanel.get("panel").add(controlsPanel);
		
		horizontalPanel.add(controlsPanel);
		horizontalPanel.add(hp2);
	}

	@Override
	public com.google.gwt.user.client.ui.Panel getPanel() {
		// TODO Auto-generated method stub
		return horizontalPanel;
	}

}
