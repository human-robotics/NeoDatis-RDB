package org.neodatis.rdb.implementation;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * To get database information. The table list and the list of columns o a
 * table...
 * 
 * @author osmadja@netcourrier.com
 * @version 10/06/2001 creation
 */

public class DatabaseMetaInformation {

	public static final String TABLE_TYPE_TABLE = "TABLE";
	public static final String TABLE_TYPE_VIEW = "VIEW";
	public static final String TABLE_TYPE_SYSTEM_TABLE = "SYSTEMA TABLE";
	public static final String TABLE_TYPE_ALIAS = "ALIAS";
	DefaultConnectionPool pool;

	public DatabaseMetaInformation() {
		pool = new DefaultConnectionPool();
	}

	/**
	 * Returns a list containing a list of table name
	 * 
	 * @param String
	 *            The schema name : user
	 * @param The
	 *            type of the table : TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL
	 *            TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
	 * @return Vector The list of table
	 * @throws Exception
	 */
	public List<String> getTableNames(String schema, String type)
			throws Exception {

		// The to store the names
		List<String> result = new ArrayList<String>();

		// The connection
		Connection connection = null;

		// Tha database infos
		DatabaseMetaData dbMetaData = null;

		// The ResultSet to parse tables
		ResultSet rset = null;
		try {
			connection = pool.getConnection();

			dbMetaData = connection.getMetaData();

			rset = dbMetaData.getTables(null, schema, null,
					new String[] { type });

			while (rset.next()) {
				String tableName = rset.getString("TABLE_NAME");
				result.add(tableName);
				System.out.println(tableName + " "
						+ rset.getString("TABLE_TYPE"));
			}
		} finally {
			if (rset != null) {
				rset.close();
			}
			pool.releaseConnection(connection);
		}

		return result;
	}

