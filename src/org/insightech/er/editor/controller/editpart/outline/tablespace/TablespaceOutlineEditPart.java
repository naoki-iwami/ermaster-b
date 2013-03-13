package org.insightech.er.editor.controller.editpart.outline.tablespace;

import java.beans.PropertyChangeEvent;

import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.db.EclipseDBManagerFactory;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.tablespace.EditTablespaceCommand;
import org.insightech.er.editor.controller.editpart.DeleteableEditPart;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.controller.editpolicy.not_element.tablespace.TablespaceComponentEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;

public class TablespaceOutlineEditPart extends AbstractOutlineEditPart
		implements DeleteableEditPart {

	public void propertyChange(PropertyChangeEvent evt) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refreshOutlineVisuals() {
		Tablespace tablespace = (Tablespace) this.getModel();

		this.setWidgetText(this.getDiagram().filter(tablespace.getName()));
		this.setWidgetImage(Activator.getImage(ImageKey.TABLESPACE));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performRequest(Request request) {
		Tablespace tablespace = (Tablespace) this.getModel();
		ERDiagram diagram = this.getDiagram();

		if (request.getType().equals(RequestConstants.REQ_OPEN)) {
			TablespaceDialog dialog = EclipseDBManagerFactory
					.getEclipseDBManager(diagram).createTablespaceDialog();

			if (dialog == null) {
				Activator
						.showMessageDialog("dialog.message.tablespace.not.supported");
			} else {
				dialog.init(tablespace, diagram);

				if (dialog.open() == IDialogConstants.OK_ID) {
					EditTablespaceCommand command = new EditTablespaceCommand(
							diagram, tablespace, dialog.getResult());
					this.execute(command);
				}
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
				new TablespaceComponentEditPolicy());
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
