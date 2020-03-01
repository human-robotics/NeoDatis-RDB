package org.neodatis.tools.app.database.generation.clazz;

import org.neodatis.tools.app.model.Concept;

public interface CodeGenerationListener {
    public void start();
	public void newTable(String schema, String tableName, Concept concept);
    public void end();
}
