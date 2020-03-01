/*
 NeoDatis ODB : Native Object Database (odb.info@neodatis.org)
 Copyright (C) 2007 NeoDatis Inc. http://www.neodatis.org

 "This file is part of the NeoDatis ODB open source object database".

 NeoDatis ODB is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 NeoDatis ODB is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.neodatis.tools;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

/**
 * Some basic string operations
 * 
 * @author osmadja
 * 
 */
public class StringUtils {

	/**
	 * Replace a string within a string
	 * 
	 * @param in_sSourceString
	 *            The String to modify
	 * @param in_sTokenToReplace
	 *            The Token to replace
	 * @param in_sNewToken
	 *            The new Token
	 * @return String The new String
	 * @exception RuntimeException
	 *                where trying to replace by a new token and this new token
	 *                contains the token to be replaced
	 */
	static public String replaceToken(String in_sSourceString, String in_sTokenToReplace, String in_sNewToken) {
		// Default is to replace all -> -1
		return replaceToken(in_sSourceString, in_sTokenToReplace, in_sNewToken, -1);
	}

	/**
	 * Replace a string within a string
	 * 
	 * @param in_sSourceString
	 *            The String to modify
	 * @param in_sTokenToReplace
	 *            The Token to replace
	 * @param in_sNewToken
	 *            The new Token
	 * @param in_nNbTimes
	 *            The number of time, the replace operation must be done. -1
	 *            means replace all
	 * @return String The new String
	 * @exception RuntimeException
	 *                where trying to replace by a new token and this new token
	 *                contains the token to be replaced
	 */
	static public String replaceToken(String in_sSourceString, String in_sTokenToReplace, String in_sNewToken, int in_nNbTimes) {
		int nIndex = 0;
		boolean bHasToken = true;
		StringBuffer sResult = new StringBuffer(in_sSourceString);
		String sTempString = sResult.toString();
		int nOldTokenLength = in_sTokenToReplace.length();
		int nTimes = 0;

		// To prevent from replace the token with a token containg Token to
		// replace
		if (in_nNbTimes == -1 && in_sNewToken.indexOf(in_sTokenToReplace) != -1) {
			throw new RuntimeException("Can not replace by this new token because it contains token to be replaced");
		}

		while (bHasToken) {
			nIndex = sTempString.indexOf(in_sTokenToReplace, nIndex);

			bHasToken = (nIndex != -1);

			if (bHasToken) {
				// Control number of times
				if (in_nNbTimes != -1) {
					if (nTimes < in_nNbTimes) {
						nTimes++;
					} else {
						// If we already replace the number of times asked then
						// go out
						break;
					}
				}

				sResult.replace(nIndex, nIndex + nOldTokenLength, in_sNewToken);
				sTempString = sResult.toString();
			}

			nIndex = 0;

		}

		return sResult.toString();
	}

	/**
	 * Tokenize a string and returns an array of string containting all tokens
	 * 
	 * @param in_sString
	 *            The string to be tokenized
	 * @param in_sSeparators
	 *            The separators
	 * @return String []
	 */
	public static String[] tokenize(String in_sString, String in_sSeparators) {
		String[] aResult = null;
		// The tokenizer
		StringTokenizer tokenizer = new StringTokenizer(in_sString, in_sSeparators);

		// To count tokens
		int i = 0;

		// Now we know how many tokens there are, so builds the array
		aResult = new String[tokenizer.countTokens()];

		while (tokenizer.hasMoreElements()) {
			// aResult[i++] = (String) tokenizer.nextElement();
			aResult[i++] = (String) tokenizer.nextToken();
		}

		return aResult;
	}

	/**
	 * If escape==true, then remove $.
	 * 
	 * @param e
	 * @param escape
	 * @return
	 */
	public static String exceptionToString(Throwable e, boolean escape) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String s = sw.getBuffer().toString();
		if (escape) {
			s = s.replaceAll("\\$", "-");
		}
		return s;
	}

	/**
	 * Method used to capitalize string and remove '_'
	 * 
	 * @example StringUtils.capitalize("CLIENT_NAME" , true) return clientName
	 * 
	 * @example StringUtils.capitalize("CLIENT_NAME" , false) return ClientName
	 * 
	 * @param in_sString
	 *            The string to capitalize
	 * 
	 * @param in_bFirstOneIsLowerCase
	 *            true if first char must be lower case, false if must be upper
	 *            case
	 * 
	 * @return The new String
	 */

	static public String capitalize(String in_sString, boolean in_bFirstOneIsLowerCase)

	{

		StringBuffer sNewString = new StringBuffer();

		char cChar = 0;

		boolean bFirstChar = true;

		boolean bWordIsStarting = false;

		for (int i = 0; i < in_sString.length(); i++)

		{

			cChar = in_sString.charAt(i);

			// If it the char

			if (bFirstChar)

			{

				// If it is the first char, then sets it to lower case

				if (cChar != '_')

				{

					if (in_bFirstOneIsLowerCase)

					{

						sNewString.append(("" + cChar).toLowerCase());

						bFirstChar = false;

					}

					else

					{

						sNewString.append(("" + cChar).toUpperCase());

						bFirstChar = false;

					}

				}

				// else nothing to do, let s pull the char

			}

			else

			{

				// If char is a separator then just mark the fact that a word is
				// going to begin, so we will set next char to Uppercase

				if (cChar == '_' && !bFirstChar)

				{

					bWordIsStarting = true;

				}

				else

				{

					String sToAppend = "" + cChar;

					if (bWordIsStarting)

					{

						sToAppend = sToAppend.toUpperCase();

						bWordIsStarting = false;

					}

					else

					{

						sToAppend = sToAppend.toLowerCase();

					}

					sNewString.append(sToAppend);

				}

			}

		}

		return sNewString.toString();

	}
	
	/** Builds a new string using in source and filling all non filled char with

	** in_cChar till the result gets the correct size : in_nTotalSize

	@param String The source string

	@param char The character to be used to fill

	@param int The total desired new size

	@return The new String of size in_nTotalValue

	@example fillStartWithChar("aisa" , '*' , 10 ) => "aisa******"

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



}
