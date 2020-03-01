package org.neodatis.rdb.query;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Stack;

import org.apache.log4j.Category;
import org.neodatis.rdb.DBColumn;
import org.neodatis.rdb.DBTable;
import org.neodatis.rdb.Sqlable;
import org.neodatis.rdb.Where;
import org.neodatis.rdb.implementation.DbSpecific;
import org.neodatis.tools.StringUtils;

/* A simple where clause
 @version 19/07/2002 - Olivier : Creation
 */

public class WhereImpl implements Where {

	/** Creates the root */
	static Category _log = Category.getInstance(WhereImpl.class.getName());

	Stack _stack;

	/**
	 * 
	 * Creates an empty where.
	 * 
	 */
	public WhereImpl() {
		init();
	}

	/**
	 * 
	 * Creates a Where instance
	 * 
	 * <pre>
	 * new DefaultWhere(mydbColumn, WhereOperator.EQUAL, &quot;olivier&quot;);
	 * new DefaultWhere(mydbColumn, WhereOperator.EQUAL, new Long(1));
	 * new DefaultWhere(mydbColumn, WhereOperator.EQUAL, otherDbColumn);
	 * </pre>
	 * 
	 * @param in_column
	 *            The left column
	 * @param The
	 *            where operator (= , < , ...)
	 * @param in_value
	 *            The right part - can be a String , a Long, a Double, a
	 *            DBColumn,....
	 * 
	 */
	public WhereImpl(DBColumn in_column, WhereOperator in_operator, Object in_value) {
		init();
		SimpleWhere sw = new SimpleWhere(in_column, in_operator, in_value);
		_stack.add(sw);
	}

	/**
	 * 
	 * Creates a Where instance.
	 * 
	 * <pre>
	 * 
	 * Example :
	 * new DefaultWhere( mydbColumn , WhereOperator.EQUAL , null , true );
	 * 
	 * =&gt; will not affect the object. it Checks that right part is null and returns!
	 * </pre>
	 * 
	 * @param in_column
	 *            The left column
	 * @param The
	 *            where operator (= , < , ...)
	 * @param in_value
	 *            The right part - can be a String , a Long, a Double, a
	 *            DBColumn,....
	 * @param in_bIgnoreIfValueIsNull
	 *            If true and right part is null, then do nothing.
	 * 
	 */
	public WhereImpl(DBColumn in_column, WhereOperator in_operator, Object in_value, boolean in_bIgnoreIfValueIsNull) {
		init();

		if (in_bIgnoreIfValueIsNull && in_value == null) {
			_log.warn("Right part is null - do not do anything!");
			return;
		}
		SimpleWhere sw = new SimpleWhere(in_column, in_operator, in_value);
		_stack.add(sw);
	}

	/**
	 * 
	 * Constructor
	 * 
	 * @param in_where
	 *            A where object
	 * 
	 */
	public WhereImpl(Where in_where) {
		init();

		_stack.add(in_where);
	}

	/**
	 * 
	 * Inits the stack object.
	 * 
	 */
	protected void init() {
		_stack = new Stack();
	}

	/**
	 * Get the sql representation of the where
	 * 
	 * @return The sql representation
	 * @param To
	 *            tell if the wher must contain aliases or not
	 */
	public String getSql(boolean in_bWithAlias) {
		StringBuffer sResult = new StringBuffer();
		String sSql = null;
		SimpleWhere sw = null;
		Object object = null;
		Object rightOperand = null;
		Object leftOperand = null;
		String sLeftOperand = null;
		String sRightOperand = null;

		int i = 1;
		Stack tempStack = (Stack) _stack.clone();

		while (tempStack.size() > 1) {
			System.out.println(i++ + " - Main Stack is " + tempStack.toString());

			object = tempStack.pop();
			System.out.println("object = " + object.toString());

			if (object.getClass() == WhereConnector.class) {
				rightOperand = tempStack.pop();
				leftOperand = tempStack.pop();

				sLeftOperand = getSql(leftOperand, in_bWithAlias);
				sRightOperand = getSql(rightOperand, in_bWithAlias);

				System.out.println("Left operand is : " + sLeftOperand);
				System.out.println("Connector is : " + object.toString());
				System.out.println("Right operand is : " + sRightOperand);

				sSql = new StringBuffer(sLeftOperand).append(object.toString()).append(sRightOperand).toString();
				tempStack.push(sSql);
			}
		}

		if (!tempStack.isEmpty()) {
			Object o = tempStack.pop();
			Sqlable sqlable = (Sqlable) o;
			sResult.append(" ( ").append(sqlable.getSql(in_bWithAlias)).append(" ) ");
		}

		return sResult.toString();
	}

	/**
	 * 
	 * Return the sql represnetaion of the where.
	 * 
	 * @param in_object
	 *            The java object to bind column values
	 * @param in_bWithAlias
	 *            true if the sql representation must be generated with table
	 *            alias
	 * @return The sql representaion.
	 * 
	 */
	protected String getSql(Object in_object, boolean in_bWithAlias) {
		if (in_object.getClass() == DefaultDBColumn.class) {
			return ((DBColumn) in_object).getSql(in_bWithAlias);
		}
		return in_object.toString();
	}

