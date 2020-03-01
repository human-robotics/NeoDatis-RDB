package org.neodatis.rdb.test.test1;

import org.neodatis.rdb.RDB;
import org.neodatis.rdb.RDBFactory;
import org.neodatis.rdb.util.generation.Main;

public class Generate {
	public static void main(String[] args) throws Exception {
		
		
		RDB rdb = RDBFactory.open();
		
		// postgresqlrdb.ddl("create table table2 (id integer, name varchar(50), data typea)");

		//System.out.println( r.getData());
		rdb.commit();

		String[] a = new String[3];
		a[0] = "src-test";
		a[1] = "/tables.properties";
		a[2] = "jdbc";
		//JavaClassBuilderFromFile.main(a);
	}

}
