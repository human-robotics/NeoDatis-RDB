package org.neodatis.rdb;

import org.neodatis.rdb.implementation.DefaultRDB;
import org.neodatis.rdb.query.DefaultSelectQuery;

public class TestMultiConnections {
	public static void main(String[] args) throws Exception {
		RDB rdb = RDBFactory.open("/OtherConnectionPool.txt");
		DefaultRDB rrr = (DefaultRDB) rdb;
		rrr.getSql().getConnecitonPool().getDatabase();
		System.out.println(rdb.select(new DefaultSelectQuery(Configuracao.class)));
		
		rdb.close();
	}
}
