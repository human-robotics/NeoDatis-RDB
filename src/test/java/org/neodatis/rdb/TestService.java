package org.neodatis.rdb;

import java.util.List;


public class TestService {
	public static void main(String[] args) throws Exception {
		Service<Configuracao> service = new Service<Configuracao>(Configuracao.class);
		List<Configuracao> configs = service.findAll();
		System.out.println(configs);
		
		Configuracao c = service.findById(39L);
		System.out.println(c);
		
		c.setDescricao(c.getDescricao()+" - updated");
		service.save(c);
	}
}
