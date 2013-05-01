package edu.uncc.ssdi.meetingscheduler.respondtorequest.client;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.uncc.ssdi.meetingscheduler.client.customwidgets.RespondToPollCustomFlexTable;
import edu.uncc.ssdi.meetingscheduler.client.jsonoverlayclasses.MeetingPollInfo;
import edu.uncc.ssdi.meetingscheduler.client.panels.Panel;
import edu.uncc.ssdi.meetingscheduler.client.services.MeetingSchedulingService;
import edu.uncc.ssdi.meetingscheduler.client.services.MeetingSchedulingServiceAsync;
import edu.uncc.ssdi.meetingscheduler.shared.exceptions.ObjectUnInitializedException;

public class RespondPanel implements Panel {
	
	private MeetingSchedulingServiceAsync meetingSchedulerHandle = GWT.create(MeetingSchedulingService.class);
	
	private VerticalPanel mainPanel;
	
	private HorizontalPanel meetingDetailsPanel;
	private HorizontalPanel meetingMetadataPanel;
	private HorizontalPanel pollDateTimesHorizontalPanel;
	private VerticalPanel proposedDateTimesPanel;
	private VerticalPanel cannotAttendAndCommentsPanel;
	
	private RespondToPollCustomFlexTable customFlexTable;
	private TextField<String> nameTextField;
	private TextField<String> emailIdTextField;
	private TextArea commentsTextArea;
	private CheckBox unavailableCheckBox;
	private Button submitButton;
	
	private Integer pollId; 

	public RespondPanel(String timesJsonString, Integer pollId){
		mainPanel = new VerticalPanel();
		meetingDetailsPanel = new HorizontalPanel();
		meetingMetadataPanel = new HorizontalPanel();
		proposedDateTimesPanel = new VerticalPanel();
		pollDateTimesHorizontalPanel = new HorizontalPanel();
		cannotAttendAndCommentsPanel = new VerticalPanel();
		
		nameTextField = new TextField<String>();
		emailIdTextField = new TextField<String>();
		commentsTextArea = new TextArea();
		unavailableCheckBox = new CheckBox();
		submitButton = new Button("Submit");
		
		this.pollId = pollId;
		
		initializeSubmitButton();
		
		MeetingPollInfo mpi = getMeetingPollInfo(timesJsonString);
		
		TextField<String> subjectTextField = new TextField<String>();
		TextField<String> organizerTextField = new TextField<String>();
		TextField<String> locationTextField = new TextField<String>();
		TextField<Integer> durationTextField = new TextField<Integer>();
		TextArea descTextArea = new TextArea();
		
		subjectTextField.setValue(mpi.getSubject());
		subjectTextField.setShim(true);
		subjectTextField.setReadOnly(true);
		subjectTextField.setAutoWidth(true);
		
		organizerTextField.setValue(mpi.getOrganizer());
		organizerTextField.setShim(true);
		organizerTextField.setReadOnly(true);
		organizerTextField.setAutoWidth(true);
		
		locationTextField.setValue(mpi.getLocation());
		locationTextField.setShim(true);
		locationTextField.setReadOnly(true);
		locationTextField.setAutoWidth(true);
		
		durationTextField.setValue(Integer.parseInt(mpi.getDuration()));
		durationTextField.setShim(true);
		durationTextField.setReadOnly(true);
		durationTextField.setAutoWidth(true);
		
		descTextArea.setValue(mpi.getDesc());
		descTextArea.setShim(true);
		descTextArea.setReadOnly(true);
		descTextArea.setWidth("100%");
		
		FlexTable metaDataFlexTable = new FlexTable();
		
		metaDataFlexTable.setWidget(0, 0, new Label("Subject: "));
		metaDataFlexTable.setWidget(0, 1, subjectTextField);
		
		metaDataFlexTable.setWidget(0, 2, new Label("Organizer: "));
		metaDataFlexTable.setWidget(0, 3, organizerTextField);
		
		metaDataFlexTable.setWidget(1, 0, new Label("Location: "));
		metaDataFlexTable.setWidget(1, 1, locationTextField);
		
		metaDataFlexTable.setWidget(1, 2, new Label("Duration: "));
		metaDataFlexTable.setWidget(1, 3, durationTextField);

		metaDataFlexTable.getFlexCellFormatter().setColSpan(2, 1, 4);
		
		metaDataFlexTable.setWidget(2, 0, new Label("Description: "));
		metaDataFlexTable.setWidget(2, 1, descTextArea);
		
		metaDataFlexTable.setCellSpacing(10);
		metaDataFlexTable.setCellPadding(2);
		
		FlexTable userDataFlexTable = new FlexTable();
		userDataFlexTable.setWidth("100%");
		userDataFlexTable.setCellPadding(2);
		userDataFlexTable.setCellSpacing(10);
		
		nameTextField.setAutoWidth(true);
		nameTextField.setAllowBlank(false);
		nameTextField.setMessageTarget("tooltip");
		nameTextField.setShim(true);
		nameTextField.setWidth("220px");
		
		emailIdTextField.setAutoWidth(true);
		emailIdTextField.setShim(true);
		emailIdTextField.setAllowBlank(false);
		emailIdTextField.setMessageTarget("tooltip");
		emailIdTextField.setWidth("220px");
		
		commentsTextArea.setAutoWidth(true);
		commentsTextArea.setShim(true);
		commentsTextArea.setWidth("450px");
		commentsTextArea.setHeight("66px");
		
		//dummy panel for formatting spaces.
		HorizontalPanel dummyPanel = new HorizontalPanel();
		dummyPanel.setWidth("60px");
		
		userDataFlexTable.setWidget(0, 0, new Label("Name: "));
		userDataFlexTable.setWidget(0, 1, nameTextField);
		
		userDataFlexTable.setWidget(1, 0, new Label("Email Id: "));
		userDataFlexTable.setWidget(1, 1, emailIdTextField);
		
		userDataFlexTable.setWidget(2, 0, new Label("Cannot Attend? "));
		userDataFlexTable.setWidget(2, 1, unavailableCheckBox);
		
		userDataFlexTable.setWidget(3, 0, new Label("Comments: "));
		userDataFlexTable.setWidget(3, 1, commentsTextArea);
		
		cannotAttendAndCommentsPanel.add(userDataFlexTable);
		cannotAttendAndCommentsPanel.setBorderWidth(1);
		
		FlexTable meetingMetaDataFlexTable = new FlexTable();
		meetingMetaDataFlexTable.setWidget(0, 0, meetingMetadataPanel);
		meetingMetaDataFlexTable.setWidget(0, 1, dummyPanel);
		meetingMetaDataFlexTable.setWidget(0, 2, cannotAttendAndCommentsPanel);
		
		meetingMetadataPanel.add(metaDataFlexTable);
		meetingMetadataPanel.setBorderWidth(1);	
		
		meetingDetailsPanel.add(meetingMetaDataFlexTable);
		meetingDetailsPanel.setSpacing(15);
		
		customFlexTable = new RespondToPollCustomFlexTable(timesJsonString);
		
		pollDateTimesHorizontalPanel.add(customFlexTable.getTable());
		proposedDateTimesPanel.add(pollDateTimesHorizontalPanel);
		proposedDateTimesPanel.add(submitButton);
		
		mainPanel.add(meetingDetailsPanel);
		mainPanel.add(proposedDateTimesPanel);
		mainPanel.setSpacing(4);
		mainPanel.setBorderWidth(1);

	}
	
