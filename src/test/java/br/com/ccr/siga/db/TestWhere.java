/**
 * 
 */
package br.com.ccr.siga.db;

import java.util.Date;

import junit.framework.TestCase;

import org.neodatis.rdb.Where;
import org.neodatis.rdb.query.WhereImpl;
import org.neodatis.rdb.query.WhereOperator;

import com.mycompany.myobjects.metadata.ControlePracaDBHelper;

/**
 * @author olivier
 *
 */
public class TestWhere extends TestCase{
	public void testIn(){
		String in = "'p1','p2'";
		//Where where = new DefaultWhere(ControlePracaDBHelper.DATA_HORA_ULTIMA_COMUNICACAO, WhereOperator.GREATER_THAN, new Date());
		Where where = new WhereImpl(ControlePracaDBHelper.NOME_PRACA,WhereOperator.IN,in);
		String s = where.getSql(false);
		System.out.println(s);
		assertTrue(s.indexOf(" IN ('p1','p2')")!=-1);
		
	}
}
