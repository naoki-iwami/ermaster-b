package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.controller.editpart.element.node.TableViewEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;

public class CreateRelatedTableCommand extends AbstractCreateRelationCommand {

	private Relation relation1;

	private Relation relation2;

	private ERTable relatedTable;

	private ERDiagram diagram;

	private int sourceX;

	private int sourceY;

	private int targetX;

	private int targetY;

	public CreateRelatedTableCommand() {
		super();

		this.relatedTable = new ERTable();
	}

	public void setSourcePoint(int x, int y) {
		this.sourceX = x;
		this.sourceY = y;
	}

	private void setTargetPoint(int x, int y) {
		this.targetX = x;
		this.targetY = y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTarget(EditPart target) {
		super.setTarget(target);

		if (target != null) {
			if (target instanceof TableViewEditPart) {
				TableViewEditPart tableEditPart = (TableViewEditPart) target;

				Point point = tableEditPart.getFigure().getBounds().getCenter();
				this.setTargetPoint(point.x, point.y);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		ERDiagramEditPart.setUpdateable(false);

		this.init();

		this.diagram.addNewContent(this.relatedTable);

		this.relation1.setSource((ERTable) this.source.getModel());
		this.relation1.setTargetTableView(this.relatedTable);

		this.relation2.setSource((ERTable) this.target.getModel());
		this.relation2.setTargetTableView(this.relatedTable);

		ERDiagramEditPart.setUpdateable(true);

		this.diagram.getDiagramContents().getContents().getTableSet()
				.setDirty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		ERDiagramEditPart.setUpdateable(false);

		this.diagram.removeContent(this.relatedTable);

		this.relation1.setSource(null);
		this.relation1.setTargetTableView(null);

		this.relation2.setSource(null);
		this.relation2.setTargetTableView(null);

		ERDiagramEditPart.setUpdateable(true);

		this.diagram.getDiagramContents().getContents().getTableSet()
				.setDirty();
	}

	private void init() {
		ERTable sourceTable = (ERTable) this.getSourceModel();

		this.diagram = sourceTable.getDiagram();

		this.relation1 = sourceTable.createRelation();

		ERTable targetTable = (ERTable) this.getTargetModel();
		this.relation2 = targetTable.createRelation();

		this.relatedTable.setLocation(new Location(
				(this.sourceX + this.targetX - ERTable.DEFAULT_WIDTH) / 2,
				(this.sourceY + this.targetY - ERTable.DEFAULT_HEIGHT) / 2,
				ERTable.DEFAULT_WIDTH, ERTable.DEFAULT_HEIGHT));

		this.relatedTable.setLogicalName(ERTable.NEW_LOGICAL_NAME);
		this.relatedTable.setPhysicalName(ERTable.NEW_PHYSICAL_NAME);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canExecute() {
		if (!super.canExecute()) {
			return false;
		}

		if (!(this.getSourceModel() instanceof ERTable)
				|| !(this.getTargetModel() instanceof ERTable)) {
			return false;
		}

		return true;
	}
}
