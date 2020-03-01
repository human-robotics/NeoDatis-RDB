/*

 *@version 24/07/2002 - Olivier : creation
 */
package org.neodatis.rdb.query;

import org.neodatis.rdb.ObjectReadyCallback;
import org.neodatis.rdb.SelectQuery;

public class CustomSelectQuery extends DefaultCustomQuery {
	
	ObjectReadyCallback objectReadyCallback;

	/** Used to put data in a specific class
	 * 
	 */
	Class objectClass;
	
	public CustomSelectQuery(String in_sSql) {
		super(in_sSql);
	}

	public Class getObjectClass() {
		return objectClass;
	}

	public CustomSelectQuery setObjectClass(Class objectClass) {
		this.objectClass = objectClass;
		return this;
	}
	
	public ObjectReadyCallback objectCallback() {
		return objectReadyCallback;
	}

	public CustomSelectQuery setObjectReadyCallback(ObjectReadyCallback callback) {
		this.objectReadyCallback = callback;
		return this;
	}
}
