package org.insightech.er.editor.view;

import org.eclipse.core.resources.IMarker;
import org.eclipse.gef.EditPart;
import org.eclipse.ui.ide.IGotoMarker;
import org.insightech.er.editor.ERDiagramEditor;

public class ERDiagramGotoMarker implements IGotoMarker {

	private ERDiagramEditor editor;

	public ERDiagramGotoMarker(ERDiagramEditor editor) {
		this.editor = editor;
	}

	public void gotoMarker(IMarker marker) {
		focus(this.editor.getMarkedObject(marker));
	}

	private void focus(Object object) {
		EditPart editPart = (EditPart) this.editor.getGraphicalViewer()
				.getEditPartRegistry().get(object);

		if (editPart != null) {
			this.editor.getGraphicalViewer().select(editPart);
			this.editor.getGraphicalViewer().reveal(editPart);
		}
	}
}
