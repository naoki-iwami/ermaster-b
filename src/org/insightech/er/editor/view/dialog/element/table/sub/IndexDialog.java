package org.insightech.er.editor.view.dialog.element.table.sub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.ResourceString;
import org.insightech.er.Resources;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public class IndexDialog extends AbstractDialog {

	private Text nameText;

	private Button addButton;

	private Button removeButton;

	private Button upButton;

	private Button downButton;

	private org.eclipse.swt.widgets.List allColumnList;

	private Table indexColumnList;

	private List<NormalColumn> selectedColumns;

	private List<NormalColumn> allColumns;

	private ERTable table;

	private Combo typeCombo;

	private Text tableText;

	private Text descriptionText;

	private Button uniqueCheckBox;

	private Button fullTextCheckBox;

	private boolean add;

	private Index targetIndex;

	private Index resultIndex;

	private Map<Column, Button> descCheckBoxMap = new HashMap<Column, Button>();

	private Map<Column, TableEditor> columnCheckMap = new HashMap<Column, TableEditor>();

	public IndexDialog(Shell parentShell, Index targetIndex, ERTable table) {
		super(parentShell);

		this.targetIndex = targetIndex;
		this.table = table;
		this.allColumns = table.getExpandedColumns();
		this.selectedColumns = new ArrayList<NormalColumn>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite composite) {
		this.createComposite(composite);
		this.createComposite1(composite);

		this.initializeAllList();

		this.setListener();

		this.nameText.setFocus();
	}

	/**
	 * This method initializes composite
	 * 
	 */
	private void createComposite(Composite parent) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(gridData);
		composite.setLayout(gridLayout);

		this.createCheckComposite(composite);

		this.tableText = CompositeFactory.createText(this, composite,
				"label.table.name", 1, -1, SWT.READ_ONLY | SWT.BORDER, false);
		this.nameText = CompositeFactory.createText(this, composite,
				"label.index.name", false);
		this.typeCombo = CompositeFactory.createReadOnlyCombo(this, composite,
				"label.index.type");

		this.initTypeCombo();

		this.descriptionText = CompositeFactory.createTextArea(this, composite,
				"label.description", -1, 100, 1, true);
	}

	private void initTypeCombo() {
		java.util.List<String> indexTypeList = DBManagerFactory.getDBManager(
				this.table.getDiagram()).getIndexTypeList(this.table);

		this.typeCombo.add("");

		for (String indexType : indexTypeList) {
			this.typeCombo.add(indexType);
		}
	}

	/**
	 * This method initializes composite
	 * 
	 */
	private void createCheckComposite(Composite composite) {
		GridData gridData2 = new GridData();
		gridData2.horizontalSpan = 2;
		gridData2.heightHint = 30;
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.grabExcessHorizontalSpace = true;

		Composite checkComposite = new Composite(composite, SWT.NONE);
		checkComposite.setLayoutData(gridData2);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		checkComposite.setLayout(gridLayout);

		this.uniqueCheckBox = new Button(checkComposite, SWT.CHECK);
		this.uniqueCheckBox.setText(ResourceString
				.getResourceString("label.index.unique"));

		DBManager dbManager = DBManagerFactory.getDBManager(this.table
				.getDiagram());

		if (dbManager.isSupported(DBManager.SUPPORT_FULLTEXT_INDEX)) {
			this.fullTextCheckBox = new Button(checkComposite, SWT.CHECK);
			this.fullTextCheckBox.setText(ResourceString
					.getResourceString("label.index.fulltext"));
		}
	}

	/**
	 * This method initializes composite1
	 * 
	 */
	private void createComposite1(Composite parent) {
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 3;
		gridLayout2.verticalSpacing = 20;

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		Composite composite = new Composite(parent, SWT.NONE);
		createGroup(composite);
		composite.setLayout(gridLayout2);
		composite.setLayoutData(gridData);

		this.addButton = CompositeFactory.createAddButton(composite);
		this.createGroup1(composite);
		this.removeButton = CompositeFactory.createRemoveButton(composite);
	}

	/**
	 * This method initializes group
	 * 
	 */
	private void createGroup(Composite composite) {
		GridLayout gridLayout4 = new GridLayout();
		gridLayout4.verticalSpacing = 5;
		gridLayout4.marginHeight = 10;
		GridData gridData6 = new GridData();
		gridData6.widthHint = 150;
		gridData6.heightHint = 150;
		GridData gridData3 = new GridData();
		gridData3.verticalSpan = 2;
		gridData3.horizontalAlignment = GridData.BEGINNING;

		Group group = new Group(composite, SWT.NONE);
		group.setLayoutData(gridData3);
		group.setLayout(gridLayout4);
		group
				.setText(ResourceString
						.getResourceString("label.all.column.list"));
		allColumnList = new org.eclipse.swt.widgets.List(group, SWT.BORDER
				| SWT.V_SCROLL);
		allColumnList.setLayoutData(gridData6);
	}

	/**
	 * This method initializes group1
	 * 
	 */
	private void createGroup1(Composite composite) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.verticalSpacing = 20;
		gridLayout.marginHeight = 10;

		GridData upButtonGridData = new GridData();
		upButtonGridData.grabExcessHorizontalSpace = false;
		upButtonGridData.verticalAlignment = GridData.END;
		upButtonGridData.grabExcessVerticalSpace = true;
		upButtonGridData.widthHint = Resources.BUTTON_WIDTH;

		GridData downButtonGridData = new GridData();
		downButtonGridData.grabExcessVerticalSpace = true;
		downButtonGridData.verticalAlignment = GridData.BEGINNING;
		downButtonGridData.widthHint = Resources.BUTTON_WIDTH;

		GridData gridData4 = new GridData();
		gridData4.verticalSpan = 2;

		Group group = new Group(composite, SWT.NONE);
		group.setText(ResourceString
				.getResourceString("label.index.column.list"));
		group.setLayout(gridLayout);
		group.setLayoutData(gridData4);

		this.initializeIndexColumnList(group);

		// indexColumnList = new List(group, SWT.BORDER | SWT.V_SCROLL);
		// indexColumnList.setLayoutData(gridData5);

		this.upButton = new Button(group, SWT.NONE);
		this.upButton.setText(ResourceString
				.getResourceString("label.up.arrow"));
		this.upButton.setLayoutData(upButtonGridData);

		this.downButton = new Button(group, SWT.NONE);
		this.downButton.setText(ResourceString
				.getResourceString("label.down.arrow"));
		this.downButton.setLayoutData(downButtonGridData);
	}

	private void initializeAllList() {
		for (NormalColumn column : this.allColumns) {
			this.allColumnList.add(column.getPhysicalName());
		}
	}

	private void initializeIndexColumnList(Composite parent) {
		GridData gridData = new GridData();
		gridData.heightHint = 150;
		gridData.verticalSpan = 2;

		indexColumnList = new Table(parent, SWT.FULL_SELECTION | SWT.BORDER);
		indexColumnList.setHeaderVisible(true);
		indexColumnList.setLayoutData(gridData);
		indexColumnList.setLinesVisible(false);

		TableColumn tableColumn = new TableColumn(indexColumnList, SWT.CENTER);
		tableColumn.setWidth(150);
		tableColumn.setText(ResourceString
				.getResourceString("label.column.name"));

		if (DBManagerFactory.getDBManager(this.table.getDiagram()).isSupported(
				DBManager.SUPPORT_DESC_INDEX)) {
			TableColumn tableColumn1 = new TableColumn(indexColumnList,
					SWT.CENTER);
			tableColumn1.setWidth(50);
			tableColumn1.setText(ResourceString
					.getResourceString("label.order.desc"));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setData() {
		if (this.targetIndex != null && !add) {
			this.tableText.setText(Format.null2blank(this.targetIndex
					.getTable().getPhysicalName()));

			this.nameText.setText(this.targetIndex.getName());

			this.descriptionText.setText(Format.null2blank(this.targetIndex
					.getDescription()));

			if (!Check.isEmpty(this.targetIndex.getType())) {
				boolean selected = false;

				for (int i = 0; i < this.typeCombo.getItemCount(); i++) {
					if (this.typeCombo.getItem(i).equals(
							this.targetIndex.getType())) {
						this.typeCombo.select(i);
						selected = true;
						break;
					}
				}

				if (!selected) {
					typeCombo.setText(this.targetIndex.getType());
				}
			}

			java.util.List<Boolean> descs = this.targetIndex.getDescs();
			int i = 0;

			for (NormalColumn column : this.targetIndex.getColumns()) {
				Boolean desc = Boolean.FALSE;

				if (descs.size() > i && descs.get(i) != null) {
					desc = descs.get(i);
				}

				this.addIndexColumn(column, desc);
				i++;
			}

			this.uniqueCheckBox.setSelection(!this.targetIndex.isNonUnique());

			DBManager dbManager = DBManagerFactory.getDBManager(table
					.getDiagram());
			if (dbManager.isSupported(DBManager.SUPPORT_FULLTEXT_INDEX)) {
				this.fullTextCheckBox.setSelection(this.targetIndex
						.isFullText());
			}
		}
	}

	private void addIndexColumn(NormalColumn column, Boolean desc) {
		TableItem tableItem = new TableItem(this.indexColumnList, SWT.NONE);

		tableItem.setText(0, column.getPhysicalName());

		this.setTableEditor(column, tableItem, desc);

		this.selectedColumns.add(column);

	}

	private void setTableEditor(final NormalColumn normalColumn,
			TableItem tableItem, Boolean desc) {
		Button descCheckButton = new Button(this.indexColumnList, SWT.CHECK);
		descCheckButton.pack();

		if (DBManagerFactory.getDBManager(this.table.getDiagram()).isSupported(
				DBManager.SUPPORT_DESC_INDEX)) {

			TableEditor editor = new TableEditor(this.indexColumnList);

			editor.minimumWidth = descCheckButton.getSize().x;
			editor.horizontalAlignment = SWT.CENTER;
			editor.setEditor(descCheckButton, tableItem, 1);

			this.columnCheckMap.put(normalColumn, editor);
		}

		this.descCheckBoxMap.put(normalColumn, descCheckButton);
		descCheckButton.setSelection(desc.booleanValue());
	}

	private void setListener() {
		this.upButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = indexColumnList.getSelectionIndex();

				if (index == -1 || index == 0) {
					return;
				}

				changeColumn(index - 1, index);
				indexColumnList.setSelection(index - 1);
			}

		});

		this.downButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = indexColumnList.getSelectionIndex();

				if (index == -1 || index == indexColumnList.getItemCount() - 1) {
					return;
				}

				changeColumn(index, index + 1);
				indexColumnList.setSelection(index + 1);
			}

		});

		this.addButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = allColumnList.getSelectionIndex();

				if (index == -1) {
					return;
				}

				NormalColumn column = allColumns.get(index);
				if (selectedColumns.contains(column)) {
					return;
				}

				addIndexColumn(column, Boolean.FALSE);

				validate();
			}

		});

		this.removeButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = indexColumnList.getSelectionIndex();

				if (index == -1) {
					return;
				}

				indexColumnList.remove(index);
				NormalColumn column = selectedColumns.remove(index);
				descCheckBoxMap.remove(column);

				disposeCheckBox(column);

				for (int i = index; i < indexColumnList.getItemCount(); i++) {
					column = selectedColumns.get(i);

					Button descCheckBox = descCheckBoxMap.get(column);
					boolean desc = descCheckBox.getSelection();
					disposeCheckBox(column);

					TableItem tableItem = indexColumnList.getItem(i);
					setTableEditor(column, tableItem, desc);
				}

				validate();
			}

		});

		this.nameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});
	}

	public void changeColumn(int index1, int index2) {
		NormalColumn column1 = selectedColumns.remove(index1);
		NormalColumn column2 = null;

		if (index1 < index2) {
			column2 = selectedColumns.remove(index2 - 1);
			selectedColumns.add(index1, column2);
			selectedColumns.add(index2, column1);

		} else if (index1 > index2) {
			column2 = selectedColumns.remove(index2);
			selectedColumns.add(index1 - 1, column2);
			selectedColumns.add(index2, column1);
		}

		boolean desc1 = this.descCheckBoxMap.get(column1).getSelection();
		boolean desc2 = this.descCheckBoxMap.get(column2).getSelection();

		TableItem[] tableItems = indexColumnList.getItems();

		this.column2TableItem(column1, desc1, tableItems[index2]);
		this.column2TableItem(column2, desc2, tableItems[index1]);

	}

	private void column2TableItem(NormalColumn column, boolean desc,
			TableItem tableItem) {
		this.disposeCheckBox(column);

		tableItem.setText(0, column.getPhysicalName());

		this.setTableEditor(column, tableItem, new Boolean(desc));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void perfomeOK() {
		String text = nameText.getText();

		this.resultIndex = new Index(table, text, !this.uniqueCheckBox
				.getSelection(), this.typeCombo.getText(), null);
		this.resultIndex.setDescription(this.descriptionText.getText().trim());

		int i = 0;

		for (NormalColumn selectedColumn : selectedColumns) {
			Boolean desc = Boolean.valueOf(this.descCheckBoxMap.get(
					selectedColumn).getSelection());
			this.resultIndex.addColumn(selectedColumn, desc);
			i++;
		}

		DBManager dbManager = DBManagerFactory.getDBManager(table.getDiagram());
		if (dbManager.isSupported(DBManager.SUPPORT_FULLTEXT_INDEX)) {
			this.resultIndex.setFullText(this.fullTextCheckBox.getSelection());
		}
	}

	public Index getResultIndex() {
		return this.resultIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getErrorMessage() {
		String text = nameText.getText().trim();

		if (text.equals("")) {
			return "error.index.name.empty";
		}

		if (!Check.isAlphabet(text)) {
			return "error.index.name.not.alphabet";
		}

		if (indexColumnList.getItemCount() == 0) {
			return "error.index.column.empty";
		}

		DBManager dbManager = DBManagerFactory.getDBManager(this.table
				.getDiagram());

		if (dbManager.isSupported(DBManager.SUPPORT_FULLTEXT_INDEX)) {
			if (fullTextCheckBox.getSelection()) {
				for (NormalColumn indexColumn : selectedColumns) {
					if (!indexColumn.isFullTextIndexable()) {
						return "error.index.fulltext.impossible";
					}
				}
			}
		}

		return null;
	}

	@Override
	protected String getTitle() {
		return "dialog.title.index";
	}

	private void disposeCheckBox(Column column) {
		TableEditor oldEditor = this.columnCheckMap.get(column);

		if (oldEditor != null) {
			if (oldEditor.getEditor() != null) {
				oldEditor.getEditor().dispose();
			}
			oldEditor.dispose();
		}

		this.columnCheckMap.remove(column);
	}
}
