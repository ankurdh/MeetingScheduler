package edu.uncc.ssdi.meetingscheduler.client.customwidgets;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Panel;

public class DateTimeWidget {

	private MyDateTimePicker myCalendar;
	
	public DateTimeWidget(){
		
		myCalendar = new MyDateTimePicker();
				
	}
	
	public MyDateTimePicker getDateTimePicker(){
		return myCalendar;
	}
	
	public Panel getDateTimeWidget(){
		return myCalendar.getPanel();
	}
	
	public CustomFlexTable getCustomFlexTable(){
		return myCalendar.getTimesTable();
	}
	
}


class MyDateFormatter extends DateTimeFormat {

	public MyDateFormatter(String pattern) {
		super(pattern);
	}
	
	public String getFormattedDate(Date d){
		return format(d);
	}
	
	public static String getStrinFromPattern(String pattern, String date){
		
		return null;
	}
	
}

class ColumnTimeMap {
	public static Map<Integer, Date> timeMap = new HashMap<Integer, Date>();
	
	public static void setValue(Integer key, Date d){
		timeMap.put(key, d);
	}
	
	public static Date getValue(Integer key){
		return timeMap.get(key);
	}
	
}