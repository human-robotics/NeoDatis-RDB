package org.neodatis.rdb.implementation;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Category;
import org.neodatis.rdb.DbObjectMapping;
import org.neodatis.tools.StringUtils;

public class RdbReflection {
	static Category _log = Category.getInstance(RdbReflection.class.getName());
	public final static String _DATABASE_FIELD_PREFIX = "db";
	/** The Date format to be used */
	protected SimpleDateFormat dateToStringFormatter = null;

	
	public RdbReflection(){
		dateToStringFormatter = new SimpleDateFormat(DbSpecific.get().getObjectDatePattern());
		dateToStringFormatter.setLenient(false);
	}
	
	/**
	 * Gets the field value in a string literal type
	 */
	String getStringFieldValue(Field field, DbObjectMapping dbObjectMapping) {
		// Prevent from IllegalAccessException
		field.setAccessible(true);

		// System.out.println("field value for "+in_field.getName() );

		String sFieldValue = null;
		StringBuffer sReturnFieldValue = new StringBuffer();

		// Gets the class type
		Class classType = field.getType();

		// Gets the object representing the field
		Object obj = null;
		try {
			obj = field.get(dbObjectMapping);

			if(obj==null){
				return "null";
			}
			// this will trigger exception if object is null
			sFieldValue = obj.toString();
		} catch (IllegalAccessException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			_log.error(sw.toString());

			_log.error("Continuing");
			return "null";
		} finally {
			field.setAccessible(false);
		}

		// System.out.println("class name " + classType.getName() );
		// Formatting according field type
		if (classType.getName().endsWith("String")) {
			//String s1 = StringUtils.replaceToken(sFieldValue, "'", "%");
			// Replaces ' by '' to avoid sql errors
			//return new StringBuffer("'").append(StringUtils.replaceToken(s1, "%", "''")).append("'").toString();
			return new StringBuffer("'").append(sFieldValue).append("'").toString();
		}

		if (classType.getName().endsWith("char")) {

			return new StringBuffer("'").append(sFieldValue).append("'").toString();
		}
		if (classType.getName().endsWith("boolean")) {

			return sFieldValue.toString();
		}

		if (classType.getName().endsWith("Date")) {
			try {
				_log.debug("Date before transform=" + ((Date) obj).toString());

				String sDateTime = dateToStringFormatter.format((Date) obj);
				// Gets the pattern of the converter
				String sStringToDateConverter = DbSpecific.get().getStringToDateConverter();

				// replace the variable and returns the value
				String sConverter = StringUtils.replaceToken(sStringToDateConverter, "@", sDateTime);
				_log.debug("The date Converter is : " + sConverter);
				return sConverter;
				// return
				// "TO_DATE('"+sDateTime+"','"+DbSpecific.get().getDbDatePattern()
				// +"')";
			} catch (Throwable e) {
				return "null";
			}
		}

		// Formatting according field type
		if (classType.getName().endsWith("boolean")) {

			return (sFieldValue.equals("true") ? "1" : "0");
		}

		// For NaN problem
		if (classType.getName().endsWith("double") || classType.getName().endsWith("int") || classType.getName().endsWith("float")
				|| classType.getName().endsWith("short")) {
			if (sFieldValue.equals("NaN")) {
				sFieldValue = "null";
			}
		}

		return sFieldValue;
	}
	/**
	 * Gets all the fields for the simple select
	 * 
	 * @param The
	 *            Simple select
	 * @return An array of Field
	 * */
	public Field[] getFieldsOf(Class in_class) {

		boolean bOk = false;
		boolean bAbort = false;
		int nFieldsNumber = 0;

		// gets the current class
		Class aClass = in_class;

		// Creates a vector to store our database fields
		List asFields = new ArrayList();

		// To store fields
		Field[] fields = null;

		// While we do not find the database fields
		// we will analyse the super class. If the
		// super class does not any database fields
		// then we will anilyse the super class of
		// the super class
		while (!bOk && !bAbort) {

			// Gets the fields
			fields = aClass.getDeclaredFields();

			for (int nField = 0; nField < fields.length; nField++) {
				if (fields[nField].getName().startsWith(_DATABASE_FIELD_PREFIX)) {
					asFields.add(fields[nField]);
				}
			}

			nFieldsNumber = asFields.size();

			if (nFieldsNumber != 0) {
				bOk = true;
			} else {
				_log.debug("Class " + aClass.getName() + " do not have fields!!");

				// If no fields where found then analyse super class
				aClass = aClass.getSuperclass();

				if (aClass == Object.class) {
					bAbort = true;
					_log.error("Superclass is Object => do not have any database field");
				}

			}
		}

		// System.out.println(" Nb fields = " + _nFieldsNumber);

		Field[] fieldsToReturn = new Field[nFieldsNumber];

		fieldsToReturn = (Field[]) asFields.toArray(fieldsToReturn);

		return fieldsToReturn;
	}
	
	/**
	 * 
	 * Gets the field value - Return the object
	 * 
	 * @param The
	 *            field
	 * @param The
	 *            object to get the field
	 * @return The object contained in the field - null if field is null
	 * @exception IllegalAccessException
	 *                if The field can not be accessed
	 * 
	 */
	Object getExactFieldValue(Field in_field, DbObjectMapping in_dbObjectMapping) throws IllegalAccessException {
		// Prevent from IllegalAccessException
		in_field.setAccessible(true);

		return in_field.get(in_dbObjectMapping);

	}

	/**
	 * Gets The database field with the specified name
	 * 
	 * @param The
	 *            name of the field
	 * @return The Field
	 * @exception NoSuchFieldException
	 *                if field does not exist
	 * */
	public Field getField(String in_sFieldName, Class in_class) throws NoSuchFieldException {
		Field[] fields = getFieldsOf(in_class);

		for (int nField = 0; nField < fields.length; nField++) {
			if (fields[nField].getName().equals(in_sFieldName)) {
				return fields[nField];
			}
		}
		throw new NoSuchFieldException(in_sFieldName + " on class " + in_class.getName());
	}

	

	/**
	 * Returns The field name without prefix
	 * 
	 * @param The
	 *            field
	 * @return The field name without the Database prefix (db)
	 */
	public String getRealFieldName(Field in_field) {
		return in_field.getName().substring(2, in_field.getName().length());
	}
	
	boolean fieldIsNull(Field field, DbObjectMapping dbObjectMapping) throws IllegalArgumentException, IllegalAccessException {
		// Prevent from IllegalAccessException
		field.setAccessible(true);


		// Gets the object representing the field
		Object obj = null;
		obj = field.get(dbObjectMapping);
		return obj==null;
	}

	public Object getFieldValue(Field field, DbObjectMapping o) throws IllegalArgumentException, IllegalAccessException {
		field.setAccessible(true);
		return field.get(o);
	}
}
