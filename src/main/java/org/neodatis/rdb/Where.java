package org.neodatis.rdb;


/*
@version 18/07/2002 - Olivier : Creation
*/

public interface Where extends Sqlable {
    /** Get the sql representation of the where
     * @return The sql representation
     * @param To tell if the wher must contain aliases or not
     */
    public String getSql(boolean in_bWithAlias);

    public Where or(Where in_where);
    public Where and(Where in_where);

    /**
     *
     * Checks if where has objects
     * @return true if Where is empty
     *
     */
     public boolean isEmpty();

}
