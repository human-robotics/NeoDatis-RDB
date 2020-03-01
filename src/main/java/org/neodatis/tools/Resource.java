package org.neodatis.tools;

/**
@author Olivier Smadja - osmadja@netcourrier.com
@date 01/01/2000 - creation
*/

import java.util.ResourceBundle;

import org.apache.log4j.Category;
import org.neodatis.rdb.implementation.DbSpecific;

/** Encapsulation of ResourceBundle to manage exception
    Resource name will be formatted like this : type.label
    if the string in not present in resource then return label
    */
public class Resource
{

	static Category _log = Category.getInstance(Resource.class.getName());
	/** The Resource bundle*/
	ResourceBundle _resource;

	/** The token separator. Default is "."*/
	char _cTokenSeparator;

	/** The default separator */
	final char _cDefaultSeparator = '.';

	/** A boolean value to know if resource is ok*/
	boolean _bIsOk;

	/** Constructor
	@param String The name of the resource file
	*/
	public Resource(String in_sFileName )
	{
		try
		{
			_resource = ResourceBundle.getBundle( in_sFileName );
			_log.info("DbSpecific keyset = "+ _resource.keySet());
			_log.info("ToLowerCase?  = "+ _resource.containsKey("ToLowerCase"));
			_bIsOk = true;
		}
		catch( java.util.MissingResourceException e )
		{
			System.out.println("Resource not found : " + in_sFileName);
			_bIsOk = false;
		}

		_cTokenSeparator = _cDefaultSeparator;
	}

	/** To change the separator
	@param char The new separator
	*/
	void setSeparator( char in_cNewSeparator )
	{
		_cTokenSeparator = in_cNewSeparator;
	}

	/** To check if the resource was initialized ok
	@return boolean true if ok
	*/
	public boolean isOk()
	{
		return _bIsOk;
	}


	/** Overrides getString to manage exceptions
	@param String The token name
	@return String The token value of its right part ex : label.Subject returns
	Subject if label.Subject not present in resource file
	@deprecated
	*/
	public String getString( String in_sToken )
	{
		String sTokenValue = null;
		try
		{
			sTokenValue = _resource.getString( in_sToken  );
		}
		catch( Exception e )
		{
			// The token was not found in resource so we are going to take
			// the last rigth part after the last separator :
			// if the separator is "." : and the token is menu.Open
			// we will return Open, so we take the rest of the string
			// after the last separator

			int nIndex = 0;
			System.out.println(in_sToken + " is missing	in resource file!");
			nIndex = in_sToken.lastIndexOf( _cTokenSeparator );
			if( nIndex != -1 )
			{
				sTokenValue	= in_sToken.substring( nIndex+1 );
			}
			else
			{
				throw new RuntimeException("Element '"+ in_sToken+"' not found in Resource File",e);
			}
		}

		return sTokenValue;

	}


	/** Overrides getString to manage exceptions
	@param String The token name
	@param String The default value
	@return String The token value of its right part ex : label.Subject returns
	Subject if label.Subject not present in resource file
	*/
	public String getString( String in_sToken , String in_sDefaultValue )
	{
		try
		{
			return _resource.getString( in_sToken  );
		}
		catch( Exception e )
		{
		    return in_sDefaultValue;
		}
	}

	/** Gets an int value from ressource
	@param String The token name
	@param int The dafaultValue if token not present
	@return int The token value of its right part ex : label.Subject returns
	-1 if label.Subject not present in resource file
	*/
	public int getInt( String in_sToken , int in_nDefaultValue)
	{
		try
		{
			return Integer.parseInt( getString(in_sToken) );
		}
		catch( NumberFormatException e1 )
		{
			return in_nDefaultValue;
		}
	}

	/** Gets a double value from ressource
	@param String The token name
		@param int The dafaultValue if token not present
	@return int The token value of its right part ex : label.Subject returns
	-1 if label.Subject not present in resource file
	*/
	public double getDouble( String in_sToken , double in_nDefaultValue)
	{
		try
		{
			return Double.parseDouble( getString(in_sToken) );
		}
		catch( NumberFormatException e1 )
		{
			return in_nDefaultValue;
		}
	}

	/** Gets an int value from ressource
	@param String The token name
	@param int The dafaultValue if token not present
	@return int The token value of its right part ex : label.Subject returns
	-1 if label.Subject not present in resource file
	*/
	public int getIntFromHexaString( String in_sToken , int in_nDefaultValue )
	{
		try
		{
			return Integer.parseInt( getString(in_sToken) , 16 );
		}
		catch( NumberFormatException e1 )
		{
			return in_nDefaultValue;
		}
	}

	/** Gets a boolean value from ressource
	@param String The token name
	@return boolean The token value of its right part ex : label.Subject returns
	*/
	public boolean getBoolean( String in_sToken )
	{
		System.out.println("Check for bool value :" + in_sToken + " " + getString(in_sToken) );
		return new Boolean( getString(in_sToken) ).booleanValue();
	}

	public static void main(String [] args )
	{
		Resource r = new Resource("JMail");
		System.out.println(r.getString("label.subject") );
		System.out.println( r.getString("label.blue.red"));

		Resource r2 = new Resource("JMail2");
		System.out.println( r2.getString("label.blue.red"));




	}
}