package org.insightech.er.db.impl.postgres;

import org.eclipse.swt.widgets.Composite;
import org.insightech.er.db.EclipseDBManagerBase;
import org.insightech.er.db.impl.postgres.tablespace.PostgresTablespaceDialog;
import org.insightech.er.editor.view.dialog.element.table.tab.AdvancedComposite;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;

public class PostgresEclipseDBManager extends EclipseDBManagerBase {

	public String getId() {
		return PostgresDBManager.ID;
	}

	public AdvancedComposite createAdvancedComposite(Composite composite) {
		return new PostgresAdvancedComposite(composite);
	}

	public TablespaceDialog createTablespaceDialog() {
		return new PostgresTablespaceDialog();
	}

}
