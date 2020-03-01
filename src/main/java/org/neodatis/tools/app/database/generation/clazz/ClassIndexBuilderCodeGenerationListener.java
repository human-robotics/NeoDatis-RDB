package org.neodatis.tools.app.database.generation.clazz;

import java.util.ArrayList;
import java.util.List;

import org.neodatis.tools.app.model.Concept;

public class ClassIndexBuilderCodeGenerationListener implements CodeGenerationListener{

    List<String> classNames;
    public void start() {
        classNames = new ArrayList<String>();

    }

    public void newTable(String schema, String tableName, Concept concept) {
        classNames.add(concept.getFullClassName());
    }

    public void end() {

    }
}
