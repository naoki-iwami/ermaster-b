package org.insightech.er.editor.view.action.option.notation.system;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.common.notation.ChangeNotationCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.action.AbstractBaseAction;

public abstract class AbstractChangeNotationAction extends AbstractBaseAction {

	public AbstractChangeNotationAction(String id, String type,
			ERDiagramEditor editor) {
		super(id, null, IAction.AS_RADIO_BUTTON, editor);
		this.setText(ResourceString
				.getResourceString("action.title.change.notation." + type));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Event event) {
		if (!this.isChecked()) {
			return;
		}

		ERDiagram diagram = this.getDiagram();

		ChangeNotationCommand command = new ChangeNotationCommand(diagram, this
				.getNotation());

		this.execute(command);
	}

	protected abstract String getNotation();
}
