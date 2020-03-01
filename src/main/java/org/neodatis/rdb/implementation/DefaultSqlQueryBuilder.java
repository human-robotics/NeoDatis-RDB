package org.neodatis.rdb.implementation;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Category;
import org.neodatis.rdb.DBTable;
import org.neodatis.rdb.RDB;
import org.neodatis.rdb.DbObjectMapping;
import org.neodatis.rdb.DeleteQuery;
import org.neodatis.rdb.InsertQuery;
import org.neodatis.rdb.RDBFactory;
import org.neodatis.rdb.SelectQuery;
import org.neodatis.rdb.UpdateQuery;
import org.neodatis.rdb.query.CustomSelectQueryResult;
import org.neodatis.rdb.query.DefaultDeleteQuery;
import org.neodatis.rdb.query.DefaultInsertQuery;
import org.neodatis.rdb.query.DefaultSelectQuery;
import org.neodatis.rdb.query.DefaultSingleTableSelectQuery;
import org.neodatis.rdb.query.DefaultUpdateQuery;
import org.neodatis.tools.StringUtils;

/**
 * Classe SqlQueryBuilder - Object with Sql functions. Builds SQL Inserts,
 * Updates, Selects and Deletes using Reflection Upon simple objects
 * 
 * <pre>
 * Example : &lt;br&gt;
 * If you have the following class
 * &lt;code&gt;
 * public class Client implements DbObjectMapping
 *     {
 *         //** Database fields
 *         protected String NAME;
 *         protected Date BIRTH_DATE;
 *         protected String ADRESS;
 *         protected BigDecimal MONEY;
 * 
 *        //** To get the primary key field name
 *        public PrimaryKey getPrimaryKey()
 *        {
 *           return new DefaultPrimaryKey(&quot;NAME&quot;);
 *        }
 * 
 *        //** To get the physical storage name
 *        public String getStorageName()
 *        {
 *           return &quot;CLIENT&quot;;
 *        }
 * 
 *         ....And all setters and Getters
 *     }
 *     &lt;/code&gt;
 * 
 *     This class will be abble to automatically generate all the possibles Insert, select ,... Queries
 * 
 * </pre>
 * 
 * @author Olivier smadja - mailto:osmadja@netcourrier.com
 * @version 01/01/2000 - creation
 * @version 16/08/2000 - change the name of the class SqlObject ->
 *          SqlQueryBuilder
 * @version 16/08/2000 - Adds the management of the table name. Table name can
 *          be different from the name of the class. _sClassName attribute
 *          removed.
 * @version 21/08/2000 - Managment of Date type value - Managment of char type
 *          value - Managment of inheritance and field search in superclass -
 *          Substituition of System.out.println with Tracer.println
 * @version 24/08/2000 - Adds the order by clause
 * @version 29/08/2000 - Change not initialized value from -1 to empty string
 * @version 30/08/2000 - Adds the group by clause
 * @version 31/08/2000 - Adds the getter for table name
 * @version 13/09/2000 - Adds the buildCustomUpdate.
 * @version 14/09/2000 - Correction of the where (where where)
 * @version 31/12/2000 - Uses a DbSpecific to be database independent
 * @version 04/02/2001 - Adds the join function only for the where
 * @version 05/02/2001 - Adds the possiblity of various joins
 * @version 06/02/2001 - Adds the distinct funciton on a field
 * @version 10/02/2001 - Adds buildCustomUpdateWithValues
 * @version 09/04/2001 - Sets the bIsJoin flag to false and two join vectors to
 *          new in init and to true in setJoinAlias.
 * @version 01/05/2001 - Manages ' in string : replace ' by ''
 * @version 23/05/2001 - avoid join table duplication
 * @version 05/06/2001 - use Long is setFieldValue instead of Integer to correct
 *          bug with bigvalues
 * @version 14/07/2001 - adjust to new Tracer class(with labels)
 * @version 17/07/2001 - Separate Int and Long set in setFieldValue
 * @version 13/08/2001 - Adds the RemoveDistinctField() method.
 * 
 */

public class DefaultSqlQueryBuilder {
	/** Creates the root */
	static Category _log = Category.getInstance(DefaultSqlQueryBuilder.class.getName());

