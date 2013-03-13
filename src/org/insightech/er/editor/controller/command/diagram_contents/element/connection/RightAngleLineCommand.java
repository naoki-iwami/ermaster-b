package org.insightech.er.editor.controller.command.diagram_contents.element.connection;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;

public class RightAngleLineCommand extends AbstractCommand {

	private static final int SPACE = 20;
	private int sourceX;

	private int sourceY;

	private int targetX;

	private int targetY;

	private ConnectionElement connection;

	private List<Bendpoint> oldBendpointList;

	private List<Bendpoint> newBendpointList;

	public RightAngleLineCommand(int sourceX, int sourceY, int targetX,
			int targetY, ConnectionEditPart connectionEditPart) {
		this.sourceX = sourceX;
		this.sourceY = sourceY;
		this.targetX = targetX;
		this.targetY = targetY;
		this.connection = (ConnectionElement) connectionEditPart.getModel();

		this.oldBendpointList = this.connection.getBendpoints();

		this.newBendpointList = new ArrayList<Bendpoint>();

		if (oldBendpointList.size() > 0) {
			if (!oldBendpointList.get(0).isRelative()) {
				int prev2X = -1;
				int prev2Y = -1;

				int prevX = this.sourceX;
				int prevY = this.sourceY;

				int x = -1;
				int y = -1;

				for (int i = 0; i < oldBendpointList.size(); i++) {
					Bendpoint bendpoint = oldBendpointList.get(i);

					if (Math.abs(prevX - bendpoint.getX()) <= Math.abs(prevY
							- bendpoint.getY())) {
						x = prevX;

						if (i == oldBendpointList.size() - 1) {
							y = targetY;
							if (x == targetX) {
								break;
							}

						} else {
							y = bendpoint.getY();
						}

					} else {
						y = prevY;

						if (i == oldBendpointList.size() - 1) {
							x = targetX;
							if (y == targetY) {
								break;
							}

						} else {
							x = bendpoint.getX();
						}
					}

					Bendpoint newBendpoint = new Bendpoint(x, y);

					if ((x == prevX && prevX == prev2X)
							|| (y == prevY && prevY == prev2Y)) {
						this.newBendpointList.remove(this.newBendpointList
								.size() - 1);
					} else {
						prev2X = prevX;
						prev2Y = prevY;
					}

					prevX = x;
					prevY = y;
					this.newBendpointList.add(newBendpoint);
				}
			}

		} else {
			if (this.sourceX != this.targetX && this.sourceY != this.targetY) {
				NodeElementEditPart sourceEditPart = (NodeElementEditPart) connectionEditPart
						.getSource();
				Rectangle sourceRectangle = sourceEditPart.getFigure()
						.getBounds();

				NodeElementEditPart targetEditPart = (NodeElementEditPart) connectionEditPart
						.getTarget();
				Rectangle targetRectangle = targetEditPart.getFigure()
						.getBounds();

				if (sourceRectangle.y - SPACE < targetY
						&& sourceRectangle.y + sourceRectangle.height + SPACE > targetRectangle.y) {
					int x = 0;
					
					if (this.sourceX < this.targetX) {
						x = (sourceRectangle.x + sourceRectangle.width + targetRectangle.x) / 2;
						
					} else {
						x = (targetRectangle.x + targetRectangle.width + sourceRectangle.x) / 2;
					}

					Bendpoint newBendpoint1 = new Bendpoint(x, sourceY);
					this.newBendpointList.add(newBendpoint1);

					Bendpoint newBendpoint2 = new Bendpoint(x, targetY);
					this.newBendpointList.add(newBendpoint2);

//				} else if (targetRectangle.x - SPACE < sourceX
//						&& targetRectangle.x + targetRectangle.width + SPACE > sourceX) {

				} else {
					int y = 0;

					if (this.sourceY < this.targetY) {
						y = (sourceRectangle.y + sourceRectangle.height + targetRectangle.y) / 2;
						
					} else {
						y = (targetRectangle.y + targetRectangle.height + sourceRectangle.y) / 2;
					}

					Bendpoint newBendpoint1 = new Bendpoint(this.sourceX, y);
					this.newBendpointList.add(newBendpoint1);

					Bendpoint newBendpoint2 = new Bendpoint(this.targetX, y);
					this.newBendpointList.add(newBendpoint2);

//				} else {
//					Bendpoint newBendpoint = new Bendpoint(sourceX, targetY);
//					this.newBendpointList.add(newBendpoint);

				}
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.connection.setBendpoints(this.newBendpointList);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.connection.setBendpoints(this.oldBendpointList);
	}
}
