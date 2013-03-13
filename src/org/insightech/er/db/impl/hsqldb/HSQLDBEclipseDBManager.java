package org.insightech.er.db.impl.hsqldb;

import org.eclipse.swt.widgets.Composite;
import org.insightech.er.db.EclipseDBManagerBase;
import org.insightech.er.editor.view.dialog.element.table.tab.AdvancedComposite;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;

public class HSQLDBEclipseDBManager extends EclipseDBManagerBase {

	public String getId() {
		return HSQLDBDBManager.ID;
	}

	public AdvancedComposite createAdvancedComposite(Composite composite) {
		return new HSQLDBAdvancedComposite(composite);
	}

	public TablespaceDialog createTablespaceDialog() {
		return null;
	}

}
