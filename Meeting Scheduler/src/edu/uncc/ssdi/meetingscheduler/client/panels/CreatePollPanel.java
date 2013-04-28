package edu.uncc.ssdi.meetingscheduler.client.panels;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.uncc.ssdi.meetingscheduler.client.customwidgets.CustomFlexTableHelper;
import edu.uncc.ssdi.meetingscheduler.client.customwidgets.DateTimeWidget;
import edu.uncc.ssdi.meetingscheduler.client.services.CreatePollService;
import edu.uncc.ssdi.meetingscheduler.client.services.CreatePollServiceAsync;
import edu.uncc.ssdi.meetingscheduler.client.state.UserLoginState;
import edu.uncc.ssdi.meetingscheduler.shared.exceptions.ObjectUnInitializedException;

public class CreatePollPanel implements Panel {
	
	//get a handle on the Create Poll service.
	CreatePollServiceAsync createPollService = GWT.create(CreatePollService.class);
	
	private HorizontalPanel metadataAndEmailIdPanel;
	private VerticalPanel emailIdPanel;
	private ScrollPanel emailIdScrollPanel;
	private FlexTable emailIdFlexTable;
	
	private VerticalPanel verticalPanel;
	private HorizontalPanel hp1;
	private HorizontalPanel hp2;
	private DateTimeWidget dateTimeWidget;
	
	private TextField<String> subjectTextField;
	private TextField<String> organizerTextField;
	private TextField<String> locationTextField;
	private TextField<Integer> durationTextField;
	private TextArea descTextArea;
	
	private Integer pollId;
	private Integer trackingPollId;

	public CreatePollPanel(){
		verticalPanel = new VerticalPanel();
		initialize();
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
	}
	
