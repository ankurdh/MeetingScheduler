package edu.uncc.ssdi.meetingscheduler.client.customwidgets;

import edu.uncc.ssdi.meetingscheduler.shared.exceptions.ObjectUnInitializedException;

public class CustomFlexTableHelper {
	
	@SuppressWarnings("deprecation")
	public static String getTimesAndDatesAsJSON(CustomFlexTable timesTable) throws ObjectUnInitializedException {
		
		StringBuffer jsonString = new StringBuffer();
		boolean atleastOneElement = false;
		jsonString.append("[\n");
		
		//TODO iterate over the flex table here and get all the dates and times of selected checkboxes.
		int noOfRows = timesTable.getRowCount();
		
		for(int i = 1 ; i < noOfRows ; i ++){
			
			int noOfCols = timesTable.getCellCount(i);
			for(int j = 1 ; j < noOfCols ; j ++){
				CustomCheckBox checkBox = (CustomCheckBox)timesTable.getWidget(i, j);
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
