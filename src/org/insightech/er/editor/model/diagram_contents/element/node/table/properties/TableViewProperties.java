package org.insightech.er.editor.model.diagram_contents.element.node.table.properties;

import java.io.Serializable;

import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;

public abstract class TableViewProperties implements Serializable, Cloneable {

	private static final long serialVersionUID = -4482559358342532447L;

	private String schema;

	private Tablespace tableSpace;

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public Tablespace getTableSpace() {
		return tableSpace;
	}

	public void setTableSpace(Tablespace tableSpace) {
		this.tableSpace = tableSpace;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TableViewProperties clone() {
		TableViewProperties clone = null;

		try {
			clone = (TableViewProperties) super.clone();

		} catch (CloneNotSupportedException e) {
		}

		return clone;
	}

}
