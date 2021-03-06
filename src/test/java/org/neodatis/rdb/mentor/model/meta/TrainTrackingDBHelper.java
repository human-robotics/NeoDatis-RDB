// class generated by NeoDatis Database ClassBuilder 2.0
package org.neodatis.rdb.mentor.model.meta;

// import NeoDatis Database library
import java.math.BigDecimal;
import java.util.Date;

import org.neodatis.rdb.DBColumn;
import org.neodatis.rdb.DBTable;
import org.neodatis.rdb.query.DefaultDBColumn;
import org.neodatis.rdb.query.DefaultDBTable;

public class TrainTrackingDBHelper{
	public static final DBTable table = new DefaultDBTable("train_tracking");

	public static final DBColumn ID =  new DefaultDBColumn( table , "id" , Long.class );
	public static final DBColumn PN_NAME =  new DefaultDBColumn( table , "pn_name" , String.class );
	public static final DBColumn DATA_DATE =  new DefaultDBColumn( table , "data_date" , Date.class );
	public static final DBColumn ENTER_DATE =  new DefaultDBColumn( table , "enter_date" , Date.class );
	public static final DBColumn EXIT_DATE =  new DefaultDBColumn( table , "exit_date" , Date.class );
	public static final DBColumn TRAIN_ID =  new DefaultDBColumn( table , "train_id" , Long.class );
	public static final DBColumn TRAIN_PREFIX =  new DefaultDBColumn( table , "train_prefix" , String.class );
	public static final DBColumn KM =  new DefaultDBColumn( table , "km" , String.class );
	public static final DBColumn STARTKM =  new DefaultDBColumn( table , "startkm" , BigDecimal.class );
	public static final DBColumn ENDKM =  new DefaultDBColumn( table , "endkm" , BigDecimal.class );
	public static final DBColumn LOCAL =  new DefaultDBColumn( table , "local" , String.class );
	public static final DBColumn MANAGMENT_DATE =  new DefaultDBColumn( table , "managment_date" , Date.class );
	public static final DBColumn DATA_STATUS =  new DefaultDBColumn( table , "data_status" , Long.class );
	public static final DBColumn ACTIVATION_STATUS =  new DefaultDBColumn( table , "activation_status" , Long.class );
	public static final DBColumn PN_DATA_ID =  new DefaultDBColumn( table , "pn_data_id" , Long.class );
	public static final DBColumn WEB_SERVICE_CALL_ID =  new DefaultDBColumn( table , "web_service_call_id" , Long.class );
	public static final DBColumn DESCRIPTION =  new DefaultDBColumn( table , "description" , String.class );

	//** To get the column list
	static public DBColumn [] getColumns()
	{
		DBColumn [] array = new DefaultDBColumn[17];
		array[0] = ID;
		array[1] = PN_NAME;
		array[2] = DATA_DATE;
		array[3] = ENTER_DATE;
		array[4] = EXIT_DATE;
		array[5] = TRAIN_ID;
		array[6] = TRAIN_PREFIX;
		array[7] = KM;
		array[8] = STARTKM;
		array[9] = ENDKM;
		array[10] = LOCAL;
		array[11] = MANAGMENT_DATE;
		array[12] = DATA_STATUS;
		array[13] = ACTIVATION_STATUS;
		array[14] = PN_DATA_ID;
		array[15] = WEB_SERVICE_CALL_ID;
		array[16] = DESCRIPTION;
		return array;
	}
}
