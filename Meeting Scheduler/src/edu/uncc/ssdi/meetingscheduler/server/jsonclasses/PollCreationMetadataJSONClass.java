package edu.uncc.ssdi.meetingscheduler.server.jsonclasses;

public class PollCreationMetadataJSONClass {
	
	private String subject;
	private String location;
	private String desc;
	private String organizer;
	private Integer duration;
	
	public PollCreationMetadataJSONClass(){
		
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getOrganizer() {
		return organizer;
	}

	public void setOrganizer(String organizer) {
		this.organizer = organizer;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}
}