	// final String DATE_TIME_FORMAT_OBJECT = "dd/MM/yyyy HH:mm";
	// final String DATE_TIME_FORMAT_BD = "DD/MM/YYYY HH24:MI";
	final String DATE_TIME_DEFAULT_FORMAT_BD = "dd/MM/yyyy HH:mm";

	
	protected RdbReflection rdbReflection;

	/**
	 * Constructor
	 */
	public DefaultSqlQueryBuilder() {
		init();
	}

	/**
	 * Inits the object
	 */
	void init() {
		rdbReflection = new RdbReflection();

	}

	public String buildInsert(InsertQuery insertQuery, RDB rdb) throws Exception {

		int nField = 0;
		DefaultInsertQuery defaultInsertQuery = (DefaultInsertQuery) insertQuery;

		Field[] fields = rdbReflection.getFieldsOf(defaultInsertQuery.getObject().getClass());

		StringBuffer sql = new StringBuffer();

		checkPrimaryKeyValue(defaultInsertQuery, rdb);

		String nextIdSelect = DbSpecific.get().getNextIdSelect("", "");
		DbObjectMapping object = insertQuery.getObject();
		String primaryKey = RdbReflection._DATABASE_FIELD_PREFIX + object.getPrimaryKey().getName();

		sql.append(" INSERT INTO ").append(Util.getTableName(defaultInsertQuery.getObject())).append(" ( ");

		for (nField = 0; nField < fields.length; nField++) {
			// for native objects, ignore primary key field
			if (fields[nField].getName().equals(primaryKey)) {
				if (nextIdSelect.equalsIgnoreCase("native") && rdbReflection.fieldIsNull(fields[nField],object)) {
					continue;
				}
			}

			sql.append(" ").append(Util.getColumnName(rdbReflection.getRealFieldName(fields[nField]))).append(" ");

			if (nField < fields.length - 1) {
				sql.append(" , ");
			}
		}

		sql.append(" ) VALUES ( ");

		for (nField = 0; nField < fields.length; nField++) {

			// for native objects, ignore primary key field
			if (fields[nField].getName().equals(primaryKey)) {
				if (nextIdSelect.equalsIgnoreCase("native") && rdbReflection.fieldIsNull(fields[nField],object)) {
					continue;
				}
			}
			sql.append(" ").append(rdbReflection.getStringFieldValue(fields[nField], defaultInsertQuery.getObject())).append(" ");

			if (nField < fields.length - 1) {
				sql.append(" , ");
			}
		}

		sql.append(" ) ");

		// _log.info( "BUILT SQL :" + sql.toString() );

		return sql.toString();

	}
	/** Builds the prepared statement string with '?'
	 * 
	 * @param insertQuery
	 * @param defaultRDB 
	 * @return
	 * @throws Exception
	 */
	
	public String buildInsertPreparedStatement(InsertQuery insertQuery, RDB rdb) throws Exception {

		int nField = 0;
		DefaultInsertQuery defaultInsertQuery = (DefaultInsertQuery) insertQuery;

		Field[] fields = rdbReflection.getFieldsOf(defaultInsertQuery.getObject().getClass());

		StringBuffer sql = new StringBuffer();

		checkPrimaryKeyValue(defaultInsertQuery,rdb);

		String nextIdSelect = DbSpecific.get().getNextIdSelect("", "");
		DbObjectMapping object = insertQuery.getObject();
		String primaryKey = RdbReflection._DATABASE_FIELD_PREFIX + object.getPrimaryKey().getName();

		sql.append(" INSERT INTO ").append(Util.getTableName(defaultInsertQuery.getObject())).append(" ( ");

		for (nField = 0; nField < fields.length; nField++) {
			// for native objects, ignore primary key field
			if (fields[nField].getName().equals(primaryKey)) {
				if (nextIdSelect.equalsIgnoreCase("native") && rdbReflection.fieldIsNull(fields[nField],object)) {
					continue;
				}
			}

			sql.append(" ").append(Util.getColumnName(rdbReflection.getRealFieldName(fields[nField]))).append(" ");

			if (nField < fields.length - 1) {
				sql.append(" , ");
			}
		}

		sql.append(" ) VALUES ( ");

		for (nField = 0; nField < fields.length; nField++) {

			// for native objects, ignore primary key field
			if (fields[nField].getName().equals(primaryKey)) {
				if (nextIdSelect.equalsIgnoreCase("native") && rdbReflection.fieldIsNull(fields[nField],object)) {
					continue;
				}
			}
			sql.append("?");

			if (nField < fields.length - 1) {
				sql.append(" , ");
			}
		}

		sql.append(" ) ");

		// _log.info( "BUILT SQL :" + sql.toString() );

		return sql.toString();

	}

