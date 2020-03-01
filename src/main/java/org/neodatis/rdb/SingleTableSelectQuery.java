package org.neodatis.rdb;

/* The Base interface for All Select Query
@version 11/07/2002 - Olivier : Creation
*/


public interface SingleTableSelectQuery extends Query
{
    /** The Type of the object that the select must return
    <pre>Example " Event.class "</pre>
    @return The Object type*/
    public Class getObjectType();

    /** The String describing the SQL ALIAS
    <pre>Example " event "</pre>
    @return The TABLE alias - Can be null
    */
    public String getAlias() throws Exception;


}
