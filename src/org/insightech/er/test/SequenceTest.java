package org.insightech.er.test;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.insightech.er.Activator;
import org.insightech.er.editor.model.dbimport.DBObject;
import org.insightech.er.editor.model.dbimport.DBObjectSet;
import org.insightech.er.editor.view.dialog.dbimport.SelectImportedObjectFromDBDialog;

public class SequenceTest {

	private Display display = new Display();
	private Shell shell = new Shell(display);

	public static void main(String[] args) throws Exception {
		new Activator();
		new SequenceTest();
	}

	public SequenceTest() {
		initialize(shell);

		// shell.open();
		// while (!shell.isDisposed()) {
		// if (!display.readAndDispatch())
		// display.sleep();
		// }
		// display.dispose();
	}

	private void initialize(Composite parent) {
		DBObjectSet dbObjects = new DBObjectSet();
		dbObjects.add(new DBObject("schema", "a", DBObject.TYPE_TABLE));
		dbObjects.add(new DBObject("schema", "b", DBObject.TYPE_TABLE));
		dbObjects.add(new DBObject("schema", "c", DBObject.TYPE_TABLE));
		dbObjects.add(new DBObject("schema", "a", DBObject.TYPE_SEQUENCE));
		dbObjects.add(new DBObject("schema", "b", DBObject.TYPE_SEQUENCE));
		dbObjects.add(new DBObject("schema", "c", DBObject.TYPE_SEQUENCE));
		dbObjects.add(new DBObject("schema", "a", DBObject.TYPE_VIEW));
		dbObjects.add(new DBObject("schema", "b", DBObject.TYPE_VIEW));
		dbObjects.add(new DBObject("schema", "c", DBObject.TYPE_VIEW));

		SelectImportedObjectFromDBDialog dialog = new SelectImportedObjectFromDBDialog(shell, null,
				dbObjects);

		dialog.open();
	}

}
