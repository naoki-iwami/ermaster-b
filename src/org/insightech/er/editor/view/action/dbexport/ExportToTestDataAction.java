package org.insightech.er.editor.view.action.dbexport;

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
import org.insightech.er.editor.view.dialog.dbexport.ExportToTestDataDialog;

public class ExportToTestDataAction extends AbstractBaseAction {

	public static final String ID = ExportToTestDataAction.class.getName();

	public ExportToTestDataAction(ERDiagramEditor editor) {
		super(ID, ResourceString
				.getResourceString("action.title.export.test.data"), editor);
		this.setImageDescriptor(Activator
				.getImageDescriptor(ImageKey.EXPORT_TO_TEST_DATA));
	}

	@Override
	public void execute(Event event) throws Exception {
		ERDiagram diagram = this.getDiagram();

		ExportToTestDataDialog dialog = new ExportToTestDataDialog(PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getShell(), this
				.getEditorPart(), diagram, diagram.getDiagramContents()
				.getTestDataList(), -1);

		dialog.open();
		this.refreshProject();

		if (dialog.getExportTestDataSetting() != null
				&& !diagram.getDiagramContents().getSettings()
						.getExportSetting().getExportTestDataSetting().equals(
								dialog.getExportTestDataSetting())) {
			Settings newSettings = (Settings) diagram.getDiagramContents()
					.getSettings().clone();
			newSettings.getExportSetting().setExportTestDataSetting(
					dialog.getExportTestDataSetting());

			ChangeSettingsCommand command = new ChangeSettingsCommand(diagram,
					newSettings);
			this.execute(command);
		}
	}

}
