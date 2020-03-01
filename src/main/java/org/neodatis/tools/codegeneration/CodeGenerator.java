package org.neodatis.tools.codegeneration;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Vector;

import org.neodatis.tools.StringUtils;
import org.neodatis.tools.Tools;

public class CodeGenerator {
	public void generateAccessors(Object in_object, int in_nOffset) {
		StringBuffer sRes = new StringBuffer();
		Field[] asFields = in_object.getClass().getDeclaredFields();

		// To store the field name
		String sFieldName = null;

		// To store the transformed field name using in_nOffset
		String sTransformedFieldName = null;

		for (int i = 0; i < asFields.length; i++) {

			sFieldName = asFields[i].getName();
			sTransformedFieldName = sFieldName.substring(in_nOffset, sFieldName.length());

			// THE GET
			sRes.append("\n\t/** Accessor to get the value of " + sFieldName + "*/");
			sRes.append("\n\tpublic " + asFields[i].getType().getName());
			sRes.append(" get" + sTransformedFieldName + "()\n\t{\n\t");
			sRes.append("\treturn " + sFieldName + ";\n\t}\n");

			// THE SET
			sRes.append("\n\t/** Accessor to set the value of " + sFieldName + "*/");
			sRes.append("\n\tpublic void");
			sRes.append(" set" + sTransformedFieldName + "(");
			sRes.append(asFields[i].getType().getName() + " in_");
			sRes.append(sFieldName + ")\n\t{\n\t\t");
			sRes.append(sFieldName + " = in_" + sFieldName + ";\n\t}\n");

		}

		System.out.println(sRes.toString());
	}

	/**
	 * Generate a data conatiner class
	 * 
	 * @param String
	 *            The name of the package
	 * @param String
	 *            The name of the class
	 * @param Vector
	 *            The list of field types
	 * @param Vector
	 *            The list of field names
	 * @param int The attribute offset for get and set methods
	 * @param String
	 *            the access type for attributes
	 * @param String
	 *            The param to be match for search purpose
	 * @return String
	 */
	public String generateDataContainer(String in_sPackageName, String in_sClassName, Vector in_listOfTypes, Vector in_listOfNames, int in_nOffset,
			String in_sAttributeAccessType, String in_sParamForMatching) {
		// First builds 'no comment' comments!
		String sComment = "Insert attribute description here";
		Vector aComments = new Vector();
		for (int i = 0; i < in_listOfNames.size(); i++) {
			aComments.addElement(sComment);
		}

		return generateDataContainer(in_sPackageName, in_sClassName, in_listOfTypes, in_listOfNames, in_nOffset, in_sAttributeAccessType, in_sParamForMatching,
				null, false);
	}

	/**
	 * Generate a data conatiner class
	 * 
	 * @param String
	 *            The name of the package
	 * @param String
	 *            The name of the class
	 * @param String
	 *            The Class description
	 * @param Vector
	 *            The list of field types
	 * @param Vector
	 *            The list of field names
	 * @param int The attribute offset for get and set methods
	 * @param String
	 *            the access type for attributes
	 * @param String
	 *            The param to be match for search purpose
	 * @param String
	 *            The Name of the base class
	 * @param boolean If class must be abstract
	 * @return String
	 */
	public String generateDataContainer(String in_sPackageName, String in_sClassName, Vector in_listOfTypes, Vector in_listOfNames, int in_nOffset,
			String in_sAttributeAccessType, String in_sParamForMatching, String in_sBaseClassName, boolean in_bIsAbstract) {

		// First builds 'no comment' comments!
		String sComment = "Insert attribute description here";
		Vector aComments = new Vector();
		for (int i = 0; i < in_listOfNames.size(); i++) {
			aComments.addElement(sComment);
		}
		return generateDataContainer(in_sPackageName, in_sClassName, "Insert class description here", in_listOfTypes, in_listOfNames, aComments, in_nOffset,
				in_sAttributeAccessType, in_sParamForMatching, in_sBaseClassName, false);
	}

