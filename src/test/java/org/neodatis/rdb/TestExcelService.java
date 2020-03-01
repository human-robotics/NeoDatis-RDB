package org.neodatis.rdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neodatis.rdb.layout.ILabel;
import org.neodatis.rdb.layout.LayoutInfo;
import org.neodatis.rdb.query.W;
import org.neodatis.rdb.test.meta.ConfiguracaoDBHelper;
import org.neodatis.tools.StringUtils;


public class TestExcelService {
	public static void main(String[] args) throws Exception {
		Service<Configuracao> service = new Service<Configuracao>(Configuracao.class);
		
		ILabel label = new ILabel() {
			
			public String getLabel(String s) {
				return StringUtils.replaceToken(s, "db", "");
			}
		};
		
		List<DBColumn> columns = new ArrayList<DBColumn>();
		columns.add(ConfiguracaoDBHelper.ID);
		columns.add(ConfiguracaoDBHelper.CODIGO);
		columns.add(ConfiguracaoDBHelper.DESCRICAO);
		
		int nb = service.allToExcel(W.empty(), null, new LayoutInfo("Config List", "configs.xlsx",columns, label));
		
	}
}
