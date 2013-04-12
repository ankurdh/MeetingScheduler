package edu.uncc.ssdi.meetingscheduler.server.jsonclasses;

public class PollResponseMetadataJSONClass {
	
	private String name;
	private String comments;
	private Boolean isUnavailable;
	
	public PollResponseMetadataJSONClass() {}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Boolean getIsUnavailable() {
		return isUnavailable;
	}

	public void setIsAvailable(Boolean isAvailable) {
		this.isUnavailable = isAvailable;
	}
}
