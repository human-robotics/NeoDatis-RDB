package org.neodatis.rdb.implementation;
	
/**
 * Classe DatabaseProblemException - Exception thrown when the database access fails
 * @author Olivier smadja - osmadja@netcourrier.com
 * @version 16/09/2000 - creation
 */


/** Exception provided to developper to manage database problem
**/
public class DatabaseProblemException extends RuntimeException
{
	public DatabaseProblemException( String in_sMessage )
	{
		super( "Database problem : " + in_sMessage );
	}
}