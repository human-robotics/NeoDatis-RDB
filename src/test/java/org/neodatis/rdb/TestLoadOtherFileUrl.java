package org.neodatis.rdb;

public class TestLoadOtherFileUrl {
	public static void main(String[] args) throws Exception {
		//System.setProperty("neodatis.rdb.url.property.file", "conf-local/ConnectionPool.properties");
		RDBFactory.open().close();
	}
}
