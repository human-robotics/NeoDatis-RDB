package org.neodatis.rdb;



/**
 * An interface used to receive an object as they become ready from queries
 * @author olivier
 *
 * @param <T>
 */
public interface ObjectReadyCallback<T> {
	public void object(T object) throws Exception;

	public void setColumnNames(String[] columnNames);
}
