package org.neodatis.rdb.query;


import java.util.List;
import java.util.ArrayList;

import org.neodatis.rdb.DBColumn;
import org.neodatis.rdb.ObjectReadyCallback;
import org.neodatis.rdb.SelectQuery;
import org.neodatis.rdb.SingleTableSelectQuery;
import org.neodatis.rdb.Where;
import org.neodatis.rdb.implementation.Util;

/** An object that contain all data to define a select query
<pre>
The class of the object type of objects that must return
The where Clause
The order by
The group by
The join informations
</pre>

@author Olivier Smadja - osmadja@netcourrier.com
@version 10/07/2002 - Olivier : creation


*/


public class DefaultSelectQuery implements SelectQuery
{
    private List _singleTableSelectList;

    /** The SQL Where - optional */
    Where _where;

    /** The SQL Order by - optional*/
    String _sOrderBy;

    /** The SQL Group by - optional*/
    String _sGroupBy;

    /** To indicate if select if distinct*/
    boolean _bIsDistinct;
    
    ObjectReadyCallback objectReadyCallback;

    /** Define a select with no information. The addSingleTableSelect must be used to add single selects
     */
    public DefaultSelectQuery()
    {
        init();
    }

    /** Define a select with a single select without where - Returns all elements
     * @param The type of objects that must be returned
     * Example :
     * new DefaultSelectQuery( Event.class ) defines the following select : "select * from event"
     */
    public DefaultSelectQuery(Class _objectType)
    {
        init();
        addSingleTableSelect(new DefaultSingleTableSelectQuery(_objectType)) ;
    }

    /**
     * @param The object to be return - For simple select
     * @param The SQL Where     - can be null
     */
    public DefaultSelectQuery(Class _objectType, Where in_where)
    {
        init();
        addSingleTableSelect(new DefaultSingleTableSelectQuery(_objectType)) ;
        _where = in_where;
    }

    /**
     * @param The object to be return - For simple select
     * @param The SQL Where     - can be null
     * @param The SQL Order By  - can be null
     */
    public DefaultSelectQuery(Class _objectType, Where in_where, String _sOrderBY)
    {
        init();
        addSingleTableSelect(new DefaultSingleTableSelectQuery(_objectType));

        _where = in_where;
        this._sOrderBy = _sOrderBY;
    }
    /**
     * @param The object to be return - For simple select
     * @param The SQL Where     - can be null
     * @param The SQL Order By  - can be null
     * @param The SQL Group by  - can be null
     */
    public DefaultSelectQuery(Class _objectType, Where in_where, String _sOrderBY, String _sGroupBy)
    {
        init();
        addSingleTableSelect(new DefaultSingleTableSelectQuery(_objectType)) ;
        _where = in_where;
        this._sOrderBy = _sOrderBY;
        this._sGroupBy = _sGroupBy;
    }

    /** Inits internal fields : The select array and the bDistinct boolean
     *
     */
    protected void init()
    {
        _singleTableSelectList = new ArrayList();
        _bIsDistinct = false;
    }

    /** Adds a singleTableSelect (select ... from table)
     * Can be used to create join select
     */
    public void addSingleTableSelect( SingleTableSelectQuery in_simpleSelect )
    {
        _singleTableSelectList.add( in_simpleSelect );
    }

    /** Adds a singleTableSelect (select ... from table)
     * Can be used to create join select
     */
    public SelectQuery addSingleTableSelect( Class clazz )
    {
        _singleTableSelectList.add( new DefaultSingleTableSelectQuery(clazz) );
        return this;
    }

    public Class getObjectType(int in_nSelectIndex) {
        return ((SingleTableSelectQuery) (_singleTableSelectList.get(in_nSelectIndex))).getObjectType();
    }

    public Where getWhere() {
        return _where;
    }

    public String getOrderBy() {
        return _sOrderBy;
    }

    public String getGroupBy() {
        return _sGroupBy;
    }

    public boolean isDistinct()
    {
        return _bIsDistinct;
    }

    public void setIsDistinct(boolean _bIsDistinct)
    {
        this._bIsDistinct = _bIsDistinct;
    }

    public List getSingleTableSelectList()
    {
        return _singleTableSelectList;
    }

    /** Returns an array of classes of objects that the Select must return
     * @return The list of classes
     */
    public Class [] getObjectTypes()
    {
        SingleTableSelectQuery singleTableSelectQuery = null;

        Class [] allClasses = new Class[ _singleTableSelectList.size() ];

        for( int nClass = 0 ; nClass < _singleTableSelectList.size() ; nClass ++ )
        {
            singleTableSelectQuery = (SingleTableSelectQuery) _singleTableSelectList.get(nClass);
            allClasses[nClass] = singleTableSelectQuery.getObjectType();
        }

        return allClasses;
    }
    
	public String[] getAliases() throws Exception {
        SingleTableSelectQuery singleTableSelectQuery = null;

        String [] aliases = new String[ _singleTableSelectList.size() ];

        for( int i=0;i<_singleTableSelectList.size() ; i ++ )
        {
            singleTableSelectQuery = (SingleTableSelectQuery) _singleTableSelectList.get(i);
            // use table order as alias to name fields
            aliases[i] = String.valueOf(i);
        }

        return aliases;
	}


    /** Sets The String describing the SQL WHERE
    <pre>Example " EVENT_ID = 15 "</pre>
    @param  The SQL WHERE - Can be null
    */
    public SelectQuery setWhere(Where in_where)
    {
        _where = in_where;
        return this;
    }

    /** Sets The String describing the SQL GROUP BY
    <pre>Example " EVENT_ID "</pre>
    @param The SQL GROUP BY - Can be null
    */
    public SelectQuery setGroupBy(String in_sGroupBy)
    {
        _sGroupBy = in_sGroupBy;
        return this;
    }

    /** Sets The String describing the SQL ORDER BY
    <pre>Example " EVENT_ID "</pre>
    @param The SQL ORDER BY - Can be null
    */
    public SelectQuery setOrderBy(String orderBy)
    {
        _sOrderBy = Util.getColumnName(orderBy);
        return this;
    }

    public SelectQuery setOrderByAsc(DBColumn orderBy)
    {
        _sOrderBy = Util.getColumnName(orderBy.getName()) + " asc";
        return this;
    }
    public SelectQuery setOrderByDesc(DBColumn orderBy)
    {
        _sOrderBy = Util.getColumnName(orderBy.getName()) + " desc";
        return this;
    }

    /**
     * Sets the where with AND, if no where is defines, w is assumed
     */
	public SelectQuery and(Where w) {
		if(getWhere()==null){
			setWhere(w);
		}else{
			getWhere().and(w);
		}
		
		return this;
	}
	public SelectQuery or(Where w) {
		if(getWhere()==null){
			setWhere(w);
		}else{
			getWhere().or(w);
		}
		return this;
	}


	public boolean onlyIds() {
		return false;
	}

	public ObjectReadyCallback objectCallback() {
		return objectReadyCallback;
	}

	public SelectQuery setObjectReadyCallback(ObjectReadyCallback callback) {
		this.objectReadyCallback = callback;
		return this;
	}

	
}