	private void initializeSubmitButton() {
		submitButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				String responseMetadataString = getResponseMetadataJSONString();
				String responseTimesString = "";
				if(!unavailableCheckBox.getValue()){
					try {
						responseTimesString = customFlexTable.getResponseJSONString();
					} catch (ObjectUnInitializedException e) {
						MessageBox.info("Error!", e.getMessage(), null);
						return;
					}
				}
				  
				meetingSchedulerHandle.registerPollResponse(pollId, responseMetadataString, responseTimesString, 
						new AsyncCallback<Boolean>() {

							@Override
							public void onFailure(Throwable caught) {
								MessageBox.alert("Error!", "Something went wrong!", null);
							}

							@Override
							public void onSuccess(Boolean result) {
								if(result){
									MessageBox.info("Success!", "Your availability has been saved!", null);
									RootPanel.get("respondPanel").clear();
								}
								else{
									MessageBox.alert("Error!", "Something went wrong!", null);
								}
							}
						});
			}
		});
	}

	@Override
	public void update() {
		
	}

	@Override
	public void initialize() {
		nameTextField.setAllowBlank(false);
		commentsTextArea.setAllowBlank(true);
		
		unavailableCheckBox.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				if(unavailableCheckBox.getValue())
					commentsTextArea.setAllowBlank(false);
			}
		});
	}
	
	private boolean allFieldsValid(){
		//TODO Implement validation rules here.
		
		return true;
		
	}
	
	public String getResponseMetadataJSONString(){
		
		if(!allFieldsValid()){
			
			//TODO: error handling code goes here. 
			
		}
		
		//start building the meta data response JSON string
		StringBuffer sb = new StringBuffer();
		sb.append("{\n");
		
		sb.append("\"name\":"+ "\"" + nameTextField.getValue() + "\",\n");
		sb.append("\"email\":" + "\"" + emailIdTextField.getValue() + "\",\n");
		sb.append("\"isUnavailable\":" + "\"" + unavailableCheckBox.getValue() + "\",\n");
		sb.append("\"comments\":" + "\"" + commentsTextArea.getValue() + "\"\n");
		
		return sb.append("}\n").toString();
	}

	@Override
	public com.google.gwt.user.client.ui.Panel getPanel() {
		return mainPanel;
	}
	
	private final native MeetingPollInfo getMeetingPollInfo(String json)/*-{
		return eval("(" + json + ")");
	}-*/;
}
