package org.insightech.er.db.impl.oracle;

import org.eclipse.swt.widgets.Composite;
import org.insightech.er.db.EclipseDBManagerBase;
import org.insightech.er.db.impl.oracle.tablespace.OracleTablespaceDialog;
import org.insightech.er.editor.view.dialog.element.table.tab.AdvancedComposite;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;

public class OracleEclipseDBManager extends EclipseDBManagerBase {

	public String getId() {
		return OracleDBManager.ID;
	}

	public AdvancedComposite createAdvancedComposite(Composite composite) {
		return new OracleAdvancedComposite(composite);
	}

	public TablespaceDialog createTablespaceDialog() {
		return new OracleTablespaceDialog();
	}

}
