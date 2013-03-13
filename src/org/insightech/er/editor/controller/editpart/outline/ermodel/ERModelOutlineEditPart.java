package org.insightech.er.editor.controller.editpart.outline.ermodel;

import java.beans.PropertyChangeEvent;

import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.editor.controller.command.ermodel.OpenERModelCommand;
import org.insightech.er.editor.controller.editpart.DeleteableEditPart;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.controller.editpolicy.element.node.NodeElementComponentEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableSet;

public class ERModelOutlineEditPart extends AbstractOutlineEditPart implements
		DeleteableEditPart {

	public void propertyChange(PropertyChangeEvent evt) {
//		if (evt.getPropertyName().equals(TableSet.PROPERTY_CHANGE_TABLE_SET)) {
//			refresh();
//		}
		if (evt.getPropertyName().equals(ERModel.PROPERTY_CHANGE_VTABLES)) {
			refresh();
		}
	}

	@Override
	public void refresh() {
//		if (ERDiagramEditPart.isUpdateable()) {
			refreshChildren();
			refreshVisuals();
//		}
	}

	@Override
	public DragTracker getDragTracker(Request req) {
		return new SelectEditPartTracker(this);
	}

	public boolean isDeleteable() {
		return true;
	}

	@Override
	protected void refreshOutlineVisuals() {
		this.refreshName();

		for (Object child : this.getChildren()) {
			EditPart part = (EditPart) child;
			part.refresh();
		}
	}

	private void refreshName() {
		ERModel model = (ERModel) this.getModel();
//		ERModelSet modelSet = (ERModelSet) this.getModel();


//		ERDiagram diagram = (ERDiagram) this.getRoot().getContents().getModel();
//
//		String name = null;
//
//		int viewMode = diagram.getDiagramContents().getSettings()
//				.getOutlineViewMode();
//
//		if (viewMode == Settings.VIEW_MODE_PHYSICAL) {
//			if (model.getPhysicalName() != null) {
//				name = model.getPhysicalName();
//
//			} else {
//				name = "";
//			}
//
//		} else if (viewMode == Settings.VIEW_MODE_LOGICAL) {
//			if (model.getLogicalName() != null) {
//				name = model.getLogicalName();
//
//			} else {
//				name = "";
//			}
//
//		} else {
//			if (model.getLogicalName() != null) {
//				name = model.getLogicalName();
//
//			} else {
//				name = "";
//			}
//
//			name += "/";
//
//			if (model.getPhysicalName() != null) {
//				name += model.getPhysicalName();
//
//			}
//		}

		this.setWidgetText(model.getName());
		this.setWidgetImage(Activator.getImage(ImageKey.DIAGRAM));
	}

	@Override
	public void performRequest(Request request) {
		ERModel model = (ERModel) this.getModel();
		ERDiagram diagram = this.getDiagram();

		if (request.getType().equals(RequestConstants.REQ_OPEN)) {
			OpenERModelCommand command = new OpenERModelCommand(diagram, model);
			this.execute(command);
		}

		super.performRequest(request);
	}

	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new NodeElementComponentEditPolicy());
	}


}
