package org.insightech.er.editor.controller.editpart.outline.ermodel;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.controller.editpart.outline.AbstractOutlineEditPart;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModelSet;

public class ERModelSetOutlineEditPart extends AbstractOutlineEditPart {

	@Override
	protected List getModelChildren() {
		ERModelSet modelSet = (ERModelSet) this.getModel();

		List<ERModel> list = new ArrayList<ERModel>();
		for (ERModel table : modelSet) {
			list.add(table);
		}
		Collections.sort(list, new Comparator<ERModel>() {
			@Override
			public int compare(ERModel o1, ERModel o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

//		if (this.getDiagram().getDiagramContents().getSettings()
//				.getViewOrderBy() == Settings.VIEW_MODE_LOGICAL) {
//			Collections.sort(list, TableView.LOGICAL_NAME_COMPARATOR);
//
//		} else {
//			Collections.sort(list, TableView.PHYSICAL_NAME_COMPARATOR);
//
//		}

		return list;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ERModelSet.PROPERTY_CHANGE_MODEL_SET)) {
			refresh();
		}
	}

	@Override
	protected void refreshOutlineVisuals() {
		this.setWidgetText(ResourceString.getResourceString("label.ermodel")
				+ " (" + this.getModelChildren().size() + ")");
		this.setWidgetImage(Activator.getImage(ImageKey.DICTIONARY));
	}

}
