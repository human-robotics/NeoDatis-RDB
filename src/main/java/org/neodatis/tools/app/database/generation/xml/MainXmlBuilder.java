package org.neodatis.tools.app.database.generation.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.neodatis.rdb.implementation.DatabaseColumn;
import org.neodatis.rdb.implementation.DatabaseMetaInformation;
import org.neodatis.tools.app.database.generation.clazz.ClassBuilderFromDb;

/**
 * Description : Generates java file matching database tables all description
 * must be available in classes.properties file
 * 
 * @author : Olivier Smadja - osmadja@gmail.com
 * @version 2012
 */

public class MainXmlBuilder {
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
	public MainXmlBuilder(String appName, String directory, String fileName, String packageName, Filter filter, String interfaceNameToImplement,
			String classNameToExtend) {
		init(appName, directory, fileName, packageName, filter, interfaceNameToImplement, classNameToExtend);

	}

	/**
	 * @param String
	 *            The name of the input file
	 * @param String
	 *            The name of the db : oracle, jdbc
	 */
	void init(String appName, String directory, String outputXmlFileName, String packageName, Filter filter, String interfaceNameToImplement,
			String classNameToExtend) {
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

		DatabaseMetaInformation databaseMetaInformation = new DatabaseMetaInformation();
		List<String> tables = databaseMetaInformation.getTableNames(null, DatabaseMetaInformation.TABLE_TYPE_TABLE);
		List<String> views = databaseMetaInformation.getTableNames(null, DatabaseMetaInformation.TABLE_TYPE_VIEW);
		
		List<String> objects = new ArrayList<String>();
		objects.addAll(tables);
		objects.addAll(views);
		
		int nbClasses = 0;
		// parse all properties : all tables
		for (String objectName : objects) {

			if (filter == null || (filter!= null && filter.match(objectName))) {
				// Manages this table
				manageOneTable(objectName, databaseMetaInformation);
				nbClasses++;
			}

		}
		databaseMetaInformation.close();

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
	public void manageOneTable(String tableName, DatabaseMetaInformation databaseMetaInformation) throws Exception {
		System.out.println("\n" + (++nbTables) + ") Table = " + tableName);

		ClassBuilderFromDb builder = null;
		// JavaClassBuilderFromDbOracle builder = null;

		String schema = null;
		if(tableName.indexOf(".")!=-1){
			String[] tokens = tableName.split("\\.");
			schema = tokens[0];
			tableName = tokens[1];
		}
		List<DatabaseColumn> fields = databaseMetaInformation.getTableColumnsGeneric(schema, tableName);
		
		if(xmlGenerator!=null){
			xmlGenerator.newTable(schema, tableName, fields,packageName);
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

	public static void main(String[] args1) throws Exception {

		String[] args = {"NeoDatis", "src-generated", "neodatis.xml"};
		
		if (args.length != 3) {
			displayHelpMessage();
		} else {
			String appName = args[0];
			String directoryToCreateSource = args[1];
			String xmlFileName = args[2];
			String packageName = "default";
			MainXmlBuilder builder = new MainXmlBuilder(appName, directoryToCreateSource, xmlFileName, packageName, null, null, null);
			if (builder.isOk()) {
				builder.manageTables();
			} else {
				System.out.println("Problem with builder");
			}

		}

	}
}
