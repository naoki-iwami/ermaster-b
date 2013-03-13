package org.insightech.er.editor.view.dialog.element.view.tab;

import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ResourceString;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.ValidatableTabWrapper;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.group.ChangeGroupCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.CopyGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.view.dialog.common.ERTableComposite;
import org.insightech.er.editor.view.dialog.common.ERTableCompositeHolder;
import org.insightech.er.editor.view.dialog.element.view.ViewDialog;
import org.insightech.er.editor.view.dialog.group.GroupManageDialog;
import org.insightech.er.editor.view.dialog.word.column.ViewColumnDialog;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public class AttributeTabWrapper extends ValidatableTabWrapper implements
		ERTableCompositeHolder {

	private static final int GROUP_TABLE_HEIGHT = 75;

	private View copyData;

	private Text physicalNameText;

	private Text logicalNameText;

	private Combo groupCombo;

	private Button groupAddButton;

	private Button groupManageButton;

	private ViewDialog viewDialog;

	private ERTableComposite tableComposite;

	private ERTableComposite groupTableComposite;

	public AttributeTabWrapper(ViewDialog viewDialog, TabFolder parent,
			int style, View copyData) {
		super(viewDialog, parent, style, "label.table.attribute");

		this.copyData = copyData;
		this.viewDialog = viewDialog;

		this.init();
	}

	@Override
	public void initComposite() {
		this.setLayout(new GridLayout());

		this.createHeader(this);
		this.createBody(this);
		this.createFooter(this);
	}

	private void createHeader(Composite parent) {
		Composite header = new Composite(parent, SWT.NONE);

		GridLayout gridLayout = new GridLayout(4, false);
		gridLayout.horizontalSpacing = 20;

		header.setLayout(gridLayout);

		this.physicalNameText = CompositeFactory.createText(viewDialog, header,
				"label.physical.name", 1, 200, false);
		this.logicalNameText = CompositeFactory.createText(viewDialog, header,
				"label.logical.name", 1, 200, true);

		this.physicalNameText.setText(Format.null2blank(copyData
				.getPhysicalName()));
		this.logicalNameText.setText(Format.null2blank(copyData
				.getLogicalName()));
	}

	private void createBody(Composite parent) {
		Composite content = new Composite(parent, SWT.BORDER);
		GridData contentGridData = new GridData();
		contentGridData.horizontalAlignment = GridData.FILL;
		contentGridData.grabExcessHorizontalSpace = true;

		content.setLayoutData(contentGridData);

		content.setLayout(new GridLayout(6, false));

		this.initTable(content);
	}

	private void initTable(Composite parent) {
		ViewColumnDialog columnDialog = new ViewColumnDialog(PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getShell(),
				this.copyData);

		this.tableComposite = new ERTableComposite(this, parent, this.copyData
				.getDiagram(), null, this.copyData.getColumns(), columnDialog,
				this.viewDialog, 2, true, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validatePage() throws InputException {
		String text = logicalNameText.getText().trim();
		this.copyData.setLogicalName(text);

		if (text.equals("")) {
			throw new InputException("error.table.logical.name.empty");
		}

		text = physicalNameText.getText().trim();
		if (!Check.isAlphabet(text)) {
			if (copyData.getDiagram().getDiagramContents().getSettings()
					.isValidatePhysicalName()) {
				throw new InputException(
						"error.table.physical.name.not.alphabet");
			}
		}
		this.copyData.setPhysicalName(text);
	}

	private void createFooter(Composite parent) {
		Composite footer = new Composite(parent, SWT.NONE);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		footer.setLayout(gridLayout);

		this.createGroupCombo(footer);

		this.groupAddButton = new Button(footer, SWT.NONE);
		this.groupAddButton.setText(ResourceString
				.getResourceString("label.button.add.group.to.view"));

		this.groupAddButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int targetIndex = groupCombo.getSelectionIndex();
				if (targetIndex == -1) {
					return;
				}

				ColumnGroup columnGroup = getColumnGroups().get(targetIndex);
				tableComposite.addTableData(columnGroup);

				groupAddButton.setEnabled(false);
			}

		});

		this.groupAddButton.setEnabled(false);

		createGroup(footer);

		this.initGroupCombo();
	}

	/**
	 * This method initializes combo
	 * 
	 */
	private void createGroupCombo(Composite parent) {
		GridData gridData = new GridData();
		gridData.widthHint = 200;

		this.groupCombo = new Combo(parent, SWT.READ_ONLY);
		this.groupCombo.setLayoutData(gridData);

		this.groupCombo.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int targetIndex = groupCombo.getSelectionIndex();
				if (targetIndex == -1) {
					return;
				}

				selectGroup(targetIndex);
			}
		});
	}

	private void initGroupCombo() {
		this.groupCombo.removeAll();

		for (ColumnGroup columnGroup : this.getColumnGroups()) {
			this.groupCombo.add(columnGroup.getGroupName());
		}

		this.groupTableComposite.setColumnList(null);
	}

	private void restructGroup() {
		this.initGroupCombo();

		int index = 0;
		for (Column column : this.copyData.getColumns()) {
			if (column instanceof ColumnGroup) {
				if (!this.getColumnGroups().contains((ColumnGroup) column)) {
					this.tableComposite.removeColumn(index);
					continue;
				}
			}
			index++;
		}

		this.viewDialog.validate();
	}

	/**
	 * This method initializes group
	 * 
	 */
	private void createGroup(Composite parent) {
		GridData gridData1 = new GridData();
		gridData1.heightHint = 100;
		gridData1.widthHint = -1;
		GridData gridData = new GridData();
		gridData.heightHint = -1;
		gridData.horizontalSpan = 2;

		// FormToolkit toolkit = new FormToolkit(this.getDisplay());
		// Form root = toolkit.createForm(parent);
		// root.getBody().setLayout(new GridLayout());
		//		
		// ExpandableComposite expandableComposite = toolkit
		// .createExpandableComposite(root.getBody(),
		// ExpandableComposite.TWISTIE);
		//
		// Composite inner = toolkit.createComposite(expandableComposite);
		// inner.setLayout(new GridLayout());
		// expandableComposite.setClient(inner);
		// toolkit.createLabel(inner, "aaa");

		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setLayoutData(gridData);

		this.groupTableComposite = new ERTableComposite(this, group,
				this.copyData.getDiagram(), null, null, null, null, 2, false,
				false, GROUP_TABLE_HEIGHT);

		this.groupManageButton = new Button(group, SWT.NONE);
		this.groupManageButton.setText(ResourceString
				.getResourceString("label.button.group.manage"));

		this.groupManageButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				GroupSet groupSet = getColumnGroups();

				GroupManageDialog dialog = new GroupManageDialog(PlatformUI
						.getWorkbench().getActiveWorkbenchWindow().getShell(),
						groupSet, copyData.getDiagram(), false, -1);

				if (dialog.open() == IDialogConstants.OK_ID) {
					List<CopyGroup> newColumnGroups = dialog
							.getCopyColumnGroups();

					Command command = new ChangeGroupCommand(viewDialog
							.getDiagram(), groupSet, newColumnGroups);

					viewDialog.getViewer().getEditDomain().getCommandStack()
							.execute(command);

					restructGroup();

					groupAddButton.setEnabled(false);
				}
			}

		});
	}

	private GroupSet getColumnGroups() {
		return this.copyData.getDiagram().getDiagramContents().getGroups();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInitFocus() {
		this.physicalNameText.setFocus();
	}

	public void selectGroup(ColumnGroup selectedColumn) {
		int targetIndex = this.getColumnGroups().indexOf(selectedColumn);

		this.groupCombo.select(targetIndex);
		this.selectGroup(targetIndex);

		this.groupAddButton.setEnabled(false);
	}

	@SuppressWarnings("unchecked")
	private void selectGroup(int targetIndex) {
		ColumnGroup columnGroup = getColumnGroups().get(targetIndex);

		if (this.copyData.getColumns().contains(columnGroup)) {
			this.groupAddButton.setEnabled(false);
		} else {
			this.groupAddButton.setEnabled(true);
		}

		this.groupTableComposite.setColumnList((List) columnGroup.getColumns());
	}

	@Override
	public void perfomeOK() {
	}
}
