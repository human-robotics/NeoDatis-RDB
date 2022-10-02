package org.neodatis.tools.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.neodatis.tools.app.model.NeoDatisAppData;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;

public class NeoDatis {
	public static NeoDatisAppData loadApplicationData(String file) throws FileNotFoundException{
		XStream xstream = new XStream();
		xstream.addPermission(AnyTypePermission.ANY);
		xstream.alias("neodatis-app-data", NeoDatisAppData.class);
		xstream.autodetectAnnotations(true);
		NeoDatisAppData app = (NeoDatisAppData) xstream.fromXML(new FileInputStream(new File(file)));
		return app;
	}
	
	
	public static void main(String[] args) throws FileNotFoundException {
		NeoDatisAppData app = NeoDatis.loadApplicationData("neodatis-app.xml");
		System.out.println(app);
	}
}
