package org.insightech.er.editor.view.action.category;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.category.ChangeFreeLayoutCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.action.AbstractBaseAction;

public class ChangeFreeLayoutAction extends AbstractBaseAction {

	public static final String ID = ChangeFreeLayoutAction.class.getName();

	public ChangeFreeLayoutAction(ERDiagramEditor editor) {
		super(ID, null, IAction.AS_CHECK_BOX, editor);
		this.setText(ResourceString
				.getResourceString("action.title.category.free.layout"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Event event) {
		ERDiagram diagram = this.getDiagram();

		ChangeFreeLayoutCommand command = new ChangeFreeLayoutCommand(diagram,
				this.isChecked());

		this.execute(command);
	}
}