	/**
	 * 
	 * Checks if query already have a primary key, if not try to automatically
	 * define it The current version assumes that primary key object are of type
	 * Long
	 * @param rdb 
	 * 
	 */
	protected void checkPrimaryKeyValue(InsertQuery insertQuery, RDB rdb) throws Exception {
		_log.debug("Checking primary key value for " + insertQuery.toString());

		// The databse service to execute select
		RDB dbService = null;

		// gets the object to get primary key
		DbObjectMapping object = insertQuery.getObject();

		// Gets the value of the primary field for this object
		Object primaryKeyObject = object.getPrimaryKey().getValue(object);

		// Checks if primary is defined
		// If object primaryKeyObejct is null, then we have to define the
		// primary
		if (primaryKeyObject == null) {
			try {
				Long value = rdb.getNextId(object);
				object.getPrimaryKey().setValue(object, value);
			} finally {
			}

		} else {
			_log.info("Primary is set : " + primaryKeyObject.toString());
		}
	}

	/**
	 * Builds the SQL SELECT
	 * 
	 * @param The
	 *            Select query bean
	 * @return The SQL SELECT
	 * @exception InstantiationException
	 *                When failing in instantiating the object
	 * @exception IllegalAccessException
	 *                When failing in accessing instantiated object
	 */
	public String buildSelect(SelectQuery in_selectQuery) throws InstantiationException, IllegalAccessException {

		DBTable table = null;

		// To indicate that it is the first field
		int nNbFields = 0;

		DefaultSelectQuery defaultGenericSelectQuery = (DefaultSelectQuery) in_selectQuery;

		// Builds the string to save sql SELECT
		StringBuffer sSelect = new StringBuffer();

		// Builds the string to save sql FROM
		StringBuffer sFrom = new StringBuffer();

		// Builds the string to save whole SQL QUery
		StringBuffer sQuery = new StringBuffer();

		// To keep each simple select
		DefaultSingleTableSelectQuery singleTableSelect = null;

		// To keeps each simple select fields
		Field[] singleTableSelectFields = null;

		// The single table alias
		String singleTableAlias = null;

		if (defaultGenericSelectQuery.isDistinct()) {
			sSelect.append(" DISTINCT ");
		}

		// Parse all simple selects
		for (int nSelect = 0; nSelect < defaultGenericSelectQuery.getSingleTableSelectList().size(); nSelect++) {
			// For each simple select
			singleTableSelect = (DefaultSingleTableSelectQuery) defaultGenericSelectQuery.getSingleTableSelectList().get(nSelect);

			// Gets the fields
			singleTableSelectFields = rdbReflection.getFieldsOf(singleTableSelect.getObjectType());

			_log.debug("Class name is " + singleTableSelect.getObjectType().getName());
			
			DbObjectMapping o = (DbObjectMapping) (singleTableSelect.getObjectType().newInstance());

			table = o.getTable();

			// Gets the alias
			singleTableAlias = table.getAlias();

			if (sFrom.length() != 0) {
				sFrom.append(" , ");
			}
			// Builds The From part
			sFrom.append(Util.getTableName(o));
			sFrom.append(" ").append(singleTableAlias);

			// For each fields, appends the alias and the field , and this
			// result to the query
			for (int nField = 0; nField < singleTableSelectFields.length; nField++) {
				// Check if must be inserted a comma
				if (nNbFields > 0) {
					sSelect.append(" , ");
				}

				String fieldName = Util.getColumnName(rdbReflection.getRealFieldName(singleTableSelectFields[nField]));
				
				if(singleTableAlias.isEmpty()){
					// Appends the table alias and the field
					sSelect.append(" ").append(fieldName);
					
				} else {
					// Appends the table alias and the field
					sSelect.append(" ").append(singleTableAlias).append(".").append(fieldName);
				}
				// put the field alias name: fiels name + _ and table index
				sSelect.append(" f_").append(nField).append("_").append(nSelect);

				// Adds one the number of fields
				nNbFields++;
			}
		}

		// Actually build the whole query
		sQuery.append("SELECT ").append(sSelect).append(" FROM ").append(sFrom);

		if (defaultGenericSelectQuery.getWhere() != null && !defaultGenericSelectQuery.getWhere().isEmpty()) {
			sQuery.append(" WHERE ").append(defaultGenericSelectQuery.getWhere().getSql(true));
		}

		if (defaultGenericSelectQuery.getOrderBy() != null) {
			sQuery.append(" ORDER BY ").append(defaultGenericSelectQuery.getOrderBy());
		}

		if (defaultGenericSelectQuery.getGroupBy() != null) {
			sQuery.append(" GROUP BY  ").append(defaultGenericSelectQuery.getGroupBy());
		}

		// _log.info( "BUILT SQL :" + sQuery.toString() );

		return sQuery.toString();

	}

