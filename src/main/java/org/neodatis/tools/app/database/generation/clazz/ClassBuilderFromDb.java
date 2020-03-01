package org.neodatis.tools.app.database.generation.clazz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.neodatis.tools.StringUtils;
import org.neodatis.tools.app.model.Attribute;
import org.neodatis.tools.app.model.Concept;

/**
 * Description : Builds a java file containing a java class templating a
 * database table , Builds also a Helper class representing the table with all
 * its attribites
 * 
 * @author : Olivier Smadja - osmadja@gmail.com
 * @date : 16/08/2000 - creation
 * @date 13/01/2001 - Removes the toUpper of the table name. MySql compatibility
 * @version 09/07/2002 - Changes The packages - and generates simples classes -
 *          data bean
 */

public abstract class ClassBuilderFromDb {
	protected static Logger logger = Logger.getLogger(ClassBuilderFromDb.class);
	public final String _DATABASE_FIELD_PREFIX = "db";

	private String sourceDirectory;

	/** To Write The main java class to file */
	private PrintStream mainClassStream;

	/** To Write The helper java class to file */
	private PrintStream helperClassStream;

	protected Concept concept;

	protected CodeGenerationListener codeGenerationListener;

	public ClassBuilderFromDb(String directory, Concept concept, CodeGenerationListener codeGenerator) throws Exception {
		init(directory, concept, codeGenerator);
	}

	/**
	 * Initialisation of the object
	 * 
	 * @param codeGenerator
	 * @throws Exception
	 */
	private void init(String directory, Concept concept, CodeGenerationListener codeGenerator) throws Exception {
		this.concept = concept;
		this.sourceDirectory = directory;
		logger.info("Src Directory = " + sourceDirectory);
		logger.info("Table     = " + concept.getName());
		logger.info("Class     = " + concept.getClassName());
		logger.info("PrimaryKey  = " + concept.getConceptPrimaryKey());
		logger.info("Package   = " + concept.getPackageName());
		logger.info("BaseClass = " + concept.getConceptExtends());
		logger.info("Interface = " + concept.getConceptImplements());
		logger.info("Helper Package = " + concept.getHelperPackageName());

		// To respect file table name
		codeGenerationListener = codeGenerator;
		this.sourceDirectory = directory;
		createClasses();
	}

	/**
	 * Execute class creation
	 * 
	 * @throws Exception
	 */
	public void createClasses() throws Exception {

		initStreams();

		try {
			// Builds the main java class
			buildMainClass();

			// Builds the helper class
			buildHelperClass();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		closeFile();

	}

	void closeFile() {
		if (mainClassStream != null) {
			mainClassStream.close();
		}
	}

	void initStreams() {

		String sMainClassFileName = sourceDirectory + "/" + concept.getPackageName().replace('.', '/') + "/" + concept.getClassName() + getFileExtension();
		String sHelperClassName = sourceDirectory + "/" + concept.getHelperPackageName().replace('.', '/') + "/" + concept.getClassName() + "DBHelper"
				+ getFileExtension();

		mainClassStream = buildOneStream(sMainClassFileName);
		helperClassStream = buildOneStream(sHelperClassName);

	}

	protected PrintStream buildOneStream(String fileName) {
		// Gets the abstract file to checks if exists
		File abstractFile = new File(fileName);
		PrintStream stream = null;

		// If the file exists , deletes it
		if (abstractFile.exists()) {
			abstractFile.delete();

			/*
			 * // Builds the new File name String sNewFileName = fileName +
			 * ".backup";
			 * 
			 * // Builds the abstract object File abstractFile2 = new File(
			 * sNewFileName );
			 * 
			 * // Renames the file boolean bResult =
			 * abstractFile.renameTo(abstractFile2);
			 * 
			 * if( bResult ) {
			 * System.out.println("File "+fileName+" has been renamed to "+
			 * sNewFileName); } else {
			 * System.out.println("Unable to rename file "+fileName ); }
			 */
		}

		File parent = abstractFile.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}

		try {
			// Creates the object to write logs
			stream = new PrintStream(new FileOutputStream(fileName, false));

		}

		catch (FileNotFoundException e) {
			System.out.println("Error while creating " + fileName);
		}

		return stream;
	}

	/**
	 * Gets the file in which will be written the class
	 * 
	 * @return PrintStream The stream to write
	 */
	PrintStream getMainFile() {
		return mainClassStream;
	}

	/**
	 * Gets the file in which will be written the helper class
	 * 
	 * @return PrintStream The stream to write
	 */
	PrintStream getHelperFile() {
		return helperClassStream;
	}

