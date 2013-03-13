package org.insightech.er.editor.model.testdata;

import java.util.LinkedHashMap;
import java.util.Map;

import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;

public class TestData implements Cloneable, Comparable<TestData> {

	public static final int EXPORT_FORMT_SQL = 0;

	public static final int EXPORT_FORMT_DBUNIT = 1;
	
	public static final int EXPORT_FORMT_DBUNIT_FLAT_XML = 2;

	public static final int EXPORT_FORMT_DBUNIT_XLS = 3;

	public static final int EXPORT_ORDER_DIRECT_TO_REPEAT = 0;
	
	public static final int EXPORT_ORDER_REPEAT_TO_DIRECT = 1;
	
	private String name;

	private int exportOrder;
	
	private Map<ERTable, TableTestData> tableTestDataMap;

	public TestData() {
		this.tableTestDataMap = new LinkedHashMap<ERTable, TableTestData>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

	public int getExportOrder() {
		return exportOrder;
	}

	public void setExportOrder(int exportOrder) {
		this.exportOrder = exportOrder;
	}

	

	public Map<ERTable, TableTestData> getTableTestDataMap() {
		return tableTestDataMap;
	}

	public void setTableTestDataMap(Map<ERTable, TableTestData> tableTestDataMap) {
		this.tableTestDataMap = tableTestDataMap;
	}

	public void putTableTestData(ERTable table, TableTestData tableTestData) {
		this.tableTestDataMap.put(table, tableTestData);
	}

	public boolean contains(ERTable table) {
		return this.tableTestDataMap.containsKey(table);
	}

	public ERTable get(int index) {
		int i = 0;

		for (ERTable table : this.tableTestDataMap.keySet()) {
			if (i == index) {
				return table;
			}
			i++;
		}

		return null;
	}

	public void removeTableTestData(int index) {
		int i = 0;

		for (ERTable table : this.tableTestDataMap.keySet()) {
			if (i == index) {
				this.tableTestDataMap.remove(table);
				break;
			}

			i++;
		}
	}

	@Override
	public TestData clone() {
		TestData clone = new TestData();

		clone.name = this.name;
		clone.exportOrder = this.exportOrder;

		for (Map.Entry<ERTable, TableTestData> entry : this.tableTestDataMap
				.entrySet()) {
			TableTestData cloneTableTestData = entry.getValue().clone();
			clone.tableTestDataMap.put(entry.getKey(), cloneTableTestData);
		}

		return clone;
	}

	public int compareTo(TestData other) {
		if (other == null) {
			return -1;
		}

		if (this.name == null) {
			return 1;
		}
		if (other.name == null) {
			return -1;
		}

		return this.name.toUpperCase().compareTo(other.name.toUpperCase());
	}
}
