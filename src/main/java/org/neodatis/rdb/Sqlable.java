package org.neodatis.rdb;

/*   Interface that all SQL - Able object must implement
@version 23/07/2002 - Olivier : Creation
	*/

public interface Sqlable {

    /**To get the sql represnetation of the object
     * @param To indicate if object must contain table alias
     *
     */
    public String getSql(boolean in_bWithAlias );
}
