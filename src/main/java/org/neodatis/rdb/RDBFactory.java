/**
 * 
 */
package org.neodatis.rdb;

import org.apache.log4j.Category;
import org.neodatis.ConnectionPoolInfo;
import org.neodatis.rdb.implementation.DefaultRDB;

/**
 * @author olivier
 *
 */
public class RDBFactory {
	static Category logger = Category.getInstance(RDBFactory.class.getName());
	
	/**
	 * Open NeoDatis using Connection Pool information from default ConnectionPool.properties file found in the classpath
	 * @return
	 * @throws Exception
	 */
	public static RDB open() throws Exception{
		return new DefaultRDB();
	}
	
	
	/**
	 * Open NeoDatis using Connection Pool information from the propertyFile passed
	 * @param propertyFile The name of the file to retrieve Connection Pool information
	 * @return
	 * @throws Exception
	 */
	public static RDB open(String propertyFile) throws Exception{
		return new DefaultRDB(propertyFile);
	}
	
	
	/**
	 * Allows to define programmatically the default connection pool information
	 * @param cpi
	 * @throws Exception
	 */
	public static void useAsDefault(ConnectionPoolInfo cpi) throws Exception{
		logger.info("Testing default connection info...");
		new DefaultRDB(cpi).close();
	}

}
