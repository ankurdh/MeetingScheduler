package edu.uncc.ssdi.meetingscheduler.server.email;

import java.util.ArrayList;

public class EmailHelper {
	
	public static ArrayList<String> getSubjectAndBodyForPollCreation(int pollId, String pollStarterName, String respondingPageURI){
		ArrayList<String> subjectAndBody = new ArrayList<String>();
		
		subjectAndBody.add(0, pollStarterName + " has requested your availability");
		
		String body = "Please follow the below URI and enter: " + pollId + " when prompted.\n " + respondingPageURI;
		subjectAndBody.add(1, body);		
		
		return subjectAndBody;
	}

}
