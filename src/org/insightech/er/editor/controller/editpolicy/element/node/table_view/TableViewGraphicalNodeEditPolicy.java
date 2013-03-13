package org.insightech.er.editor.controller.editpolicy.element.node.table_view;

import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.SimpleRaisedBorder;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.swt.widgets.Display;
import org.insightech.er.editor.controller.editpart.element.node.TableViewEditPart;
import org.insightech.er.editor.controller.editpolicy.element.node.NodeElementGraphicalNodeEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ERModelUtil;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public class TableViewGraphicalNodeEditPolicy extends
		NodeElementGraphicalNodeEditPolicy {

	@Override
	public void showTargetFeedback(Request request) {
		ERDiagram diagram = ERModelUtil.getDiagram(this.getHost().getRoot().getContents());

		if (diagram.isTooltip()) {
			ZoomManager zoomManager = ((ScalableFreeformRootEditPart) this
					.getHost().getRoot()).getZoomManager();
			double zoom = zoomManager.getZoom();

			TableView tableView = (TableView) this.getHost().getModel();
			Rectangle tableBounds = this.getHostFigure().getBounds();

			String name = TableViewEditPart.getTableViewName(tableView, diagram);

			Label label = new Label();
			label.setText(name);
			label.setBorder(new SimpleRaisedBorder());
			label.setBackgroundColor(ColorConstants.orange);
			label.setOpaque(true);

			Dimension dim = FigureUtilities.getTextExtents(name, Display
					.getCurrent().getSystemFont());

			label.setBounds(new Rectangle((int) (zoom * (tableBounds.x + 33)),
					(int) (zoom * (tableBounds.y + 5)),
					(int) (dim.width * 1.5), 20));

			this.addFeedback(label);
		}
		super.showTargetFeedback(request);
	}

	@Override
	public void eraseTargetFeedback(Request request) {
		LayerManager manager = (LayerManager) this.getHost().getRoot();
		IFigure layer = manager.getLayer(LayerConstants.PRIMARY_LAYER);
		this.getFeedbackLayer().setBounds(layer.getBounds());

		List list = this.getFeedbackLayer().getChildren();

		for (Iterator iter = list.iterator(); iter.hasNext();) {
			Object obj = iter.next();
			if (obj instanceof Label) {
				iter.remove();
			}
		}
		this.getFeedbackLayer().repaint();

		super.eraseTargetFeedback(request);
	}

}
