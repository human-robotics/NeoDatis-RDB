package org.neodatis.rdb;


/* The Base interface for All Select Query
@version 11/07/2002 - Olivier : Creation
*/


public interface SelectQuery extends Query
{
    /** The Type of the object that the select must return
     * <pre>Example " Event.class "</pre>
     * @param The index of the select
     * @return The Object type
    */
    public Class getObjectType(int in_sSelectIndex);

    /** The Types of all the objects that the select must return
     * <pre>Example : [Event.class,EventType.class,State.class] </pre>
     * @return The Object types
    */
    public Class [] getObjectTypes();

    /** The String describing the SQL WHERE
    <pre>Example " EVENT_ID = 15 "</pre>
    @return The SQL WHERE - Can be null
    */
    public Where getWhere();
    
    /**
     * Adds the where is the where with and clause. If 'current where is null' , w is assumed 
     * @param w
     * @return
     */
    public SelectQuery and(Where w);
    /**
     * Adds the where is the where with and clause. If 'current where is null' , w is assumed 
     * @param w
     * @return
     */
    public SelectQuery or(Where w) ;

    /** The String describing the SQL GROUP BY
    <pre>Example " EVENT_ID "</pre>
    @return The SQL GROUP BY - Can be null
    */
    public String getGroupBy();

    /** The String describing the SQL ORDER BY
    <pre>Example " EVENT_ID "</pre>
    @return The SQL ORDER BY - Can be null
    */
    public String getOrderBy();

    /** Sets The String describing the SQL WHERE
    <pre>Example " EVENT_ID = 15 "</pre>
    @param  The SQL WHERE - Can be null
    */
    public SelectQuery setWhere(Where in_where);

    /** Sets The String describing the SQL GROUP BY
    <pre>Example " EVENT_ID "</pre>
    @param The SQL GROUP BY - Can be null
    */
    public SelectQuery setGroupBy(String in_sGroupBy);

    /** Sets The String describing the SQL ORDER BY
    <pre>Example " EVENT_ID "</pre>
    @param The SQL ORDER BY - Can be null
    */
    public SelectQuery setOrderByAsc(DBColumn orderBy);
    public SelectQuery setOrderByDesc(DBColumn orderBy);
    public SelectQuery setOrderBy(String orderBy);

	/** Adds a singleTableSelect (select ... from table)
	 * Can be used to create join select
	 */
	public SelectQuery addSingleTableSelect(Class clazz);

	public String[] getAliases() throws Exception;
	
	/** To specify a callback to be called when an object is ready */
	public ObjectReadyCallback objectCallback();
	
	public SelectQuery setObjectReadyCallback(ObjectReadyCallback callback);


}
