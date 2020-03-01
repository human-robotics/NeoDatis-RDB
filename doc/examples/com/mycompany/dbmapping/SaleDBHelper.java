// class generated by JConcept Database ClassBuilder 2.0
package com.mycompany.dbmapping;

// import JConcept Database library
import br.com.jconcept.database.*;

import br.com.jconcept.database.query.*;

import java.util.Date;

import java.math.BigDecimal;

public class SaleDBHelper{
	public static final DBTable table = new DefaultDBTable("SALE");

	public static final DBColumn SALE_ID =  new DefaultDBColumn( table , "SALE_ID" , Long.class );
	public static final DBColumn CLIENT_ID =  new DefaultDBColumn( table , "CLIENT_ID" , Long.class );
	public static final DBColumn PRODUCT_ID =  new DefaultDBColumn( table , "PRODUCT_ID" , Long.class );
	public static final DBColumn SALE_DATE =  new DefaultDBColumn( table , "SALE_DATE" , Date.class );
	public static final DBColumn QUANTITY =  new DefaultDBColumn( table , "QUANTITY" , Long.class );

	//** To get the column list
	static public DBColumn [] getColumns()
	{
		DBColumn [] array = new DefaultDBColumn[5];
		array[0] = SALE_ID;
		array[1] = CLIENT_ID;
		array[2] = PRODUCT_ID;
		array[3] = SALE_DATE;
		array[4] = QUANTITY;
		return array;
	}
}
