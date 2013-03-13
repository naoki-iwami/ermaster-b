package org.insightech.er.editor.model;

import org.eclipse.gef.EditPart;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;

public class ERModelUtil {
	
	public static ERDiagram getDiagram(EditPart editPart) {
		Object model = editPart.getModel();
		if (model instanceof ERModel) {
			return ((ERModel) model).getDiagram();
			
		}
		return (ERDiagram) model;
	}

}
