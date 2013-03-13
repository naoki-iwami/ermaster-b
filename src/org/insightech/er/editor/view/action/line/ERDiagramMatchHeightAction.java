package org.insightech.er.editor.view.action.line;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.MatchHeightAction;
import org.eclipse.ui.IWorkbenchPart;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.editor.controller.editpart.element.node.column.NormalColumnEditPart;

public class ERDiagramMatchHeightAction extends MatchHeightAction {

	public ERDiagramMatchHeightAction(IWorkbenchPart part) {
		super(part);
		this.setImageDescriptor(Activator
				.getImageDescriptor(ImageKey.MATCH_HEIGHT));
		this.setDisabledImageDescriptor(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List getSelectedObjects() {
		List objects = new ArrayList(super.getSelectedObjects());
		boolean first = true;

		for (Iterator iter = objects.iterator(); iter.hasNext();) {
			Object object = iter.next();

			if (!(object instanceof EditPart)) {
				iter.remove();
				
			} else {
				EditPart editPart = (EditPart) object;
				if (editPart instanceof NormalColumnEditPart) {
					iter.remove();

				} else {
					if (first) {
						editPart.setSelected(2);
						first = false;
					}
				}
			}
		}

		return objects;
	}

}
