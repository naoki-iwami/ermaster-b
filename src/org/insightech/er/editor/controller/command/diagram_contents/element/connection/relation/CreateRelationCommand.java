package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import java.util.List;

import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModelSet;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class CreateRelationCommand extends AbstractCreateRelationCommand {

	private Relation relation;

	private List<NormalColumn> foreignKeyColumnList;

	public CreateRelationCommand(Relation relation) {
		this(relation, null);
	}

	public CreateRelationCommand(Relation relation,
			List<NormalColumn> foreignKeyColumnList) {
		super();
		this.relation = relation;
		this.foreignKeyColumnList = foreignKeyColumnList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		ERDiagramEditPart.setUpdateable(false);

		this.relation.setSource((TableView) source.getModel());

		ERDiagramEditPart.setUpdateable(true);

		this.relation.setTargetTableView((TableView) target.getModel(),
				this.foreignKeyColumnList);

		if (this.relation.getSource() instanceof ERTable
				|| this.relation.getTarget() instanceof ERTable) {
			// ビュー内でリレーションを消した場合、ここにはERVirtualTableでなくERTableで来る
			ERModelSet modelSet = this.relation.getSource().getDiagram().getDiagramContents().getModelSet();
			modelSet.createRelation(relation);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		ERDiagramEditPart.setUpdateable(false);

		this.relation.setSource(null);

		ERDiagramEditPart.setUpdateable(true);

		this.relation.setTargetTableView(null);

		TableView targetTable = (TableView) this.target.getModel();
		targetTable.setDirty();
	}
}
