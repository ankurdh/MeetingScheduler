package edu.uncc.ssdi.meetingscheduler.respondtorequest.client;

import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.uncc.ssdi.meetingscheduler.client.customwidgets.CustomFlexTable;
import edu.uncc.ssdi.meetingscheduler.client.panels.Panel;
import edu.uncc.ssdi.meetingscheduler.respondtorequest.client.jsonoverlayclasses.MeetingPollInfo;

public class RespondPanel implements Panel {
	
	private VerticalPanel mainPanel;
	
	private HorizontalPanel meetingDetailsPanel;
	private HorizontalPanel pollDateTimesHorizontalPanel;
	private VerticalPanel proposedDateTimesPanel;
	private VerticalPanel cannotAttendAndCommentsPanel;
	
	private CustomFlexTable customFlexTable;
	private TextField<String> nameTextField;
	private TextArea commentsTextArea;
	private CheckBox unavailableCheckBox;
	private Button submitButton;

	public RespondPanel(String timesJsonString){
		mainPanel = new VerticalPanel();
		meetingDetailsPanel = new HorizontalPanel();
		proposedDateTimesPanel = new VerticalPanel();
		pollDateTimesHorizontalPanel = new HorizontalPanel();
		cannotAttendAndCommentsPanel = new VerticalPanel();
		
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
		
		FlexTable fTable = new FlexTable();
		
		fTable.setWidget(0, 0, new Label("Subject: "));
		fTable.setWidget(0, 1, subjectTextField);
		
		fTable.setWidget(0, 2, new Label("Organizer: "));
		fTable.setWidget(0, 3, organizerTextField);
		
		fTable.setWidget(1, 0, new Label("Location: "));
		fTable.setWidget(1, 1, locationTextField);
		
		fTable.setWidget(1, 2, new Label("Duration: "));
		fTable.setWidget(1, 3, durationTextField);

		fTable.getFlexCellFormatter().setColSpan(2, 1, 4);
		
		fTable.setWidget(2, 0, new Label("Description: "));
		fTable.setWidget(2, 1, descTextArea);
		
		fTable.setCellSpacing(10);
		fTable.setCellPadding(2);
		
		meetingDetailsPanel.add(fTable);
		
		customFlexTable = new CustomFlexTable(false, timesJsonString);
		
		nameTextField = new TextField<String>();
		commentsTextArea = new TextArea();
		unavailableCheckBox = new CheckBox();
		submitButton = new Button("Submit");
		
		proposedDateTimesPanel.add(pollDateTimesHorizontalPanel);
		proposedDateTimesPanel.add(cannotAttendAndCommentsPanel);
		proposedDateTimesPanel.add(submitButton);
		
		mainPanel.add(meetingDetailsPanel);
		mainPanel.add(proposedDateTimesPanel);
		mainPanel.setBorderWidth(1);

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
				commentsTextArea.setAllowBlank(false);
			}
		});
	}

	@Override
	public com.google.gwt.user.client.ui.Panel getPanel() {
		return mainPanel;
	}
	
	private final native MeetingPollInfo getMeetingPollInfo(String json)/*-{
		return eval("(" + json + ")");
	}-*/;
}
