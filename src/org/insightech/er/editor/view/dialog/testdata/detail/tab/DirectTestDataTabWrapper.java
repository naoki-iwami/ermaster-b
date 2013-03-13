package org.insightech.er.editor.view.dialog.testdata.detail.tab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.RowHeaderTable;
import org.insightech.er.common.widgets.ValidatableTabWrapper;
import org.insightech.er.common.widgets.table.CellEditWorker;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.testdata.DirectTestData;
import org.insightech.er.editor.view.dialog.testdata.detail.TestDataDialog;
import org.insightech.er.util.Format;

public class DirectTestDataTabWrapper extends ValidatableTabWrapper {

	private TestDataDialog dialog;

	private RowHeaderTable editColumnTable;

	private DirectTestData directTestData;

	private ERTable table;

	public DirectTestDataTabWrapper(TestDataDialog dialog, TabFolder parent,
			int style) {
		super(dialog, parent, style, "label.testdata.direct.input");

		this.dialog = dialog;

		this.init();
	}

	@Override
	public void initComposite() {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		this.setLayout(layout);

		Text dummy = CompositeFactory.createNumText(dialog, this, "", 50);
		dummy.setVisible(false);

		this.createEditTable(this);
	}

	private void createEditTable(Composite composite) {
		this.editColumnTable = CompositeFactory.createRowHeaderTable(composite,
				TestDataDialog.WIDTH - 20, TestDataDialog.TABLE_HEIGHT, 75, 25,
				2, false, true);
		this.editColumnTable.setCellEditWorker(new CellEditWorker() {

			public void addNewRow() {
				addNewRowToTable();
			}

			public void changeRowNum() {
				dialog.resetTestDataNum();
			}

			public boolean isModified(int row, int column) {
				return false;
			}

		});
	}

	@Override
	protected void setData() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validatePage() throws InputException {
		this.saveTableData();
	}

	@Override
	public void setInitFocus() {
	}

	@Override
	public void reset() {
		saveTableData();

		this.table = dialog.getTargetTable();
		this.directTestData = dialog.getTestData().getTableTestDataMap().get(
				this.table).getDirectTestData();

		// �e�[�u���ύX
		this.initTable();
	}

	private void saveTableData() {
		if (this.directTestData != null) {
			List<Map<NormalColumn, String>> dataList = new ArrayList<Map<NormalColumn, String>>();

			List<NormalColumn> normalColumnList = this.table
					.getExpandedColumns();

			for (int row = 0; row < this.editColumnTable.getItemCount() - 1; row++) {
				Map<NormalColumn, String> data = new HashMap<NormalColumn, String>();

				for (int column = 0; column < normalColumnList.size(); column++) {
					NormalColumn normalColumn = normalColumnList.get(column);
					String value = (String) this.editColumnTable.getValueAt(
							row, column);
					data.put(normalColumn, value);
				}

				dataList.add(data);
			}

			this.directTestData.setDataList(dataList);
		}
	}

	private void initTable() {
		this.editColumnTable.setVisible(false);

		this.editColumnTable.removeData();

		for (NormalColumn normalColumn : this.table.getExpandedColumns()) {
			String name = normalColumn.getName();
			String type = null;

			if (normalColumn.getType() == null) {
				type = "";

			} else {
				type = Format.formatType(normalColumn.getType(), normalColumn
						.getTypeData(), this.dialog.getDiagram().getDatabase());
			}

			this.editColumnTable.addColumnHeader(name + "\r\n" + type, 100);
		}

		for (Map<NormalColumn, String> data : directTestData.getDataList()) {
			this.addTableItem(data);
		}

		// ��s
		this.addNewRowToTable();

		this.editColumnTable.setVisible(true);
	}

	private void addNewRowToTable() {
		this.editColumnTable.addRow("+", null);
	}

	private void addTableItem(Map<NormalColumn, String> data) {
		List<NormalColumn> columns = this.table.getExpandedColumns();

		String[] values = new String[columns.size()];

		for (int i = 0; i < columns.size(); i++) {
			values[i] = data.get(columns.get(i));
		}

		this.editColumnTable.addRow(String.valueOf(this.editColumnTable
				.getItemCount() + 1), values);
	}

	@Override
	public void perfomeOK() {
	}

	public int getTestDataNum() {
		return this.editColumnTable.getItemCount() - 1;
	}
}