	/**
	 * Retrieves table infos and save info in Vectors
	 * 
	 * @throws Exception
	 */
	abstract public void getTableInfos() throws SQLException, Exception;

	/** Builds the Setter method for the in_nIndex field */
	String buildSetter(int in_nIndex) {
		Attribute attribute = concept.getAttributes().get(in_nIndex);
		String sDbFieldName = attribute.getName();
		String sFieldName = StringUtils.capitalize(sDbFieldName, true);
		String sMethodName = StringUtils.capitalize(sDbFieldName, false);

		StringBuffer sMethod = new StringBuffer();
		sMethod.append("\n\t// Setter for attribute ");
		sMethod.append(sFieldName);
		sMethod.append("\n\tpublic void set");
		sMethod.append(sMethodName);
		sMethod.append("( ");
		sMethod.append(attribute.getType());
		sMethod.append(" in_");
		sMethod.append(sFieldName);
		sMethod.append(" ) \n");
		sMethod.append("\t{\n");
		sMethod.append("\t\t").append(_DATABASE_FIELD_PREFIX);
		sMethod.append(sDbFieldName);
		sMethod.append(" = in_");
		sMethod.append(sFieldName);
		sMethod.append(";\n");
		sMethod.append("\t}");

		return sMethod.toString();

	}

	/** Builds the Getter method for the in_nIndex field */
	String buildGetter(int in_nIndex) {
		Attribute attribute = concept.getAttributes().get(in_nIndex);
		String sDbFieldName = attribute.getName();
		String sFieldName = StringUtils.capitalize(sDbFieldName, true);
		String sMethodName = StringUtils.capitalize(sDbFieldName, false);

		StringBuffer sMethod = new StringBuffer();

		sMethod.append("\n\t// Getter for attribute ");
		sMethod.append(sFieldName);
		sMethod.append("\n\tpublic ");
		sMethod.append(attribute.getType());
		sMethod.append(" get");
		sMethod.append(sMethodName);
		sMethod.append("()\n");
		sMethod.append("\t{\n");
		sMethod.append("\t\treturn ");
		sMethod.append(_DATABASE_FIELD_PREFIX);
		sMethod.append(sDbFieldName);
		sMethod.append(";\n");
		sMethod.append("\t}");

		return sMethod.toString();

	}

	public void buildMainClass() throws Exception {
		getTableInfos();
		buildClassHeader();
		buildClassAttributes();
		buildDbObject();
		buildClassMethods();
		buildToStringMethod();
		buildToMapMethod();
		buildToJSonMethod();
		buildToListMethod();
		buildGetAttributeNamesList();
		buildEndOfClass(getMainFile());
	}

	public void buildHelperClass() throws SQLException {
		buildClassHeaderForHelperClass();
		buildClassAttributesForHelperClass();
		buildClassMethodsForHelperClass();
		// buildToStringMethod();
		buildEndOfClass(getHelperFile());
	}

	public void buildClassHeader() {
		getMainFile().println("// class generated by JConcept Database ClassBuilder 2.0");
		getMainFile().println("package " + concept.getPackageName() + ";\n");
		getMainFile().println("// import JConcept Database library");
		getMainFile().println("import org.neodatis.rdb.*;\n");
		getMainFile().println("import org.neodatis.rdb.query.*;\n");
		getMainFile().println("import org.neodatis.rdb.implementation.*;\n");

		getMainFile().println("// For Date attribute");
		getMainFile().println("import java.util.Date;\n");
		getMainFile().println("// For BigDecimal attribute");
		getMainFile().println("import java.math.*;\n");

		getMainFile().print("public class " + concept.getClassName());

		if (concept.getConceptExtends() != null) {
			getMainFile().print(" extends " + concept.getConceptExtends() + " ");
		}

		getMainFile().println(" implements DbObjectMapping , org.neodatis.rdb.ToX");
		if(concept.getConceptImplements()!=null){
			getMainFile().println(" ," + concept.getConceptImplements());
		}


		getMainFile().println("{");
	}

	public void buildClassHeaderForHelperClass() {
		getHelperFile().println("// class generated by NeoDatis Database ClassBuilder 2.0");
		getHelperFile().println("package " + (concept.getHelperPackageName() == null ? concept.getPackageName() : concept.getHelperPackageName()) + ";\n");
		getHelperFile().println("// import NeoDatis Database library");
		getHelperFile().println("import org.neodatis.rdb.*;\n");
		getHelperFile().println("import org.neodatis.rdb.query.*;\n");
		getHelperFile().println("import java.util.Date;\n");
		getHelperFile().println("import java.math.BigDecimal;\n");

		getHelperFile().print("public class " + concept.getClassName() + "DBHelper");

		getHelperFile().println("{");
	}

