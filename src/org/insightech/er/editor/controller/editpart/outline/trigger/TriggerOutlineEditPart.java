package org.insightech.er.editor.controller.editpart.outline.trigger;

import java.beans.PropertyChangeEvent;

import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.trigger.EditTriggerCommand;
import org.insightech.er.editor.controller.editpart.DeleteableEditPart;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.controller.editpolicy.not_element.trigger.TriggerComponentEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.view.dialog.outline.trigger.TriggerDialog;

public class TriggerOutlineEditPart extends AbstractOutlineEditPart implements
		DeleteableEditPart {

	public void propertyChange(PropertyChangeEvent evt) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refreshOutlineVisuals() {
		Trigger trigger = (Trigger) this.getModel();

		this.setWidgetText(this.getDiagram().filter(trigger.getName()));
		this.setWidgetImage(Activator.getImage(ImageKey.TRIGGER));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performRequest(Request request) {
		Trigger trigger = (Trigger) this.getModel();
		ERDiagram diagram = (ERDiagram) this.getRoot().getContents().getModel();

		if (request.getType().equals(RequestConstants.REQ_OPEN)) {
			TriggerDialog dialog = new TriggerDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), trigger);

			if (dialog.open() == IDialogConstants.OK_ID) {
				EditTriggerCommand command = new EditTriggerCommand(diagram,
						trigger, dialog.getResult());
				this.getViewer().getEditDomain().getCommandStack().execute(
						command);
			}
		}

		super.performRequest(request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new TriggerComponentEditPolicy());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DragTracker getDragTracker(Request req) {
		return new SelectEditPartTracker(this);
	}

	public boolean isDeleteable() {
		return true;
	}
}
