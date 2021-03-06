// class generated by NeoDatis Database ClassBuilder 2.0
package org.neodatis.rdb.mentor.model.meta;

// import NeoDatis Database library
import java.util.Date;

import org.neodatis.rdb.DBColumn;
import org.neodatis.rdb.DBTable;
import org.neodatis.rdb.query.DefaultDBColumn;
import org.neodatis.rdb.query.DefaultDBTable;

public class UsersDBHelper{
	public static final DBTable table = new DefaultDBTable("users");

	public static final DBColumn ID =  new DefaultDBColumn( table , "id" , Long.class );
	public static final DBColumn NAME =  new DefaultDBColumn( table , "name" , String.class );
	public static final DBColumn LOGIN =  new DefaultDBColumn( table , "login" , String.class );
	public static final DBColumn PASSWORD =  new DefaultDBColumn( table , "password" , String.class );
	public static final DBColumn CREATION_DATE =  new DefaultDBColumn( table , "creation_date" , Date.class );
	public static final DBColumn LAST_LOGIN_DATE =  new DefaultDBColumn( table , "last_login_date" , Date.class );
	public static final DBColumn STATUS =  new DefaultDBColumn( table , "status" , Long.class );
	public static final DBColumn PROFILE_ID =  new DefaultDBColumn( table , "profile_id" , Long.class );

	//** To get the column list
	static public DBColumn [] getColumns()
	{
		DBColumn [] array = new DefaultDBColumn[8];
		array[0] = ID;
		array[1] = NAME;
		array[2] = LOGIN;
		array[3] = PASSWORD;
		array[4] = CREATION_DATE;
		array[5] = LAST_LOGIN_DATE;
		array[6] = STATUS;
		array[7] = PROFILE_ID;
		return array;
	}
}
