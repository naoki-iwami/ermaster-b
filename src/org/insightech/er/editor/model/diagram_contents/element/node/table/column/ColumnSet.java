package org.insightech.er.editor.model.diagram_contents.element.node.table.column;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ObjectListModel;

public class ColumnSet implements ObjectListModel {

	public String getDescription() {
		return "";
	}

	public String getName() {
		return ResourceString
				.getResourceString("label.object.type.column_list");
	}

	public String getObjectType() {
		return "list";
	}

}
