package edu.uncc.ssdi.meetingscheduler.server.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Ankur
 *
 */
public class QueryHelper {
	
	private static Log log = LogFactory.getLog(QueryHelper.class);
	private static Statement s;
	private static Connection connection;
	
	/**
	 * 
	 * @param a Connection object that will be used by all the class methods.
	 * @throws SQLException
	 */
	public static void initializeQueryHelper(Connection c) throws SQLException{
		connection = c;
		try {
			s = connection.createStatement();
			
		} catch (SQLException e) {
			log.error("Failed to create statement object.", e);
			throw e;
		}
	}
	
	/**
	 * @param query
	 * @return returns true if the query yeilds atleast one row.
	 */
	public static boolean doesRowExistAs(String query){
		
		try {
			
			ResultSet rs = getResultSet(query);
			
			if(rs.next())
				return true;
		} catch (SQLException e) {
			log.error("Failed to get result set." , e);
		}
		
		return false;
		
	}
	
	/**
	 * @param query
	 * @return returns the result of the query as ResultSet.
	 * @throws SQLException
	 */
	public static ResultSet getResultSet(String query) throws SQLException{
		
		try {
			return s.executeQuery(query);
		} catch (SQLException e) {
			throw e;
		}		
	}
	
	/**
	 * @param query
	 * @throws SQLException
	 */
	public static void fireInsert(String query) throws SQLException {
		try {
			s.executeUpdate(query);
		} catch (SQLException e) {
			throw e;
		}
	}
	
	/**
	* @author Ankur
	* @return returns the next id for the table. Not required to increment the returned value. 
	* @param query that is of the form "select max(pk_col) from <desired_table_name>
	*/
	public static int getNextIndex(String query){
		int tableIndex = 1;
		
		try {
			ResultSet rs = getResultSet(query);
			while (rs.next()) {
				tableIndex = rs.getInt(1) + 1;
			}
		} catch (SQLException se) {
			
		}
		
		return tableIndex;
		
	}
	
	/**
	 * @param Value that sets the AutoCommitt
	 */
	public static void setAutoCommit(boolean val){
		try {
			connection.setAutoCommit(val);
		} catch (SQLException e) {
			e.printStackTrace();
			//TODO should not be ignored ideally!
			//TODO throw the exception further down the chain.
		}
	}

	/**
	 * @throws SQLException
	 * commits on the connection object used by the query helper.
	 */
	public static void makeCommit() throws SQLException {
		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static void closeStatement() throws SQLException {
		s.close();
	}
}
