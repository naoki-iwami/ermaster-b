package org.insightech.er.editor.controller.editpolicy.element.connection;

import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.BendpointEditPolicy;
import org.eclipse.gef.requests.BendpointRequest;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.bendpoint.CreateBendpointCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.bendpoint.DeleteBendpointCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.bendpoint.MoveBendpointCommand;
import org.insightech.er.editor.controller.editpart.element.AbstractModelEditPart;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.controller.editpart.element.node.ERModelEditPart;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.view.figure.connection.ERDiagramConnection;

public class ERDiagramBendpointEditPolicy extends BendpointEditPolicy {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getCreateBendpointCommand(
			BendpointRequest bendpointrequest) {
		AbstractConnectionEditPart connectionEditPart = (AbstractConnectionEditPart) this
				.getHost();
		ConnectionElement connection = (ConnectionElement) connectionEditPart
				.getModel();

		if (connection.getSource() == connection.getTarget()) {
			return null;
		}

		Point point = bendpointrequest.getLocation();
		this.getConnection().translateToRelative(point);

		CreateBendpointCommand createBendpointCommand = new CreateBendpointCommand(
				connection, point.x, point.y, bendpointrequest.getIndex());

		return createBendpointCommand;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getDeleteBendpointCommand(
			BendpointRequest bendpointrequest) {
		ConnectionElement connection = (ConnectionElement) getHost().getModel();

		if (connection.getSource() == connection.getTarget()) {
			return null;
		}

		DeleteBendpointCommand command = new DeleteBendpointCommand(connection,
				bendpointrequest.getIndex());

		return command;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getMoveBendpointCommand(BendpointRequest bendpointrequest) {
		ConnectionEditPart editPart = (ConnectionEditPart) this.getHost();

		Point point = bendpointrequest.getLocation();
		this.getConnection().translateToRelative(point);

		MoveBendpointCommand command = new MoveBendpointCommand(editPart,
				point.x, point.y, bendpointrequest.getIndex());

		return command;
	}

	@Override
	protected List createSelectionHandles() {
		this.showSelectedLine();
		return super.createSelectionHandles();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void showSelection() {
		EditPart contents = this.getHost().getRoot().getContents();
		if (contents instanceof ERModelEditPart) {
			ERModelEditPart part = (ERModelEditPart) contents;
			part.refreshVisuals();
		} else {
			ERDiagramEditPart diagramEditPart = (ERDiagramEditPart) contents;
			diagramEditPart.refreshVisuals();
		}

		super.showSelection();
	}

	protected void showSelectedLine() {
		ERDiagramConnection connection = (ERDiagramConnection) this
				.getHostFigure();
		connection.setSelected(true);
	}

	@Override
	protected void removeSelectionHandles() {
		ERDiagramConnection connection = (ERDiagramConnection) this
				.getHostFigure();
		connection.setSelected(false);

		super.removeSelectionHandles();
	}
}
