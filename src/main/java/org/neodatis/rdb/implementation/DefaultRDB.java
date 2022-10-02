/* A default implemenation of the database service
@version 09/07/2002 - Olivier : Creation

 */
package org.neodatis.rdb.implementation;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Category;
import org.neodatis.ConnectionPoolInfo;
import org.neodatis.rdb.APIInternalException;
import org.neodatis.rdb.CustomQuery;
import org.neodatis.rdb.DbObjectMapping;
import org.neodatis.rdb.DbObjectWithLongId;
import org.neodatis.rdb.DeleteQuery;
import org.neodatis.rdb.InsertQuery;
import org.neodatis.rdb.Query;
import org.neodatis.rdb.QueryResult;
import org.neodatis.rdb.RDB;
import org.neodatis.rdb.SelectQuery;
import org.neodatis.rdb.UpdateQuery;
import org.neodatis.rdb.query.CustomDeleteQuery;
import org.neodatis.rdb.query.CustomSelectQuery;
import org.neodatis.rdb.query.CustomSelectQueryResult;
import org.neodatis.rdb.query.DefaultDeleteQuery;
import org.neodatis.rdb.query.DefaultInsertQuery;
import org.neodatis.rdb.query.DefaultQueryResult;
import org.neodatis.rdb.query.DefaultSelectQuery;
import org.neodatis.rdb.query.DefaultUpdateQuery;
import org.neodatis.tools.StringUtils;

/**
 * A Default database service implementation using Reflection
 * 
 * @author Olivier Smadja - osmadja@netcourrier.com
 * @see br.com.RDB.database.DatabaseService
 * @see br.com.jconcept.database.implementation.SqlQueryBuilder
 */
public class DefaultRDB implements RDB {

	SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	public static final String NO_ALIAS_USE_NATURAL_FIELD = "__neodatis_no_alias_use_natural_field__";

	/** Creates the root */
	static Category logger = Category.getInstance(DefaultRDB.class.getName());

	protected DefaultSqlQueryBuilder defaultSqlQueryBuilder;
	protected RdbReflection rdbReflection;

	protected Sql sql;

	/**
	 * Constructor
	 * 
	 * @throws Exception
	 * 
	 */
	public DefaultRDB() throws Exception {
		sql = new Sql("default");
		defaultSqlQueryBuilder = new DefaultSqlQueryBuilder();
		rdbReflection = new RdbReflection();
		DATE_FORMAT.setLenient(true);

	}

	public DefaultRDB(String propertyFileName) throws Exception {
		sql = new Sql("default", propertyFileName);
		defaultSqlQueryBuilder = new DefaultSqlQueryBuilder();
		rdbReflection = new RdbReflection();

	}

	public DefaultRDB(ConnectionPoolInfo cpi) {
		sql = new Sql("default", cpi);
		defaultSqlQueryBuilder = new DefaultSqlQueryBuilder();
		rdbReflection = new RdbReflection();
	}

	public void insert(DbObjectMapping o) throws Exception {
		executeUpdate(new DefaultInsertQuery(o));
	}

	public void update(DbObjectMapping o) throws Exception {
		executeUpdate(new DefaultUpdateQuery(o));
	}

	public void save(DbObjectWithLongId o) throws Exception {

		if (o.getId() != null) {
			update(o);
		} else {
			insert(o);
		}
	}

	/**
	 * Executes the query - INSERT , UPDATE AND DELETE
	 * 
	 * @param The query
	 * @return The number of afected rows
	 * @exception SQLException If an sql exception occurs
	 */
	protected int executeUpdate(Query query) throws Exception {
		Class queryType = query.getClass();

		if (queryType == DefaultInsertQuery.class) {
			return (insert((DefaultInsertQuery) query) ? 1 : 0);
		}

		if (queryType == DefaultUpdateQuery.class) {
			boolean ok = update((DefaultUpdateQuery) query);
			return ok ? 1 : 0;
		}

		if (queryType == DefaultDeleteQuery.class) {
			return delete((DefaultDeleteQuery) query);
		}

		throw new IllegalArgumentException("Invalid query type for method executeUpdate");

	}

