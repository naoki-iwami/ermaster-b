package org.insightech.er.editor.view.action.option.notation.system;

import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.model.settings.Settings;

public class ChangeToIDEF1XNotationAction extends AbstractChangeNotationAction {

	public static final String ID = ChangeToIDEF1XNotationAction.class
			.getName();

	public ChangeToIDEF1XNotationAction(ERDiagramEditor editor) {
		super(ID, "idef1x", editor);
	}

	@Override
	protected String getNotation() {
		return Settings.NOTATION_IDEF1X;
	}

}
