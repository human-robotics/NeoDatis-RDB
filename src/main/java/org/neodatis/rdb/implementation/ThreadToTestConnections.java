package org.neodatis.rdb.implementation;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Category;
import org.neodatis.tools.StringUtils;

public class ThreadToTestConnections implements Runnable {

	static Category log = Category.getInstance(ThreadToTestConnections.class
			.getName());

	protected DefaultConnectionPool pool;
	protected long period;
	protected String testQuery;
	protected int nbTests;

	public ThreadToTestConnections(DefaultConnectionPool p, long period,
			String queryForTest) {
		this.pool = p;
		this.period = period;
		this.testQuery = queryForTest;

		log.info("Starting ConnectionChecker thread with query:" + testQuery
				+ " , executing every " + period);
	}

	public void run() {

		while (true) {
			try {
				Thread.sleep(period);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				testConnections();
			} catch (Throwable e) {
				log.error(e);
			}
		}
	}

	private void testConnections() throws Exception {
		nbTests++;
		int nbOk = 0;
		List<ConnectionInfo> l = pool.getConnections();
		log.debug("RDB:Testing " + l.size() + " connections of pool with query "
				+ testQuery +" , Test Nb "+ nbTests);
		for (int i = 0; i < l.size(); i++) {
			boolean connectionIsOk = false;
			Statement statement = null;
			Connection c = null;
			ConnectionInfo ci = null;
			boolean connectionHasBeenTested = false;
			try {
				log.info("Testing connection " + (i + 1));
				ci = l.get(i);
				if (true || ci.isAvailable) {
					connectionHasBeenTested = true;
					c = ci.getConnection();
					c.setAutoCommit(false);
					// test connection
					statement = c.createStatement();
					statement.execute(testQuery);
					connectionIsOk = true;

				} else {
					log.debug("Connection " + (i + 1)
							+ " is busy, can not use it to test :-(");
				}
			} catch (Exception e) {
				connectionIsOk = false;
				log.error(StringUtils.exceptionToString(e, true));
			} finally {
				if (statement != null) {
					statement.close();
					if (connectionHasBeenTested) {
						if (c != null) {
							c.commit();
						}
						pool.releaseConnection(c);
					}
				}

				if (connectionHasBeenTested) {
					if (!connectionIsOk) {
						log.error("Connection " + (i + 1)
								+ " is down, rebuilding");
						try {
							pool.rebuildConnection(ci, i);
							log.error("Connection " + (i + 1)
									+ " is down, rebuilding done!");
							nbOk++;
						} catch (Throwable e) {
							log.error("Error while rebuilding connection " + (i+1)
									+ ":"
									+ StringUtils.exceptionToString(e, true));
						}

					} else {
						log.debug("Connection " + (i + 1) + " is ok");
						nbOk++;
					}
				}
			}

		}
		log.debug("Testing " + l.size() + " connections :  "+nbOk + " are ok");
	}

}
