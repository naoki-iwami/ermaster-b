package org.insightech.er.editor.model.diagram_contents.element.node.table.column;

import org.insightech.er.editor.model.AbstractModel;

public abstract class Column extends AbstractModel {

	private static final long serialVersionUID = -7808147996469841719L;

	private ColumnHolder columnHolder;

	abstract public String getName();

	public void setColumnHolder(ColumnHolder columnHolder) {
		this.columnHolder = columnHolder;
	}

	public ColumnHolder getColumnHolder() {
		return this.columnHolder;
	}
}
