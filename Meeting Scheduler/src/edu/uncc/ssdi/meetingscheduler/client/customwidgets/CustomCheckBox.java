package edu.uncc.ssdi.meetingscheduler.client.customwidgets;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.SimpleCheckBox;

public class CustomCheckBox extends SimpleCheckBox {
	
	private Date dateTime;

	@SuppressWarnings("deprecation")
	public CustomCheckBox(Date d) {
		
		if(d != null)
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
