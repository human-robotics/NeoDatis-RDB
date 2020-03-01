package org.neodatis.rdb;

import java.util.List;
import java.util.Map;

public interface ToX {
	public String toJSon();
	public Map<String, Object> toMap();
	public List<String> toList();
	public List<String> getAttributeNames();
}
