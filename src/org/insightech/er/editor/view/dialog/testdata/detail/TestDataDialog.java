package org.insightech.er.editor.view.dialog.testdata.detail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.ValidatableTabWrapper;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.testdata.TableTestData;
import org.insightech.er.editor.model.testdata.TestData;
import org.insightech.er.editor.view.dialog.testdata.detail.tab.DirectTestDataTabWrapper;
import org.insightech.er.editor.view.dialog.testdata.detail.tab.RepeatTestDataTabWrapper;
import org.insightech.er.util.Format;

public class TestDataDialog extends AbstractDialog {

	public static final int WIDTH = 750;

	public static final int TABLE_HEIGHT = 300;

	/*---------- メインパネル -----------*/

	/** → */
	private Button addButton;

	/** ← */
	private Button removeButton;

	/** 全テーブルリスト */
	private org.eclipse.swt.widgets.List allTableListWidget;

	/** テストデータ作成テーブル */
	private Table selectedTableTable;

	private Button repeatToDirectRadio;

	private Button directToRepeatRadio;

	/*---------- タブパネル -----------*/

	/** タブフォルダー */
	private TabFolder tabFolder;

	/** タブリスト */
	private List<ValidatableTabWrapper> tabWrapperList;

	private DirectTestDataTabWrapper directTestDataTabWrapper;

	private RepeatTestDataTabWrapper repeatTestDataTabWrapper;

	/*---------- データモデル -----------*/

	private ERDiagram diagram;

	/** 現在編集中のデータ */
	private TestData testData;

	private List<ERTable> allTableList;

	/** テストデータ名 */
	private Text nameText;

	private int selectedTableIndex = -1;

	public ERDiagram getDiagram() {
		return this.diagram;
	}

	public TestDataDialog(Shell parentShell, ERDiagram diagram,
			TestData testData) {
		super(parentShell);

		this.diagram = diagram;

		this.testData = testData.clone();

		this.allTableList = diagram.getDiagramContents().getContents()
				.getTableSet().getList();

		// タブパネルリスト
		this.tabWrapperList = new ArrayList<ValidatableTabWrapper>();
	}

	@Override
	protected void initialize(Composite composite) {
		this.createNameComposite(composite);
		this.createTopComposite(composite);
		this.createBottomComposite(composite);
	}

	private void createNameComposite(Composite parent) {
		// パネル
		Composite nameComposite = new Composite(parent, SWT.NONE);

		GridLayout mainLayout = new GridLayout();
		mainLayout.numColumns = 2;
		nameComposite.setLayout(mainLayout);

		this.nameText = CompositeFactory.createText(this, nameComposite,
				"label.testdata.name", 1, 200, true);
	}

	private void createTopComposite(Composite parent) {
		// パネル
		Composite topComposite = new Composite(parent, SWT.NONE);

		GridLayout mainLayout = new GridLayout();
		mainLayout.numColumns = 3;
		topComposite.setLayout(mainLayout);

		GridData topGridData = new GridData();
		topGridData.grabExcessHorizontalSpace = true;
		topGridData.horizontalAlignment = GridData.FILL;
		topGridData.heightHint = 150;
		topComposite.setLayoutData(topGridData);

		// テーブル一覧
		this.createAllTableList(topComposite);

		// →
		this.addButton = CompositeFactory.createAddButton(topComposite);
		this.addButton.setEnabled(false);

		// テストデータ作成テーブル一覧
		this.createSelectedTableTable(topComposite);

		// ←
		this.removeButton = CompositeFactory.createRemoveButton(topComposite);
		this.removeButton.setEnabled(false);
	}

