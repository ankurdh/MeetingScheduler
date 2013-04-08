package edu.uncc.ssdi.meetingscheduler.client.panels;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
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
	
	private FlexTable table;
	private Grid g;
	private TextField<String> usernameTextField;
	private TextField<String> passwordTextField;
	private Button submitButton;
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
	
	private Button getSubmitButton(){
		
		Button submitButton = new Button("Login", new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				loginService.verifyRegisteredUser("a", "a", new AsyncCallback<Boolean>() {
					
					@Override
					public void onSuccess(Boolean result) {
						
						final DialogBox d = new DialogBox();
						final VerticalPanel v = new VerticalPanel();
						Button b = new Button("Close", new SelectionListener<ButtonEvent>(){

							@Override
							public void componentSelected(ButtonEvent ce) {
								d.hide();
							}
							
						});
						
						if (result){ 
							
							v.add(new Label("User Registered!"));
							UserLoginState.setUserLoginState(true);
							StateHelper.setState(State.LOGGED_IN);
							
						}
						else v.add(new Label("User Not Registered"));
						
						v.add(b);
						
						d.setWidget(v);
						d.setText("Registration Notification: ");
						d.setAnimationEnabled(true);
						d.center();
						d.show();

					}
					
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						
					}
				});
			}
			
		});		
		
		submitButton.setAutoWidth(true);
		submitButton.setAutoHeight(true);
		return submitButton;
		
	}
	
	private Button getNonLoginButton(){
		Button button = new Button("Continue without login", new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				StateHelper.setState(State.START_POLL);
				UserLoginState.setUserLoginState(false);
			}
			
		});
		
		button.setAutoWidth(true);
		button.setAutoHeight(true);
		
		return button;
	}

	@Override
	public void initialize() {
		
		table = new FlexTable();
		
		g = new Grid(3, 3);
		g.setCellSpacing(10);
		
		usernameTextField = new TextField<String>();
		usernameTextField.setAutoWidth(true);
		usernameTextField.setAllowBlank(false);

		passwordTextField = new TextField<String>();
		passwordTextField.setPassword(true);
		passwordTextField.setAllowBlank(false);
		
		submitButton = getSubmitButton();
		noLoginButton = getNonLoginButton();
		registerWithGoogleButton = getRegisterWithGoogleButton();		
		
		table.setCellPadding(10);
		
		Label userName = new Label("UserName:");
		Label password = new Label("Password:");
		
		g.setWidget(0, 0, userName);
		g.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
		g.setWidget(0,1,usernameTextField);
		g.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
		g.setWidget(1,0, password);
		g.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER);
		g.setWidget(1,1,passwordTextField);
		g.getCellFormatter().setHorizontalAlignment(1, 1, HasHorizontalAlignment.ALIGN_CENTER);
		g.setWidget(2,0, registerWithGoogleButton);
		g.getCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);
		g.setWidget(2,1, noLoginButton);
		g.getCellFormatter().setHorizontalAlignment(2, 1, HasHorizontalAlignment.ALIGN_CENTER);
		g.setWidget(1, 2, submitButton);
		g.getCellFormatter().setHorizontalAlignment(1, 2, HasHorizontalAlignment.ALIGN_CENTER);
		
		panel.add(g);
		panel.setBorderWidth(3);
		panel.setSpacing(5);
		panel.setStyleName("loginVerticalPanel");
				
	}

	private Button getRegisterWithGoogleButton() {
		Button btn = new Button("Login with Google Account", new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
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
