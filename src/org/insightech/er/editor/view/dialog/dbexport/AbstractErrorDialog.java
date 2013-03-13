package org.insightech.er.editor.view.dialog.dbexport;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.ResourceString;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.util.Format;

public abstract class AbstractErrorDialog extends Dialog {

	protected Text textArea;

	public AbstractErrorDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		this.getShell().setText(
				ResourceString.getResourceString(this.getTitle()));

		Composite composite = (Composite) super.createDialogArea(parent);

		this.textArea = CompositeFactory.createTextArea(null, composite, this
				.getMessage(), 400, 200, 1, false, false);

		composite.setLayout(new GridLayout());

		this.textArea.setText(Format.null2blank(this.getData()));

		return composite;
	}

	protected abstract String getData();

	protected String getMessage() {
		return "dialog.message.export.ddl.error";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		this.createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		setReturnCode(buttonId);
		close();

		super.buttonPressed(buttonId);
	}

	protected abstract String getTitle();
}
