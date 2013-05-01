package edu.uncc.ssdi.meetingscheduler.client.panels;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.uncc.ssdi.meetingscheduler.client.jsonoverlayclasses.PollTrackingInfo;
import edu.uncc.ssdi.meetingscheduler.client.services.MeetingSchedulingService;
import edu.uncc.ssdi.meetingscheduler.client.services.MeetingSchedulingServiceAsync;

public class TrackResponsesPanel implements Panel {
	
	private MeetingSchedulingServiceAsync meetingSchedulingService = GWT.create(MeetingSchedulingService.class);
	private JsArray<PollTrackingInfo> pollTrackingInfo;

	private VerticalPanel corePanel;
	private HorizontalPanel buttonsPanel;
	private VerticalPanel overallResultsPanel;
	private String responseJSONString;
	private Integer pollId;
	private FlexTable ft;
	private RadioGroup rg;

	public TrackResponsesPanel(String result, Integer pollId){
		
		responseJSONString = result;
		this.pollId = pollId;
		
		initialize();
	}
	
	@Override
	public void update() {
		
	}

	@Override
	public void initialize() {
		corePanel = new VerticalPanel();
		overallResultsPanel = new VerticalPanel();
		buttonsPanel = new HorizontalPanel();
		rg = new RadioGroup("date-times");
		
		corePanel.add(overallResultsPanel);
		
		buttonsPanel.add(getScheduleMeetingButton());
		buttonsPanel.add(getAvailableParticipantsButton());
		buttonsPanel.add(getRemindUnparticipatedParticipants());
		
		corePanel.add(buttonsPanel);
		
		populateOverallResultsPanel();
		
	}
	
	private Widget getRemindUnparticipatedParticipants() {
		
		Button reminderButton = new Button("Remind Unresponsive Participants", new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				
				meetingSchedulingService.remindParticipants(pollId, new AsyncCallback<Boolean>(){

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(Boolean result) {
					}
				});
			}
		});
		Info.display("Success", "Server Requested to send emamil reminders");

		return reminderButton;
		
	}

	private Button getAvailableParticipantsButton() {
		Button availableParticipantsButton = new Button("See who's available", new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				String dateTime = null;
				
				try {
					dateTime = rg.getValue().getId();
					
					showAvailableParticipants(dateTime);
					
				} catch (NullPointerException npe){
					MessageBox.alert("Error", "Please select atleast one date-time", null);
				}
			}
		});
		
		return availableParticipantsButton;
	}

	private Button getScheduleMeetingButton() {
		Button scheduleMeetingButton = new Button("Schedule Meeting", new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				final Radio radio = rg.getValue();
				
				if(!radio.getValue()){
					MessageBox.alert("Error", "Please select a date-time", null);
				}
				
				meetingSchedulingService.scheduleMeeting(pollId, radio.getId(), new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageBox.alert("Error", "Failed to schedule meeting!", null);
					}

					@Override
					public void onSuccess(Boolean result) {
						if(result){
							MessageBox.info("Success", "Meeting scheduled successfully", null);
						}
					}
				});
			}
		});
		
		return scheduleMeetingButton;
	}

	private void populateOverallResultsPanel(){
		//Ask the server to get the best available datetimes for the meeting.
		meetingSchedulingService.getBestScheduleTimes(pollId, new AsyncCallback<String>(){

			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert("Error", "Server threw an error: " + caught.getMessage(), null);
			}

			@Override
			public void onSuccess(String result) {
				
				if(result == null){
					MessageBox.info("Results", "No responses yet!", null);
					return;
				}
				
				ft = new FlexTable();
				
				pollTrackingInfo = getPollTrackingInfo(result);
				int row = ft.getRowCount();
				
				//set the header of the flextable. 
				ft.setWidget(row, 0, new Label("Date-Time"));
				ft.getCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
				ft.setWidget(row, 1, new Label("Participants Available"));
				ft.getColumnFormatter().setWidth(1, "100px");
				ft.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_CENTER);
				ft.setWidget(row, 2, new Label("When to schedule?"));
				ft.getCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_CENTER);
				
				for(int i = 0 ; i < pollTrackingInfo.length() ; i ++){
					PollTrackingInfo pti = pollTrackingInfo.get(i);
					
					Radio rb = new Radio();
					rb.setId(pti.getDate());
					
					rg.add(rb);
					
					ft.setWidget(++row, 0, new Label(pti.getDate()));
					ft.getCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
					ft.setWidget(row, 1, new Label(pti.getCount()));
					ft.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_CENTER);
					ft.setWidget(row, 2, rb);
					ft.getCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_CENTER);
				}
				
				ft.getRowFormatter().addStyleName(0, "DateTimeWidgetHeader");
				
				overallResultsPanel.add(ft);
			}
		});
	}

	@Override
	public com.google.gwt.user.client.ui.Panel getPanel() {
		return corePanel;
	}
	
	private void showAvailableParticipants(final String dateTime){
		meetingSchedulingService.getAvailableParticipantsFor(pollId, dateTime, new AsyncCallback<String>(){
			
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert("Error", "Server threw an error: " + caught.getMessage(), null);
			}

			@Override
			public void onSuccess(String result) {
				
				if(result == null){
					MessageBox.info("Info", "No one available yet!", null);
					return;
				}
				
				Dialog dialog = new Dialog();
				FlexTable nameCommentFt = new FlexTable();
				nameCommentFt.getRowFormatter().addStyleName(0, "DateTimeWidgetHeader");
				
				int row = nameCommentFt.getRowCount();
				Label nameLabel = new Label("Name");
				Label commentLabel = new Label("Comment");
				
				nameLabel.setStyleAttribute("font-style", "italic");
				nameLabel.setStyleAttribute("font-weight", "900");
				nameLabel.setStyleAttribute("text-decoration", "underline");
				commentLabel.setStyleAttribute("font-style", "italic");
				commentLabel.setStyleAttribute("font-weight", "900");
				commentLabel.setStyleAttribute("text-decoration", "underline");
				
				nameCommentFt.setWidget(row, 0, nameLabel);
				nameCommentFt.getCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
				nameCommentFt.setWidget(row, 1, commentLabel);
				nameCommentFt.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_CENTER);
				
				String [] nameCommentArray = result.split(",");
				for(String nameComment : nameCommentArray){
					nameCommentFt.setWidget(++row, 0, new Label(nameComment.split(":")[0]));
					nameCommentFt.getCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
					nameCommentFt.setWidget(row, 1, new Label(nameComment.split(":")[1]));
					nameCommentFt.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_CENTER);
				}
				
				nameCommentFt.setSize("100%", "100%");
				nameCommentFt.setBorderWidth(1);
				dialog.add(nameCommentFt);
				dialog.setHeading("Participants Available on " + dateTime);
				dialog.setTitle("Participants Available");
				dialog.setAnimCollapse(true);
				dialog.setShim(true);
				dialog.show();
				dialog.setHideOnButtonClick(true);
				dialog.center();
				
			}
		});

	}
	
	private final native JsArray<PollTrackingInfo> getPollTrackingInfo(String json)/*-{
		return eval("(" + json + ")");
	}-*/;
}