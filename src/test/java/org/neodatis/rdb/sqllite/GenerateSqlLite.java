package org.neodatis.rdb.sqllite;

import org.neodatis.rdb.DbObjectWithLongId;
import org.neodatis.tools.app.NeoDatis;
import org.neodatis.tools.app.database.generation.clazz.MainClassBuilder;
import org.neodatis.tools.app.database.generation.xml.MainXmlBuilder;
import org.neodatis.tools.app.model.NeoDatisAppData;

public class GenerateSqlLite {
	public static void main(String[] args) throws Exception {
		String xmlFile = "model-hr-sqllite.xml";
		String directory = "src/test/java";
		String packageName = "org.neodatis.rdb.sqllite";

		MainXmlBuilder builder = new MainXmlBuilder("hr", directory, xmlFile, packageName,
				null, null, DbObjectWithLongId.class.getName());
		if (builder.isOk()) {
			builder.manageTables();
		} else {
			System.out.println("Problem with builder");
		}


		NeoDatisAppData app = NeoDatis.loadApplicationData(xmlFile);

		MainClassBuilder builder2 = new MainClassBuilder(directory, app.getConcepts());
		if (builder2.isOk()) {
			builder2.manageTables();
		} else {
			System.out.println("Problem with builder");
		}

	}
}
