package org.neodatis.rdb.util.generation;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.neodatis.rdb.implementation.DatabaseMetaInformation;
import org.neodatis.rdb.implementation.DefaultConnectionPool;
import org.neodatis.rdb.implementation.Sql;
/**
* Description : Builds a java file containing a java class templating
* a database table
* @author : Olivier Smadja - osmadja@netcourrier.com
* @date : 16/08/2000 - creation
* @version 09/07/2002 - Olivier : Changes the philosophy :

*/

public class JavaClassBuilderFromDbOracle extends ClassBuilderFromDb
{

	/**
		Constructor
		@param sourceDirectory 
		@param String The name of the package of the class to be created
		@param String The name of the class to be created
		@param String The name of the table to reflect
		@param String The name of the primary key field
	 * @throws Exception 
	*/
	public JavaClassBuilderFromDbOracle(String sourceDirectory, String in_sPackageName , String in_sClassName , String in_sTableNameToReflect , String in_sKeyField , CodeGenerationListener codeGenerationListener) throws Exception
	{
        super(sourceDirectory, in_sTableNameToReflect , in_sClassName , in_sKeyField , in_sPackageName , null , null , codeGenerationListener);
	}

	/**
	Constructor
	@param String The name of the package of the class to be created
	@param String The name of the class to be created
	@param String The name of the table to reflect
	@param String The name of the primary key field
	@param String The base class name. The default is DbObject
    @param The name of the package of helper class
	 * @throws Exception 
	*/
	public JavaClassBuilderFromDbOracle(String sourceDirectory, String in_sPackageName , String in_sClassName , String in_sTableNameToReflect , String in_sKeyField , String in_sBaseClassName , String in_sHelperPackageName ) throws Exception
	{
		super(sourceDirectory, in_sTableNameToReflect , in_sClassName , in_sKeyField , in_sPackageName , in_sBaseClassName , in_sHelperPackageName, null);

	}

	/** Inits the object
	 * @throws Exception */
	private void init(String in_sPackageName , String in_sBaseClassName ) throws Exception
	{
		packageName = in_sPackageName;
		baseClassName = in_sBaseClassName;

		// Actually creates the class
		super.createClasses();
	}

	/** Get table info : field name, field type , filed precision
	 * @throws Exception 
    public void getTableInfos()	throws Exception
	{
		Sql sql = null;
        ResultSet rset = null;
        String sColumnName = null;
        String sColumnType = null;
        int nDataPrecision = -1;
        int nDataScale = -1;

        StringBuffer sSelect = new StringBuffer();
        sSelect.append("SELECT COLUMN_NAME , DATA_TYPE , DATA_PRECISION , DATA_SCALE FROM USER_TAB_COLUMNS WHERE TABLE_NAME = '");
        sSelect.append(getTableName().toUpperCase()).append("'");

        boolean tableExist = false;
        try {
            sql = new Sql("default");

            rset = sql.select(sSelect.toString());

            while( rset.next() )
            {
            	tableExist = true;
            	sColumnName     = rset.getString("COLUMN_NAME");
                sColumnType     = rset.getString("DATA_TYPE");
                nDataPrecision  = rset.getInt("DATA_PRECISION");
                nDataScale      = rset.getInt("DATA_SCALE");

                
                ( sColumnName , sColumnType , nDataPrecision , nDataScale );
                System.out.println("Name : " + sColumnName + " Type : " + sColumnType + " prec " + nDataPrecision + " Scale = " + nDataScale );
            }
        } 
        finally {
        	if(!tableExist){
        		System.out.println("\n\nError : Table "+getTableName()+" does not exist\n\n");
        	}
            if( sql!=null )
            {
                sql.close();
            }
        }

    }
    */
	
	public void getTableInfos() throws SQLException {
		String tableName = getTableName();
		String schema = "";
		if(tableName.indexOf(".")!=-1){
			String[] tokens = tableName.split("\\.");
			schema = tokens[0];
			tableName = tokens[1];
		}
		super.fields = new DatabaseMetaInformation().getTableColumnsGeneric(schema, tableName);
		
		if(codeGenerationListener!=null){
			codeGenerationListener.newTable(schema, tableName, fields);
		}
	}

	public String getFieldType(int in_nIndex)
	{
		String sDbType  = super.getFieldType( in_nIndex );
        int nPrecision  = super.getFieldTypePrecision( in_nIndex );
        int nScale      = super.getFieldTypeScale( in_nIndex );

		if ( sDbType.startsWith("VARCHAR") )
		{
			return "String";
		}

		if ( sDbType.startsWith("CHAR") )
		{
			return "String";
		}

        if ( sDbType.startsWith("NUMBER") )
		{
			if( nScale > 0 )
            {
                return "BigDecimal";
            }
            else
            {
                return "Long";
            }
		}

        if ( sDbType.startsWith("FLOAT") )
		{
            return "BigDecimal";
		}

		if ( sDbType.startsWith("DATE") )
		{
			return "Date";
		}
		if ( sDbType.startsWith("DATE") )
		{
			return "Date";
		}
		if ( sDbType.startsWith("DATE") )
		{
			return "Date";
		}
		if ( sDbType.startsWith("TIMESTAMP") )
		{
			return "Date";
		}
		if ( sDbType.startsWith("BLOB") )
		{
			return "byte[]";
		}
		throw new RuntimeException("Table " + tableName + " : don't know how to convert field of type " + sDbType + " to java");

	}


	public static void main( String [] args ) throws Exception
	{
        int nNbArgs = args.length;

		if ( nNbArgs < 4 )
		{
			displayHelpMessage();
		}
		else
		{
			String directory 	= args[0];
			String sPackage 	= args[1];
			String sClassName 	= args[2];
			String sTableName 	= args[3];
			String sKeyFieldName= args[4];

			System.out.println("\nJConcept Class Builder for Java 2.0\n");
			System.out.println("\n creating java class :\n");
			System.out.println("\t - SrcDir \t= "+directory);
			System.out.println("\t - Package \t= "+sPackage);
			System.out.println("\t - Class name \t= "+sClassName);
			System.out.println("\t - Table name \t= "+sTableName);
			System.out.println("\t - Primary key\t= "+sKeyFieldName);
			System.out.println("\n\n");

			JavaClassBuilderFromDbOracle builder = new JavaClassBuilderFromDbOracle(directory, sPackage,sClassName,sTableName,sKeyFieldName , null);
		}

        DefaultConnectionPool.getDefaultInstance().close();

	}

	static void displayHelpMessage()
	{
		String sPackage 	= "br.com.jconcept.test";
		String sClassName 	= "User";
		String sTableName 	= "USER";
		String sKeyFieldName= "USERID";

		System.out.println("\nJConcept Class Builder for Java 2.0\n");
		System.out.println("\n Must pass 4 parameters :");
		System.out.println("\t - Package \t\tex : "+sPackage);
		System.out.println("\t - Class name \tex : "+sClassName);
		System.out.println("\t - Table name \tex : "+sTableName);
		System.out.println("\t - Primary key\tex : "+sKeyFieldName);
	}
}