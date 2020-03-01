package org.neodatis.rdb;


/*
@version 22/07/2002 - Olivier : Creation
	*/

public interface DBColumn extends Sqlable {
    public String getName();
    public Class getType();
	public DBColumn toUpper();
	public DBColumn toLower();
	public DBColumn function(String functionName);
}
