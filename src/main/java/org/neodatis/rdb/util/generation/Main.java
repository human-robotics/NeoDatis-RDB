package org.neodatis.rdb.util.generation;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Description : Generates java file matching database tables all description
 * must be available in classes.properties file
 * 
 * @author : Olivier Smadja - osmadja@netcourrier.com
 * @version : date - 07/08/2001
 */

public class Main {
	/** To store properties */
	Properties _properties;

	/** The name of the db . can be oracle, jdbc */
	String dbType;

	/** The directory where to build the source files */
	String sourceDirectory;

	/** To store the number of managed table */
	int _nNbTables;

	/** To check object status */
	boolean _bIsOk;

	/**
	 * Constructor
	 * 
	 * @param String
	 *            The name of the input file
	 * @param String
	 *            The name of the db : oracle, jdbc
	 */
	public Main(String directory, String in_sFileName, String in_sDb) {
		init(directory, in_sFileName, in_sDb);
	}

	/**
	 * @param String
	 *            The name of the input file
	 * @param String
	 *            The name of the db : oracle, jdbc
	 */
	void init(String directory, String in_sFileName, String in_sDb) {
		_bIsOk = true;
		dbType = in_sDb;
		sourceDirectory = directory;

		System.out.println("Database type='" + dbType + "'");
		System.out.println("Source Directory=" + sourceDirectory);
		InputStream is = getClass().getResourceAsStream(in_sFileName);

		_properties = new Properties();

		try {
			_properties.load(is);
		} catch (Exception e) {
			System.out.println("Error while getting the file " + in_sFileName);
			System.out.println("Check if the file exist in directory : " + System.getProperty("user.dir"));
			_bIsOk = false;
		}

		_nNbTables = 0;
	}

	/** Manages all config file tables */
	public void manageTables() throws Exception {
		// Gets all properties
		Enumeration tables = _properties.propertyNames();

		// To keep table name
		String sTableName = null;

		// To keep the class description
		String sClassDescription = null;

		int nbClasses = 0;
		// parse all properties : all tables
		while (tables.hasMoreElements()) {
			// Gets the table name
			sTableName = (String) tables.nextElement();

			// Gets the class description for this table
			sClassDescription = _properties.getProperty(sTableName);

			// Manages this table
			manageOneTable(sTableName, sClassDescription);

			nbClasses++;
		}

		System.out.println(nbClasses + " classes created sucessfully!");
	}

	/**
	 * Manages one specific table
	 * 
	 * @param String
	 *            The table name
	 * @param String
	 *            The class description
	 */
	public void manageOneTable(String in_sTableName, String in_sClassDescription) throws Exception{
		System.out.println("\n" + (++_nNbTables) + ") Table = " + in_sTableName + " Class Desc =" + in_sClassDescription);

		ClassBuilderFromDb builder = null;
		// JavaClassBuilderFromDbOracle builder = null;
		String[] tokens = in_sClassDescription.split(",");
		boolean done = false;
		String sTableName = in_sTableName;
		String sPackage = tokens[0];
		String sClassName = tokens[1];
		String sKeyFieldName = tokens[2];

		// The default is to extends DbObject
		String sBaseClassName = null;
		String sHelperClassPackage = null;

		sBaseClassName = tokens[3];
		sHelperClassPackage = tokens[4];

		if (dbType.equalsIgnoreCase("oracle")) {
			builder = new JavaClassBuilderFromDbOracle(sourceDirectory, sPackage, sClassName, sTableName, sKeyFieldName, sBaseClassName, sHelperClassPackage);
			done = true;
		}
		if (dbType.equalsIgnoreCase("jdbc")) {
			builder = new JavaClassBuilderFromDbJDBC(sourceDirectory, sPackage, sClassName, sTableName, sKeyFieldName, sBaseClassName, sHelperClassPackage);
			done = true;
		}
		if (!done) {
			System.out.println("Invalid database type " + dbType + " => not doing nothing");
		}
	}

	/** To checks object status */
	public boolean isOk() {
		return _bIsOk;
	}

	static public void displayHelpMessage() {
		System.out.println("\nNeoDatis Class Builder for Java 2.0\n");
		System.out
				.println("Must pass 3 arguments : \n\t1) The directory where to create the source files \n\t2) The config file name (beginning with /)");
		System.out.println("\n\t3) The type of database access : Describing classes to be generated and the database type : oracle or jdbc");
		System.out.println("ex : src test.properties oracle");
		System.out.println("This file must contains the following data");
		System.out.println("<TableName>=<PackageName>,<ClassName>,<PrimaryKeyFieldName><mother class - optional>");
		System.out.println("One line must be defined for each table to be matched into a java class");
	}

	public static void main(String[] args) throws Exception {

		if (args.length != 3) {
			displayHelpMessage();
		} else {
			Main builder = new Main(args[0], args[1], args[2]);
			if (builder.isOk()) {
				builder.manageTables();
			} else {
				System.out.println("Problem with builder");
			}

		}

	}
}
