package org.insightech.er.db.impl.db2;

import org.eclipse.swt.widgets.Composite;
import org.insightech.er.db.EclipseDBManagerBase;
import org.insightech.er.db.impl.db2.tablespace.DB2TablespaceDialog;
import org.insightech.er.editor.view.dialog.element.table.tab.AdvancedComposite;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;

public class DB2EclipseDBManager extends EclipseDBManagerBase {

	public String getId() {
		return DB2DBManager.ID;
	}

	public AdvancedComposite createAdvancedComposite(Composite composite) {
		return new DB2AdvancedComposite(composite);
	}

	public TablespaceDialog createTablespaceDialog() {
		return new DB2TablespaceDialog();
	}

}
