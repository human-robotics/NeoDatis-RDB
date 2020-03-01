package org.neodatis.rdb.query;

import org.neodatis.rdb.DBColumn;
import org.neodatis.rdb.DBTable;
import org.neodatis.rdb.implementation.DbSpecific;
import org.neodatis.rdb.implementation.Util;

/*
 @version 22/07/2002 - Olivier : Creation
 */

public class DefaultDBColumn implements DBColumn {
	private String name;
	private Class type;
	private DBTable dbTable;
	private String function;

	public DefaultDBColumn(String in_sName) {
		name = in_sName;
	}
	public DefaultDBColumn(DBTable in_dbTable, String in_sName, Class in_class) {
		name = in_sName;
		type = in_class;
		dbTable = in_dbTable;
	}

	public String getName() {
		return name;
	}

	public Class getType() {
		return type;
	}

	public DBTable getTable() {
		return dbTable;
	}

	/**
	 * Returns the sql representaion of the column, The default is to return
	 * with table alias.
	 * 
	 */
	public String toString() {

		return getSql(true);
	}

	/**
	 * Return the representation of the column depending of the boolean
	 * 
	 * @param To
	 *            indicate if column representation must contain the alias, if
	 *            true, it will.
	 *            <code> If the column is NAME , and the alias if myclient.
	 * The result of toString(true) is myclient.name ,
	 * while the result of toString(false) is name
	 */
	public String getSql(boolean in_bWithAlias) {
		String value = null;
		if (in_bWithAlias) {
			if (dbTable==null || dbTable.getAlias().isEmpty()) {
				value = name;
			} else {
				value = new StringBuffer(dbTable.getAlias()).append(".")
						.append(name).toString();
			}
		} else {
			value = name;
		}

		if (function != null) {
			value = function.replaceAll("@", value);
		}
		return Util.getColumnName(value);
	}

	/**
	 * the toUpper is defined in the DbSpecific.get().properties file
	 */
	public DBColumn toUpper() {
		function = DbSpecific.get().getToUpperCaseFunction();
		return this;
	}

	/**
	 * the toLower is defined in the DbSpecific.get().properties file
	 */
	public DBColumn toLower() {
		function = DbSpecific.get().getToLowerCaseFunction();
		return this;
	}

	/**
	 * Adds a specific function to the current column. The name of the column
	 * must be declared in the DbSpecific.get().properties file
	 * 
	 * 
	 * <code>
	 * Example : To truncate a date : declare 
	 * DateToStringConverter=TO_CHAR('@','DD/MM/YYYY') in the DbSpecific.get().properties file
	 * 
	 * and call MyClassDbHelper.function("DateToStringConverter");
	 * 
	 * </code>
	 */
	public DBColumn function(String functionName) {
		DefaultDBColumn newColumn = new DefaultDBColumn(dbTable, name, type);
		newColumn.function = DbSpecific.get().getValue(functionName);
		return newColumn;
	}

}
