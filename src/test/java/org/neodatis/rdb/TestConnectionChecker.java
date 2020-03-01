package org.neodatis.rdb;

import org.neodatis.rdb.query.DefaultSelectQuery;

public class TestConnectionChecker {
	public static void main(String[] args) throws Exception {
		System.out.println("Oi");
		int i = 1;
		while (true) {
			try {
				RDB rdb = RDBFactory.open();
				System.out.println(rdb.select(new DefaultSelectQuery(Configuracao.class)).getObject(0)+" - Test "+ i++);
				rdb.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			Thread.sleep(20000);
		}

	}
}
