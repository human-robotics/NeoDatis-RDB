package org.neodatis.rdb.views;

import org.neodatis.tools.app.database.generation.xml.Filter;





public class DbFilter implements Filter{

	public boolean match(String f) {
		return f.indexOf("user_profile_function")!=-1 ||
				f.indexOf("api_key")!=-1 || f.indexOf("robot_user")!=-1;
	}

}
