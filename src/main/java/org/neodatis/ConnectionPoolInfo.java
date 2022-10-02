package org.neodatis;

public class ConnectionPoolInfo {
	
	/**
	 * Default value for automatic closing timeout : 1 year
	 */
	public static final long INFINITE = 365 * 24 * 60 * 60 * 1000;


	protected String connectionName;
	protected String userName;
	protected String password;
	protected String driver;
	protected String url;
	protected int nbConnections;
	protected long timeOut;
	protected boolean testConnections;
	protected String testQuery;
	protected long testConnectionsEveryMs;
	protected String databaseType;
	protected Boolean autocommit;
	
	
	
	public enum DatabaseType {
		oracle,
		mysql,
		derby,
		postgresql,
		sqlserver,
		sqlite		
	}

	
	
	/**
	 * 
	 * @param databaseType Use {@link ConnectionPoolInfo}.{@link DatabaseType}. If you have a string , use {@link ConnectionPoolInfo}.{@link DatabaseType}.valueOf(...)
	 * @param connectionName The name of the connection (for debug)
	 * @param userName The connection database use name
	 * @param password The connection password
	 * @param driver the jdbc driver class name
	 * @param url the jdbc url class
	 * @param nbConnections The number of connections of the pool
	 * @param timeOut The timeout for the connection(not used)
	 * @param testConnections true for NeoDatis to periodically test the connections
	 * @param testQuery The test query (to test connections) to use
	 * @param testConnectionsEveryMs The perior of test in ms. Ex 60000, will test every minute
	 */
	public ConnectionPoolInfo(DatabaseType databaseType, String connectionName, String userName, String password, String driver, String url, int nbConnections, long timeOut, boolean testConnections, String testQuery, long testConnectionsEveryMs) {
		super();
		this.databaseType = databaseType.toString();
		this.connectionName = connectionName;
		this.userName = userName;
		this.password = password;
		this.driver = driver;
		this.url = url;
		this.nbConnections = nbConnections;
		this.timeOut = timeOut;
		this.testConnections = testConnections;
		this.testQuery = testQuery;
		this.testConnectionsEveryMs = testConnectionsEveryMs;
	}
	
	/**
	 * 
	 * @param databaseType Use {@link ConnectionPoolInfo}.{@link DatabaseType}. If you have a string , use {@link ConnectionPoolInfo}.{@link DatabaseType}.valueOf(...) 
	 * @param userName The connection database use name
	 * @param password The connection password
	 * @param driver the jdbc driver class name
	 * @param url the jdbc url class
	 * @param nbConnections The number of connections of the pool
	 * @param testConnections true for NeoDatis to periodically test the connections
	 * @param testQuery The test query (to test connections) to use
	 * @param testConnectionsEveryMs The period of test in ms. Ex 60000, will test every minute
	 */
	public ConnectionPoolInfo(String databaseType, String userName, String password, String driver, String url, int nbConnections, boolean testConnections, String testQuery, long testConnectionsEveryMs) {
		super();
		this.databaseType = databaseType;
		this.connectionName = "default";
		this.userName = userName;
		this.password = password;
		this.driver = driver;
		this.url = url;
		this.nbConnections = nbConnections;
		this.timeOut = INFINITE;
		this.testConnections = testConnections;
		this.testQuery = testQuery;
		this.testConnectionsEveryMs = testConnectionsEveryMs;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}



	public void setUrl(String url) {
		this.url = url;
	}



	public int getNbConnections() {
		return nbConnections;
	}



	public void setNbConnections(int nbConnections) {
		this.nbConnections = nbConnections;
	}



	public long getTimeOut() {
		return timeOut;
	}



	public void setTimeOut(long timeOut) {
		this.timeOut = timeOut;
	}



	/**
	 * To check if the object matches the incoming string
	 * 
	 * @return boolean
	 * @param String
	 **/
	public boolean match(String in_sStringToMatch)

	{

		return connectionName.equals(in_sStringToMatch);
	}

	public boolean isTestConnections() {
		return testConnections;
	}

	public void setTestConnections(boolean testConnections) {
		this.testConnections = testConnections;
	}

	public String getTestQuery() {
		return testQuery;
	}

	public void setTestQuery(String testQuery) {
		this.testQuery = testQuery;
	}

	public long getTestConnectionsEveryMs() {
		return testConnectionsEveryMs;
	}

	public void setTestConnectionsEveryMs(long testConnectionsEveryMs) {
		this.testConnectionsEveryMs = testConnectionsEveryMs;
	}

	

	@Override
	public String toString() {
		return "ConnectionPoolInfo [connectionName=" + connectionName + ", userName=" + userName + ", password=" + password + ", driver=" + driver + ", url="
				+ url + ", nbConnections=" + nbConnections + ", timeOut=" + timeOut + ", testConnections=" + testConnections + ", testQuery=" + testQuery
				+ ", testConnectionsEveryMs=" + testConnectionsEveryMs + ", databaseType=" + databaseType + ", autocommit=" + autocommit + "]";
	}

	public String getDatabaseType() {
		return databaseType;
	}
	
	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}

	public Boolean getAutocommit() {
		return autocommit;
	}

	public void setAutocommit(Boolean autocommit) {
		this.autocommit = autocommit;
	}
	
	
	
	
	
	
}