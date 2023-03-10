// class generated by NeoDatis Database ClassBuilder 2.0
package org.neodatis.rdb.sqllite.meta;

// import NeoDatis Database library
import org.neodatis.rdb.*;

import org.neodatis.rdb.query.*;

import java.util.Date;

import java.math.BigDecimal;

public class UserDBHelper{
	public static final DBTable table = new DefaultDBTable("User");

	public static final DBColumn ID =  new DefaultDBColumn( table , "id" , Long.class );
	public static final DBColumn NAME =  new DefaultDBColumn( table , "name" , String.class );
	public static final DBColumn GENDER =  new DefaultDBColumn( table , "gender" , Long.class );
	public static final DBColumn EMAIL =  new DefaultDBColumn( table , "email" , String.class );
	public static final DBColumn BIRTH_DATE =  new DefaultDBColumn( table , "birth_date" , String.class );

	//** To get the column list
	static public DBColumn [] getColumns()
	{
		DBColumn [] array = new DefaultDBColumn[5];
		array[0] = ID;
		array[1] = NAME;
		array[2] = GENDER;
		array[3] = EMAIL;
		array[4] = BIRTH_DATE;
		return array;
	}
}