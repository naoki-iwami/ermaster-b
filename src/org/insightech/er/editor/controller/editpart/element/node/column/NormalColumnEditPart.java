package org.insightech.er.editor.controller.editpart.element.node.column;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.insightech.er.editor.controller.editpart.element.node.TableViewEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.tracking.UpdatedNodeElement;
import org.insightech.er.editor.view.figure.table.TableFigure;
import org.insightech.er.editor.view.figure.table.column.NormalColumnFigure;
import org.insightech.er.util.Format;

public class NormalColumnEditPart extends ColumnEditPart {

	private boolean selected;

	@Override
	protected IFigure createFigure() {
		NormalColumnFigure figure = new NormalColumnFigure();
		return figure;
	}

	@Override
	public void refreshTableColumns(UpdatedNodeElement updated) {
		ERDiagram diagram = this.getDiagram();

		NormalColumnFigure columnFigure = (NormalColumnFigure) this.getFigure();

		NormalColumn normalColumn = (NormalColumn) this.getModel();
		
		if (diagram.isShowMainColumn()) {
			if (normalColumn.isAutoIncrement()
					|| normalColumn.getWord() == null) {
				// •\Ž¦‚·‚é
			} else {
				return; // •\Ž¦‚µ‚È‚¢
			}
		}

		TableViewEditPart parent = (TableViewEditPart) this.getParent();
		parent.getContentPane().add(figure);

		int notationLevel = diagram.getDiagramContents().getSettings().getNotationLevel();

		if (notationLevel != Settings.NOTATION_LEVLE_TITLE) {
			TableFigure tableFigure = (TableFigure) parent.getFigure();

			List<NormalColumn> selectedReferencedColulmnList = this
					.getSelectedReferencedColulmnList();
			List<NormalColumn> selectedForeignKeyColulmnList = this
					.getSelectedForeignKeyColulmnList();

			boolean isSelectedReferenced = selectedReferencedColulmnList
					.contains(normalColumn);
			boolean isSelectedForeignKey = selectedForeignKeyColulmnList
					.contains(normalColumn);

			boolean isAdded = false;
			boolean isUpdated = false;
			if (updated != null) {
				isAdded = updated.isAdded(normalColumn);
				isUpdated = updated.isUpdated(normalColumn);
			}

			if ((notationLevel == Settings.NOTATION_LEVLE_KEY)
					&& !normalColumn.isPrimaryKey()
					&& !normalColumn.isForeignKey()
					&& !normalColumn.isReferedStrictly()) {
				columnFigure.clearLabel();
				return;
			}

			addColumnFigure(diagram, tableFigure, columnFigure, normalColumn,
					isSelectedReferenced, isSelectedForeignKey, isAdded,
					isUpdated, false);

			if (selected) {
				columnFigure.setBackgroundColor(ColorConstants.titleBackground);
				columnFigure.setForegroundColor(ColorConstants.titleForeground);
			}

		} else {
			columnFigure.clearLabel();
			return;
		}
	}

	public static void addColumnFigure(ERDiagram diagram,
			TableFigure tableFigure, NormalColumnFigure columnFigure,
			NormalColumn normalColumn, boolean isSelectedReferenced,
			boolean isSelectedForeignKey, boolean isAdded, boolean isUpdated,
			boolean isRemoved) {
		int notationLevel = diagram.getDiagramContents().getSettings()
				.getNotationLevel();

		String type = diagram.filter(Format.formatType(normalColumn.getType(),
				normalColumn.getTypeData(), diagram.getDatabase()));

		boolean displayKey = true;
		if (notationLevel == Settings.NOTATION_LEVLE_COLUMN) {
			displayKey = false;
		}

		boolean displayDetail = false;
		if (notationLevel == Settings.NOTATION_LEVLE_KEY
				|| notationLevel == Settings.NOTATION_LEVLE_EXCLUDE_TYPE
				|| notationLevel == Settings.NOTATION_LEVLE_DETAIL) {
			displayDetail = true;
		}

		boolean displayType = false;
		if (notationLevel == Settings.NOTATION_LEVLE_DETAIL) {
			displayType = true;
		}

		tableFigure.addColumn(columnFigure, diagram.getDiagramContents()
				.getSettings().getViewMode(), diagram.filter(normalColumn
				.getPhysicalName()), diagram.filter(normalColumn
				.getLogicalName()), type, normalColumn.isPrimaryKey(),
				normalColumn.isForeignKey(), normalColumn.isNotNull(),
				normalColumn.isUniqueKey(), displayKey, displayDetail,
				displayType, isSelectedReferenced, isSelectedForeignKey,
				isAdded, isUpdated, isRemoved);
	}

