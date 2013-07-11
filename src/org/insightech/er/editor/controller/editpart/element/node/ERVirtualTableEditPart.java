package org.insightech.er.editor.controller.editpart.element.node;

import java.beans.PropertyChangeEvent;

public class ERVirtualTableEditPart extends ERTableEditPart {

	@Override
	public void refreshVisuals() {
		// TODO Auto-generated method stub
		super.refreshVisuals();
	}

	@Override
	protected void refreshChildren() {
		// TODO Auto-generated method stub
		super.refreshChildren();
	}

	@Override
	public void doPropertyChange(PropertyChangeEvent event) {

//		if (event.getPropertyName().equals(ViewableModel.PROPERTY_CHANGE_COLOR)) {
//			this.refreshVisuals();
//		}
//		if (event.getPropertyName().equals(TableView.PROPERTY_CHANGE_COLUMNS)) {
//			this.refreshChildren();
////			refreshVisuals();
//		}

		super.doPropertyChange(event);

	}

}
