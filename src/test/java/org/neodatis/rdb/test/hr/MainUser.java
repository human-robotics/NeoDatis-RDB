package org.neodatis.rdb.test.hr;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.neodatis.rdb.RDB;
import org.neodatis.rdb.RDBFactory;
import org.neodatis.rdb.query.W;
import org.neodatis.tools.SqlInjection;

public class MainUser {
	public static void main(String[] args) throws Exception {
		RDB rdb = RDBFactory.open();	
		List<User> users = User.DB.find(W.equal(UserDBHelper.EMAIL, "osmadja2@gmail.com"));
		if(users.isEmpty()) {
			User u = new User();
			u.setName("name");
			u.setPassword("password");
			u.setCreated(new Date());
			u.setUid(UUID.randomUUID().toString());
			u.setEmail("olivier@neodatis.org");
			u.setStatus(1L);
			u.save();
		}
		List<User> users2 = User.DB.find(W.equal(UserDBHelper.EMAIL, "olivier@neodatis.org"));
		System.out.println("Users for osmadja2@gmail.com="+users2.size());
		
		List<User> users3 = User.DB.find(W.equal(UserDBHelper.EMAIL, new SqlInjection().escapeSql("' or 1=1 or '1")));
		System.out.println("Users for sql injection="+users3.size());

	}
}