	/**
	 * 
	 * Connects a new where this where using OR operator. If where is empty just
	 * put the where on the stack
	 * 
	 * <pre>
	 *  'current where'  OR 'new where'
	 * </pre>
	 * 
	 * @param in_where
	 * 
	 * 
	 */
	public Where or(Where in_where) {

		if (in_where.isEmpty()) {
			_log.warn("Where is empty");
			return this;
		}

		if (_stack.empty()) {
			_stack.add(in_where);
		} else {
			Object object = _stack.pop();
			SimpleWhere sw = new SimpleWhere(object, WhereConnector.OR, in_where);
			_stack.push(sw);
		}

		return this;
	}

	/**
	 * 
	 * Connects a new where this where using AND operator. If where is empty
	 * just put the where on the stack
	 * 
	 * <pre>
	 *  'current where'  AND 'new where'
	 * </pre>
	 * 
	 * @param in_where
	 * 
	 * 
	 */
	public Where and(Where in_where) {
		if (in_where.isEmpty()) {
			_log.warn("Where is empty");
			return this;
		}

		if (_stack.empty()) {
			_stack.add(in_where);
		} else {
			Object object = _stack.pop();
			SimpleWhere sw = new SimpleWhere(object, WhereConnector.AND, in_where);
			_stack.push(sw);
		}

		return this;
	}

	/**
	 * 
	 * Returns the stack
	 * 
	 */
	protected Stack getStack() {
		return _stack;
	}

	/**
	 * 
	 * Checks if where has objects
	 * 
	 * @return true if Where is empty
	 * 
	 */
	public boolean isEmpty() {
		return _stack.empty();
	}

	/**
	 * 
	 * Return a string representation of the object
	 * 
	 * @return The string representation
	 * 
	 */
	public String toString() {
		return _stack.toString();
	}

	public static void main(String[] args) {

		DBTable table = new DefaultDBTable("CLIENT");
		DBColumn columnName = new DefaultDBColumn(table, "NAME", String.class);
		DBColumn columnStreet = new DefaultDBColumn(table, "STREET", String.class);
		DBColumn columnStreetNumber = new DefaultDBColumn(table, "STREET_NUMBER", Long.class);
		DBColumn columnBirthDate = new DefaultDBColumn(table, "BIRTH_DATE", Date.class);

		Where whereDate = new WhereImpl(columnBirthDate, WhereOperator.GREATER_THAN, new Date());
		System.out.println("Where date = " + whereDate.getSql(true));

		Where where1 = new WhereImpl();
		Where where2 = new WhereImpl(columnStreet, WhereOperator.EQUAL, "1");
		Where where3 = new WhereImpl(columnName, WhereOperator.EQUAL, null, true);

		where1.and(where2).and(where3);

		System.out.println("Where " + where1.getSql(true));

		// DefaultWhere dw = new DefaultWhere( columnName , WhereOperator.EQUAL
		// , "Olivier");
		WhereImpl dw = new WhereImpl(columnName, WhereOperator.EQUAL, columnName);
		WhereImpl dw2 = new WhereImpl(columnStreet, WhereOperator.EQUAL, "pinheiro");
		WhereImpl dw3 = new WhereImpl(columnStreetNumber, WhereOperator.EQUAL, "115");
		WhereImpl dw4 = new WhereImpl(columnBirthDate, WhereOperator.EQUAL, "03/09/1971");

		System.out.println("Where Sql with alias: " + dw3.toString());

		dw3.or(dw.and(dw2)).and(dw4);
		// DefaultWhere dw5 = new DefaultWhere( dw3.or( dw.and( dw2 ) ));
		// dw5.and( dw4 );

		// System.out.println("Where List : " + dw.myToString() );
		System.out.println("----------------------------");
		System.out.println("size  = " + dw3.getStack().size());
		System.out.println("Where Sql with alias: " + dw3.getSql(false));
		// System.out.println("Where Sql with alias: " + dw2.getSql(true) );
		// System.out.println("Where Sql without alias: " + dw3.getSql(false) );

		/*
		 * Stack stack = new Stack(); Stack s1 = new Stack(); Stack s2 = new
		 * Stack();
		 * 
		 * 
		 * s1.add( EQUAL ); s1.add( new Long(1) ); s1.add( new Long(2) );
		 * 
		 * 
		 * s2.add( GREATER_THAN ); s2.add( new Long(15) ); s2.add( new Long(25)
		 * );
		 * 
		 * 
		 * stack.add(AND); stack.add(s1); stack.add(s2);
		 * 
		 * 
		 * System.out.println("Tree = " + stack.toString() );
		 */
	}

	class SimpleWhere implements Sqlable {
		final SimpleDateFormat _dtFormat = new SimpleDateFormat(DbSpecific.get().getObjectDatePattern());

		Object leftOperand;
		Object rightOperand;
		Object connector;

		public SimpleWhere(Object _sLeftOperand, WhereOperator _operator, Object _sRightOperand) {
			this.leftOperand = _sLeftOperand;
			this.rightOperand = _sRightOperand;
			this.connector = _operator;
		}

