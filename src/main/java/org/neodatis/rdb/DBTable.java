package org.neodatis.rdb;

/*
@version 22/07/2002 - Olivier : Creation
*/

public interface DBTable {
    /** Return the alias of table, used for joins
     * @return The alias
     */
    public String getAlias();

    /** Returns The table name
     * @return the Table name
     */
    public String getName();
}
