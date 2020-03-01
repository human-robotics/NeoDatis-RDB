package org.neodatis.rdb.util.generation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.neodatis.rdb.implementation.DatabaseColumn;
import org.neodatis.tools.StringUtils;

/**
* Description : Builds a java file containing a java class templating
* a database table , Builds also a Helper class representing the table with all its attribites
* @author : Olivier Smadja - osmadja@netcourrier.com
* @date : 16/08/2000 - creation
* @date 13/01/2001 - Removes the toUpper of the table name. MySql compatibility
* @version 09/07/2002 - Changes The packages - and generates simples classes - data bean

*/

public abstract class ClassBuilderFromDb
{
    public final String _DATABASE_FIELD_PREFIX = "db";
    
    private String sourceDirectory;

    /** To Write The main java class to file*/
	private PrintStream mainClassStream;

    /** To Write The helper java class to file*/
	private PrintStream helperClassStream;

	/** The name of database table to reflect*/
	protected String tableName;

	/** The name of class */
	protected String className;

	/** The name of the primary key field*/
	protected String keyFieldName;
	
	protected List<DatabaseColumn> fields;

	/** The name of the base class */
	protected String baseClassName;

    /** The name of the main package to create */
	protected String packageName;

    /** The name of the helper package to create */
	protected String helperPackageName;

	protected CodeGenerationListener codeGenerationListener;
	/**
	Constructor
	 * @param codeGenerator 
	@param String The name of the table
	@param String The name of the class to be created
	@param String The name of the table to reflect
	@param String The name of the primary key field
	@param String The name of the package
	@param String The name of the base class - can be null
    @param String The name of the helper package
	 * @throws Exception 
	*/
	public ClassBuilderFromDb(String sourceDirectory, String in_sTableName,String in_sClassName,String in_sKeyField , String in_sPackageName , String in_sBaseClassName , String in_sHelperPackageName, CodeGenerationListener codeGenerator) throws Exception
	{
		init(sourceDirectory,  in_sTableName,in_sClassName, in_sKeyField , in_sPackageName , in_sBaseClassName , in_sHelperPackageName, codeGenerator );
	}


	/** Initialisation of the object 
	 * @param codeGenerator 
	 * @throws Exception */
	private void init(String sourceDirectory, String in_sTableName,String in_sClassName , String in_sKeyFieldName , String in_sPackageName , String in_sBaseClassName , String in_sHelperPackageName, CodeGenerationListener codeGenerator  ) throws Exception
	{

        System.out.println("Src Directory = " + sourceDirectory );
		System.out.println("Table     = " + in_sTableName );
        System.out.println( "Class     = " + in_sClassName );
        System.out.println( "PrimaryK  = " + in_sKeyFieldName );
        System.out.println( "Package   = " + in_sPackageName );
        System.out.println( "BaseClass = " + in_sBaseClassName );
        System.out.println( "Helper Package = " + in_sHelperPackageName );

        // To respect file table name
        codeGenerationListener = codeGenerator;
        this.sourceDirectory = sourceDirectory;
        tableName 			= in_sTableName;
		className 			= in_sClassName;
		keyFieldName			= in_sKeyFieldName;
        packageName           = in_sPackageName;
		baseClassName         = in_sBaseClassName;
        helperPackageName     = in_sHelperPackageName;

		fields = new ArrayList<DatabaseColumn>();

        createClasses();

	}

	/** Execute class creation
	 * @throws Exception */
	public void createClasses() throws Exception
	{

		initStreams();

		try
		{
			// Builds the main java class
            buildMainClass();

            // Builds the helper class
            buildHelperClass();
		}
		catch( SQLException e )
		{
			e.printStackTrace();
		}

		closeFile();

	}

	void closeFile()
	{
		if ( mainClassStream!= null)
		{
			mainClassStream.close();
		}
	}

	void initStreams()
	{

        String sMainClassFileName   = sourceDirectory+"/"+ packageName.replace('.','/')+"/" +className + getFileExtension();
        String sHelperClassName     = sourceDirectory+"/"+ helperPackageName.replace('.','/')+"/"+ getClassName()+ "DBHelper" + getFileExtension();
        
        mainClassStream    = buildOneStream( sMainClassFileName );
        helperClassStream  = buildOneStream( sHelperClassName );

	}

