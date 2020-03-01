package org.neodatis.rdb.access;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.neodatis.ConnectionPoolInfo;
import org.neodatis.rdb.RDBFactory;

public class TestDbAccess {
	
	
	@Test
	public void testCreateConnectionWithClass() throws Exception {
		
		ConnectionPoolInfo cpi = new ConnectionPoolInfo(ConnectionPoolInfo.DatabaseType.mysql,"Karteira1", "root", "oli123", "com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/karteira", 10, 60000, true, "select 1 from db_status", 60000);
		RDBFactory.useAsDefault(cpi);
		
		System.out.println("Connected with ConnectionPoolInfo Class");
		
		List<Users> users = Users.DB.findAll();
		System.out.println(users);
		
		Assert.assertNotEquals(0,  users.size());
		
	}


}
