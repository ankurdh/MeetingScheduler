package edu.uncc.ssdi.meetingscheduler.client.customwidgets;

import java.util.Date;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;

import edu.uncc.ssdi.meetingscheduler.shared.exceptions.ObjectUnInitializedException;

public class MyDateTimePicker extends DatePicker {
	
	private MyFlexTable timesTable;
	private HorizontalPanel panel;
	
	public MyDateTimePicker(){

		panel = new HorizontalPanel();
		panel.setSpacing(10);
		timesTable = new MyFlexTable();
		
		timesTable.setCellSpacing(2);
		timesTable.setCellPadding(5);
		
		panel.add(this);
		panel.add(timesTable);
		
	}
	
	public Panel getPanel() {
		return panel;
	}
	
	@Override
	public void onDayClick(ComponentEvent ce){
		super.onDayClick(ce);
		El target = ce.getTargetEl();
	    El e = target.findParent("a", 5);
	    if (e != null) {
	    	String dt = e.dom.getPropertyString("dateValue");
	    	String[] tokens = dt.split(",");
	        int year = Integer.parseInt(tokens[0]);
	        int month = Integer.parseInt(tokens[1]);
	        int day = Integer.parseInt(tokens[2]);
	        Date d = new DateWrapper(year, month, day).asDate();
	        
	        timesTable.addRow(d);
	        
	    }
	}
	
	@SuppressWarnings("deprecation")
	public String getTimesAndDatesAsJSON() throws ObjectUnInitializedException {
		
		StringBuffer jsonString = new StringBuffer();
		boolean atleastOneElement = false;
		jsonString.append("[\n");
		
		//TODO iterate over the flex table here and get all the dates and times of selected checkboxes.
		int noOfRows = timesTable.getRowCount();
		
		for(int i = 1 ; i < noOfRows ; i ++){
			
			int noOfCols = timesTable.getCellCount(i);
			for(int j = 1 ; j < noOfCols ; j ++){
				MyCheckBox checkBox = (MyCheckBox)timesTable.getWidget(i, j);
				if(checkBox.getValue()){
					atleastOneElement = true;
					jsonString.append("{\"dateTime\":\"" + (checkBox.getDate().getYear() + 1900) + "-"
					+ (checkBox.getDate().getMonth() + 1) + "-"
					+ (checkBox.getDate().getDate() < 10? ("0" + checkBox.getDate().getDate()) : checkBox.getDate().getDate()) + " "
					+ (checkBox.getDate().getHours() == 9? "09":checkBox.getDate().getHours()) + ":"
					+ (checkBox.getDate().getMinutes() == 0?"00":checkBox.getDate().getMinutes()) + ":00\"},\n");
					
				}
			}
		}
		
		if(!atleastOneElement)
			throw new ObjectUnInitializedException("Please select atleast one date-time(s)!");
		
		jsonString.setCharAt(jsonString.lastIndexOf(","), '\n');
		jsonString.append("]");
		
		System.out.println(jsonString.toString());
		return jsonString.toString();
	}
}