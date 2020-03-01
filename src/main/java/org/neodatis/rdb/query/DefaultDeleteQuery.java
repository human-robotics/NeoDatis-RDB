package org.neodatis.rdb.query;

import org.neodatis.rdb.DbObjectMapping;
import org.neodatis.rdb.DeleteQuery;
import org.neodatis.rdb.Where;

/** An object that contain all data to build the delete query
* The object containing data - must implements DbObjectMapping
@version 10/07/2002 - Olivier : creation
*/

public class DefaultDeleteQuery implements DeleteQuery
{
    /** The Object that contains data*/
    DbObjectMapping _object;

    /** The Where to make object*/
    Where _where;

    /** The object type to delete*/
    Class _objectType;

    public DefaultDeleteQuery(DbObjectMapping _object)
    {
        this._object = _object;
    }

    public DefaultDeleteQuery(Class in_objectType, Where in_where ) {
        _objectType = in_objectType;
        _where = in_where;
    }

    public DbObjectMapping getObject() {
        return _object;
    }

    public Where getWhere() {
        return _where;
    }

    public Class getObjectType() {
        return _objectType;
    }
}
