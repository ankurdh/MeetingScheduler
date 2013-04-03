package edu.uncc.ssdi.meetingscheduler.client.customwidgets;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimpleCheckBox;

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
	
}

class MyFlexTable extends FlexTable {
	
	private FlexCellFormatter cellFormatter = getFlexCellFormatter();
	private ArrayList<Date> currentList = new ArrayList<Date>();
	
	@SuppressWarnings("deprecation")
	public MyFlexTable(){
		
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
				MyCheckBox checkBox = new MyCheckBox(checkBoxDate);
				cellFormatter.setAlignment(rows, (2*i)+j+1, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
				setWidget(rows, (2*i)+j+1, checkBox);
			}
		}
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

class MyCheckBox extends SimpleCheckBox {
	
	private Date dateTime;

	@SuppressWarnings("deprecation")
	public MyCheckBox(Date d) {
		
		dateTime = new Date(d.getYear(), d.getMonth(), d.getDate(), d.getHours(), d.getMinutes());

		addStyleName("CheckBoxStyle");
		
		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(!getValue())
					setValue(false);
				else setValue(true);
			}
		});

		addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				if (event.isShiftKeyDown())
					setValue(!getValue());
			}
		});
	}
	
	public Date getDate(){
		return dateTime;
	}
}