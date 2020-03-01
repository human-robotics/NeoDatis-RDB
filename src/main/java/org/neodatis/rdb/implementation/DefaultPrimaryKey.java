/*
 * Created by IntelliJ IDEA.
 * User: Olivier
 * Date: 09/07/2002
 * Time: 18:20:19
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.neodatis.rdb.implementation;

import java.lang.reflect.Field;

import org.apache.log4j.Category;
import org.neodatis.rdb.DbObjectMapping;
import org.neodatis.rdb.PrimaryKey;
import org.neodatis.rdb.RDB;

public class DefaultPrimaryKey implements PrimaryKey {

	/** Creates the root */
	static Category _log = Category.getInstance(DefaultPrimaryKey.class
			.getName());

	String _sPrimaryKeyFieldName;

	public DefaultPrimaryKey(String in_sPrimaryKeyFieldName) {
		_sPrimaryKeyFieldName = in_sPrimaryKeyFieldName;
	}

	public String toString() {
		return _sPrimaryKeyFieldName;
	}

	/**
	 * Return the SQL of the primary
	 * 
	 * @param The
	 *            object Example "CLIENT_ID = 10"
	 */
	public String getSql(DbObjectMapping in_object)
			throws NoSuchFieldException, IllegalAccessException {
		String sFieldName = RdbReflection._DATABASE_FIELD_PREFIX
				+ _sPrimaryKeyFieldName;

		RdbReflection rdbReflection = new RdbReflection();

		_log.debug("Getting primary key field : " + sFieldName + " for "
				+ in_object.toString());

		StringBuffer sSql = new StringBuffer();
		Class objectClass = in_object.getClass();

		_log.debug("Class is " + objectClass.getName());

		Field field = rdbReflection.getField(sFieldName, objectClass);
		// Prevent from IllegalAccessException
		field.setAccessible(true);
		sSql.append(Util.getColumnName(_sPrimaryKeyFieldName)).append(" = ")
				.append(rdbReflection.getStringFieldValue(field, in_object));

		return sSql.toString();
	}

	/**
	 *
	 * Returns the value of the primary key field
	 *
	 * @param The
	 *            object
	 * @return The primary key value
	 *
	 */
	public Object getValue(DbObjectMapping in_object) throws Exception {
		String sFieldName = RdbReflection._DATABASE_FIELD_PREFIX
				+ _sPrimaryKeyFieldName;

		RdbReflection rdbReflection = new RdbReflection();

		Class objectClass = in_object.getClass();

		Field field = rdbReflection.getField(sFieldName, objectClass);

		Object fieldValue = rdbReflection.getExactFieldValue(field, in_object);

		return fieldValue;
	}

	/**
	 *
	 * Sets the value of the primary key
	 *
	 * @param The
	 *            primary key value - for instance , it only supports Long
	 *            values
	 *
	 */
	public void setValue(DbObjectMapping in_object, Long in_value)
			throws Exception {
		String sFieldName = RdbReflection._DATABASE_FIELD_PREFIX
				+ _sPrimaryKeyFieldName;

		RdbReflection rdbReflection = new RdbReflection();

		Class objectClass = in_object.getClass();

		Field field = rdbReflection.getField(sFieldName, objectClass);

		// Prevent from IllegalAccessException
		field.setAccessible(true);

		field.set(in_object, in_value);

	}

	/**
	 *
	 * Retuns the name of the primary key
	 *
	 */
	public String getName() {
		return _sPrimaryKeyFieldName;
	}

}
