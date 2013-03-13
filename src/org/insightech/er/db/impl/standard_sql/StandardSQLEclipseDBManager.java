package org.insightech.er.db.impl.standard_sql;

import org.eclipse.swt.widgets.Composite;
import org.insightech.er.db.EclipseDBManagerBase;
import org.insightech.er.editor.view.dialog.element.table.tab.AdvancedComposite;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;

public class StandardSQLEclipseDBManager extends EclipseDBManagerBase {

	public String getId() {
		return StandardSQLDBManager.ID;
	}

	public AdvancedComposite createAdvancedComposite(Composite composite) {
		return new StandardSQLAdvancedComposite(composite);
	}

	public TablespaceDialog createTablespaceDialog() {
		return null;
	}

}
