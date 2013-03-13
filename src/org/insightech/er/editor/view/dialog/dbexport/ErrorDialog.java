package org.insightech.er.editor.view.dialog.dbexport;

import org.eclipse.swt.widgets.Shell;

public class ErrorDialog extends AbstractErrorDialog {

	private String message;

	public ErrorDialog(Shell parentShell, String message) {
		super(parentShell);

		this.message = message;
	}

	@Override
	protected String getData() {
		return this.message;
	}

	@Override
	protected String getTitle() {
		return "dialog.title.error";
	}

}
