/*
@version 10/09/2002 - Creation
@version 16/07/2002 - Adds the getSql method

*/
package org.neodatis.rdb;

public interface PrimaryKey {


    /**
     *
     * Retuns the name of the primary key
     *
     */
    public String getName();

    /**
     *
     * Return the SQL of the primary
     * @param The object
     * Example "CLIENT_ID = 10"
     *
     *
     */
    public String getSql(DbObjectMapping in_object) throws Exception;

    /**
     *
     * Returns the value of the primary key
     *
     * @param The object
     * @return The primary key value
     *
     */
    public Object getValue(DbObjectMapping in_object) throws Exception;

    /**
     *
     * Sets the value of the primary key
     *
     * @param The primary key value - for instance , it only supports Long values
     *
     */
    public void setValue(DbObjectMapping in_object , Long in_value) throws Exception;

}
