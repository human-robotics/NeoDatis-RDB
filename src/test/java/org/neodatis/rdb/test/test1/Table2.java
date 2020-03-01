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

public class Table2 extends Object  implements DbObjectMapping 
{
	//** Database fields
	protected Long dbID;
	protected String dbNAME;
	protected byte[] dbDATA;

	//** Construtor
	public Table2()
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
		return new DefaultDBTable("table2");
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

	// Setter for attribute id
	public void setId( Long in_id ) 
	{
		dbID = in_id;
	}

	// Getter for attribute id
	public Long getId()
	{
		return dbID;
	}

	// Setter for attribute name
	public void setName( String in_name ) 
	{
		dbNAME = in_name;
	}

	// Getter for attribute name
	public String getName()
	{
		return dbNAME;
	}

	// Setter for attribute data
	public void setData( byte[] in_data ) 
	{
		dbDATA = in_data;
	}

	// Getter for attribute data
	public byte[] getData()
	{
		return dbDATA;
	}
	/** toString method*/
	public String toString()
	{

		StringBuffer sResult = new StringBuffer();
		sResult.append("\n[\n");
		sResult.append( "\t id : " ).append( getId()).append("\n");
		sResult.append( "\t name : " ).append( getName()).append("\n");
		sResult.append( "\t data : " ).append( getData()).append("\n");
		sResult.append("]\n");
		return sResult.toString();
	}

}
	//** Database fields
