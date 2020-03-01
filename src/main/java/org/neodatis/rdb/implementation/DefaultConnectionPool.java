package org.neodatis.rdb.implementation;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;

import org.apache.log4j.Category;
import org.neodatis.ConnectionPoolInfo;
import org.neodatis.rdb.Config;
import org.neodatis.rdb.ConnectionPool;
import org.neodatis.rdb.Version;
import org.neodatis.tools.StringUtils;

/**
 * a database connection pool
 *
 * @author Olivier smadja - mailto:osmadja@netcourrier.com
 * @version 09/07/2002 Change package and philosophy 02/2012 : add tyny mysql :
 *          Short
 */

public class DefaultConnectionPool implements ConnectionPool {
	public final static String DEFAULT_SELECT = "select 1 from test_connections";

	/**
	 * Creates the root
	 */
	static Category logger = Category.getInstance(DefaultPrimaryKey.class.getName());

	/**
	 * Default name for available connections
	 */
	static final String _DEFAULT_NAME = "default name";

	/**
	 * List of connection
	 */
	protected List<ConnectionInfo> connections;

	/**
	 * The number of spool connection
	 */
	protected int _nNbConnections;

	/**
	 * To check if the object is ok
	 */
	protected boolean _bOk = false;

	/**
	 * To check if the object is ok
	 */
	protected boolean ready = false;

	/**
	 * To check resource init status
	 */
	protected boolean resourceIsOk = false;

	/**
	 * To get init parameters
	 */
	protected Properties _resource;

	/**
	 * The name of the initialization file
	 */
	protected static final String DEFAULT_FILE_NAME = "/ConnectionPool";

	protected String propertyFileName;
	protected boolean usedSpecificFileName;
	protected String database;

	/**
	 * To keep connection info
	 */
	protected ConnectionPoolInfo connectionPoolInfo;

	/**
	 * To indicate if the pool is dynamic or not. Dynamic pool can create more
	 * connections on demand
	 */
	protected boolean _bDynamicPool;

	// protected static DefaultConnectionPool instance;
	protected static Map<String, DefaultConnectionPool> pools = new HashMap<String, DefaultConnectionPool>();

