package org.insightech.er.test;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.insightech.er.Activator;
import org.insightech.er.db.impl.mysql.MySQLDBManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.testdata.TestData;
import org.insightech.er.editor.view.dialog.testdata.TestDataManageDialog;

public class TableTest {

	private Display display = new Display();
	private Shell shell = new Shell(display);

	public static void main(String[] args) throws Exception {
		new Activator();
		new TableTest();
	}

	public TableTest() {
		initialize(shell);
	}

	private void initialize(Composite parent) {
		List<TestData> testDataList = new ArrayList<TestData>();
		TestDataManageDialog dialog = new TestDataManageDialog(shell, null,
				new ERDiagram(MySQLDBManager.ID), testDataList);

		dialog.open();
	}
}
