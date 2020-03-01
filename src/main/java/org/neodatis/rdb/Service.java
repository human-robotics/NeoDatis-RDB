package org.neodatis.rdb;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.neodatis.rdb.helper.ToExcelCallback;
import org.neodatis.rdb.implementation.RdbReflection;
import org.neodatis.rdb.layout.LayoutInfo;
import org.neodatis.rdb.query.DefaultDBColumn;
import org.neodatis.rdb.query.DefaultDBTable;
import org.neodatis.rdb.query.DefaultSelectQuery;
import org.neodatis.rdb.query.W;
import org.neodatis.tools.StringUtils;

public class Service<T extends DbObjectMapping> {
	
	protected static Logger logger = Logger.getLogger("NeoDatisRDBService");
	
	ObjectReadyCallback<T> objectReadyCallback;
	
	
	public static final DBColumn ID = new DefaultDBColumn(
			new DefaultDBTable(""), "ID", Long.class);

	protected Class<T> clazz;

	public Service(Class<T> c) {
		this.clazz = c;
	}

	public void save(DbObjectWithLongId object) throws Exception {
		RDB rdb = null;

		try {
			rdb = RDBFactory.open();
			if (object.getId() != null) {
				rdb.update(object);
			} else {
				rdb.insert(object);
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

	public void delete(DbObjectWithLongId object) throws Exception {
		RDB rdb = null;

		try {
			rdb = RDBFactory.open();
			rdb.delete(object);
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

	public void delete(DeleteQuery w) throws Exception {
		RDB rdb = null;

		try {
			rdb = RDBFactory.open();
			rdb.delete(w);
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

	public List<T> findAll() throws Exception {
		RDB rdb = null;
		try {
			rdb = RDBFactory.open();
			return rdb.select(new DefaultSelectQuery(clazz).setObjectReadyCallback(objectReadyCallback)).getData();
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

	/**
	 * 
	 * @param orderBy
	 *            just the expression , what goes after ORDER BY
	 * @return
	 * @throws Exception
	 */
	public List<T> findAll(String orderBy) throws Exception {
		RDB rdb = null;
		try {
			rdb = RDBFactory.open();
			return rdb
					.select(new DefaultSelectQuery(clazz).setObjectReadyCallback(objectReadyCallback).setOrderBy(orderBy))
					.getData();
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

	/**
	 * Return one object that matches teh where. If no match is found, then
	 * return null
	 * 
	 * @param where
	 * @return
	 * @throws Exception
	 */
	public T findOne(Where where) throws Exception {
		RDB rdb = null;
		try {
			rdb = RDBFactory.open();
			List<T> result = rdb.select(new DefaultSelectQuery(clazz, where).setObjectReadyCallback(objectReadyCallback))
					.getData();
			if (result.isEmpty()) {
				return null;
			}

			return (T) result.get(0);
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

	public List<T> find(Where where) throws Exception {
		RDB rdb = null;
		try {
			rdb = RDBFactory.open();
			return rdb.select(new DefaultSelectQuery(clazz, where).setObjectReadyCallback(objectReadyCallback)).getData();
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

	public List<T> find(Where where, String orderBy) throws Exception {
		RDB rdb = null;
		try {
			rdb = RDBFactory.open();
			return rdb.select(new DefaultSelectQuery(clazz, where, orderBy).setObjectReadyCallback(objectReadyCallback))
					.getData();
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

	public T findById(Long id) throws Exception {
		RDB rdb = null;
		try {
			rdb = RDBFactory.open();
			return (T) rdb.select(
					new DefaultSelectQuery(clazz, W.equal(ID, id)).setObjectReadyCallback(objectReadyCallback))
					.getObject(0);
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

	/**
	@Deprecated Use toExcel instead
	*/
	public int allToExcel(Where where, String orderBy, LayoutInfo lo)
			throws Exception {
		RDB rdb = null;
		try {
			rdb = RDBFactory.open();
			List<T> objects = rdb.select(
					new DefaultSelectQuery(clazz, where, orderBy)).getData();

			Workbook wb = new XSSFWorkbook();
			Sheet sheet = wb.createSheet("Principal");

			// set title
			sheet.createRow((short) 0).createCell(1).setCellValue(lo.getTitle());

			int rowIndex = 2;

			RdbReflection reflection = new RdbReflection();
			Field[] fields = reflection.getFieldsOf(clazz);
			// build a map for fields
			Map<String, Field> fieldsByName = new HashMap<String, Field>();
			for (Field f : fields) {
				fieldsByName.put(StringUtils.replaceToken(f.getName(), "db", ""), f);
			}
			
			if(lo.getColumns()!=null) {
				fields = new Field[lo.getColumns().size()];
				int index = 0;
				for (DBColumn c : lo.getColumns()) {
					fields[index++] = fieldsByName.get(c.getName());
				}
			}

			
			Row row = sheet.createRow((short) rowIndex++);
			int cellIndex = 1;
			
			
			// labels
			for (Field f: fields) {
				String label = f.getName();
				if(lo.getLabel()!=null ){
					label = lo.getLabel().getLabel(f.getName());					
				}
				// Create a cell and put a value in it.
				Cell cell = row.createCell(cellIndex++);
				cell.setCellValue(label);
			}
			
			for (T o : objects) {
				row = sheet.createRow((short) rowIndex++);

				cellIndex = 1;
				for (Field f: fields) {
					Object value = reflection.getFieldValue(f, o);

					// Create a cell and put a value in it.
					Cell cell = row.createCell(cellIndex++);
					cell.setCellValue(safe(value));
				}
			}

			FileOutputStream fileOut = new FileOutputStream(lo.getFileName());
			wb.write(fileOut);
			fileOut.close();
			wb.close();
			
			

			return objects.size();
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
	
	public int toExcel(Where where, String orderBy, LayoutInfo lo)
			throws Exception {
		RDB rdb = null;
		long t0 = System.currentTimeMillis();
		try {
			logger.info("New NeoDatisRDB Exporting : "+ lo.getFileName());
			SXSSFWorkbook wb = new SXSSFWorkbook(100);
			final Sheet sheet = wb.createSheet("Principal");

			// set title
			sheet.createRow((short) 0).createCell(1).setCellValue(lo.getTitle());

			int rowIndex = 2;

			RdbReflection reflection = new RdbReflection();
			Field[] fields = reflection.getFieldsOf(clazz);
			// build a map for fields
			Map<String, Field> fieldsByName = new HashMap<String, Field>();
			for (Field f : fields) {
				fieldsByName.put(StringUtils.replaceToken(f.getName(), "db", ""), f);
			}
			
			if(lo.getColumns()!=null) {
				fields = new Field[lo.getColumns().size()];
				int index = 0;
				for (DBColumn c : lo.getColumns()) {
					fields[index++] = fieldsByName.get(c.getName());
				}
			}

			
			Row row = sheet.createRow((short) rowIndex++);
			int cellIndex = 1;
			
			
			// labels
			for (Field f: fields) {
				String label = f.getName();
				if(lo.getLabel()!=null ){
					label = lo.getLabel().getLabel(f.getName());					
				}
				// Create a cell and put a value in it.
				Cell cell = row.createCell(cellIndex++);
				cell.setCellValue(label);
			}
			

			ToExcelCallback callback = new ToExcelCallback(sheet, fields, reflection);
			
			
			rdb = RDBFactory.open();
			// Using callbac to manage big result without out of memory
			rdb.select(
					new DefaultSelectQuery(clazz, where, orderBy).setObjectReadyCallback(callback)).getData();

			logger.info("Writing to file "+ lo.getFileName());
			long t1 = System.currentTimeMillis();
			
			FileOutputStream fileOut = new FileOutputStream(lo.getFileName());
			wb.write(fileOut);
			
			long t2 = System.currentTimeMillis();
			logger.info("Written to file "+ lo.getFileName() + " in "+ (t2-t1)+"ms");
			
			logger.info("Closing file ");
			fileOut.close();
			logger.info("Closing wb ");
			wb.close();
			logger.info("Disposing wb ");
			
			
			wb.dispose();
			return callback.getNbObjects();
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

	private String safe(Object value) {
		if(value==null){
			return "-";
		}
		return String.valueOf(value);
	}

	public Service<T> setObjectReadyCallback(ObjectReadyCallback<T> callback) {
		this.objectReadyCallback = callback;
		return this;
	}
	
	public String getVersion() {
		return Version.VERSION;

	}
}