	/**
	 * Generate a data conatiner class info a java file
	 * 
	 * @param String
	 *            The name of the package
	 * @param String
	 *            The name of the class
	 * @param String
	 *            The Class description
	 * @param Vector
	 *            The list of field types
	 * @param Vector
	 *            The list of field names
	 * @param Vector
	 *            The list of comments for each fields
	 * @param int The attribute offset for get and set methods
	 * @param String
	 *            the access type for attributes
	 * @param String
	 *            The param to be match for search purpose
	 * @param String
	 *            The Name of the base class
	 * @param boolean If class must be abstract
	 */
	public void generateDataContainerToFile(String in_sPackageName, String in_sClassName, String in_sClassDescription, Vector in_listOfTypes,
			Vector in_listOfNames, Vector in_listComments, int in_nOffset, String in_sAttributeAccessType, String in_sParamForMatching,
			String in_sBaseClassName, boolean in_bIsAbstract) {
		String sCode = generateDataContainer(in_sPackageName, in_sClassName, in_sClassDescription, in_listOfTypes, in_listOfNames, in_listComments, in_nOffset,
				in_sAttributeAccessType, in_sParamForMatching, in_sBaseClassName, in_bIsAbstract);

		PrintStream file = Tools.createFileStream(in_sClassName + ".java");
		if (file != null) {
			file.print(sCode);
			file.close();
		}
	}

	/**
	 * Generate a data conatiner class info a java file
	 * 
	 * @param String
	 *            The name of the package
	 * @param String
	 *            The name of the class
	 * @param Vector
	 *            The list of field types
	 * @param Vector
	 *            The list of field names
	 * @param int The attribute offset for get and set methods
	 * @param String
	 *            the access type for attributes
	 * @param String
	 *            The param to be match for search purpose
	 * @param String
	 *            The Name of the base class
	 * @param boolean If the class is abstract
	 */
	public void generateDataContainerToFile(String in_sPackageName, String in_sClassName, Vector in_listOfTypes, Vector in_listOfNames, int in_nOffset,
			String in_sAttributeAccessType, String in_sParamForMatching, String in_sBaseClassName, boolean in_bIsAbstract) {
		String sCode = generateDataContainer(in_sPackageName, in_sClassName, in_listOfTypes, in_listOfNames, in_nOffset, in_sAttributeAccessType,
				in_sParamForMatching, in_sBaseClassName, in_bIsAbstract);

		PrintStream file = Tools.createFileStream(in_sClassName + ".java");
		if (file != null) {
			file.print(sCode);
			file.close();
		}
	}

	/**
	 * Generate a data conatiner class info a java file
	 * 
	 * @param String
	 *            The name of the package
	 * @param String
	 *            The name of the class
	 * @param Vector
	 *            The list of field types
	 * @param Vector
	 *            The list of field names
	 * @param int The attribute offset for get and set methods
	 * @param String
	 *            the access type for attributes
	 * @param String
	 *            The param to be match for search purpose
	 */
	public void generateDataContainerToFile(String in_sPackageName, String in_sClassName, Vector in_listOfTypes, Vector in_listOfNames, int in_nOffset,
			String in_sAttributeAccessType, String in_sParamForMatching) {
		String sCode = generateDataContainer(in_sPackageName, in_sClassName, in_listOfTypes, in_listOfNames, in_nOffset, in_sAttributeAccessType,
				in_sParamForMatching, null, false);

		PrintStream file = Tools.createFileStream(in_sClassName + ".java");
		if (file != null) {
			file.print(sCode);
			file.close();
		}
	}

