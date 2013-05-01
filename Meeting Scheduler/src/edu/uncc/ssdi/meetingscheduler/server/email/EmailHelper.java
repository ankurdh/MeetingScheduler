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
	
	public static ArrayList<String> getSubjectAndBodyForScheduledMeeting(String subject, 
			Integer duration, String location, String organizedBy, String desc, String scheduleDateTime){
		
		ArrayList<String> subjectAndBody = new ArrayList<String>();
		String meetingSubject = "";
		
		if(!subject.toLowerCase().contains("meeting"))
			meetingSubject = subject + " Meeting";
		else meetingSubject = subject;
		
		subjectAndBody.add(0, "'" + meetingSubject + "' Schedule Notice");
		subjectAndBody.add(1, "The meeting with subject '" + meetingSubject + "', organized by '" + organizedBy + "' is scheduled to be at '" + 
				location + "' for a span of " + duration + "hrs starting from: " + scheduleDateTime);
		
		return subjectAndBody;
	}
	
	public static ArrayList<String> getSubjectAndBodyForPollReminder(String subject, Integer pollId){
		ArrayList<String> subjectAndBody = new ArrayList<String>();
		
		subjectAndBody.add(0, "Reminder To Poll Your Availability");
		subjectAndBody.add(1, "Gentle reminder to provide your availability for the meeting: " + subject +
				"\n\n Please follow the below URI and enter: " + pollId + " when prompted.\n" + 
				"http://127.0.0.1:8888/RespondToRequest.html?gwt.codesvr=127.0.0.1:9997");
		
		return subjectAndBody;
	}
}
