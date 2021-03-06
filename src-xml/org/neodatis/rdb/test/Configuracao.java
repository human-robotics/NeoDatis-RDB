// class generated by JConcept Database ClassBuilder 2.0
package org.neodatis.rdb.test;

// import JConcept Database library
import org.neodatis.rdb.*;

import org.neodatis.rdb.query.*;

import org.neodatis.rdb.implementation.*;

// For Date attribute
import java.util.Date;

// For BigDecimal attribute
import java.math.*;

public class Configuracao extends Object  implements DbObjectMapping 
{
	//** Database fields
	protected Long dbID;
	protected String dbCODIGO;
	protected String dbVALOR;
	protected String dbDESCRICAO;

	//** Construtor
	public Configuracao()
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
		return new DefaultDBTable("CONFIGURACAO");
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

	// Setter for attribute codigo
	public void setCodigo( String in_codigo ) 
	{
		dbCODIGO = in_codigo;
	}

	// Getter for attribute codigo
	public String getCodigo()
	{
		return dbCODIGO;
	}

	// Setter for attribute valor
	public void setValor( String in_valor ) 
	{
		dbVALOR = in_valor;
	}

	// Getter for attribute valor
	public String getValor()
	{
		return dbVALOR;
	}

	// Setter for attribute descricao
	public void setDescricao( String in_descricao ) 
	{
		dbDESCRICAO = in_descricao;
	}

	// Getter for attribute descricao
	public String getDescricao()
	{
		return dbDESCRICAO;
	}
	/** toString method*/
	public String toString()
	{

		StringBuffer sResult = new StringBuffer();
		sResult.append("\n[\n");
		sResult.append( "\t id : " ).append( getId()).append("\n");
		sResult.append( "\t codigo : " ).append( getCodigo()).append("\n");
		sResult.append( "\t valor : " ).append( getValor()).append("\n");
		sResult.append( "\t descricao : " ).append( getDescricao()).append("\n");
		sResult.append("]\n");
		return sResult.toString();
	}

}
	//** Database fields
