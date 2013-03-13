package org.insightech.er.editor.view.property_source;

import java.util.List;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.insightech.er.ResourceString;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.ERDiagram;

public class ERDiagramPropertySource implements IPropertySource {

	public static final String PROPERTY_INIT_DATABASE = "initDatabase";

	private ERDiagram diagram;

	public ERDiagramPropertySource(ERDiagram diagram) {
		this.diagram = diagram;
	}

	public Object getEditableValue() {
		return this.diagram;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		List<String> dbList = DBManagerFactory.getAllDBList();

		return new IPropertyDescriptor[] { new ComboBoxPropertyDescriptor(
				"database", ResourceString.getResourceString("label.database"),
				dbList.toArray(new String[dbList.size()])) };
	}

	public Object getPropertyValue(Object id) {
		if (id.equals("database")) {
			List<String> dbList = DBManagerFactory.getAllDBList();

			for (int i = 0; i < dbList.size(); i++) {
				if (dbList.get(i).equals(this.diagram.getDatabase())) {
					return new Integer(i);
				}
			}

			return new Integer(0);
		}

		return null;
	}

	public boolean isPropertySet(Object id) {
		if (id.equals("database")) {
			return true;
		}
		return false;
	}

	public void resetPropertyValue(Object id) {
	}

	public void setPropertyValue(Object id, Object value) {
		if (id.equals("database")) {
			List<String> dbList = DBManagerFactory.getAllDBList();

			int index = Integer.parseInt(String.valueOf(value));

			this.diagram.setDatabase(dbList.get(index));
		}
	}

}
