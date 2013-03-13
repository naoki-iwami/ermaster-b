package org.insightech.er.editor.controller.editpart.element.node.column;

import java.beans.PropertyChangeEvent;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.insightech.er.editor.controller.editpart.element.AbstractModelEditPart;
import org.insightech.er.editor.controller.editpolicy.element.node.table_view.NormalColumnComponentEditPolicy;
import org.insightech.er.editor.controller.editpolicy.element.node.table_view.ColumnSelectionHandlesEditPolicy;
import org.insightech.er.editor.model.tracking.UpdatedNodeElement;

public abstract class ColumnEditPart extends AbstractModelEditPart {

	public abstract void refreshTableColumns(UpdatedNodeElement updated);

	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new ColumnSelectionHandlesEditPolicy());
		this.installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new NormalColumnComponentEditPolicy());
	}

	@Override
	public void doPropertyChange(PropertyChangeEvent evt) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EditPart getTargetEditPart(Request request) {
		EditPart editPart = super.getTargetEditPart(request);

		if (!this.getDiagram().isDisableSelectColumn()) {
			return editPart;
		}

		if (editPart != null) {
			return editPart.getParent();
		}

		return null;
	}
}
