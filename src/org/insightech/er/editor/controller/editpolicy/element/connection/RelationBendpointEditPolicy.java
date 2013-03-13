package org.insightech.er.editor.controller.editpolicy.element.connection;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.handles.BendpointMoveHandle;
import org.eclipse.gef.requests.BendpointRequest;
import org.eclipse.swt.SWT;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.bendpoint.MoveRelationBendpointCommand;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.controller.editpart.element.connection.RelationEditPart;
import org.insightech.er.editor.controller.editpart.element.node.ERTableEditPart;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;

public class RelationBendpointEditPolicy extends ERDiagramBendpointEditPolicy {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void showMoveBendpointFeedback(BendpointRequest bendpointrequest) {
		Relation relation = (Relation) getHost().getModel();
		RelationEditPart editPart = (RelationEditPart) this.getHost();

		if (relation.getSource() == relation.getTarget()) {
			if (bendpointrequest.getIndex() != 1) {
				return;
			}
			Point point = bendpointrequest.getLocation();
			this.getConnection().translateToRelative(point);
			Bendpoint rate = this.getRate(point);
			rate.setRelative(true);

			float rateX = (100f - (rate.getX() / 2)) / 100;
			float rateY = (100f - (rate.getY() / 2)) / 100;

			ERTableEditPart tableEditPart = (ERTableEditPart) editPart
					.getSource();
			Rectangle bounds = tableEditPart.getFigure().getBounds();

			Rectangle rect = new Rectangle();
			rect.x = (int) (bounds.x + (bounds.width * rateX));
			rect.y = (int) (bounds.y + (bounds.height * rateY));
			rect.width = (int) (bounds.width * rate.getX() / 100);
			rect.height = (int) (bounds.height * rate.getY() / 100);

			relation.setSourceLocationp(100, (int) (100 * rateY));

			relation.setTargetLocationp((int) (100 * rateX), 100);

			LayerManager manager = (LayerManager) tableEditPart.getRoot();
			IFigure layer = manager.getLayer(LayerConstants.PRIMARY_LAYER);
			this.getFeedbackLayer().setBounds(layer.getBounds());

			List children = this.getFeedbackLayer().getChildren();
			children.clear();
			this.getFeedbackLayer().repaint();

			ZoomManager zoomManager = ((ScalableFreeformRootEditPart) this
					.getHost().getRoot()).getZoomManager();
			double zoom = zoomManager.getZoom();

			Polyline feedbackFigure = new Polyline();
			feedbackFigure.addPoint(new Point((int) (rect.x * zoom),
					(int) (rect.y * zoom)));
			feedbackFigure.addPoint(new Point((int) (rect.x * zoom),
					(int) ((rect.y + rect.height) * zoom)));
			feedbackFigure.addPoint(new Point(
					(int) ((rect.x + rect.width) * zoom),
					(int) ((rect.y + rect.height) * zoom)));
			feedbackFigure
					.addPoint(new Point((int) ((rect.x + rect.width) * zoom),
							(int) (rect.y * zoom)));
			feedbackFigure.addPoint(new Point((int) (rect.x * zoom),
					(int) (rect.y * zoom)));

			feedbackFigure.setLineStyle(SWT.LINE_DASH);

			feedbackFigure.translateToRelative(feedbackFigure.getLocation());

			this.addFeedback(feedbackFigure);

		} else {
			super.showMoveBendpointFeedback(bendpointrequest);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void showCreateBendpointFeedback(BendpointRequest bendpointrequest) {
		Relation relation = (Relation) getHost().getModel();

		if (relation.getSource() == relation.getTarget()) {
			return;
		}
		super.showCreateBendpointFeedback(bendpointrequest);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void eraseConnectionFeedback(BendpointRequest request) {
		this.getFeedbackLayer().getChildren().clear();
		super.eraseConnectionFeedback(request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getMoveBendpointCommand(BendpointRequest bendpointrequest) {
		Relation relation = (Relation) getHost().getModel();
		RelationEditPart editPart = (RelationEditPart) this.getHost();

		if (relation.getSource() == relation.getTarget()) {
			if (bendpointrequest.getIndex() != 1) {
				return null;

			} else {
				Point point = bendpointrequest.getLocation();
				Bendpoint rate = this.getRate(point);

				MoveRelationBendpointCommand command = new MoveRelationBendpointCommand(
						editPart, rate.getX(), rate.getY(), bendpointrequest
								.getIndex());

				return command;
			}
		}

		Point point = bendpointrequest.getLocation();
		this.getConnection().translateToRelative(point);

		MoveRelationBendpointCommand command = new MoveRelationBendpointCommand(
				editPart, point.x, point.y, bendpointrequest.getIndex());

		return command;
	}

	private Bendpoint getRate(Point point) {
		RelationEditPart editPart = (RelationEditPart) this.getHost();

		ERTableEditPart tableEditPart = (ERTableEditPart) editPart.getSource();
		Rectangle rectangle = tableEditPart.getFigure().getBounds();

		int xRate = (point.x - rectangle.x - rectangle.width) * 200
				/ rectangle.width;
		int yRate = (point.y - rectangle.y - rectangle.height) * 200
				/ rectangle.height;

		return new Bendpoint(xRate, yRate);
	}

	@Override
	protected void showSelection() {
		super.showSelection();

		RelationEditPart editPart = (RelationEditPart) this.getHost();
		editPart.refresh();
	}

	@Override
	protected void hideSelection() {
		super.hideSelection();

		RelationEditPart editPart = (RelationEditPart) this.getHost();
		editPart.refresh();
	}

	@Override
	protected List createSelectionHandles() {
		Relation relation = (Relation) getHost().getModel();

		if (relation.getSource() == relation.getTarget()) {
			List<BendpointMoveHandle> list = new ArrayList<BendpointMoveHandle>();

			ConnectionEditPart connEP = (ConnectionEditPart) getHost();

			list.add(new BendpointMoveHandle(connEP, 1, 2));

			this.showSelectedLine();

			ERDiagramEditPart diagramEditPart = (ERDiagramEditPart) this
					.getHost().getRoot().getContents();
			diagramEditPart.refreshVisuals();

			return list;
		}

		return super.createSelectionHandles();
	}
}
