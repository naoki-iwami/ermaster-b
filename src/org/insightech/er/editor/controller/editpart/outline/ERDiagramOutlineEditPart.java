package org.insightech.er.editor.controller.editpart.outline;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;

public class ERDiagramOutlineEditPart extends AbstractOutlineEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List getModelChildren() {
		List<AbstractModel> modelChildren = new ArrayList<AbstractModel>();
		ERDiagram diagram = (ERDiagram) this.getModel();
		DiagramContents diagramContents = diagram.getDiagramContents();

		modelChildren.add(diagramContents.getModelSet());
//		modelChildren.add(diagramContents.getContents().getErmodelSet());
//		modelChildren.add(diagramContents.getDictionary());
		modelChildren.add(diagramContents.getGroups());
		modelChildren.add(diagramContents.getContents().getTableSet());
		modelChildren.add(diagramContents.getContents().getViewSet());
		modelChildren.add(diagramContents.getTriggerSet());
		modelChildren.add(diagramContents.getSequenceSet());
//		modelChildren.add(diagramContents.getIndexSet());
		modelChildren.add(diagramContents.getTablespaceSet());

		return modelChildren;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ERDiagram.PROPERTY_CHANGE_ALL)
				|| evt.getPropertyName().equals(
						ERDiagram.PROPERTY_CHANGE_SETTINGS)) {
			refresh();
		}
//		if (evt.getPropertyName().equals(ERModelSet.PROPERTY_CHANGE_MODEL_SET)) {
//			Object newValue = evt.getNewValue();
//			if (newValue != null) {
//				
//				Set<Entry<NodeElement, EditPart>> entrySet = getModelToEditPart().entrySet();
//				for (Entry<NodeElement, EditPart> entry : entrySet) {
//					if (entry.getKey().equals(newValue)) {
//						// エレメントの更新
//						entry.getValue().refresh();
//					}
//				}
//
//			} else {
//				refresh();
//			}
//		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refreshOutlineVisuals() {
		for (Object child : this.getChildren()) {
			EditPart part = (EditPart) child;
			part.refresh();
		}
	}
	
	@Override
	public EditPart getTargetEditPart(Request request) {
		if (request instanceof ChangeBoundsRequest) {
			ChangeBoundsRequest breq = (ChangeBoundsRequest) request;
			
		}
		// TODO Auto-generated method stub
		return super.getTargetEditPart(request);
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

}
