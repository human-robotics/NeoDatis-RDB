package org.neodatis.rdb.util.generation;

import java.sql.SQLException;

import org.neodatis.rdb.implementation.DatabaseMetaInformation;

/**
 * Description : Builds a java file containing a java class templating a
 * database table
 * 
 * @author : Olivier Smadja - osmadja@netcourrier.com
 * @date : 07/08/2001 - creation
 */

public class JavaClassBuilderFromDbJDBC extends ClassBuilderFromDb {

	/**
	 * Constructor
	 * @param xmlGenerator 
	 * @param sHelperClassPackage 
	 * @param sBaseClassName 
	 * 
	 * @param String
	 *            The name of the package of the class to be created
	 * @param String
	 *            The name of the class to be created
	 * @param String
	 *            The name of the table to reflect
	 * @param String
	 *            The name of the primary key field
	 * @throws Exception
	 */
	public JavaClassBuilderFromDbJDBC(String directory, String in_sPackageName, String in_sClassName, String in_sTableNameToReflect, String in_sKeyField, String sBaseClassName, String sHelperClassPackage, CodeGenerationListener codeGenerator)
			throws Exception {
		super(directory, in_sTableNameToReflect, in_sClassName, in_sKeyField, in_sPackageName, sBaseClassName, sHelperClassPackage, codeGenerator);
	}

	/**
	 * Constructor
	 * 
	 * @param String
	 *            The name of the package of the class to be created
	 * @param String
	 *            The name of the class to be created
	 * @param String
	 *            The name of the table to reflect
	 * @param String
	 *            The name of the primary key field
	 * @param String
	 *            The base class name. The default is DbObject
	 * @param The
	 *            helper class package
	 * @throws Exception
	 */
	public JavaClassBuilderFromDbJDBC(String directory, String in_sPackageName, String in_sClassName, String in_sTableNameToReflect, String in_sKeyField,
			String in_sBaseClassName, String in_sHelperClassPackage) throws Exception {
		super(directory, in_sTableNameToReflect, in_sClassName, in_sKeyField, in_sPackageName, in_sBaseClassName, in_sHelperClassPackage, null);

	}

	public void getTableInfos() throws SQLException {
		String tableName = getTableName();
		String schema = null;
		if(tableName.indexOf(".")!=-1){
			String[] tokens = tableName.split("\\.");
			schema = tokens[0];
			tableName = tokens[1];
		}
		super.fields = new DatabaseMetaInformation().getTableColumnsGeneric(schema, tableName);
		
		if(codeGenerationListener!=null){
			codeGenerationListener.newTable(schema, tableName, fields);
		}
	}

	

	public static void main(String[] args) throws Exception {
		int nNbArgs = args.length;

		System.out.println("Args = " + nNbArgs);

		if (nNbArgs < 4) {
			displayHelpMessage();
		} else {
			String directory = args[0];
			String sPackage = args[1];
			String sClassName = args[2];
			String sTableName = args[3];
			String sKeyFieldName = args[4];
			String sBaseClass = null;
			String sHelperPackage = null;
			if (args.length >= 4) {
				sBaseClass = args[5];
			}
			if (args.length >= 5) {
				sHelperPackage = args[6];
			}

			System.out.println("\nJConcept Class Builder for Java 1.0\n");
			System.out.println("\n creating java class :\n");
			System.out.println("\t - SrcDir \t= " + directory);
			System.out.println("\t - Package \t= " + sPackage);
			System.out.println("\t - Class name \t= " + sClassName);
			System.out.println("\t - Table name \t= " + sTableName);
			System.out.println("\t - Primary key\t= " + sKeyFieldName);
			System.out.println("\t - Base class\t= " + sBaseClass);
			System.out.println("\t - HelperPackage\t= " + sHelperPackage);
			System.out.println("\n\n");

			JavaClassBuilderFromDbJDBC builder = new JavaClassBuilderFromDbJDBC(directory, sPackage, sClassName, sTableName, sKeyFieldName, sBaseClass,
					sHelperPackage);
		}

	}

	static void displayHelpMessage() {
		String sPackage = "br.com.jconcept.test";
		String sClassName = "User";
		String sTableName = "USER";
		String sKeyFieldName = "USERID";
		String sBaseClassName = "MyClass";
		String sHelperPackage = "br.com.jconcept.test.helper";

		System.out.println("\nJConcept Class Builder for Java 2.1\n");
		System.out.println("\n Must pass at least 4 parameters :");
		System.out.println("\t - Package \t\tex : " + sPackage);
		System.out.println("\t - Class name \tex : " + sClassName);
		System.out.println("\t - Table name \tex : " + sTableName);
		System.out.println("\t - Primary key\tex : " + sKeyFieldName);
		System.out.println("\t - Base class\tex : " + sBaseClassName);
		System.out.println("\t - Helper class package\tex : " + sHelperPackage);
	}
}