package edu.uncc.ssdi.meetingscheduler.client.panels;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.uncc.ssdi.meetingscheduler.client.customwidgets.DateTimeWidget;
import edu.uncc.ssdi.meetingscheduler.client.services.CreatePollService;
import edu.uncc.ssdi.meetingscheduler.client.services.CreatePollServiceAsync;
import edu.uncc.ssdi.meetingscheduler.client.state.UserLoginState;
import edu.uncc.ssdi.meetingscheduler.shared.exceptions.ObjectUnInitializedException;

public class CreatePollPanel implements Panel {
	
	//get a handle on the Create Poll service.
	CreatePollServiceAsync createPollService = GWT.create(CreatePollService.class);
	
	private VerticalPanel verticalPanel;
	private HorizontalPanel hp1;
	private HorizontalPanel hp2;
	private DateTimeWidget dateTimeWidget;
	
	private TextField<String> subjectTextField;
	private TextField<String> organizerTextField;
	private TextField<String> locationTextField;
	private TextField<Integer> durationTextField;
	private TextArea descTextArea;

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
					dateTimeJSONString = dateTimeWidget.getDateTimePicker().getTimesAndDatesAsJSON();
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
									MessageBox.info("Success!", "Poll created successfully with ID: " + result + 
										"\n\nKindly email this number to participants to respond to poll.", null);
							}
						}
						
					});

				} else {
					//TODO: user is logged in. Invoke the logged in user method here. 
				}
								
			}		
		});
				
		return btn;
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
		
		dateTimeWidget = new DateTimeWidget();
		
		hp1 = getMeetingMetadataPanel();
		hp2 = new HorizontalPanel();
		hp2.add(dateTimeWidget.getDateTimeWidget());
		
		Button createPollButton = getCreatePollButton();
				
		verticalPanel.add(hp1);
		verticalPanel.add(hp2);
		verticalPanel.add(createPollButton);

	}

	@Override
	public com.google.gwt.user.client.ui.Panel getPanel() {
		return verticalPanel;
	}
}
