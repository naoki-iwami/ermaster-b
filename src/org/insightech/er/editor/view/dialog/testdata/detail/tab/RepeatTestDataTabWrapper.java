package org.insightech.er.editor.view.dialog.testdata.detail.tab;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.RowHeaderTable;
import org.insightech.er.common.widgets.ValidatableTabWrapper;
import org.insightech.er.common.widgets.table.CellEditWorker;
import org.insightech.er.common.widgets.table.HeaderClickListener;
import org.insightech.er.editor.model.dbexport.testdata.TestDataCreator;
import org.insightech.er.editor.model.dbexport.testdata.impl.SQLTestDataCreator;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.testdata.RepeatTestData;
import org.insightech.er.editor.model.testdata.RepeatTestDataDef;
import org.insightech.er.editor.view.dialog.testdata.detail.RepeatTestDataSettingDialog;
import org.insightech.er.editor.view.dialog.testdata.detail.TestDataDialog;
import org.insightech.er.util.Format;

public class RepeatTestDataTabWrapper extends ValidatableTabWrapper {

	private static final int MAX_REPEAT_PREVIEW_NUM = 50;

	private TestDataDialog dialog;

	private Text testDataNumText;

	private RowHeaderTable editColumnTable;

	private RepeatTestData repeatTestData;

	private ERTable table;

	public RepeatTestDataTabWrapper(TestDataDialog dialog, TabFolder parent,
			int style) {
		super(dialog, parent, style, "label.testdata.repeat.input");

		this.dialog = dialog;

		this.init();
	}

	@Override
	public void initComposite() {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		this.setLayout(layout);

		this.testDataNumText = CompositeFactory.createNumText(this.dialog,
				this, "label.record.num", 50);
		this.createEditTable(this);
	}

	private void createEditTable(Composite composite) {
		this.editColumnTable = CompositeFactory.createRowHeaderTable(composite,
				TestDataDialog.WIDTH - 20, TestDataDialog.TABLE_HEIGHT, 75, 25,
				2, true, true);

		this.editColumnTable.setCellEditWorker(new CellEditWorker() {

			public void addNewRow() {
			}

			public void changeRowNum() {
				dialog.resetTestDataNum();
			}

			public boolean isModified(int row, int column) {
				TestDataCreator testDataCreator = new SQLTestDataCreator();
				testDataCreator.init(dialog.getTestData());

				if (column >= table.getExpandedColumns().size()) {
					return false;
				}

				NormalColumn normalColumn = table.getExpandedColumns().get(
						column);

				RepeatTestDataDef dataDef = repeatTestData
						.getDataDef(normalColumn);

				String defaultValue = testDataCreator.getRepeatTestDataValue(
						row, dataDef, normalColumn);
				Object value = editColumnTable.getValueAt(row, column);

				if (defaultValue == null) {
					defaultValue = "null";
				}
				if (value == null) {
					value = "null";
				}

				if (!defaultValue.equals(value)) {
					dataDef.setModifiedValue(row, value.toString());
					return true;

				} else {
					dataDef.removeModifiedValue(row);
				}

				return false;
			}

		});

		this.editColumnTable.setHeaderClickListener(new HeaderClickListener() {

			public void onHeaderClick(final int column) {
				getDisplay().asyncExec(new Runnable() {
					public void run() {
						RepeatTestDataSettingDialog dialog = new RepeatTestDataSettingDialog(
								getShell(), column,
								RepeatTestDataTabWrapper.this, table);
						dialog.open();
					}
				});
			}
		});
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

		this.initTableData();

		this.editColumnTable.setVisible(true);
	}

	@Override
	public void reset() {
		if (this.repeatTestData != null) {
			this.perfomeOK();
		}

		this.table = dialog.getTargetTable();

		this.repeatTestData = dialog.getTestData().getTableTestDataMap().get(
				this.table).getRepeatTestData();
		this.testDataNumText.setText(Format.toString(this.repeatTestData
				.getTestDataNum()));

		// �e�[�u���ύX
		this.initTable();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validatePage() throws InputException {
	}

	@Override
	public void setInitFocus() {
	}

	@Override
	public void perfomeOK() {
		if (this.repeatTestData != null) {
			this.repeatTestData.setTestDataNum(this.getTestDataNum());
		}
	}

	@Override
	protected void addListener() {
		super.addListener();

		this.testDataNumText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent modifyevent) {
				initTableData();
			}
		});
	}

	public void initTableData() {
		if (this.table != null) {
			this.editColumnTable.setVisible(false);

			TestDataCreator testDataCreator = new SQLTestDataCreator();
			testDataCreator.init(dialog.getTestData());

			this.editColumnTable.removeAllRow();

			int num = this.getTestDataNum();

			if (num > MAX_REPEAT_PREVIEW_NUM) {
				num = MAX_REPEAT_PREVIEW_NUM;
			}

			for (int i = 0; i < num; i++) {
				Object[] values = new Object[this.table.getExpandedColumns()
						.size()];

				int columnIndex = 0;

				for (NormalColumn column : this.table.getExpandedColumns()) {
					values[columnIndex++] = testDataCreator
							.getMergedRepeatTestDataValue(i, repeatTestData
									.getDataDef(column), column);
				}

				this.editColumnTable.addRow(String.valueOf(this.editColumnTable
						.getItemCount() + 1), values);
			}

			this.editColumnTable.setVisible(true);
		}
	}

	public void setRepeatTestDataDef(NormalColumn column,
			RepeatTestDataDef repeatTestDataDef) {
		this.repeatTestData.setDataDef(column, repeatTestDataDef);
	}

	public RepeatTestData getRepeatTestData() {
		return repeatTestData;
	}

	public int getTestDataNum() {
		String text = testDataNumText.getText();
		int num = 0;
		if (!text.equals("")) {
			try {
				num = Integer.parseInt(text);
			} catch (Exception e) {
			}
		}

		return num;
	}
}
