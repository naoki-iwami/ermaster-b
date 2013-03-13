package org.insightech.er.editor.view.dialog.option.tab;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TabFolder;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.ValidatableTabWrapper;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.option.OptionSettingDialog;

public class OptionTabWrapper extends ValidatableTabWrapper {

	private Button autoImeChangeCheck;

	private Button validatePhysicalNameCheck;

	private Button useBezierCurveCheck;

	private Button suspendValidatorCheck;

	private Settings settings;

	private OptionSettingDialog dialog;

	public OptionTabWrapper(OptionSettingDialog dialog, TabFolder parent,
			int style, Settings settings) {
		super(dialog, parent, style, "label.option");

		this.settings = settings;
		this.dialog = dialog;

		this.init();
	}

	@Override
	public void initComposite() {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		this.setLayout(layout);

		this.autoImeChangeCheck = CompositeFactory.createCheckbox(this.dialog,
				this, "label.auto.ime.change");
		this.validatePhysicalNameCheck = CompositeFactory.createCheckbox(
				this.dialog, this, "label.validate.physical.name");
		this.useBezierCurveCheck = CompositeFactory.createCheckbox(this.dialog,
				this, "label.use.bezier.curve");
		this.suspendValidatorCheck = CompositeFactory.createCheckbox(
				this.dialog, this, "label.suspend.validator");
	}

	@Override
	public void setData() {
		this.autoImeChangeCheck.setSelection(this.settings.isAutoImeChange());
		this.validatePhysicalNameCheck.setSelection(this.settings
				.isValidatePhysicalName());
		this.useBezierCurveCheck.setSelection(this.settings.isUseBezierCurve());
		this.suspendValidatorCheck.setSelection(this.settings
				.isSuspendValidator());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validatePage() throws InputException {
		this.settings.setAutoImeChange(this.autoImeChangeCheck.getSelection());
		this.settings.setValidatePhysicalName(this.validatePhysicalNameCheck
				.getSelection());
		this.settings
				.setUseBezierCurve(this.useBezierCurveCheck.getSelection());
		this.settings.setSuspendValidator(this.suspendValidatorCheck
				.getSelection());
	}

	@Override
	public void setInitFocus() {
		this.autoImeChangeCheck.setFocus();
	}

	@Override
	public void perfomeOK() {
	}
}
