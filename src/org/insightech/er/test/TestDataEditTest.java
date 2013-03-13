package org.insightech.er.test;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.insightech.er.Activator;
import org.insightech.er.db.impl.mysql.MySQLDBManager;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.testdata.TestData;
import org.insightech.er.editor.view.dialog.testdata.detail.TestDataDialog;

public class TestDataEditTest {

	private Display display = new Display();
	private Shell shell = new Shell(display);

	public static void main(String[] args) throws Exception {
		new Activator();
		new TestDataEditTest();
	}

	public TestDataEditTest() {
		initialize(shell);
	}

	private void initialize(Composite parent) {
		ERDiagram diagram = new ERDiagram(MySQLDBManager.ID);
		ERTable table = new ERTable();
		table.setPhysicalName("table1");
		table.setLogicalName("table1");
		TypeData typeData = new TypeData(null, null, false, null, false, null);

		Word word1 = new Word("a", "a", SqlType.valueOfId("bigint"), typeData,
				null, MySQLDBManager.ID);

		NormalColumn column1 = new NormalColumn(word1, true, true, true, true,
				null, null, null, null, null);

		Word word2 = new Word("a", "a", SqlType.valueOfId("bigint"), typeData,
				null, MySQLDBManager.ID);
		NormalColumn column2 = new NormalColumn(word2, true, true, true, true,
				null, null, null, null, null);
		table.addColumn(column1);
		table.addColumn(column2);

		diagram.addContent(table);

		TestDataDialog dialog = new TestDataDialog(shell, diagram,
				new TestData());

		dialog.open();
	}
}
