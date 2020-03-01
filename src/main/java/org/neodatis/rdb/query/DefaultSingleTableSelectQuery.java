package org.neodatis.rdb.query;

import org.neodatis.rdb.DbObjectMapping;
import org.neodatis.rdb.SingleTableSelectQuery;

/** An object that contain all data to define a simple select query without where , order by ...
<pre>
The class of the object type of objects that must return
The alias
</pre>

@author Olivier Smadja - osmadja@netcourrier.com
@version 12/07/2002 - Olivier : creation
*/


public class DefaultSingleTableSelectQuery implements SingleTableSelectQuery
{
    /** The Object type that select must return*/
    Class _objectType;

    public DefaultSingleTableSelectQuery(Class _objectType)
    {
        this._objectType = _objectType;
        init();
    }

    protected void init()
    {
        // nothing to do
    }
    public Class getObjectType()
    {
        return _objectType;
    }

    public void setObjectType(Class _objectType) {
        this._objectType = _objectType;
    }

    /** Gets the alias to be used in select , if no alias was defined then builds one automatically : <my><table_name>
     * @return The alias name
     * @exception InstantiationException If occured problem while creating an instance of the object
     * @exception IllegalAccessException If deleteQuery was not well initialized
     */
    public String getAlias() throws IllegalAccessException , InstantiationException{

        return ( (DbObjectMapping) _objectType.newInstance()).getTable().getAlias();
    }
}
