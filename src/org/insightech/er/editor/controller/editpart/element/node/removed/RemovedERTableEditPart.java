package org.insightech.er.editor.controller.editpart.element.node.removed;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.insightech.er.Activator;
import org.insightech.er.editor.controller.editpart.element.node.IResizable;
import org.insightech.er.editor.controller.editpart.element.node.TableViewEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.tracking.RemovedERTable;
import org.insightech.er.editor.view.figure.table.TableFigure;

public class RemovedERTableEditPart extends RemovedNodeElementEditPart
		implements IResizable {

	private Font titleFont;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		ERDiagram diagram = this.getDiagram();
		Settings settings = diagram.getDiagramContents().getSettings();

		TableFigure figure = new TableFigure(settings);

		this.changeFont(figure);

		return figure;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refreshVisuals() {
		try {
			TableFigure figure = (TableFigure) this.getFigure();

			RemovedERTable removedERTable = (RemovedERTable) this.getModel();
			ERTable table = (ERTable) removedERTable.getNodeElement();

			figure.create(null);

			ERDiagram diagram = this.getDiagram();

			int viewMode = diagram.getDiagramContents().getSettings()
					.getViewMode();

			if (viewMode == Settings.VIEW_MODE_PHYSICAL) {
				figure.setName(diagram.filter(table.getPhysicalName()));

			} else if (viewMode == Settings.VIEW_MODE_LOGICAL) {
				figure.setName(diagram.filter(table.getLogicalName()));

			} else {
				figure.setName(diagram.filter(table.getLogicalName()) + "/"
						+ diagram.filter(table.getPhysicalName()));
			}

			this.refreshTableColumns(figure);

			super.refreshVisuals();

		} catch (Exception e) {
			Activator.showExceptionDialog(e);
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
	}

	private Font changeFont(TableFigure tableFigure) {
		Font font = super.changeFont(tableFigure);

		FontData fonDatat = font.getFontData()[0];

		this.titleFont = new Font(Display.getCurrent(), fonDatat.getName(),
				fonDatat.getHeight(), SWT.BOLD);

		tableFigure.setFont(font, this.titleFont);

		return font;
	}

	private void refreshTableColumns(TableFigure tableFigure) {
		RemovedERTable removedERTable = (RemovedERTable) this.getModel();
		ERTable table = (ERTable) removedERTable.getNodeElement();

		ERDiagram diagram = this.getDiagram();

		tableFigure.clearColumns();

		TableViewEditPart.showRemovedColumns(diagram, tableFigure, table
				.getColumns(), false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFigure getContentPane() {
		TableFigure figure = (TableFigure) super.getContentPane();

		return figure.getColumns();
	}
}
