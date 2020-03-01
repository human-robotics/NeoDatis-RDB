package org.neodatis.rdb.implementation;

import java.sql.Connection;
import java.util.Date;


class ConnectionInfo {

	/** The connection object */
	protected Connection _connection;

	/** The name of the connection **/
	protected String name;

	/** The user name **/
	protected String userName;

	/** The password **/
	protected String password;

	/** The driver name **/
	protected String driver;

	/** The driver url **/
	protected String _sUrl;

	/** The number of the connection **/
	protected int connectionNumber;

	/** Creation date of the connection */
	protected Date creationDate;

	/** Last use */
	protected Date lastUse;

	/** The date of the getConncetion */
	protected Date get;

	/** Connection closing timeout */
	protected long timeOut;

	/** To check if connection is available */
	protected boolean isAvailable;

	/** Was killed remotly ? */
	protected boolean killed;

	/**
	 * Constructor
	 * 
	 * @param String
	 *            The Conncetion sql object
	 * @param String
	 *            The name of the connection
	 * @param String
	 *            The user
	 * @param String
	 *            The password
	 * @param String
	 *            The driver name
	 * @param String
	 *            The driver url
	 * @param int The connection Number
	 * @param long The automatic connection timeout
	 **/
	public ConnectionInfo(Connection in_connection, String in__sName, String in__sUserName, String in__sPassword, String in__sDriver, String in__sUrl,
			int in__nConnectionNumber, long in_nTimeout) {
		_connection = in_connection;
		name = in__sName;
		userName = in__sUserName;
		password = in__sPassword;
		driver = in__sDriver;
		_sUrl = in__sUrl;
		connectionNumber = in__nConnectionNumber;
		timeOut = in_nTimeout;
		creationDate = new Date();
		lastUse = null;
		get = null;
		isAvailable = true;
		killed = false;
	}

	/** Release the connection */
	public void release() {
		isAvailable = true;
	}

	/** To check if the connection is the same */
	public boolean isSameConnection(Connection in_connection) {
		return (_connection == in_connection);
	}

	/** To check if a connection is close */
	public boolean isClosed() {
		try {
			// Do not need to check this.
			return false;
			// return _connection.isClosed();
		} catch (Exception e) {
			return false;
		}
	}

	/** To check if connection is killed */
	public boolean isKilled() {
		return killed;
	}

	/** Sets the connection to killed status */
	public void setKilled(boolean in_bKilled) {
		killed = in_bKilled;
	}

	/** To check if a connection is free : available and not close */
	public boolean isAvailable() {
		if (isAvailable) {
			if (!isClosed()) {
				return true;
			} else {
				// if connection is closed.

			}
		} else {
			// Check for timeout
			if (getUseTime() > timeOut) {
				DefaultConnectionPool.logger.info("!!! The connection " + connectionNumber + " called " + name + " has been used during more than timeout "
						+ timeOut);
				DefaultConnectionPool.logger.info(" ====>> set this connection to available status");
				isAvailable = true;
				return true;
			}

			return false;
		}

		return false;
	}

	/** Get The time of the last use of the conncetion in ms */
	public long getUseTime() {
		try {
			return new Date().getTime() - get.getTime();
		} catch (Exception e) {
			return 0;
		}
	}

	/** Reserve the connection */
	public void reserve() {
		isAvailable = false;
	}

	/** Accessor to get _connection */
	public Connection getConnection() {
		lastUse = new Date();
		get = new Date();
		reserve();
		return _connection;
	}

	/** Accessor to get the value of _sName */
	public String getName() {
		return name;
	}

	/** Accessor to set the value of _sName */
	public void setName(String in__sName) {
		name = in__sName;
	}

	/** Accessor to get the value of _sUserName */
	public String getUserName() {
		return userName;
	}

	/** Accessor to set the value of _sUserName */
	public void setUserName(String in__sUserName) {
		userName = in__sUserName;
	}

	/** Accessor to get the value of _sPassword */
	public String getPassword() {
		return password;
	}

	/** Accessor to set the value of _sPassword */
	public void setPassword(String in__sPassword) {
		password = in__sPassword;
	}

	/** Accessor to get the value of _sDriver */
	public String getDriver() {
		return driver;
	}

	/** Accessor to set the value of _sDriver */
	public void setDriver(String in__sDriver) {
		driver = in__sDriver;
	}

	/** Accessor to get the value of _sUrl */
	public String getUrl() {
		return _sUrl;
	}

	/** Accessor to set the value of _sUrl */
	public void setUrl(String in__sUrl) {
		_sUrl = in__sUrl;
	}

	/** Accessor to get the value of _nConnectionNumber */
	public int getConnectionNumber() {
		return connectionNumber;
	}

	/** Accessor to set the value of _nConnectionNumber */
	public void setNbConnections(int in__nConnectionNumber) {
		connectionNumber = in__nConnectionNumber;
	}

	/** Accessor to get the value of getDate */
	public Date getGetDate() {
		return get;
	}

	/**
	 * To check if the object matches the incoming string
	 * 
	 * @return boolean
	 * @param String
	 **/
	public boolean match(String in_sStringToMatch)

	{

		return name.equals(in_sStringToMatch);
	}

	/**
	 * To get the description of the object
	 * 
	 * @return String
	 **/
	public String toString()

	{

		StringBuffer sResult = new StringBuffer();

		sResult.append(" ConnectionName = " + name);

		sResult.append(" UserName = " + userName);

		sResult.append(" Password = " + password);

		sResult.append(" Driver = " + driver);

		sResult.append(" Url = " + _sUrl);

		sResult.append(" ConnectionNumber= " + connectionNumber);

		return sResult.toString();
	}

	public static void main(String[] args) throws Exception {

		new DefaultConnectionPool().init();
	}

	public void setConnection(Connection connection) {
		this._connection = connection;
		this.isAvailable = true;
	}
}