	/**
	 * Executes the query returning a list of result - SELECT
	 * 
	 * @param The query
	 * @return The query result
	 * @exception SQLException If an sql exception occurs
	 */
	public QueryResult executeQuery(Query query) throws SQLException, APIInternalException {
		Class queryType = query.getClass();

		if (queryType == DefaultSelectQuery.class) {
			return select((SelectQuery) query);
		}

		if (queryType == CustomSelectQuery.class) {
			CustomSelectQuery csq = (CustomSelectQuery) query;
			if (csq.getObjectClass() == null) {
				return select(csq);
			} else {
				return selectInClass(csq);
			}
		}

		throw new IllegalArgumentException("Invalid query type for method executeQuery");
	}

	/**
	 * Inserts data of the object
	 * 
	 * @param insertQuery Object describing insert
	 * @exception SQLException         If an sql exception occurs
	 * @exception APIInternalException For internal problems
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	protected boolean insert(InsertQuery insertQuery) throws Exception {
		String q = null;

		try {
			if (DbSpecific.get().usePreparedStatement()) {
				q = buildInsertQueryPreparedStatement(insertQuery);
				return sql.insertPreparedStatement(q, insertQuery.getObject(), false);
			} else {
				q = buildInsertQuery(insertQuery);
				return sql.insert(q);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}

	/**
	 * Inserts data of the object
	 * 
	 * @param insertQuery Object describing insert
	 * @exception SQLException         If an sql exception occurs
	 * @exception APIInternalException For internal problems
	 */
	protected boolean insert2(InsertQuery insertQuery) throws SQLException, APIInternalException {
		String sInsertQuery = null;

		try {
			sInsertQuery = buildInsertQueryPreparedStatement(insertQuery);

			return sql.insert(sInsertQuery);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
		}
	}

	/**
	 * Updates data of the object
	 * 
	 * @param updateQuery Object describing update
	 * @return The number of updated objects
	 * @exception Occurs a problem while building the query
	 */
	protected boolean update(UpdateQuery updateQuery) throws SQLException, APIInternalException {
		String q = null;

		try {
			q = buildUpdateQueryPreparedStatement(updateQuery);

			return sql.insertPreparedStatement(q, updateQuery.getObject(), true);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			throw new APIInternalException(e);
		} finally {
		}

	}

	public int delete(DbObjectMapping o) throws SQLException, APIInternalException {
		return delete(new DefaultDeleteQuery(o));
	}

	/**
	 * Deletes data of the object
	 * 
	 * @param deleteQuery Object describing update
	 * @return The number of deleted objects
	 * @exception SQLException If an sql exception occurs
	 */
	public int delete(DeleteQuery deleteQuery) throws SQLException, APIInternalException {
		String sDeleteQuery = null;

		try {
			sDeleteQuery = buildDeleteQuery(deleteQuery);

			return sql.delete(sDeleteQuery);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new APIInternalException(e);
		} finally {
		}

	}

	/**
	 * Deletes data of the object
	 *
	 * @param deleteQuery Object describing update
	 * @return The number of deleted objects
	 * @exception SQLException If an sql exception occurs
	 */
	public int delete(CustomDeleteQuery deleteQuery) throws SQLException, APIInternalException {
		String sDeleteQuery = null;

		try {
			sDeleteQuery = deleteQuery.getSql();

			return sql.delete(sDeleteQuery);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new APIInternalException(e);
		} finally {
		}

	}

