package org.neodatis.rdb.util;

public class MemoryMonitor {
	public static String get(String label){
		StringBuffer buffer = new StringBuffer();
		buffer.append(label).append(":Free=").append(+Runtime.getRuntime().freeMemory()/1024).append("k / Total=").append(Runtime.getRuntime().totalMemory()/1024).append("k");
		return buffer.toString();
	}
}
