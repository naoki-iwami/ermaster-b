package org.insightech.er.editor.model.testdata;

public class TableTestData implements Cloneable {

	private DirectTestData directTestData;

	private RepeatTestData repeatTestData;

	public TableTestData() {
		this.directTestData = new DirectTestData();
		this.repeatTestData = new RepeatTestData();
	}

	public DirectTestData getDirectTestData() {
		return directTestData;
	}

	public void setDirectTestData(DirectTestData directTestData) {
		this.directTestData = directTestData;
	}

	public RepeatTestData getRepeatTestData() {
		return repeatTestData;
	}

	public void setRepeatTestData(RepeatTestData repeatTestData) {
		this.repeatTestData = repeatTestData;
	}

	public int getTestDataNum() {
		return this.directTestData.getTestDataNum()
				+ this.repeatTestData.getTestDataNum();
	}

	@Override
	public TableTestData clone() {
		TableTestData clone = new TableTestData();

		clone.directTestData = this.directTestData.clone();
		clone.repeatTestData = this.repeatTestData.clone();

		return clone;
	}
}
