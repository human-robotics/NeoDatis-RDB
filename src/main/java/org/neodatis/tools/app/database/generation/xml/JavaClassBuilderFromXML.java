package org.neodatis.tools.app.database.generation.xml;

import java.sql.SQLException;

import org.neodatis.tools.app.database.generation.clazz.ClassBuilderFromDb;
import org.neodatis.tools.app.database.generation.clazz.CodeGenerationListener;
import org.neodatis.tools.app.model.Concept;

/**
 */

public class JavaClassBuilderFromXML extends ClassBuilderFromDb {

	
	public JavaClassBuilderFromXML(String directory, Concept concept, CodeGenerationListener codeGenerator)
			throws Exception {
		super(directory, concept, codeGenerator);
	}

	public void getTableInfos() throws SQLException {
		// nothing to do
		String tableName = concept.getName();
		String schema = null;
		if(tableName.indexOf(".")!=-1){
			String[] tokens = tableName.split("\\.");
			schema = tokens[0];
			tableName = tokens[1];
		}
		
		if(codeGenerationListener!=null){
			codeGenerationListener.newTable(schema, tableName, getConcept());
		}
	}
}