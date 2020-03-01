package org.neodatis.rdb.util.generation;

import java.util.List;

import org.neodatis.rdb.implementation.DatabaseColumn;

public interface CodeGenerationListener {
	public void newTable(String schema, String tableName, List<DatabaseColumn> fields);
}
