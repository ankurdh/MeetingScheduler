package edu.uncc.ssdi.meetingscheduler.respondtorequest.client;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

import edu.uncc.ssdi.meetingscheduler.client.panels.TrackResponsesPanel;
import edu.uncc.ssdi.meetingscheduler.client.services.CreatePollService;
import edu.uncc.ssdi.meetingscheduler.client.services.CreatePollServiceAsync;
import edu.uncc.ssdi.meetingscheduler.client.services.MeetingSchedulingService;
import edu.uncc.ssdi.meetingscheduler.client.services.MeetingSchedulingServiceAsync;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * @author Ankur
 */
public class RespondToRequest implements EntryPoint {
	private Integer pollId; 
	
	public void onModuleLoad() {
		
		final CreatePollServiceAsync pollService = GWT.create(CreatePollService.class);
		final MeetingSchedulingServiceAsync meetingSchedulingService = GWT.create(MeetingSchedulingService.class);
		
		MessageBox.prompt("Welcome", "Enter the poll id: ", new Listener<MessageBoxEvent> (){

			@Override
			public void handleEvent(MessageBoxEvent be) {
				if("ok".equalsIgnoreCase(be.getButtonClicked().getItemId()))
                {
					pollId = Integer.parseInt(be.getValue());
					
					pollService.getPollDetails(pollId, new AsyncCallback<String>(){

						@Override
						public void onFailure(Throwable caught) {
							MessageBox.info("Something went wrong!", "Server threw an error: " + caught.getMessage(), null);
						}

						@Override
						public void onSuccess(String result) {
							if(result != null){
								
								if(result.startsWith("TrackingInfo"))
									RootPanel.get("respondPanel").add(new TrackResponsesPanel(result, pollId).getPanel());
								else 
									RootPanel.get("respondPanel").add(new RespondPanel(result, pollId).getPanel());
							} else {
								
								MessageBox.confirm("Err..", "No responses yet! Send out reminders to invitees?", new Listener<MessageBoxEvent>(){

									@Override
									public void handleEvent(MessageBoxEvent be) {
										if(be.getButtonClicked().getItemId().equalsIgnoreCase("yes")){
											
											meetingSchedulingService.remindParticipants(pollId, new AsyncCallback<Boolean>(){

												@Override
												public void onFailure(Throwable caught) {
													Info.display("Failed!", "Failed to send emails! We're looking into it!");
												}

												@Override
												public void onSuccess(Boolean result) {
													Info.display("Success", "Email reminders sent!");
												}
											});
										}
									}
								});
							}
						}
					});
                }
			}
		});
	}
}
