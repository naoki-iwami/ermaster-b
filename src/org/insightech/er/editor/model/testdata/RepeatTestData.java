package org.insightech.er.editor.model.testdata;

import java.util.HashMap;
import java.util.Map;

import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class RepeatTestData implements Cloneable {

	private int testDataNum;

	private Map<NormalColumn, RepeatTestDataDef> dataDefMap;

	public RepeatTestData() {
		this.dataDefMap = new HashMap<NormalColumn, RepeatTestDataDef>();
	}

	public RepeatTestDataDef getDataDef(NormalColumn normalColumn) {
		RepeatTestDataDef dataDef = this.dataDefMap.get(normalColumn);

		if (dataDef == null) {
			dataDef = this.createDataDef(normalColumn);
			this.dataDefMap.put(normalColumn, dataDef);
		}

		return dataDef;
	}

	public RepeatTestDataDef setDataDef(NormalColumn normalColumn,
			RepeatTestDataDef dataDef) {
		return this.dataDefMap.put(normalColumn, dataDef);
	}

	public int getTestDataNum() {
		return testDataNum;
	}

	public void setTestDataNum(int testDataNum) {
		this.testDataNum = testDataNum;
	}

	@Override
	public RepeatTestData clone() {
		RepeatTestData clone = new RepeatTestData();

		clone.testDataNum = this.testDataNum;

		for (Map.Entry<NormalColumn, RepeatTestDataDef> entry : dataDefMap
				.entrySet()) {
			RepeatTestDataDef cloneTemplateTestDataDef = entry.getValue()
					.clone();
			clone.dataDefMap.put(entry.getKey(), cloneTemplateTestDataDef);
		}

		return clone;
	}

	private RepeatTestDataDef createDataDef(NormalColumn normalColumn) {
		RepeatTestDataDef dataDef = new RepeatTestDataDef();

		SqlType sqlType = normalColumn.getType();
		Integer length = normalColumn.getTypeData().getLength();

		dataDef.setFrom(1);
		dataDef.setIncrement(1);
		dataDef.setRepeatNum(1);

		if (length != null) {
			if (length == 1) {
				dataDef.setTo(9);

			} else if (length == 2) {
				dataDef.setTo(99);

			} else {
				dataDef.setTo(100);
			}

		} else {
			dataDef.setTo(100);
		}

		if (normalColumn.isForeignKey()) {
			dataDef.setType(RepeatTestDataDef.TYPE_FOREIGNKEY);

		} else {
			dataDef.setType(RepeatTestDataDef.TYPE_FORMAT);

		}

		String template = null;
		String[] selects = null;

		if (sqlType == null) {
			String prefix = normalColumn.getName() + "_";

			template = prefix + "%";
			selects = new String[] { prefix + "1", prefix + "2", prefix + "3",
					prefix + "4" };

		} else if (sqlType.isNumber()) {
			template = "%";
			selects = new String[] { "1", "2", "3", "4" };

		} else if (sqlType.isTimestamp()) {
			template = "2000-01-% 12:00:00.000";
			selects = new String[] { "2000-01-01 12:00:00.000",
					"2000-01-02 12:00:00.000", "2000-01-03 12:00:00.000",
					"2000-01-04 12:00:00.000" };

		} else {
			String prefix = normalColumn.getName();

			if (length != null) {
				if (length < 4) {
					prefix = "";

				} else {
					if (prefix.length() > length - 3) {
						prefix = prefix.substring(0, length - 3) + "_";

					} else {
						prefix = prefix + "_";
					}
				}

			}

			template = prefix + "%";
			selects = new String[] { prefix + "1", prefix + "2", prefix + "3",
					prefix + "4" };

		}

		dataDef.setTemplate(template);
		dataDef.setSelects(selects);

		return dataDef;
	}
}