	/**
	 * Generate a data conatiner class
	 * 
	 * @param String
	 *            The name of the package
	 * @param String
	 *            The name of the class
	 * @param String
	 *            The Class description
	 * @param Vector
	 *            The list of field types
	 * @param Vector
	 *            The list of field names
	 * @param Vector
	 *            The list of comments for each fields
	 * @param int The attribute offset for get and set methods
	 * @param String
	 *            the access type for attributes
	 * @param String
	 *            The param to be match for search purpose
	 * @param String
	 *            The Name of the base class
	 * @param boolean If class must be abstract
	 * @return String
	 */
	public String generateDataContainer(String in_sPackageName, String in_sClassName, String in_sClassDescription, Vector in_listOfTypes,
			Vector in_listOfNames, Vector in_listOfComments, int in_nOffset, String in_sAttributeAccessType, String in_sParamForMatching,
			String in_sBaseClassName, boolean in_bIsAbstract) {
		StringBuffer sRes = new StringBuffer();

		sRes.append("// Class generated automatically - " + new Date().toString() + "\n");
		sRes.append("package " + in_sPackageName + ";\n\n");
		sRes.append("import java.util.*;\n\n");

		sRes.append("/** " + in_sClassName + " : \n");
		sRes.append(in_sClassDescription);
		sRes.append("\n");
		sRes.append("**/\n\n");

		sRes.append("\n");

		if (in_bIsAbstract) {
			sRes.append("abstract ");
		}
		sRes.append("public class " + in_sClassName);
		if (in_sBaseClassName != null) {
			sRes.append(" extends " + in_sBaseClassName);
		}

		sRes.append("\n");

		sRes.append("{\n");

		// Builds fields declaration
		for (int i = 0; i < in_listOfTypes.size(); i++) {
			sRes.append("\n\t/** ");
			sRes.append((String) in_listOfComments.elementAt(i));
			sRes.append("**/\n");
			sRes.append("\t" + in_sAttributeAccessType + " " + (String) in_listOfTypes.elementAt(i) + " " + (String) in_listOfNames.elementAt(i) + ";\n\n");
		}

		/**/
		// Builds constructor comments

		sRes.append("\n\n\t/** Constructor\n");

		sRes.append("\n\t**/");
		// Builds constructor
		sRes.append("\n\tpublic " + in_sClassName + "()");

		sRes.append("\n\t{");

		// Builds constructor body
		for (int i = 0; i < in_listOfTypes.size(); i++) {
			String sType = (String) in_listOfTypes.elementAt(i);
			String sInitValue = "0";
			if (sType.equals("String")) {
				sInitValue = "\"\"";
			}
			if (sType.equals("char")) {
				sInitValue = "''";
			}

			if (sType.equals("Date")) {
				sInitValue = "null";
			}
			if (sType.equals("boolean")) {
				sInitValue = "false";
			}

			sRes.append("\n\t\t" + (String) in_listOfNames.elementAt(i) + " = " + sInitValue + ";");
		}

		sRes.append("\n\t}");
		/***/

		// Builds constructor comments

		sRes.append("\n\n\t/** Constructor\n");

		// Builds constructor parameters
		for (int i = 0; i < in_listOfTypes.size(); i++) {
			sRes.append("\t@param " + (String) in_listOfTypes.elementAt(i) + " ? \n");
		}

		sRes.append("\n\t**/");
		// Builds constructor
		sRes.append("\n\tpublic " + in_sClassName + "(");

		// Builds constructor parameters
		for (int i = 0; i < in_listOfTypes.size(); i++) {
			sRes.append((String) in_listOfTypes.elementAt(i) + " in_" + (String) in_listOfNames.elementAt(i));
			if (i < in_listOfTypes.size() - 1) {
				sRes.append(",");
			}
		}

		// ends constructor parameters
		sRes.append(")");

		sRes.append("\n\t{");

		// Builds constructor body
		for (int i = 0; i < in_listOfTypes.size(); i++) {
			sRes.append("\n\t\t" + (String) in_listOfNames.elementAt(i) + " = in_" + (String) in_listOfNames.elementAt(i) + ";");
		}

		sRes.append("\n\t}");
		
		sRes.append("\n");
		
		String t = "public static Service<@class> DB = new Service<@class>(@class.class);";
		t = StringUtils.replaceToken(t, "@class", in_sClassName);
		
		sRes.append("\n\t\t"+t);
		sRes.append("\n\n");
		
		
		

		// Builds acessors
		for (int i = 0; i < in_listOfTypes.size(); i++) {
			String sFieldName = (String) (String) in_listOfNames.elementAt(i);
			String sTransformedFieldName = sFieldName.substring(in_nOffset, sFieldName.length());

			// THE GET
			sRes.append("\n\t/** Accessor to get the value of " + sFieldName + "*/");
			sRes.append("\n\tpublic " + (String) in_listOfTypes.elementAt(i));
			sRes.append(" get" + sTransformedFieldName + "()\n\t{\n");
			sRes.append("\t\treturn " + sFieldName + ";\n\t}\n");

			// THE SET
			sRes.append("\n\t/** Accessor to set the value of " + sFieldName + "*/");
			sRes.append("\n\tpublic void");
			sRes.append(" set" + sTransformedFieldName + "(");
			sRes.append((String) in_listOfTypes.elementAt(i) + " in_");
			sRes.append(sFieldName + ")\n\t{\n");
			sRes.append("\t\t" + sFieldName + " = in_" + sFieldName + ";\n\t}\n");

			sRes.append("\n");

		}

		// sRes.append(
		// "\n\t/** To check if the object matches the incoming string\n\t@return boolean\n\t@param String\n\t**/");
		// sRes.append( "\n\tpublic boolean match( String in_sStringToMatch )\n"
		// );
		// sRes.append( "\n\t{\n" );
		// sRes.append( "\n\t\treturn " + in_sParamForMatching +
		// ".equals(in_sStringToMatch);\n" );
		// sRes.append( "\t}\n");

		sRes.append("\n\t/** To get the description of the object\n\t@return String\n\t**/");
		sRes.append("\n\tpublic String toString(  )\n");
		sRes.append("\n\t{\n");
		sRes.append("\n\t\tStringBuffer sResult = new StringBuffer();\n");

		for (int i = 0; i < in_listOfTypes.size(); i++) {
			String sFieldName = (String) (String) in_listOfNames.elementAt(i);
			String sTransformedFieldName = sFieldName.substring(in_nOffset, sFieldName.length());

			sRes.append("\n\t\tsResult.append( \" " + sTransformedFieldName + " = \"+" + sFieldName + " );\n");
		}
		sRes.append("\n\t\treturn sResult.toString();\n");
		sRes.append("\t}\n");

		sRes.append("}");

		return sRes.toString();

	}