	/**
	 * Builds the UPDATE Clause
	 * 
	 * @param String
	 *            The Where Clause
	 * @return String The UPDATE clause
	 * @exception Can
	 *                be IllegalAcessException or NoSuchFieldException for the
	 *                implementation of primary key
	 */
	public String buildUpdate(UpdateQuery in_updateQuery) throws Exception {
		StringBuffer sQuery = new StringBuffer();
		StringBuffer sFields = new StringBuffer();
		Field[] fields = null;

		DefaultUpdateQuery defaultUpdateQuery = (DefaultUpdateQuery) in_updateQuery;

		fields = rdbReflection.getFieldsOf(in_updateQuery.getObject().getClass());

		sQuery.append(" UPDATE ").append(Util.getTableName(in_updateQuery.getObject())).append(" ");

		// Mysql does not suporte alias in updates
		// sQuery.append( in_updateQuery.getObject().getTable().getAlias() );
		sQuery.append(" SET ");

		for (int nField = 0; nField < fields.length; nField++) {

			if (defaultUpdateQuery.canUpdateField(rdbReflection.getRealFieldName(fields[nField]))) {
				if (sFields.length() != 0) {
					sFields.append(" , ");
				}
				sFields.append(" ").append(Util.getColumnName(rdbReflection.getRealFieldName(fields[nField])));
				sFields.append(" = ").append(rdbReflection.getStringFieldValue(fields[nField], in_updateQuery.getObject()));
			}
		}

		sQuery.append(sFields);

		if (in_updateQuery.getWhere() != null && !in_updateQuery.getWhere().isEmpty()) {
			sQuery.append(" WHERE ").append(in_updateQuery.getWhere().getSql(false));
		} else {
			sQuery.append(" WHERE ").append(in_updateQuery.getObject().getPrimaryKey().getSql(in_updateQuery.getObject()));
		}

		// _log.info("Build SQL : " + sQuery.toString() );

		return sQuery.toString();

	}


