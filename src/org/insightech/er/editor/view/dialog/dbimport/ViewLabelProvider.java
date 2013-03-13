package org.insightech.er.editor.view.dialog.dbimport;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.graphics.Image;
import org.insightech.er.editor.model.StringObjectModel;
import org.insightech.er.editor.model.dbimport.DBObject;

public class ViewLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	@Override
	public String getText(Object element) {
		TreeNode treeNode = (TreeNode) element;

		Object value = treeNode.getValue();
		if (value instanceof DBObject) {
			DBObject dbObject = (DBObject) value;
			return dbObject.getName();

		} else if (value instanceof StringObjectModel) {
			StringObjectModel object = (StringObjectModel) value;
			return object.getName();
		}

		return value.toString();
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		return "xxx";
	}

}