	/**
	 * @author Ankur
	 * @return the button responsible to create the poll. The method creates the button
	 * and adds all the necessary handles for the events, calls to the server etc.
	 */
	private Button getCreatePollButton(){
		Button btn = new Button("Create Poll", new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				String dateTimeJSONString = null;
				try {
					dateTimeJSONString = CustomFlexTableHelper.getTimesAndDatesAsJSON(dateTimeWidget.getCustomFlexTable());
				} catch (ObjectUnInitializedException e) {
					MessageBox.info("Error!", e.getMessage(), null);
					return;
				}
				String pollDetailsJSONString = null;
				try {
					pollDetailsJSONString = getPollDetailsJSONString();
				} catch (ObjectUnInitializedException e) {
					MessageBox.info("Error!", e.getMessage(), null);
					return;
				}

				if(!UserLoginState.userLoggedIn){
					createPollService.setPollDetails(pollDetailsJSONString, dateTimeJSONString, new AsyncCallback<Integer>(){

						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onSuccess(Integer result) {
							if(result != -1){ 
									pollId = result;
									getPollTrackingIdAndDisplayInfo();
							}
						}
						
					});

				} else {
					//TODO: user is logged in. Invoke the logged in user method here.
					String participantNameEmailIdJSON = null;
					try {
						participantNameEmailIdJSON = getUserNameEmailIdJSONString();
					} catch(ObjectUnInitializedException e){
						MessageBox.info("Error!", e.getMessage(), null);
						return;
					}
					
					createPollService.setPollDetails(pollDetailsJSONString, dateTimeJSONString, 
							participantNameEmailIdJSON, UserLoginState.getLoggedInUserId(), new AsyncCallback<Integer>() {

								@Override
								public void onFailure(Throwable caught) {
									// TODO Auto-generated method stub
									
								}

								@Override
								public void onSuccess(Integer result) {
									if(result != -1){ 
										pollId = result;
										getPollTrackingIdAndDisplayInfo();
									}
									
									//TODO: pass the participantNameEmailIdJSON object to the EmailHelper here. 
									//or, should it be implemented at the server?
									
								}
					});
					
				}
			}		
		});
				
		return btn;
	}
	
	/**
	 * This method is called only if a user is logged in and he has entered the poll participant names
	 * and their email ids. 
	 * @author Ankur
	 */
	private String getUserNameEmailIdJSONString() throws ObjectUnInitializedException {
		int rows = emailIdFlexTable.getRowCount();
		
		if(rows == 0)
			throw new ObjectUnInitializedException("Please enter atleast one participant");
		
		StringBuffer jsonString = new StringBuffer();
		
		jsonString.append("[\n");
		
		for(int i = 1 ; i < rows ; i ++){
			TextField<String> name = (TextField<String>)emailIdFlexTable.getWidget(i, 0);
			TextField<String> emailId = (TextField<String>)emailIdFlexTable.getWidget(i, 1);
			
			if(name.getValue().length() == 0)
				continue;
			if(emailId.getValue().length() == 0)
				continue;
			
			jsonString.append("{\n").append("\"name\":").append("\"" + name.getValue() + "\",\n")
					  .append("\"email\":").append("\"" + emailId.getValue() + "\"\n},\n");
		}
		
		jsonString.setCharAt(jsonString.lastIndexOf(","), ']');
		return jsonString.append("\n").toString();
	}
	
	private void getPollTrackingIdAndDisplayInfo() {
		
		//get the tracking id from ther server and show it to the poll starter!
		createPollService.getPollTrackingId(pollId, new AsyncCallback<Integer>(){

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(Integer result) {
				if(result != -1) {
					trackingPollId = result;
				
					MessageBox.info("Poll Handles", "Poll created successfully. Poll Id: " + pollId +
							".\nEmail this id & the below URL to participants for providing response.\nTracking Number: " + trackingPollId 
							+ ", use this number to track responses and schedule meeting. " + 
							"URL : http://127.0.0.1:8888/RespondToRequest.html?gwt.codesvr=127.0.0.1:9997", null);
					
				}
				else
					MessageBox.info("Error!", "Something failed in the server!", null);
			}
			
		});	
	}
	
	/**
	 * @return Returns the poll metadata like the subject, location, organizer, description etc, in 
	 * JSON format after validation.
	 * @throws ObjectUnInitializedException
	 */
	
	private String getPollDetailsJSONString() throws ObjectUnInitializedException {
		
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		
		if(subjectTextField.getValue() == null || subjectTextField.getValue().equals(""))
			throw new ObjectUnInitializedException("Subject field cannot be empty!");
		if(locationTextField.getValue() == null || locationTextField.getValue().equals("") )
			throw new ObjectUnInitializedException("Location field cannot be empty!");
		if(organizerTextField.getValue() == null || organizerTextField.getValue().equals(""))
			throw new ObjectUnInitializedException("Organizer field cannot be empty!");
		if(descTextArea.getValue() == null || descTextArea.getValue().equals(""))
			throw new ObjectUnInitializedException("Description area cannot be empty!");
		if(durationTextField.getValue() == null)
			throw new ObjectUnInitializedException("Duration field cannot be empty!");
		
		try{
			Integer.parseInt(String.valueOf(durationTextField.getValue()));
		} catch (NumberFormatException nfe){
			throw new ObjectUnInitializedException("Duration field has to be a number.");
		}
				
		sb.append("\"subject\":\"" + subjectTextField.getValue() + "\",\n");
		sb.append("\"location\":\"" + locationTextField.getValue() + "\",\n");
		sb.append("\"duration\":\"" + durationTextField.getValue() + "\",\n");
		sb.append("\"organizer\":\"" + organizerTextField.getValue() + "\",\n");
		sb.append("\"desc\":\"" + descTextArea.getValue() + "\"\n");
		
		sb.append("}");
						
		return sb.toString();
	}
	
	private HorizontalPanel getMeetingMetadataPanel(){
		HorizontalPanel hPanel = new HorizontalPanel();
		
		FlexTable fTable = new FlexTable();
		
		Label subjectLabel = new Label("Subject:");
		Label organizerLabel = new Label("Organizer:");
		Label location = new Label("Location:");
		Label duration = new Label("Duration(hrs):");
		Label desc = new Label("Description:");
		
		subjectTextField = new TextField<String>();
		subjectTextField.setAutoWidth(true);
		subjectTextField.setAllowBlank(false);
		subjectTextField.setBorders(true);
		
		organizerTextField = new TextField<String>();
		organizerTextField.setAutoWidth(true);
		organizerTextField.setAllowBlank(false);
		organizerTextField.setBorders(true);
		
		locationTextField = new TextField<String>();
		locationTextField.setAutoWidth(true);
		locationTextField.setAllowBlank(false);
		locationTextField.setBorders(true);
		
		durationTextField = new TextField<Integer>();
		durationTextField.setAutoWidth(true);
		durationTextField.setAllowBlank(false);
		durationTextField.setBorders(true);
		
		descTextArea = new TextArea();
		descTextArea.setWidth("100%");
		descTextArea.setAllowBlank(false);
		descTextArea.setBorders(true);
		
		fTable.setWidget(0, 0, subjectLabel);
		fTable.setWidget(0, 1, subjectTextField);
		
		fTable.setWidget(0, 2, organizerLabel);
		fTable.setWidget(0, 3, organizerTextField);
		
		fTable.setWidget(1, 0, location);
		fTable.setWidget(1, 1, locationTextField);
		
		fTable.setWidget(1, 2, duration);
		fTable.setWidget(1, 3, durationTextField);

		fTable.getFlexCellFormatter().setColSpan(2, 1, 4);
		
		fTable.setWidget(2, 0, desc);
		fTable.setWidget(2, 1, descTextArea);
		
		fTable.setCellSpacing(10);
		fTable.setCellPadding(2);
		
		hPanel.add(fTable);
		hPanel.setBorderWidth(2);
		hPanel.setStyleName("loginVerticalPanel");
		return hPanel;
	}

	@Override
	public void initialize() {
		
		pollId = -1;
		trackingPollId = -1;
		
		dateTimeWidget = new DateTimeWidget();
		metadataAndEmailIdPanel = new HorizontalPanel();
		
		hp1 = getMeetingMetadataPanel();
		hp2 = new HorizontalPanel();
		hp2.add(dateTimeWidget.getDateTimeWidget());
		
		metadataAndEmailIdPanel.add(hp1);
		
		if(UserLoginState.userLoggedIn){
			populateEmailPanel();
			metadataAndEmailIdPanel.add(emailIdPanel);
			emailIdPanel.add(emailIdScrollPanel);
			emailIdScrollPanel.add(emailIdFlexTable);
			metadataAndEmailIdPanel.setSpacing(10);
			
		}
		
		Button createPollButton = getCreatePollButton();
				
		verticalPanel.add(metadataAndEmailIdPanel);
		verticalPanel.add(hp2);
		verticalPanel.add(createPollButton);

	}

	private void populateEmailPanel() {
		
		emailIdPanel = new VerticalPanel();
		emailIdPanel.setBorderWidth(1);		
		
		emailIdScrollPanel = new ScrollPanel();
		emailIdScrollPanel.setAlwaysShowScrollBars(true);
		emailIdScrollPanel.setHeight("154px");
		emailIdScrollPanel.setWidth("420px");
		
		emailIdFlexTable = new FlexTable();
		emailIdFlexTable.setWidget(0, 0, new Label("Name"));
		emailIdFlexTable.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
		emailIdFlexTable.setWidget(0, 1, new Label("Email ID"));
		emailIdFlexTable.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
		emailIdFlexTable.getElement().getStyle().setProperty("margin", "0 auto");
		emailIdFlexTable.setSize("100%", "100%");
		
		addRowToEmailIdFlexTable();
		
}
	
	private void addRowToEmailIdFlexTable(){
		
		int rows = emailIdFlexTable.getRowCount();
		
		TextField<String> nameTextField = new TextField<String>();
		TextField<String> emailIdTextField = new TextField<String>();
		
		nameTextField.focus();
		
		nameTextField.setShim(true);
		emailIdTextField.setShim(true);
		
		emailIdTextField.addKeyListener(new KeyListener(){
			@Override
			public void componentKeyPress(ComponentEvent event) {
				super.componentKeyPress(event);
				
				if(event.getKeyCode() == KeyCodes.KEY_ENTER)
					addRowToEmailIdFlexTable();
			}
		});
		
		emailIdFlexTable.setWidget(rows, 0, nameTextField);
		emailIdFlexTable.getCellFormatter().setHorizontalAlignment(rows, 0, HasHorizontalAlignment.ALIGN_CENTER);
		emailIdFlexTable.setWidget(rows, 1, emailIdTextField);
		emailIdFlexTable.getCellFormatter().setHorizontalAlignment(rows, 1, HasHorizontalAlignment.ALIGN_CENTER);
		
	}

	@Override
	public com.google.gwt.user.client.ui.Panel getPanel() {
		return verticalPanel;
	}
}