		public SimpleWhere(Object leftOperand, WhereConnector connector, Object rightOperand) {
			this.leftOperand = leftOperand;
			this.rightOperand = rightOperand;
			this.connector = connector;
		}

		public Object getLeftOperand() {
			return leftOperand;
		}

		public Object getRightOperand() {
			return rightOperand;
		}

		public Object geConnector() {
			return connector;
		}

		public String getSql(boolean in_bWithAlias) {
			boolean bOk = false;
			StringBuffer sql = new StringBuffer();

			try {

				// If left operand is a column, and right operand is not
				if (leftOperand.getClass() == DefaultDBColumn.class && (rightOperand == null || rightOperand.getClass() != DefaultDBColumn.class)) {
					DBColumn column = (DBColumn) leftOperand;

					if (rightOperand == null) {
						if(connector.toString().equals(WhereOperator.IS_NOT_NULL.toString())) {
							sql.append(column.getSql(in_bWithAlias)).append(" is not null");
							bOk = true;
						} else {
							sql.append(column.getSql(in_bWithAlias)).append(" is null");
							bOk = true;
						}
					} else {

						if (connector.equals(WhereOperator.IN) || connector.equals(WhereOperator.NOT_IN)) {
							sql.append(column.getSql(in_bWithAlias));
							String s = null;
							if (rightOperand instanceof String) {
								s = connector.toString().replaceAll("%", rightOperand.toString());
							} else if (rightOperand instanceof Collection) {
								// This is a list, we need to build the 'in'
								// String
								Iterator iterator = ((Collection) rightOperand).iterator();
								StringBuffer buffer = new StringBuffer();
								int i = 0;
								while (iterator.hasNext()) {
									if (i != 0) {
										buffer.append(",");
									}
									Object o = iterator.next();
									if (o instanceof String) {
										buffer.append("'").append(o.toString()).append("'");
									} else {
										buffer.append(o.toString());
									}
									i++;
								}
								s = connector.toString().replaceAll("%", buffer.toString());
							}
							sql.append(s);
							bOk = true;
							return sql.toString();
						}

						if (column.getType() == String.class) {
							sql.append(column.getSql(in_bWithAlias)).append(connector.toString());

							if (rightOperand != null) {
								sql.append("'").append(rightOperand).append("'");
							} else {
								sql.append("null");
							}
							bOk = true;

						}

						if (column.getType() == Date.class) {
							sql.append(column.getSql(in_bWithAlias)).append(connector.toString());

							if (rightOperand != null) {
								if (rightOperand.getClass() == Date.class || rightOperand.getClass() == java.sql.Date.class) {
									sql.append(DbSpecific.get().convertDateToSqlString((Date) rightOperand));
								} else {
									sql.append("'").append(rightOperand).append("'");
								}
							} else {
								sql.append("null");
							}
							bOk = true;
						}

						if (!bOk) {
							if (rightOperand != null) {
								sql.append(column.getSql(in_bWithAlias)).append(connector.toString()).append(rightOperand);
							} else {
								sql.append(column.getSql(in_bWithAlias)).append(connector.toString()).append("null");
							}
						}
					}
				} else {
					Sqlable leftSqlable = (Sqlable) leftOperand;
					Sqlable rightSqlable = (Sqlable) rightOperand;
					sql.append(leftSqlable.getSql(in_bWithAlias)).append(connector.toString()).append(rightSqlable.getSql(in_bWithAlias));
				}
			} catch (NullPointerException e) {

				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				_log.error(sw.toString());

				_log.error("Error while getting sql of SimpleWhere : " + this.myToString());
				throw e;
			}

			return sql.toString();
		}

		public String toString() {
			boolean bOk = false;
			StringBuffer sResult = new StringBuffer();

			// If left operand is a column, and right operand is not
			if (leftOperand.getClass() == DefaultDBColumn.class && rightOperand.getClass() != DefaultDBColumn.class) {
				DBColumn column = (DBColumn) leftOperand;

				if (column.getType() == String.class) {
					sResult.append(leftOperand).append(connector.toString()).append("'").append(rightOperand).append("'");
					bOk = true;
				}

				if (column.getType() == Date.class) {
					String sConverter = DbSpecific.get().getDateToStringConverter();
					sConverter = StringUtils.replaceToken(sConverter, "@", leftOperand.toString(), 1);
					sResult.append(sConverter).append(connector.toString());
					sResult.append("'").append(rightOperand).append("'");
					bOk = true;
				}

				if (!bOk) {
					sResult.append(leftOperand).append(connector.toString()).append(rightOperand);
				}

			} else {
				sResult.append(leftOperand).append(connector.toString()).append(rightOperand);
			}

			return sResult.toString();
		}

		public String myToString() {
			StringBuffer sResult = new StringBuffer();

			sResult.append("Left Operand = ").append(leftOperand).append(" - ");
			sResult.append("Connector = ").append(connector).append(" - ");
			sResult.append("Right Operand = ").append(rightOperand).append(" \n");
			return sResult.toString();
		}

	}

}