	public void buildClassAttributes() {
		getMainFile().println("\t//** Database fields");

		for (int i = 0; i < concept.getAttributes().size(); i++) {
			Attribute a = concept.getAttributes().get(i);
			getMainFile().println("\tprotected " + a.getType() + " " +  _DATABASE_FIELD_PREFIX + a.getName() + ";");
		}
	}

	
	public void buildDbObject() {
		getMainFile().println("\t//** Db Object");
		
		String t = "public static Service<@class> DB = new Service<@class>(@class.class);";
		t = StringUtils.replaceToken(t, "@class", concept.getClassName());
		
		getMainFile().println("\t"+t);
	}

	public void buildClassAttributesForHelperClass() {
		getMainFile().println("\t//** Database fields");

		getHelperFile().println("\tpublic static final DBTable table = new DefaultDBTable(\"" + concept.getName() + "\");");

		getHelperFile().println();

		for (int i = 0; i < concept.getAttributes().size(); i++) {
			Attribute a = concept.getAttributes().get(i);
			getHelperFile().println(
					"\tpublic static final DBColumn " + a.getName().toUpperCase() + " =  new DefaultDBColumn( table , \"" + a.getName() +  "\" , " + a.getType()+ ".class );");
		}
	}

	public void buildToStringMethod() {
		String sFieldName = null;
		String sMethodName = null;

		getMainFile().println("\t/** toString method*/");
		getMainFile().println("\tpublic String toString()");
		getMainFile().println("\t{\n");
		getMainFile().println("\t\tStringBuffer sResult = new StringBuffer();");
		getMainFile().println("\t\tsResult.append(\"\\n[\\n\");");

		for (int i = 0; i < concept.getAttributes().size(); i++) {
			Attribute a = concept.getAttributes().get(i);
			sFieldName = StringUtils.capitalize(a.getName(), true);
			sMethodName = "get" + StringUtils.capitalize(a.getName(), false) + "()";
			getMainFile().println("\t\tsResult.append( \"\\t " + sFieldName + " : \" ).append( " + sMethodName + ").append(\"\\n\");");
		}
		getMainFile().println("\t\tsResult.append(\"]\\n\");");
		getMainFile().println("\t\treturn sResult.toString();");
		getMainFile().println("\t}\n");
	}

	public void buildToJSonMethod() {
		String sFieldName = null;

		getMainFile().println("\t/** toJSon method*/");
		getMainFile().println("\tpublic String toJSon()");
		getMainFile().println("\t{\n");
		getMainFile().println("\t\tStringBuffer sResult = new StringBuffer();");
		getMainFile().println("\t\tsResult.append(\"{\");");

		for (int i = 0; i < concept.getAttributes().size(); i++) {
			if(i>0){
				getMainFile().println("\t\tsResult.append( \",\");");
			}
			Attribute a = concept.getAttributes().get(i);
			sFieldName = StringUtils.capitalize(a.getName(), true);
			String sMethodName = "get" + StringUtils.capitalize(a.getName(), false) + "()";
			//sResult.append( "\"id\":\"").append( getId()).append("\"");
			getMainFile().println("\t\tsResult.append( \"\\\""+sFieldName+"\\\":\\\"\" ).append(String.valueOf("+ sMethodName+") ).append(\"\\\"\");");
		}
		getMainFile().println("\t\tsResult.append(\"}\");");
		getMainFile().println("\t\treturn sResult.toString();");
		getMainFile().println("\t}\n");
	}
	
	public void buildToMapMethod() {
		String sFieldName = null;

		getMainFile().println("\t/** toMap method*/");
		getMainFile().println("\tpublic java.util.Map<String,Object> toMap()");
		getMainFile().println("\t{\n");
		getMainFile().println("\t\tjava.util.Map<String,Object> map = new java.util.HashMap<String,Object>();");

		for (int i = 0; i < concept.getAttributes().size(); i++) {
			Attribute a = concept.getAttributes().get(i);
			sFieldName = StringUtils.capitalize(a.getName(), true);
			String sMethodName = "get" + StringUtils.capitalize(a.getName(), false) + "()";
			getMainFile().println("\t\tmap.put( \"" + sFieldName +"\" , String.valueOf(" + sMethodName + "));");
		}
		getMainFile().println("\t\treturn map;");
		getMainFile().println("\t}\n");
	}
	public void buildToListMethod() {
		String sFieldName = null;

		getMainFile().println("\t/** toList method*/");
		getMainFile().println("\tpublic java.util.List<String> toList()");
		getMainFile().println("\t{\n");
		getMainFile().println("\t\tjava.util.List<String> list = new java.util.ArrayList<String>();");

		for (int i = 0; i < concept.getAttributes().size(); i++) {
			Attribute a = concept.getAttributes().get(i);
			sFieldName = StringUtils.capitalize(a.getName(), true);
			String sMethodName = "get" + StringUtils.capitalize(a.getName(), false) + "()";
			getMainFile().println("\t\tlist.add( String.valueOf(" + sMethodName + "));");
		}
		getMainFile().println("\t\treturn list;");
		getMainFile().println("\t}\n");
	}
	
