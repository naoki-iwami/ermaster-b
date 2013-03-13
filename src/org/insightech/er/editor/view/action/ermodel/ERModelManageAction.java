package org.insightech.er.editor.view.action.ermodel;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.common.ChangeSettingsCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.action.AbstractBaseAction;
import org.insightech.er.editor.view.action.category.CategoryManageAction;
import org.insightech.er.editor.view.dialog.category.CategoryManageDialog;

public class ERModelManageAction extends AbstractBaseAction {

	public static final String ID = ERModelManageAction.class.getName();

	public ERModelManageAction(ERDiagramEditor editor) {
		super(ID, ResourceString
				.getResourceString("action.title.ermodel.manage"), editor);
	}

	@Override
	public void execute(Event event) throws Exception {
		ERDiagram diagram = this.getDiagram();

		Settings settings = (Settings) diagram.getDiagramContents()
				.getSettings().clone();

		CategoryManageDialog dialog = new CategoryManageDialog(PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getShell(),
				settings, diagram);

		if (dialog.open() == IDialogConstants.OK_ID) {
			ChangeSettingsCommand command = new ChangeSettingsCommand(diagram,
					settings);
			this.execute(command);
		}
	}

}
