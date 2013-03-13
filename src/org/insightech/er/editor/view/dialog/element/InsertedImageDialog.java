package org.insightech.er.editor.view.dialog.element;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.SpinnerWithScale;
import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImage;

public class InsertedImageDialog extends AbstractDialog {

	private SpinnerWithScale hueSpinner;

	private SpinnerWithScale saturationSpinner;

	private SpinnerWithScale brightnessSpinner;

	private SpinnerWithScale alphaSpinner;

	private Button fixAspectRatioCheckbox;

	private InsertedImage insertedImage;

	private InsertedImage newInsertedImage;

	public InsertedImageDialog(Shell parentShell, InsertedImage insertedImage) {
		super(parentShell, 4);

		this.insertedImage = insertedImage;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite composite) {
		this.hueSpinner = CompositeFactory.createSpinnerWithScale(this,
				composite, "label.image.hue", "", 0, 360);
		// this.hueScale.setPageIncrement(10);

		this.saturationSpinner = CompositeFactory.createSpinnerWithScale(this,
				composite, "label.image.saturation", -100, 100);

		this.brightnessSpinner = CompositeFactory.createSpinnerWithScale(this,
				composite, "label.image.brightness", -100, 100);

		this.alphaSpinner = CompositeFactory.createSpinnerWithScale(this,
				composite, "label.image.alpha", 0, 255);

		this.fixAspectRatioCheckbox = CompositeFactory.createCheckbox(this,
				composite, "label.image.fix.aspect.ratio", 3);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getErrorMessage() {
		this.insertedImage.setHue(this.hueSpinner.getSelection());
		this.insertedImage.setSaturation(this.saturationSpinner.getSelection());
		this.insertedImage.setBrightness(this.brightnessSpinner.getSelection());
		this.insertedImage.setAlpha(this.alphaSpinner.getSelection());

		this.insertedImage.setFixAspectRatio(this.fixAspectRatioCheckbox
				.getSelection());

		this.insertedImage.setDirty();

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void perfomeOK() {
		this.newInsertedImage = new InsertedImage();
		this.newInsertedImage.setHue(this.hueSpinner.getSelection());
		this.newInsertedImage.setSaturation(this.saturationSpinner
				.getSelection());
		this.newInsertedImage.setBrightness(this.brightnessSpinner
				.getSelection());
		this.newInsertedImage.setAlpha(this.alphaSpinner.getSelection());
		this.newInsertedImage.setFixAspectRatio(this.fixAspectRatioCheckbox
				.getSelection());
	}

	@Override
	protected String getTitle() {
		return "dialog.title.image.information";
	}

	@Override
	protected void setData() {
		this.hueSpinner.setSelection(this.insertedImage.getHue());
		this.saturationSpinner.setSelection(this.insertedImage.getSaturation());
		this.brightnessSpinner.setSelection(this.insertedImage.getBrightness());
		this.alphaSpinner.setSelection(this.insertedImage.getAlpha());
		this.fixAspectRatioCheckbox.setSelection(this.insertedImage
				.isFixAspectRatio());
	}

	public InsertedImage getNewInsertedImage() {
		return newInsertedImage;
	}

}