	public void buildGetAttributeNamesList() {
		String sFieldName = null;

		getMainFile().println("\t/** getAttributeNames method*/");
		getMainFile().println("\tpublic java.util.List<String> getAttributeNames()");
		getMainFile().println("\t{\n");
		getMainFile().println("\t\tjava.util.List<String> list = new java.util.ArrayList<String>();");

		for (int i = 0; i < concept.getAttributes().size(); i++) {
			Attribute a = concept.getAttributes().get(i);
			sFieldName = StringUtils.capitalize(a.getName(), true);
			getMainFile().println("\t\tlist.add( \"" + sFieldName + "\");");
		}
		getMainFile().println("\t\treturn list;");
		getMainFile().println("\t}\n");
	}
	
	public void buildClassMethods() {
		buildConstrutor();

		buildGetPrimaryKeyMethod();
		buildGetTableNameMethod(getMainFile());
		buildEqualsMethod(getMainFile());

		buildSetterAndGetter();
	}

	public void buildClassMethodsForHelperClass() {
		buildGetColumnList(getHelperFile());
	}

	public void buildConstrutor() {
		getMainFile().println("\n\t//** Construtor");

		getMainFile().println("\tpublic " + concept.getClassName() + "()");
		getMainFile().println("\t{");
		getMainFile().println("\t}");
	}

	public void buildGetPrimaryKeyMethod() {
		getMainFile().println("\n\t//** To get the primary key field name");

		getMainFile().println("\tpublic PrimaryKey getPrimaryKey()");
		getMainFile().println("\t{");
		getMainFile().println("\t\treturn new DefaultPrimaryKey(\"" + concept.getConceptPrimaryKey().getName() + "\");");
		getMainFile().println("\t}");
	}

	public void buildGetTableNameMethod(PrintStream in_stream) {
		in_stream.println("\n\t//** To get the table name");

		in_stream.println("\tpublic DBTable getTable()");
		in_stream.println("\t{");
		in_stream.println("\t\treturn new DefaultDBTable(\"" + concept.getName() + "\");");
		in_stream.println("\t}");
	}

	public void buildEqualsMethod(PrintStream in_stream) {
		in_stream.println("\n\t//** To check equality");

		in_stream.println("\tpublic boolean equals(Object in_object)");
		in_stream.println("\t{");
		in_stream.println("\t\tif(in_object==null) return false;");
		in_stream.println("\t\tif(!(in_object instanceof DbObjectMapping)) return false;");
		in_stream.println("\t\tDbObjectMapping objectToCompare = (DbObjectMapping) in_object;");
		in_stream.println("\t\ttry \n\t\t{");
		in_stream.println("\t\t\treturn getPrimaryKey().getValue(this).equals(objectToCompare.getPrimaryKey().getValue(objectToCompare));");
		in_stream.println("\t\t} \n\t\tcatch(Exception e)\n\t\t{");
		in_stream.println("\t\t\treturn false; \n\t\t}");
		in_stream.println("\t}");
		
	}

	public void buildGetColumnList(PrintStream in_stream) {
		in_stream.println("\n\t//** To get the column list");

		in_stream.println("\tstatic public DBColumn [] getColumns()");
		in_stream.println("\t{");
		in_stream.println("\t\tDBColumn [] array = new DefaultDBColumn[" + concept.getAttributes().size() + "];");
		for (int i = 0; i < concept.getAttributes().size(); i++) {
			in_stream.println("\t\tarray[" + i + "] = " + concept.getAttributes().get(i).getName().toUpperCase() + ";");
		}
		in_stream.println("\t\treturn array;");
		in_stream.println("\t}");
	}

	public void buildSetterAndGetter() {
		getMainFile().println("\n\t//** Setters and Getters");

		for (int i = 0; i < concept.getAttributes().size(); i++) {
			// Setter
			getMainFile().println(buildSetter(i));

			// Getter
			getMainFile().println(buildGetter(i));
		}

	}

	public void buildEndOfClass(PrintStream in_strean) {
		in_strean.println("}");
	}

	public String getFileExtension() {
		return ".java";
	}

	public String getKeyField() {
		return "dbTABLE_NAME";
	}

    public Concept getConcept() {
        return concept;
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
    }
}
