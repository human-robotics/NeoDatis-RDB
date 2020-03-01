/* The interface that all "databaseable" objects
@version 10/09/2002 - Creation
*/
package org.neodatis.rdb;

import java.io.Serializable;

public interface DbObjectMapping extends Serializable {

    /** To get the primary key field name*/
	public PrimaryKey getPrimaryKey();

    /** To get The table */
	public DBTable getTable();
}
