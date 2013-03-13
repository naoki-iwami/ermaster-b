package org.insightech.er.editor.view.action.ermodel;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.ermodel.AddERModelCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.action.AbstractBaseAction;

public class ERModelAddAction extends AbstractBaseAction {

	public static final String ID = ERModelAddAction.class.getName();

	public ERModelAddAction(ERDiagramEditor editor) {
		super(ID, ResourceString
				.getResourceString("action.title.ermodel.add"), editor);
	}

	@Override
	public void execute(Event event) throws Exception {
		ERDiagram diagram = this.getDiagram();

		Settings settings = (Settings) diagram.getDiagramContents()
				.getSettings().clone();

		InputDialog dialog = new InputDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				"ダイアグラム作成", "ダイアグラム名を入力して下さい。", "", null);
		if (dialog.open() == IDialogConstants.OK_ID) {
			AddERModelCommand command = new AddERModelCommand(diagram, dialog.getValue());
			this.execute(command);
		}
		
//		CategoryManageDialog dialog = new CategoryManageDialog(PlatformUI
//				.getWorkbench().getActiveWorkbenchWindow().getShell(),
//				settings, diagram);
//
//		if (dialog.open() == IDialogConstants.OK_ID) {
//			ChangeSettingsCommand command = new ChangeSettingsCommand(diagram,
//					settings);
//			this.execute(command);
//		}
	}

}
