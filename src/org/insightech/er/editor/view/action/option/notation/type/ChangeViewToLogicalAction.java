package org.insightech.er.editor.view.action.option.notation.type;

import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.model.settings.Settings;

public class ChangeViewToLogicalAction extends AbstractChangeViewAction {

	public static final String ID = ChangeViewToLogicalAction.class.getName();

	public ChangeViewToLogicalAction(ERDiagramEditor editor) {
		super(ID, "logical", editor);
	}

	@Override
	protected int getViewMode() {
		return Settings.VIEW_MODE_LOGICAL;
	}
}
