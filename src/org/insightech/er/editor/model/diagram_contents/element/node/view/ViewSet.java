package org.insightech.er.editor.model.diagram_contents.element.node.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ObjectListModel;

public class ViewSet extends AbstractModel implements ObjectListModel,
		Iterable<View> {

	private static final long serialVersionUID = -120487815554383179L;

	public static final String PROPERTY_CHANGE_VIEW_SET = "ViewSet";

	private List<View> viewList;

	public ViewSet() {
		this.viewList = new ArrayList<View>();
	}

	public void add(View view) {
		this.viewList.add(view);
		this.firePropertyChange(PROPERTY_CHANGE_VIEW_SET, null, null);
	}

	public void add(int index, View view) {
		this.viewList.add(index, view);
		this.firePropertyChange(PROPERTY_CHANGE_VIEW_SET, null, null);
	}

	public int remove(View view) {
		int index = this.viewList.indexOf(view);
		this.viewList.remove(index);
		this.firePropertyChange(PROPERTY_CHANGE_VIEW_SET, null, null);

		return index;
	}

	public List<View> getList() {
		Collections.sort(this.viewList);
		
		return this.viewList;
	}

	public Iterator<View> iterator() {
		Collections.sort(this.viewList);
		
		return this.viewList.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ViewSet clone() {
		ViewSet viewSet = (ViewSet) super.clone();
		List<View> newViewList = new ArrayList<View>();

		for (View view : viewList) {
			View newView = (View) view.clone();
			newViewList.add(newView);
		}

		viewSet.viewList = newViewList;

		return viewSet;
	}

	public String getDescription() {
		return "";
	}

	public String getName() {
		return ResourceString.getResourceString("label.object.type.view_list");
	}

	public String getObjectType() {
		return "list";
	}
}
