package org.insightech.er.editor.controller.editpart.element.node;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.insightech.er.Activator;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.CreateCommentConnectionCommand;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.controller.editpart.element.connection.RelationEditPart;
import org.insightech.er.editor.controller.editpart.element.node.column.ColumnEditPart;
import org.insightech.er.editor.controller.editpart.element.node.column.GroupColumnEditPart;
import org.insightech.er.editor.controller.editpart.element.node.column.NormalColumnEditPart;
import org.insightech.er.editor.controller.editpart.element.node.index.IndexEditPart;
import org.insightech.er.editor.controller.editpolicy.element.node.table_view.TableViewComponentEditPolicy;
import org.insightech.er.editor.controller.editpolicy.element.node.table_view.TableViewGraphicalNodeEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.tracking.UpdatedNodeElement;
import org.insightech.er.editor.view.figure.anchor.XYChopboxAnchor;
import org.insightech.er.editor.view.figure.table.TableFigure;
import org.insightech.er.editor.view.figure.table.column.GroupColumnFigure;
import org.insightech.er.editor.view.figure.table.column.NormalColumnFigure;

public abstract class TableViewEditPart extends NodeElementEditPart implements
		IResizable {

	private Font titleFont;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List getModelChildren() {
		List<Object> modelChildren = new ArrayList<Object>();

		TableView tableView = (TableView) this.getModel();

		ERDiagram diagram = this.getDiagram();
		if (diagram.getDiagramContents().getSettings().isNotationExpandGroup()) {
			modelChildren.addAll(tableView.getExpandedColumns());
		} else {
			modelChildren.addAll(tableView.getColumns());
		}

		if (tableView instanceof ERTable) {
			modelChildren.addAll(((ERTable)tableView).getIndexes());
		}

		return modelChildren;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doPropertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(
				TableView.PROPERTY_CHANGE_PHYSICAL_NAME)) {
			refreshVisuals();
		} else if (event.getPropertyName().equals(
				TableView.PROPERTY_CHANGE_LOGICAL_NAME)) {
			refreshVisuals();

		} else if (event.getPropertyName().equals(
				TableView.PROPERTY_CHANGE_COLUMNS)) {
			this.refreshChildren();
			refreshVisuals();
		}

		super.doPropertyChange(event);
		this.refreshConnections();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh() {
		super.refresh();
		this.refreshConnections();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refreshVisuals() {
		try {
			TableFigure tableFigure = (TableFigure) this.getFigure();
			TableView tableView = (TableView) this.getModel();

			if (getModel() instanceof ERTable || getModel() instanceof ERVirtualTable) {
//				if (((ERTable)getModel()).getName().equals("BIZ_COMPANY_BLOCK")) {
					System.out.println("TableViewEditPart::refreshVisuals " + ((ERTable)getModel()).getName());
//				}
			}
			tableFigure.create(tableView.getColor());

			ERDiagram diagram = this.getDiagram();
			tableFigure.setName(getTableViewName(tableView, diagram));

			UpdatedNodeElement updated = null;
			if (diagram.getChangeTrackingList().isCalculated()) {
				updated = diagram.getChangeTrackingList().getUpdatedNodeElement(tableView);
			}

			for (Object child : this.getChildren()) {
				if (child instanceof ColumnEditPart) {
					ColumnEditPart part = (ColumnEditPart) child;
					part.refreshTableColumns(updated);
				}
				if (child instanceof IndexEditPart) {
					IndexEditPart part = (IndexEditPart) child;
					part.refreshTableColumns(updated);
				}
//				if (diagram.isShowMainColumn()) {
//				} else {
//					part.refreshTableColumns(updated);
//				}
			}

			if (updated != null) {
				ERTable table = null;
				if (getModel() instanceof ERTable) {
					table = (ERTable) getModel();
				}
				if (getModel() instanceof ERVirtualTable) {
					table = ((ERVirtualTable) getModel()).getRawTable();
				}

				showRemovedColumns(diagram, table, tableFigure, updated
						.getRemovedColumns(), true);
			}

			super.refreshVisuals();

			if (ERDiagramEditPart.isUpdateable()) {
				this.getFigure().getUpdateManager().performValidation();
			}

		} catch (Exception e) {
			Activator.showExceptionDialog(e);
		}

	}

	public static void showRemovedColumns(ERDiagram diagram, ERTable table,
			TableFigure tableFigure, Collection<Column> removedColumns,
			boolean isRemoved) {

		int notationLevel = diagram.getDiagramContents().getSettings()
				.getNotationLevel();

		for (Column removedColumn : removedColumns) {

			if (removedColumn instanceof ColumnGroup) {
				if (diagram.getDiagramContents().getSettings()
						.isNotationExpandGroup()) {
					ColumnGroup columnGroup = (ColumnGroup) removedColumn;

					for (NormalColumn normalColumn : columnGroup.getColumns()) {
						if (notationLevel == Settings.NOTATION_LEVLE_KEY
								&& !normalColumn.isPrimaryKey()
								&& !normalColumn.isForeignKey()
								&& !normalColumn.isReferedStrictly()) {
							continue;
						}

						NormalColumnFigure columnFigure = new NormalColumnFigure();
						tableFigure.getColumns().add(columnFigure);

						NormalColumnEditPart.addColumnFigure(diagram, table, tableFigure,
								columnFigure, normalColumn, false, false, false,
								false, isRemoved);
					}

				} else {
					if ((notationLevel == Settings.NOTATION_LEVLE_KEY)) {
						continue;
					}

					GroupColumnFigure columnFigure = new GroupColumnFigure();
					tableFigure.getColumns().add(columnFigure);

					GroupColumnEditPart.addGroupColumnFigure(diagram,
							tableFigure, columnFigure, removedColumn, false,
							false, isRemoved);
				}

			} else {
				NormalColumn normalColumn = (NormalColumn) removedColumn;
				if (notationLevel == Settings.NOTATION_LEVLE_KEY
						&& !normalColumn.isPrimaryKey()
						&& !normalColumn.isForeignKey()
						&& !normalColumn.isReferedStrictly()) {
					continue;
				}

				NormalColumnFigure columnFigure = new NormalColumnFigure();
				tableFigure.getColumns().add(columnFigure);

				NormalColumnEditPart.addColumnFigure(diagram, table, tableFigure,
						columnFigure, normalColumn, false, false, false,
						false, isRemoved);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void changeSettings(Settings settings) {
		TableFigure figure = (TableFigure) this.getFigure();
		figure.setSettings(settings);

		super.changeSettings(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void disposeFont() {
		if (this.titleFont != null) {
			this.titleFont.dispose();
		}
		super.disposeFont();
	}

	protected Font changeFont(TableFigure tableFigure) {
		Font font = super.changeFont(tableFigure);

		FontData fonData = font.getFontData()[0];

		this.titleFont = new Font(Display.getCurrent(), fonData.getName(),
				fonData.getHeight(), SWT.BOLD);

		tableFigure.setFont(font, this.titleFont);

		return font;
	}

	public static String getTableViewName(TableView tableView, ERDiagram diagram) {
		String name = null;

		int viewMode = diagram.getDiagramContents().getSettings().getViewMode();

		if (viewMode == Settings.VIEW_MODE_PHYSICAL) {
			name = diagram.filter(tableView.getPhysicalName());

		} else if (viewMode == Settings.VIEW_MODE_LOGICAL) {
			name = diagram.filter(tableView.getLogicalName());

		} else {
			name = diagram.filter(tableView.getLogicalName()) + " / "
					+ diagram.filter(tableView.getPhysicalName());
		}

		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart editPart) {
		if (!(editPart instanceof RelationEditPart)) {
			return super.getSourceConnectionAnchor(editPart);
		}

		Relation relation = (Relation) editPart.getModel();

		Rectangle bounds = this.getFigure().getBounds();

		XYChopboxAnchor anchor = new XYChopboxAnchor(this.getFigure());

		if (relation.getSourceXp() != -1 && relation.getSourceYp() != -1) {
			anchor.setLocation(new Point(bounds.x
					+ (bounds.width * relation.getSourceXp() / 100), bounds.y
					+ (bounds.height * relation.getSourceYp() / 100)));
		}

		return anchor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		if (request instanceof ReconnectRequest) {
			ReconnectRequest reconnectRequest = (ReconnectRequest) request;

			ConnectionEditPart connectionEditPart = reconnectRequest
					.getConnectionEditPart();

			if (!(connectionEditPart instanceof RelationEditPart)) {
				return super.getSourceConnectionAnchor(request);
			}

			Relation relation = (Relation) connectionEditPart.getModel();
			if (relation.getSource() == relation.getTarget()) {
				return new XYChopboxAnchor(this.getFigure());
			}

			EditPart editPart = reconnectRequest.getTarget();

			if (editPart == null
					|| !editPart.getModel().equals(relation.getSource())) {
				return new XYChopboxAnchor(this.getFigure());
			}

			Point location = new Point(reconnectRequest.getLocation());
			this.getFigure().translateToRelative(location);
			IFigure sourceFigure = ((TableViewEditPart) connectionEditPart
					.getSource()).getFigure();

			XYChopboxAnchor anchor = new XYChopboxAnchor(this.getFigure());

			Rectangle bounds = sourceFigure.getBounds();

			Rectangle centerRectangle = new Rectangle(bounds.x
					+ (bounds.width / 4), bounds.y + (bounds.height / 4),
					bounds.width / 2, bounds.height / 2);

			if (!centerRectangle.contains(location)) {
				Point point = getIntersectionPoint(location, sourceFigure);
				anchor.setLocation(point);
			}

			return anchor;

		} else if (request instanceof CreateConnectionRequest) {
			CreateConnectionRequest connectionRequest = (CreateConnectionRequest) request;

			Command command = connectionRequest.getStartCommand();

			if (command instanceof CreateCommentConnectionCommand) {
				return super.getTargetConnectionAnchor(request);
			}
		}

		return new XYChopboxAnchor(this.getFigure());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart editPart) {
		if (!(editPart instanceof RelationEditPart)) {
			return super.getTargetConnectionAnchor(editPart);
		}

		Relation relation = (Relation) editPart.getModel();

		XYChopboxAnchor anchor = new XYChopboxAnchor(this.getFigure());

		Rectangle bounds = this.getFigure().getBounds();

		if (relation.getTargetXp() != -1 && relation.getTargetYp() != -1) {
			anchor.setLocation(new Point(bounds.x
					+ (bounds.width * relation.getTargetXp() / 100), bounds.y
					+ (bounds.height * relation.getTargetYp() / 100)));
		}

		return anchor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		if (request instanceof ReconnectRequest) {
			ReconnectRequest reconnectRequest = (ReconnectRequest) request;

			ConnectionEditPart connectionEditPart = reconnectRequest
					.getConnectionEditPart();

			if (!(connectionEditPart instanceof RelationEditPart)) {
				return super.getTargetConnectionAnchor(request);
			}

			Relation relation = (Relation) connectionEditPart.getModel();
			if (relation.getSource() == relation.getTarget()) {
				return new XYChopboxAnchor(this.getFigure());
			}

			EditPart editPart = reconnectRequest.getTarget();

			if (editPart == null
					|| !editPart.getModel().equals(relation.getTarget())) {
				return new XYChopboxAnchor(this.getFigure());
			}

			Point location = new Point(reconnectRequest.getLocation());
			this.getFigure().translateToRelative(location);
			IFigure targetFigure = ((TableViewEditPart) connectionEditPart
					.getTarget()).getFigure();

			XYChopboxAnchor anchor = new XYChopboxAnchor(this.getFigure());

			Rectangle bounds = targetFigure.getBounds();

			Rectangle centerRectangle = new Rectangle(bounds.x
					+ (bounds.width / 4), bounds.y + (bounds.height / 4),
					bounds.width / 2, bounds.height / 2);

			if (!centerRectangle.contains(location)) {
				Point point = getIntersectionPoint(location, targetFigure);
				anchor.setLocation(point);
			}

			return anchor;

		} else if (request instanceof CreateConnectionRequest) {
			CreateConnectionRequest connectionRequest = (CreateConnectionRequest) request;

			Command command = connectionRequest.getStartCommand();

			if (command instanceof CreateCommentConnectionCommand) {
				return super.getTargetConnectionAnchor(request);
			}
		}

		return new XYChopboxAnchor(this.getFigure());
	}

	public static Point getIntersectionPoint(Point s, IFigure figure) {

		Rectangle r = figure.getBounds();

		int x1 = s.x - r.x;
		int x2 = r.x + r.width - s.x;
		int y1 = s.y - r.y;
		int y2 = r.y + r.height - s.y;

		int x = 0;
		int dx = 0;
		if (x1 < x2) {
			x = r.x;
			dx = x1;

		} else {
			x = r.x + r.width;
			dx = x2;
		}

		int y = 0;
		int dy = 0;

		if (y1 < y2) {
			y = r.y;
			dy = y1;

		} else {
			y = r.y + r.height;
			dy = y2;
		}

		if (dx < dy) {
			y = s.y;
		} else {
			x = s.x;
		}

		return new Point(x, y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFigure getContentPane() {
		TableFigure figure = (TableFigure) super.getContentPane();

		return figure.getColumns();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new TableViewComponentEditPolicy());
		this.installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new TableViewGraphicalNodeEditPolicy());
	}
}
