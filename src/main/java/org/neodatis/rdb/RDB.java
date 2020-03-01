package org.neodatis.rdb;

import java.sql.SQLException;

import org.neodatis.ConnectionPoolInfo;
import org.neodatis.rdb.implementation.DefaultConnectionPool;
import org.neodatis.rdb.query.CustomDeleteQuery;
import org.neodatis.rdb.query.CustomSelectQuery;
import org.neodatis.rdb.query.DefaultSelectQuery;

/* Database service is an interface to define the contract of a class that would do the database working.
The class that will implement this interface must be abble to insert , update , deletes and select all kind of data
@author Olivier smadja - osmadja@netcourrier.com
@version 09/07/2002 - Olivier : Creation
*/

public interface RDB {

	/**
	 *
	 * Executes the query returning a list of result - SELECT
	 * 
	 * @param The
	 *            query
	 * @return The query result
	 * @exception SQLException
	 *                If an sql exception occurs
	 *
	 *
	 */
	public QueryResult executeQuery(Query in_query) throws SQLException, APIInternalException;

	/**
	 *
	 * Checks if objects exists
	 * 
	 * @param in_selectQuery
	 *            The Query object
	 * @return boolean true if exist
	 * @see DefaultSelectQuery
	 * @exception SQLException
	 *                If an sql exception occurs
	 *
	 *
	 *
	 */
	public boolean exist(SelectQuery in_selectQuery) throws SQLException, APIInternalException;

	/**
	 *
	 * Counts the number of elememts
	 * 
	 * @param in_selectQuery
	 *            The Query object
	 * @return boolean true if exist
	 * @see DefaultSelectQuery
	 * @exception SQLException
	 *                If an sql exception occurs
	 *
	 *
	 */
	public long count(SelectQuery in_selectQuery) throws SQLException, APIInternalException;

	/**
	 *
	 * Gets the next object id
	 * 
	 * @param in_objectMapping
	 *            The object to get the next id
	 * @return Long The Next id
	 * @exception SQLException
	 *                when sql problem occur * @exception APIINternalException For
	 *                other errors
	 *
	 *
	 */
	public Long getNextId(DbObjectMapping in_objectMapping) throws SQLException, APIInternalException;

	void insert(DbObjectMapping o) throws Exception;

	void update(DbObjectMapping o) throws Exception;

	void save(DbObjectWithLongId o) throws Exception;

	int delete(DbObjectMapping o) throws SQLException, APIInternalException;

	int delete(DeleteQuery q) throws SQLException, APIInternalException;

	int delete(CustomDeleteQuery q) throws SQLException, APIInternalException;

	/**
	 * Selects data according to the DefaultSelectQuery object
	 * 
	 * @param selectQuery
	 *            The Query object
	 * @return A Query result object
	 * @see DefaultSelectQuery
	 * @exception SQLException
	 *                If an sql exception occurs
	 */
	public QueryResult select(SelectQuery selectQuery) throws SQLException, APIInternalException;

	/**
	 * Selects data according to the CustomSelectQuery object
	 * 
	 * @param selectQuery
	 *            The Query object
	 * @return A Query result object
	 * @see CustomSelectQuery
	 * @exception SQLException
	 *                If an sql exception occurs
	 */
	public QueryResult select(CustomSelectQuery selectQuery) throws SQLException, APIInternalException;

	public void commit() throws SQLException;

	public void rollback() throws SQLException;

	public void ddl(String ddl) throws SQLException;

	public void close();

	public ConnectionPoolInfo getConnectionPoolInfo();

}
