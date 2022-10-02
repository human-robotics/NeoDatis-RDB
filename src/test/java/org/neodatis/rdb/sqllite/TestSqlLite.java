package org.neodatis.rdb.sqllite;

import java.util.Date;
import java.util.List;

import org.neodatis.rdb.RDBFactory;
import org.neodatis.tools.app.NeoDatis;

public class TestSqlLite {

	public static void insertUserWithDate() throws Exception {
		RDBFactory.open().close();

		List<UserWithDate> users = UserWithDate.DB.findAll();

		System.out.println("Users with date are " + users);

		UserWithDate user1 = new UserWithDate();
		user1.setName("Olivier Smadja");
		user1.setEmail("osmadja@gmail.com");
		user1.setGender(1L);
		user1.setBirthDate(new Date());

		user1.save();

		System.out.println("User id is " + user1.getId());
	}
	
	public static void insertUser() throws Exception {
		RDBFactory.open().close();

		List<User> users = User.DB.findAll();

		System.out.println("Users are " + users);

		User user1 = new User();
		user1.setName("Olivier Smadja");
		user1.setEmail("osmadja@gmail.com");
		user1.setGender(1L);
		user1.setBirthDate(new Date().toString());

		user1.save();

		System.out.println("User id is " + user1.getId());
	}

	public static void main(String[] args) throws Exception {
		insertUserWithDate();

	}
}
