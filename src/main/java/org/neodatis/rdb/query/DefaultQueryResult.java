package org.neodatis.rdb.query;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Category;
import org.neodatis.rdb.DbObjectMapping;
import org.neodatis.rdb.QueryResult;


/** A basic implementation of the QueryResult interface

*@version 27/07/2002 - Olivier : creation
*/
public class DefaultQueryResult implements QueryResult
{
    /** Creates the root */
    static Category _log = Category.getInstance(DefaultQueryResult.class.getName());


    /** To store the result type*/
    protected int _nResultType;

    /** To store data*/
    protected List _data;


    public DefaultQueryResult( int in_nResultType , List in_data )
    {
        _nResultType = in_nResultType;
        _data = in_data;

    }

    /** The method to get result type
     * @param The result type
     * <pre>
     * - SINGLE_SELECT
     * - JOINED_SELECT
     * - INSERT
     * - UPDATE
     * - DELETE
     * - CUSTOM_SELECT
     * <pre>
     * */
    public int getResultType()
    {
        return _nResultType;
    }

    /**
     *
     * Return the number of elements in the result
     * @return The number of retirned objects
     *
     *
     */
    public long getNumberOfObjects()
    {
        if( _data != null )
        {
            return _data.size();
        }

        return 0;
    }

    /**
     *
     * Returns a boolean indicating if there are data
     * @return true if there are data, false if not
     *
     */
    public boolean hasObjects()
    {
        return getNumberOfObjects() >  0;
    }

    /**
     *
     * The method to get the object. This can be used for select result
     * @param The index of the element - Starts at 0
     *  <code>
     *  If you call getObject( 0 ) will return the first returned object
     *  </code>
     *
     *
     */
    public Object getObject( int in_nRowIndex )
    {
        if( getResultType() != SINGLE_SELECT )
        {
            _log.debug("Warning : This result is not a simple select. You are retrieving a complex data set"  );
            _log.debug("Result Type is : " + getResultType() + " And Object type is " + _data.get(in_nRowIndex).getClass().getName() );
        }

        return _data.get(in_nRowIndex);
    }

    /**
     *
     * Checks if the object is contained in the result. - The default checks uses the primary key for equals
     * @param in_object - The object must implements a DbObjectMapping
     * @return true if contains object
     *
     *
     */
    public boolean contains(DbObjectMapping in_object)
    {
        switch(getResultType())
        {
            case SINGLE_SELECT:return _data.contains(in_object);
            case JOINED_SELECT:
            {
                for( int i = 0 ; i < _data.size() ; i ++ )
                {
                    if( getObjectOfType(i , in_object.getClass() ).equals(in_object) )
                    {
                        return true;
                    }
                }
            }
            break;
        }

        return false;
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
    public Object getObjectOfType( int in_nRowIndex , Class in_objectClass ) throws IllegalArgumentException , IllegalStateException
    {
        if( _data != null && getResultType() == JOINED_SELECT )
        {
            _log.debug("Class is " + _data.get(in_nRowIndex).getClass() );
            Object object = ( ( Map ) _data.get(in_nRowIndex) ).get(in_objectClass);

            if( object == null )
            {
                throw new IllegalArgumentException("Class " + in_objectClass.getName() + " does not exist in result query");
            }

            return object;
        }
        throw new IllegalStateException("This result query does not have data or is not the result of a joined select ");
    }

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
    public Object getFieldValue( int in_nRowIndex , String in_sFieldName ) throws IllegalArgumentException , IllegalStateException
    {
        if( _data != null && getResultType() == CUSTOM_SELECT )
        {
            Object object = ( ( Map ) _data.get(in_nRowIndex) ).get(in_sFieldName.toUpperCase());

            if( object == null )
            {
				object = ( ( Map ) _data.get(in_nRowIndex) ).get(in_sFieldName.toLowerCase());
				
				if( object == null ){
					throw new IllegalArgumentException("field " + in_sFieldName + " does not exist in result query");
				}
                
            }

            return object;
        }
        throw new IllegalStateException("This result query does not have data or is not the result of CustomQuerySelect");
    }


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
    public List getData()
    {
        return _data;
    }

    public String toString()
    {
        if(_data != null )
        {
            return _data.toString();
        }
        return "Result without data";
    }

	public List getListOf(Class c) {
		if(_data==null || _data.isEmpty()){
			return new ArrayList();
		}
		List result = new ArrayList();
		for(Object o:_data){
			if(o instanceof Map){
				Map m = (Map) o;
				Object oo = m.get(c);
				if(oo!=null){
					result.add(oo);
				}
			}
		}
		return result;
	}
}
