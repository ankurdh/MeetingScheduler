package edu.uncc.ssdi.meetingscheduler.server.email;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EmailFactory implements ServletContextListener{
	
	private static Log log = LogFactory.getLog(EmailFactory.class);
	
	private static Email email;
	
	public EmailFactory(){	}
	
	public static void createInstance(){
		email = new Email();
		email.initialize();
	}
	
	public static Email getInstance(){
		if(email == null)
			createInstance();
		
		return email;
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		createInstance();		
		log.info("Initialized Email client successfully");
	}
}
