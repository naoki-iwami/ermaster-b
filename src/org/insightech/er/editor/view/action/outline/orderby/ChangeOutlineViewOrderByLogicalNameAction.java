package org.insightech.er.editor.view.action.outline.orderby;

import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.controller.command.common.ChangeOutlineViewOrderByCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.action.outline.AbstractOutlineBaseAction;

public class ChangeOutlineViewOrderByLogicalNameAction extends
		AbstractOutlineBaseAction {

	public static final String ID = ChangeOutlineViewOrderByLogicalNameAction.class
			.getName();

	public ChangeOutlineViewOrderByLogicalNameAction(TreeViewer treeViewer) {
		super(ID, null, IAction.AS_RADIO_BUTTON, treeViewer);
		this.setText(ResourceString.getResourceString("label.logical.name"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Event event) {
		ERDiagram diagram = this.getDiagram();

		ChangeOutlineViewOrderByCommand command = new ChangeOutlineViewOrderByCommand(
				diagram, Settings.VIEW_MODE_LOGICAL);

		this.execute(command);
	}

}
