package org.insightech.er.editor.view.dialog.testdata;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorPart;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.testdata.TableTestData;
import org.insightech.er.editor.model.testdata.TestData;
import org.insightech.er.editor.view.dialog.dbexport.ExportToTestDataDialog;
import org.insightech.er.editor.view.dialog.testdata.detail.TestDataDialog;
import org.insightech.er.util.Format;

public class TestDataManageDialog extends AbstractDialog {

	private static final int GROUP_LIST_HEIGHT = 230;

	// ERデータ
	private ERDiagram diagram;

	// テストデータ一覧
	private org.eclipse.swt.widgets.List testDataListWidget;

	// 追加
	private Button addButton;

	// 編集
	private Button editButton;

	// 削除
	private Button deleteButton;

	// コピー
	private Button copyButton;

	private Button exportButton;

	// テーブル一覧
	private Table testDataTable;

	// 編集中テストデータリスト
	private List<TestData> testDataList;

	private IEditorPart editorPart;

	/**
	 * コンストラクタ
	 */
	public TestDataManageDialog(Shell parentShell, IEditorPart editorPart,
			ERDiagram diagram, List<TestData> testDataList) {
		super(parentShell, 2);

		this.editorPart = editorPart;
		this.diagram = diagram;
		this.testDataList = testDataList;
	}

	/**
	 * 初期化処理を実施します。
	 */
	@Override
	protected void initialize(Composite composite) {
		// テストデータ一覧パネル
		this.createLeftComposite(composite);

		// テストデータ詳細（テーブル一覧）パネル
		this.createRightComposite(composite);
	}

	/**
	 * テストデータ一覧パネルを生成します。
	 */
	private void createLeftComposite(Composite parent) {
		GridData gridData = new GridData();

		GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 10;
		gridLayout.numColumns = 3;

		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayoutData(gridData);
		composite.setLayout(gridLayout);

		// テストデータ一覧
		GridData listCompGridData = new GridData();
		listCompGridData.horizontalSpan = 4;
		this.createTestDataList(composite, listCompGridData);

		// 追加
		this.addButton = CompositeFactory.createButton(composite,
				"label.button.add");

		// 編集
		this.editButton = CompositeFactory.createButton(composite,
				"label.button.edit");
		this.editButton.setEnabled(false);

		// 削除
		this.deleteButton = CompositeFactory.createButton(composite,
				"label.button.delete");
		this.deleteButton.setEnabled(false);

		// コピー
		this.copyButton = CompositeFactory.createButton(composite,
				"label.button.copy");
	}

	private void createTestDataList(Composite parent, GridData gridData) {
		GridLayout gridLayout = new GridLayout();

		Group group = new Group(parent, SWT.NONE);
		group.setText(ResourceString.getResourceString("label.testdata.list"));
		group.setLayoutData(gridData);
		group.setLayout(gridLayout);

		GridData listGridData = new GridData();
		listGridData.widthHint = 200;
		listGridData.heightHint = GROUP_LIST_HEIGHT;

		this.testDataListWidget = new org.eclipse.swt.widgets.List(group,
				SWT.BORDER | SWT.V_SCROLL);
		this.testDataListWidget.setLayoutData(listGridData);

		this.initTestDataList();
	}

