package edu.uncc.ssdi.meetingscheduler.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.uncc.ssdi.meetingscheduler.shared.exceptions.ObjectUnInitializedException;

public class DBHelper implements ServletContextListener {
	
	private static Log log = LogFactory.getLog(DBHelper.class);
	
	public static Connection connection;
	
	private static void connectToDB() throws SQLException, ClassNotFoundException {
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "root");
			
		} catch (ClassNotFoundException e){
			e.printStackTrace();
			throw e;
		} catch (SQLException e){
			e.printStackTrace();
			throw e;
		}	
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
		try{
			connection.close();
			log.info("Successfully closed the DB connection.");
		} catch(SQLException se){
			log.fatal("Failed to close connection.", se);
		}
		
		try {
			QueryHelper.closeStatement();
			log.info("Successfully closed the statement object.");
		} catch(SQLException se){
			log.fatal("Failed to close statement.", se);
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		
		try {
			connectToDB();
			log.info("Connected to DB successfully.");
			
			QueryHelper.initializeQueryHelper(connection);
			log.info("QueryHelper initialized successfully.");
			
		} catch(ClassNotFoundException e){
			e.printStackTrace();
		} catch(SQLException e){
			e.printStackTrace();
		} 		
	}
	
	public static Connection getConnection() throws ObjectUnInitializedException {
		
		if(connection == null)
			throw new ObjectUnInitializedException("Connection not initialized.");
		
		return connection;
		
	}

}
