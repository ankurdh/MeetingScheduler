package edu.uncc.ssdi.meetingscheduler.client.panels;

import com.extjs.gxt.ui.client.widget.Label;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.uncc.ssdi.meetingscheduler.client.jsonoverlayclasses.PollTrackingInfo;
import edu.uncc.ssdi.meetingscheduler.client.services.MeetingSchedulingService;
import edu.uncc.ssdi.meetingscheduler.client.services.MeetingSchedulingServiceAsync;

public class TrackResponsesPanel implements Panel {
	
	MeetingSchedulingServiceAsync meetingSchedulingService = GWT.create(MeetingSchedulingService.class);
	JsArray<PollTrackingInfo> pollTrackingInfo;

	private VerticalPanel corePanel;
	private VerticalPanel overallResultsPanel;
	private String responseJSONString;
	private Integer pollId;

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
		
		corePanel.add(overallResultsPanel);
		
		populateOverallResultsPanel();
//		populateComingListPanel();
//		populateNonComingList();
		
	}
	
	private void populateOverallResultsPanel(){
		//Ask the server to get the best available datetimes for the meeting.
		meetingSchedulingService.getBestScheduleTimes(pollId, new AsyncCallback<String>(){

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(String result) {
				FlexTable ft = new FlexTable();
				
				pollTrackingInfo = getPollTrackingInfo(result);
				int row = ft.getRowCount();
				
				//set the header of the flextable. 
				ft.setWidget(row, 0, new Label("Date-Time"));
				ft.getCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
				ft.setWidget(row, 1, new Label("Participants Available"));
				ft.getColumnFormatter().setWidth(1, "100px");
				ft.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_CENTER);
				
				for(int i = 0 ; i < pollTrackingInfo.length() ; i ++){
					PollTrackingInfo pti = pollTrackingInfo.get(i);
					
					ft.setWidget(++row, 0, new Label(pti.getDate()));
					ft.getCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
					ft.setWidget(row, 1, new Label(pti.getCount()));
					ft.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_CENTER);
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
	
	private final native JsArray<PollTrackingInfo> getPollTrackingInfo(String json)/*-{
		return eval("(" + json + ")");
	}-*/;

}
