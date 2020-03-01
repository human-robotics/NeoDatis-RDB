package org.neodatis.rdb;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Category;

/**
 * a class able to execute ddl.
 * 
 * ddl must be in the following form : alter table resolution add status int
 * default 1
 * 
 * @author olivier
 *
 */
public class DDLExecuter {
	static Category logger = Category.getInstance(DDLExecuter.class.getName());

	List<String> sqls;
	boolean debug = true;
	int nbExecuted;
	int nbAlreadyExist;
	int nbErrors;
	List<String> errors;

	public DDLExecuter() {
		sqls = new ArrayList<String>();
		errors = new ArrayList<String>();
	}

	public DDLExecuter addDDL(String ddl) {
		sqls.add(ddl);
		return this;
	}

	public void ddl(String sql) throws Exception {
		RDB rdb = RDBFactory.open();

		rdb.ddl(sql);
		System.out.println("OK");
	}

	public void execute() throws Exception {
		RDB rdb = null;

		try {
			rdb = RDBFactory.open();
			
			nbExecuted = 0;
			nbAlreadyExist = 0;
			nbErrors = 0;
			

			for (String ddl : sqls) {
				boolean wasExecuted = false;
				boolean alreadyExist = false;
				String error= "no error";
				try{
					rdb.ddl(ddl);
					wasExecuted = true;
					nbExecuted++;
					errors.add("no error");
				}catch(Throwable e) {
					error = e.getMessage();
					errors.add(error);	
					nbErrors++;
					alreadyExist = e.getMessage().toLowerCase().indexOf("exist") != -1;
					if(alreadyExist) {
						nbAlreadyExist++;
					}
				}
				
				if(debug) {
					logger.info("Executing ddl "+ ddl+ " : executed:"+wasExecuted+" | already Exist:"+ alreadyExist + " > "+ error);
					 
				}
			}
		} finally {
			if (rdb != null) {
				rdb.commit();
				rdb.close();
			}
		}
	}
	
	public int getNbAlreadyExist() {
		return nbAlreadyExist;
	}
	public int getNbErrors() {
		return nbErrors;
	}
	
	public int getNbExecuted() {
		return nbExecuted;
	}
	public List<String> getErrors() {
		return errors;
	}

	public static void main(String[] args) throws Exception {
		DDLExecuter ddlExecuter = new DDLExecuter();
		ddlExecuter.addDDL("alter table sensor_sat add 	  valor_classe4 int");
		//ddlExecuter.addDDL("alter table resolucao add status int default 1");
		//ddlExecuter.addDDL("select * from usuario");
		ddlExecuter.execute();
		System.out.println(ddlExecuter.getErrors());
	
	}

}
