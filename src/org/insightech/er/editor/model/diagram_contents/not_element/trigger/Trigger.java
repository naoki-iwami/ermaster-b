package org.insightech.er.editor.model.diagram_contents.not_element.trigger;

import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.WithSchemaModel;

public class Trigger extends WithSchemaModel implements ObjectModel {

	private static final long serialVersionUID = -4766050732350578313L;

	private String sql;

	private String description;

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	/**
	 * description ÇéÊìæÇµÇ‹Ç∑.
	 * 
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * description Çê›íËÇµÇ‹Ç∑.
	 * 
	 * @param description
	 *            description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public String getObjectType() {
		return "trigger";
	}
}
