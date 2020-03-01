package org.neodatis.rdb;

/* The Base interface for All Delete Query
@version 11/07/2002 - Olivier : Creation
*/

public interface DeleteQuery extends Query
{
    /** The Type of the object that the select must return
     * <pre>Example " Event.class "</pre>
     * @return The Object type
    **/
    public Class getObjectType();

    /** The String describing the SQL WHERE
    * <pre>Example " EVENT_ID = 15 "</pre>
    * @return The SQL WHERE - Can be null
    */
    public Where getWhere();

    /** The object to be deleted
    @return The object to be deleted
    */
    public DbObjectMapping getObject();
}
