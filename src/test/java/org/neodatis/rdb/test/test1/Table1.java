// class generated by JConcept Database ClassBuilder 2.0
package org.neodatis.rdb.test.test1;

// import JConcept Database library
import org.neodatis.rdb.*;

import org.neodatis.rdb.query.*;

import org.neodatis.rdb.implementation.*;

// For Date attribute
import java.util.Date;

// For BigDecimal attribute
import java.math.*;

public class Table1 extends Object  implements DbObjectMapping 
{
	//** Database fields

	//** Construtor
	public Table1()
	{
	}

	//** To get the primary key field name
	public PrimaryKey getPrimaryKey()
	{
		return new DefaultPrimaryKey("ID");
	}

	//** To get the table name
	public DBTable getTable()
	{
		return new DefaultDBTable("PUBLIC.TABLE1");
	}

	//** To check equality
	public boolean equals(Object in_object)
	{
		DbObjectMapping objectToCompare = (DbObjectMapping) in_object;
		try 
		{
			return getPrimaryKey().getValue(this).equals(objectToCompare.getPrimaryKey().getValue(objectToCompare));
		} 
		catch(Exception e)
		{
			return false; 
		}
	}

	//** Setters and Getters
	/** toString method*/
	public String toString()
	{

		StringBuffer sResult = new StringBuffer();
		sResult.append("\n[\n");
		sResult.append("]\n");
		return sResult.toString();
	}

}
	//** Database fields
