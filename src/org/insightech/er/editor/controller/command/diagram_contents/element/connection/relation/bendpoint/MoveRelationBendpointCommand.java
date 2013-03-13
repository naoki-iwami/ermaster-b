package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.bendpoint;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.controller.editpart.element.connection.RelationEditPart;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;

public class MoveRelationBendpointCommand extends AbstractCommand {

	private RelationEditPart editPart;

	private Bendpoint bendPoint;

	private Bendpoint oldBendpoint;

	private int index;

	public MoveRelationBendpointCommand(RelationEditPart editPart, int x,
			int y, int index) {
		this.editPart = editPart;
		this.bendPoint = new Bendpoint(x, y);
		this.index = index;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		Relation relation = (Relation) editPart.getModel();
		boolean relative = relation.getBendpoints().get(0).isRelative();

		if (relative) {
			this.oldBendpoint = relation.getBendpoints().get(0);

			this.bendPoint.setRelative(true);

			float rateX = (100f - (bendPoint.getX() / 2)) / 100;
			float rateY = (100f - (bendPoint.getY() / 2)) / 100;

			relation.setSourceLocationp(100, (int) (100 * rateY));
			relation.setTargetLocationp((int) (100 * rateX), 100);

			relation.setParentMove();

			relation.replaceBendpoint(0, this.bendPoint);

		} else {
			this.oldBendpoint = relation.getBendpoints().get(index);
			relation.replaceBendpoint(index, this.bendPoint);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		Relation relation = (Relation) editPart.getModel();
		boolean relative = relation.getBendpoints().get(0).isRelative();

		if (relative) {
			float rateX = (100f - (this.oldBendpoint.getX() / 2)) / 100;
			float rateY = (100f - (this.oldBendpoint.getY() / 2)) / 100;

			relation.setSourceLocationp(100, (int) (100 * rateY));
			relation.setTargetLocationp((int) (100 * rateX), 100);

			relation.setParentMove();

			relation.replaceBendpoint(0, this.oldBendpoint);

		} else {
			relation.replaceBendpoint(index, this.oldBendpoint);
		}
	}

}
