package org.neodatis.rdb.query;


import java.util.*;

import org.neodatis.rdb.QueryResult;


/** A basic implementation of the QueryResult interface

*@version 27/07/2002 - Olivier : creation
*/
public class CustomSelectQueryResult extends DefaultQueryResult
{

    /** The list of types of the column's select*/
    Class [] columnTypes;

    /** The list of column names*/
    String [] columnNames;

    public CustomSelectQueryResult( List in_data , Class [] in_typeList , String [] in_columnNames )
    {
        super( CUSTOM_SELECT , in_data );

        columnTypes       = in_typeList;
        columnNames    = in_columnNames;
    }


    /**
     *
     * The method to get the object of the given type. This can be used for join select result
     * @param The index of the element - Starts at 0
     * @param The type of the object to get
     * @exception  IllegalArgumentException If object of the given type is not present
     * @exception  IllegalStateException If data has not been initialized - This can happen if this queryResult is not the
     * result of a select
     *  <code>
     *  If you call getObjectType( 0 , Client.class ) will return the object of type Cient at the first index, if exists
     *  </code>
     *
     *
     */
    public Object getObjectOfType( int in_nIndex , Class in_objectClass ) throws IllegalArgumentException , IllegalStateException
    {
        throw new IllegalStateException("This result query does not suport this method - It is a CustomSelectQueryResult");
    }

    /**
     *
     * To get the object of the specified index with the specified column name
     * @param in_nRowIndex The row index - Starts in 0
     * @param The Name of the column
     */
    public Object getObject( int in_nRowIndex , String in_sColumnName )
    {
        Map map = null;

        map = (Map) getObject(in_nRowIndex);

        return map.get( in_sColumnName );
    }

    /**
     *
     * To get the object of the specified index with the specified column name
     * @param in_nRowIndex The row index - Start in 0
     * @param in_nColumnIndex The index of the column , start in 0
     */
    public Object getObject( int in_nRowIndex , int in_nColumnIndex )
    {
        Map map = null;

        map = (Map) getObject(in_nRowIndex);

        return map.get( columnNames[in_nColumnIndex] );


    }

    /**
     *
     * Return the column Names
     * @return an array of string
     *
     */
    public String [] getColumnNames()
    {
        return columnNames;
    }

    /**
     *
     * Return the column Types
     * @return an array of class
     *
     */
    public Class [] getColumnTypes()
    {
        return columnTypes;
    }


    public String toString()
    {
        StringBuffer sResult = new StringBuffer();

        sResult.append( Arrays.asList(columnNames)).append("\n");
        sResult.append( Arrays.asList(columnTypes)).append("\n");
        sResult.append( super.toString() ).append("\n");

        return sResult.toString();
    }

}
