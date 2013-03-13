package org.insightech.er.common.widgets.table;

public interface CellEditWorker {

	public void addNewRow();

	public void changeRowNum();

	public boolean isModified(int row, int column);

}
