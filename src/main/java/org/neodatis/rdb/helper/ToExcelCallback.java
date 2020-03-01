package org.neodatis.rdb.helper;

import java.lang.reflect.Field;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.neodatis.rdb.DbObjectMapping;
import org.neodatis.rdb.ObjectReadyCallback;
import org.neodatis.rdb.implementation.RdbReflection;

public class ToExcelCallback implements ObjectReadyCallback<DbObjectMapping> {
	protected static Logger logger = Logger.getLogger("NeoDatisRDBServiceTOExcel");

	int nbObjects = 0;
	int localRowIndex = 3;
	
	Sheet sheet;
	Field[] fields;
	RdbReflection reflection;
	boolean dontWrite = System.getProperty("NEODATIS_EXCEL_DONT_WRITE")!=null;
	
	
	
	public ToExcelCallback(Sheet sheet, Field[] fields, RdbReflection reflection) {
		super();
		this.sheet = sheet;
		this.fields = fields;
		this.reflection = reflection;
	}
	public void object(DbObjectMapping object) throws Exception {
		if(nbObjects>1 && nbObjects % 1000==0) {
			logger.info("NeoDatisRdb.Service.ToExcelCallback - "+nbObjects+" objects");
		}
		nbObjects++;
		
		if(dontWrite){
			return;
		}
		Row row = sheet.createRow(localRowIndex++);

		int cellIndex = 1;
		for (Field f: fields) {
			DbObjectMapping o = object;
			Object value = reflection.getFieldValue(f, o);

			// Create a cell and put a value in it.
			Cell cell = row.createCell(cellIndex++);
			cell.setCellValue(safe(value));
		}
	}
	public int getNbObjects() {
		return nbObjects;
	}
	
	private String safe(Object value) {
		if(value==null){
			return "-";
		}
		return String.valueOf(value);
	}
	public void setColumnNames(String[] columnNames) {
		// TODO Auto-generated method stub
		
	}
}
