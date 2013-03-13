package org.insightech.er.common.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.util.Check;

public abstract class AbstractDialog extends Dialog {

	private CLabel errorMessageText = null;

	private int numColumns;

	private boolean enabledOkButton = true;

	protected boolean initialized = false;
	
	protected AbstractDialog(Shell parentShell) {
		this(parentShell, 1);
	}

	protected AbstractDialog(Shell parentShell, int numColumns) {
		super(parentShell);

		this.numColumns = numColumns;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		this.getShell().setText(
				ResourceString.getResourceString(this.getTitle()));

		Composite composite = (Composite) super.createDialogArea(parent);

		try {
			GridLayout layout = new GridLayout();
			layout.numColumns = this.numColumns;
			this.initLayout(layout);

			composite.setLayout(layout);
			composite.setLayoutData(this.createLayoutData());

			this.createErrorComposite(composite);

			this.initialize(composite);

			this.setData();

			this.initialized = true;

		} catch (Exception e) {
			Activator.showExceptionDialog(e);
		}

		return composite;
	}

	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		
		this.addListener();
		this.validate();

		return control;
	}

	protected void initLayout(GridLayout layout) {
	}

	protected int getNumColumns() {
		return this.numColumns;
	}

	protected int getErrorLine() {
		return 1;
	}

	protected Object createLayoutData() {
		return new GridData(GridData.FILL_BOTH);
	}

	protected void createErrorComposite(Composite parent) {
		this.errorMessageText = new CLabel(parent, SWT.NONE);
		this.errorMessageText.setText("");

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.heightHint = 30 * this.getErrorLine();
		gridData.horizontalSpan = this.numColumns;

		this.errorMessageText.setLayoutData(gridData);
	}

	protected Integer getIntegerValue(Text text) {
		String value = text.getText();
		if (Check.isEmpty(value)) {
			return null;
		}

		try {
			return Integer.valueOf(value.trim());

		} catch (NumberFormatException e) {
			return null;
		}
	}

	final public boolean validate() {
		if (!this.initialized) {
			return true;
		}
		
		Button okButton = this.getButton(IDialogConstants.OK_ID);
		if (okButton != null) {
			okButton.setEnabled(false);
		}

		String errorMessage = this.getErrorMessage();

		if (errorMessage != null) {
			this.setMessage(ResourceString.getResourceString(errorMessage));
			return false;
		}

		if (okButton != null && this.enabledOkButton) {
			okButton.setEnabled(true);
		}

		this.setMessage(null);

		return true;
	}

	protected void setMessage(String errorMessage) {
		if (this.errorMessageText != null) {
			if (errorMessage == null) {
				this.errorMessageText.setImage(null);
				this.errorMessageText.setText("");

			} else {
				Image errorIcon = Activator.getImage(ImageKey.ERROR);
				this.errorMessageText.setImage(errorIcon);
				this.errorMessageText.setText(errorMessage);
			}
		}
	}

	abstract protected void initialize(Composite composite);

	abstract protected void setData();

	protected void addListener() {
	}

	protected static boolean isBlank(Text text) {
		if (text.getText().trim().length() == 0) {
			return true;
		}

		return false;
	}

	protected static boolean isBlank(Combo combo) {
		if (combo.getText().trim().length() == 0) {
			return true;
		}

		return false;
	}

	protected void enabledButton(boolean enabled) {
		this.enabledOkButton = enabled;

		Button button1 = this.getButton(IDialogConstants.OK_ID);
		if (button1 != null) {
			button1.setEnabled(enabled);
		}

		Button button2 = this.getButton(IDialogConstants.CANCEL_ID);
		if (button2 != null) {
			button2.setEnabled(enabled);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CLOSE_ID
				|| buttonId == IDialogConstants.CANCEL_ID
				|| buttonId == IDialogConstants.BACK_ID) {
			this.setReturnCode(buttonId);
			this.close();

		} else if (buttonId == IDialogConstants.OK_ID) {
			try {
				if (!validate()) {
					return;
				}

				this.perfomeOK();
				setReturnCode(buttonId);
				close();

			} catch (InputException e) {
				this.setMessage(ResourceString
						.getResourceString(e.getMessage()));
				return;

			} catch (Exception e) {
				Activator.showExceptionDialog(e);
			}
		}

		super.buttonPressed(buttonId);
	}

	abstract protected String getErrorMessage();

	abstract protected void perfomeOK() throws InputException;

	abstract protected String getTitle();

	protected Button createCheckbox(Composite composite, String title) {
		return CompositeFactory.createCheckbox(this, composite, title, this
				.getNumColumns());
	}

}
