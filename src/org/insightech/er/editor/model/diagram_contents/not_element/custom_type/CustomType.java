package org.insightech.er.editor.model.diagram_contents.not_element.custom_type;

import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.WithSchemaModel;

public class CustomType extends WithSchemaModel implements ObjectModel {

	private static final long serialVersionUID = -4492787972500741281L;

	private String description;

	public String getObjectType() {
		return "sequence";
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

}
