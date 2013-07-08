package org.insightech.er.editor.controller.editpart.element.node;

import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.insightech.er.Activator;
import org.insightech.er.Resources;
import org.insightech.er.editor.controller.editpart.DeleteableEditPart;
import org.insightech.er.editor.controller.editpart.element.AbstractModelEditPart;
import org.insightech.er.editor.controller.editpart.element.connection.ERDiagramConnectionEditPart;
import org.insightech.er.editor.controller.editpart.element.node.column.ColumnEditPart;
import org.insightech.er.editor.controller.editpolicy.element.node.NodeElementGraphicalNodeEditPolicy;
import org.insightech.er.editor.model.ViewableModel;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.tracking.ChangeTrackingList;
import org.insightech.er.editor.view.figure.connection.ERDiagramConnection;
import org.insightech.er.editor.view.figure.table.TableFigure;
import org.insightech.er.util.Check;

public abstract class NodeElementEditPart extends AbstractModelEditPart
		implements NodeEditPart, DeleteableEditPart {

	private Font font;
	private Font largeFont;

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
		if (event.getPropertyName().equals(NodeElement.PROPERTY_CHANGE_RECTANGLE)) {
			refreshVisuals();

		} else if (event.getPropertyName().equals(ViewableModel.PROPERTY_CHANGE_COLOR)) {
			refreshVisuals();

		} else if (event.getPropertyName().equals(ViewableModel.PROPERTY_CHANGE_FONT)) {
			this.changeFont(this.figure);
			refreshVisuals();
//			if (getNodeModel().needsUpdateOtherModel()) {
//				// 全ビューのリフレッシュ
//				getNodeModel().getDiagram().refreshAllModel(event, getNodeModel());
//			} else {
//				refreshVisuals();
//			}
//

		} else if (event.getPropertyName().equals(NodeElement.PROPERTY_CHANGE_INCOMING)) {
			refreshTargetConnections();

		} else if (event.getPropertyName().equals(NodeElement.PROPERTY_CHANGE_OUTGOING)) {
			refreshSourceConnections();
		}
	}

	public NodeElement getNodeModel() {
		return (NodeElement) super.getModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new NodeElementGraphicalNodeEditPolicy());
	}

	protected void setVisible() {
		NodeElement element = (NodeElement) this.getModel();
		Category category = this.getCurrentCategory();

		if (category != null) {
			this.figure.setVisible(category.isVisible(element, this
					.getDiagram()));

		} else {
			this.figure.setVisible(true);
		}
	}

	protected Font changeFont(IFigure figure) {
		this.disposeFont();

		NodeElement nodeElement = (NodeElement) this.getModel();

		String fontName = nodeElement.getFontName();
		int fontSize = nodeElement.getFontSize();

		if (Check.isEmpty(fontName)) {
			FontData fontData = Display.getCurrent().getSystemFont()
					.getFontData()[0];
			fontName = fontData.getName();
			nodeElement.setFontName(fontName);
		}
		if (fontSize <= 0) {
			fontSize = ViewableModel.DEFAULT_FONT_SIZE;
			nodeElement.setFontSize(fontSize);
		}

		this.font = new Font(Display.getCurrent(), fontName, fontSize, SWT.NORMAL);

		if (getDiagram().getDiagramContents().getSettings().getTitleFontEm() != null) {
			int largeFontSize = getDiagram().getDiagramContents().getSettings().getTitleFontEm().multiply(new BigDecimal(nodeElement.getFontSize())).intValue();
			this.largeFont = new Font(Display.getCurrent(), fontName, largeFontSize, SWT.NORMAL);
		}

		figure.setFont(this.font);
		if (figure instanceof TableFigure) {
			((TableFigure)figure).setLargeFont(this.largeFont);
		}

		return font;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refreshVisuals() {
		NodeElement element = (NodeElement) this.getModel();

		this.setVisible();

		Rectangle rectangle = this.getRectangle();

		GraphicalEditPart parent = (GraphicalEditPart) this.getParent();

		IFigure figure = this.getFigure();

		int[] color = element.getColor();

		if (color != null) {
			ChangeTrackingList changeTrackingList = this.getDiagram()
					.getChangeTrackingList();

			if (changeTrackingList.isCalculated()
					&& (element instanceof Note || element instanceof ERTable)) {
				if (changeTrackingList.isAdded(element)) {
					figure.setBackgroundColor(Resources.ADDED_COLOR);

				} else if (changeTrackingList.getUpdatedNodeElement(element) != null) {
					figure.setBackgroundColor(Resources.UPDATED_COLOR);

				} else {
					figure.setBackgroundColor(ColorConstants.white);
				}

			} else {
				Color bgColor = Resources.getColor(color);
				figure.setBackgroundColor(bgColor);
			}

		}

		parent.setLayoutConstraint(this, figure, rectangle);

	}

	public void refreshConnections() {
		for (Object sourceConnection : this.getSourceConnections()) {
			ConnectionEditPart editPart = (ConnectionEditPart) sourceConnection;
			ConnectionElement connectinoElement = (ConnectionElement) editPart
					.getModel();
			connectinoElement.setParentMove();
		}

		for (Object targetConnection : this.getTargetConnections()) {
			ConnectionEditPart editPart = (ConnectionEditPart) targetConnection;
			ConnectionElement connectinoElement = (ConnectionElement) editPart
					.getModel();

			connectinoElement.setParentMove();
		}
	}

	protected Rectangle getRectangle() {
		NodeElement element = (NodeElement) this.getModel();

		Point point = new Point(element.getX(), element.getY());

		Dimension dimension = new Dimension(element.getWidth(), element
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List getModelSourceConnections() {
		NodeElement element = (NodeElement) this.getModel();
		return element.getOutgoings();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List getModelTargetConnections() {
		NodeElement element = (NodeElement) this.getModel();
		return element.getIncomings();
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

		for (Object object : this.getSourceConnections()) {
			ERDiagramConnectionEditPart editPart = (ERDiagramConnectionEditPart) object;
			ERDiagramConnection connection = (ERDiagramConnection) editPart
					.getFigure();
			connection.setBezier(settings.isUseBezierCurve());

			editPart.refresh();
		}
	}

	public boolean isDeleteable() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSelected(int value) {
		if (value != 0) {
			for (Object editPartObject : this.getViewer()
					.getSelectedEditParts()) {
				if (editPartObject instanceof ColumnEditPart) {
					((ColumnEditPart) editPartObject).setSelected(0);
				}
			}
		}

		super.setSelected(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performRequest(Request request) {
		if (request.getType().equals(RequestConstants.REQ_OPEN)) {
			try {
				performRequestOpen();

			} catch (Exception e) {
				Activator.showExceptionDialog(e);
			}
		}

		super.performRequest(request);
	}

	abstract protected void performRequestOpen();
}
