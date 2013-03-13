package org.insightech.er.editor.model.diagram_contents.not_element.group;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.ColumnHolder;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class ColumnGroup extends Column implements ObjectModel,
		Comparable<ColumnGroup>, ColumnHolder {

	private static final long serialVersionUID = -5923128797828160786L;

	private String groupName;

	private List<NormalColumn> columns;

	public ColumnGroup() {
		this.columns = new ArrayList<NormalColumn>();
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<NormalColumn> getColumns() {
		return this.columns;
	}

	public NormalColumn getColumn(int index) {
		return this.columns.get(index);
	}

	public void addColumn(NormalColumn column) {
		this.columns.add(column);
		column.setColumnHolder(this);
	}

	public void setColumns(List<NormalColumn> columns) {
		this.columns = columns;
		for (Column column : columns) {
			column.setColumnHolder(this);
		}
	}

	public void removeColumn(NormalColumn column) {
		this.columns.remove(column);
	}

	public List<TableView> getUsedTalbeList(ERDiagram diagram) {
		List<TableView> usedTableList = new ArrayList<TableView>();

		for (TableView table : diagram.getDiagramContents().getContents()
				.getTableViewList()) {
			for (Column tableColumn : table.getColumns()) {
				if (tableColumn == this) {
					usedTableList.add(table);
					break;
				}
			}
		}

		return usedTableList;
	}

	public int compareTo(ColumnGroup other) {
		if (other == null) {
			return -1;
		}

		if (this.groupName == null) {
			return 1;
		}
		if (other.getGroupName() == null) {
			return -1;
		}

		return this.groupName.toUpperCase().compareTo(
				other.getGroupName().toUpperCase());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.getGroupName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ColumnGroup clone() {
		ColumnGroup clone = (ColumnGroup) super.clone();

		List<NormalColumn> cloneColumns = new ArrayList<NormalColumn>();

		for (NormalColumn column : this.columns) {
			NormalColumn cloneColumn = (NormalColumn) column.clone();
			cloneColumns.add(cloneColumn);
		}

		clone.setColumns(cloneColumns);

		return clone;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());

		sb.append(", groupName:" + groupName);
		sb.append(", columns:" + columns);

		return sb.toString();
	}

	public String getDescription() {
		return "";
	}

	public String getObjectType() {
		return "group";
	}

}
