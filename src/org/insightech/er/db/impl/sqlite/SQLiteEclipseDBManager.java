package org.insightech.er.db.impl.sqlite;

import org.eclipse.swt.widgets.Composite;
import org.insightech.er.db.EclipseDBManagerBase;
import org.insightech.er.editor.view.dialog.element.table.tab.AdvancedComposite;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;

public class SQLiteEclipseDBManager extends EclipseDBManagerBase {

	public String getId() {
		return SQLiteDBManager.ID;
	}

	public AdvancedComposite createAdvancedComposite(Composite composite) {
		return new SQLiteAdvancedComposite(composite);
	}

	public TablespaceDialog createTablespaceDialog() {
		return null;
	}

}
