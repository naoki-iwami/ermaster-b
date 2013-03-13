package org.insightech.er.editor.view.action.option.notation.system;

import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.model.settings.Settings;

public class ChangeToIENotationAction extends AbstractChangeNotationAction {

	public static final String ID = ChangeToIENotationAction.class.getName();

	public ChangeToIENotationAction(ERDiagramEditor editor) {
		super(ID, "ie", editor);
	}

	@Override
	protected String getNotation() {
		return Settings.NOTATION_IE;
	}

}
