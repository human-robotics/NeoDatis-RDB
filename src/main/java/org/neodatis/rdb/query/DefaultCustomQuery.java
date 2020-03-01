package org.neodatis.rdb.query;


import java.util.List;
import java.util.ArrayList;

import org.neodatis.rdb.CustomQuery;

/** A class containg the sql string of the query

@author Olivier Smadja - osmadja@netcourrier.com
@version 24/07/2002 - Olivier : creation

<code>
Example : select * from emp
</code>


*/

public abstract class DefaultCustomQuery implements CustomQuery
{
    /** The sql representation of the query*/
    String _sSql;

    public DefaultCustomQuery(String in_sSql)
    {
        _sSql = in_sSql;
    }

    public String getSql()
    {
        return _sSql;
    }

    public String toString()
    {
        return getSql();
    }
}