	/**
	 * Builds the UPDATE Clause
	 * 
	 * @param String
	 *            The Where Clause
	 * @return String The UPDATE clause
	 * @exception Can
	 *                be IllegalAcessException or NoSuchFieldException for the
	 *                implementation of primary key
	 */
	public String buildUpdatePrepraredStatement(UpdateQuery updateQuery) throws Exception {
		StringBuffer sQuery = new StringBuffer();
		StringBuffer sFields = new StringBuffer();
		Field[] fields = null;

		DefaultUpdateQuery defaultUpdateQuery = (DefaultUpdateQuery) updateQuery;

		fields = rdbReflection.getFieldsOf(updateQuery.getObject().getClass());

		sQuery.append(" UPDATE ").append(Util.getTableName(updateQuery.getObject())).append(" ");
		String nextIdSelect = DbSpecific.get().getNextIdSelect("", "");
		String primaryKey = RdbReflection._DATABASE_FIELD_PREFIX +  updateQuery.getObject().getPrimaryKey().getName();
		// Mysql does not suporte alias in updates
		// sQuery.append( in_updateQuery.getObject().getTable().getAlias() );
		sQuery.append(" SET ");

		for (int nField = 0; nField < fields.length; nField++) {
			// if native & primary => do not update field
			if (fields[nField].getName().equals(primaryKey)) {
				boolean isNative = nextIdSelect.equalsIgnoreCase("native");
				if ( isNative) {
					continue;
				}
			}

			if (defaultUpdateQuery.canUpdateField(rdbReflection.getRealFieldName(fields[nField]))) {
				if (sFields.length() != 0) {
					sFields.append(" , ");
				}
				sFields.append(" ").append(Util.getColumnName(rdbReflection.getRealFieldName(fields[nField])));
				sFields.append(" = ?");
			}
		}

		sQuery.append(sFields);

		if (updateQuery.getWhere() != null && !updateQuery.getWhere().isEmpty()) {
			sQuery.append(" WHERE ").append(updateQuery.getWhere().getSql(false));
		} else {
			sQuery.append(" WHERE ").append(updateQuery.getObject().getPrimaryKey().getSql(updateQuery.getObject()));
		}

		// _log.info("Build SQL : " + sQuery.toString() );

		return sQuery.toString();

	}
	/**
	 * Builds the UPDATE Clause
	 * 
	 * @return String The UPDATE clause
	 * 
	 *         public String buildUpdate(UpdateQuery in_updateQuery) throws
	 *         SQLException { Field keyField = null; DefaultUpdateQuery
	 *         defaultUpdateQuery = (DefaultUpdateQuery) in_updateQuery;
	 * 
	 *         try {
	 *         //System.out.println("Looking for key field for class "+this
	 *         .getClass().getName() ); keyField = getKeyFieldObject(); } catch(
	 *         NoSuchFieldException e ) { throw new
	 *         SQLException("Unable to find key field : "
	 *         +in_updateQuery.getObject().getPrimaryKey().toString()); }
	 * 
	 *         StringBuffer sWhere = new StringBuffer();
	 * 
	 * 
	 *         
	 *         sWhere.append(in_updateQuery.getObject().getPrimaryKey().toString(
	 *         ) ).append( " = " ).append( getStringFieldValue(keyField) );
	 * 
	 *         return buildUpdate( sWhere.toString() );
	 * 
	 *         }
	 * 
	 *         /** Builds the UPDATE Clause
	 * @return String The UPDATE clause
	 * @param String
	 *            The list of fields to be updates
	 * 
	 *            public String buildCustomUpdate(String in_sFields) throws
	 *            SQLException { Field keyField = null;
	 * 
	 *            try {
	 *            //System.out.println("Looking for key field for class "+this
	 *            .getClass().getName() ); keyField = getKeyFieldObject(); }
	 *            catch( NoSuchFieldException e ) { throw new
	 *            SQLException("Unable to find key field : "+
	 *            _dbObjectMapping.getPrimaryKey().toString()); }
	 * 
	 *            StringBuffer sWhere = new StringBuffer();
	 * 
	 *            sWhere.append( _dbObjectMapping.getPrimaryKey().toString() +
	 *            " = " + getStringFieldValue(keyField) );
	 * 
	 *            return buildCustomUpdate( in_sFields , sWhere.toString() );
	 * 
	 *            }
	 * 
	 *            /** Builds the UPDATE Clause
	 * @param String
	 *            The Where Clause
	 * @param String
	 *            The fields to be updated
	 * @return String The UPDATE clause
	 * 
	 *         public String buildCustomUpdate(String in_sFields , String
	 *         in_sWhere) throws SQLException { StringBuffer sQuery = new
	 *         StringBuffer(); StringBuffer sAttributes = new StringBuffer();
	 *         String sAttributesWithoutLastComma = null;
	 * 
	 *         sQuery.append(" UPDATE "+ _sTableName + " SET " );
	 * 
	 *         for(int nField = 0 ; nField < _nFieldsNumber ; nField ++ ) { //
	 *         Checks if this fields is in the list of in_sfields if(
	 *         in_sFields.toUpperCase().indexOf(
	 *         getFieldName(nField).toUpperCase() ) != -1 ) {
	 *         sAttributes.append( " "+ getFieldName(nField) + " = " +
	 *         getStringFieldValue(nField) );
	 * 
	 *         if( nField < _nFieldsNumber-1 ) { sAttributes.append(","); } } }
	 * 
	 *         sAttributesWithoutLastComma = sAttributes.toString();
	 * 
	 *         // If this string ends with a comma(,) // Then removes it if(
	 *         sAttributesWithoutLastComma.endsWith(",") ) {
	 *         sAttributesWithoutLastComma =
	 *         sAttributesWithoutLastComma.substring(1 ,
	 *         sAttributesWithoutLastComma.length()-1); }
	 * 
	 *         sQuery.append(sAttributesWithoutLastComma);
	 * 
	 *         if( in_sWhere != null ) { sQuery.append(" WHERE "+ in_sWhere); }
	 * 
	 *         Tracer.println( sQuery.toString() , _TRACE_LABEL_SQL);
	 * 
	 *         return sQuery.toString();
	 * 
	 *         }
	 * 
	 *         /** Builds the UPDATE Clause
	 * @return String The UPDATE clause
	 * @param String
	 *            The list of fields to be updates and their values
	 * 
	 *            public String buildCustomUpdateWithValues(String
	 *            in_sFieldsAndValues) throws SQLException { Field keyField =
	 *            null;
	 * 
	 *            try {
	 *            //System.out.println("Looking for key field for class "+this
	 *            .getClass().getName() ); keyField = getKeyFieldObject(); }
	 *            catch( NoSuchFieldException e ) { throw new
	 *            SQLException("Unable to find key field : "
	 *            +_dbObjectMapping.getPrimaryKey().toString() ); }
	 * 
	 *            StringBuffer sWhere = new StringBuffer();
	 * 
	 *            sWhere.append( _dbObjectMapping.getPrimaryKey().toString() +
	 *            " = " + getStringFieldValue(keyField) );
	 * 
	 *            return buildCustomUpdateWithValues( in_sFieldsAndValues ,
	 *            sWhere.toString() );
	 * 
	 *            }
	 * 
	 *            /** Builds the UPDATE Clause
	 * @param String
	 *            The Where Clause
	 * @param String
	 *            The fields to be updated and their values
	 * @return String The UPDATE clause
	 * 
	 *         public String buildCustomUpdateWithValues(String
	 *         in_sFieldsAndValues , String in_sWhere) throws SQLException {
	 *         StringBuffer sQuery = new StringBuffer(); StringBuffer
	 *         sAttributes = new StringBuffer(); String
	 *         sAttributesWithoutLastComma = null;
	 * 
	 *         sQuery.append(" UPDATE " ).append( _sTableName ).append( " SET "
	 *         ); sQuery.append( in_sFieldsAndValues ).append(" " );
	 * 
	 *         if( in_sWhere != null ) { sQuery.append(" WHERE "+ in_sWhere); }
	 * 
	 *         Tracer.println( sQuery.toString() , _TRACE_LABEL_SQL);
	 * 
	 *         return sQuery.toString();
	 * 
	 *         }
	 */
	/**
	 * Builds the DELETE Clause
	 * 
	 * @return String The DELETE clause
	 * @param The
	 *            Delete query object
	 * @exception InstantiationException
	 *                When failing in instanciating the object
	 * @exception IllegalAccessException
	 *                When failing in accessing instanciated object
	 */
	public String buildDelete(DeleteQuery in_deleteQuery) throws Exception {

		StringBuffer sQuery = new StringBuffer();
		DefaultDeleteQuery defaultDeleteQuery = (DefaultDeleteQuery) in_deleteQuery;

		// Creates an instance
		DbObjectMapping object = null;
		// Checks if object exists in query, if not creates an instance
		if (in_deleteQuery.getObject() == null) {
			object = (DbObjectMapping) defaultDeleteQuery.getObjectType().newInstance();
		} else {
			object = in_deleteQuery.getObject();
		}

		sQuery.append(" DELETE FROM ").append(Util.getTableName(object));

		// mysql do not accept delete with alias
		// sQuery.append(" ").append( object.getTable().getAlias() );

		if (defaultDeleteQuery.getWhere() != null && !defaultDeleteQuery.getWhere().isEmpty()) {
			sQuery.append(" WHERE ").append(defaultDeleteQuery.getWhere().getSql(false));
		} else {
			sQuery.append(" WHERE ").append(object.getPrimaryKey().getSql(object));
		}

		// _log.info( "BUILT SQL :" + sQuery.toString() );

		return sQuery.toString();

	}

	
	
	

	
}