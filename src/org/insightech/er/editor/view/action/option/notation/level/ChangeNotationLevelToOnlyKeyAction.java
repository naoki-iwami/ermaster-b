package org.insightech.er.editor.view.action.option.notation.level;

import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.model.settings.Settings;

public class ChangeNotationLevelToOnlyKeyAction extends
		AbstractChangeNotationLevelAction {

	public static final String ID = ChangeNotationLevelToOnlyKeyAction.class
			.getName();

	public ChangeNotationLevelToOnlyKeyAction(ERDiagramEditor editor) {
		super(ID, editor);
	}

	@Override
	protected int getLevel() {
		return Settings.NOTATION_LEVLE_KEY;
	}

}
