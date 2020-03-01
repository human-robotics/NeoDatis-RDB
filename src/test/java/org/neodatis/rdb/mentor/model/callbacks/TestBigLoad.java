package org.neodatis.rdb.mentor.model.callbacks;

import org.neodatis.rdb.RDB;
import org.neodatis.rdb.RDBFactory;
import org.neodatis.rdb.layout.LayoutInfo;
import org.neodatis.rdb.mentor.model.PnData;
import org.neodatis.rdb.mentor.model.meta.PnDataDBHelper;
import org.neodatis.rdb.query.W;
import org.neodatis.rdb.util.MemoryMonitor;

public class TestBigLoad {
	public static void main(String[] args) throws Exception {
		System.out.println(MemoryMonitor.get("before"));
		BasicCallback callback = new BasicCallback();
		RDB rdb = RDBFactory.open();
		rdb.close();
		//
//		List<PnData> all = PnData.DB.setObjectReadyCallback(callback).findAll();
//		System.out.println("Result="+callback.getCounter());
//		
//		System.out.println(all.size());
//		System.out.println(MemoryMonitor.get("after"));
		
		
		PnData.DB.toExcel(W.empty(), PnDataDBHelper.ID.getName()+" ASC", new LayoutInfo("PnData", "PnData.xlsx"));
	}
}
