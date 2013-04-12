package edu.uncc.ssdi.meetingscheduler.client.customwidgets;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

import edu.uncc.ssdi.meetingscheduler.client.jsonoverlayclasses.MeetingPollInfo;

public class CustomFlexTable extends FlexTable {
	
	private FlexCellFormatter cellFormatter = getFlexCellFormatter();
	private ArrayList<Date> currentList = new ArrayList<Date>();
	
	@SuppressWarnings("deprecation")
	private void initializeForPollCreation(){
		
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
	
	public CustomFlexTable(boolean initForPollCreation, String timesJsonString){
		if(initForPollCreation)
			initializeForPollCreation();
		else initializeForPollResponse(timesJsonString);
	}
	
	public CustomFlexTable(boolean initForPollCreation){
		this(initForPollCreation, null);
	}

	private void initializeForPollResponse(String timesJsonString) {

	}

	public boolean contains(Date d){
		if(currentList.contains(d))
			return true;
		
		return false;
	}
	
	public void remove(Date d){
		currentList.remove(d);
		
		int rows = getRowCount();
		String dateToDelete = new MyDateFormatter("EEE, MMM d, yyyy").getFormattedDate(d);
		
		for(int i = 1 ; i < rows ; i ++){
			if(getText(i, 0).equals(dateToDelete)){
				removeRow(i);
				return;
			}
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public void addRow(Date d) {
		
		int rows = getRowCount();
		MyDateFormatter dateFormat = new MyDateFormatter("EEE, MMM d, yyyy");
		setText(rows, 0, dateFormat.getFormattedDate(d));
		
		currentList.add(d);
		
		for(int i = 0 ; i <= 8 ; i ++){
			for(int j = 0 ; j < 2 ; j ++){
				Date checkBoxDate = new Date(d.getYear(), d.getMonth(), d.getDate(), ColumnTimeMap.getValue((2*i)+j+1).getHours(), ColumnTimeMap.getValue((2*i)+j+1).getMinutes());
				CustomCheckBox checkBox = new CustomCheckBox(checkBoxDate);
				cellFormatter.setAlignment(rows, (2*i)+j+1, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
				setWidget(rows, (2*i)+j+1, checkBox);
			}
		}
	}		
}