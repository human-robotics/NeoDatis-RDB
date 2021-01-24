package org.neodatis.rdb.test.hr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLSyntaxErrorException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.ws.ServiceMode;

import org.junit.Before;
import org.junit.Test;
import org.neodatis.rdb.RDB;
import org.neodatis.rdb.RDBFactory;
import org.neodatis.rdb.Where;
import org.neodatis.rdb.query.W;
import org.neodatis.tools.SqlInjection;

public class TestSqlInjection {

	public static String EMAIL = "test@neodatis.org";

	@Before
	public void setup() throws Exception {
		RDB rdb = RDBFactory.open();
		List<User> users = User.DB.find(W.equal(UserDBHelper.EMAIL, EMAIL));

		if (users.isEmpty()) {
			User u = new User();
			u.setName("name");
			u.setPassword("password");
			u.setCreated(new Date());
			u.setUid(UUID.randomUUID().toString());
			u.setEmail(EMAIL);
			u.setStatus(1L);
			u.setCompany("NeoDatis");
			u.setLastLogin(new Date());
			u.setUpdatedBy("updated_by");
			u.setParentUserUid(UUID.randomUUID().toString());
			u.setLoginAttempts(1000L);
			u.save();
		}
		rdb.close();
	}

	@Test
	public void testWithWhere() throws Exception {
		// This query , if there is sql injection vulnerability should return x users
		Where w1 = W.equal(UserDBHelper.EMAIL, "' or 1=1 or '1");
		List<User> users = User.DB.find(w1);
		System.out.println("Users for Sql Injection with where.: " + w1.getSql(true) + " : " + users.size());
		// should be 0
		assertEquals(0, users.size());

	}

	@Test
	public void testInsert() throws Exception {

		User u = new User();
		u.setName("name_" + UUID.randomUUID().toString());
		u.setPassword("'; drop table user;'");
		u.setCreated(new Date());
		u.setUid(UUID.randomUUID().toString());
		u.setEmail("insert@neodatis.org");
		u.setStatus(1L);
		u.setCompany("NeoDatis");
		u.setLastLogin(new Date());
		u.setUpdatedBy("updated_by");
		u.setParentUserUid(UUID.randomUUID().toString());
		u.setLoginAttempts(1000L);
		u.save();

		// check if user has been created
		Where w1 = W.equal(UserDBHelper.NAME, u.getName());
		List<User> users = User.DB.find(w1);
		System.out.println("Users for after insert: " + users.size());
		// should be 1
		assertEquals(1, users.size());
		users.get(0).delete();

		users = User.DB.find(w1);
		System.out.println("Users for after delete: " + users.size());
		// should be 0
		assertEquals(0, users.size());

	}

	@Test
	public void testWithComment() throws Exception {
		// This query , if there is sql injection vulnerability should return x users
		Where w1 = W.equal(UserDBHelper.EMAIL, "' --");
		List<User> users = User.DB.find(w1);
		System.out.println("Users for Sql Injection with comment.: " + w1.getSql(true) + " : " + users.size());
		// should be 0
		assertEquals(0, users.size());

		w1 = W.equal(UserDBHelper.EMAIL, "\\' --");
		users = User.DB.find(w1);
		System.out.println("Users for Sql Injection with comment.: " + w1.getSql(true) + " : " + users.size());
		// should be 0
		assertEquals(0, users.size());
	}

	@Test
	public void testUnionSelect() throws Exception {
		// This query , if there is sql injection vulnerability should return x users
		Where w1 = W.equal(UserDBHelper.EMAIL, "' UNION SELECT * from user--");
		List<User> users = User.DB.find(w1);
		System.out.println("Users for Sql Injection with union select.: " + w1.getSql(true) + " : " + users.size());
		// should be 0
		assertEquals(0, users.size());
	}

	@Test
	public void testInOrderBy() throws Exception {
		try {
			// This query , if there is sql injection vulnerability should return x users
			Where w1 = W.equal(UserDBHelper.EMAIL, EMAIL);
			List<User> users = User.DB.find(w1, "order by name; select name from user");
			System.out.println("Users for Sql Injection with union select.: " + w1.getSql(true) + " : " + users.size());
			// should be 0
			assertEquals(0, users.size());
		} catch (SQLSyntaxErrorException e) {
			// TODO: handle exception
		}
	}

	@Test
	public void testInOrderBy2() throws Exception {
		try {
			// This query , if there is sql injection vulnerability should return x users
			Where w1 = W.equal(UserDBHelper.EMAIL, EMAIL);
			List<User> users = User.DB.find(w1, "order by name union all select * from user");
			System.out.println("Users for Sql Injection with union select.: " + w1.getSql(true) + " : " + users.size());
			// should be 0
			assertEquals(0, users.size());
		} catch (SQLSyntaxErrorException e) {
			// TODO: handle exception
		}
	}

	@Test
	public void testInOrderByWithBackspace() throws Exception {
		try {
			// This query , if there is sql injection vulnerability should return x users
			Where w1 = W.equal(UserDBHelper.EMAIL, EMAIL);
			String s = new String(new byte[] { 8, 8, 8, 8, 8, 8 });
			;
			List<User> users = User.DB.find(w1, s);
			System.out.println("Users for Sql Injection with union select.: " + w1.getSql(true) + " : " + users.size());
			// should be 0
			assertTrue(false);
		} catch (SQLSyntaxErrorException e) {
			// TODO: handle exception
		}
	}
}
