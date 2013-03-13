package org.insightech.er.editor.view.action.edit;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.actions.SelectAllAction;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;

public class SelectAllContentsAction extends SelectAllAction {

	private IWorkbenchPart part;

	public SelectAllContentsAction(IWorkbenchPart part) {
		super(part);
		this.part = part;
		this.setText(ResourceString
				.getResourceString("action.title.select.all"));

		this.setActionDefinitionId("org.eclipse.ui.edit.selectAll");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		GraphicalViewer viewer = (GraphicalViewer) part
				.getAdapter(GraphicalViewer.class);

		if (viewer != null) {
			List<NodeElementEditPart> children = new ArrayList<NodeElementEditPart>();

			for (Object child : viewer.getContents().getChildren()) {
				if (child instanceof NodeElementEditPart) {
					NodeElementEditPart editPart = (NodeElementEditPart) child;
					if (editPart.getFigure().isVisible()) {
						children.add(editPart);
					}
				}
			}

			viewer.setSelection(new StructuredSelection(children));
		}
	}
}
