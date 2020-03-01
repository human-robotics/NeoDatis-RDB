package com.mycompany.myobjects;

import java.util.List;

import junit.framework.TestCase;

import org.neodatis.rdb.RDB;
import org.neodatis.rdb.RDBFactory;
import org.neodatis.rdb.query.DefaultSelectQuery;
import org.neodatis.rdb.test.test1.Table1;
import org.neodatis.rdb.test.test1.Table2;

public class TestConnect extends TestCase {

	public void testConnect() throws Exception {

		RDB rdb = RDBFactory.open();

		System.out.println(rdb.select(new DefaultSelectQuery(Table1.class)).getData());
		
		Table1 t1 = new Table1();
		//t1.setId(new Long(1));
		//t1.setName("name " + System.currentTimeMillis());
		rdb.insert(t1);

		// System.out.println( r.getData());
		rdb.commit();
		
		Thread.sleep(1000);

	}

	public void testInsertWithBlob() throws Exception {

		RDB rdb = RDBFactory.open();

		List<Table2> tt = rdb.select(new DefaultSelectQuery(Table2.class)).getData();
		for(Table2 t2:tt){
			System.out.println(t2.getName() );
			
			long l = t2.getData().length;
			System.out.println(new String(t2.getData()));	
		}
		
		
		Table2 t2 = new Table2();
		t2.setId(new Long(1));
		t2.setName("name " + System.currentTimeMillis());
		t2.setData("test".getBytes());
		rdb.insert(t2);

		// System.out.println( r.getData());
		rdb.commit();
		
		Thread.sleep(1000);

	}
}
