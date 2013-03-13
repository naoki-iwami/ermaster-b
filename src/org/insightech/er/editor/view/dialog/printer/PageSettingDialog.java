package org.insightech.er.editor.view.dialog.printer;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.PageSetting;

public class PageSettingDialog extends AbstractDialog {

	private PageSetting pageSetting;

	private Button vButton;

	private Button hButton;

	private Spinner scaleSpinner;

	private Combo sizeCombo;

	private Spinner topMarginSpinner;

	private Spinner rightMarginSpinner;

	private Spinner bottomMarginSpinner;

	private Spinner leftMarginSpinner;

	private ERDiagram diagram;

	public PageSettingDialog(Shell parentShell, ERDiagram diagram) {
		super(parentShell, 1);

		this.pageSetting = diagram.getPageSetting();
		this.diagram = diagram;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createErrorComposite(Composite parent) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite parent) {
		parent.setBackground(ColorConstants.white);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		parent.setLayout(layout);

		this.initDirectionGroup(parent);
		this.initScaleGroup(parent);
		this.initSizeGroup(parent);
	}

	private void initDirectionGroup(Composite parent) {
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		Group directionGroup = new Group(parent, SWT.NONE);
		directionGroup.setLayoutData(gridData);
		directionGroup.setBackground(ColorConstants.white);
		directionGroup.setText(ResourceString
				.getResourceString("label.page.direction"));

		GridLayout directionGroupLayout = new GridLayout();
		directionGroupLayout.marginWidth = 20;
		directionGroupLayout.horizontalSpacing = 20;
		directionGroupLayout.numColumns = 4;

		directionGroup.setLayout(directionGroupLayout);

		Label vImage = new Label(directionGroup, SWT.NONE);
		vImage.setImage(Activator.getImage(ImageKey.PAGE_SETTING_V));

		vButton = new Button(directionGroup, SWT.RADIO);
		vButton.setBackground(ColorConstants.white);
		vButton.setText(ResourceString.getResourceString("label.page.direction.v"));

		Label hImage = new Label(directionGroup, SWT.NONE);
		hImage.setImage(Activator.getImage(ImageKey.PAGE_SETTING_H));

		hButton = new Button(directionGroup, SWT.RADIO);
		hButton.setBackground(ColorConstants.white);
		hButton.setText(ResourceString.getResourceString("label.page.direction.h"));
	}

	private void initScaleGroup(Composite parent) {
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		Group scaleGroup = new Group(parent, SWT.NONE);
		scaleGroup.setLayoutData(gridData);
		scaleGroup.setBackground(ColorConstants.white);
		scaleGroup.setText(ResourceString
				.getResourceString("label.page.scale.printing"));

		GridLayout scaleGroupLayout = new GridLayout();
		scaleGroupLayout.marginWidth = 20;
		scaleGroupLayout.horizontalSpacing = 20;
		scaleGroupLayout.numColumns = 3;
		scaleGroup.setLayout(scaleGroupLayout);

		Label label = new Label(scaleGroup, SWT.NONE);
		label.setBackground(ColorConstants.white);
		label.setText(ResourceString.getResourceString("label.page.scale"));

		scaleSpinner = new Spinner(scaleGroup, SWT.BORDER);
		scaleSpinner.setIncrement(5);
		scaleSpinner.setMinimum(10);
		scaleSpinner.setMaximum(400);
		scaleSpinner.setSelection(100);

		label = new Label(scaleGroup, SWT.NONE);
		label.setBackground(ColorConstants.white);
		label.setText("%");

	}

