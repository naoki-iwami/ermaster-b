package org.insightech.er.editor.controller.editpart.element.node.column;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.insightech.er.editor.controller.editpart.element.node.TableViewEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.tracking.UpdatedNodeElement;
import org.insightech.er.editor.view.figure.table.TableFigure;
import org.insightech.er.editor.view.figure.table.column.GroupColumnFigure;

public class GroupColumnEditPart extends ColumnEditPart {

	private boolean selected;
	
	@Override
	protected IFigure createFigure() {
		GroupColumnFigure figure = new GroupColumnFigure();
		return figure;
	}

	@Override
	public void refreshTableColumns(UpdatedNodeElement updated) {
		ERDiagram diagram = this.getDiagram();

		GroupColumnFigure columnFigure = (GroupColumnFigure) this.getFigure();

		TableViewEditPart parent = (TableViewEditPart) this.getParent();
		parent.getContentPane().add(figure);

		int notationLevel = diagram.getDiagramContents().getSettings()
				.getNotationLevel();

		Column column = (Column) this.getModel();

		if (notationLevel != Settings.NOTATION_LEVLE_TITLE) {
			TableFigure tableFigure = (TableFigure) parent.getFigure();

			boolean isAdded = false;
			boolean isUpdated = false;
			if (updated != null) {
				isAdded = updated.isAdded(column);
				isUpdated = updated.isUpdated(column);
			}

			if ((notationLevel == Settings.NOTATION_LEVLE_KEY)) {
				columnFigure.clearLabel();
				return;
			}

			addGroupColumnFigure(diagram, tableFigure, columnFigure, column,
					isAdded, isUpdated, false);

			if (selected) {
				columnFigure.setBackgroundColor(ColorConstants.titleBackground);
				columnFigure.setForegroundColor(ColorConstants.titleForeground);
			}
			
		} else {
			columnFigure.clearLabel();
			return;
		}
	}

	public static void addGroupColumnFigure(ERDiagram diagram,
			TableFigure tableFigure, GroupColumnFigure columnFigure,
			Column column, boolean isAdded, boolean isUpdated, boolean isRemoved) {

		ColumnGroup groupColumn = (ColumnGroup) column;

		tableFigure.addColumnGroup(columnFigure, diagram.getDiagramContents()
				.getSettings().getViewMode(), diagram.filter(groupColumn
				.getName()), isAdded, isUpdated, isRemoved);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSelected(int value) {
		GroupColumnFigure figure = (GroupColumnFigure) this.getFigure();

		if (value != 0 && this.getParent() != null
				&& this.getParent().getParent() != null) {
			List selectedEditParts = this.getViewer().getSelectedEditParts();

			if (selectedEditParts != null && selectedEditParts.size() == 1) {
				figure.setBackgroundColor(ColorConstants.titleBackground);
				figure.setForegroundColor(ColorConstants.titleForeground);
				selected = true;
				
				super.setSelected(value);
			}

		} else {
			figure.setBackgroundColor(null);
			figure.setForegroundColor(null);
			selected = false;
			
			super.setSelected(value);
		}

	}
}
