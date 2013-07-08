package org.insightech.er.editor.view.action.option.notation;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.common.notation.ChangeTitleFontSizeCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.action.AbstractBaseAction;

public class ChangeTitleFontSizeAction extends AbstractBaseAction {

	public static final String ID = ChangeTitleFontSizeAction.class.getName();

	public ChangeTitleFontSizeAction(ERDiagramEditor editor) {
		super(ID, null, IAction.AS_CHECK_BOX, editor);
		this.setText(ResourceString.getResourceString("action.title.display.titleFontLarge"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Event event) {
		ERDiagram diagram = this.getDiagram();

		ChangeTitleFontSizeCommand command = new ChangeTitleFontSizeCommand(diagram, this
				.isChecked());

		this.execute(command);
	}
}
