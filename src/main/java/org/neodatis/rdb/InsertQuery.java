package org.neodatis.rdb;

/* The Base interface for All Insert Query
@version 11/07/2002 - Olivier : Creation
*/

public interface InsertQuery extends Query
{
    /** The object to be inserted
    @return The object to be inserted
    */
    public DbObjectMapping getObject();

}