	/**
	 * テストデータ詳細を生成します。
	 * 
	 * @param gridData
	 * 
	 */
	private void createRightComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.BORDER);

		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		composite.setLayoutData(gridData);

		GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 8;
		composite.setLayout(gridLayout);

		// テストデータテーブル
		GridData tableGridData = new GridData();
		tableGridData.heightHint = GROUP_LIST_HEIGHT;
		tableGridData.verticalIndent = 15;

		this.testDataTable = new Table(composite, SWT.BORDER
				| SWT.FULL_SELECTION);
		this.testDataTable.setHeaderVisible(true);
		this.testDataTable.setLayoutData(tableGridData);
		this.testDataTable.setLinesVisible(true);

		TableColumn nameColumn = new TableColumn(testDataTable, SWT.NONE);
		nameColumn.setWidth(300);
		nameColumn.setResizable(false);
		nameColumn.setText(ResourceString
				.getResourceString("label.testdata.table.name"));

		TableColumn dataNumColumn = new TableColumn(testDataTable, SWT.NONE);
		dataNumColumn.setWidth(80);
		dataNumColumn.setResizable(false);
		dataNumColumn.setText(ResourceString
				.getResourceString("label.testdata.table.test.num"));

		this.exportButton = CompositeFactory.createButton(composite,
				"label.button.testdata.export", 1);
		this.exportButton.setEnabled(false);
	}

	private void initTestDataList() {
		Collections.sort(this.testDataList);

		this.testDataListWidget.removeAll();

		for (TestData testData : this.testDataList) {
			this.testDataListWidget.add(Format.null2blank(testData.getName()));
		}
	}

	private void initTableData() {
		// テーブル一覧
		this.testDataTable.removeAll();

		int targetIndex = this.testDataListWidget.getSelectionIndex();
		if (targetIndex == -1) {
			return;
		}

		TestData testData = this.testDataList.get(targetIndex);

		for (Map.Entry<ERTable, TableTestData> entry : testData
				.getTableTestDataMap().entrySet()) {
			ERTable table = entry.getKey();
			TableTestData tableTestData = entry.getValue();

			TableItem tableItem = new TableItem(testDataTable, SWT.NONE);
			tableItem.setText(0, table.getName());
			tableItem
					.setText(1, String.valueOf(tableTestData.getTestDataNum()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getErrorMessage() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void perfomeOK() {
	}

	@Override
	protected String getTitle() {
		return "dialog.title.testdata";
	}

	@Override
	protected void setData() {
	}

	private void addTestData() {
		TestData oldTestData = new TestData();

		TestDataDialog testDataDialog = new TestDataDialog(this.getShell(),
				diagram, oldTestData);

		if (testDataDialog.open() == IDialogConstants.OK_ID) {
			TestData newTestData = testDataDialog.getTestData();

			this.testDataList.add(newTestData);

			this.initTestDataList();

			for (int i = 0; i < this.testDataList.size(); i++) {
				TestData testData = this.testDataList.get(i);

				if (testData == newTestData) {
					this.testDataListWidget.setSelection(i);
					break;
				}
			}

			this.editButton.setEnabled(true);
			this.deleteButton.setEnabled(true);
			this.exportButton.setEnabled(true);

			initTableData();
		}
	}

	private void exportTestData() {
		int targetIndex = this.testDataListWidget.getSelectionIndex();

		ExportToTestDataDialog exportTestDataDialog = new ExportToTestDataDialog(
				this.getShell(), this.editorPart, this.diagram, testDataList,
				targetIndex);

		exportTestDataDialog.open();
	}

	private void editTestData(int selectedTableIndex) {
		int targetIndex = this.testDataListWidget.getSelectionIndex();
		if (targetIndex == -1) {
			return;
		}

		TestData oldTestData = this.testDataList.get(targetIndex);

		TestDataDialog testDataDialog = new TestDataDialog(this.getShell(),
				diagram, oldTestData);
		if (selectedTableIndex != -1) {
			testDataDialog.setSelectedTable(selectedTableIndex);
		}

		if (testDataDialog.open() == IDialogConstants.OK_ID) {
			TestData newTestData = testDataDialog.getTestData();

			this.testDataList.remove(targetIndex);
			this.testDataList.add(targetIndex, newTestData);

			this.initTestDataList();

			for (int i = 0; i < this.testDataList.size(); i++) {
				TestData testData = this.testDataList.get(i);

				if (testData == newTestData) {
					this.testDataListWidget.setSelection(i);
					break;
				}
			}

			initTableData();
		}
	}

	private void copyTestData() {
		int targetIndex = this.testDataListWidget.getSelectionIndex();
		if (targetIndex == -1) {
			return;
		}

		TestData oldTestData = this.testDataList.get(targetIndex);

		TestData copyTestData = oldTestData.clone();

		this.testDataList.add(copyTestData);

		this.initTestDataList();

		for (int i = 0; i < this.testDataList.size(); i++) {
			TestData testData = this.testDataList.get(i);

			if (testData == copyTestData) {
				this.testDataListWidget.setSelection(i);
				break;
			}
		}

		initTableData();
	}

	@Override
	protected void addListener() {
		super.addListener();

		this.addButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				addTestData();
			}
		});

		this.editButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				editTestData(testDataTable.getSelectionIndex());
			}
		});

		this.deleteButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int targetIndex = testDataListWidget.getSelectionIndex();
				if (targetIndex == -1) {
					return;
				}

				testDataList.remove(targetIndex);

				initTestDataList();

				if (targetIndex >= testDataList.size()) {
					targetIndex = testDataList.size() - 1;
				}

				testDataListWidget.setSelection(targetIndex);
				if (targetIndex == -1) {
					editButton.setEnabled(false);
					deleteButton.setEnabled(false);
					exportButton.setEnabled(false);
				}

				initTableData();
			}
		});

		this.copyButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				copyTestData();
			}
		});

		this.exportButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				exportTestData();
			}
		});

		this.testDataListWidget.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (testDataListWidget.getSelectionIndex() != -1) {
					initTableData();
					editButton.setEnabled(true);
					deleteButton.setEnabled(true);
					exportButton.setEnabled(true);
				}
			}
		});

		this.testDataListWidget.addMouseListener(new MouseAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				editTestData(testDataTable.getSelectionIndex());
			}
		});

		this.testDataTable.addMouseListener(new MouseAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				editTestData(testDataTable.getSelectionIndex());
			}
		});

	}
}
