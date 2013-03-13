package org.insightech.er.editor.controller.editpart.element.node.removed;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.insightech.er.Resources;
import org.insightech.er.editor.controller.editpart.DeleteableEditPart;
import org.insightech.er.editor.controller.editpart.element.AbstractModelEditPart;
import org.insightech.er.editor.model.ViewableModel;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.tracking.RemovedNodeElement;

public abstract class RemovedNodeElementEditPart extends AbstractModelEditPart
		implements NodeEditPart, DeleteableEditPart {

	private Font font;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deactivate() {
		this.disposeFont();

		super.deactivate();
	}

	protected void disposeFont() {
		if (this.font != null) {
			this.font.dispose();
		}
	}

	@Override
	public void doPropertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(
				NodeElement.PROPERTY_CHANGE_RECTANGLE)) {
			refreshVisuals();

		} else if (event.getPropertyName().equals(
				ViewableModel.PROPERTY_CHANGE_COLOR)) {
			refreshVisuals();

		} else if (event.getPropertyName().equals(
				ViewableModel.PROPERTY_CHANGE_FONT)) {
			this.changeFont(this.figure);
			refreshVisuals();

		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
	}

	protected void setVisible() {
		Category category = this.getCurrentCategory();

		if (category != null) {
			this.figure.setVisible(false);
		} else {
			this.figure.setVisible(true);
		}
	}

	protected Font changeFont(IFigure figure) {
		this.disposeFont();

		RemovedNodeElement removedNodeElement = (RemovedNodeElement) this
				.getModel();

		String fontName = removedNodeElement.getFontName();
		int fontSize = removedNodeElement.getFontSize();

		if (fontName == null) {
			FontData fontData = Display.getCurrent().getSystemFont()
					.getFontData()[0];
			fontName = fontData.getName();
		}
		if (fontSize <= 0) {
			fontSize = ViewableModel.DEFAULT_FONT_SIZE;
		}

		this.font = new Font(Display.getCurrent(), fontName, fontSize,
				SWT.NORMAL);

		figure.setFont(this.font);

		return font;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refreshVisuals() {
		this.setVisible();

		Rectangle rectangle = this.getRectangle();

		GraphicalEditPart parent = (GraphicalEditPart) this.getParent();

		IFigure figure = this.getFigure();

		figure.setBackgroundColor(Resources.REMOVED_COLOR);

		parent.setLayoutConstraint(this, figure, rectangle);
	}

	protected Rectangle getRectangle() {
		RemovedNodeElement removedNodeElement = (RemovedNodeElement) this
				.getModel();

		NodeElement nodeElement = removedNodeElement.getNodeElement();

		Point point = new Point(nodeElement.getX(), nodeElement.getY());

		Dimension dimension = new Dimension(nodeElement.getWidth(), nodeElement
				.getHeight());

		Dimension minimumSize = this.figure.getMinimumSize();
		if (dimension.width != -1 && dimension.width < minimumSize.width) {
			dimension.width = minimumSize.width;
		}
		if (dimension.height != -1 && dimension.height < minimumSize.height) {
			dimension.height = minimumSize.height;
		}

		return new Rectangle(point, dimension);
	}

	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart arg0) {
		return new ChopboxAnchor(this.getFigure());
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request arg0) {
		return new ChopboxAnchor(this.getFigure());
	}

	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart arg0) {
		return new ChopboxAnchor(this.getFigure());
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request arg0) {
		return new ChopboxAnchor(this.getFigure());
	}

	public void changeSettings(Settings settings) {
		this.refresh();
	}

	public boolean isDeleteable() {
		return false;
	}

}