    protected PrintStream buildOneStream(String fileName)
	{
		// Gets the abstract file to checks if exists
		File abstractFile = new File( fileName );
        PrintStream stream = null;

		// If the file exists , deletes it
		if( abstractFile.exists() )
		{
			abstractFile.delete();
			
			/*
			// Builds the new File name
			String sNewFileName = fileName + ".backup";

			// Builds the abstract object
			File abstractFile2 = new File( sNewFileName );

			// Renames the file
			boolean bResult = abstractFile.renameTo(abstractFile2);

			if( bResult )
			{
				System.out.println("File "+fileName+" has been renamed to "+ sNewFileName);
			}
			else
			{
				System.out.println("Unable to rename file "+fileName );
			}
			*/
		}
		
		File parent = abstractFile.getParentFile();
		if(!parent.exists()){
			parent.mkdirs();
		}

		
		try
		{
			// Creates the object to write logs
			stream = new PrintStream( new FileOutputStream( fileName , false ) );

		}

		catch( FileNotFoundException e)
		{
			System.out.println("Error while creating "+	fileName );
		}

        return stream;
	}

	/** Gets the file in which will be written the class
	@return PrintStream The stream to write*/
	PrintStream getMainFile()
	{
		return mainClassStream;
	}

    /** Gets the file in which will be written the helper class
	@return PrintStream The stream to write*/
	PrintStream getHelperFile()
	{
		return helperClassStream;
	}

	/** Gets the number of fields for the class to be created
	@return int The number of field*/
	public int getFieldNumber()
	{
		try
		{
			return fields.size();
		}
		catch( NullPointerException e )
		{
			return 0;
		}
	}


	/** Retrieves table infos and save info in Vectors
	 * @throws Exception */
	abstract public void getTableInfos() throws SQLException, Exception;

	/** Gets a specific field name
	@return String The field name
	@param int The index of the field*/
	public String getFieldName(int in_nIndex)
		throws NullPointerException
	{
		return fields.get( in_nIndex ).getName();
	}

	/** Gets a specific field type
	@return String The field type
	@param int The index of the field*/
	public String getFieldType(int in_nIndex)
		throws NullPointerException
	{
		return fields.get( in_nIndex ).getDataTypeAsString();
	}

    /** Gets a specific field type precision
	@return String The field type
	@param int The index of the field*/
	public int getFieldTypePrecision(int in_nIndex)
		throws NullPointerException
	{
		return fields.get( in_nIndex ).getDataLength();
	}

    /** Gets a specific field type scale
	@return String The field type
	@param int The index of the field*/
	public int getFieldTypeScale(int in_nIndex)
		throws NullPointerException
	{
		return fields.get( in_nIndex ).getScale();
	}

    /** Builds the Setter method for the in_nIndex field*/
	String buildSetter( int in_nIndex )
	{
		String sDbFieldName = getFieldName( in_nIndex ) ;
        String sFieldName = StringUtils.capitalize( getFieldName( in_nIndex ) , true );
        String sMethodName = StringUtils.capitalize( getFieldName( in_nIndex ) , false );


        StringBuffer sMethod = new StringBuffer();
		sMethod.append( "\n\t// Setter for attribute ");
		sMethod.append( sFieldName );
		sMethod.append( "\n\tpublic void set");
		sMethod.append( sMethodName );
		sMethod.append( "( " );
		sMethod.append( getFieldType( in_nIndex ) );
		sMethod.append( " in_" );
		sMethod.append( sFieldName );
		sMethod.append( " ) \n");
		sMethod.append( "\t{\n");
		sMethod.append( "\t\t" ).append(_DATABASE_FIELD_PREFIX);
		sMethod.append( sDbFieldName );
		sMethod.append( " = in_" );
		sMethod.append(  sFieldName );
		sMethod.append( ";\n");
		sMethod.append( "\t}");

		return sMethod.toString();

	}

