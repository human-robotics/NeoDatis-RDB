/**
 * 
 */
package br.com.ccr.siga.db;

import junit.framework.TestCase;

import org.neodatis.rdb.QueryResult;
import org.neodatis.rdb.RDB;
import org.neodatis.rdb.RDBFactory;
import org.neodatis.rdb.query.DefaultSelectQuery;
import org.neodatis.rdb.test.test1.Test1;

/**
 * @author olivier
 * 
 */
public class TestConnectionPool extends TestCase {
	public void testIn() throws Exception {
		RDB rdb = RDBFactory.open();
		QueryResult r = rdb.executeQuery(new DefaultSelectQuery(Test1.class));
		System.out.println(r.getData());
		rdb.close();
	}

}
