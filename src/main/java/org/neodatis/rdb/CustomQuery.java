/*  A simple interface for all custom queries
*@version 24/07/2002 - Olivier : creation
*/
package org.neodatis.rdb;

public interface CustomQuery extends Query
{
    /** Returns th sql of the query
     * @return The Sql query
     */
    public String getSql();
}
