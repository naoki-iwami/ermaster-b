package org.insightech.er.editor.model.testdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class DirectTestData implements Cloneable {

	private List<Map<NormalColumn, String>> dataList;

	public DirectTestData() {
		this.dataList = new ArrayList<Map<NormalColumn, String>>();
	}

	public List<Map<NormalColumn, String>> getDataList() {
		return dataList;
	}

	public void setDataList(List<Map<NormalColumn, String>> dataList) {
		this.dataList = dataList;
	}

	public int getTestDataNum() {
		return this.dataList.size();
	}

	@Override
	public DirectTestData clone() {
		DirectTestData clone = new DirectTestData();

		for (Map<NormalColumn, String> data : this.dataList) {
			Map<NormalColumn, String> cloneData = new HashMap<NormalColumn, String>();
			for (Map.Entry<NormalColumn, String> entry : data.entrySet()) {
				cloneData.put(entry.getKey(), entry.getValue());
			}

			clone.dataList.add(cloneData);
		}

		return clone;
	}
}
