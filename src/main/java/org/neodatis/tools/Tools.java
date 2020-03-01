package org.neodatis.tools;

import java.io.*;
import java.text.*;
import java.util.*;



/**
@author Olivier Smadja - osmadja@netcourrier.com
@date 01/01/2000 - creation
@update 22/08/2000 - adds fill string with white specific car
@version 18/09/2001 - adds getIndexOfStringInStringArray
**/
public class Tools
{
	/** Gets the index of a specified string in a string array(String[])
	@param String [] The string array
	@param String The string to look for
	@return int The index . -1 if string not found
	*/
	static public int getIndexOfStringInStringArray( String [] in_string_array , String in_sStringToLookFor )
	{

	    if( in_string_array == null || in_sStringToLookFor == null )
	    {
		return -1;
	    }

	    for( int i = 0 ; i < in_string_array.length ; i ++ )
	    {
		if( in_string_array[i].equals(in_sStringToLookFor) )
		{
		    return i;
		}
	    }
	    // If we reach this point, the string does not exist in the array
	    return -1;
	}

	/** Replace a string within a string
	@param String The String to modify
	@param String The Token to replace
	@param String The new Token
	@return String The new String
	@deprecated see StringUtils.replaceToken() method
	*/
	static public String replaceToken( String in_sSourceString , String in_sTokenToReplace , String in_sNewToken )
	{
		int nIndex = 0;
		boolean bHasToken = true;
		StringBuffer sResult = new StringBuffer( in_sSourceString );
		String sTempString = sResult.toString();
		int nOldTokenLength = in_sTokenToReplace.length();

		while(bHasToken)
		{
			nIndex = sTempString.indexOf( in_sTokenToReplace , nIndex );

			bHasToken = (nIndex != -1);

			if( bHasToken )
			{
				sResult.replace( nIndex , nIndex + nOldTokenLength , in_sNewToken );
				sTempString = sResult.toString();
			}

			nIndex = 0;

		}

		return sResult.toString();
	}

	/**
	@deprecated see StringUtils.getTokenBeginingWith() method
	*/
	static public String getTokenBeginingWith( String in_sSourceString , String in_sTokenStart )
	{
		StringTokenizer tok = new StringTokenizer( in_sSourceString , in_sTokenStart );

		try
		{
			tok.nextToken();

			String sResult = tok.nextToken();

			return sResult;
		}
		catch( java.util.NoSuchElementException e )
		{
			return null;
		}
	}

	static public String getStringFromFile( String in_sFileName )
	{
	 	String sLine = null;
	 	StringBuffer sString = new StringBuffer();
	 	BufferedReader fileReader = null;


	 	// If File is not valid
	 	if( in_sFileName == null )
		{
			return null;
		}
		try
		{
			FileReader f = new FileReader(in_sFileName );
			if( f == null )
			{
				return null;
			}

			fileReader = new BufferedReader( f );

			if( fileReader == null )
			{
				return null;
			}


			while( ( sLine = fileReader.readLine())!= null )
			{
				if(sLine!=null)
				{
					sString.append( sLine+"\n" );
				}
			}
		}
		catch(Exception e)
		{
			return null;
		}
		finally
		{
			try
			{
				if(fileReader!= null)
				{
					fileReader.close();
				}
			}
			catch(Exception e)
			{
				return null;
			}
		}

		return sString.toString();
	}


	/** Builds a new string using in source and filling all non filled char with
	** in_cChar till the result gets the correct size : in_nTotalSize
	@param String The source string
	@param char The character to be used to fill
	@param int The total desired new size
	@return The new String of size in_nTotalValue
	@example fillStartWithChar("aisa" , '*' , 10 ) => "******aisa"
	@deprecated use StringUtils.fillStartWithChar
	**/
	public static String fillStartWithChar( String in_sSource , char in_cChar , int in_nTotalSize )
	{
    String sSource = in_sSource;

    // Checks in the entry in null
    if( sSource == null )
    {
      sSource = "";
    }

		int nCurrentSize = sSource.length();

		// First check the size
		if ( nCurrentSize > in_nTotalSize )
		{
			return sSource.substring(0 , in_nTotalSize );
		}

		StringBuffer sResult = new StringBuffer();

		for( int i = 0 ; i < in_nTotalSize - nCurrentSize ; i ++ )
		{
			sResult.append( in_cChar );
		}

		sResult.append( sSource );

		return sResult.toString();
	}

