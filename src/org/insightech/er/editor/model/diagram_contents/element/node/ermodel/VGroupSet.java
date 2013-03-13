package org.insightech.er.editor.model.diagram_contents.element.node.ermodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ObjectListModel;

public class VGroupSet extends AbstractModel implements ObjectListModel, Iterable<VGroup> {

	private static final long serialVersionUID = 5264397678674390103L;

	public static final String PROPERTY_CHANGE_GROUP_SET = "GroupSet";

	private List<VGroup> groupList;

	public VGroupSet() {
		this.groupList = new ArrayList<VGroup>();
	}

	public void add(VGroup table) {
		this.groupList.add(table);
		this.firePropertyChange(PROPERTY_CHANGE_GROUP_SET, null, null);
	}

	public int remove(VGroup table) {
		int index = this.groupList.indexOf(table);
		this.groupList.remove(index);
		this.firePropertyChange(PROPERTY_CHANGE_GROUP_SET, null, null);

		return index;
	}

	public void setDirty() {
		this.firePropertyChange(PROPERTY_CHANGE_GROUP_SET, null, null);
	}

	public List<VGroup> getList() {
		Collections.sort(this.groupList);

		return this.groupList;
	}

	public Iterator<VGroup> iterator() {
		Collections.sort(this.groupList);

		return this.groupList.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VGroupSet clone() {
		VGroupSet groupSet = (VGroupSet) super.clone();
		List<VGroup> newTableList = new ArrayList<VGroup>();

		for (VGroup table : this.groupList) {
			VGroup newTable = (VGroup) table.clone();
			newTableList.add(newTable);
		}

		groupSet.groupList = newTableList;

		return groupSet;
	}

//	public List<String> getAutoSequenceNames(String database) {
//		List<String> autoSequenceNames = new ArrayList<String>();
//
//		for (VGroup group : this.groupList) {
//			String prefix = group.getNameWithSchema(database) + "_";
//
//			for (NormalColumn column : group.getNormalColumns()) {
//				SqlType sqlType = column.getType();
//
//				if (SqlType.valueOfId(SqlType.SQL_TYPE_ID_SERIAL).equals(
//						sqlType)
//						|| SqlType.valueOfId(SqlType.SQL_TYPE_ID_BIG_SERIAL)
//								.equals(sqlType)) {
//					autoSequenceNames
//							.add((prefix + column.getPhysicalName() + "_seq")
//									.toUpperCase());
//				}
//			}
//		}
//
//		return autoSequenceNames;
//	}

	public String getDescription() {
		return "";
	}

	public String getName() {
		return ResourceString.getResourceString("label.object.type.table_list");
	}

	public String getObjectType() {
		return "list";
	}

}
