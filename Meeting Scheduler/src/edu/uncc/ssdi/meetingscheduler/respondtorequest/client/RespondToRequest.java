/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package edu.uncc.ssdi.meetingscheduler.respondtorequest.client;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

import edu.uncc.ssdi.meetingscheduler.client.panels.TrackResponsesPanel;
import edu.uncc.ssdi.meetingscheduler.client.services.CreatePollService;
import edu.uncc.ssdi.meetingscheduler.client.services.CreatePollServiceAsync;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * @author Ankur
 */
public class RespondToRequest implements EntryPoint {
	private Integer pollId; 
	
	public void onModuleLoad() {
		
		final CreatePollServiceAsync pollService = GWT.create(CreatePollService.class);
		
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
								
								MessageBox.info("Err..", "No responses yet!", null);
								
							}
							
						}
					});
                }
			}
		});
	}
}
