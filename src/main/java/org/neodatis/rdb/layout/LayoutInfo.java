package org.neodatis.rdb.layout;

import java.util.List;

import org.neodatis.rdb.DBColumn;

public class LayoutInfo {
	String title;
	String fileName;
	List<DBColumn> columns;
	ILabel label;
	
	
	
	
	
	public LayoutInfo(String title, String fileName, List<DBColumn> columns,
			ILabel label) {
		super();
		this.title = title;
		this.fileName = fileName;
		this.columns = columns;
		this.label = label;
	}
	public LayoutInfo(String title, String fileName, List<DBColumn> columns) {
		super();
		this.title = title;
		this.fileName = fileName;
		this.columns = columns;
	}
	public LayoutInfo(String title, String fileName) {
		super();
		this.title = title;
		this.fileName = fileName;
	}
	public String getTitle() {
		return title;
	}
	public String getFileName() {
		return fileName;
	}
	public List<DBColumn> getColumns() {
		return columns;
	}
	public ILabel getLabel() {
		return label;
	}
	
	
	
}
