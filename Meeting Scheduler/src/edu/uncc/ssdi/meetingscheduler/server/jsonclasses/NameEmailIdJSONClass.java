package edu.uncc.ssdi.meetingscheduler.server.jsonclasses;

public class NameEmailIdJSONClass {
	
	private String name;
	private String email;
	
	public NameEmailIdJSONClass(){}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setEmailID(String email){
		this.email = email;
	}
	
	public String getName() {
		return name;
	}
	
	public String getEmailId(){
		return email;
	}
}
