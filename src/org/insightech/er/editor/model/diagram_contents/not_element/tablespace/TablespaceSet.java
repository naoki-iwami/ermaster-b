package org.insightech.er.editor.model.diagram_contents.not_element.tablespace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.editor.model.AbstractModel;

public class TablespaceSet extends AbstractModel implements
		Iterable<Tablespace> {

	private static final long serialVersionUID = 9018173533566296453L;

	public static final String PROPERTY_CHANGE_TABLESPACE_SET = "TablespaceSet";

	private List<Tablespace> tablespaceList;

	public TablespaceSet() {
		this.tablespaceList = new ArrayList<Tablespace>();
	}

	public void addTablespace(Tablespace tablespace) {
		this.tablespaceList.add(tablespace);
		Collections.sort(this.tablespaceList);

		this.firePropertyChange(PROPERTY_CHANGE_TABLESPACE_SET, null, null);
	}

	public int remove(Tablespace tablespace) {
		int index = this.tablespaceList.indexOf(tablespace);
		this.tablespaceList.remove(index);
		this.firePropertyChange(PROPERTY_CHANGE_TABLESPACE_SET, null, null);

		return index;
	}

	public boolean contains(String name) {
		for (Tablespace tablespace : tablespaceList) {
			if (name.equalsIgnoreCase(tablespace.getName())) {
				return true;
			}
		}

		return false;
	}

	public List<Tablespace> getTablespaceList() {
		return this.tablespaceList;
	}

	public Iterator<Tablespace> iterator() {
		return this.tablespaceList.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TablespaceSet clone() {
		TablespaceSet tablespaceSet = (TablespaceSet) super.clone();
		List<Tablespace> newTablespaceList = new ArrayList<Tablespace>();

		for (Tablespace tablespace : tablespaceList) {
			Tablespace newTablespace = (Tablespace) tablespace.clone();
			newTablespaceList.add(newTablespace);
		}

		tablespaceSet.tablespaceList = newTablespaceList;

		return tablespaceSet;
	}
}
