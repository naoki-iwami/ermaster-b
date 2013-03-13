package org.insightech.er.editor.model.diagram_contents.not_element.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.editor.model.AbstractModel;

public class GroupSet extends AbstractModel implements Iterable<ColumnGroup> {

	private static final long serialVersionUID = 6192280105150073360L;

	public static final String PROPERTY_CHANGE_GROUP_SET = "group_set";

	private String database;

	private List<ColumnGroup> groups;

	public GroupSet() {
		this.groups = new ArrayList<ColumnGroup>();
	}

	public void add(ColumnGroup group) {
		this.groups.add(group);
		Collections.sort(this.groups);

		this.firePropertyChange(PROPERTY_CHANGE_GROUP_SET, null, null);
	}

	public void remove(ColumnGroup group) {
		this.groups.remove(group);

		this.firePropertyChange(PROPERTY_CHANGE_GROUP_SET, null, null);
	}

	public Iterator<ColumnGroup> iterator() {
		return this.groups.iterator();
	}

	public List<ColumnGroup> getGroupList() {
		return this.groups;
	}

	public void clear() {
		this.groups.clear();
	}

	public boolean contains(ColumnGroup group) {
		return this.groups.contains(group);
	}

	public ColumnGroup get(int index) {
		return this.groups.get(index);
	}

	public int indexOf(ColumnGroup group) {
		return this.groups.indexOf(group);
	}

	/**
	 * database ÇéÊìæÇµÇ‹Ç∑.
	 * 
	 * @return database
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * database Çê›íËÇµÇ‹Ç∑.
	 * 
	 * @param database
	 *            database
	 */
	public void setDatabase(String database) {
		this.database = database;
	}
}
