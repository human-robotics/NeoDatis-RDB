package org.neodatis.rdb.util.generation;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.neodatis.rdb.implementation.DatabaseMetaInformation;

/**
 * Description : Generates java file matching database tables all description
 * must be available in classes.properties file
 * 
 * @author : Olivier Smadja - osmadja@gmail.com
 * @version 2012
 */

public class MainXml {
	/** To store properties */
	Properties _properties;

	/** The name of the db . can be oracle, jdbc */
	String dbType;

	/** The directory where to build the source files */
	String sourceDirectory;
	String xmlOutputFileName;
	String packageName;

	/** To store the number of managed table */
	int nbTables;

	/** To check object status */
	boolean isOk;

	protected XmlGenerator xmlGenerator;
	protected Filter filter;
	protected String interfaceNameToImplement;

	/**
	 * Constructor
	 * 
	 * @param String
	 *            The name of the input file
	 * @param String
	 *            The name of the db : oracle, jdbc
	 */
	public MainXml(String appName, String directory, String fileName, String packageName, Filter filter, String interfaceNameToImplement , String classNameToExtend) {
		init(appName, directory, fileName, packageName, filter, interfaceNameToImplement, classNameToExtend);
		
	}

	/**
	 * @param String
	 *            The name of the input file
	 * @param String
	 *            The name of the db : oracle, jdbc
	 */
	void init(String appName, String directory, String outputXmlFileName, String packageName, Filter filter, String interfaceNameToImplement , String classNameToExtend ) {
		isOk = true;
		this.packageName = packageName;
		this.xmlOutputFileName = outputXmlFileName;
		this.filter = filter;
		System.out.println("Database type='" + dbType + "'");
		System.out.println("Source Directory=" + sourceDirectory);
		System.out.println("Xml Output File name=" + xmlOutputFileName);
		xmlGenerator = new XmlGenerator(appName, interfaceNameToImplement, classNameToExtend);
		nbTables = 0;
	}

	/** Manages all config file tables */
	public void manageTables() throws Exception {

		List<String> tables = new DatabaseMetaInformation().getTableNames(null, DatabaseMetaInformation.TABLE_TYPE_TABLE);

		int nbClasses = 0;
		// parse all properties : all tables
		for (String tableName : tables) {
			
			if(filter!=null && filter.match(tableName)){
				// Manages this table
				manageOneTable(tableName);
				nbClasses++;
			}
			
		}

		System.out.println(nbClasses + " classes created sucessfully!");
		xmlGenerator.close();
		xmlGenerator.toFile(xmlOutputFileName);

	}

	/**
	 * Manages one specific table
	 * 
	 * @param String
	 *            The table name
	 * @param String
	 *            The class description
	 */
	public void manageOneTable(String tableName) throws Exception {
		System.out.println("\n" + (++nbTables) + ") Table = " + tableName);

		ClassBuilderFromDb builder = null;
		// JavaClassBuilderFromDbOracle builder = null;
		String sClassName = tableName;
		String sKeyFieldName = "id";

		// The default is to extends DbObject
		String sBaseClassName = null;
		String sHelperClassPackage = null;

		sBaseClassName = "Object";
		sHelperClassPackage = packageName + ".meta";
		boolean done = false;
		dbType = "jdbc";

		if (dbType.equalsIgnoreCase("oracle")) {
			builder = new JavaClassBuilderFromDbOracle(sourceDirectory, packageName, sClassName, tableName, sKeyFieldName, sBaseClassName, sHelperClassPackage);
			done = true;
		}
		if (dbType.equalsIgnoreCase("jdbc")) {
			builder = new JavaClassBuilderFromDbJDBC(sourceDirectory, packageName, sClassName, tableName, sKeyFieldName, sBaseClassName, sHelperClassPackage,
					xmlGenerator);
			done = true;
		}
		if (!done) {
			System.out.println("Invalid database type " + dbType + " => not doing nothing");
		}
	}

	/** To checks object status */
	public boolean isOk() {
		return isOk;
	}

	public String getInterfaceNameToImplement() {
		return interfaceNameToImplement;
	}

	public void setInterfaceNameToImplement(String interfaceNameToImplement) {
		this.interfaceNameToImplement = interfaceNameToImplement;
	}

	static public void displayHelpMessage() {
		System.out.println("\nNeoDatis XML Builder for Java 2.0\n");
		System.out.println("Must pass 2 arguments : \n\t1) App Name \n\t2) The directory where to create the source files \n\t3) The XML output file name");

	}

	public static void main(String[] args) throws Exception {

		if (args.length != 3) {
			displayHelpMessage();
		} else {
			String appName = args[0];
			String directoryToCreateSource = args[1];
			String xmlFileName = args[2];
			String packageName = "default";
			MainXml builder = new MainXml(appName, directoryToCreateSource, xmlFileName, packageName, null,null,null);
			if (builder.isOk()) {
				builder.manageTables();
			} else {
				System.out.println("Problem with builder");
			}

		}

	}
}