	public static DefaultConnectionPool getInstance(ConnectionPoolInfo cpi) {
		if (cpi == null) {
			return getDefaultInstance();
		}
		DefaultConnectionPool pool = pools.get(cpi.getConnectionName());
		if (pool == null) {
			pool = new DefaultConnectionPool(cpi.getConnectionName());
			try {
				pool.init(cpi);
				pools.put(cpi.getConnectionName(), pool);
				pools.put(DEFAULT_FILE_NAME, pool);

			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		}
		return pool;
	}

	public static synchronized DefaultConnectionPool getInstance(String propertyFile) throws Exception {
		if (propertyFile == null) {
			return getDefaultInstance();
		}
		DefaultConnectionPool pool = pools.get(propertyFile);
		if (pool == null) {
			pool = new DefaultConnectionPool(propertyFile);
			pool.init();
			pools.put(propertyFile, pool);
		}
		return pool;
	}

	public static synchronized DefaultConnectionPool getDefaultInstance() {
		DefaultConnectionPool pool = pools.get(DEFAULT_FILE_NAME);
		if (pool == null) {
			pool = new DefaultConnectionPool(DEFAULT_FILE_NAME);
			pool.usedSpecificFileName = false;
			try {
				pool.init();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			pools.put(DEFAULT_FILE_NAME, pool);
		}
		return pool;
	}

	public DefaultConnectionPool(String propertyFile) {
		this.propertyFileName = propertyFile;
		if (propertyFileName != null) {
			this.usedSpecificFileName = true;
		}
	}

	public DefaultConnectionPool() {
		// this.propertyFileName = DEFAULT_FILE_NAME;
		this.usedSpecificFileName = false;
	}

	/**
	 * Inits the pool - creates all connections
	 *
	 * @throws Exception
	 */
	synchronized public void init() throws Exception {
		// If pool is already initialized , do nothing
		if (ready) {
			return;
		}

		_bOk = false;

		// To get config parameters
		initResource();

		// If config file has been found
		if (resourceIsOk) {
			try {
				// Get the indicator to know if pool must be dynamic : if it can
				// grow on demand
				String sTemp = getInitParameter("DynamicPool", "no");
				_bDynamicPool = (sTemp.equals("yes") ? true : false);

				logger.info("DynamicPool=" + _bDynamicPool);

				// New 10/05/2001

				String sPool = null;
				String sTimeout = null;
				int poolsize = 0;
				long nTimeout = 0;

				// Gets the number of connection of the pool
				sPool = getInitParameter("pool.size");
				if (sPool == null) {
					logger.error("WARNING : Please use pool.size instead of pool to specify the size of the pool");
					sPool = getInitParameter("pool");
				}

				database = getInitParameter("database_type");

				Config.databaseType = database;

				// check the time to automatically release a connetion
				// If does not exist then use INFINITE
				sTimeout = getInitParameter("automatic_closing_timeout", String.valueOf(ConnectionPoolInfo.INFINITE));

				// Convert string to int
				poolsize = Integer.parseInt(sPool);
				nTimeout = Long.parseLong(sTimeout);

				String testQuery = getInitParameter("test.query");
				// true or false
				String testConnections = getInitParameter("test.connections");
				String testConnectionsEveryMs = getInitParameter("test.connections.period.in.ms");

				String other = System.getProperty("neodatis.rdb.test.query");
				if (other != null) {
					testQuery = other;
				}

				if (testQuery == null) {
					testQuery = DEFAULT_SELECT;
				}

				
				String sAutocommit = getInitParameter("autocommit");
				
				Boolean autocommit = null;
				if(sAutocommit!=null) {
					autocommit = "true".equals(sAutocommit) ? Boolean.TRUE:Boolean.FALSE;
				}
				
				
				boolean bTestConnections = false;
				long nTestConnectionEveryMs = 30000;
				if (testConnections != null) {
					bTestConnections = testConnections.equals("true");
				}
				if (testConnectionsEveryMs != null) {
					nTestConnectionEveryMs = Long.parseLong(testConnectionsEveryMs);
				}

				if (bTestConnections) {
					// we need a connection to execute the query to test
					// connections :-)
					logger.debug("Adding 1 connection to execute the query to test connections");
					poolsize++;
				}
				// Create a connection pool info
				connectionPoolInfo = new ConnectionPoolInfo( ConnectionPoolInfo.DatabaseType.valueOf(Config.databaseType), "default pool", getInitParameter("user"), getInitParameter("password"),
						getInitParameter("driver"), getInitParameter("url"), poolsize, nTimeout, bTestConnections, testQuery, nTestConnectionEveryMs);

				connectionPoolInfo.setAutocommit(autocommit);
				init(connectionPoolInfo);

			} catch (MissingResourceException e) {

				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				logger.error(sw.toString());
				logger.fatal("Resource does not contain all parameters(driver,url,user,password,pool)");
				_bOk = false;
			}
		}

	}

	/**
	 * Inits the pool - creates all connections
	 *
	 * @throws Exception
	 */
	synchronized public void init(ConnectionPoolInfo connectionPoolInfo) throws Exception {

		// Creates the list
		connections = new ArrayList<ConnectionInfo>();
		Config.databaseType = connectionPoolInfo.getDatabaseType();

		// Connects and creates connections
		_bOk = connect(connectionPoolInfo);

		ready = true;

		if (connectionPoolInfo.isTestConnections()) {
			Thread t = new Thread(new ThreadToTestConnections(this, connectionPoolInfo.getTestConnectionsEveryMs(), connectionPoolInfo.getTestQuery()));
			t.start();
		} else {
			logger.info("Connection won't check connections");
		}

		//
		String sForceTableNameUpperCase = getInitParameter("force.table.name.uppercase");
		if (sForceTableNameUpperCase != null) {
			if ("true".equals(sForceTableNameUpperCase)) {
				Config.forceTableNameUpperCase = true;
				logger.info("forceTableNameUpperCase is true");
			} else {
				logger.info("forceTableNameUpperCase is false");
			}
		} else {
			logger.info("forceTableNameUpperCase is not defined");
		}
		String sForceTableNameLowerCase = getInitParameter("force.table.name.lowercase");
		if (sForceTableNameLowerCase != null) {
			if ("true".equals(sForceTableNameLowerCase)) {
				Config.forceTableNameLowerCase = true;
				logger.info("forceTableNameLowerCase is true");
			} else {
				logger.info("forceTableNameLowerCase is false");
			}
		} else {
			logger.info("forceTableNameLowerCase is not defined");
		}
		
		
		
		if (sForceTableNameLowerCase != null) {
			if ("true".equals(sForceTableNameLowerCase)) {
				Config.forceTableNameLowerCase = true;
				logger.info("forceTableNameLowerCase is true");
			} else {
				logger.info("forceTableNameLowerCase is false");
			}
		} else {
			logger.info("forceTableNameLowerCase is not defined");
		}
		
		

		if (_bOk) {
			logger.error("Neodatis RDB ConnectionPool OK");
		} else {
			logger.fatal("DefaultConnectionPool NOT OK");
			throw new DatabaseProblemException("DefaultConnectionPool NOT OK");
		}

	}

	/**
	 * Inits the resource to read initialization file
	 */
	void initResource() {
		String configFile = propertyFileName;
		// for default file, we need to add .properties
		if (!usedSpecificFileName) {
			if (configFile == null) {
				configFile = DEFAULT_FILE_NAME;
			}
			configFile += ".properties";
		}
		try {
			InputStream is = null;
			String propertyUrl = System.getProperty("neodatis.rdb.url.property.file");
			if (propertyUrl == null) {
				// to resolve a java 7 u45 issue that consider insecure
				// properties that does not start with jnlp in java webstart
				propertyUrl = System.getProperty("jnlp.neodatis.rdb.url.property.file");
			}
			if (propertyUrl != null) {
				logger.error("Loading property file from url " + propertyUrl);
				is = new URL(propertyUrl).openStream();
			} else {
				logger.debug("Looking for database config file : " + configFile + " in classpath ");
				URL url = DefaultConnectionPool.class.getResource(configFile);
				is = DefaultConnectionPool.class.getResourceAsStream(configFile);
				logger.error("Loading NeoDatisRDB config from " + url.toString());
			}

			if (is == null) {
				throw new Exception("File not found " + configFile);
			}

			_resource = new Properties();
			_resource.load(is);
			resourceIsOk = true;
		} catch (Exception e) {
			resourceIsOk = false;
			if (!usedSpecificFileName) {
				logger.fatal("Default Resource not found : " + configFile + ". Trying by using ip address");
				logger.fatal(" <- Classpath : " + System.getProperty("java.class.path"));
				// tries to get property file with ip
				try {
					String ip = InetAddress.getLocalHost().getHostAddress();

					configFile = propertyFileName + "_" + ip + ".properties";

					logger.info("Looking for database config file : " + configFile + "in classpath");

					InputStream is = DefaultConnectionPool.class.getResourceAsStream(configFile);

					if (is == null) {
						throw new Exception("File not found " + configFile);
					}

					_resource = new Properties();
					_resource.load(is);
					resourceIsOk = true;
				} catch (Exception e1) {
					resourceIsOk = false;
					throw new RuntimeException("Resource not found : " + configFile, e1);
				}
			} else {
				logger.fatal("Default Resource not found : " + configFile + ". UsedSpecificFile :-(");
			}
		}
	}

	/**
	 * Closes the pool => Closes all connections
	 */
	public void close() {
		ConnectionInfo ci = null;
		// Stores the number of connections
		// _nNbConnections = in_nNbConnections;

		for (int i = 0; i < getNbConnections(); i++) {
			logger.info("Closing connection" + (i + 1));

			try {
				ci = (ConnectionInfo) connections.get(i);
				ci.getConnection().close();
			} catch (SQLException e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				logger.error(sw.toString());
				logger.error("Could not close connection " + (i + 1));
			}
		}
	}

	/**
	 * Creates a spool of connection
	 *
	 * @param in_connectionPoolInfo
	 *            The data of connection
	 * @return boolean The status
	 * @throws Exception
	 */
	boolean connect(ConnectionPoolInfo in_connectionPoolInfo) throws Exception {
		try {
			boolean bOk = false;

			String sNbConnections = System.getProperty(Constants.NB_CONNECTIONS);
			int nbConnections = in_connectionPoolInfo.getNbConnections();
			if (sNbConnections != null) {
				nbConnections = Integer.parseInt(sNbConnections);
			}

			logger.error("** NeoDatis RDB : " + Version.VERSION);
			logger.error("** Connection Pool : " + in_connectionPoolInfo.getConnectionName() + " : ");
			logger.error("\t- Driver\t\t= " + in_connectionPoolInfo.getDriver());
			logger.error("\t- URL\t\t= " + in_connectionPoolInfo.getUrl());
			logger.error("\t- User\t\t= " + in_connectionPoolInfo.getUserName());
			logger.error("\t- Pool size\t= " + nbConnections);
			logger.error("\t- DatabaseType\t= " + Config.databaseType);
			logger.error("\t- Test connections\t= " + in_connectionPoolInfo.isTestConnections());

			if (in_connectionPoolInfo.isTestConnections()) {
				logger.error("\t- Test query\t= " + in_connectionPoolInfo.getTestQuery());
				logger.error("\t- Test period\t= " + in_connectionPoolInfo.getTestConnectionsEveryMs());
			}

			in_connectionPoolInfo.setNbConnections(nbConnections);
			for (int i = 0; i < in_connectionPoolInfo.getNbConnections(); i++) {
				addConnection(i + 1, in_connectionPoolInfo);
			}

			bOk = true;

			return bOk;
		} catch (Throwable e) {
			logger.error("Error while creating connection pool", e);
			throw new Exception(e);
		}
	}

	/**
	 * adds a connection to the pool
	 *
	 * @param in_connectionPoolInfo
	 *            The data of the connection
	 * @return in_connectionPoolInfo The index of the new connection
	 */
	int addConnection(int index, ConnectionPoolInfo in_connectionPoolInfo) {
		Connection connection = null;
		ConnectionInfo connectionInfo = null;
		try {
			logger.info("Adding connection " + index );

			// Creates the connection
			connection = Database.createJdbcConnection(in_connectionPoolInfo.getUserName(), in_connectionPoolInfo.getPassword(),
					in_connectionPoolInfo.getDriver(), in_connectionPoolInfo.getUrl());
			
			
			if(in_connectionPoolInfo.getAutocommit()!=null) {
				connection.setAutoCommit( in_connectionPoolInfo.getAutocommit().booleanValue());
			}
			//neutralconnection.setAutoCommit(false);
			logger.info("\t\t Connection autocommit="+ connection.getAutoCommit() + " (was set to "+ String.valueOf(in_connectionPoolInfo.getAutocommit())+")");

			// Creates the connection Info Object
			connectionInfo = new ConnectionInfo(connection, _DEFAULT_NAME, in_connectionPoolInfo.getUserName(), in_connectionPoolInfo.getPassword(),
					in_connectionPoolInfo.getDriver(), in_connectionPoolInfo.getUrl(), connections.size() + 1, in_connectionPoolInfo.getTimeOut());

			// Actually creates the connection
			connections.add(connectionInfo);

			_nNbConnections++;

			return _nNbConnections - 1;
		} catch (ClassNotFoundException e1) {
			logger.fatal("DefaultConnectionPool.connect : driver " + in_connectionPoolInfo.getDriver() + " not found");
			throw new DatabaseProblemException("DefaultConnectionPool.connect : driver " + in_connectionPoolInfo.getDriver() + " not found");
		} catch (java.sql.SQLException e2) {
			StringWriter sw = new StringWriter();
			e2.printStackTrace(new PrintWriter(sw));
			logger.fatal(sw.toString());
			throw new DatabaseProblemException("Trying to connect to " + in_connectionPoolInfo.getUrl() + e2.getMessage());
		}

	}

	/**
	 * Gets a connection from pool
	 *
	 * @return Connection The available connection from spool
	 * @throws Exception
	 */
	synchronized public Connection getConnection() throws Exception {
		return getConnection(_DEFAULT_NAME);
	}

	/**
	 * Gets a connection from pool
	 *
	 * @param in_sCaller
	 *            The name of the caller - this name is set as a attroibute of
	 *            connection in order to know , when monitoring who called this
	 *            connection - It is very usefull to check connection loss
	 * @return Connection The available connection from spool
	 * @throws Exception
	 */
	synchronized public Connection getConnection(String in_sCaller) throws Exception {
		// If pool is not ready , initialize it
		if (!ready) {
			init();
		}

		// If pool is ok
		if (_bOk) {

			long time2Wait = 500;
			int nRetry = 0;
			while (nRetry < 5) {
				// Get the number of available connections
				int nAvailableConnection = getNumberOfAvailableConnections();

				// If there connections available
				if (nAvailableConnection > -1) {
					// Stores the name of the caller
					setConnectionCallerName(nAvailableConnection, in_sCaller);

					logger.debug("<<-- Returning connection " + (nAvailableConnection + 1) + " of " + _nNbConnections + "(Free:" + getNbFreeConnections()
							+ "-Closed:" + getNbClosedConnections() + ") Name : " + in_sCaller);
					Connection c = connections.get(nAvailableConnection).getConnection();

					return c;
				}
				Thread.sleep(time2Wait);
				nRetry++;

				logger.fatal("Waiting for connection  retry " + nRetry);

			}

			// _log.fatal("**** DefaultConnectionPool panic!!!! : No more connections. All "
			// + _nNbConnections + " connections are in use!!!");
			logger.fatal(StringUtils.exceptionToString(new Exception("DefaultConnectionPool panic!!!!"), true));

			throw new DatabaseProblemException("DefaultConnectionPool panic!!!! : No more connections. All " + _nNbConnections + " connections are in use!!!");
		}

		logger.fatal("**** DefaultConnectionPool panic!!!! The pool is not ok!!!");
		throw new DatabaseProblemException("DefaultConnectionPool panic!!!! : The pool is not ok");

	}

	/**
	 * releases a connection
	 */
	synchronized public void releaseConnection(Connection in_connection) {
		// Get the index of the connection
		int nIndex = getConnectionIndex(in_connection);

		String sCallerName = _DEFAULT_NAME;

		if (nIndex > -1) {
			// Before setting to default , gets The connection caller name
			sCallerName = connections.get(nIndex).getName();

			connections.get(nIndex).release();

			logger.debug("-->> Releasing connection " + (nIndex + 1) + " (Free=" + getNbFreeConnections() + "/" + _nNbConnections + "-Closed:"
					+ getNbClosedConnections() + ") Name is " + sCallerName);
		} else {
			logger.fatal("!!!! Could not Release connection (free=" + getNbFreeConnections() + "/" + _nNbConnections + ") ");
			logger.fatal("Connection Description : " + (in_connection == null ? "Connection is null!!" : in_connection.toString()));
		}
	}

	/**
	 * Change the status of a connection to kill
	 */
	public void SetKillStatus(Connection in_connection) {
		int nIndex = getConnectionIndex(in_connection);

		if (nIndex != -1) {
			connections.get(nIndex).setKilled(true);
		}
	}

	/**
	 * Gets the index of a connection
	 *
	 * @param in_connection
	 *            The conenction to get the index
	 * @return The index of the connection
	 */
	synchronized int getConnectionIndex(Connection in_connection) {
		int i = 0;

		while (i < _nNbConnections && !connections.get(i).isSameConnection(in_connection)) {
			i++;
		}

		if (i >= _nNbConnections) {
			i = -1;
		}

		return i;
	}

	/**
	 * Get the number of available connections
	 *
	 * @return The connection index
	 */
	public synchronized int getNumberOfAvailableConnections() {
		int i = 0;

		try {
			// Checks also if connection is not closed
			while (i < _nNbConnections && !connections.get(i).isAvailable()) { /*
																				 * :
																				 * 22
																				 * /
																				 * 06
																				 * /
																				 * 2003
																				 * :
																				 * Do
																				 * not
																				 * need
																				 * this
																				 * if
																				 * (
																				 * _asConnections
																				 * .
																				 * getConnectionInfo
																				 * (
																				 * i
																				 * )
																				 * .
																				 * isClosed
																				 * (
																				 * )
																				 * )
																				 * {
																				 * _log
																				 * .
																				 * debug
																				 * (
																				 * "!!! Getting available connection : Connection "
																				 * +
																				 * i
																				 * +
																				 * " has been closed!!"
																				 * )
																				 * ;
																				 * }
																				 */
				i++;
			}
		} catch (Exception e) {
			logger.error("!!! Getting available connection : Error while checking isClosed!");
		}
		// If we enter here, then it means that there is no more available
		// connection
		if (i >= _nNbConnections) {
			// Checks if we are in dunamic mode
			if (_bDynamicPool) {
				// Adds a new connection
				i = addConnection(0, connectionPoolInfo);

				connections.get(i).reserve();
			} else {
				i = -1;
			}
		} else {
			connections.get(i).reserve();
		}

		return i;
	}

	/**
	 * Gets the number of free connections
	 *
	 * @return int The number of free connections
	 */
	public int getNbFreeConnections() {
		int i = 0;

		for (int nConnection = 0; nConnection < _nNbConnections; nConnection++) {
			if (connections.get(nConnection).isAvailable()) {
				i++;
			}
		}

		return i;

	}

	/**
	 * Gets the number of connections
	 *
	 * @return int The number of connections
	 */
	public int getNbConnections() {
		return _nNbConnections;
	}

	/**
	 * Gets the number of closed connectsions
	 *
	 * @return int The number of closed connections
	 */
	public int getNbClosedConnections() {
		int i = 0;

		for (int nConnection = 0; nConnection < _nNbConnections; nConnection++) {
			if (connections.get(nConnection).isClosed()) {
				i++;
			}
		}

		return i;

	}

	/**
	 * Gets all unavailable connectsions
	 *
	 * @return String Name of unavaible connections
	 */
	public String getConnectionsStatus() {

		StringBuffer sResult = new StringBuffer();
		ConnectionInfo ci = null;

		for (int nConnection = 0; nConnection < _nNbConnections; nConnection++) {
			ci = connections.get(nConnection);

			if (!ci.isAvailable()) {
				sResult.append("Connection " + ci.getName());
				sResult.append(" is ");
				if (ci.isClosed()) {
					sResult.append("closed");
				} else {
					if (ci.isKilled()) {
						sResult.append("killed");
					} else {
						sResult.append("in use");
					}
				}
				sResult.append(" since ").append(ci.getGetDate());
			} else {
				sResult.append("is free");
			}
			sResult.append("\n");
		}

		return sResult.toString();

	}

	/**
	 * Gets a parameter from resource
	 *
	 * @param in_sParameter
	 *            The Parameter to return
	 * @return String The value of teh parameter
	 */
	String getInitParameter(String in_sParameter) throws MissingResourceException {
		if (_resource == null) {
			return null;
		}
		return _resource.getProperty(in_sParameter);
	}

	/**
	 * Gets a parameter from resource and gives a default value
	 *
	 * @param in_sParameter
	 *            The Parameter to return
	 * @param in_sDefaultValue
	 *            The default value
	 * @return String The value of teh parameter
	 */
	String getInitParameter(String in_sParameter, String in_sDefaultValue) {
		try {
			String v = getInitParameter(in_sParameter);
			if (v == null) {
				return in_sDefaultValue;
			}
			return v;
		} catch (MissingResourceException e) {
			return in_sDefaultValue;
		}
	}

	/**
	 * To check if object is ready
	 *
	 * @return boolean
	 */
	boolean isReady() {
		return ready;
	}

	/**
	 * Sets the connection caller name
	 *
	 * @param in_nIndex
	 *            The connection index
	 * @param in_sCallerName
	 *            The name
	 */
	protected void setConnectionCallerName(int in_nIndex, String in_sCallerName) {
		connections.get(in_nIndex).setName(in_sCallerName);
	}

	/**
	 * Gets the connection caller name
	 *
	 * @param in_nIndex
	 *            The connection index
	 */
	protected String getConnectionCallerName(int in_nIndex) {
		return connections.get(in_nIndex).getName();
	}

	public ConnectionPoolInfo getConnectionPoolInfo() {
		return connectionPoolInfo;
	}

	public String getConnectionPoolInfoDesc() {
		return connectionPoolInfo.toString();
	}
	public List<ConnectionInfo> getConnections() {
		return connections;
	}

	public static void main(String in_args[]) throws Exception {
		logger.info("starting...");
		Connection c = null;
		// ConnectionSpool spool = new ConnectionSpool();
		DefaultConnectionPool pool = new DefaultConnectionPool();
		pool.init();

		for (int i = 0; i < 10; i++) {
			c = pool.getConnection("teste" + i);

			System.out.println(pool.getConnectionsStatus());

			pool.releaseConnection(c);

			System.out.println("free connection=" + pool.getNbFreeConnections());
		}

	}

	/**
	 * Rebuild a specific connection
	 *
	 * @param i
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public void rebuildConnection(ConnectionInfo ci, int i) throws SQLException, ClassNotFoundException {
		try {
			logger.error("Trying to clean dead connection with index " + (i + 1));
			Connection c = ci.getConnection();
			c.close();
			c = null;
		} catch (Exception e) {
			// TODO: handle exception
		}
		logger.error("Recreating a connection " + (i + 1) + " for the pool");
		Connection connection = Database.createJdbcConnection(connectionPoolInfo.getUserName(), connectionPoolInfo.getPassword(),
				connectionPoolInfo.getDriver(), connectionPoolInfo.getUrl());
		logger.error("Connection " + (i + 1) + " recreated. Done!");
		ci.setConnection(connection);
	}

	public String getDatabase() {
		return database;
	}

}
