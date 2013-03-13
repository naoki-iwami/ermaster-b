package org.insightech.er.editor.view.dialog.dbexport;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;

public class ExportWarningDialog extends ExportErrorDialog {

	public ExportWarningDialog(Shell parentShell, List<ValidateResult> errorList) {
		super(parentShell, errorList);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		this.createButton(parent, IDialogConstants.OK_ID, ResourceString
				.getResourceString("label.button.continue"), true);
		this.createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CLOSE_ID
				|| buttonId == IDialogConstants.CANCEL_ID) {
			setReturnCode(buttonId);
			close();

		} else if (buttonId == IDialogConstants.OK_ID) {
			setReturnCode(buttonId);
			close();
		}

		super.buttonPressed(buttonId);
	}

	@Override
	protected String getTitle() {
		return "dialog.title.export.ddl";
	}
}
