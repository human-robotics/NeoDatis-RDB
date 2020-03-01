package org.neodatis.rdb.implementation;
	
/**
 * Classe SqlObjectBuildQueryException - Exception thrown when incorrect field 
 * names are found
 * @author Olivier smadja - osmadja@netcourrier.com
 */


/** Exception that is thrown when the object has his SqlStatement
* object not initialized
**/
public class SqlStatementNotInitializedException extends Exception
{
	public SqlStatementNotInitializedException( String in_sMessage )
	{
		super( in_sMessage );
	}
}