	private void createAllTableList(Composite composite) {
		Group group = new Group(composite, SWT.NONE);

		GridData gridData = new GridData();
		gridData.verticalSpan = 2;
		gridData.horizontalAlignment = GridData.BEGINNING;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		group.setLayoutData(gridData);

		GridLayout groupLayout = new GridLayout();
		group.setLayout(groupLayout);
		group.setText(ResourceString.getResourceString("label.all.table"));

		// 全てのテーブル
		GridData comboGridData = new GridData();
		comboGridData.widthHint = 300;
		comboGridData.grabExcessVerticalSpace = true;
		comboGridData.verticalAlignment = GridData.FILL;

		this.allTableListWidget = new org.eclipse.swt.widgets.List(group,
				SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		this.allTableListWidget.setLayoutData(comboGridData);
	}

	private void createSelectedTableTable(Composite composite) {
		GridData gridData = new GridData();
		gridData.verticalSpan = 2;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;

		Group group = new Group(composite, SWT.NONE);
		group.setText(ResourceString
				.getResourceString("label.testdata.table.list"));
		group.setLayout(gridLayout);
		group.setLayoutData(gridData);

		GridData tableGridData = new GridData();
		tableGridData.grabExcessVerticalSpace = true;
		tableGridData.verticalAlignment = GridData.FILL;
		tableGridData.widthHint = 300;
		tableGridData.verticalSpan = 2;

		this.selectedTableTable = new Table(group, SWT.FULL_SELECTION
				| SWT.BORDER | SWT.MULTI);
		this.selectedTableTable.setHeaderVisible(false);
		this.selectedTableTable.setLayoutData(tableGridData);
		this.selectedTableTable.setLinesVisible(false);

		TableColumn tableColumn = new TableColumn(this.selectedTableTable,
				SWT.CENTER);
		tableColumn.setWidth(200);
		tableColumn.setText(ResourceString
				.getResourceString("label.testdata.table.name"));

		TableColumn numColumn = new TableColumn(this.selectedTableTable,
				SWT.CENTER);
		numColumn.setWidth(80);
		numColumn.setText(ResourceString
				.getResourceString("label.testdata.table.test.num"));
	}

	private void createBottomComposite(Composite composite) {
		GridData bottomGridData = new GridData();
		bottomGridData.grabExcessHorizontalSpace = true;
		bottomGridData.horizontalAlignment = GridData.FILL;
		bottomGridData.widthHint = WIDTH;

		this.createOutputOrderGroup(composite);
		
		// タブ
		this.tabFolder = new TabFolder(composite, SWT.NONE);
		this.tabFolder.setLayoutData(bottomGridData);

		this.directTestDataTabWrapper = new DirectTestDataTabWrapper(this,
				this.tabFolder, SWT.NONE);
		this.tabWrapperList.add(this.directTestDataTabWrapper);

		this.repeatTestDataTabWrapper = new RepeatTestDataTabWrapper(this,
				this.tabFolder, SWT.NONE);
		this.tabWrapperList.add(this.repeatTestDataTabWrapper);

	}

	private void createOutputOrderGroup(Composite parent) {
		GridData groupGridData = new GridData();
		groupGridData.horizontalAlignment = GridData.FILL;
		groupGridData.grabExcessHorizontalSpace = true;
		
		GridLayout groupLayout = new GridLayout();
		groupLayout.marginWidth = 15;
		groupLayout.marginHeight = 15;

		Group group = new Group(parent, SWT.NONE);
		group.setText(ResourceString.getResourceString("label.output.order"));
		group.setLayoutData(groupGridData);
		group.setLayout(groupLayout);

		this.directToRepeatRadio = CompositeFactory.createRadio(this, group,
				"label.output.order.direct.to.repeat");
		this.repeatToDirectRadio = CompositeFactory.createRadio(this, group,
				"label.output.order.repeat.to.direct");
	}

	
	private void initSelectedTableTable() {
		// テーブル一覧
		this.selectedTableTable.removeAll();

		for (Map.Entry<ERTable, TableTestData> entry : testData
				.getTableTestDataMap().entrySet()) {
			ERTable table = entry.getKey();
			TableTestData tableTestData = entry.getValue();

			TableItem tableItem = new TableItem(this.selectedTableTable,
					SWT.NONE);
			tableItem.setText(0, table.getName());
			tableItem
					.setText(1, String.valueOf(tableTestData.getTestDataNum()));
		}
	}

	public void resetTestDataNum() {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				int targetIndex = selectedTableTable.getSelectionIndex();

				if (targetIndex != -1) {

					int num = directTestDataTabWrapper.getTestDataNum()
							+ repeatTestDataTabWrapper.getTestDataNum();

					TableItem tableItem = selectedTableTable
							.getItem(targetIndex);
					tableItem.setText(1, String.valueOf(num));
				}
			}

		});

	}

	@Override
	protected void setData() {
		this.nameText.setText(Format.null2blank(this.testData.getName()));

		for (ERTable table : this.allTableList) {
			this.allTableListWidget.add(Format.null2blank(table.getName()));
		}

		initSelectedTableTable();

		if (this.selectedTableIndex != -1) {
			this.selectedTableTable.select(this.selectedTableIndex);
			removeButton.setEnabled(true);
			resetTabs();
			this.selectedTableIndex = -1;
		}

		if (this.testData.getExportOrder() == TestData.EXPORT_ORDER_DIRECT_TO_REPEAT) {
			this.directToRepeatRadio.setSelection(true);

		} else {
			this.repeatToDirectRadio.setSelection(true);

		}
	}

	@Override
	protected String getTitle() {
		return "dialog.title.testdata.edit";
	}

	@Override
	protected String getErrorMessage() {
		String text = this.nameText.getText().trim();

		if (text.equals("")) {
			return "error.testdata.name.empty";
		}

		if (this.selectedTableTable.getItemCount() == 0) {
			return "error.testdata.table.empty";
		}

		try {
			for (ValidatableTabWrapper tabWrapper : this.tabWrapperList) {
				tabWrapper.validatePage();
			}

		} catch (InputException e) {
			return e.getMessage();
		}

		return null;
	}

	@Override
	protected void perfomeOK() throws InputException {
		String text = this.nameText.getText().trim();

		this.testData.setName(text);

		if (this.repeatToDirectRadio.getSelection()) {
			testData.setExportOrder(TestData.EXPORT_ORDER_REPEAT_TO_DIRECT);

		} else if (this.directToRepeatRadio.getSelection()) {
			testData.setExportOrder(TestData.EXPORT_ORDER_DIRECT_TO_REPEAT);

		}

		for (ValidatableTabWrapper tab : this.tabWrapperList) {
			tab.perfomeOK();
		}
	}

	@Override
	protected void addListener() {
		super.addListener();

		this.allTableListWidget.addSelectionListener(new SelectionAdapter() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent evt) {
				int index = allTableListWidget.getSelectionIndex();

				if (index == -1) {
					addButton.setEnabled(false);
				} else {
					addButton.setEnabled(true);
				}
			}
		});

		this.addButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int[] indexes = allTableListWidget.getSelectionIndices();

				if (indexes.length < 1) {
					return;
				}

				for (int index : indexes) {
					// 選択されたテーブルにてテーブルテストデータを追加

					ERTable table = allTableList.get(index);
					if (!testData.contains(table)) {
						TableTestData tableTestData = new TableTestData();

						testData.putTableTestData(table, tableTestData);
					}
				}

				initSelectedTableTable();
				validate();
			}

		});

		this.removeButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = selectedTableTable.getSelectionIndex();

				if (index == -1) {
					return;
				}

				testData.removeTableTestData(index);

				initSelectedTableTable();
				validate();

				if (selectedTableTable.getItemCount() <= index) {
					index--;
				}

				selectedTableTable.setSelection(index);
				if (index == -1) {
					removeButton.setEnabled(false);
				}
			}

		});

		this.selectedTableTable.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent evt) {

				int index = selectedTableTable.getSelectionIndex();

				if (index == -1) {
					removeButton.setEnabled(false);
					return;

				} else {
					removeButton.setEnabled(true);
				}

				resetTabs();
			}

		});

		this.tabFolder.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				int index = tabFolder.getSelectionIndex();

				ValidatableTabWrapper selectedTabWrapper = tabWrapperList
						.get(index);
				selectedTabWrapper.setInitFocus();
			}

		});

	}

	private void resetTabs() {
		for (ValidatableTabWrapper tab : tabWrapperList) {
			// tab.setVisible(false);
			tab.reset();
			// tab.setVisible(true);
		}
	}

	public ERTable getTargetTable() {
		int targetIndex = this.selectedTableTable.getSelectionIndex();
		return this.testData.get(targetIndex);
	}

	public TestData getTestData() {
		return this.testData;
	}

	public void setSelectedTable(int selectedTableIndex) {
		this.selectedTableIndex = selectedTableIndex;
	}
}
