package org.insightech.er.editor.view.dialog.dbexport;

import java.util.List;

import org.eclipse.swt.widgets.Shell;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;

public class ExportErrorDialog extends AbstractErrorDialog {

	private List<ValidateResult> errorList;

	public ExportErrorDialog(Shell parentShell, List<ValidateResult> errorList) {
		super(parentShell);

		this.errorList = errorList;
	}

	@Override
	protected String getMessage() {
		return "dialog.message.export.ddl.error.no.continue";
	}

	@Override
	protected String getData() {
		StringBuilder text = new StringBuilder();

		for (ValidateResult errorMessage : this.errorList) {
			text.append(errorMessage.getMessage());
			text.append("\r\n");
		}

		return text.toString();
	}

	@Override
	protected String getTitle() {
		return "dialog.title.export.db";
	}

}
