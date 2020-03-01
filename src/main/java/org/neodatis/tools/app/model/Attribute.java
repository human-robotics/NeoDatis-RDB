package org.neodatis.tools.app.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;


@XStreamAlias("attribute")
public class Attribute {
	//name="ID" type="Long" is-nullable="false" is-primary-key="true" foreign-key="none" comment=""
	@XStreamAsAttribute
	String name;
	@XStreamAsAttribute
	String type;
	
	@XStreamAlias("is-nullable")
	@XStreamAsAttribute
	boolean isNullable;
	
	@XStreamAlias("is-primary-key")
	@XStreamAsAttribute
	boolean isPrimaryKey;
	
	@XStreamAlias("foreign-key")
	@XStreamAsAttribute
	String foreignKeyInformation;
	
	
	public Attribute() {
	}


	public Attribute(String name, String type, boolean isNullable, boolean isPrimaryKey, String foreignKeyInformation) {
		super();
		this.name = name;
		this.type = type;
		this.isNullable = isNullable;
		this.isPrimaryKey = isPrimaryKey;
		this.foreignKeyInformation = foreignKeyInformation;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public boolean isNullable() {
		return isNullable;
	}


	public void setNullable(boolean isNullable) {
		this.isNullable = isNullable;
	}


	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}


	public void setPrimaryKey(boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}


	public String getForeignKeyInformation() {
		return foreignKeyInformation;
	}


	public void setForeignKeyInformation(String foreignKeyInformation) {
		this.foreignKeyInformation = foreignKeyInformation;
	}


	@Override
	public String toString() {
		return "Attribute [name=" + name + ", type=" + type + ", isNullable=" + isNullable + ", isPrimaryKey=" + isPrimaryKey + ", foreignKeyInformation="
				+ foreignKeyInformation + "]";
	}
	
	
}
