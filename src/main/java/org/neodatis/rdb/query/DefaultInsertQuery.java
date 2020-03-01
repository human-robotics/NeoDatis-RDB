package org.neodatis.rdb.query;

import org.neodatis.rdb.DbObjectMapping;
import org.neodatis.rdb.InsertQuery;

/** An object that contain all data to build the insert query
* The object containing data - must implements DbObjectMapping
@version 10/07/2002 - Olivier : creation
*/

public class DefaultInsertQuery implements InsertQuery
{
    /** The Object that contains data*/
    DbObjectMapping _object;

    public DefaultInsertQuery(DbObjectMapping _object)
    {
        this._object = _object;
    }

    public DbObjectMapping getObject() {
        return _object;
    }
}