	private void initSizeGroup(Composite parent) {
		GridData sizeGroupGridData = new GridData();
		sizeGroupGridData.grabExcessHorizontalSpace = true;
		sizeGroupGridData.horizontalAlignment = GridData.FILL;

		Group sizeGroup = new Group(parent, SWT.NONE);
		sizeGroup.setLayoutData(sizeGroupGridData);
		sizeGroup.setBackground(ColorConstants.white);

		GridLayout sizeGroupLayout = new GridLayout();
		sizeGroupLayout.marginWidth = 20;
		sizeGroupLayout.horizontalSpacing = 20;
		sizeGroupLayout.numColumns = 2;
		sizeGroup.setLayout(sizeGroupLayout);

		Label label = new Label(sizeGroup, SWT.NONE);
		label.setBackground(ColorConstants.white);
		label.setText(ResourceString.getResourceString("label.page.size"));

		sizeCombo = new Combo(sizeGroup, SWT.READ_ONLY | SWT.BORDER);
		sizeCombo.setBackground(ColorConstants.white);
		this.setPaperSize(sizeCombo);

		label = new Label(sizeGroup, SWT.NONE);
		label.setBackground(ColorConstants.white);
		label.setText(ResourceString.getResourceString("label.page.margin"));

		Composite marginComposite = new Composite(sizeGroup, SWT.NONE);
		marginComposite.setBackground(ColorConstants.white);

		GridLayout marginCompositeLayout = new GridLayout();
		marginCompositeLayout.marginWidth = 10;
		marginCompositeLayout.horizontalSpacing = 10;
		marginCompositeLayout.numColumns = 6;
		marginComposite.setLayout(marginCompositeLayout);

		label = new Label(marginComposite, SWT.NONE);
		label = new Label(marginComposite, SWT.NONE);

		label = new Label(marginComposite, SWT.NONE);
		label.setBackground(ColorConstants.white);
		label.setText(ResourceString.getResourceString("label.page.margin.top"));

		topMarginSpinner = new Spinner(marginComposite, SWT.BORDER);
		this.setMarginSpinner(topMarginSpinner);

		label = new Label(marginComposite, SWT.NONE);
		label = new Label(marginComposite, SWT.NONE);

		label = new Label(marginComposite, SWT.NONE);
		label.setBackground(ColorConstants.white);
		label.setText(ResourceString.getResourceString("label.page.margin.left"));

		leftMarginSpinner = new Spinner(marginComposite, SWT.BORDER);
		this.setMarginSpinner(leftMarginSpinner);

		label = new Label(marginComposite, SWT.NONE);
		label = new Label(marginComposite, SWT.NONE);

		label = new Label(marginComposite, SWT.NONE);
		label.setBackground(ColorConstants.white);
		label.setText(ResourceString.getResourceString("label.page.margin.right"));

		rightMarginSpinner = new Spinner(marginComposite, SWT.BORDER);
		this.setMarginSpinner(rightMarginSpinner);

		label = new Label(marginComposite, SWT.NONE);
		label = new Label(marginComposite, SWT.NONE);

		label = new Label(marginComposite, SWT.NONE);
		label.setBackground(ColorConstants.white);
		label.setText(ResourceString.getResourceString("label.page.margin.bottom"));

		bottomMarginSpinner = new Spinner(marginComposite, SWT.BORDER);
		this.setMarginSpinner(bottomMarginSpinner);

		label = new Label(marginComposite, SWT.NONE);
		label = new Label(marginComposite, SWT.NONE);
	}

	private void setMarginSpinner(Spinner spinner) {
		spinner.setDigits(1);
		spinner.setIncrement(5);
		spinner.setMinimum(0);
		spinner.setMaximum(1000);
		spinner.setSelection(20);
	}

	private void setPaperSize(Combo combo) {
		for (String paperSize : PageSetting.getAllPaperSize()) {
			combo.add(paperSize);
		}

		combo.select(0);
	}

	@Override
	protected String getTitle() {
		return "dialog.title.page.setting";
	}

	@Override
	protected void perfomeOK() throws InputException {
		this.pageSetting = new PageSetting(this.hButton.getSelection(),
				this.scaleSpinner.getSelection(), this.sizeCombo.getText(),
				this.topMarginSpinner.getSelection(), this.rightMarginSpinner
						.getSelection(), this.bottomMarginSpinner
						.getSelection(), this.leftMarginSpinner.getSelection());
		this.diagram.setPageSetting(this.pageSetting);
	}

	@Override
	protected void setData() {
		if (this.pageSetting.isDirectionHorizontal()) {
			this.hButton.setSelection(true);
		} else {
			this.vButton.setSelection(true);
		}

		this.scaleSpinner.setSelection(this.pageSetting.getScale());
		this.sizeCombo.setText(this.pageSetting.getPaperSize());
		this.topMarginSpinner.setSelection(this.pageSetting.getTopMargin());
		this.rightMarginSpinner.setSelection(this.pageSetting.getRightMargin());
		this.bottomMarginSpinner.setSelection(this.pageSetting
				.getBottomMargin());

		this.leftMarginSpinner.setSelection(this.pageSetting.getLeftMargin());
	}

	@Override
	protected String getErrorMessage() {
		return null;
	}

}
