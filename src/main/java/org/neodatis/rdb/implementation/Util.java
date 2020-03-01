package org.neodatis.rdb.implementation;

import org.neodatis.rdb.Config;
import org.neodatis.rdb.DBColumn;
import org.neodatis.rdb.DbObjectMapping;

public class Util {
	public static String getTableName(DbObjectMapping o) {
		return getRigthCase(o.getTable().getName());
	}

	public static String getColumnName(String name) {
		return getRigthCase(name);
	}

	public static String getRigthCase(String s) {
		if (Config.forceTableNameUpperCase) {
			return s.toUpperCase();
		}
		if (Config.forceTableNameLowerCase) {
			return s.toLowerCase();
		}
		return s;
	}
}
