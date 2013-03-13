package org.insightech.er.db.impl.access;

import org.eclipse.swt.widgets.Composite;
import org.insightech.er.db.EclipseDBManagerBase;
import org.insightech.er.editor.view.dialog.element.table.tab.AdvancedComposite;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;

public class AccessEclipseDBManager extends EclipseDBManagerBase {

	public String getId() {
		return AccessDBManager.ID;
	}

	public AdvancedComposite createAdvancedComposite(Composite composite) {
		return new AccessAdvancedComposite(composite);
	}

	public TablespaceDialog createTablespaceDialog() {
		return null;
	}

}
