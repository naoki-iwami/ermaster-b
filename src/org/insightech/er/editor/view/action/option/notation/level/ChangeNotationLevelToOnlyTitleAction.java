package org.insightech.er.editor.view.action.option.notation.level;

import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.model.settings.Settings;

public class ChangeNotationLevelToOnlyTitleAction extends
		AbstractChangeNotationLevelAction {

	public static final String ID = ChangeNotationLevelToOnlyTitleAction.class
			.getName();

	public ChangeNotationLevelToOnlyTitleAction(ERDiagramEditor editor) {
		super(ID, editor);
	}

	@Override
	protected int getLevel() {
		return Settings.NOTATION_LEVLE_TITLE;
	}

}
