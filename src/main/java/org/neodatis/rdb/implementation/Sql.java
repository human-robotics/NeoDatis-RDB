package org.neodatis.rdb.implementation;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Category;
import org.neodatis.ConnectionPoolInfo;
import org.neodatis.rdb.ConnectionPool;
import org.neodatis.rdb.DbObjectMapping;

/**
 * To Execute Sql Query : Insert,Select, Delete and Update
 * 
 * @author Olivier smadja <osmadja@netcourrier.com>
 * @version 01/01/2000 - creation
 * @version 21/08/2000 - Use of Tracer instead of System.out.println
 * @version 22/08/2000 - Checks the validity of the statement and throws a
 *          SqlStatementNotInitialized exception if _statement equals null
 * @version 24/08/2000 - Implementation of the release of connection of close
 * @version 31/08/2000 - Adds The customSelect method which enables the user to
 *          make any type of select
 * @version 14/07/2001 - Adjust to new Tracer class(with labels)
 * @version 19/07/2001 - in select and simpleExceute : throw the exception
 *          instead of returning null
 * @version 17/10/2001 - Adds the name of the caller to trace connection loss
 * @version 16/01/2002 - Adds commit method!
 * @version 21/01/2002 - Adds Trace for select and SimpleExecute
 */

public class Sql {
	/** Creates the root */
	static Category logger = Category.getInstance(Sql.class.getName());
	protected RdbReflection rdbReflection;
	public static final String _DEFAULT_NAME = "no name";
	String _sObjectCallerName;

	/** The associated connection */
	Connection connection;
	DefaultConnectionPool pool;

	int defaultFetchSize = 1000;

	// To check if the object is ok
	boolean _bOk = false;

	Statement statement = null;

	ResultSet _resultSet;

	/**
	 * Constructor
	 * 
	 * @param in_sObjectCallerName name of the object that called it
	 * @throws Exception
	 */
	public Sql(String in_sObjectCallerName) throws Exception {
		_sObjectCallerName = in_sObjectCallerName;
		init((String) null);
	}

	public Sql(String in_sObjectCallerName, String propertyFileName) throws Exception {
		_sObjectCallerName = in_sObjectCallerName;
		init(propertyFileName);
	}

