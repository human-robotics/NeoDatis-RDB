package org.neodatis.rdb.mentor.model.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AlarmType {
	
	public static List<Integer> allIds;
	public static List<String> allLabels;
	public static Map<Integer,String> labelsById;
	
	public static final Integer CONNECTION_ERROR = new Integer(1);
	public static final Integer CONNECTION_OK = new Integer(2);
	public static final Integer WEB_SERVICE_CALL_ERROR = new Integer(3);
	public static final Integer WEB_SERVICE_CALL_OK = new Integer(4);
	public static final Integer PN_WITH_EXTERNAL_NAME_DOES_NOT_EXIST = new Integer(5);
	public static final Integer PN_WITH_NO_ACTIVATTION_DATA_IN_PERIOD = new Integer(6);
	
	static {
		init();
	}
	
	public static void init(){
		if(allIds==null) {
			allIds = new ArrayList<Integer>();
			allIds.add(CONNECTION_ERROR);
			allIds.add(CONNECTION_OK);
			allIds.add(WEB_SERVICE_CALL_ERROR);
			allIds.add(WEB_SERVICE_CALL_OK);
			allIds.add(PN_WITH_EXTERNAL_NAME_DOES_NOT_EXIST);
			allIds.add(PN_WITH_NO_ACTIVATTION_DATA_IN_PERIOD);
		}
		if(allLabels==null) {
			allLabels = new ArrayList<String>();
			allLabels.add("connection_error");
			allLabels.add("connection_ok");
			allLabels.add("web_service_call_error");
			allLabels.add("web_service_call_ok");
			allLabels.add("pn_with_external_name_does_not_exist");
			allLabels.add("pn_with_no_activation_data_in_perdio");
			
		}
		labelsById = new HashMap<Integer, String>();
		for(int i=0;i<allIds.size();i++) {
			labelsById.put(allIds.get(i), allLabels.get(i));
		}
	}
	public static List<Integer> allIds(){
		return allIds;
	}
	public static List<String> allLabels(){
		return allLabels; 
	}
	
	public static String getLabel(Integer alarmTypeId) {
		return labelsById.get(alarmTypeId);
	}
	
	public static void main(String[] args) {
		System.out.println(AlarmType.getLabel(CONNECTION_ERROR));
	}
	
}
