package org.insightech.er.editor.controller.editpolicy.element.node;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.insightech.er.Activator;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.AbstractCreateConnectionCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.CreateCommentConnectionCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.CreateConnectionCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.AbstractCreateRelationCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.CreateRelatedTableCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.CreateRelationByExistingColumnsCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.CreateRelationCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.CreateSelfRelationCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.ReconnectSourceCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.ReconnectTargetCommand;
import org.insightech.er.editor.controller.editpart.element.node.ERTableEditPart;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.controller.editpart.element.node.TableViewEditPart;
import org.insightech.er.editor.model.diagram_contents.element.connection.CommentConnection;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.connection.RelatedTable;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.connection.RelationByExistingColumns;
import org.insightech.er.editor.model.diagram_contents.element.connection.SelfRelation;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;

public class NodeElementGraphicalNodeEditPolicy extends GraphicalNodeEditPolicy {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getConnectionCompleteCommand(
			CreateConnectionRequest request) {
		AbstractCreateConnectionCommand command = (AbstractCreateConnectionCommand) request
				.getStartCommand();

		NodeElementEditPart targetEditPart = (NodeElementEditPart) request
				.getTargetEditPart();

		if (command instanceof AbstractCreateRelationCommand) {
			if (!(targetEditPart instanceof TableViewEditPart)) {
				return null;
			}
		}

		String validatedMessage = command.validate();
		if (validatedMessage != null) {
			Activator.showErrorDialog(validatedMessage);

			return null;
		}

		command.setTarget(targetEditPart);

		if (!command.canExecute()) {
			return null;
		}

		return command;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		EditPart editPart = request.getTargetEditPart();
		Object object = request.getNewObject();

		if (editPart instanceof ERTableEditPart) {
			Command command = this.getRelationCreateCommand(request, object);

			if (command != null) {
				return command;
			}
		}

		if (object instanceof CommentConnection) {
			CommentConnection connection = (CommentConnection) object;

			CreateConnectionCommand command = new CreateCommentConnectionCommand(
					connection);

			command.setSource(request.getTargetEditPart());
			request.setStartCommand(command);

			return command;
		}

		return null;
	}

	private Command getRelationCreateCommand(CreateConnectionRequest request,
			Object object) {
		if (object instanceof Relation) {
			Relation relation = (Relation) object;
			CreateRelationCommand command = new CreateRelationCommand(relation);

			EditPart source = request.getTargetEditPart();
			command.setSource(source);

			ERTable sourceTable = (ERTable) source.getModel();

			Relation temp = sourceTable.createRelation();
			relation.setReferenceForPK(temp.isReferenceForPK());
			relation.setReferencedComplexUniqueKey(temp
					.getReferencedComplexUniqueKey());
			relation.setReferencedColumn(temp.getReferencedColumn());

			request.setStartCommand(command);

			return command;

		} else if (object instanceof RelatedTable) {
			CreateRelatedTableCommand command = new CreateRelatedTableCommand();

			ERTableEditPart sourceEditPart = (ERTableEditPart) request
					.getTargetEditPart();

			command.setSource(sourceEditPart);

			if (sourceEditPart != null) {
				Point point = sourceEditPart.getFigure().getBounds()
						.getCenter();
				command.setSourcePoint(point.x, point.y);
			}

			request.setStartCommand(command);

			return command;

		} else if (object instanceof SelfRelation) {
			ERTableEditPart sourceEditPart = (ERTableEditPart) request
					.getTargetEditPart();
			ERTable sourceTable = (ERTable) sourceEditPart.getModel();

			CreateSelfRelationCommand command = new CreateSelfRelationCommand(
					sourceTable.createRelation());

			command.setSource(sourceEditPart);

			request.setStartCommand(command);

			return command;

		} else if (object instanceof RelationByExistingColumns) {
			CreateRelationByExistingColumnsCommand command = new CreateRelationByExistingColumnsCommand();

			EditPart source = request.getTargetEditPart();
			command.setSource(source);

			request.setStartCommand(command);

			return command;
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getReconnectSourceCommand(
			ReconnectRequest reconnectrequest) {
		ConnectionElement connection = (ConnectionElement) reconnectrequest
				.getConnectionEditPart().getModel();

		if (!(connection instanceof Relation)) {
			return null;
		}

		Relation relation = (Relation) connection;

		if (relation.getSource() == relation.getTarget()) {
			return null;
		}

		NodeElement newSource = (NodeElement) reconnectrequest.getTarget().getModel();
		if (!relation.getSource().equals(newSource)) {
			return null;
		}

		NodeElementEditPart sourceEditPart = (NodeElementEditPart) reconnectrequest
				.getConnectionEditPart().getSource();

		Point location = new Point(reconnectrequest.getLocation());

		IFigure sourceFigure = sourceEditPart.getFigure();
		sourceFigure.translateToRelative(location);

		int xp = -1;
		int yp = -1;

		Rectangle bounds = sourceFigure.getBounds();

		Rectangle centerRectangle = new Rectangle(
				bounds.x + (bounds.width / 4), bounds.y + (bounds.height / 4),
				bounds.width / 2, bounds.height / 2);

		if (!centerRectangle.contains(location)) {
			Point point = ERTableEditPart.getIntersectionPoint(location,
					sourceFigure);
			xp = 100 * (point.x - bounds.x) / bounds.width;
			yp = 100 * (point.y - bounds.y) / bounds.height;
		}

		ReconnectSourceCommand command = new ReconnectSourceCommand(relation,
				xp, yp);

		return command;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getReconnectTargetCommand(
			ReconnectRequest reconnectrequest) {
		ConnectionElement connection = (ConnectionElement) reconnectrequest
				.getConnectionEditPart().getModel();

		if (!(connection instanceof Relation)) {
			return null;
		}

		Relation relation = (Relation) connection;

		if (relation.getSource() == relation.getTarget()) {
			return null;
		}

		NodeElement newTarget = (NodeElement) reconnectrequest.getTarget()
				.getModel();
		if (!relation.getTarget().equals(newTarget)) {
			return null;
		}

		NodeElementEditPart targetEditPart = (NodeElementEditPart) reconnectrequest
				.getConnectionEditPart().getTarget();

		Point location = new Point(reconnectrequest.getLocation());

		IFigure targetFigure = targetEditPart.getFigure();
		targetFigure.translateToRelative(location);

		int xp = -1;
		int yp = -1;

		Rectangle bounds = targetFigure.getBounds();

		Rectangle centerRectangle = new Rectangle(
				bounds.x + (bounds.width / 4), bounds.y + (bounds.height / 4),
				bounds.width / 2, bounds.height / 2);

		if (!centerRectangle.contains(location)) {
			Point point = ERTableEditPart.getIntersectionPoint(location,
					targetFigure);

			xp = 100 * (point.x - bounds.x) / bounds.width;
			yp = 100 * (point.y - bounds.y) / bounds.height;
		}
		ReconnectTargetCommand command = new ReconnectTargetCommand(relation,
				xp, yp);

		return command;
	}
}
