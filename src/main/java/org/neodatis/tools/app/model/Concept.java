package org.neodatis.tools.app.model;

import java.util.List;

import org.neodatis.tools.StringUtils;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("concept")
public class Concept {
	@XStreamAsAttribute
	String name;
	
	@XStreamAsAttribute
	@XStreamAlias("implements")
	String conceptImplements;
	
	@XStreamAsAttribute
	@XStreamAlias("extends")
	String conceptExtends;

	@XStreamAsAttribute
	String packageName;

	@XStreamAsAttribute
	String helperPackageName;
	@XStreamImplicit
	List<Attribute> attributes;
	
	
	public Concept() {
		super();
	}


	public Concept(String name, List<Attribute> attributes) {
		super();
		this.name = name;
		this.attributes = attributes;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public List<Attribute> getAttributes() {
		return attributes;
	}


	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}


	@Override
	public String toString() {
		return "Concept [name=" + name + ", attributes=" + attributes + "]";
	}
	
	
	public Attribute getConceptPrimaryKey(){
		for(Attribute a: attributes){
			if(a.isPrimaryKey){
				return a;
			}
		}
		return null;
	}


	public String getClassName() {
		return StringUtils.capitalize(name, false);
	}


	


	public String getPackageName() {
		return packageName;
	}


	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}


	public String getHelperPackageName() {
		return helperPackageName;
	}


	public void setHelperPackageName(String helpPackageName) {
		this.helperPackageName = helpPackageName;
	}

    public String getFullClassName(){
        return packageName+"."+getClassName();
    }


	public String getConceptImplements() {
		return conceptImplements;
	}


	public void setConceptImplements(String conceptImplements) {
		this.conceptImplements = conceptImplements;
	}


	public String getConceptExtends() {
		return conceptExtends;
	}


	public void setConceptExtends(String conceptExtends) {
		this.conceptExtends = conceptExtends;
	}
	
	
	
}
