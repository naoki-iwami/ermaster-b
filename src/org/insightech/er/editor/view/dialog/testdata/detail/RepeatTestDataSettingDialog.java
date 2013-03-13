package org.insightech.er.editor.view.dialog.testdata.detail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.testdata.RepeatTestDataDef;
import org.insightech.er.editor.view.dialog.testdata.detail.tab.RepeatTestDataTabWrapper;
import org.insightech.er.util.Format;

public class RepeatTestDataSettingDialog extends AbstractDialog {

	private static final int LABEL_WIDTH = 90;

	private static final int NUM_WIDTH = 50;

	private StackLayout stackLayout;
	private Composite cardPanel;

	private Composite nonePanel;
	private Composite templatePanel;
	private Composite foreignKeyPanel;
	private Composite enumPanel;

	private Combo columnCombo;

	private Combo typeCombo;

	private Label repeatNumLabel;

	private Text repeatNum;

	private Text template;

	private Text from;

	private Text to;

	private Text increment;

	private Text selects;

	private RepeatTestDataDef dataDef;

	private RepeatTestDataTabWrapper repeatTestDataTabWrapper;

	private int columnIndex;

	private NormalColumn normalColumn;

	private ERTable table;

	private boolean createContents = false;

	public RepeatTestDataSettingDialog(Shell parentShell, int columnIndex,
			RepeatTestDataTabWrapper repeatTestDataTabWrapper, ERTable table) {
		super(parentShell, 2);

		this.repeatTestDataTabWrapper = repeatTestDataTabWrapper;
		this.table = table;
		this.columnIndex = columnIndex;
	}

	@Override
	protected void initialize(Composite composite) {
		CompositeFactory.createLabel(composite, "label.column", 1, LABEL_WIDTH);
		this.columnCombo = CompositeFactory.createReadOnlyCombo(this,
				composite, null);

		CompositeFactory.createLabel(composite, "label.testdata.repeat.type",
				1, LABEL_WIDTH);
		this.typeCombo = CompositeFactory.createReadOnlyCombo(this, composite,
				null);

		this.repeatNumLabel = CompositeFactory.createLabel(composite,
				"label.testdata.repeat.num", 1, LABEL_WIDTH);
		this.repeatNum = CompositeFactory.createNumText(this, composite, null);

		this.initCardPanel(composite);

		this.initTypeCombo();
		this.initColumnCombo();
	}

	private void initColumnCombo() {
		for (NormalColumn normalColumn : this.table.getExpandedColumns()) {
			this.columnCombo.add(normalColumn.getName());
		}
	}

	private void initTypeCombo() {
		this.typeCombo.add(RepeatTestDataDef.TYPE_NULL);
		this.typeCombo.add(RepeatTestDataDef.TYPE_FORMAT);

		normalColumn = table.getExpandedColumns().get(columnIndex);

		if (normalColumn.isForeignKey()) {
			this.typeCombo.add(RepeatTestDataDef.TYPE_FOREIGNKEY);
		}

		this.typeCombo.add(RepeatTestDataDef.TYPE_ENUM);
	}

	private void initCardPanel(Composite composite) {
		this.cardPanel = new Composite(composite, SWT.NONE);

		this.stackLayout = new StackLayout();
		this.stackLayout.marginHeight = 0;
		this.stackLayout.marginWidth = 0;

		this.cardPanel.setLayout(this.stackLayout);

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.horizontalIndent = 0;
		gridData.verticalIndent = 0;
		this.cardPanel.setLayoutData(gridData);

		this.initNonePanel();
		this.initTemplatePanel();
		this.initForeignKeyPanel();
		this.initEnumPanel();
	}

	private void initNonePanel() {
		this.nonePanel = new Composite(this.cardPanel, SWT.NONE);
	}

	private void initTemplatePanel() {
		this.templatePanel = new Composite(this.cardPanel, SWT.NONE);
		GridLayout templatePanelLayout = new GridLayout(7, false);
		templatePanelLayout.marginHeight = 0;
		templatePanelLayout.marginWidth = 0;

		this.templatePanel.setLayout(templatePanelLayout);

		CompositeFactory.createLabel(templatePanel,
				"label.testdata.repeat.format", 1, LABEL_WIDTH);
		this.template = CompositeFactory.createText(this, templatePanel, null,
				6, false);
		CompositeFactory.filler(templatePanel, 1);
		CompositeFactory.createExampleLabel(templatePanel,
				"label.testdata.repeat.comment", 6);

		CompositeFactory.filler(templatePanel, 1);
		CompositeFactory.createLabel(templatePanel,
				"label.testdata.repeat.start");
		this.from = CompositeFactory.createNumText(this, templatePanel, null,
				NUM_WIDTH);
		CompositeFactory
				.createLabel(templatePanel, "label.testdata.repeat.end");
		this.to = CompositeFactory.createNumText(this, templatePanel, null,
				NUM_WIDTH);
		CompositeFactory.createLabel(templatePanel,
				"label.testdata.repeat.increment");
		this.increment = CompositeFactory.createNumText(this, templatePanel,
				null, NUM_WIDTH);
	}

