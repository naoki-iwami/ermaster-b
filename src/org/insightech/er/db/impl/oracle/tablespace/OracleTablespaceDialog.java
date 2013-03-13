package org.insightech.er.db.impl.oracle.tablespace;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.ResourceString;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceSizeCaluculatorDialog;
import org.insightech.er.util.Format;

public class OracleTablespaceDialog extends TablespaceDialog {

	private Text dataFile;

	private Text fileSize;

	private Button autoExtend;

	private Text autoExtendSize;

	private Text autoExtendMaxSize;

	private Text minimumExtentSize;

	private Text initial;

	private Text next;

	private Text minExtents;

	private Text maxExtents;

	private Text pctIncrease;

	private Button logging;

	private Button offline;

	private Button temporary;

	private Button autoSegmentSpaceManagement;

	private Button calculatorButton;

	public OracleTablespaceDialog() {
		super(6);
	}

	@Override
	protected void initialize(Composite composite) {
		super.initialize(composite);

		this.dataFile = CompositeFactory.createText(this, composite,
				"label.tablespace.data.file", 1, 200, false);
		this.fileSize = CompositeFactory.createText(this, composite,
				"label.size", 1, NUM_TEXT_WIDTH, false);
		this.calculatorButton = new Button(composite, SWT.NONE);
		this.calculatorButton.setText(ResourceString
				.getResourceString("label.calculate"));

		CompositeFactory.filler(composite, 1);

		CompositeFactory.filler(composite, 1);
		CompositeFactory.createExampleLabel(composite,
				"label.tablespace.data.file.example");
		CompositeFactory.filler(composite, 1);
		CompositeFactory.createExampleLabel(composite,
				"label.tablespace.size.example", 2);
		CompositeFactory.filler(composite, 1);

		Group autoExtendGroup = new Group(composite, SWT.NONE);
		GridLayout autoExtendGroupLayout = new GridLayout();
		autoExtendGroupLayout.numColumns = 5;
		autoExtendGroup.setLayout(autoExtendGroupLayout);
		autoExtendGroup.setText(ResourceString
				.getResourceString("label.tablespace.auto.extend"));

		GridData autoExtendGroupGridData = new GridData();
		autoExtendGroupGridData.horizontalSpan = this.getNumColumns();
		autoExtendGroupGridData.horizontalAlignment = GridData.FILL;
		autoExtendGroupGridData.grabExcessHorizontalSpace = true;
		autoExtendGroup.setLayoutData(autoExtendGroupGridData);

		this.autoExtend = CompositeFactory.createCheckbox(this,
				autoExtendGroup, "label.tablespace.auto.extend", 1);
		this.autoExtendSize = CompositeFactory.createText(this,
				autoExtendGroup, "label.size", 1, NUM_TEXT_WIDTH, false);
		this.autoExtendMaxSize = CompositeFactory.createText(this,
				autoExtendGroup, "label.max.size", 1, NUM_TEXT_WIDTH, false);
		CompositeFactory.filler(autoExtendGroup, 2);
		CompositeFactory.createExampleLabel(autoExtendGroup,
				"label.tablespace.size.example");
		CompositeFactory.filler(autoExtendGroup, 1);
		CompositeFactory.createExampleLabel(autoExtendGroup,
				"label.tablespace.size.example");

		this.minimumExtentSize = CompositeFactory.createText(this, composite,
				"label.tablespace.minimum.extent.size", 1, NUM_TEXT_WIDTH,
				false);
		CompositeFactory.filler(composite, 4);
		CompositeFactory.filler(composite, 1);
		CompositeFactory.createExampleLabel(composite,
				"label.tablespace.size.example");
		CompositeFactory.filler(composite, 4);

		Group defaultStorageGroup = new Group(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		defaultStorageGroup.setLayout(layout);
		defaultStorageGroup.setText("Default Storage");
		GridData defaultStorageGroupGridData = new GridData();
		defaultStorageGroupGridData.horizontalSpan = this.getNumColumns();
		defaultStorageGroupGridData.horizontalAlignment = GridData.FILL;
		defaultStorageGroupGridData.grabExcessHorizontalSpace = true;
		defaultStorageGroup.setLayoutData(defaultStorageGroupGridData);

		this.initial = CompositeFactory.createText(this, defaultStorageGroup,
				"label.tablespace.initial", 1, NUM_TEXT_WIDTH, false);
		CompositeFactory.createExampleLabel(defaultStorageGroup, "ex) 1M");
		this.next = CompositeFactory.createText(this, defaultStorageGroup,
				"label.tablespace.next", 1, NUM_TEXT_WIDTH, false);
		CompositeFactory.createExampleLabel(defaultStorageGroup, "ex) 1M");
		this.minExtents = CompositeFactory.createText(this,
				defaultStorageGroup, "label.tablespace.min.extents", 1,
				NUM_TEXT_WIDTH, false);
		CompositeFactory.createExampleLabel(defaultStorageGroup, "ex) 1");
		this.maxExtents = CompositeFactory.createText(this,
				defaultStorageGroup, "label.tablespace.max.extents", 1,
				NUM_TEXT_WIDTH, false);
		CompositeFactory.createExampleLabel(defaultStorageGroup, "ex) 4096");
		this.pctIncrease = CompositeFactory.createText(this,
				defaultStorageGroup, "label.tablespace.pct.increase", 1,
				NUM_TEXT_WIDTH, false);
		CompositeFactory.createExampleLabel(defaultStorageGroup, "ex) 0");

		this.logging = this.createCheckbox(composite,
				"label.tablespace.logging");
		this.offline = this.createCheckbox(composite,
				"label.tablespace.offline");
		this.temporary = this.createCheckbox(composite,
				"label.tablespace.temporary");
		this.autoSegmentSpaceManagement = this.createCheckbox(composite,
				"label.tablespace.auto.segment.space.management");
	}

	@Override
	protected TablespaceProperties setTablespaceProperties() {
		OracleTablespaceProperties properties = new OracleTablespaceProperties();

		properties.setAutoExtend(this.autoExtend.getSelection());
		properties
				.setAutoExtendMaxSize(this.autoExtendMaxSize.getText().trim());
		properties.setAutoExtendSize(this.autoExtendSize.getText().trim());
		properties
				.setAutoSegmentSpaceManagement(this.autoSegmentSpaceManagement
						.getSelection());
		properties.setDataFile(this.dataFile.getText().trim());
		properties.setFileSize(this.fileSize.getText().trim());
		properties.setInitial(this.initial.getText().trim());
		properties.setLogging(this.logging.getSelection());
		properties.setMaxExtents(this.maxExtents.getText().trim());
		properties.setMinExtents(this.minExtents.getText().trim());
		properties
				.setMinimumExtentSize(this.minimumExtentSize.getText().trim());
		properties.setNext(this.next.getText().trim());
		properties.setOffline(this.offline.getSelection());
		properties.setPctIncrease(this.pctIncrease.getText().trim());
		properties.setTemporary(this.temporary.getSelection());

		return properties;
	}

	@Override
	protected void setData(TablespaceProperties tablespaceProperties) {
		if (tablespaceProperties instanceof OracleTablespaceProperties) {
			OracleTablespaceProperties properties = (OracleTablespaceProperties) tablespaceProperties;

			this.autoExtend.setSelection(properties.isAutoExtend());
			this.autoExtendMaxSize.setText(Format.toString(properties
					.getAutoExtendMaxSize()));
			this.autoExtendSize.setText(Format.toString(properties
					.getAutoExtendSize()));
			this.autoSegmentSpaceManagement.setSelection(properties
					.isAutoSegmentSpaceManagement());
			this.dataFile.setText(Format.toString(properties.getDataFile()));
			this.fileSize.setText(Format.toString(properties.getFileSize()));
			this.initial.setText(Format.toString(properties.getInitial()));
			this.logging.setSelection(properties.isLogging());
			this.maxExtents
					.setText(Format.toString(properties.getMaxExtents()));
			this.minExtents
					.setText(Format.toString(properties.getMinExtents()));
			this.minimumExtentSize.setText(Format.toString(properties
					.getMinimumExtentSize()));
			this.next.setText(Format.toString(properties.getNext()));
			this.offline.setSelection(properties.isOffline());
			this.pctIncrease.setText(Format.toString(properties
					.getPctIncrease()));
			this.temporary.setSelection(properties.isTemporary());
		}

		this.setAutoExtendEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getErrorMessage() {
		String errorMessage = super.getErrorMessage();
		if (errorMessage != null) {
			return errorMessage;
		}

		if (this.autoExtend.getSelection()) {
			String text = this.autoExtendSize.getText().trim();
			if (text.equals("")) {
				return "error.tablespace.auto.extend.size.empty";
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addListener() {
		super.addListener();

		this.autoExtend.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				setAutoExtendEnabled();
			}

		});

		this.calculatorButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				TablespaceSizeCaluculatorDialog dialog = new TablespaceSizeCaluculatorDialog();
				dialog.init(diagram);
				dialog.open();
			}

		});
	}

	private void setAutoExtendEnabled() {
		boolean enabled = autoExtend.getSelection();
		autoExtendSize.setEnabled(enabled);
		autoExtendMaxSize.setEnabled(enabled);
	}
}
