package org.neodatis.rdb.query;

import org.neodatis.rdb.SimpleSelectQuery;

/** An object that contain all data to define a simple select query without where , order by ...
<pre>
The class of the object type of objects that must return
The alias
</pre>

@author Olivier Smadja - osmadja@netcourrier.com
@version 12/07/2002 - Olivier : creation
*/


public class DefaultSimpleSelectQuery implements SimpleSelectQuery
{
    /** The Object type that select must return*/
    Class _objectType;

    /** The Alias - Optional*/
    String _sAlias;

    public DefaultSimpleSelectQuery(Class _objectType)
    {
        this._objectType = _objectType;
    }

    public DefaultSimpleSelectQuery(Class _objectType, String _sAlias)
    {
        this._objectType = _objectType;
        this._sAlias = _sAlias;
    }

    public Class getObjectType() {
        return _objectType;
    }

    public void setObjectType(Class _objectType) {
        this._objectType = _objectType;
    }

    public String getAlias() {
        return _sAlias;
    }

    public void setAlias(String _sAlias) {
        this._sAlias = _sAlias;
    }
}
