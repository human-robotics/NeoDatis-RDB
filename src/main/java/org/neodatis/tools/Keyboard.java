package org.neodatis.tools;

import java.io.IOException;
/** A class to get keyboard input
 * Title:        JConcept
 * Description:  All Jconcept libraries
 * Copyright:    Copyright (c) 1999
 * Company:      JConcept
 * @author Olivier Smadja
 * @version creation 04/06/2001
 */

public class Keyboard
{
    static public void clear()
	throws IOException
    {
//      getString()
    }


    static public char getChar()
	throws IOException
    {
	byte [] bytes = new byte[1];
	byte [] bytes2 = new byte[1];
	int nLength = System.in.read(bytes , 0 , 1  );

        // Get the \n or \r
        System.in.read(bytes2 , 0 , 1  );


	return (char) bytes[0];
    }

    static public String getString()
	throws IOException
    {
	StringBuffer sString = new StringBuffer();
	char cInput = 0 ;

	while( cInput != '\n' && cInput != '\r' )
	{
	    cInput = (char) System.in.read();

            if( cInput != '\n' && cInput != '\r' )
            {
              sString.append( cInput );
            }

	}
	return sString.toString();
    }

    static public String getString(String in_sLabel)
	throws IOException
    {
	System.out.println( "=> " + in_sLabel );

	return getString();
    }

    static public String getString(String in_sLabel , String in_sDefault )
	throws IOException
    {
	System.out.print( "=> " + in_sLabel + " ('"+in_sDefault+"')");

	String sInput = getString();

	//System.out.println( "returned by getString : '" + sInput + "'" );
	if( sInput.length() == 0 || sInput.charAt(0) == '\n' || sInput.charAt(0) == '\r')
	{
	    //System.out.println( "returned " + in_sDefault );
	    return in_sDefault;
	}

	//System.out.println( "returned " + sInput );
	return sInput;
    }



    public static void main(String[] args)
	throws Exception
    {
	char c = Keyboard.getChar();
	System.out.println( " c = " + c  );
	String s = Keyboard.getString("Test" , "olivier");

	System.out.println( " c = " + c + "  String = " + s );

    }
}