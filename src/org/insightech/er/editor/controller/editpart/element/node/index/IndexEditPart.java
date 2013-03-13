package org.insightech.er.editor.controller.editpart.element.node.index;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.internal.ui.rulers.GuideEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.insightech.er.editor.controller.editpart.element.AbstractModelEditPart;
import org.insightech.er.editor.controller.editpart.element.node.TableViewEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.tracking.UpdatedNodeElement;
import org.insightech.er.editor.view.figure.table.IndexFigure;
import org.insightech.er.editor.view.figure.table.TableFigure;

public class IndexEditPart extends AbstractModelEditPart {

	private boolean selected;

	@Override
	protected void createEditPolicies() {
//		this.installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
//				new ColumnSelectionHandlesEditPolicy());
//		this.installEditPolicy(EditPolicy.COMPONENT_ROLE,
//				new NormalColumnComponentEditPolicy());
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

	@Override
	protected IFigure createFigure() {
		IndexFigure figure = new IndexFigure();
//		figure.setBorder(new LineBorder(ColorConstants.black, 1));
		return figure;
	}

	public void refreshTableColumns(UpdatedNodeElement updated) {
		ERDiagram diagram = this.getDiagram();

		IndexFigure indexFigure = (IndexFigure) this.getFigure();

		Index index = (Index) this.getModel();

		if (diagram.isShowMainColumn()) {
			// インデックスは常に表示
		}

		int notationLevel = diagram.getDiagramContents().getSettings().getNotationLevel();

		TableViewEditPart parent = (TableViewEditPart) this.getParent();
		List figures = parent.getContentPane().getChildren();
		boolean isFirst = false;
		if (! (figures.get(figures.size() - 1) instanceof IndexFigure)) {
			// Index の先頭はボーダーを付ける
			if (notationLevel != Settings.NOTATION_LEVLE_TITLE) {
				isFirst = true;
				parent.getContentPane().add(new GuideEditPart.GuideLineFigure());
				Label indexHeader = new Label();
				indexHeader.setLabelAlignment(PositionConstants.LEFT);
				indexHeader.setText("<< index >>");
				indexHeader.setBorder(new MarginBorder(new Insets(4, 3, 0, 0)));

				parent.getContentPane().add(indexHeader);
			}
		}
		parent.getContentPane().add(figure);

		if (notationLevel != Settings.NOTATION_LEVLE_TITLE) {
			TableFigure tableFigure = (TableFigure) parent.getFigure();

//			List<NormalColumn> selectedReferencedColulmnList = this
//					.getSelectedReferencedColulmnList();
//			List<NormalColumn> selectedForeignKeyColulmnList = this
//					.getSelectedForeignKeyColulmnList();
//
//			boolean isSelectedReferenced = selectedReferencedColulmnList
//					.contains(index);
//			boolean isSelectedForeignKey = selectedForeignKeyColulmnList
//					.contains(index);
//
//			boolean isAdded = false;
//			boolean isUpdated = false;
//			if (updated != null) {
//				isAdded = updated.isAdded(index);
//				isUpdated = updated.isUpdated(index);
//			}
//
//			if ((notationLevel == Settings.NOTATION_LEVLE_KEY)
//					&& !index.isPrimaryKey()
//					&& !index.isForeignKey()
//					&& !index.isReferedStrictly()) {
//				indexFigure.clearLabel();
//				return;
//			}

			addColumnFigure(diagram, tableFigure, indexFigure, index,
					isFirst,
					/* isSelectedReferenced, isSelectedForeignKey, isAdded, isUpdated, */
					false);

			if (selected) {
				indexFigure.setBackgroundColor(ColorConstants.titleBackground);
				indexFigure.setForegroundColor(ColorConstants.titleForeground);
			}

		} else {
			indexFigure.clearLabel();
			return;
		}
	}

	public static void addColumnFigure(ERDiagram diagram,
			TableFigure tableFigure, IndexFigure indexFigure, Index index,
			/*boolean isSelectedReferenced, boolean isSelectedForeignKey, boolean isAdded, boolean isUpdated, */
			boolean isFirst, boolean isRemoved) {
//		int notationLevel = diagram.getDiagramContents().getSettings()
//				.getNotationLevel();
//
//		String type = diagram.filter(Format.formatType(normalColumn.getType(),
//				normalColumn.getTypeData(), diagram.getDatabase()));
//		boolean displayKey = true;
//		if (notationLevel == Settings.NOTATION_LEVLE_COLUMN) {
//			displayKey = false;
//		}
//
//		boolean displayDetail = false;
//		if (notationLevel == Settings.NOTATION_LEVLE_KEY
//				|| notationLevel == Settings.NOTATION_LEVLE_EXCLUDE_TYPE
//				|| notationLevel == Settings.NOTATION_LEVLE_DETAIL) {
//			displayDetail = true;
//		}
//
//		boolean displayType = false;
//		if (notationLevel == Settings.NOTATION_LEVLE_DETAIL) {
//			displayType = true;
//		}

		tableFigure.addIndex(indexFigure,
				diagram.getDiagramContents().getSettings().getViewMode(),
				diagram.filter(index.getName()),
				diagram.filter(index.getName()),
				isFirst);
	}

//	private List<NormalColumn> getSelectedReferencedColulmnList() {
//		List<NormalColumn> referencedColulmnList = new ArrayList<NormalColumn>();
//
//		TableViewEditPart parent = (TableViewEditPart) this.getParent();
//		TableView tableView = (TableView) parent.getModel();
//
//		for (Object object : parent.getSourceConnections()) {
//			ConnectionEditPart connectionEditPart = (ConnectionEditPart) object;
//
//			int selected = connectionEditPart.getSelected();
//
//			if (selected == EditPart.SELECTED
//					|| selected == EditPart.SELECTED_PRIMARY) {
//				ConnectionElement connectionElement = (ConnectionElement) connectionEditPart
//						.getModel();
//
//				if (connectionElement instanceof Relation) {
//					Relation relation = (Relation) connectionElement;
//
//					if (relation.isReferenceForPK()) {
//						referencedColulmnList.addAll(((ERTable) tableView)
//								.getPrimaryKeys());
//
//					} else if (relation.getReferencedComplexUniqueKey() != null) {
//						referencedColulmnList.addAll(relation
//								.getReferencedComplexUniqueKey()
//								.getColumnList());
//
//					} else {
//						referencedColulmnList.add(relation
//								.getReferencedColumn());
//					}
//				}
//			}
//
//		}
//		return referencedColulmnList;
//	}
//
//	private List<NormalColumn> getSelectedForeignKeyColulmnList() {
//		List<NormalColumn> foreignKeyColulmnList = new ArrayList<NormalColumn>();
//
//		TableViewEditPart parent = (TableViewEditPart) this.getParent();
//
//		for (Object object : parent.getTargetConnections()) {
//			ConnectionEditPart connectionEditPart = (ConnectionEditPart) object;
//
//			int selected = connectionEditPart.getSelected();
//
//			if (selected == EditPart.SELECTED
//					|| selected == EditPart.SELECTED_PRIMARY) {
//				ConnectionElement connectionElement = (ConnectionElement) connectionEditPart
//						.getModel();
//
//				if (connectionElement instanceof Relation) {
//					Relation relation = (Relation) connectionElement;
//
//					foreignKeyColulmnList.addAll(relation
//							.getForeignKeyColumns());
//				}
//			}
//		}
//
//		return foreignKeyColulmnList;
//	}

//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public void setSelected(int value) {
//		NormalColumnFigure figure = (NormalColumnFigure) this.getFigure();
//
//		if (value != 0 && this.getParent() != null
//				&& this.getParent().getParent() != null) {
//			List selectedEditParts = this.getViewer().getSelectedEditParts();
//
//			if (selectedEditParts != null && selectedEditParts.size() == 1) {
//				NormalColumn normalColumn = (NormalColumn) this.getModel();
//
//				if (normalColumn.getColumnHolder() instanceof ColumnGroup) {
//					for (Object child : this.getParent().getChildren()) {
//						AbstractGraphicalEditPart childEditPart = (AbstractGraphicalEditPart) child;
//
//						NormalColumn column = (NormalColumn) childEditPart
//								.getModel();
//						if (column.getColumnHolder() == normalColumn
//								.getColumnHolder()) {
//							this.setGroupColumnFigureColor(
//									(TableViewEditPart) this.getParent(),
//									(ColumnGroup) normalColumn
//											.getColumnHolder(), true);
//						}
//					}
//
//				} else {
//					figure.setBackgroundColor(ColorConstants.titleBackground);
//					figure.setForegroundColor(ColorConstants.titleForeground);
//					selected = true;
//				}
//
//				super.setSelected(value);
//			}
//
//		} else {
//			NormalColumn normalColumn = (NormalColumn) this.getModel();
//
//			if (normalColumn.getColumnHolder() instanceof ColumnGroup) {
//				for (Object child : this.getParent().getChildren()) {
//					AbstractGraphicalEditPart childEditPart = (AbstractGraphicalEditPart) child;
//
//					NormalColumn column = (NormalColumn) childEditPart
//							.getModel();
//					if (column.getColumnHolder() == normalColumn
//							.getColumnHolder()) {
//						this.setGroupColumnFigureColor((TableViewEditPart) this
//								.getParent(), (ColumnGroup) normalColumn
//								.getColumnHolder(), false);
//					}
//				}
//
//			} else {
//				figure.setBackgroundColor(null);
//				figure.setForegroundColor(null);
//				selected = false;
//			}
//
//			super.setSelected(value);
//		}
//
//	}
//
//	private void setGroupColumnFigureColor(TableViewEditPart parentEditPart,
//			ColumnGroup columnGroup, boolean selected) {
//		for (NormalColumn column : columnGroup.getColumns()) {
//			for (Object editPart : parentEditPart.getChildren()) {
//				NormalColumnEditPart childEditPart = (NormalColumnEditPart) editPart;
//				if (childEditPart.getModel() == column) {
//					NormalColumnFigure columnFigure = (NormalColumnFigure) childEditPart
//							.getFigure();
//					if (selected) {
//						columnFigure
//								.setBackgroundColor(ColorConstants.titleBackground);
//						columnFigure
//								.setForegroundColor(ColorConstants.titleForeground);
//
//					} else {
//						columnFigure.setBackgroundColor(null);
//						columnFigure.setForegroundColor(null);
//					}
//
//					childEditPart.selected = selected;
//					break;
//				}
//			}
//		}
//	}

}
