package org.neodatis.tools.app.database.generation.xml;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.neodatis.rdb.implementation.DatabaseColumn;
import org.neodatis.tools.app.database.generation.clazz.CodeGenerationListener;
import org.neodatis.tools.app.model.Concept;

public class XmlGenerator implements CodeGenerationListener {
	protected static final String TEMPLATE_XML_NEODATIS_APP_OPEN = "<neodatis-app-data name=\"@name\">";
	protected static final String TEMPLATE_XML_NEODATIS_APP_CLOSE = "</neodatis-app-data>";
	protected static final String TEMPLATE_XML_CONCEPT_OPEN = "\n\t<concept name=\"@name\" @more packageName=\"@package\">";
	protected static final String TEMPLATE_XML_CONCEPT_CLOSE = "\n\t</concept>";
	protected static final String TEMPLATE_XML_ATTIBUTE = "\n\t\t<attribute name=\"@name\" type=\"@type\" is-nullable=\"@nullable\" is-primary-key=\"@ispk\" foreign-key=\"@fk\" comment=\"@comment\"/>";

	protected StringBuilder builder;
	protected String interfaceNameToImplement;
	protected String classNameToExtend;
	

	public XmlGenerator(String appName, String interfaceNameToImplement, String classNameToExtend) {
		builder = new StringBuilder();
		this.interfaceNameToImplement = interfaceNameToImplement;
		this.classNameToExtend = classNameToExtend;
		builder.append(TEMPLATE_XML_NEODATIS_APP_OPEN.replaceAll("@name", appName));
	}

	public void newTable(String schema, String tableName, List<DatabaseColumn> fields, String packageName) {
		System.out.println("New Table : " + tableName + " with fields " + fields +" - packageName = "+ packageName);
		String opening = TEMPLATE_XML_CONCEPT_OPEN.replaceAll("@name", tableName);
		
		
		String more = "";
		if(interfaceNameToImplement!=null){
			more = " implements=\""+interfaceNameToImplement+"\" ";
		}
		if(classNameToExtend!=null){
			more += " extends=\""+classNameToExtend+"\" ";
		}
		
		opening = opening.replaceAll("@more", more);
		opening = opening.replaceAll("@package", packageName);
		
		builder.append(opening);
		
		for (DatabaseColumn fi : fields) {
			String text = TEMPLATE_XML_ATTIBUTE.replaceAll("@name", fi.getName()).replaceAll("@type", fi.getDataTypeAsString());
			text = text.replaceAll("@nullable", String.valueOf(fi.isAllowNull())).replaceAll("@ispk", String.valueOf(fi.isPrimaryKey()));
			if (fi.getForeignKeyInformation() != null) {
				text = text.replaceAll("@fk", fi.getForeignKeyInformation());
			} else {
				text = text.replaceAll("@fk", "none");
			}
			String comment = String.valueOf(fi.getComment());
			text = text.replaceAll("@comment", comment);
			builder.append(text);

		}
		;

		builder.append(TEMPLATE_XML_CONCEPT_CLOSE);
	}

	public void close() {
		builder.append("\n").append(TEMPLATE_XML_NEODATIS_APP_CLOSE);
	}

	public String getContent() {
		return builder.toString();
	}

	public String toFile(String fileName) throws IOException {
		FileOutputStream fos = new FileOutputStream(fileName);
		fos.write(builder.toString().getBytes());
		fos.close();
		return fileName;
	}

	public void start() {
		// TODO Auto-generated method stub
		
	}

	public void newTable(String schema, String tableName, Concept concept) {
		// TODO Auto-generated method stub
		
	}

	public void end() {
		// TODO Auto-generated method stub
		
	}
}