	/**
	 * Selects data according to the DefaultSelectQuery object
	 * 
	 * @param selectQuery The Query object
	 * @return A Query result object
	 * @see DefaultSelectQuery
	 * @exception SQLException If an sql exception occurs
	 */
	public QueryResult select(SelectQuery selectQuery) throws SQLException, APIInternalException {
		// The return list
		List list = new ArrayList();

		// To check if it is a join query - if then is more than one select
		boolean bIsJoin = selectQuery.getObjectTypes().length != 1;

		// To execute sql commands
		// To get data
		ResultSet rset = null;
		// The store the SQL Query
		String sSelectQuery = null;

		try {

			sSelectQuery = buildSelectQuery(selectQuery);

			rset = sql.select(sSelectQuery);

			Class[] classNames = selectQuery.getObjectTypes();
			String[] aliases = selectQuery.getAliases();

			while (rset.next()) {
				if (bIsJoin) {
					list.add(fromDatabaseToObjects(rset, classNames, aliases));
				} else {
					if (selectQuery.objectCallback() != null) {
						// when using callbacks, objects are not added to the
						// list
						selectQuery.objectCallback().object(fromDatabaseToObject(rset, classNames[0], aliases[0]));
					} else {
						list.add(fromDatabaseToObject(rset, classNames[0], aliases[0]));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();

			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new APIInternalException(e);
		} finally {
		}
		return new DefaultQueryResult((bIsJoin ? QueryResult.JOINED_SELECT : QueryResult.SINGLE_SELECT), list);
	}

	/**
	 * Selects data according to the CustomSelectQuery object
	 * 
	 * @param selectQuery The Query object
	 * @return A Query result object
	 * @see CustomSelectQuery
	 * @exception SQLException If an sql exception occurs
	 */
	public QueryResult select(CustomSelectQuery selectQuery) throws SQLException, APIInternalException {
		// The return list
		List list = null;

		// To get data
		ResultSet rset = null;

		// To get metadata
		ResultSetMetaData metadata = null;

		// The store the SQL Query
		String sSelectQuery = null;

		// To get type list form metadata
		Class[] typeList = null;

		// To keep columnNames
		String[] columnNames = null;

		// To contain the data of each line
		Map map = null;

		// The query result
		QueryResult queryResult = null;

		try {

			// Creates the list
			list = new ArrayList();

			// Gets the select
			sSelectQuery = selectQuery.getSql();

			// Executes the query
			rset = sql.select(sSelectQuery);

			// Gets the Metadata
			metadata = rset.getMetaData();

			// Gets the column types
			typeList = getTypeListFromMetadata(metadata);

			// Gets the column names
			columnNames = getColumnAliasNamesFromMetadata(metadata);

			if (selectQuery.objectCallback() != null) {
				selectQuery.objectCallback().setColumnNames(columnNames);
			}

			while (rset.next()) {
				map = new HashMap();
				for (int nColumn = 0; nColumn < columnNames.length; nColumn++) {
					int type = rset.getType();
					Object o = null;
					o = rset.getObject(nColumn + 1);

					map.put(columnNames[nColumn], o);
				}

				if (selectQuery.objectCallback() != null) {
					// when using callbacks, objects are not added to the
					// list
					selectQuery.objectCallback().object(map);
				} else {
					list.add(map);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new APIInternalException(e);
		} finally {
		}
		return new CustomSelectQueryResult(list, typeList, columnNames);
	}

	/**
	 * Selects data according to the CustomSelectQuery object
	 * 
	 * @param selectQuery The Query object
	 * @return A Query result object
	 * @see CustomSelectQuery
	 * @exception SQLException If an sql exception occurs
	 */
	public QueryResult selectInClass(CustomSelectQuery selectQuery) throws SQLException, APIInternalException {
		// The return list
		List list = null;

		// To get data
		ResultSet rset = null;

		// To get metadata
		ResultSetMetaData metadata = null;

		// The store the SQL Query
		String sSelectQuery = null;

		// To get type list form metadata
		Class[] typeList = null;

		// To keep columnNames
		String[] columnNames = null;

		// To contain the data of each line
		Map map = null;

		// The query result
		QueryResult queryResult = null;

		try {

			// Creates the list
			list = new ArrayList();

			// Gets the select
			sSelectQuery = selectQuery.getSql();

			// Executes the query
			rset = sql.select(sSelectQuery);

			// Gets the Metadata
			metadata = rset.getMetaData();

			// Gets the column types
			typeList = getTypeListFromMetadata(metadata);

			// Gets the column names
			columnNames = getColumnNamesFromMetadata(metadata);

			while (rset.next()) {
				list.add(fromDatabaseToObject(rset, selectQuery.getObjectClass(), NO_ALIAS_USE_NATURAL_FIELD));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new APIInternalException(e);
		} finally {
		}
		return new CustomSelectQueryResult(list, typeList, columnNames);
	}

	/**
	 * Build the column name list from metadata
	 * 
	 * @param metaData The ResultSetMetada
	 * @return String [] The array of column names
	 * @exception SQLException If metadata access method throw exception
	 */
	protected String[] getColumnNamesFromMetadata(ResultSetMetaData metaData) throws SQLException {
		String[] nameList = new String[metaData.getColumnCount()];

		for (int nColumn = 0; nColumn < metaData.getColumnCount(); nColumn++) {
			nameList[nColumn] = metaData.getColumnName(nColumn + 1);
		}

		return nameList;
	}

	/**
	 * Build the column name list from metadata using alias
	 * 
	 * @param metaData The ResultSetMetada
	 * @return String [] The array of column names
	 * @exception SQLException If metadata access method throw exception
	 */
	protected String[] getColumnAliasNamesFromMetadata(ResultSetMetaData metaData) throws SQLException {
		String[] nameList = new String[metaData.getColumnCount()];

		for (int nColumn = 0; nColumn < metaData.getColumnCount(); nColumn++) {
			nameList[nColumn] = metaData.getColumnLabel(nColumn + 1);
		}

		return nameList;
	}

	/**
	 * Build a type list (list of class) from the metadata
	 * 
	 * @param metaData The ResultSetMetada
	 * @return Class [] The array of types
	 * @exception SQLException If metadata access method throw exception
	 */
	protected Class[] getTypeListFromMetadata(ResultSetMetaData metaData) throws SQLException {
		Class[] typeList = new Class[metaData.getColumnCount()];
		int nType = 0;

		for (int nColumn = 0; nColumn < metaData.getColumnCount(); nColumn++) {
			nType = metaData.getColumnType(nColumn + 1);
			typeList[nColumn] = getClassFromJDBCType(nType);
		}

		return typeList;
	}

	/**
	 * 
	 * Gets a class from a jdbc standard type
	 * 
	 */
	protected Class getClassFromJDBCType(int type) {
		switch (type) {
		case Types.CHAR:
		case Types.LONGVARCHAR:
		case Types.VARCHAR:
			return String.class;

		case Types.DATE:
			return Date.class;

		case Types.DOUBLE:
		case Types.DECIMAL:
		case Types.FLOAT:
		case Types.REAL:
		case Types.NUMERIC:
			return BigDecimal.class;

		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.TINYINT:
			return Long.class;
		}

		return String.class;
	}

	/**
	 * Checks if objects exists
	 * 
	 * @param selectQuery The Query object
	 * @return boolean true if exist
	 * @see DefaultSelectQuery
	 * @exception SQLException If an sql exception occurs
	 */
	public boolean exist(SelectQuery selectQuery) throws SQLException, APIInternalException {
		return select(selectQuery).getNumberOfObjects() > 0;
	}

	/**
	 * Counts the number of elememts
	 * 
	 * @param selectQuery The Query object
	 * @return boolean true if exist
	 * @see DefaultSelectQuery
	 * @exception SQLException If an sql exception occurs
	 */
	public long count(SelectQuery selectQuery) throws SQLException, APIInternalException {
		return select(selectQuery).getNumberOfObjects();
	}

	// ///////////

	/**
	 * Inserts data of the object
	 * 
	 * @param insertQuery Object describing insert
	 * @exception SQLException         If an sql exception occurs
	 * @exception APIInternalException For internal problems
	 */
	protected String buildInsertQuery(InsertQuery insertQuery) throws SQLException, APIInternalException {
		try {
			return defaultSqlQueryBuilder.buildInsert(insertQuery, this);
		} catch (Exception e) {
			throw new APIInternalException(e);
		}
	}

	/**
	 * Inserts data of the object
	 * 
	 * @param insertQuery Object describing insert
	 * @exception SQLException         If an sql exception occurs
	 * @exception APIInternalException For internal problems
	 */
	protected String buildInsertQueryPreparedStatement(InsertQuery insertQuery)
			throws SQLException, APIInternalException {
		try {
			return defaultSqlQueryBuilder.buildInsertPreparedStatement(insertQuery, this);
		} catch (Exception e) {
			throw new APIInternalException(e);
		}
	}

	/**
	 * @deprecated Use the one with prepared statement Builds the sql string to
	 *             update the object
	 * 
	 * @param updateQuery Object describing update
	 * @exception Exception If an sql exception occurs or
	 */
	protected String buildUpdateQuery(UpdateQuery updateQuery) throws Exception {
		String sUpdate = null;

		sUpdate = defaultSqlQueryBuilder.buildUpdate(updateQuery);

		return sUpdate;

	}

	/**
	 * Builds the sql string to update the object
	 * 
	 * @param updateQuery Object describing update
	 * @exception Exception If an sql exception occurs or
	 */
	protected String buildUpdateQueryPreparedStatement(UpdateQuery updateQuery) throws Exception {
		return defaultSqlQueryBuilder.buildUpdatePrepraredStatement(updateQuery);
	}

	/**
	 * Builds the sql string to delete the object The query object must contain (The
	 * object) OR (The class and the where)
	 * 
	 * @param deleteQuery Object describing delete
	 * @exception SQLException           If an sql exception occurs
	 * @exception InstantiationException If occured problem while creating an
	 *                                   instance of the object
	 * @exception IllegalAccessException If deleteQuery was not well initialized
	 */
	protected String buildDeleteQuery(DeleteQuery deleteQuery)
			throws SQLException, InstantiationException, IllegalAccessException, Exception {
		String sDelete = null;
		sDelete = defaultSqlQueryBuilder.buildDelete(deleteQuery);
		return sDelete;
	}

	/**
	 * Selects data of the object
	 * 
	 * @param selectQuery The Query object
	 * @return List The list obj found object. If no object is found, returns an
	 *         empty list
	 * @exception SQLException If an sql exception occurs
	 */
	protected String buildSelectQuery(SelectQuery selectQuery)
			throws SQLException, IllegalAccessException, InstantiationException {
		return defaultSqlQueryBuilder.buildSelect(selectQuery);
	}

	/**
	 * Fetches the next object in current ResultSet
	 * 
	 * @return boolean true if it's not the end
	 */
	protected Object fromDatabaseToObject(ResultSet rset, Class clazz, String alias)
			throws SQLException, IllegalAccessException, InstantiationException {
		String sTemp = null;

		Field field = null;
		Object object = clazz.newInstance();
		ResultSetMetaData metadata = rset.getMetaData();
		Field[] fields = rdbReflection.getFieldsOf(clazz);

		// Parses all fields.
		for (int nField = 0; nField < fields.length; nField++) {
			field = fields[nField];

			if (NO_ALIAS_USE_NATURAL_FIELD.equals(alias)) {
				object = manageOneFieldUsingNaturalFieldName(rset, object, field, alias, nField);
			} else {
				object = manageOneField(rset, object, field, alias, nField);

			}
		}

		return object;
	}

	/**
	 * Puts data of result set in objects. This method can achieve joined select
	 * object retrieving 1) Creates an instance of each class 2) Gets the fields of
	 * each class 3) For each class, use the resultset to populate its instanciated
	 * object *
	 * 
	 * @param The resultset
	 * @param The array of class : objects types we must return
	 * @return A Map of objects. For each class the method received in the array, it
	 *         will put a populated object in the map. The key is the class
	 * @exception SQLException           If fails while retrieving data from result
	 *                                   set
	 * @exception IllegalAccessException If fails to access an instantiated object
	 * @exception InstantiationException If fails to instanciate a class
	 * @return boolean true if it's not the end
	 */
	protected Map fromDatabaseToObjects(ResultSet rset, Class[] classes, String[] aliases)
			throws SQLException, IllegalAccessException, InstantiationException {
		Field field = null;

		// Create the array of object
		Map allObjects = new HashMap();

		// To manage a single object
		Object object = null;
		// To store fields of each class
		Field[] fields = null;

		// To keep track of the current field
		int nCurrentField = 0;

		// For each class
		for (int nClass = 0; nClass < classes.length; nClass++) {
			if (logger.isDebugEnabled()) {
				logger.debug("Populating object of type " + classes[nClass].getName());
			}

			// Creates an instance of the object
			object = classes[nClass].newInstance();

			if (logger.isDebugEnabled()) {
				logger.debug("Putting object of type " + object.getClass().getName() + " in hash map");
			}

			// Stores the object in the object list
			allObjects.put(classes[nClass], object);

			// Get fields of this class
			fields = rdbReflection.getFieldsOf(classes[nClass]);

			if (logger.isDebugEnabled()) {
				logger.debug("The class " + classes[nClass].getName() + " has " + fields.length + " database fields");
			}
			// Parses all fields.
			for (int nField = 0; nField < fields.length; nField++) {
				field = fields[nField];
				// For each field , sets the value
				object = manageOneField(rset, object, field, aliases[nClass], nField);
			}

		}
		return allObjects;
	}

	protected Object manageOneField(ResultSet rset, Object object, Field field, String alias, int fieldIndex)
			throws SQLException, IllegalAccessException {
		Object fieldObject = null;
		// Prevent from IllegalAccessException
		field.setAccessible(true);

		// field name is field name + _ + alias (alias is put at the end as it
		// is a number)
		String sFieldName = new StringBuffer("f_").append(fieldIndex).append("_").append(alias).toString();

		if (field.getType() == String.class) {
			fieldObject = rset.getString(sFieldName);
		}
		if (field.getType() == BigDecimal.class) {
			fieldObject = rset.getBigDecimal(sFieldName);
		}
		if (field.getType() == Boolean.class || field.getType() == Boolean.TYPE) {
			fieldObject = rset.getBoolean(sFieldName);
		}
		if (field.getType() == Long.class) {
			fieldObject = rset.getObject(sFieldName);
			if (fieldObject != null) {
				fieldObject = new Long(rset.getLong(sFieldName));
			}
		}
		if (field.getType() == Integer.class) {
			fieldObject = rset.getObject(sFieldName);
			if (fieldObject != null) {
				fieldObject = new Integer(rset.getInt(sFieldName));
			}
		}
		if (field.getType() == Short.class) {
			fieldObject = rset.getObject(sFieldName);
			if (fieldObject != null) {
				fieldObject = new Short(rset.getShort(sFieldName));
			}
		}
		if (field.getType() == Double.class) {
			fieldObject = rset.getObject(sFieldName);
			if (fieldObject != null) {
				fieldObject = new Double(rset.getDouble(sFieldName));
			}
		}
		if (field.getType() == Float.class) {
			fieldObject = rset.getObject(sFieldName);
			if (fieldObject != null) {
				fieldObject = new Float(rset.getFloat(sFieldName));
			}
		}
		if (field.getType() == Date.class) {
			// fieldObject = in_rset.getDate(in_nColumnIndex+1) ;
			try {
				fieldObject = rset.getTimestamp(sFieldName);
			} catch (SQLException e1) {
				logger.error("Could not obtain Timestamp - getting a Date object");
				try {
					fieldObject = rset.getDate(sFieldName);
				} catch (Exception e2) {
					fieldObject = rset.getString(sFieldName);
					try {
						// try as a long (value in milliseconds)
						Date date = new Date(Long.parseLong(String.valueOf(fieldObject)));
						fieldObject = date;
					} catch (Exception e) {
						// now try as string 
						try {
							Date date = DATE_FORMAT.parse(String.valueOf(fieldObject));
							fieldObject = date;
						} catch (ParseException e3) {
							fieldObject = new Date();
						}
					}
				}

			}
		}
		if (Blob.class.isAssignableFrom(field.getType()))

		{
			fieldObject = rset.getBytes(sFieldName);
			byte[] bb = (byte[]) fieldObject;
		}
		if (field.getType() == byte[].class) {
			fieldObject = rset.getBytes(sFieldName);
		}
		try {
			if (fieldObject != null) {
				field.set(object, fieldObject);
			}
		} catch (Exception e) {
			logger.error(
					"Error setting value of field " + field.getName() + "(" + field.getType().getName() + ") : value="
							+ String.valueOf(fieldObject) + " | Value type=" + fieldObject.getClass().getSimpleName());
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Field Type is " + field.getType().getName() + " / Field name = " + sFieldName + "("
					+ field.getName() + ") Value is : " + (fieldObject == null ? "null" : fieldObject.toString()));
		}
		return object;
	}

	protected Object manageOneFieldUsingNaturalFieldName(ResultSet rset, Object object, Field field, String alias,
			int fieldIndex) throws SQLException, IllegalAccessException {
		Object fieldObject = null;
		// Prevent from IllegalAccessException
		field.setAccessible(true);

		// field name is field name + _ + alias (alias is put at the end as it
		// is a number)
		String sFieldName = toDbName(field.getName());

		if (field.getType() == String.class) {
			fieldObject = rset.getString(sFieldName);
		}
		if (field.getType() == BigDecimal.class) {
			fieldObject = rset.getBigDecimal(sFieldName);
		}
		if (field.getType() == Boolean.class || field.getType() == Boolean.TYPE) {
			fieldObject = rset.getBoolean(sFieldName);
		}
		if (field.getType() == Long.class) {
			fieldObject = rset.getObject(sFieldName);
			if (fieldObject != null) {
				fieldObject = new Long(rset.getLong(sFieldName));
			}
		}
		if (field.getType() == Integer.class) {
			fieldObject = rset.getObject(sFieldName);
			if (fieldObject != null) {
				fieldObject = new Integer(rset.getInt(sFieldName));
			}
		}
		if (field.getType() == Short.class) {
			fieldObject = rset.getObject(sFieldName);
			if (fieldObject != null) {
				fieldObject = new Short(rset.getShort(sFieldName));
			}
		}
		if (field.getType() == Double.class) {
			fieldObject = rset.getObject(sFieldName);
			if (fieldObject != null) {
				fieldObject = new Double(rset.getDouble(sFieldName));
			}
		}
		if (field.getType() == Float.class) {
			fieldObject = rset.getObject(sFieldName);
			if (fieldObject != null) {
				fieldObject = new Float(rset.getFloat(sFieldName));
			}
		}
		if (field.getType() == Date.class) {
			// fieldObject = in_rset.getDate(in_nColumnIndex+1) ;
			try {
				fieldObject = rset.getTimestamp(sFieldName);
			} catch (SQLException e) {
				logger.error("Could not obtain Timestamp - getting a Date object");
				fieldObject = rset.getDate(sFieldName);

			}
		}
		if (Blob.class.isAssignableFrom(field.getType())) {
			fieldObject = rset.getBytes(sFieldName);
			byte[] bb = (byte[]) fieldObject;
		}
		if (field.getType() == byte[].class) {
			fieldObject = rset.getBytes(sFieldName);
		}
		try {
			if (fieldObject != null) {
				field.set(object, fieldObject);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Field Type is " + field.getType().getName() + " / Field name = " + sFieldName + "("
					+ field.getName() + ") Value is : " + (fieldObject == null ? "null" : fieldObject.toString()));
		}
		return object;
	}

	private String toDbName(String name) {

		if (name.startsWith("db")) {
			return name.substring(2);
		}
		String dbName = "";
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (Character.isUpperCase(c)) {
				dbName += "_";
			}

			dbName += Character.toUpperCase(c);
		}
		return dbName;
	}

	/**
	 * 
	 * Gets the next object id <br>
	 * Uses The GetNextId token defined in DbSpecific.properties file to get the
	 * query to be executed to get the next id
	 * 
	 * <pre>
	 * * A commun definition of this token for oracle would be :
	 * select s_@param.nextval from dual
	 * 
	 * * For a MySql, it could be :
	 * select count(*) from @param for update
	 * 
	 * The api replaces the '@param' token by the table name.
	 * 
	 * </pre>
	 * 
	 * @param objectMapping The object to get the next id
	 * @return Long The Next id
	 * @exception SQLException         If the selec execution fails
	 * @exception APIINternalException For other errors
	 * 
	 */
	public Long getNextId(DbObjectMapping objectMapping) throws SQLException, APIInternalException {
		String nextIdSelect = null;
		CustomSelectQueryResult queryResult = null;

		try {
			nextIdSelect = DbSpecific.get().getNextIdSelect(Util.getTableName(objectMapping),
					objectMapping.getPrimaryKey().getName());

			if (nextIdSelect.equals("native")) {
				return null;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("executing " + nextIdSelect);
			}

			CustomQuery selectQuery = new CustomSelectQuery(nextIdSelect);

			queryResult = (CustomSelectQueryResult) executeQuery(selectQuery);

			return new Long(queryResult.getObject(0, 0).toString());
		} catch (Exception e) {
			logger.error(StringUtils.exceptionToString(e, true));
			throw new APIInternalException(e);
		}

	}

	public void commit() throws SQLException {
		sql.commit();
		sql.close();
	}

	public void rollback() throws SQLException {
		sql.rollback();
		sql.close();
	}

	public void ddl(String ddl) throws SQLException {
		try {
			sql.ddl(ddl);
			logger.info("DDL Execution OK " + ddl);
		} catch (SQLException e) {
			logger.error("ERROR WHILE EXECUTING " + ddl, e);
			throw e;
		}
	}

	public void close() {
		sql.close();
	}

	public Sql getSql() {
		return sql;
	}

	public ConnectionPoolInfo getConnectionPoolInfo() {
		return sql.getConnecitonPool().getConnectionPoolInfo();
	}

}
