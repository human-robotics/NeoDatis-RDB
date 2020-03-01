/**
 * 
 */
package br.com.ccr.siga.db;

import java.sql.SQLException;
import java.util.Date;

import org.neodatis.rdb.APIInternalException;
import org.neodatis.rdb.RDB;
import org.neodatis.rdb.RDBFactory;
import org.neodatis.rdb.implementation.DefaultRDB;
import org.neodatis.rdb.query.DefaultInsertQuery;

import com.mycompany.myobjects.ControlePraca;

/**
 * @author olivier
 *
 */
public class TestInsert {
	public static void main(String[] args) throws Exception {
		ControlePraca controle = new ControlePraca();
		controle.setNomePraca("Teste");
		controle.setDataHoraUltimaComunicacao(new Date());
		controle.setDescricaoMsg("Mensagem de descricao");
		controle.setIdPraca(new Long(1));
		
		RDB service = RDBFactory.open();
		service.insert(controle);
	}

}