	/** Builds a new string using in source and filling all non filled char with
	** in_cChar till the result gets the correct size : in_nTotalSize
	@param String The source string
	@param char The character to be used to fill
	@param int The total desired new size
	@return The new String of size in_nTotalValue
	@example fillStartWithChar("aisa" , '*' , 10 ) => "aisa******"
	@deprecated use StringUtils.fillStartWithChar
	**/
	public static String fillEndWithChar( String in_sSource , char in_cChar , int in_nTotalSize )
	{
    String sSource = in_sSource;

    // Checks in the entry in null
    if( sSource == null )
    {
      sSource = "";
    }

		int nCurrentSize = sSource.length();

		// First check the size
		if ( nCurrentSize > in_nTotalSize )
		{
			return sSource.substring(0 , in_nTotalSize );
		}

		StringBuffer sResult = new StringBuffer(sSource);

		for( int i = 0 ; i < in_nTotalSize - nCurrentSize ; i ++ )
		{
			sResult.append( in_cChar );
		}

		return sResult.toString();
	}

    /** Builds a string with a specific format of the date today + in_nDeltaDays
    @param int The number of days to add to today :0 will be today,
     -1 will be yesterday, 1 will be tomorrow
    @param String The Date format
    @return String The formatted String
    @deprecated use StringUtils.getStringOfDate
    **/
    static public String getStringOfDate( int in_sDeltaDays , String in_sDateFormatPattern)
    {
     	Date dtDate = new Date();

        // To get date of yesterday
        dtDate.setTime( dtDate.getTime() + (1000*60*60*24)*in_sDeltaDays );

        SimpleDateFormat dtFormater = new SimpleDateFormat( in_sDateFormatPattern );

        return dtFormater.format( dtDate );
    }

     /** Builds a string with the date format : 'DD/MM/YYYY' of the date today + in_nDeltaDays
    @param int The number of days to add to today :0 will be today,
     -1 will be yesterday, 1 will be tomorrow
    @return String The formatted String
    @deprecated use StringUtils.getStringOfDate
    **/
    static public String getStringOfDate( int in_sDeltaDays )
    {
    	return getStringOfDate( in_sDeltaDays , "dd/MM/yyyy" );
    }

    /** Builds a file object from a file name
     *  @param String The File name
     *  @return PrintStream
     */
    static public PrintStream createFileStream(String in_sFileName)
    {
	PrintStream file = null;
	try
	{
	    // Creates the object to write logs
	    file = new PrintStream( new FileOutputStream( in_sFileName , false ) );
	}
	catch( java.io.FileNotFoundException e)
	{
	    System.out.println("Error while creating "+	in_sFileName );
	}
	return file;
    }


    static public void main( String args[])
	{
        for(int i = -5 ; i < 6 ; i ++ )
        {
        	System.out.println( Tools.getStringOfDate(i) );
        }

        System.out.println( Tools.getStringOfDate(-2 , "dd/MM") );


		String s = "0123456#789 012345678901234565767890";

		String s2 = Tools.replaceToken( s , "0" , "ab"  );

	/*	//System.out.println( "Final result2 " + s2 );
		String sTemp = null;

		String test = "fsdjfsjhf#olivier#> efdkjd#aisa#.jdhfjsfh#pierre#";

		System.out.println( test );

		while( ( sTemp = getTokenBeginingWith( test , "#" ))  != null )
		{
			System.out.println( sTemp );
			test = Tools.replaceToken( test , "#"+sTemp+"#"  , "JavaConcept!" );
		}

		System.out.println( test );

		String str = "Aisa";
		String str2 = Tools.fillStartWithChar( str , '*' , 10 );
		String str3 = Tools.fillEndWithChar( str , '*' , 10 );
		System.out.println( str2 + "/" + str3 );
        */
	}

}