	/** Builds the Getter method for the in_nIndex field*/
	String buildGetter( int in_nIndex )
	{
        String sDbFieldName = getFieldName( in_nIndex ) ;
        String sFieldName = StringUtils.capitalize( getFieldName( in_nIndex ) , true );
        String sMethodName = StringUtils.capitalize( getFieldName( in_nIndex ) , false );

		StringBuffer sMethod = new StringBuffer();

		sMethod.append( "\n\t// Getter for attribute ");
		sMethod.append( sFieldName );
		sMethod.append( "\n\tpublic ");
		sMethod.append( getFieldType(in_nIndex) );
		sMethod.append( " get" );
		sMethod.append( sMethodName );
		sMethod.append( "()\n");
		sMethod.append( "\t{\n");
		sMethod.append( "\t\treturn " );
        sMethod.append( _DATABASE_FIELD_PREFIX );
		sMethod.append(  sDbFieldName );
		sMethod.append( ";\n");
		sMethod.append( "\t}");

		return sMethod.toString();

	}

    public void buildMainClass()
		throws Exception
	{
		getTableInfos();
		buildClassHeader();
		buildClassAttributes();
		buildClassMethods();
        buildToStringMethod();
		buildEndOfClass(getMainFile());
	}

    public void buildHelperClass()
		throws SQLException
	{
		buildClassHeaderForHelperClass();
		buildClassAttributesForHelperClass();
		buildClassMethodsForHelperClass();
        //buildToStringMethod();
		buildEndOfClass(getHelperFile());
	}

	public void buildClassHeader()
	{
		getMainFile().println( "// class generated by JConcept Database ClassBuilder 2.0");
		getMainFile().println( "package " + packageName + ";\n");
		getMainFile().println( "// import JConcept Database library");
		getMainFile().println( "import org.neodatis.rdb.*;\n");
		getMainFile().println( "import org.neodatis.rdb.query.*;\n");
		getMainFile().println( "import org.neodatis.rdb.implementation.*;\n");
        
		getMainFile().println( "// For Date attribute");
		getMainFile().println( "import java.util.Date;\n");
        getMainFile().println( "// For BigDecimal attribute");
		getMainFile().println( "import java.math.*;\n");


        getMainFile().print( "public class " + getClassName() );

        if( baseClassName != null )
        {
            getMainFile().print( " extends " + baseClassName + " " );
        }

        getMainFile().println( " implements DbObjectMapping " );

		getMainFile().println( "{" );
	}

    public void buildClassHeaderForHelperClass()
	{
		getHelperFile().println( "// class generated by NeoDatis Database ClassBuilder 2.0");
		getHelperFile().println( "package " + (helperPackageName==null?packageName:helperPackageName) + ";\n");
		getHelperFile().println( "// import NeoDatis Database library");
		getHelperFile().println( "import org.neodatis.rdb.*;\n");
        getHelperFile().println( "import org.neodatis.rdb.query.*;\n");
        getHelperFile().println( "import java.util.Date;\n");
        getHelperFile().println( "import java.math.BigDecimal;\n");

        getHelperFile().print( "public class " + getClassName() + "DBHelper");

		getHelperFile().println( "{" );
	}

	public void buildClassAttributes()
	{
		getMainFile().println( "\t//** Database fields" );

		for( int i = 0 ; i < getFieldNumber() ; i ++ )
		{
			getMainFile().println( "\tprotected " + getFieldType(i) + " "+ _DATABASE_FIELD_PREFIX + getFieldName(i) + ";" );
		}
	}

    public void buildClassAttributesForHelperClass()
	{
		getMainFile().println( "\t//** Database fields" );

        getHelperFile().println( "\tpublic static final DBTable table = new DefaultDBTable(\"" + getTableName() + "\");" );

        getHelperFile().println();

        for( int i = 0 ; i < getFieldNumber() ; i ++ )
		{
			getHelperFile().println( "\tpublic static final DBColumn " + getFieldName(i) + " =  new DefaultDBColumn( table , \""+ getFieldName(i)  + "\" , " + getFieldType(i) + ".class );" );
		}
	}


