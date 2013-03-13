package org.insightech.er.editor.view.dialog.element.table.tab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.Activator;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.ValidatableTabWrapper;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.CopyComplexUniqueKey;
import org.insightech.er.editor.view.dialog.common.ERTableComposite;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public class ComplexUniqueKeyTabWrapper extends ValidatableTabWrapper {

	private ERTable copyData;

	private Text nameText;

	private Combo complexUniqueKeyCombo;

	private Table columnTable;

	private Button addButton;

	private Button updateButton;

	private Button deleteButton;

	private List<TableEditor> tableEditorList;

	private Map<TableEditor, NormalColumn> editorColumnMap;

	public ComplexUniqueKeyTabWrapper(AbstractDialog dialog, TabFolder parent,
			int style, ERTable copyData) {
		super(dialog, parent, style, "label.complex.unique.key");

		this.copyData = copyData;
		this.tableEditorList = new ArrayList<TableEditor>();
		this.editorColumnMap = new HashMap<TableEditor, NormalColumn>();

		this.init();
	}

	@Override
	public void initComposite() {
		GridLayout gridLayout = new GridLayout();
		// gridLayout.verticalSpacing = 20;
		this.setLayout(gridLayout);

		this.complexUniqueKeyCombo = CompositeFactory.createReadOnlyCombo(null,
				this, "label.complex.unique.key");

		this.nameText = CompositeFactory.createText(null, this,
				"label.unique.key.name", false);

		CompositeFactory.filler(this, 1);

		this.columnTable = CompositeFactory.createTable(this, 200, 1);

		CompositeFactory.createTableColumn(this.columnTable,
				"label.logical.name", ERTableComposite.NAME_WIDTH, SWT.NONE);
		CompositeFactory
				.createTableColumn(this.columnTable, "label.unique.key",
						ERTableComposite.UNIQUE_KEY_WIDTH, SWT.NONE);

		GridLayout buttonGridLayout = new GridLayout();
		buttonGridLayout.numColumns = 3;

		Composite buttonComposite = new Composite(this, SWT.NONE);
		buttonComposite.setLayout(buttonGridLayout);

		this.addButton = CompositeFactory.createButton(buttonComposite,
				"label.button.add");
		this.updateButton = CompositeFactory.createButton(buttonComposite,
				"label.button.update");
		this.deleteButton = CompositeFactory.createButton(buttonComposite,
				"label.button.delete");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validatePage() throws InputException {
	}

	@Override
	public void setInitFocus() {
		this.complexUniqueKeyCombo.setFocus();
	}

	public void restruct() {
		this.columnTable.removeAll();

		this.disposeTableEditor();

		for (NormalColumn normalColumn : this.copyData.getNormalColumns()) {
			TableItem tableItem = new TableItem(this.columnTable, SWT.NONE);
			tableItem.setText(0, Format.null2blank(normalColumn.getName()));

			TableEditor tableEditor = CompositeFactory
					.createCheckBoxTableEditor(tableItem, false, 1);
			this.tableEditorList.add(tableEditor);
			this.editorColumnMap.put(tableEditor, normalColumn);
		}

		this.setComboData();
		this.setButtonStatus(false);
	}

	@Override
	public void dispose() {
		this.disposeTableEditor();
		super.dispose();
	}

	private void disposeTableEditor() {
		for (TableEditor tableEditor : this.tableEditorList) {
			tableEditor.getEditor().dispose();
			tableEditor.dispose();
		}

		this.tableEditorList.clear();
		this.editorColumnMap.clear();
	}

	@Override
	protected void addListener() {
		super.addListener();

		this.complexUniqueKeyCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				checkSelectedKey();
			}
		});

		this.addButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String name = nameText.getText().trim();

				if (!"".equals(name)) {
					if (!Check.isAlphabet(name)) {
						Activator
								.showErrorDialog("error.unique.key.name.not.alphabet");
						return;
					}
				}

				List<NormalColumn> columnList = new ArrayList<NormalColumn>();

				for (TableEditor tableEditor : tableEditorList) {
					Button checkBox = (Button) tableEditor.getEditor();
					if (checkBox.getSelection()) {
						columnList.add(editorColumnMap.get(tableEditor));
					}
				}

				if (columnList.isEmpty()) {
					Activator
							.showErrorDialog("error.not.checked.complex.unique.key.columns");
					return;
				}

				if (contains(columnList) != null) {
					Activator
							.showErrorDialog("error.already.exist.complex.unique.key");
					return;
				}

				ComplexUniqueKey complexUniqueKey = new CopyComplexUniqueKey(
						new ComplexUniqueKey(name), null);
				complexUniqueKey.setColumnList(columnList);
				copyData.getComplexUniqueKeyList().add(complexUniqueKey);
				addComboData(complexUniqueKey);
				complexUniqueKeyCombo.select(complexUniqueKeyCombo
						.getItemCount() - 1);
				setButtonStatus(true);
			}

		});

		this.updateButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = complexUniqueKeyCombo.getSelectionIndex();
				if (index == -1) {
					return;
				}

				String name = nameText.getText().trim();

				if (!Check.isAlphabet(name)) {
					Activator
							.showErrorDialog("error.unique.key.name.not.alphabet");
					return;
				}

				ComplexUniqueKey complexUniqueKey = copyData
						.getComplexUniqueKeyList().get(index);

				List<NormalColumn> columnList = new ArrayList<NormalColumn>();

				for (TableEditor tableEditor : tableEditorList) {
					Button checkBox = (Button) tableEditor.getEditor();
					if (checkBox.getSelection()) {
						columnList.add(editorColumnMap.get(tableEditor));
					}
				}

				if (columnList.isEmpty()) {
					Activator
							.showErrorDialog("error.not.checked.complex.unique.key.columns");
					return;
				}

				ComplexUniqueKey sameKey = contains(columnList);
				if (sameKey != null && sameKey != complexUniqueKey) {
					Activator
							.showErrorDialog("error.already.exist.complex.unique.key");
					return;
				}

				complexUniqueKey.setUniqueKeyName(name);
				complexUniqueKey.setColumnList(columnList);
				complexUniqueKeyCombo.remove(index);
				complexUniqueKeyCombo.add(complexUniqueKey.getLabel(), index);
				complexUniqueKeyCombo.select(index);
			}
		});

		this.deleteButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = complexUniqueKeyCombo.getSelectionIndex();
				if (index == -1) {
					return;
				}

				complexUniqueKeyCombo.remove(index);
				copyData.getComplexUniqueKeyList().remove(index);

				if (index < copyData.getComplexUniqueKeyList().size()) {
					complexUniqueKeyCombo.select(index);
				} else {
					complexUniqueKeyCombo.select(index - 1);
				}

				checkSelectedKey();
			}
		});
	}

	private void checkSelectedKey() {
		int index = complexUniqueKeyCombo.getSelectionIndex();

		ComplexUniqueKey complexUniqueKey = null;
		String name = null;

		if (index != -1) {
			complexUniqueKey = copyData.getComplexUniqueKeyList().get(index);
			name = complexUniqueKey.getUniqueKeyName();

			setButtonStatus(true);

		} else {
			setButtonStatus(false);
		}

		nameText.setText(Format.null2blank(name));

		for (TableEditor tableEditor : tableEditorList) {
			Button checkbox = (Button) tableEditor.getEditor();

			NormalColumn column = editorColumnMap.get(tableEditor);
			if (complexUniqueKey != null
					&& complexUniqueKey.getColumnList().contains(column)) {
				checkbox.setSelection(true);
			} else {
				checkbox.setSelection(false);
			}
		}
	}

	public ComplexUniqueKey contains(List<NormalColumn> columnList) {
		for (ComplexUniqueKey complexUniqueKey : this.copyData
				.getComplexUniqueKeyList()) {
			if (columnList.size() == complexUniqueKey.getColumnList().size()) {
				boolean exist = true;
				for (NormalColumn column : columnList) {
					if (!complexUniqueKey.getColumnList().contains(column)) {
						exist = false;
						break;
					}
				}

				if (exist) {
					return complexUniqueKey;
				}
			}
		}

		return null;
	}

	private void setComboData() {
		this.complexUniqueKeyCombo.removeAll();

		for (Iterator<ComplexUniqueKey> iter = this.copyData
				.getComplexUniqueKeyList().iterator(); iter.hasNext();) {
			ComplexUniqueKey complexUniqueKey = iter.next();

			if (complexUniqueKey.isRemoved(this.copyData.getNormalColumns())) {
				iter.remove();
			} else {
				this.addComboData(complexUniqueKey);
			}
		}
	}

	private void addComboData(ComplexUniqueKey complexUniqueKey) {
		this.complexUniqueKeyCombo.add(complexUniqueKey.getLabel());
	}

	private void setButtonStatus(boolean enabled) {
		if (enabled) {
			if (this.copyData.getComplexUniqueKeyList().get(
					this.complexUniqueKeyCombo.getSelectionIndex())
					.isReferenced(copyData)) {
				enabled = false;
			}
		}

		this.updateButton.setEnabled(enabled);
		this.deleteButton.setEnabled(enabled);
	}

	@Override
	public void perfomeOK() {
	}
}
