package edu.uncc.ssdi.meetingscheduler.client.state;

public class UserLoginState {
	
	public static boolean userLoggedIn;
	public static UserDetails userDetails;
	
	static {
		userLoggedIn = false;
		userDetails = new UserDetails();
	}
	
	public static void setUserLoginState(boolean val){
		userLoggedIn = val;
	}
	
	public static String getLoggedInUserName(){
		return userDetails.getUserName();
	}
	
	public static boolean getUserLoginState(){
		return userLoggedIn;
	}
}

class UserDetails {
	
	private String userName;
	//other required user attributes.
	
	public UserDetails(){
		
	}
	
	public UserDetails(String userName){
		this.userName = userName;
	}
	
	public String getUserName(){
		return userName;
	}
}
