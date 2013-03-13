package org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.util.Format;

public class ComplexUniqueKey extends AbstractModel {

	private static final long serialVersionUID = -3970737521746421701L;

	private List<NormalColumn> columnList;

	private String uniqueKeyName;

	public ComplexUniqueKey(String uniqueKeyName) {
		this.uniqueKeyName = uniqueKeyName;
		this.columnList = new ArrayList<NormalColumn>();
	}

	public String getUniqueKeyName() {
		return uniqueKeyName;
	}

	public List<NormalColumn> getColumnList() {
		return columnList;
	}

	public void addColumn(NormalColumn column) {
		this.columnList.add(column);
	}

	public void setColumnList(List<NormalColumn> columnList) {
		this.columnList = columnList;
	}

	public void setUniqueKeyName(String uniqueKeyName) {
		this.uniqueKeyName = uniqueKeyName;
	}

	public boolean isRemoved(List<NormalColumn> tableColumnList) {
		for (NormalColumn normalColumn : this.columnList) {
			if (!tableColumnList.contains(normalColumn)) {
				return true;
			}
		}

		return false;
	}

	public String getLabel() {
		StringBuilder sb = new StringBuilder();

		sb.append(Format.null2blank(this.uniqueKeyName));
		sb.append(" (");
		boolean first = true;
		for (NormalColumn normalColumn : this.getColumnList()) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append(normalColumn.getName());
		}
		sb.append(")");

		return sb.toString();
	}

	public boolean isReferenced(ERTable table) {
		boolean isReferenced = false;

		ComplexUniqueKey target = this;
		if (target instanceof CopyComplexUniqueKey) {
			target = ((CopyComplexUniqueKey) target).getOriginal();
		}

		for (Relation relation : table.getOutgoingRelations()) {
			if (relation.getReferencedComplexUniqueKey() == target) {
				isReferenced = true;
				break;
			}
		}

		return isReferenced;
	}
}
