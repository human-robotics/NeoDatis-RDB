package org.neodatis.rdb.implementation;


/**
 * Title: JConcept Description: All Jconcept libraries Copyright: Copyright (c)
 * 1999-2012 Company: JConcept
 * 
 * @author Olivier Smadja
 * @version 10/06/2001 creation
 */

public class DatabaseColumn {
	protected boolean isPrimaryKey;
	protected String tableName;
	protected String name;
	protected int dataType;
	protected String dataTypeAsString;
	protected int dataLength;
	protected int scale;
	protected String comment;
	protected boolean allowNull;
	/** foreign table.column , can be null*/
	protected String foreignKeyInformation;

	/**
	 * Constructor
	 **/
	public DatabaseColumn() {
		tableName = "";
		name = "";
		dataType = 0;
		dataLength = 0;
		comment = "";
	}

	public DatabaseColumn(boolean isPrimaryKey, String tableName, String name, int dataType, String dataTypeAsString, int dataLength,int scale, String comment, boolean allowNull, String foreignKeyInformation) {
		super();
		this.isPrimaryKey = isPrimaryKey;
		this.tableName = tableName;
		this.name = name;
		this.dataType = dataType;
		this.dataTypeAsString = dataTypeAsString;
		this.dataLength = dataLength;
		this.scale = scale;
		this.comment = comment;
		this.allowNull = allowNull;
		this.foreignKeyInformation = foreignKeyInformation;
	}

	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}

	public void setPrimaryKey(boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public int getDataLength() {
		return dataLength;
	}

	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isAllowNull() {
		return allowNull;
	}

	public void setAllowNull(boolean allowNull) {
		this.allowNull = allowNull;
	}

	public String getForeignKeyInformation() {
		return foreignKeyInformation;
	}

	public void setForeignKeyInformation(String foreignKeyInformation) {
		this.foreignKeyInformation = foreignKeyInformation;
	}

	public String getDataTypeAsString() {
		return dataTypeAsString;
	}

	public void setDataTypeAsString(String dataTypeAsString) {
		this.dataTypeAsString = dataTypeAsString;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	@Override
	public String toString() {
		return "DatabaseColumn [isPrimaryKey=" + isPrimaryKey + ", tableName=" + tableName + ", name=" + name + ", dataType=" + dataType
				+ ", dataTypeAsString=" + dataTypeAsString + ", dataLength=" + dataLength + ", scale=" + scale + ", comment=" + comment + ", allowNull="
				+ allowNull + ", foreignKeyInformation=" + foreignKeyInformation + "]";
	}

	
	
	
}