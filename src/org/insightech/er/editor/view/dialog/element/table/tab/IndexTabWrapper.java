package org.insightech.er.editor.view.dialog.element.table.tab;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.ValidatableTabWrapper;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.CopyIndex;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.view.dialog.common.ERTableComposite;
import org.insightech.er.editor.view.dialog.element.table.sub.IndexDialog;
import org.insightech.er.util.Format;

public class IndexTabWrapper extends ValidatableTabWrapper {

	private static final int BUTTON_WIDTH = 60;

	private ERTable copyData;

	private Table indexTable;

	private List<Button> checkButtonList;

	private List<TableEditor> editorList;

	private Button addButton;

	private Button editButton;

	private Button deleteButton;

	public IndexTabWrapper(AbstractDialog dialog, TabFolder parent, int style,
			ERTable copyData) {
		super(dialog, parent, style, "label.index");

		this.copyData = copyData;

		this.checkButtonList = new ArrayList<Button>();
		this.editorList = new ArrayList<TableEditor>();

		this.init();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validatePage() throws InputException {
		this.resutuctIndexData();
	}

	@Override
	public void initComposite() {
		this.setLayout(new GridLayout());

		Composite content = new Composite(this, SWT.BORDER);

		this.createBody(content);
	}

	private void createBody(Composite content) {
		GridData contentGridData = new GridData();
		contentGridData.horizontalAlignment = GridData.FILL;
		contentGridData.grabExcessHorizontalSpace = true;

		content.setLayoutData(contentGridData);

		content.setLayout(new GridLayout(3, false));

		this.initTable(content);
		this.initTableButton(content);

		this.setTableData();
	}

	private void initTable(Composite parent) {
		GridData gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.heightHint = 200;

		this.indexTable = new Table(parent, SWT.BORDER | SWT.HIDE_SELECTION);

		this.indexTable.setHeaderVisible(true);
		this.indexTable.setLayoutData(gridData);
		this.indexTable.setLinesVisible(true);

		CompositeFactory.createTableColumn(this.indexTable,
				"label.column.name", ERTableComposite.NAME_WIDTH, SWT.NONE);
		TableColumn separatorColumn = CompositeFactory.createTableColumn(
				this.indexTable, "", 3, SWT.NONE);
		separatorColumn.setResizable(false);
	}

	private void initTableButton(Composite parent) {
		GridData gridData = new GridData();
		gridData.widthHint = BUTTON_WIDTH;

		this.addButton = new Button(parent, SWT.NONE);
		this.addButton.setText(ResourceString
				.getResourceString("label.button.add"));
		this.addButton.setLayoutData(gridData);

		this.editButton = new Button(parent, SWT.NONE);
		this.editButton.setText(ResourceString
				.getResourceString("label.button.edit"));
		this.editButton.setLayoutData(gridData);

		this.deleteButton = new Button(parent, SWT.NONE);
		this.deleteButton.setText(ResourceString
				.getResourceString("label.button.delete"));
		this.deleteButton.setLayoutData(gridData);
	}

	@Override
	protected void addListener() {
		this.addButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				IndexDialog dialog = new IndexDialog(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell(), null, copyData);

				if (dialog.open() == IDialogConstants.OK_ID) {
					addIndexData(dialog.getResultIndex(), true);
				}
			}
		});

		this.editButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				Index targetIndex = getTargetIndex();
				if (targetIndex == null) {
					return;
				}

				IndexDialog dialog = new IndexDialog(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell(), targetIndex,
						copyData);

				if (dialog.open() == IDialogConstants.OK_ID) {
					addIndexData(dialog.getResultIndex(), false);
				}
			}
		});

		this.deleteButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				removeIndex();
			}
		});
	}

	private void setTableData() {
		List<Index> indexes = this.copyData.getIndexes();

		TableItem radioTableItem = new TableItem(this.indexTable, SWT.NONE);

		for (int i = 0; i < indexes.size(); i++) {
			TableColumn tableColumn = new TableColumn(this.indexTable,
					SWT.CENTER);
			tableColumn.setWidth(60);
			tableColumn.setResizable(false);
			tableColumn.setText("Index" + (i + 1));

			TableEditor editor = new TableEditor(this.indexTable);

			Button radioButton = new Button(this.indexTable, SWT.RADIO);
			radioButton.addSelectionListener(new SelectionAdapter() {

				/**
				 * {@inheritDoc}
				 */
				@Override
				public void widgetSelected(SelectionEvent event) {
					setButtonEnabled(true);
				}
			});

			radioButton.pack();

			editor.minimumWidth = radioButton.getSize().x;
			editor.horizontalAlignment = SWT.CENTER;
			editor.setEditor(radioButton, radioTableItem, i + 2);

			this.checkButtonList.add(radioButton);
			this.editorList.add(editor);
		}

		for (NormalColumn normalColumn : this.copyData.getExpandedColumns()) {
			TableItem tableItem = new TableItem(this.indexTable, SWT.NONE);
			tableItem.setText(0, Format.null2blank(normalColumn.getName()));

			for (int i = 0; i < indexes.size(); i++) {
				Index index = indexes.get(i);

				List<NormalColumn> indexColumns = index.getColumns();
				for (int j = 0; j < indexColumns.size(); j++) {
					NormalColumn indexColumn = indexColumns.get(j);

					if (normalColumn.equals(indexColumn)) {
						tableItem.setText(i + 2, String.valueOf(j + 1));
						break;
					}
				}
			}
		}

		setButtonEnabled(false);
	}

	public void addIndexData(Index index, boolean add) {
		int selectedIndex = -1;

		for (int i = 0; i < this.checkButtonList.size(); i++) {
			Button checkButton = this.checkButtonList.get(i);
			if (checkButton.getSelection()) {
				selectedIndex = i;
				break;
			}
		}

		Index copyIndex = null;

		if (add || selectedIndex == -1) {
			copyIndex = new CopyIndex(copyData, index, null);
			this.copyData.addIndex(copyIndex);

		} else {
			copyIndex = this.copyData.getIndex(selectedIndex);
			CopyIndex.copyData(index, copyIndex);

		}

		this.restruct();
	}

	public void removeIndex() {
		int selectedIndex = -1;

		for (int i = 0; i < this.checkButtonList.size(); i++) {
			Button checkButton = this.checkButtonList.get(i);
			if (checkButton.getSelection()) {
				selectedIndex = i;
				break;
			}
		}

		if (selectedIndex == -1) {
			return;
		}

		this.copyData.removeIndex(selectedIndex);

		this.restruct();
	}

	public void restruct() {
		this.clearButtonAndEditor();

		while (this.indexTable.getColumnCount() > 2) {
			TableColumn tableColumn = this.indexTable.getColumn(2);
			tableColumn.dispose();
		}

		this.indexTable.removeAll();

		this.resutuctIndexData();

		this.setTableData();
	}

	private void resutuctIndexData() {
		for (Index index : this.copyData.getIndexes()) {
			List<NormalColumn> indexColumns = index.getColumns();

			Iterator<NormalColumn> columnIterator = indexColumns.iterator();
			Iterator<Boolean> descIterator = index.getDescs().iterator();

			while (columnIterator.hasNext()) {
				NormalColumn indexColumn = columnIterator.next();
				descIterator.next();

				if (!this.copyData.getExpandedColumns().contains(indexColumn)) {
					columnIterator.remove();
					descIterator.remove();
				}
			}
		}
	}

	private void clearButtonAndEditor() {
		for (Button checkButton : this.checkButtonList) {
			checkButton.dispose();
		}

		this.checkButtonList.clear();

		for (TableEditor editor : this.editorList) {
			editor.dispose();
		}

		this.editorList.clear();
	}

	public Index getTargetIndex() {
		int selectedIndex = -1;

		for (int i = 0; i < this.checkButtonList.size(); i++) {
			Button checkButton = this.checkButtonList.get(i);
			if (checkButton.getSelection()) {
				selectedIndex = i;
				break;
			}
		}

		if (selectedIndex == -1) {
			return null;
		}

		return this.copyData.getIndex(selectedIndex);
	}

	private void setButtonEnabled(boolean enabled) {
		this.editButton.setEnabled(enabled);
		this.deleteButton.setEnabled(enabled);
	}

	@Override
	public void setInitFocus() {
	}

	@Override
	public void perfomeOK() {
	}

}
