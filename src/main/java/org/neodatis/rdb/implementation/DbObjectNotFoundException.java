package org.neodatis.rdb.implementation;
	
/**
 * Classe SqlObjectBuildQueryException - Exception thrown when incorrect field 
 * names are found
 * @author Olivier smadja - osmadja@netcourrier.com
 */


/** Exception provided to developper to manage empty request response
**/
public class DbObjectNotFoundException extends Exception
{
	public DbObjectNotFoundException( String in_sMessage )
	{
		super( "Database object not found : " + in_sMessage );
	}
}