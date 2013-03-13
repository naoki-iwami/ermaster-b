package org.insightech.er.editor.view.action.dbexport;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.RetargetAction;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.Validator;
import org.insightech.er.editor.model.settings.DBSetting;
import org.insightech.er.editor.view.action.AbstractBaseAction;
import org.insightech.er.editor.view.dialog.dbexport.ExportDBSettingDialog;
import org.insightech.er.editor.view.dialog.dbexport.ExportErrorDialog;
import org.insightech.er.editor.view.dialog.dbexport.ExportToDBDialog;

public class ExportToDBAction extends AbstractBaseAction {

	public static final String ID = ExportToDBAction.class.getName();

	private Validator validator;

	public ExportToDBAction(ERDiagramEditor editor) {
		super(ID, ResourceString.getResourceString("action.title.export.db"),
				editor);

		this.validator = new Validator();
	}

	@Override
	public void execute(Event event) {
		ERDiagram diagram = this.getDiagram();

		List<ValidateResult> errorList = validator.validate(diagram);

		if (!errorList.isEmpty()) {
			ExportErrorDialog dialog = new ExportErrorDialog(PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getShell(),
					errorList);
			dialog.open();
			return;
		}

		ExportDBSettingDialog dialog = new ExportDBSettingDialog(PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getShell(), diagram);

		if (dialog.open() == IDialogConstants.OK_ID) {
			String ddl = dialog.getDdl();

			DBSetting dbSetting = dialog.getDbSetting();

			ExportToDBDialog exportToDBDialog = new ExportToDBDialog(PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getShell(),
					diagram, dbSetting, ddl);

			exportToDBDialog.open();
		}
	}

	public static class ExportToDBRetargetAction extends RetargetAction {

		public ExportToDBRetargetAction() {
			super(ID, ResourceString
					.getResourceString("action.title.export.db"));

			this.setImageDescriptor(Activator
					.getImageDescriptor(ImageKey.EXPORT_TO_DB));
			this.setToolTipText(this.getText());
		}

	}
}
