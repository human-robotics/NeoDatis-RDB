package org.neodatis.rdb.mentor.model.callbacks;

import org.neodatis.rdb.ObjectReadyCallback;
import org.neodatis.rdb.mentor.model.PnData;

public class BasicCallback implements ObjectReadyCallback<PnData> {
	int counter;

	public void object(PnData object) {
		System.out.println(object.getName());
		counter++;
	}
	
	public int getCounter() {
		return counter;
	}

	public void setColumnNames(String[] columnNames) {
		// TODO Auto-generated method stub
		
	}

}