	private List<NormalColumn> getSelectedReferencedColulmnList() {
		List<NormalColumn> referencedColulmnList = new ArrayList<NormalColumn>();

		TableViewEditPart parent = (TableViewEditPart) this.getParent();
		TableView tableView = (TableView) parent.getModel();

		for (Object object : parent.getSourceConnections()) {
			ConnectionEditPart connectionEditPart = (ConnectionEditPart) object;

			int selected = connectionEditPart.getSelected();

			if (selected == EditPart.SELECTED
					|| selected == EditPart.SELECTED_PRIMARY) {
				ConnectionElement connectionElement = (ConnectionElement) connectionEditPart
						.getModel();

				if (connectionElement instanceof Relation) {
					Relation relation = (Relation) connectionElement;

					if (relation.isReferenceForPK()) {
						referencedColulmnList.addAll(((ERTable) tableView)
								.getPrimaryKeys());

					} else if (relation.getReferencedComplexUniqueKey() != null) {
						referencedColulmnList.addAll(relation
								.getReferencedComplexUniqueKey()
								.getColumnList());

					} else {
						referencedColulmnList.add(relation
								.getReferencedColumn());
					}
				}
			}

		}
		return referencedColulmnList;
	}

	private List<NormalColumn> getSelectedForeignKeyColulmnList() {
		List<NormalColumn> foreignKeyColulmnList = new ArrayList<NormalColumn>();

		TableViewEditPart parent = (TableViewEditPart) this.getParent();

		for (Object object : parent.getTargetConnections()) {
			ConnectionEditPart connectionEditPart = (ConnectionEditPart) object;

			int selected = connectionEditPart.getSelected();

			if (selected == EditPart.SELECTED
					|| selected == EditPart.SELECTED_PRIMARY) {
				ConnectionElement connectionElement = (ConnectionElement) connectionEditPart
						.getModel();

				if (connectionElement instanceof Relation) {
					Relation relation = (Relation) connectionElement;

					foreignKeyColulmnList.addAll(relation
							.getForeignKeyColumns());
				}
			}
		}

		return foreignKeyColulmnList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSelected(int value) {
		NormalColumnFigure figure = (NormalColumnFigure) this.getFigure();

		if (value != 0 && this.getParent() != null
				&& this.getParent().getParent() != null) {
			List selectedEditParts = this.getViewer().getSelectedEditParts();

			if (selectedEditParts != null && selectedEditParts.size() == 1) {
				NormalColumn normalColumn = (NormalColumn) this.getModel();

				if (normalColumn.getColumnHolder() instanceof ColumnGroup) {
					for (Object child : this.getParent().getChildren()) {
						AbstractGraphicalEditPart childEditPart = (AbstractGraphicalEditPart) child;

						NormalColumn column = (NormalColumn) childEditPart
								.getModel();
						if (column.getColumnHolder() == normalColumn
								.getColumnHolder()) {
							this.setGroupColumnFigureColor(
									(TableViewEditPart) this.getParent(),
									(ColumnGroup) normalColumn
											.getColumnHolder(), true);
						}
					}

				} else {
					figure.setBackgroundColor(ColorConstants.titleBackground);
					figure.setForegroundColor(ColorConstants.titleForeground);
					selected = true;
				}

				super.setSelected(value);
			}

		} else {
			NormalColumn normalColumn = (NormalColumn) this.getModel();

			if (normalColumn.getColumnHolder() instanceof ColumnGroup) {
				for (Object child : this.getParent().getChildren()) {
					AbstractGraphicalEditPart childEditPart = (AbstractGraphicalEditPart) child;

					NormalColumn column = (NormalColumn) childEditPart
							.getModel();
					if (column.getColumnHolder() == normalColumn
							.getColumnHolder()) {
						this.setGroupColumnFigureColor((TableViewEditPart) this
								.getParent(), (ColumnGroup) normalColumn
								.getColumnHolder(), false);
					}
				}

			} else {
				figure.setBackgroundColor(null);
				figure.setForegroundColor(null);
				selected = false;
			}

			super.setSelected(value);
		}

	}

	private void setGroupColumnFigureColor(TableViewEditPart parentEditPart,
			ColumnGroup columnGroup, boolean selected) {
		for (NormalColumn column : columnGroup.getColumns()) {
			for (Object editPart : parentEditPart.getChildren()) {
				NormalColumnEditPart childEditPart = (NormalColumnEditPart) editPart;
				if (childEditPart.getModel() == column) {
					NormalColumnFigure columnFigure = (NormalColumnFigure) childEditPart
							.getFigure();
					if (selected) {
						columnFigure
								.setBackgroundColor(ColorConstants.titleBackground);
						columnFigure
								.setForegroundColor(ColorConstants.titleForeground);

					} else {
						columnFigure.setBackgroundColor(null);
						columnFigure.setForegroundColor(null);
					}

					childEditPart.selected = selected;
					break;
				}
			}
		}
	}
}
