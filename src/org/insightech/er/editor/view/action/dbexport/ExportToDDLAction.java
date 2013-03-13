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
import org.insightech.er.editor.view.dialog.dbexport.ExportToDDLDialog;

public class ExportToDDLAction extends AbstractBaseAction {

	public static final String ID = ExportToDDLAction.class.getName();

	public ExportToDDLAction(ERDiagramEditor editor) {
		super(ID, ResourceString.getResourceString("action.title.export.ddl"),
				editor);
		this.setImageDescriptor(Activator
				.getImageDescriptor(ImageKey.EXPORT_DDL));
	}

	@Override
	public void execute(Event event) {
		ERDiagram diagram = this.getDiagram();

		ExportToDDLDialog dialog = new ExportToDDLDialog(PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getShell(), diagram,
				this.getEditorPart(), this.getGraphicalViewer());

		dialog.open();

		this.refreshProject();

		if (dialog.getExportSetting() != null
				&& !diagram.getDiagramContents().getSettings()
						.getExportSetting().equals(dialog.getExportSetting())) {
			Settings newSettings = (Settings) diagram.getDiagramContents()
					.getSettings().clone();
			newSettings.setExportSetting(dialog.getExportSetting());

			ChangeSettingsCommand command = new ChangeSettingsCommand(diagram,
					newSettings);
			this.execute(command);
		}

	}

}
