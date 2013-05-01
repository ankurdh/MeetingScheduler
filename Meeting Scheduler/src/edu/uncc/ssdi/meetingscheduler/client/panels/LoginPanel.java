package edu.uncc.ssdi.meetingscheduler.client.panels;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.api.gwt.oauth2.client.Auth;
import com.google.api.gwt.oauth2.client.AuthRequest;
import com.google.api.gwt.oauth2.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.uncc.ssdi.meetingscheduler.client.JSONDataRequestor;
import edu.uncc.ssdi.meetingscheduler.client.QueryGenerator;
import edu.uncc.ssdi.meetingscheduler.client.communication.ServerHandle;
import edu.uncc.ssdi.meetingscheduler.client.services.LoginService;
import edu.uncc.ssdi.meetingscheduler.client.services.LoginServiceAsync;
import edu.uncc.ssdi.meetingscheduler.client.state.State;
import edu.uncc.ssdi.meetingscheduler.client.state.StateHelper;
import edu.uncc.ssdi.meetingscheduler.client.state.UserLoginState;

public class LoginPanel implements Panel, QueryGenerator, JSONDataRequestor {
	
	//create a handle to the LoginServiceAsync RMI class.
	private final LoginServiceAsync loginService = GWT.create(LoginService.class);
	
	private Grid g;
	private Button noLoginButton;
	private Button registerWithGoogleButton;
	
	private String query = null;
	private com.google.gwt.user.client.ui.VerticalPanel panel = null;
	
	public LoginPanel(){
		panel = new VerticalPanel();
		initialize();
	}

	@Override
	public void update() {

	}
	
	private Button getNonLoginButton(){
		Button button = new Button("Continue without login", new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				StateHelper.setState(State.START_POLL);
				UserLoginState.setUserLoginState(true);
			}
			
		});
		
		button.setAutoWidth(true);
		button.setAutoHeight(true);
		
		return button;
	}

	@Override
	public void initialize() {
		
		g = new Grid(3, 3);
		g.setCellSpacing(10);
		
		noLoginButton = getNonLoginButton();
		registerWithGoogleButton = getRegisterWithGoogleButton();		
		
		g.setWidget(0,0, registerWithGoogleButton);
		g.getCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);
		g.setWidget(0,1, noLoginButton);
		g.getCellFormatter().setHorizontalAlignment(2, 1, HasHorizontalAlignment.ALIGN_CENTER);
		
		panel.add(g);
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panel.setBorderWidth(3);
		panel.setStyleName("loginVerticalPanel");
				
	}

	private Button getRegisterWithGoogleButton() {
		Button btn = new Button("Login with Google Account", new SelectionListener<ButtonEvent>(){

			@SuppressWarnings("deprecation")
			@Override
			public void componentSelected(ButtonEvent ce) {
				StateHelper.setState(State.LOGGED_IN);
			}
			
		});
		
		btn.setShim(true);
		btn.setAutoWidth(true);
		btn.setAutoHeight(true);
		return btn;
	}

	@Override
	public String getQuery() {
		return query;
	}

	@Override
	public void requestData(ServerHandle s) {
		// TODO Auto-generated method stub
	}

	@Override
	public void registerWithServerHandle(ServerHandle s) {
		// TODO Auto-generated method stub
		s.registerDataRequestor(this);
	}

	@Override
	public void onDataRecieved(String jsonData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public com.google.gwt.user.client.ui.Panel getPanel() {
		return panel;
	}
}
