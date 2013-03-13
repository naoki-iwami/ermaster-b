package org.insightech.er.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.RowHeaderTable;
import org.insightech.er.common.widgets.table.CellEditWorker;

public class RowHeaderTableTest {
	Display display = new Display();

	Shell shell = new Shell(display);

	Text text1;

	Text text2;

	String line = "abcdefghijklmnopqrstuvwxyz0123456789";

	private void init() {
		text1 = new Text(shell, SWT.BORDER | SWT.MULTI);
		// text.setTextLimit(12);
		text1.setText(line);

		text2 = new Text(shell, SWT.BORDER | SWT.WRAP);
		text2.setText(line);

		final RowHeaderTable table = CompositeFactory.createRowHeaderTable(
				shell, 750, 150, 100, 25, 2, true, true);
		this.initTableData(table);

		table.setCellEditWorker(new CellEditWorker() {

			public void addNewRow() {
				table.addRow("+", null);
			}

			public void changeRowNum() {
			}

			public boolean isModified(int row, int column) {
				return false;
			}

		});
	}

	private void initTableData(RowHeaderTable table) {
		table.addColumnHeader("a\r\nA", 150);
		table.addColumnHeader("b\r\nB", 50);
		table.addColumnHeader("c\r\nC", 150);
		table.addColumnHeader("d\r\nD", 50);

		Object[] values = null;

		table.addRow("1", values);
		table.addRow("2", values);
		table.addRow("3", values);
		table.addRow("4", values);
		table.addRow("5", values);
		table.addRow("6", values);

		table.removeData();

		table.addColumnHeader("a\r\nA", 150);
		table.addColumnHeader("b\r\nB", 50);
		table.addColumnHeader("c\r\nC", 150);
		// table.addColumnHeader("d\r\nD", 50);

		table.addRow("1", values);
		table.addRow("2", values);
		table.addRow("3", values);
		table.addRow("4", values);
		table.addRow("5", values);
		// table.addRow("6");
	}

	public RowHeaderTableTest() {

		shell.setLayout(new GridLayout(2, true));

		(new Label(shell, SWT.NULL)).setText("SWT.BORDER |\nSWT.MUTLI");
		(new Label(shell, SWT.NULL))
				.setText("SWT.BORDER |\nSWT.MUTLI |\nSWT.WRAP");

		init();

		shell.setSize(800, 500);
		shell.open();
		// textUser.forceFocus();

		// Set up the event loop.
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				// If no more entries in event queue
				display.sleep();
			}
		}

		display.dispose();
	}

	public static void main(String[] args) {
		new RowHeaderTableTest();
	}
}
