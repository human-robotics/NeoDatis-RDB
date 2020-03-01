package org.neodatis.rdb.test.test1;

import java.util.Date;

import org.neodatis.rdb.RDB;
import org.neodatis.rdb.RDBFactory;
import org.neodatis.rdb.util.MemoryMonitor;

import junit.framework.TestCase;

public class TestInserts extends TestCase{

	
	public void testPerf() throws Exception{
		System.out.println("Ola");
		int size = 1000000;
		for(int i=0;i<size;i++){
			Test1 t1 = new Test1();
			t1.setDt(new Date());
			t1.setName("My name "+ i);
			
			RDB rdb = RDBFactory.open();
			rdb.insert(t1);
			rdb.commit();	
			
			if(i%100==0){
				System.out.println("\n\n\n"+MemoryMonitor.get(String.valueOf(i))+"\n\n\n");
			}
		}
	}
	public static void main(String[] args) throws Exception {
		new TestInserts().testPerf();
	}
	
}
