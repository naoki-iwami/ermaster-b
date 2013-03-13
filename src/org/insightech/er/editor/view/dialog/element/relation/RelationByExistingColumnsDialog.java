package org.insightech.er.editor.view.dialog.element.relation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.insightech.er.Activator;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.editor.view.dialog.element.relation.RelationDialog.ColumnComboInfo;
import org.insightech.er.util.Format;

public class RelationByExistingColumnsDialog extends AbstractDialog {

	private static final int COLUMN_WIDTH = 200;

	private Combo columnCombo;

	private Table comparisonTable;

	private ERTable source;

	private ColumnComboInfo columnComboInfo;

	private List<NormalColumn> candidateForeignKeyColumns;

	private List<NormalColumn> referencedColumnList;

	private List<NormalColumn> foreignKeyColumnList;

	private Map<NormalColumn, List<NormalColumn>> referencedMap;

	private boolean referenceForPK;

	private ComplexUniqueKey referencedComplexUniqueKey;

	private NormalColumn referencedColumn;

	private List<TableEditor> tableEditorList;

	private Map<TableEditor, List<NormalColumn>> editorReferencedMap;

	private Map<Relation, Set<NormalColumn>> foreignKeySetMap;

	public RelationByExistingColumnsDialog(Shell parentShell, ERTable source,
			List<NormalColumn> candidateForeignKeyColumns,
			Map<NormalColumn, List<NormalColumn>> referencedMap,
			Map<Relation, Set<NormalColumn>> foreignKeySetMap) {
		super(parentShell, 2);

		this.source = source;
		this.referencedColumnList = new ArrayList<NormalColumn>();
		this.foreignKeyColumnList = new ArrayList<NormalColumn>();

		this.candidateForeignKeyColumns = candidateForeignKeyColumns;
		this.referencedMap = referencedMap;
		this.foreignKeySetMap = foreignKeySetMap;

		this.tableEditorList = new ArrayList<TableEditor>();
		this.editorReferencedMap = new HashMap<TableEditor, List<NormalColumn>>();
	}

	@Override
	protected void initLayout(GridLayout layout) {
		super.initLayout(layout);

		layout.verticalSpacing = 20;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite composite) {
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;

		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(gridData);
		label
				.setText(ResourceString
						.getResourceString("dialog.message.create.relation.by.existing.columns"));

		this.createColumnCombo(composite);

		this.createComparisonTable(composite);
	}

	/**
	 * This method initializes combo
	 * 
	 */
	private void createColumnCombo(Composite composite) {
		Label label = new Label(composite, SWT.NONE);
		label.setText(ResourceString
				.getResourceString("label.reference.column"));

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		this.columnCombo = new Combo(composite, SWT.READ_ONLY);
		this.columnCombo.setLayoutData(gridData);

		this.columnCombo.setVisibleItemCount(20);
	}

