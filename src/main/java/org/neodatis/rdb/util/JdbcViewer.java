package org.neodatis.rdb.util;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.Vector;

import org.neodatis.rdb.implementation.DatabaseColumn;
import org.neodatis.rdb.implementation.DatabaseMetaInformation;
import org.neodatis.rdb.implementation.DefaultConnectionPool;
import org.neodatis.rdb.implementation.Sql;
import org.neodatis.tools.Keyboard;
import org.neodatis.tools.StringUtils;

/**
 * @author Olivier Smadja
 * @version 10/06/2001 - creation
 * @version 29/06/2001 - versao grafica
 */

public class JdbcViewer {
	static final String _RELEASE = " 0.1";

	String[] _asQuery = new String[30];
	int _nLineindex = 0;

	TextArea _taResult;
	TextArea _taQuery;
	TextArea _taHistory;
	TextField _tNumber;
	Button _btExecute;
	Choice _choice;
	DatabaseMetaInformation dmi;
	public JdbcViewer() {
		dmi = new DatabaseMetaInformation();
	}

	void menu() throws Exception {
		boolean bContinue = true;
		char cChoice = '0';

		while (bContinue) {
			System.out.println("\nJDBC Viewer " + _RELEASE);

			System.out.println("\n\n\t1) Display Catalog");
			System.out.println("\t2) Display Table information");
			System.out.println("\t3) Execute Sql");
			System.out.println("\n\t0) Quit");

			System.out.println("\n\n\n\t\tAction ?");

			cChoice = Keyboard.getChar();

			switch (cChoice) {
			case '1':
				displayTables();
				break;
			case '2':
				displayTableInfo();
				break;
			case '3':
				queryEntry();
				break;
			case '0':
				System.exit(0);
			}

		}

	}

	void displayTables() throws Exception {
		// Gets the schema
		String sSchema = Keyboard.getString("For schema ?");

		List<String> aTablesList = dmi.getTableNames(sSchema, null);

		System.out.println("Table number : " + aTablesList.size());

		for (int i = 0; i < aTablesList.size(); i++) {
			System.out.println("" + (i + 1) + ":" + aTablesList.get(i));
		}
	}

	void displayTableInfo() throws Exception {
		// Gets the schema
		String sSchema = Keyboard.getString("For schema ?");

		// Gets the table name
		String sTableName = Keyboard.getString("Which table ?");

		List<DatabaseColumn> columns = dmi.getTableColumnsGeneric(sSchema, sTableName);
		// Vector aColumnList = DatabaseMetaInformation.getTableColumns( "video"
		// , "SALDO" );

		System.out.println("Column number for table " + sSchema + "." + sTableName + " : " + columns.size());

		for (int i = 0; i < columns.size(); i++) {
			System.out.println("" + (i + 1) + ":" + columns.get(i).toString());
		}
	}

	void queryEntry() throws Exception {

		String sQuery = null;

		try {
			do {
				// Gets the query
				sQuery = Keyboard.getString("SQL>");

				if (sQuery.startsWith("!list")) {
					for (int i = 0; i < _asQuery.length; i++) {
						System.out.println("[" + i + "]  : " + _asQuery[i]);
					}
					break;
				} else {
					if (sQuery.startsWith("!")) {
						String sTemp = sQuery.substring(1);
						if (sTemp != null) {
							int nIndex = Integer.parseInt(sTemp);

							if (nIndex < _asQuery.length) {
								sQuery = _asQuery[nIndex];
							}
						} else {
							System.out.println("! needs a number");
						}
					}
				}
				try {
					if (sQuery.equalsIgnoreCase("exit")) {
						return;
					}
					System.out.println(executeSql(sQuery, 1000));
				} catch (Exception e) {
					System.out.println("SQL Error : " + e.getMessage());
				} finally {
				}
			} while (!sQuery.equalsIgnoreCase("exit"));
		} finally {
		}
	}

	String executeSql(String in_sQuery, int in_nNbLines) throws Exception {
		ResultSet rset = null;
		ResultSetMetaData metadata = null;
		int nColumns = 0;
		StringBuffer sResult = new StringBuffer();
		Sql sql = null;
		int nSize = 0;
		int nLine = 0;

		try {
			rset = null;
			metadata = null;
			nColumns = 0;

			sql = new Sql("default");

			try {
				rset = sql.select(in_sQuery);
				metadata = rset.getMetaData();
				nColumns = metadata.getColumnCount();
				// prints header
				for (int i = 0; i < nColumns; i++) {
					nSize = metadata.getPrecision(i + 1);
					sResult.append(StringUtils.fillEndWithChar(metadata.getColumnName(i + 1), ' ', nSize));
				}
				sResult.append("\n");

				while (rset.next()) {
					if (nLine < in_nNbLines) {
						nLine++;
						for (int i = 0; i < nColumns; i++) {
							nSize = metadata.getPrecision(i + 1);
							sResult.append(StringUtils.fillEndWithChar(rset.getString(i + 1), ' ', nSize));
						}
						sResult.append("\n");
					} else {
						break;
					}
				}
			} catch (Exception e) {
				sResult.append(e.getMessage());
			} finally {
				if (rset != null) {
					rset.close();
				}
			}
		} finally {
			sql.close();
		}

		return sResult.toString();
	}

	void graphicJdbcViewer() {
		Frame frame = new Frame("JdbcViewer 1.0");
		frame.setLayout(new BorderLayout());

		_taResult = new TextArea("Resultado", 20, 50);
		_taQuery = new TextArea("Query", 5, 50);
		_taHistory = new TextArea(5, 5);
		_btExecute = new Button("SQL");
		_tNumber = new TextField(4);
		_choice = new Choice();
		_choice.add("select sysdate from dual");

		// For TextArea
		Panel panel1 = new Panel(new GridLayout(2, 1));
		panel1.add(_taQuery);
		panel1.add(_taResult);

		// for Buttons
		Panel panel2 = new Panel(new FlowLayout());

		panel2.add(_btExecute);
		panel2.add(new Label("History"));
		panel2.add(_choice);
		panel2.add(new Label("Size"));
		panel2.add(_tNumber);
		frame.add(panel2, BorderLayout.NORTH);
		frame.add(panel1, BorderLayout.CENTER);
		// To be abble to close the window
		frame.addWindowListener(new WindowAdapter() {
			//
			public void windowClosing(WindowEvent event) {
				System.exit(0);
			}
		});

		// Actions
		_btExecute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.out.println("Exexute sql " + _taQuery.getText());
				_choice.add(_taQuery.getText());
				try {
					int nSize = Integer.parseInt(_tNumber.getText());
					System.out.println("size = " + nSize);
					_taResult.setText(executeSql(_taQuery.getText(), nSize) + "\n" + _taResult.getText());
				} catch (Exception e) {
					_taResult.setText(e.getMessage());
				}

			}
		});

		_choice.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				System.out.println("re Exexute sql " + (String) event.getItem());
				_taQuery.setText((String) event.getItem());

			}
		}

		);

		frame.pack();
		frame.show();

	}

	public static void main(String[] args) throws Exception {

		if (args.length == 1 && args[0] != null && args[0].equals("-g")) {
			new JdbcViewer().graphicJdbcViewer();
		} else {
			JdbcViewer jdbcViewer = new JdbcViewer();
			jdbcViewer.menu();
		}

	}
}