	private void initForeignKeyPanel() {
		this.foreignKeyPanel = new Composite(this.cardPanel, SWT.NONE);
		this.foreignKeyPanel.setLayout(new GridLayout(2, false));
	}

	private void initEnumPanel() {
		this.enumPanel = new Composite(this.cardPanel, SWT.NONE);

		GridLayout enumPanelLayout = new GridLayout(2, false);
		enumPanelLayout.marginHeight = 0;
		enumPanelLayout.marginWidth = 0;

		this.enumPanel.setLayout(enumPanelLayout);

		CompositeFactory.createLabel(enumPanel,
				"label.testdata.repeat.enum.values", 1, LABEL_WIDTH);
		this.selects = CompositeFactory.createTextArea(this, enumPanel, null,
				-1, 100, 1, false);
	}

	@Override
	protected Point getInitialLocation(Point initialSize) {
		Point location = super.getInitialLocation(initialSize);

		location.y = 70;

		return location;
	}

	@Override
	protected void setData() {
		this.initialized = false;

		normalColumn = table.getExpandedColumns().get(columnIndex);
		dataDef = repeatTestDataTabWrapper.getRepeatTestData().getDataDef(
				normalColumn);

		this.columnCombo.select(this.columnIndex);

		if (dataDef != null) {
			this.typeCombo.setText(dataDef.getType());
			this.repeatNum.setText(Format.toString(dataDef.getRepeatNum()));
			this.template.setText(Format.toString(dataDef.getTemplate()));
			this.from.setText(Format.toString(dataDef.getFrom()));
			this.to.setText(Format.toString(dataDef.getTo()));
			this.increment.setText(Format.toString(dataDef.getIncrement()));

			StringBuilder sb = new StringBuilder();
			for (String str : dataDef.getSelects()) {
				sb.append(str);
				sb.append("\r\n");
			}
			this.selects.setText(sb.toString());

			this.setCardPanel(this.typeCombo.getText());

		} else {
			this.repeatNum.setText("1");
			this.template.setText("value_%");
			this.from.setText("1");
			this.to.setText("5");
			this.increment.setText("1");

			this.selects
					.setText("value_1\r\nvalue_2\r\nvalue_3\r\nvalue_4\r\n");
		}

		this.initialized = true;
		// this.validate();
	}

	@Override
	protected String getTitle() {
		return "dialog.title.testdata.repetition.condition.setting";
	}

	@Override
	protected String getErrorMessage() {
		if (this.createContents) {
			this.dataDef = this.getRepeatTestDataDef();

			this.repeatTestDataTabWrapper.setRepeatTestDataDef(
					this.normalColumn, this.dataDef);
			this.repeatTestDataTabWrapper.initTableData();
		}

		return null;
	}

	@Override
	protected void perfomeOK() throws InputException {
	}

	private RepeatTestDataDef getRepeatTestDataDef() {
		RepeatTestDataDef dataDef = new RepeatTestDataDef();

		dataDef.setType(this.typeCombo.getText());
		dataDef.setRepeatNum(this.getIntValue(this.repeatNum));
		dataDef.setTemplate(this.template.getText());
		dataDef.setFrom(this.getIntValue(this.from));
		dataDef.setTo(this.getIntValue(this.to));
		dataDef.setIncrement(this.getIntValue(this.increment));

		String str = this.selects.getText();
		BufferedReader reader = new BufferedReader(new StringReader(str));
		String line = null;
		List<String> lines = new ArrayList<String>();

		try {
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
		}

		dataDef.setSelects(lines.toArray(new String[lines.size()]));

		return dataDef;
	}

	private int getIntValue(Text textField) {
		try {
			return Integer.parseInt(textField.getText().trim());

		} catch (NumberFormatException e) {
		}

		return 0;
	}

	@Override
	protected void addListener() {
		super.addListener();

		this.typeCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent selectionevent) {
				setCardPanel(typeCombo.getText());
			}

		});

		this.columnCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent selectionevent) {
				columnIndex = columnCombo.getSelectionIndex();
				setData();
			}

		});
	}

	private void setCardPanel(String selectedType) {
		if (RepeatTestDataDef.TYPE_FORMAT.equals(selectedType)) {
			stackLayout.topControl = templatePanel;
			this.repeatNumLabel.setVisible(true);
			this.repeatNum.setVisible(true);
			cardPanel.layout();

		} else if (RepeatTestDataDef.TYPE_FOREIGNKEY.equals(selectedType)) {
			stackLayout.topControl = foreignKeyPanel;
			this.repeatNumLabel.setVisible(true);
			this.repeatNum.setVisible(true);
			cardPanel.layout();

		} else if (RepeatTestDataDef.TYPE_ENUM.equals(selectedType)) {
			stackLayout.topControl = enumPanel;
			this.repeatNumLabel.setVisible(true);
			this.repeatNum.setVisible(true);
			cardPanel.layout();

		} else {
			stackLayout.topControl = nonePanel;
			this.repeatNumLabel.setVisible(false);
			this.repeatNum.setVisible(false);
			cardPanel.layout();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		this.createButton(parent, IDialogConstants.CLOSE_ID,
				IDialogConstants.CLOSE_LABEL, false);
	}

	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);

		this.addListener();
		this.validate();

		this.createContents = true;

		return control;
	}
}
