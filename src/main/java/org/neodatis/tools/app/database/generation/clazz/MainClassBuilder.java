package org.neodatis.tools.app.database.generation.clazz;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.neodatis.tools.app.Config;
import org.neodatis.tools.app.NeoDatis;
import org.neodatis.tools.app.database.generation.xml.JavaClassBuilderFromXML;
import org.neodatis.tools.app.model.Attribute;
import org.neodatis.tools.app.model.Concept;
import org.neodatis.tools.app.model.NeoDatisAppData;

/**
 * Description : Generates java file matching database tables all description
 * must be available in classes.properties file
 * 
 * @author : Olivier Smadja - osmadja@netcourrier.com
 * @version : date - 07/08/2001
 */

public class MainClassBuilder {

	/** The directory where to build the source files */
	String sourceDirectory;

	List<Concept> concepts;

	/** To store the number of managed table */
	int _nNbTables;

	/** To check object status */
	boolean _bIsOk;

	/**
	 * Constructor
	 * 
	 * @param directoryToCreateFiles
	 * @param concepts
	 */
	public MainClassBuilder(String directoryToCreateFiles, List<Concept> concepts) {
		init(directoryToCreateFiles, concepts);
	}

	/**
     * @param directoryToCreateFiles
     * @param concepts
	 */
	void init(String directoryToCreateFiles, List<Concept> concepts) {
		this._bIsOk = true;
		this.sourceDirectory = directoryToCreateFiles;
		this.concepts = concepts;
		System.out.println("Source Directory=" + sourceDirectory);
		this._nNbTables = 0;
	}

	/** Manages all config file tables */
	public void manageTables() throws Exception {

		int nbClasses = 0;
		// parse all properties : all tables
		for (Concept concept : concepts) {
			// Manages this table
			manageOneTable(concept);

			nbClasses++;
		}

		buildIndexClass(concepts, sourceDirectory);
		System.out.println(nbClasses + " classes created sucessfully!");
	}

	private void buildIndexClass(List<Concept> concepts2, String sourceDirectory) throws IOException {
		String packageName = concepts2.get(0).getPackageName();
		
		StringBuilder b = new StringBuilder();
		b.append("package ").append(packageName).append(";");
		b.append("\npublic class ").append("Index__{\n");
		
		StringBuilder bList = new StringBuilder();
		bList.append("\n\n\tpublic static java.util.List<String> allClasses = new java.util.ArrayList();");
        bList.append("\n\n\tpublic static java.util.Map<String,String> conceptsByClass = new java.util.HashMap<String,String>();");
		bList.append("\n\n\tstatic {");
		
		for(Concept c:concepts2){
			b.append("\n\tpublic String ").append(c.getName()).append(" = \"").append(c.getFullClassName()).append("\";");
            bList.append("\t\tallClasses.add(\"").append(c.getFullClassName()).append("\");\n");
            bList.append("\t\tconceptsByClass.put(\"").append(c.getFullClassName()).append("\",\"").append(c.getClassName()).append("\");\n");
		}
		bList.append("\t}");
        b.append(bList);
		b.append("\n}");

        String fileName = sourceDirectory+"/"+ packageName.replaceAll("\\.", "/")+"/Index__.java";
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(b.toString().getBytes());
        fos.close();

        System.out.println("Index file "+ fileName+" created");
		
	}

	/**
	 * Manages one specific table
	 * 
	 * @param concept
	 */
	public void manageOneTable(Concept concept) throws Exception {
		System.out.println("\n" + (++_nNbTables) + ") Table = " + concept.getName());

		String packageName = "org.neodatis.vo"; 
			 
		ClassBuilderFromDb builder = null;
		if ( concept.getPackageName()!= null) {
			packageName = concept.getPackageName();
		}
		Attribute primaryKey = concept.getConceptPrimaryKey();
		
		concept.setPackageName(packageName);
		concept.setHelperPackageName(packageName+".meta");
		

		if (primaryKey == null) {
			if (!Config.acceptTableWithoutPrimaryKey) {
				throw new RuntimeException("Table " + concept.getName() + " does not have primary key\n" + concept.getAttributes());
			} else {
				System.out.println("Table " + concept.getName() + " does not have primary key, assuming " + concept.getAttributes().get(0).getName());
				concept.getAttributes().get(0).setPrimaryKey(true);
			}
		}

		builder = new JavaClassBuilderFromXML(sourceDirectory, concept, null);
	}

	/** To checks object status */
	public boolean isOk() {
		return _bIsOk;
	}

	static public void displayHelpMessage() {
		System.out.println("\nNeoDatis Class Builder for Java 2.0\n");
		System.out.println("Must pass 2 arguments : \n\t1) The directory where to create the source files \n\t2) The config file name (beginning with /)");
		
		System.out.println("ex : src test.properties oracle");
		System.out.println("This file must contains the following data");
		System.out.println("<TableName>=<PackageName>,<ClassName>,<PrimaryKeyFieldName><mother class - optional>");
		System.out.println("One line must be defined for each table to be matched into a java class");
	}

	public static void main(String[] args) throws Exception {

		if (args.length != 2) {
			displayHelpMessage();
		} else {
			String xmlFile = args[0];
			String directoryToCreateFiles = args[1];
			//String packageName = args[2];
			System.out.println("Loading application data from "+ xmlFile);
			NeoDatisAppData app = NeoDatis.loadApplicationData(xmlFile);
			MainClassBuilder builder = new MainClassBuilder(directoryToCreateFiles, app.getConcepts());
			if (builder.isOk()) {
				builder.manageTables();
			} else {
				System.out.println("Problem with builder");
			}
		}

	}
}
