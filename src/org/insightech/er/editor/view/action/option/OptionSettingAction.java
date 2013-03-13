package org.insightech.er.editor.view.action.option;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.common.ChangeSettingsCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.action.AbstractBaseAction;
import org.insightech.er.editor.view.dialog.option.OptionSettingDialog;

public class OptionSettingAction extends AbstractBaseAction {

	public static final String ID = OptionSettingAction.class.getName();

	public OptionSettingAction(ERDiagramEditor editor) {
		super(ID, ResourceString.getResourceString("action.title.option"),
				editor);
		this.setImageDescriptor(Activator.getImageDescriptor(ImageKey.OPTION));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Event event) {
		ERDiagram diagram = this.getDiagram();

		Settings settings = (Settings) diagram.getDiagramContents()
				.getSettings().clone();

		OptionSettingDialog dialog = new OptionSettingDialog(PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getShell(),
				settings, diagram);

		if (dialog.open() == IDialogConstants.OK_ID) {
			ChangeSettingsCommand command = new ChangeSettingsCommand(diagram,
					settings);

			this.execute(command);
		}
	}

}
