package org.neodatis.tools.app.model;

import java.util.List;

import org.neodatis.tools.app.model.Concept;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("neodatis-app-data")
public class NeoDatisAppData {
	@XStreamAsAttribute
	String name;
	@XStreamImplicit
	List<Concept> concepts;
	public NeoDatisAppData() {
		super();
	}
	public NeoDatisAppData(String name, List<Concept> concepts) {
		super();
		this.name = name;
		this.concepts = concepts;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Concept> getConcepts() {
		return concepts;
	}
	public void setConcepts(List<Concept> concepts) {
		this.concepts = concepts;
	}
	@Override
	public String toString() {
		return "NeoDatisApp [name=" + name + ", concepts=" + concepts + "]";
	}
	
	
}
