package org.insightech.er.editor.model.diagram_contents.element.node.ermodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;

public class ERModelSet extends AbstractModel implements Iterable<ERModel> {

	public static final String PROPERTY_CHANGE_MODEL_SET = "ModelSet";

	private List<ERModel> ermodels;

	public ERModelSet() {
		ermodels = new ArrayList<ERModel>();
	}

	public Iterator<ERModel> iterator() {
		Collections.sort(ermodels, new Comparator<ERModel>() {
			@Override
			public int compare(ERModel o1, ERModel o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return ermodels.iterator();
	}

//	public void addModel(ERModel ermodel) {
//		ermodels.add(ermodel);
//		this.firePropertyChange(PROPERTY_CHANGE_MODEL_SET, null, null);
//	}
//
	public void addModels(List<ERModel> models) {
		ermodels.addAll(models);
		this.firePropertyChange(PROPERTY_CHANGE_MODEL_SET, null, null);
	}


	public void add(ERModel ermodel) {
		this.ermodels.add(ermodel);
		this.firePropertyChange(PROPERTY_CHANGE_MODEL_SET, null, null);
	}

	public int remove(ERModel ermodel) {
		int index = this.ermodels.indexOf(ermodel);
		this.ermodels.remove(index);
		this.firePropertyChange(PROPERTY_CHANGE_MODEL_SET, null, null);

		return index;
	}

	public void changeModel(ERModel ermodel) {
		this.firePropertyChange(PROPERTY_CHANGE_MODEL_SET, null, null);
	}

	public ERModel getModel(String modelName) {
		for (ERModel model : ermodels) {
			if (model.getName().equals(modelName)) {
				return model;
			}
		}
		return null;
	}

	/**
	 * 全ビューからリレーションを削除
	 * @param relation
	 */
	public void deleteRelation(Relation relation) {
		for (ERModel model : ermodels) {
			model.deleteRelation(relation);
		}
	}

	public void createRelation(Relation relation) {
		for (ERModel model : ermodels) {
			model.createRelation(relation);
		}
	}


//	public Object getModels() {
//		return ermodels;
//	}

}
