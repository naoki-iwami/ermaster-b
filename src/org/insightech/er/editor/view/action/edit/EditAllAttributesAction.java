package org.insightech.er.editor.view.action.edit;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.edit.EditAllAttributesCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;
import org.insightech.er.editor.view.action.AbstractBaseAction;
import org.insightech.er.editor.view.dialog.edit.EditAllAttributesDialog;

public class EditAllAttributesAction extends AbstractBaseAction {

	public static final String ID = EditAllAttributesAction.class.getName();

	public EditAllAttributesAction(ERDiagramEditor editor) {
		super(ID, ResourceString
				.getResourceString("action.title.edit.all.attributes"), editor);
		this.setImageDescriptor(Activator.getImageDescriptor(ImageKey.EDIT));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Event event) {
		ERDiagram diagram = this.getDiagram();

		EditAllAttributesDialog dialog = new EditAllAttributesDialog(PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getShell(), diagram);

		if (dialog.open() == IDialogConstants.OK_ID) {
			DiagramContents newDiagramContents = dialog.getDiagramContents();
			EditAllAttributesCommand command = new EditAllAttributesCommand(
					diagram, newDiagramContents);
			this.execute(command);
		}
	}
}
