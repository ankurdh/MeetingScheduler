package edu.uncc.ssdi.meetingscheduler.client.customwidgets;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

import edu.uncc.ssdi.meetingscheduler.client.jsonoverlayclasses.MeetingPollInfo;
import edu.uncc.ssdi.meetingscheduler.shared.exceptions.ObjectUnInitializedException;

public class RespondToPollCustomFlexTable extends FlexTable {
		
	private FlexCellFormatter cellFormatter = getFlexCellFormatter();
	private String timesJsonString;
	private Map<String, ArrayList<String>> datesTimesMap;
	
	public RespondToPollCustomFlexTable(String timesJsonString){
		this.timesJsonString = timesJsonString;
		
		datesTimesMap = new HashMap<String, ArrayList<String>> ();
		
	}
	
	@SuppressWarnings("deprecation")
	private void initTableHeader(){
		setWidth("1150px");
		
		int currentHour = 9;
		for(int i = 0 ; i <= 8 ; i ++){
			for(int j = 0 ; j < 2 ; j ++){
				
				cellFormatter.setAlignment(0, (2*i)+j+1, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
				
				Date d = new Date();
				d.setHours(currentHour);
				d.setMinutes(Integer.valueOf(j%2==0?"00":String.valueOf(30*j)));
				ColumnTimeMap.setValue((2*i)+j+1, d);
				
				setText(0, (2*i)+j+1, "" + currentHour + ":" + (j%2==0?"00":String.valueOf(30*j)));
				cellFormatter.setStyleName(0, 2*i+j+1, "NonDateColumn");
			}
			currentHour++;
		}
		
		getRowFormatter().addStyleName(0, "DateTimeWidgetHeader");
		
		addStyleName("DateTimeWidgetBody");
		
		setText(0, 0, "Date");		
		setCellSpacing(2);
	}
	
	/*!
	 * \brief populates the date-times map.
	 */
	private void populateDateTimesMap(){
		
		//Parse the json string and get the dates-times into a string array.
		MeetingPollInfo mpi = getMeetingPollInfo(timesJsonString);
		String jo = mpi.getDates().toString();
		System.out.println(jo);
		String [] datesArray = jo.split(",");
		
		ArrayList<String> listOfTimes;
		//parse the string array and populate the times map
		for(String s : datesArray){
			String currentDate = s.split(" ")[0];
			String currentTime = s.split(" ")[1].substring(0, 5);
			
			if(!datesTimesMap.containsKey(currentDate))
				listOfTimes = new ArrayList<String>();
			else
				listOfTimes = datesTimesMap.get(currentDate);

			listOfTimes.add(currentTime);
			datesTimesMap.put(currentDate, listOfTimes);
		}
	}
	
	public void populateFlexTable(){
		//Initialize the table header.
		initTableHeader();
		
		populateDateTimesMap();

		int currentHour;
		
		//TODO Populate the poll response table here. Dates are getting parsed successfully :)
		
		//TODO sort the datestimesmap on the dates. 
		
		//set the date in the first column
		for(String key : datesTimesMap.keySet()){
			int rows = getRowCount();
			ArrayList<String> times = datesTimesMap.get(key);
			setText(rows, 0, key);
			
			currentHour = 9;
			
			for(int i = 0 ; i <= 8 ; i ++){
				for(int j = 0 ; j < 2 ; j ++){
					//check if the following date has to be enabled or not.
					String currentIndexTime = "" + (currentHour == 9? "0" + currentHour : currentHour ) + ":" + (j%2==0?"00":String.valueOf(30*j));
					
					Date date = getDateFrom(key, currentIndexTime);
					CustomCheckBox checkBox = new CustomCheckBox(date);
					
					if(!times.contains(currentIndexTime)){
						checkBox.setEnabled(false);
						checkBox.addStyleName("disabled");
					}
					cellFormatter.setAlignment(rows, (2*i)+j+1, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
					setWidget(rows, (2*i)+j+1, checkBox);
				}
				currentHour++;
			}
		}
	}
	
	private Date getDateFrom(String dateString, String timeString){
		
		String dateTimeString = dateString + " " + timeString + ":00";
		DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
		Date date = dtf.parse(dateTimeString);
		
		return date;
	}
	
	public FlexTable getTable(){
		
		populateFlexTable();
		
		return this;
	}
	
	private final native MeetingPollInfo getMeetingPollInfo(String json)/*-{
		return eval("(" + json + ")");
	}-*/;
	
	public String getResponseJSONString() throws ObjectUnInitializedException {
		return CustomFlexTableHelper.getResponsesAsJSONString(this);
	}

}
