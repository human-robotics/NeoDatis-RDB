// class generated by JConcept Database ClassBuilder 2.0
package com.mycompany.dbmapping;

// import JConcept Database library
import br.com.jconcept.database.*;

import br.com.jconcept.database.query.*;

import java.util.Date;

import java.math.BigDecimal;

public class ProductDBHelper{
	public static final DBTable table = new DefaultDBTable("PRODUCT");

	public static final DBColumn PRODUCT_ID =  new DefaultDBColumn( table , "PRODUCT_ID" , Long.class );
	public static final DBColumn NAME =  new DefaultDBColumn( table , "NAME" , String.class );
	public static final DBColumn DESCRIPTION =  new DefaultDBColumn( table , "DESCRIPTION" , String.class );
	public static final DBColumn UNIT_PRICE =  new DefaultDBColumn( table , "UNIT_PRICE" , Double.class );

	//** To get the column list
	static public DBColumn [] getColumns()
	{
		DBColumn [] array = new DefaultDBColumn[4];
		array[0] = PRODUCT_ID;
		array[1] = NAME;
		array[2] = DESCRIPTION;
		array[3] = UNIT_PRICE;
		return array;
	}
}
