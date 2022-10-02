package org.neodatis.rdb.implementation;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Category;
import org.neodatis.ConnectionPoolInfo.DatabaseType;
import org.neodatis.rdb.Config;
import org.neodatis.tools.Resource;
import org.neodatis.tools.StringUtils;

/**
 * To get specific constant of database. date conversion..
 * 
 * @author Olivier smadja <osmadja@gmail.com>
 * @version 31/12/2000 - creation
 */
public class DbSpecific {
	static Category _log = Category.getInstance(DbSpecific.class.getName());
	final String _FILENAME = "DbSpecific";
	SimpleDateFormat _dtFormatter;

	boolean _isInit = false;

	Resource _resource;
	protected String nextIdSelect;
	protected String selectIdAfterInsert;
	protected String databaseType;
	/** some databases do not work well with prepared statement : like sql lite */
	protected boolean usePreparedStatement;
	

	protected static DbSpecific instance;

	public DbSpecific(String databaseType) {
		this.databaseType = databaseType;
		// Default behavior is to use preparedStatements
		this.usePreparedStatement = true;
		
		if(DatabaseType.sqlite.toString().equals(databaseType)) {
			//this.usePreparedStatement = false;
		}
		init();
	}

	public static DbSpecific get() {
		if (instance == null) {
			instance = new DbSpecific(Config.databaseType);
		}
		return instance;
	}

	void init() {
		String fileName = _FILENAME;

		if (databaseType != null && !databaseType.equals("default")) {
			fileName = _FILENAME + "_" + databaseType;
		} else {
			_log.error("database type in null > not allowed!");
		}
		_log.info("Loading defaults from " + fileName);
		_resource = new Resource(fileName);
		_isInit = true;

		_dtFormatter = new SimpleDateFormat(getObjectDatePattern());
	}

	public String getValue(String in_sParam) {
		if (!_isInit) {
			init();
		}

		return _resource.getString(in_sParam);
	}

	public String getObjectDatePattern() {
		if (!_isInit) {
			init();
		}

		return _resource.getString("ObjectDatePattern");
	}

	public String getStringToDateConverter() {
		if (!_isInit) {
			init();
		}

		return _resource.getString("StringToDateConverter");
	}

	public String getDateToStringConverter() {
		if (!_isInit) {
			init();
		}

		return _resource.getString("DateToStringConverter");
	}

	public String getToUpperCaseFunction() {
		if (!_isInit) {
			init();
		}

		return _resource.getString("ToUpperCase");
	}

	public String getToLowerCaseFunction() {
		if (!_isInit) {
			init();
		}

		return _resource.getString("ToLowerCase");
	}

	public String getNextIdSelect(String in_sTableName, String in_sPkFieldName) {
		String nextIdSelect2 = null;
		if (!_isInit) {
			init();
		}

		if (nextIdSelect == null) {
			nextIdSelect = _resource.getString("NextIdSelect");
		}

		if (nextIdSelect.equalsIgnoreCase("native")) {
			return "native";
		}

		nextIdSelect2 = StringUtils.replaceToken(nextIdSelect, "@table", in_sTableName, 1);
		nextIdSelect2 = StringUtils.replaceToken(nextIdSelect2, "@field", in_sPkFieldName, 1);

		return nextIdSelect2;

	}

	/**
	 * 
	 * Converts a date to string according to specified format
	 * (DbSpecific.properties - ObjectDatePattern)
	 * 
	 */
	public String convertDateToString(Date in_dtDate) {
		return _dtFormatter.format(in_dtDate);
	}

	/**
	 * 
	 * Return the sql representation to be abble to do myDate = otherDate
	 * 
	 * <pre>
	 * For Oracle : TO_DATE('DD/MM/YYYY HH24:MI' , '10/08/2002 23:54') from the date object
	 * 
	 * </pre>
	 * 
	 * @param in_dtDate
	 *            The date to be converted
	 * @return The SQL String
	 * 
	 */
	public String convertDateToSqlString(Date in_dtDate) {
		String sConverter = getStringToDateConverter();
		return StringUtils.replaceToken(sConverter, "@", convertDateToString(in_dtDate));
	}

	public void main(String[] args) {
		System.out.println(getNextIdSelect("event", "event_id"));
		System.out.println(convertDateToSqlString(new Date()));

	}

	public void setDatabaseType(String databaseType2) {
		databaseType = databaseType2;
	}

	public String getLastIdSelect(String tableName) {

		if (selectIdAfterInsert == null) {
			selectIdAfterInsert = _resource.getString("LastAutoIncrementId");
		}
		String s = selectIdAfterInsert;
		s = StringUtils.replaceToken(s, "@", tableName, 1);
		return s;
	}
	
	public boolean usePreparedStatement() {
		return usePreparedStatement;
	}
	
	public void setUsePreparedStatement(boolean usePreparedStatement) {
		this.usePreparedStatement = usePreparedStatement;
	}
}
