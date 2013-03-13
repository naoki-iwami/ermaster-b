package org.insightech.er.editor.model.dbexport.testdata;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.settings.export.ExportTestDataSetting;
import org.insightech.er.editor.model.testdata.DirectTestData;
import org.insightech.er.editor.model.testdata.RepeatTestData;
import org.insightech.er.editor.model.testdata.RepeatTestDataDef;
import org.insightech.er.editor.model.testdata.TableTestData;
import org.insightech.er.editor.model.testdata.TestData;

public abstract class TestDataCreator {

	protected ERDiagram diagram;

	protected ExportTestDataSetting exportTestDataSetting;

	protected TestData testData;

	protected Map<NormalColumn, List<String>> valueListMap;

	public TestDataCreator() {
	}

	public void init(TestData testData) {
		this.testData = testData;
		this.valueListMap = new HashMap<NormalColumn, List<String>>();
	}

	public String getMergedRepeatTestDataValue(int count,
			RepeatTestDataDef repeatTestDataDef, NormalColumn column) {
		String modifiedValue = repeatTestDataDef.getModifiedValues().get(count);

		if (modifiedValue != null) {
			return modifiedValue;

		} else {
			String value = this.getRepeatTestDataValue(count,
					repeatTestDataDef, column);
			if (value == null) {
				return "null";
			}

			return value;
		}
	}

	public String getRepeatTestDataValue(int count,
			RepeatTestDataDef repeatTestDataDef, NormalColumn column) {
		if (repeatTestDataDef == null) {
			return null;
		}

		String type = repeatTestDataDef.getType();

		int repeatNum = repeatTestDataDef.getRepeatNum();

		if (RepeatTestDataDef.TYPE_FORMAT.equals(type)) {
			int from = repeatTestDataDef.getFrom();
			int increment = repeatTestDataDef.getIncrement();
			int to = repeatTestDataDef.getTo();

			String template = repeatTestDataDef.getTemplate();

			int no = from;

			if (repeatNum != 0 && to - from + 1 != 0) {
				no = from
						+ (((count / repeatNum) * increment) % (to - from + 1));

			}

			String value = template.replaceAll("%", String.valueOf(no));
			if (column.getType() != null && column.getType().isTimestamp()) {
				SimpleDateFormat format1 = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss.SSS");

				try {
					value = format1.format(format1.parse(value));

				} catch (ParseException e1) {
					SimpleDateFormat format2 = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");

					try {
						value = format2.format(format2.parse(value));

					} catch (ParseException e2) {
						SimpleDateFormat format3 = new SimpleDateFormat(
								"yyyy-MM-dd");

						try {
							value = format3.format(format3.parse(value));

						} catch (ParseException e3) {
						}
					}

				}

			}

			return value;

		} else if (RepeatTestDataDef.TYPE_FOREIGNKEY.equals(type)) {
			NormalColumn referencedColumn = column.getFirstReferencedColumn();
			if (referencedColumn == null) {
				return null;
			}

			List<String> referencedValueList = this
					.getValueList(referencedColumn);

			if (referencedValueList.size() == 0) {
				return null;
			}

			int index = (count / repeatNum) % referencedValueList.size();

			return referencedValueList.get(index);

		} else if (RepeatTestDataDef.TYPE_ENUM.equals(type)) {
			String[] selects = repeatTestDataDef.getSelects();

			if (selects.length == 0) {
				return null;
			}

			return selects[(count / repeatNum) % selects.length];
		}

		return null;
	}

	private List<String> getValueList(NormalColumn column) {
		List<String> valueList = this.valueListMap.get(column);

		if (valueList == null) {
			valueList = new ArrayList<String>();

			ERTable table = (ERTable) column.getColumnHolder();
			TableTestData tableTestData = this.testData.getTableTestDataMap()
					.get(table);

			if (tableTestData != null) {
				DirectTestData directTestData = tableTestData
						.getDirectTestData();
				RepeatTestData repeatTestData = tableTestData
						.getRepeatTestData();

				if (this.testData.getExportOrder() == TestData.EXPORT_ORDER_DIRECT_TO_REPEAT) {
					for (Map<NormalColumn, String> data : directTestData
							.getDataList()) {
						String value = data.get(column);
						valueList.add(value);
					}

					for (int i = 0; i < repeatTestData.getTestDataNum(); i++) {
						String value = this.getMergedRepeatTestDataValue(i,
								repeatTestData.getDataDef(column), column);
						valueList.add(value);
					}

				} else {
					for (int i = 0; i < repeatTestData.getTestDataNum(); i++) {
						String value = this.getRepeatTestDataValue(i,
								repeatTestData.getDataDef(column), column);
						valueList.add(value);
					}

					for (Map<NormalColumn, String> data : directTestData
							.getDataList()) {
						String value = data.get(column);
						valueList.add(value);
					}

				}
			}
		}

		return valueList;
	}

	final public void write(ExportTestDataSetting exportTestDataSetting,
			ERDiagram diagram) throws IOException {
		this.exportTestDataSetting = exportTestDataSetting;
		this.diagram = diagram;

		try {
			this.openFile();

			this.write();

		} finally {
			this.closeFile();
		}
	}

	protected abstract void openFile() throws IOException;

	protected void write() throws IOException {
		for (Map.Entry<ERTable, TableTestData> entry : this.testData
				.getTableTestDataMap().entrySet()) {
			ERTable table = entry.getKey();

			if (skipTable(table)) {
				continue;
			}

			TableTestData tableTestData = entry.getValue();

			DirectTestData directTestData = tableTestData.getDirectTestData();
			RepeatTestData repeatTestData = tableTestData.getRepeatTestData();

			this.writeTableHeader(diagram, table);

			if (this.testData.getExportOrder() == TestData.EXPORT_ORDER_DIRECT_TO_REPEAT) {
				for (Map<NormalColumn, String> data : directTestData
						.getDataList()) {
					this
							.writeDirectTestData(table, data, diagram
									.getDatabase());
				}

				this.writeRepeatTestData(table, repeatTestData, diagram
						.getDatabase());

			} else {
				this.writeRepeatTestData(table, repeatTestData, diagram
						.getDatabase());

				for (Map<NormalColumn, String> data : directTestData
						.getDataList()) {
					this
							.writeDirectTestData(table, data, diagram
									.getDatabase());
				}
			}

			this.writeTableFooter(table);
		}

	}

	protected abstract boolean skipTable(ERTable table);

	protected abstract void writeTableHeader(ERDiagram diagram, ERTable table);

	protected abstract void writeTableFooter(ERTable table);

	protected abstract void writeDirectTestData(ERTable table,
			Map<NormalColumn, String> data, String database);

	protected abstract void writeRepeatTestData(ERTable table,
			RepeatTestData repeatTestData, String database);

	protected abstract void closeFile() throws IOException;
}
