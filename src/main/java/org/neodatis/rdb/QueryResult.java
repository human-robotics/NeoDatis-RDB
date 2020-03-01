package org.neodatis.rdb;

import java.util.List;

/*
* The simple interface that object returning query result mus implement
* @version 27/07/2002 - Olivier : creation
*
*/

public interface QueryResult
{
    /** To indicate that result List is the result of a single select*/
    public static final int SINGLE_SELECT = 0;

    /** To indicate that result List is the result of a joined select*/
    public static final int JOINED_SELECT = 1;

    /** To indicate that result List is the result of an insert*/
    public static final int INSERT = 2;

    /** To indicate that result List is the result of am update*/
    public static final int UPDATE = 3;

    /** To indicate that result List is the result of a delete*/
    public static final int DELETE = 4;

    /** To indicate that result List is the result of a custom select*/
    public static final int CUSTOM_SELECT = 5;

    /** The method to get result type
     * @param The result type
     * <pre>
     * - SINGLE_SELECT
     * - JOINED_SELECT
     * - INSERT
     * - UPDATE
     * - DELETE
     * - CUSTOM SELECT
     * <pre>
     * */
    public int getResultType();

    /**
     *
     * Return the number of elements in the result
     * @return The number of retirned objects
     *
     *
     */
    public long getNumberOfObjects();

    /**
     *
     * Returns a boolean indicating if there are data
     * @return true if there are data, false if not
     *
     */
    public boolean hasObjects();

    /**
     *
     * The method to get the object. This can be used for select result
     * @param in_nRowIndex The index of the element - Starts at 0
     *  <code>
     *  If you call getObject( 0 ) will return the first returned object
     *  </code>
     *
     *
     */
    public Object getObject( int in_nRowIndex );


    /**
     *
     * The method to get the object of the given type. This can be used for join select result
     * @param in_nRowIndex The index of the element - Starts at 0
     * @param in_objectClass The type of the object to get
     * @exception  IllegalArgumentException If object of the given type is not present
     * @exception  IllegalStateException If data has not been initialized - This can happen if this queryResult is not the
     * result of a select

     *  <code>
     *  If you call getObjectOfType( 0 , Client.class ) will return the object of type Cient at the first index, if exists
     *  </code>
     *
     *
     */
    public Object getObjectOfType( int in_nRowIndex , Class in_objectClass ) throws IllegalArgumentException , IllegalStateException;

    /**
     *
     * Return The data list , it can be a list of object, for the case of a single select.
     * <br> It can be null for the case of insert, update and delete.
     * <p>
     * It can be a list of map in the case of a join-select. In these case, the map keys will be the classes of the objects
     * <pre>
     * For example if you have done a join select between Client and product, it will return
     * a list of map. Each map will contain an object of type Client, which will be accessible doing the following call :
     * ( (Map) getData().get(0) ).get(Client.class) and an object of type Product...
     * It is recomended to use the <a href="#getObjectOfType">getObjectOfType</a> method
     *
     * </pre>
     * </p>
     *
     *
     */
    public List getData();
    
    /**
     * In case of join, return a list of object of type
     * @param c
     * @return
     */
    public List getListOf(Class c);


    /**
        *
        * The method to get the value of a field. This method must be used with Custom select query
        *
        *  <pre>
        *  If you create a Query like :
        *
        * CustomSelectQuery selectQuery = new CustomSelectQuery("select count(*) count , max(client_id) max from CLIENT");
        *
        * QueryResult result = dbService.executeQuery( selectQuery );
        *
        * Then you can call result.getFieldValue(0 , "max") to get a Long value containing the max
        *  </pre>
        *
        * @param in_nRowIndex The index of the element - Starts at 0
        * @param in_sFiledName The field name to retrieve
        * @return The object
        * @exception  IllegalArgumentException If field with the given name is not present
        * @exception  IllegalStateException If result is not of a CustomSelectQuery
        *
        *
        */
       public Object getFieldValue( int in_nRowIndex , String in_sFieldName ) throws IllegalArgumentException , IllegalStateException;

}
