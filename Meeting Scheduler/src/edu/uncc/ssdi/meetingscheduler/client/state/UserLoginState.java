package edu.uncc.ssdi.meetingscheduler.client.state;

public class UserLoginState {
	
	public static boolean userLoggedIn;
	public static UserDetails userDetails;
	
	static {
		userLoggedIn = true;
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
	
	public static Integer getLoggedInUserId(){
		return userDetails.getUserID();
	}
}

class UserDetails {
	
	private String userName;
	private Integer userId;
	
	public UserDetails(){
		userName = "Test";
		userId = new Integer(10);
	}
	
	public UserDetails(String userName){
		this.userName = userName;
	}
	
	public String getUserName(){
		return userName;
	}
	
	public Integer getUserID(){
		return userId;
	}
	
	public void setUserID(Integer userID){
		userId = userID;
	}
}