	/**
	 * Returns a vector containing a list of columns for the table
	 * 
	 * @param String
	 *            The table name
	 * @return Vector The list of columns
	 */
	public List<DatabaseColumn> getTableColumnsGeneric(String in_sSchema,
			String in_sTableName) throws SQLException {

		// To store each column
		DatabaseColumn databaseColumn = null;

		// The to store the names
		List<DatabaseColumn> result = new ArrayList<DatabaseColumn>();

		// The connection
		Connection connection = null;

		// Tha database infos
		DatabaseMetaData dbMetaData = null;

		// The ResultSet to parse tables
		ResultSet rset = null;
		try {
			connection = pool.getConnection();

			dbMetaData = connection.getMetaData();

			DatabaseMetaData meta = connection.getMetaData();
			ResultSet rsetmeta = meta
					.getImportedKeys(null, null, in_sTableName);
			Map<String, String> foreignKeyMap = new HashMap<String, String>();
			while (rsetmeta.next()) {
				String pkTableName = rsetmeta.getString("PKTABLE_NAME");
				String pkColumnName = rsetmeta.getString("PKCOLUMN_NAME");
				String fkTableName = rsetmeta.getString("FKTABLE_NAME");
				String fkColumnName = rsetmeta.getString("FKCOLUMN_NAME");
				System.out.println(pkTableName + "." + pkColumnName + " -> "
						+ fkTableName + "." + fkColumnName);
				foreignKeyMap.put(fkColumnName, pkTableName + "."
						+ pkColumnName);
			}

			ResultSet primaryKeys = meta.getPrimaryKeys(null, null,
					in_sTableName);
			Map<String, String> primaryKeysMap = new HashMap<String, String>();
			while (primaryKeys.next()) {
				String name = primaryKeys.getString("COLUMN_NAME");
				primaryKeysMap.put(name, name);
			}

			rset = dbMetaData.getColumns(null, in_sSchema, in_sTableName, null);

			while (rset.next()) {
				String tableName = rset.getString("TABLE_NAME");
				String columnName = rset.getString("COLUMN_NAME");
				int dataType = rset.getInt("DATA_TYPE");
				int dataLength = rset.getInt("COLUMN_SIZE");
				int scale = rset.getInt("DECIMAL_DIGITS");
				boolean isNullable = rset.getInt("NULLABLE") != ResultSetMetaData.columnNoNulls;
				String comment = rset.getString("REMARKS");
				databaseColumn = new DatabaseColumn(
						primaryKeysMap.containsKey(columnName), tableName,
						columnName, dataType, getStringDataType(dataType),
						dataLength, scale, comment, isNullable,
						foreignKeyMap.get(columnName));
				result.add(databaseColumn);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		} finally {
			if (rset != null) {
				rset.close();
			}
			pool.releaseConnection(connection);
			//connection.close();
		}

		return result;
	}

	public String getStringDataType(int in_nType) {
		String type = null;
		switch (in_nType) {
		case Types.TIMESTAMP:
			type = "Date";
			break;
		case Types.DATE:
			type = "Date";
			break;
		case Types.CHAR:
			type = "String";
			break;

		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.REAL:
			type = "BigDecimal";
			break;
		case Types.BIGINT:
		case Types.SMALLINT:
		case Types.INTEGER:
			type = "Long";
			break;
		case Types.VARCHAR:
			type = "String";
			break;
		case Types.NUMERIC:
			type = "BigDecimal";
			break;
		case Types.BOOLEAN:
			type = "boolean";
			break;
		case Types.TINYINT:
			type = "Short";
			break;
		case Types.BIT:
			type = "boolean";
			break;// postgresql bug?
		case Types.BINARY:
			type = "byte[]";
			break;// postgresql bug?
		case Types.BLOB:
			type = "byte[]";
			break;// postgresql bug?
		case Types.LONGVARBINARY:
			type = "byte[]";
			break;
		case Types.LONGVARCHAR:
			type = "String";
			break;
		}

		if (type == null) {
			String s = "The JDBC type " + in_nType
					+ " is not supported by NeoDatis RDB";
			System.out.println(s);
			throw new RuntimeException(s);

		}
		System.out.println("getting Type for " + in_nType + " : " + type);

		return type;
	}

	/**
	 * Returns a vector containing a list of columns for the table
	 * 
	 * @param String
	 *            The table name
	 * @return Vector The list of columns
	 */
	public List<DatabaseColumn> getTableColumns(String in_sSchema,
			String in_sTableName) throws SQLException {
		String tableName = null;
		String columnName = null;
		int dataType = 0;
		String sComment = null;
		int dataLength = 0;

		// To store each column
		DatabaseColumn databaseColumn = null;

		// The to store the names
		List<DatabaseColumn> result = new ArrayList<DatabaseColumn>();

		// The connection
		Connection connection = null;

		// Tha database infos
		DatabaseMetaData dbMetaData = null;

		// The ResultSet to parse tables
		ResultSet rset = null;
		try {
			connection = pool.getConnection();

			DatabaseMetaData meta = connection.getMetaData();
			ResultSet rsetmeta = meta
					.getImportedKeys(null, null, in_sTableName);
			Map<String, String> foreignKeyMap = new HashMap<String, String>();
			while (rsetmeta.next()) {
				String pkTableName = rsetmeta.getString("PKTABLE_NAME");
				String pkColumnName = rsetmeta.getString("PKCOLUMN_NAME");
				String fkTableName = rsetmeta.getString("FKTABLE_NAME");
				String fkColumnName = rsetmeta.getString("FKCOLUMN_NAME");
				System.out.println(pkTableName + "." + pkColumnName + " -> "
						+ fkTableName + "." + fkColumnName);
				foreignKeyMap.put(fkColumnName, pkTableName + "."
						+ pkColumnName);
			}

			ResultSet primaryKeys = meta.getPrimaryKeys(null, null,
					in_sTableName);
			Map<String, String> primaryKeysMap = new HashMap<String, String>();
			while (primaryKeys.next()) {
				String name = primaryKeys.getString("COLUMN_NAME");
				primaryKeysMap.put(name, name);
			}

			String sql = "select * from " + in_sTableName + " where 1=0";
			rset = connection.createStatement().executeQuery(sql);
			ResultSetMetaData rsmd = rset.getMetaData();

			int columnCount = rsmd.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				tableName = in_sTableName;
				columnName = rsmd.getColumnName(i);
				dataType = rsmd.getColumnType(i);
				dataLength = rsmd.getPrecision(i);
				int scale = rsmd.getScale(i);
				boolean allowNull = rsmd.isNullable(i) != ResultSetMetaData.columnNoNulls;
				databaseColumn = new DatabaseColumn(
						primaryKeysMap.containsKey(columnName), tableName,
						columnName, dataType, getStringDataType(dataType),
						dataLength, scale, null, allowNull,
						foreignKeyMap.get(columnName));
				result.add(databaseColumn);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		} finally {
			if (rset != null) {
				rset.close();
			}
			pool.releaseConnection(connection);
		}

		return result;
	}

	/**
	 * Returns a strng containing the catalog of the current database
	 * 
	 * @throws Exception
	 */
	public String getCatalogName() throws Exception {
		Connection connection = null;

		try {
			connection = pool.getConnection();

			return connection.getCatalog();

		} finally {
			pool.releaseConnection(connection);
		}
	}

	static public void main(String[] args) throws Exception {
		DatabaseMetaInformation dmi = new DatabaseMetaInformation();
		System.out.println("Catalog : " + dmi.getCatalogName());

		List<String> tables = dmi.getTableNames(null, TABLE_TYPE_TABLE);

		System.out.println(tables);
	}

	public void close() {
		pool.close();
		
	}

}