	public String generateList(String in_sPackageName, String in_sClassName, String in_sListOf) {
		StringBuffer sRes = new StringBuffer();

		sRes.append("// Class generated automatically - " + new Date().toString() + "\n");
		sRes.append("package " + in_sPackageName + ";\n\n");

		sRes.append("\nimport java.util.*;\n");

		sRes.append("/** " + in_sClassName + " : \n");
		sRes.append(" insert class description here \n");
		sRes.append("**/\n\n");

		sRes.append("\nclass " + in_sClassName + " extends Vector \n");
		sRes.append("{\n");

		// Builds constructor comments

		sRes.append("\n\n\t/** Constructor\n");

		sRes.append("\n\t**/");
		// Builds constructor
		sRes.append("\n\tpublic " + in_sClassName + "(");

		// ends constructor parameters
		sRes.append(")");

		sRes.append("\n\t{");

		sRes.append("\n\t\tsuper();");

		sRes.append("\n\t}");

		// Builds add
		sRes.append("\n\t/** To Add an object \n\t@param " + in_sListOf + "\n\t**/");
		sRes.append("\n\n\tpublic void addObject( " + in_sListOf + " in_objectToAdd )\n\t{");
		sRes.append("\n\t\taddElement( in_objectToAdd );\n");
		sRes.append("\t}\n");

		// Builds get
		sRes.append("\n\t/** To get an object \n\t@return " + in_sListOf + "\n\t**/");
		sRes.append("\n\n\tpublic " + in_sListOf + " getObject(int in_nIndex)\n\t{");
		sRes.append("\n\t\treturn (" + in_sListOf + ") elementAt( in_nIndex );\n");
		sRes.append("\t}\n");

		// Builds search
		sRes.append("\n\t/** To search for a specific object\n\t@return " + in_sListOf + "\n\t@param String\n\t**/");
		sRes.append("\n\n\tpublic " + in_sListOf + " search(String in_sString)\n\t{");
		sRes.append("\n\t\tfor(int i=0 ; i < size() ; i ++ )\n");
		sRes.append("\t\t{");
		sRes.append("\n\t\t\tif( getObject( i ).match( in_sString ) )\n");
		sRes.append("\t\t\t\treturn getObject(i);\n");
		sRes.append("\t\t}");
		sRes.append("\n\t\treturn null;\n");
		sRes.append("\t}\n");

		// Builds the toString method
		sRes.append("\n\t/** Gets the object description@retrun String\n\t**/");
		sRes.append("\n\n\tpublic String toString()\n\t{");
		sRes.append("\n\t\tStringBuffer sResult = new StringBuffer();\n");
		sRes.append("\n\t\tfor(int i=0 ; i < size() ; i ++ )\n");
		sRes.append("\t\t{");
		sRes.append("\n\t\t\tsResult.append( getObject(i).toString()+\"\\n\" );\n");
		sRes.append("\t\t}");
		sRes.append("\n\t\treturn sResult.toString();\n");
		sRes.append("\t}\n");

		sRes.append("\n}\n");

		return sRes.toString();
	}

