package org.insightech.er.editor.view.action.tracking;

import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.action.AbstractBaseAction;
import org.insightech.er.editor.view.dialog.tracking.ChangeTrackingDialog;

public class ChangeTrackingAction extends AbstractBaseAction {

	public static final String ID = ChangeTrackingAction.class.getName();

	public ChangeTrackingAction(ERDiagramEditor editor) {
		super(ID, ResourceString
				.getResourceString("action.title.change.tracking"), editor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Event event) {
		ERDiagram diagram = this.getDiagram();

		ChangeTrackingDialog dialog = new ChangeTrackingDialog(PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getShell(), this
				.getGraphicalViewer(), diagram);

		dialog.open();
	}

}
