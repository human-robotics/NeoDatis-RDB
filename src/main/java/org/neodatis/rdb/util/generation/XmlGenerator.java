package org.neodatis.rdb.util.generation;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.neodatis.rdb.implementation.DatabaseColumn;

public class XmlGenerator implements CodeGenerationListener {
	protected static final String TEMPLATE_XML_NEODATIS_APP_OPEN = "<neodatis-app-data name=\"@name\">";
	protected static final String TEMPLATE_XML_NEODATIS_APP_CLOSE = "</neodatis-app-data>";
	protected static final String TEMPLATE_XML_CONCEPT_OPEN = "\n\t<concept name=\"@name\" @more>";
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

	public void newTable(String schema, String tableName, List<DatabaseColumn> fields) {
		System.out.println("New Table : " + tableName + " with fields " + fields);
		String opening = TEMPLATE_XML_CONCEPT_OPEN.replaceAll("@name", tableName);
		
		
		String more = "";
		if(interfaceNameToImplement!=null){
			more = " implements=\""+interfaceNameToImplement+"\" ";
		}
		if(classNameToExtend!=null){
			more += " extends=\""+classNameToExtend+"\" ";
		}
		
		opening = opening.replaceAll("@more", more);
		
		builder.append(opening);
		
		for (DatabaseColumn fi : fields) {
			String text = TEMPLATE_XML_ATTIBUTE.replaceAll("@name", fi.getName()).replaceAll("@type", fi.getDataTypeAsString());
			text = text.replaceAll("@nullable", String.valueOf(fi.isAllowNull())).replaceAll("@ispk", String.valueOf(fi.isPrimaryKey()));
			if (fi.getForeignKeyInformation() != null) {
				text = text.replaceAll("@fk", fi.getForeignKeyInformation());
			} else {
				text = text.replaceAll("@fk", "none");
			}
			text = text.replaceAll("@comment", fi.getComment());
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
}