	static public void main(String[] args) {
		CodeGenerator codgen = new CodeGenerator();

		/*
		 * Vector types = new Vector(); Vector names = new Vector();
		 * 
		 * names.addElement("_sName"); types.addElement("String");
		 * names.addElement("_sAddress"); types.addElement("String");
		 * names.addElement("_sZipCode"); types.addElement("String");
		 * names.addElement("_sCity"); types.addElement("String");
		 * names.addElement("_sState"); types.addElement("String");
		 * names.addElement("_sSex"); types.addElement("String");
		 * names.addElement("_nChildren"); types.addElement("int");
		 * names.addElement("_dBirthDate"); types.addElement("Date");
		 * codgen.generateDataContainer("br.com.jconcept.test" , "Client" ,
		 * types , names , 2 , "protected");
		 */

		Vector types = new Vector();
		Vector names = new Vector();
		Vector comments = new Vector();

		names.addElement("_sConnectionName");
		types.addElement("String");
		comments.addElement("Name of the connection");

		names.addElement("_sUserName");
		types.addElement("String");
		comments.addElement("Name of the user");
		names.addElement("_sPassword");
		types.addElement("String");
		comments.addElement("Password");
		names.addElement("_sDriver");
		types.addElement("String");
		comments.addElement("Name of the jdbc Driver");
		names.addElement("_sUrl");
		types.addElement("String");
		comments.addElement("Url to connect to db");
		names.addElement("_nNbConnections");
		types.addElement("int");
		comments.addElement("Number of connection for the pool");

		String sTemp2 = codgen.generateDataContainer("br.com.jconcept.db", "ConnectionInfo", "Classe de teste\ndklsfjkdsljkdlj", types, names, comments, 2,
				"protected", "_sConnectionName", "Mother", false);
		System.out.println(sTemp2);

		// String sTemp = codgen.generateList("br.com.jconcept.test" ,
		// "ClientList" , "Client" );
		// String sTemp = codgen.generateList("br.com.jconcept.db" ,
		// "BooleanList" , "MyBoolean" );

		// System.out.println( sTemp );
	}
}