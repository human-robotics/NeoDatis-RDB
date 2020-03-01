package org.neodatis.rdb.query;

import java.util.List;

import org.neodatis.rdb.DBColumn;
import org.neodatis.rdb.Where;

public class W {
	public static Where empty(){
		return new WhereImpl();
	}
	public static Where equal(DBColumn column, Object value){
		return new WhereImpl(column, WhereOperator.EQUAL, value);
	}
	public static Where like(DBColumn column, String value){
		return new WhereImpl(column, WhereOperator.LIKE, value);
	}
	public static Where ilike(DBColumn column, String value){
		return new WhereImpl(column.toLower(), WhereOperator.LIKE, value.toLowerCase());
	}
	public static Where gt(DBColumn column, Object value){
		return new WhereImpl(column, WhereOperator.STRICTLY_GREATHER_THAN, value);
	}
	public static Where ge(DBColumn column, Object value){
		return new WhereImpl(column, WhereOperator.GREATER_THAN, value);
	}
	public static Where lt(DBColumn column, Object value){
		return new WhereImpl(column, WhereOperator.STRICTLY_SMALLER_THAN, value);
	}
	public static Where le(DBColumn column, Object value){
		return new WhereImpl(column, WhereOperator.SMALLER_THAN, value);
	}
	public static Where isNull(DBColumn column){
		return new WhereImpl(column, WhereOperator.IS_NULL, null);
	}
	public static Where isNotNull(DBColumn column){
		return new WhereImpl(column, WhereOperator.IS_NOT_NULL, null);
	}
	public static Where in(DBColumn column, List l){
		return new WhereImpl(column, WhereOperator.IN, l);
	}
	public static Where notEqual(DBColumn column, Object value){
		return new WhereImpl(column, WhereOperator.NOT_EQUAL, value);
	}


}