	public Sql(String caller, ConnectionPoolInfo cpi) {
		_sObjectCallerName = caller;
		try {
			init(cpi);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void init(ConnectionPoolInfo cpi) throws Exception {
		pool = DefaultConnectionPool.getInstance(cpi);
		rdbReflection = new RdbReflection();
		Connection connection = pool.getConnection();
		connection.setAutoCommit(false);
		init(connection);

	}

	public static Sql getInstance(String in_sObjectCallerName) throws Exception {
		Sql sql = new Sql(in_sObjectCallerName);
		// sql.init();
		return sql;
	}

	/**
	 * Initialization of the object
	 * 
	 * @throws Exception
	 */
	public void init(String propertyFileName) throws Exception {
		pool = DefaultConnectionPool.getInstance(propertyFileName);
		rdbReflection = new RdbReflection();
		Connection connection = pool.getConnection();
		connection.setAutoCommit(false);
		init(connection);
	}

	/** Initialization of the object */
	public void init(Connection in_connection) {
		connection = in_connection;
		if (connection == null) {
			_bOk = false;

			logger.error("Sql.init did not manage a connection");

			// Prints connection pool state
			logger.error(pool.getConnectionsStatus());

			throw new DatabaseProblemException("Did not manage to get a connection");

		}

		try {
			// Ask the Statement Object
			statement = connection.createStatement();
			_resultSet = null;
			_bOk = true;

		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			logger.error(sw.toString());

			_bOk = false;
			throw new DatabaseProblemException(e.getMessage());
		}

	}

	public void close() {
		try {
			if (_resultSet != null) {
				_resultSet.close();
				_resultSet = null;
			}

			if (statement != null) {
				statement.close();
				statement = null;
			}

			if (connection != null) {
				pool.releaseConnection(connection);
			}
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			logger.error(sw.toString());
		}
	}

	public void closeRset() {
		try {
			if (_resultSet != null) {
				_resultSet.close();
				_resultSet = null;
			}
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			logger.error(sw.toString());
		}

	}

	public boolean insert(String in_sQuery) throws SQLException {

		// The insert must return 1
		return (simpleExecute(in_sQuery) == 1);
	}

	public boolean insertPreparedStatement(String query, DbObjectMapping o, boolean isUpdate)
			throws Exception, IllegalAccessException {
		boolean log = logger.isInfoEnabled();
		StringBuilder builder = null;
		log = true;
		if (log) {
			logger.info("Executing SQL : " + query);
			builder = new StringBuilder();
		}
		PreparedStatement pstatement = null;

		log = true;

		try {
			pstatement = connection.prepareStatement(query);
			String nextIdSelect = DbSpecific.get().getNextIdSelect("", "");
			String primaryKey = RdbReflection._DATABASE_FIELD_PREFIX + o.getPrimaryKey().getName();

			Field[] fields = rdbReflection.getFieldsOf(o.getClass());
			boolean retrieveIdAfterInsert = false;
			int nField = 1;
			for (int i = 0; i < fields.length; i++) {
				// System.out.println("i=" + i + " | nField=" + nField +
				// " | field = " + fields[i].getName());
				// for native objects, ignore primary key field
				if (fields[i].getName().equals(primaryKey)) {
					boolean isNative = nextIdSelect.equalsIgnoreCase("native");
					boolean fieldIsNull = rdbReflection.fieldIsNull(fields[i], o);
					if (isNative) {
						if (fieldIsNull) {
							retrieveIdAfterInsert = true;
						}
						continue;
					}
				}
				Object v = rdbReflection.getFieldValue(fields[i], o);
				if (v instanceof Blob) {
					Blob b = (Blob) v;
					long l = b.length();
					byte[] bytes = b.getBytes((long) 1, (int) l);
					if (log) {
						builder.append(fields[i].getName()).append("=").append(bytes).append(" , ");
					}
					pstatement.setBytes(nField, bytes);

				} else if (v instanceof byte[]) {
					byte[] bytes = (byte[]) v;
					if (log) {
						builder.append(fields[i].getName()).append("=").append(bytes).append(" , ");
					}

					pstatement.setBytes(nField, bytes);

				} else if (v instanceof Date) {
					if (log) {
						builder.append(fields[i].getName()).append("=").append(v).append(" , ");
					}

					pstatement.setTimestamp(nField, new Timestamp(((Date) v).getTime()));
				} else if (v instanceof BigDecimal) {
					BigDecimal bd = (BigDecimal) v;
					if (log) {
						builder.append(fields[i].getName()).append(" BigDecimal with scale ").append(bd.scale())
								.append(" = ").append(bd.toEngineeringString()).append(" , ");
					}

					pstatement.setBigDecimal(nField, bd);

				} else {
					if (log) {
						builder.append(fields[i].getName()).append("=").append(v).append(" , ");
					}

					pstatement.setObject(nField, v);
				}

				nField++;
			}

			if (log) {
				logger.info("\tPrepareStatement values =>" + builder.toString());
			}
			int nResult = pstatement.executeUpdate();

			if (nextIdSelect.equals("native") && retrieveIdAfterInsert) {
				String selectId = DbSpecific.get().getLastIdSelect(Util.getTableName(o));
				Statement statement = connection.createStatement();
				ResultSet rset = statement.executeQuery(selectId);
				rset.next();
				Object id = rset.getObject(1);
				o.getPrimaryKey().setValue(o, new Long(id.toString()));
				statement.close();
			}

			return nResult == 1;
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			logger.error(sw.toString());

			logger.error("Error while executing " + query + "\nObject is " + o.toString());
			throw e;
		} finally {
			pstatement.close();
		}
	}

	public int delete(String in_sQuery) throws SQLException {
		return simpleExecute(in_sQuery);
	}

	public int update(String in_sQuery) throws SQLException {
		return simpleExecute(in_sQuery);
	}

	public ResultSet select(String in_sQuery) throws SQLException {
		if (logger.isInfoEnabled()) {
			logger.info("EXECUTING SELECT : " + in_sQuery);
		}
		try {
			getStatement().setFetchSize(defaultFetchSize);
			// Attempt to execute the update
			_resultSet = getStatement().executeQuery(in_sQuery);
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			logger.error(sw.toString());

			logger.error("Error while executing " + in_sQuery);
			throw e;
		} catch (SqlStatementNotInitializedException e2) {
			StringWriter sw = new StringWriter();
			e2.printStackTrace(new PrintWriter(sw));
			logger.error(sw.toString());

			logger.error("Error while executing " + in_sQuery);
			throw new SQLException(e2.getMessage());
		}
		return _resultSet;
	}

	/**
	 * Selects objects using the user custom clause Select,Where,GroupBy,OrderBy
	 * 
	 * @param in_sSelect  The select clause.
	 * @param in_sTable   The name of the table
	 * @param in_sWhere   Where Clause
	 * @param in_sGroupBy The group by clause
	 * @param in_sOrderBy The order by clause
	 * @return ResultSet The result set selected
	 * @example : select sum(age) from test where name like 'jj%' group by age order
	 *          by age would call object.customSelect( "sum(age) from test " , "name
	 *          like 'jj%'" , "age" , "age" );
	 **/
	public ResultSet customSelect(String in_sSelect, String in_sTable, String in_sWhere, String in_sGroupBy,
			String in_sOrderBy) throws SQLException

	{

		StringBuffer sQuery = new StringBuffer();

		sQuery.append("SELECT ");
		sQuery.append(in_sSelect);

		sQuery.append(" FROM ");
		sQuery.append(in_sTable);

		if (in_sWhere != null) {
			sQuery.append(" WHERE ");
			sQuery.append(in_sWhere);
		}

		if (in_sGroupBy != null) {
			sQuery.append(" GROUP BY ");
			sQuery.append(in_sGroupBy);
		}

		if (in_sOrderBy != null) {
			sQuery.append(" ORDER BY ");
			sQuery.append(in_sOrderBy);
		}

		return select(sQuery.toString());
	}

	int simpleExecute(String in_sQuery) throws SQLException {
		System.out.println("Executing sql : " + in_sQuery);
		if (logger.isDebugEnabled()) {
			logger.debug("Executing SQL : " + in_sQuery);
		}
		try {
			// Attempt to execute the update
			int nResult = getStatement().executeUpdate(in_sQuery);
			System.out.println("Result is " + nResult);
			return nResult;
		} catch (SQLException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			logger.error(sw.toString());

			logger.error("Error while executing " + in_sQuery);
			throw e;
		} catch (SqlStatementNotInitializedException e2) {
			StringWriter sw = new StringWriter();
			e2.printStackTrace(new PrintWriter(sw));
			logger.error(sw.toString());

			logger.error("Error while executing " + in_sQuery);
			throw new SQLException(e2.getMessage());
		}
	}

	/**
	 * To Know if object is ok
	 * 
	 * @return boolean The Ok status
	 */
	public boolean isOk() {
		return _bOk;
	}

	/**
	 * Gets the statement
	 * 
	 * @return Statement The database statement
	 */
	public Statement getStatement() throws SqlStatementNotInitializedException {
		if (statement == null) {
			StringBuffer sMessage = new StringBuffer();
			sMessage.append("** Database Exception : The SqlStatement of this object is null!\n");
			sMessage.append("* Checks if database connection is ok\n");
			sMessage.append("* Checks if clearSql method was not called before this call\n");
			throw new SqlStatementNotInitializedException(sMessage.toString());
		}

		return statement;
	}

	/** to commit what has been done */
	public void commit() throws SQLException {
		connection.commit();
	}

	/** to rollback what has been done */
	public void rollback() throws SQLException {
		connection.rollback();
	}

	public static void main(String in_args[]) throws Exception {

		ResultSet rset = null;
		Sql sql = null;

		if (in_args.length < 1) {
			System.out.println("Pass a query to be executed");
			System.exit(0);
		}

		System.out.println("Executing select " + in_args[0]);

		sql = new Sql("default");
		rset = sql.select(in_args[0]);

		// Loop through the result data.
		while (rset.next()) {
			for (int i = 0; i < rset.getMetaData().getColumnCount(); i++) {
				System.out.print(".");
				System.out.print(rset.getString(i + 1) + " ");
			}
			System.out.println();
		}

		sql.close();

	}

	public void ddl(String ddl) throws SQLException {
		connection.prepareStatement(ddl).executeUpdate();
	}

	public ConnectionPool getConnecitonPool() {
		return pool;
	}

	public int getDefaultFetchSize() {
		return defaultFetchSize;
	}

	public void setDefaultFetchSize(int defaultFetchSize) {
		this.defaultFetchSize = defaultFetchSize;
	}

}