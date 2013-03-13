package org.insightech.er.editor.controller.editpart.element.node;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.swt.graphics.Color;
import org.insightech.er.Resources;
import org.insightech.er.editor.controller.editpolicy.ERDiagramLayoutEditPolicy;
import org.insightech.er.editor.model.ViewableModel;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.view.drag_drop.ERDiagramTransferDragSourceListener;


public class ERModelEditPart extends NodeElementEditPart {

	@Override
	public void doPropertyChange(PropertyChangeEvent event) {
		
		if (event.getPropertyName().equals(ERModel.PROPERTY_CHANGE_VTABLES)) {
			// テーブル配置変更
			this.refreshChildren();
			this.refresh();
		} else if (event.getPropertyName().equals(ConnectionElement.PROPERTY_CHANGE_CONNECTION)) {
			System.out.println("aa");
		} else if (event.getPropertyName().equals(ViewableModel.PROPERTY_CHANGE_COLOR)) {
			this.refreshVisuals();
		}
		

	}
	
	@Override
	public void refresh() {
		super.refresh();
		
		// ここのコメントアウト外すとStackOverflowになる。別のところでリフレッシュする必要あり
//		Map<NodeElement, EditPart> part = getModelToEditPart();
//		for (Entry<NodeElement, EditPart> entry : part.entrySet()) {
//			if (entry.getKey() instanceof ERVirtualTable) {
//				entry.getValue().addNotify();
//			}
//		}
		
	}
	
	
	@Override
	protected List getModelChildren() {
		List<Object> modelChildren = new ArrayList<Object>();
		ERModel model = (ERModel) this.getModel();
		modelChildren.addAll(model.getGroups());
		modelChildren.addAll(model.getTables());
		modelChildren.addAll(model.getNotes());
		return modelChildren;
	}

	
//	@Override
//	protected Rectangle getRectangle() {
//		// TODO Auto-generated method stub
//		return super.getRectangle();
//	}
	
	@Override
	protected IFigure createFigure() {
//		ERModel ermodel = (ERModel) this.getModel();
//		ERModelFigure figure = new ERModelFigure(ermodel.getName());
//		return figure;

		FreeformLayer layer = new FreeformLayer();
		layer.setLayoutManager(new FreeformLayout());
		return layer;
	}

	@Override
	protected void createEditPolicies() {
		this.installEditPolicy(EditPolicy.LAYOUT_ROLE, new ERDiagramLayoutEditPolicy());
	}
	
	@Override
	public void refreshVisuals() {
		ERModel element = (ERModel) this.getModel();

		int[] color = element.getColor();

		if (color != null) {
			Color bgColor = Resources.getColor(color);
			this.getViewer().getControl().setBackground(bgColor);
		}

		for (Object child : this.getChildren()) {
			if (child instanceof NodeElementEditPart) {
				NodeElementEditPart part = (NodeElementEditPart) child;
				part.refreshVisuals();
			}
		}
	}
	
//	private void internalRefreshTable(ERTable table) {
//		for (Entry<NodeElement, EditPart> entry : getModelToEditPart().entrySet()) {
//			if (entry.getKey().equals(table)) {
//				// テーブルの更新
//				entry.getValue().refresh();
//			}
//		}
//		
//		
//	}

	public void refreshRelations() {
		for (Object child : this.getChildren()) {
			if (child instanceof NodeElementEditPart) {
				NodeElementEditPart part = (NodeElementEditPart) child;
				part.refreshConnections();
			}
		}
	}

	private Map<NodeElement, EditPart> getModelToEditPart() {
		Map<NodeElement, EditPart> modelToEditPart = new HashMap<NodeElement, EditPart>();
		List children = getChildren();

		for (int i = 0; i < children.size(); i++) {
			EditPart editPart = (EditPart) children.get(i);
			modelToEditPart.put((NodeElement) editPart.getModel(), editPart);
		}

		return modelToEditPart;
	}

	@Override
	protected void performRequestOpen() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public EditPart getTargetEditPart(Request request) {
		System.out.println("ss");
		if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_PLACE_TABLE.equals(request.getType())) {
			return this;
		}
		return super.getTargetEditPart(request);
	}
	
}