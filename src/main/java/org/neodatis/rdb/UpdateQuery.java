package org.neodatis.rdb;

/* The Base interface for All Update Query
@version 11/07/2002 - Olivier : Creation
*/

public interface UpdateQuery extends Query
{
    /** The object to be inserted
    @return The object to be inserted
    */
    public DbObjectMapping getObject();

   /** The String describing the SQL WHERE
    <pre>Example " EVENT_ID = 15 "</pre>
    @returnThe SQL WHERE - Can be null
    */
    public Where getWhere();
}
