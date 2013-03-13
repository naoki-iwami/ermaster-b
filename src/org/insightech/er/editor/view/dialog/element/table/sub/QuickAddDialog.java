package org.insightech.er.editor.view.dialog.element.table.sub;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.RowHeaderTable;
import org.insightech.er.common.widgets.table.CellEditWorker;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.CopyWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.view.dialog.testdata.detail.TestDataDialog;

public class QuickAddDialog extends AbstractDialog {

	private RowHeaderTable editColumnTable;

	private ERDiagram diagram;

	private List<NormalColumn> columnList;

	public QuickAddDialog(Shell parentShell, ERDiagram diagram) {
		super(parentShell);

		this.diagram = diagram;
		this.columnList = new ArrayList<NormalColumn>();
	}

	@Override
	protected void initialize(Composite composite) {
		Composite body = new Composite(composite, SWT.NONE);

		GridLayout gridLayout = new GridLayout();
		body.setLayout(gridLayout);

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.widthHint = 800;

		body.setLayoutData(gridData);

		this.createEditTable(body);
	}

	private void createEditTable(Composite composite) {
		this.editColumnTable = CompositeFactory.createRowHeaderTable(composite,
				700, TestDataDialog.TABLE_HEIGHT, 75, 25, 1, false, true);
		this.editColumnTable.setCellEditWorker(new CellEditWorker() {

			public void addNewRow() {
				addNewRowToTable();
			}

			public void changeRowNum() {
			}

			public boolean isModified(int row, int column) {
				return false;
			}

		});
	}

	private void addNewRowToTable() {
		this.editColumnTable.addRow("+", null);
	}

	@Override
	protected String getErrorMessage() {
		return null;
	}

	@Override
	protected String getTitle() {
		return "label.button.quick.add";
	}

	@Override
	protected void perfomeOK() throws InputException {
		for (int row = 0; row < this.editColumnTable.getItemCount() - 1; row++) {
			String logicalName = (String) this.editColumnTable.getValueAt(row,
					0);
			String physicalName = (String) this.editColumnTable.getValueAt(row,
					1);
			String type = (String) this.editColumnTable.getValueAt(row, 2);

			int length = 0;
			try {
				length = Integer.parseInt((String) this.editColumnTable
						.getValueAt(row, 3));
			} catch (NumberFormatException e) {
			}

			int decimal = 0;
			try {
				decimal = Integer.parseInt((String) this.editColumnTable
						.getValueAt(row, 4));
			} catch (NumberFormatException e) {
			}

			SqlType sqlType = SqlType.valueOf(this.diagram.getDatabase(), type,
					length);

			TypeData typeData = new TypeData(length, decimal, false, null,
					false, null);

			Word word = new CopyWord(new Word(physicalName, logicalName,
					sqlType, typeData, null, this.diagram.getDatabase()));

			NormalColumn column = new NormalColumn(word, false, false, false,
					false, null, null, null, null, null);

			this.columnList.add(column);
		}
	}

	@Override
	protected void setData() {
		this.initTable();
	}

	private void initTable() {
		this.editColumnTable.setVisible(false);

		this.editColumnTable.removeData();

		this.editColumnTable.addColumnHeader(ResourceString
				.getResourceString("label.logical.name"), 150);
		this.editColumnTable.addColumnHeader(ResourceString
				.getResourceString("label.physical.name"), 150);
		this.editColumnTable.addColumnHeader(ResourceString
				.getResourceString("label.column.type"), 100);
		this.editColumnTable.addColumnHeader(ResourceString
				.getResourceString("label.column.length"), 100);
		this.editColumnTable.addColumnHeader(ResourceString
				.getResourceString("label.column.decimal"), 100);

		this.addNewRowToTable();

		this.editColumnTable.setVisible(true);
	}

	public List<NormalColumn> getColumnList() {
		return columnList;
	}

}