	private void createComparisonTable(Composite composite) {
		GridData tableGridData = new GridData();
		tableGridData.horizontalSpan = 2;
		tableGridData.heightHint = 100;
		tableGridData.horizontalAlignment = GridData.FILL;
		tableGridData.grabExcessHorizontalSpace = true;

		this.comparisonTable = new Table(composite, SWT.SINGLE | SWT.BORDER
				| SWT.FULL_SELECTION);
		this.comparisonTable.setLayoutData(tableGridData);
		this.comparisonTable.setHeaderVisible(true);
		this.comparisonTable.setLinesVisible(true);

		TableColumn referencedColumn = new TableColumn(this.comparisonTable,
				SWT.NONE);
		referencedColumn.setWidth(COLUMN_WIDTH);
		referencedColumn.setText(ResourceString
				.getResourceString("label.reference.column"));

		TableColumn foreignKeyColumn = new TableColumn(this.comparisonTable,
				SWT.NONE);
		foreignKeyColumn.setWidth(COLUMN_WIDTH);
		foreignKeyColumn.setText(ResourceString
				.getResourceString("label.foreign.key"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setData() {
		this.columnComboInfo = RelationDialog.setReferencedColumnComboData(
				this.columnCombo, this.source);

		this.columnCombo.select(0);

		this.createComparisonTableRows();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void perfomeOK() {
		int index = this.columnCombo.getSelectionIndex();

		if (index < this.columnComboInfo.complexUniqueKeyStartIndex) {
			this.referenceForPK = true;

		} else if (index < this.columnComboInfo.columnStartIndex) {
			ComplexUniqueKey complexUniqueKey = this.source
					.getComplexUniqueKeyList()
					.get(
							index
									- this.columnComboInfo.complexUniqueKeyStartIndex);

			this.referencedComplexUniqueKey = complexUniqueKey;

		} else {
			this.referencedColumn = this.columnComboInfo.candidateColumns
					.get(index - this.columnComboInfo.columnStartIndex);
		}

		for (TableEditor tableEditor : this.tableEditorList) {
			NormalColumn foreignKeyColumn = this.getSelectedColumn(tableEditor);
			this.foreignKeyColumnList.add(foreignKeyColumn);
		}
	}

	private NormalColumn getSelectedColumn(TableEditor tableEditor) {
		Combo foreignKeyCombo = (Combo) tableEditor.getEditor();
		int foreignKeyComboIndex = foreignKeyCombo.getSelectionIndex();
		int startIndex = 1;

		NormalColumn foreignKeyColumn = null;

		List<NormalColumn> foreignKeyList = this.editorReferencedMap
				.get(tableEditor);
		if (foreignKeyList != null) {
			if (foreignKeyComboIndex <= foreignKeyList.size()) {
				foreignKeyColumn = foreignKeyList.get(foreignKeyComboIndex
						- startIndex);
			} else {
				startIndex += foreignKeyList.size();
			}
		}

		if (foreignKeyColumn == null) {
			foreignKeyColumn = this.candidateForeignKeyColumns
					.get(foreignKeyComboIndex - startIndex);
		}

		return foreignKeyColumn;
	}

	@Override
	protected String getErrorMessage() {
		Set<NormalColumn> selectedColumns = new HashSet<NormalColumn>();

		for (TableEditor tableEditor : this.tableEditorList) {
			Combo foreignKeyCombo = (Combo) tableEditor.getEditor();
			int index = foreignKeyCombo.getSelectionIndex();

			if (index == 0) {
				return "error.foreign.key.not.selected";
			}

			NormalColumn selectedColumn = this.getSelectedColumn(tableEditor);
			if (selectedColumns.contains(selectedColumn)) {
				return "error.foreign.key.must.be.different";
			}

			selectedColumns.add(selectedColumn);
		}

		if (this.existForeignKeySet(selectedColumns)) {
			return "error.foreign.key.already.exist";
		}

		return null;
	}

	private boolean existForeignKeySet(Set<NormalColumn> columnSet) {
		boolean exist = false;

		for (Set<NormalColumn> foreignKeySet : this.foreignKeySetMap.values()) {
			if (foreignKeySet.size() == columnSet.size()) {
				exist = true;

				for (NormalColumn normalColumn : columnSet) {
					if (!foreignKeySet.contains(normalColumn)) {
						exist = false;
						continue;
					}
				}

				break;
			}
		}

		return exist;
	}

	@Override
	protected void addListener() {
		super.addListener();

		this.columnCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				comparisonTable.removeAll();
				disposeTableEditor();
				createComparisonTableRows();
				validate();
			}

		});

		this.comparisonTable.addListener(SWT.MeasureItem, new Listener() {

			public void handleEvent(Event event) {
				event.height = columnCombo.getSize().y;
				;
			}

		});
	}

	private void createComparisonTableRows() {
		try {
			int index = this.columnCombo.getSelectionIndex();

			if (index < this.columnComboInfo.complexUniqueKeyStartIndex) {
				this.referencedColumnList = this.source.getPrimaryKeys();

			} else if (index < this.columnComboInfo.columnStartIndex) {
				ComplexUniqueKey complexUniqueKey = this.source
						.getComplexUniqueKeyList()
						.get(
								index
										- this.columnComboInfo.complexUniqueKeyStartIndex);

				this.referencedColumnList = complexUniqueKey.getColumnList();

			} else {
				NormalColumn referencedColumn = this.columnComboInfo.candidateColumns
						.get(index - this.columnComboInfo.columnStartIndex);

				this.referencedColumnList = new ArrayList<NormalColumn>();
				this.referencedColumnList.add(referencedColumn);
			}

			for (NormalColumn referencedColumn : this.referencedColumnList) {
				this.column2TableItem(referencedColumn);
			}

		} catch (Exception e) {
			Activator.showExceptionDialog(e);
		}
	}

	private void column2TableItem(NormalColumn referencedColumn) {
		TableItem tableItem = new TableItem(this.comparisonTable, SWT.NONE);

		tableItem.setText(0, Format.null2blank(referencedColumn
				.getLogicalName()));

		List<NormalColumn> foreignKeyList = this.referencedMap
				.get(referencedColumn.getRootReferencedColumn());

		TableEditor tableEditor = new TableEditor(this.comparisonTable);
		tableEditor.grabHorizontal = true;

		tableEditor.setEditor(this.createForeignKeyCombo(foreignKeyList),
				tableItem, 1);
		this.tableEditorList.add(tableEditor);
		this.editorReferencedMap.put(tableEditor, foreignKeyList);
	}

	protected Combo createForeignKeyCombo(List<NormalColumn> foreignKeyList) {
		Combo foreignKeyCombo = CompositeFactory.createReadOnlyCombo(this,
				this.comparisonTable, null);

		foreignKeyCombo.add("");

		if (foreignKeyList != null) {
			for (NormalColumn normalColumn : foreignKeyList) {
				foreignKeyCombo.add(Format.toString(normalColumn.getName()));
			}
		}

		for (NormalColumn normalColumn : this.candidateForeignKeyColumns) {
			foreignKeyCombo.add(Format.toString(normalColumn.getName()));
		}

		if (foreignKeyCombo.getItemCount() > 0) {
			foreignKeyCombo.select(0);
		}

		return foreignKeyCombo;
	}

	@Override
	public boolean close() {
		this.disposeTableEditor();

		return super.close();
	}

	private void disposeTableEditor() {
		for (TableEditor tableEditor : this.tableEditorList) {
			tableEditor.getEditor().dispose();
			tableEditor.dispose();
		}

		this.tableEditorList.clear();
		this.editorReferencedMap.clear();
	}

	public List<NormalColumn> getReferencedColumnList() {
		return referencedColumnList;
	}

	public List<NormalColumn> getForeignKeyColumnList() {
		return foreignKeyColumnList;
	}

	public boolean isReferenceForPK() {
		return referenceForPK;
	}

	public ComplexUniqueKey getReferencedComplexUniqueKey() {
		return referencedComplexUniqueKey;
	}

	public NormalColumn getReferencedColumn() {
		return this.referencedColumn;
	}

	@Override
	protected String getTitle() {
		return "dialog.title.relation";
	}
}
