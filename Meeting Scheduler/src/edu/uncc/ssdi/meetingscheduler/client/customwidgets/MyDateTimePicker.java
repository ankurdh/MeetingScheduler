package edu.uncc.ssdi.meetingscheduler.client.customwidgets;

import java.util.Date;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;

import edu.uncc.ssdi.meetingscheduler.shared.exceptions.ObjectUnInitializedException;

public class MyDateTimePicker extends DatePicker {
	
	private CustomFlexTable timesTable;
	private HorizontalPanel panel;
	
	private boolean rangeSelectionChosen = false;
	private Date firstClickedDate;
	private El firstClickedElement;
	
	public MyDateTimePicker(){
		
		panel = new HorizontalPanel();
		panel.setSpacing(10);
		timesTable = new CustomFlexTable(true);
		
		timesTable.setCellSpacing(2);
		timesTable.setCellPadding(5);
		
		panel.add(this);
		panel.add(timesTable);
		
	}
	
	public CustomFlexTable getTimesTable(){
		return timesTable;
	}
	
	public Panel getPanel() {
		return panel;
	}
	
	@Override
	public void onDayClick(ComponentEvent ce){
		super.onDayClick(ce);
				
		El target = ce.getTargetEl();
	    final El e = target.findParent("a", 5);
	    
	    if (e != null) {
	    	e.setBorders(true);
	    	
	    	String dt = e.dom.getPropertyString("dateValue");
	    	String[] tokens = dt.split(",");
	        int year = Integer.parseInt(tokens[0]);
	        int month = Integer.parseInt(tokens[1]);
	        int day = Integer.parseInt(tokens[2]);
	        final Date d = new DateWrapper(year, month, day).asDate();
	        
	        if(timesTable.contains(d)){
	        	MessageBox.confirm("Duplicate Day", "Day already selected. Do you want to delete it?", new Listener<MessageBoxEvent>(){

					@Override
					public void handleEvent(MessageBoxEvent be) {
						if(be.getButtonClicked().getText().equalsIgnoreCase("yes")){
							timesTable.remove(d);
							e.setBorders(false);
							e.removeStyleName("lightGreenBG");
						}
					}
	        	});
	        	
	        	return;
	        }
	    	
	    	if(ce.isShiftKey()){
	    		
	    		if(!rangeSelectionChosen){
	    			e.setStyleName("lightGreenBG");
	    			e.setBorders(true);
		    		rangeSelectionChosen = true;
		    		
		    		firstClickedElement = e;		    		
		    		firstClickedDate = d;
		    		
		    		timesTable.addRow(d);
		    		
	    		}
	    		else
	    		{
	    			e.setStyleName("lightGreenBG");
		    		rangeSelectionChosen = false;
		    		if(firstClickedDate.before(d)){
		    			Date nextDate = null;
		    			do{
		    				Element sibling = firstClickedElement.getParent().nextSibling().getFirstChildElement();
			    			El nextDateEl = El.fly(sibling);
			    			nextDateEl.setStyleName("lightGreenBG");
			    			nextDateEl.setBorders(true);
			    			
			    			String nextDateString = nextDateEl.dom.getPropertyString("dateValue");
			    	    	String[] dateTokens = nextDateString.split(",");
			    	        int y = Integer.parseInt(dateTokens[0]);
			    	        int m = Integer.parseInt(dateTokens[1]);
			    	        int dd = Integer.parseInt(dateTokens[2]);
			    	        nextDate = new DateWrapper(y, m, dd).asDate();

			    	        if(!timesTable.contains(nextDate))
			    	        	timesTable.addRow(nextDate);
			    			
			    			firstClickedElement = nextDateEl;
			    			
		    			} while (nextDate.before(d));
		    		}
	    		}
	    	} else {
	    		timesTable.addRow(d);
	    		e.setBorders(true);
	    		e.setStyleName("lightGreenBG");
	    	}
	    }
	}
}