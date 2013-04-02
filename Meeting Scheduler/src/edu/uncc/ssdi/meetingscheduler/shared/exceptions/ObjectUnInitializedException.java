package edu.uncc.ssdi.meetingscheduler.shared.exceptions;

/**
 * 
 * @author Ankur
 *
 */
public class ObjectUnInitializedException extends Exception {

	public ObjectUnInitializedException(String string) {
		super(string);
	}
	
	public ObjectUnInitializedException(String string, Throwable e) {
		super(string, e);
	}

	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = -3132137645051422538L;

}