    public void buildToStringMethod()
	{
        String sFieldName = null;
        String sMethodName = null;


		getMainFile().println( "\t/** toString method*/" );
        getMainFile().println( "\tpublic String toString()" );
        getMainFile().println( "\t{\n" );
        getMainFile().println( "\t\tStringBuffer sResult = new StringBuffer();" );
        getMainFile().println( "\t\tsResult.append(\"\\n[\\n\");");

		for( int i = 0 ; i < getFieldNumber() ; i ++ )
		{
            sFieldName = StringUtils.capitalize( getFieldName( i ) , true );
            sMethodName = "get"+StringUtils.capitalize( getFieldName( i ) , false ) + "()";
			getMainFile().println( "\t\tsResult.append( \"\\t " +  sFieldName + " : \" ).append( "+ sMethodName + ").append(\"\\n\");");
		}
        getMainFile().println( "\t\tsResult.append(\"]\\n\");");
        getMainFile().println( "\t\treturn sResult.toString();");
        getMainFile().println( "\t}\n" );
	}

	public void buildClassMethods()
	{
		buildConstrutor();

		buildGetPrimaryKeyMethod();
        buildGetTableNameMethod(getMainFile());
        buildEqualsMethod( getMainFile() );

		buildSetterAndGetter();
	}

    public void buildClassMethodsForHelperClass()
	{
        buildGetColumnList( getHelperFile() );
	}

	public void buildConstrutor()
	{
		getMainFile().println( "\n\t//** Construtor" );

		getMainFile().println( "\tpublic " + getClassName() + "()");
		getMainFile().println( "\t{");
		getMainFile().println( "\t}");
	}

	public void buildGetPrimaryKeyMethod()
	{
		getMainFile().println( "\n\t//** To get the primary key field name" );

		getMainFile().println( "\tpublic PrimaryKey getPrimaryKey()");
		getMainFile().println( "\t{");
		getMainFile().println( "\t\treturn new DefaultPrimaryKey(\""+ getKeyFieldName().toUpperCase() + "\");" );
		getMainFile().println( "\t}");
	}

    public void buildGetTableNameMethod(PrintStream in_stream)
	{
		in_stream.println( "\n\t//** To get the table name" );

		in_stream.println( "\tpublic DBTable getTable()");
		in_stream.println( "\t{");
		in_stream.println( "\t\treturn new DefaultDBTable(\"" + getTableName() + "\");" );
		in_stream.println( "\t}");
	}

    public void buildEqualsMethod(PrintStream in_stream)
	{
		in_stream.println( "\n\t//** To check equality" );

		in_stream.println( "\tpublic boolean equals(Object in_object)");
		in_stream.println( "\t{");
        in_stream.println( "\t\tDbObjectMapping objectToCompare = (DbObjectMapping) in_object;" );
        in_stream.println( "\t\ttry \n\t\t{" );
		in_stream.println( "\t\t\treturn getPrimaryKey().getValue(this).equals(objectToCompare.getPrimaryKey().getValue(objectToCompare));" );
        in_stream.println( "\t\t} \n\t\tcatch(Exception e)\n\t\t{" );
        in_stream.println( "\t\t\treturn false; \n\t\t}" );
		in_stream.println( "\t}");
	}

    public void buildGetColumnList(PrintStream in_stream)
	{
		in_stream.println( "\n\t//** To get the column list" );

		in_stream.println( "\tstatic public DBColumn [] getColumns()");
		in_stream.println( "\t{");
		in_stream.println( "\t\tDBColumn [] array = new DefaultDBColumn["+ getFieldNumber()+"];" );
        for( int i = 0 ; i < getFieldNumber() ; i ++ )
        {
            in_stream.println( "\t\tarray["+i+"] = " + getFieldName(i) + ";" );
        }
        in_stream.println( "\t\treturn array;" );
		in_stream.println( "\t}");
	}

	public void buildSetterAndGetter()
	{
		getMainFile().println( "\n\t//** Setters and Getters" );

		for( int i = 0 ; i < getFieldNumber() ; i ++ )
		{
			// Setter
			getMainFile().println( buildSetter( i ) );

			// Getter
			getMainFile().println( buildGetter( i ) );
		}

	}

	public void buildEndOfClass(PrintStream in_strean )
	{
		in_strean.println( "}");
	}

	public String getFileExtension()
	{
		return ".java";
	}

	public String getKeyField()
	{
		return "dbTABLE_NAME";
	}


	/** Gets the class name
	*/
	public String getClassName()
	{
		return className;
	}

	/** Gets the table name to reflect
	*/
	public String getTableName()
	{
		return tableName;
	}

	/** Gets the name of the keyField*/
	public String getKeyFieldName()
	{
		return keyFieldName;
	}


}
