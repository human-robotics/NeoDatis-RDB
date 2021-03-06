// class generated by NeoDatis Database ClassBuilder 2.0
package org.neodatis.rdb.test.hr;

// import NeoDatis Database library
import org.neodatis.rdb.*;

import org.neodatis.rdb.query.*;

import java.util.Date;

import java.math.BigDecimal;

public class UserDBHelper{
	public static final DBTable table = new DefaultDBTable("user");

	public static final DBColumn ID =  new DefaultDBColumn( table , "id" , Long.class );
	public static final DBColumn UID =  new DefaultDBColumn( table , "uid" , String.class );
	public static final DBColumn NAME =  new DefaultDBColumn( table , "name" , String.class );
	public static final DBColumn EMAIL =  new DefaultDBColumn( table , "email" , String.class );
	public static final DBColumn PASSWORD =  new DefaultDBColumn( table , "password" , String.class );
	public static final DBColumn COMPANY =  new DefaultDBColumn( table , "company" , String.class );
	public static final DBColumn LAST_LOGIN =  new DefaultDBColumn( table , "last_login" , Date.class );
	public static final DBColumn CREATED =  new DefaultDBColumn( table , "created" , Date.class );
	public static final DBColumn UPDATED =  new DefaultDBColumn( table , "updated" , Date.class );
	public static final DBColumn CREATED_BY =  new DefaultDBColumn( table , "created_by" , String.class );
	public static final DBColumn UPDATED_BY =  new DefaultDBColumn( table , "updated_by" , String.class );
	public static final DBColumn LOGIN_ATTEMPTS =  new DefaultDBColumn( table , "login_attempts" , Long.class );
	public static final DBColumn STATUS =  new DefaultDBColumn( table , "status" , Long.class );
	public static final DBColumn PROFILE_UID =  new DefaultDBColumn( table , "profile_uid" , String.class );
	public static final DBColumn PARENT_USER_UID =  new DefaultDBColumn( table , "parent_user_uid" , String.class );
	public static final DBColumn USER_GROUP_UID =  new DefaultDBColumn( table , "user_group_uid" , String.class );

	//** To get the column list
	static public DBColumn [] getColumns()
	{
		DBColumn [] array = new DefaultDBColumn[16];
		array[0] = ID;
		array[1] = UID;
		array[2] = NAME;
		array[3] = EMAIL;
		array[4] = PASSWORD;
		array[5] = COMPANY;
		array[6] = LAST_LOGIN;
		array[7] = CREATED;
		array[8] = UPDATED;
		array[9] = CREATED_BY;
		array[10] = UPDATED_BY;
		array[11] = LOGIN_ATTEMPTS;
		array[12] = STATUS;
		array[13] = PROFILE_UID;
		array[14] = PARENT_USER_UID;
		array[15] = USER_GROUP_UID;
		return array;
	}
}
