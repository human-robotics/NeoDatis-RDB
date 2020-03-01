package org.neodatis.tools.app.database.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.neodatis.rdb.implementation.DatabaseColumn;
import org.neodatis.rdb.implementation.DatabaseMetaInformation;
import org.neodatis.tools.app.database.generation.clazz.ClassBuilderFromDb;
import org.neodatis.tools.app.database.generation.xml.Filter;
import org.neodatis.tools.app.model.Attribute;
import org.neodatis.tools.app.model.Concept;

/**
 * 
 * @author : Olivier Smadja - osmadja@gmail.com
 * @version 2014
 */

public class DatabaseInfoRetriever {
	/** To store properties */
	Properties _properties;

	/** The name of the db . can be oracle, jdbc */
	String dbType;

	String packageName;

	/** To store the number of managed table */
	int nbTables;

	/** To check object status */
	boolean isOk;

	protected Filter filter;
	protected String interfaceNameToImplement;
	
	List<Concept> concepts;

	/**
	 * Constructor
	 * 
	 * @param String
	 *            The name of the input file
	 * @param String
	 *            The name of the db : oracle, jdbc
	 */
	public DatabaseInfoRetriever(Filter filter) {
		init(filter);

	}

	/**
	 * @param String
	 *            The name of the input file
	 * @param String
	 *            The name of the db : oracle, jdbc
	 */
	void init(Filter filter) {
		isOk = true;
		this.filter = filter;
		concepts = new ArrayList<Concept>();
		
		nbTables = 0;
	}

	/** Manages all config file tables */
	public void manageTables() throws Exception {

		List<String> tables = new DatabaseMetaInformation().getTableNames(null, DatabaseMetaInformation.TABLE_TYPE_TABLE);

		int nbClasses = 0;
		// parse all properties : all tables
		for (String tableName : tables) {

			if (filter == null || (filter!= null && filter.match(tableName))) {
				// Manages this table
				manageOneTable(tableName);
				nbClasses++;
			}

		}

		System.out.println(nbClasses + " concepts retrieved sucessfully!");

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

		String schema = null;
		if(tableName.indexOf(".")!=-1){
			String[] tokens = tableName.split("\\.");
			schema = tokens[0];
			tableName = tokens[1];
		}
		List<Attribute> attributes = new ArrayList<Attribute>();
		List<DatabaseColumn> fields = new DatabaseMetaInformation().getTableColumnsGeneric(schema, tableName);
		for(DatabaseColumn dc:fields) {
			Attribute attribute = new Attribute(dc.getName(), dc.getDataTypeAsString(), dc.isAllowNull(), dc.isPrimaryKey(), dc.getForeignKeyInformation());
			attributes.add(attribute);
		}
		Concept concept = new Concept(tableName, attributes);
		concepts.add(concept);
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

	public List<Concept> getConcepts() {
		return concepts;
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
			DatabaseInfoRetriever builder = new DatabaseInfoRetriever(null);
			if (builder.isOk()) {
				builder.manageTables();
			} else {
				System.out.println("Problem with builder");
			}
			
			System.out.println("AllConcepts:"+ builder.getConcepts());

		}

	}
}
