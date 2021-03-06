// class generated by NeoDatis Database ClassBuilder 2.0
package org.neodatis.rdb.mentor.model.meta;

// import NeoDatis Database library
import org.neodatis.rdb.DBColumn;
import org.neodatis.rdb.DBTable;
import org.neodatis.rdb.query.DefaultDBColumn;
import org.neodatis.rdb.query.DefaultDBTable;

public class ConfigurationDBHelper{
	public static final DBTable table = new DefaultDBTable("configuration");

	public static final DBColumn ID =  new DefaultDBColumn( table , "id" , Long.class );
	public static final DBColumn CODE =  new DefaultDBColumn( table , "code" , String.class );
	public static final DBColumn STRINGVALUE =  new DefaultDBColumn( table , "stringvalue" , String.class );
	public static final DBColumn INTVALUE =  new DefaultDBColumn( table , "intvalue" , Long.class );

	//** To get the column list
	static public DBColumn [] getColumns()
	{
		DBColumn [] array = new DefaultDBColumn[4];
		array[0] = ID;
		array[1] = CODE;
		array[2] = STRINGVALUE;
		array[3] = INTVALUE;
		return array;
	}
}
