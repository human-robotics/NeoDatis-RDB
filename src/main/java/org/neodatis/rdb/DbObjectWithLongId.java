package org.neodatis.rdb;

public abstract class DbObjectWithLongId implements DbObjectMapping {

	protected Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void save() throws Exception {
		RDB rdb = null;

		try {
			rdb = RDBFactory.open();
			if (this.getId() != null) {
				rdb.update(this);
			} else {
				rdb.insert(this);
			}

		} catch (Exception e) {
			if (rdb != null) {
				rdb.rollback();
			}
			throw e;
		} finally {
			if (rdb != null) {
				rdb.commit();
			}
		}

	}

	public void insert() throws Exception {
		RDB rdb = null;

		try {
			rdb = RDBFactory.open();
			rdb.insert(this);

		} catch (Exception e) {
			if (rdb != null) {
				rdb.rollback();
			}
			throw e;
		} finally {
			if (rdb != null) {
				rdb.commit();
			}
		}

	}

	public void delete() throws Exception {
		RDB rdb = null;

		try {
			rdb = RDBFactory.open();
			rdb.delete(this);
		} catch (Exception e) {
			if (rdb != null) {
				rdb.rollback();
			}
			throw e;
		} finally {
			if (rdb != null) {
				rdb.commit();
			}
		}

	}

}
