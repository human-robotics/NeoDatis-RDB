package org.neodatis.rdb.implementation;

import java.sql.*;
import java.util.Properties;

/**
 * An easy database class. Offers static functions
 * 
 * @author Olivier Smadja - osmadja@gmail.com
 * @version 01/10/2000 - creation
 * @version 24/08/2000 Use of Tracer
 * @version 14/07/2001 adjust to new Tracer class(with labels)
 **/
public class Database {
	public static final String NO_USER_NEEDED = "NO_USER_NEEDED";

	/**
	 * To create a database connection
	 * 
	 * @param String
	 *            The User name
	 * @param String
	 *            The Password
	 * @param String
	 *            The Name of the driver
	 * @param String
	 *            The URL
	 * @return Connection The new database connection
	 * @example : for Oracle connection user=olivier password=olivier
	 *          driver=oracle.jdbc.driver.OracleDriver
	 *          url=jdbc:oracle:thin:@java2:1521:ANET where java2 is the server
	 *          name and ANET is the Oracle SID
	 */
	public static Connection createJdbcConnection(String userName,
			String password, String driver, String url

	) throws ClassNotFoundException, java.sql.SQLException {

		Connection conn = null;
		Properties props = new Properties();

		// Load the Oracle JDBC driver
		Class.forName(driver);

		if (!NO_USER_NEEDED.equals(userName)) {
			// Sets user and password
			props.put("user", userName);
			props.put("password", password);
		}

		conn = DriverManager.getConnection(url, props);

		return conn;
	}

}