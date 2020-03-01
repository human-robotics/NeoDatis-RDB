package org.neodatis.rdb;

import java.sql.Connection;
import java.sql.SQLException;

import org.neodatis.ConnectionPoolInfo;

/* The Simple interface for Connection Pool
@version 11/07/2002 - Olivier : Creation
*/

public interface ConnectionPool {

	public  ConnectionPoolInfo getConnectionPoolInfo();
	public  String getConnectionPoolInfoDesc();

	public  String getConnectionsStatus();

	public  int getNbConnections() throws SQLException;

	public  int getNbFreeConnections() throws SQLException;

	public  int getNumberOfAvailableConnections() throws SQLException;

	public  void releaseConnection(Connection in_connection) throws SQLException;

	public  Connection getConnection() throws Exception;

	public void init() throws Exception;
	
	public String getDatabase();
}
