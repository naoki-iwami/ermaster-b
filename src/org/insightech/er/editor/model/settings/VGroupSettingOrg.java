package org.insightech.er.editor.model.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.VGroup;

public class VGroupSettingOrg implements Serializable, Cloneable {

//	private static final long serialVersionUID = -7691417386790834828L;
//
////	/** 配置したモデル名 */
////	private String modelName;
//	
//	private List<VGroup> allCategories;
//
//	private List<VGroup> selectedCategories;
//
//	private boolean freeLayout;
//
//	private boolean showReferredTables;
//
////	public VGroupSetting(String modelName) {
////		super();
////		this.modelName = modelName;
////	}
//
//	/**
//	 * freeLayout を取得します.
//	 * 
//	 * @return freeLayout
//	 */
//	public boolean isFreeLayout() {
//		return freeLayout;
//	}
//
//	/**
//	 * freeLayout を設定します.
//	 * 
//	 * @param freeLayout
//	 *            freeLayout
//	 */
//	public void setFreeLayout(boolean freeLayout) {
//		this.freeLayout = freeLayout;
//	}
//
//	/**
//	 * showReferredTables を取得します.
//	 * 
//	 * @return showReferredTables
//	 */
//	public boolean isShowReferredTables() {
//		return showReferredTables;
//	}
//
//	/**
//	 * showReferredTables を設定します.
//	 * 
//	 * @param showReferredTables
//	 *            showReferredTables
//	 */
//	public void setShowReferredTables(boolean showReferredTables) {
//		this.showReferredTables = showReferredTables;
//	}
//
//	public VGroupSetting() {
//		this.allCategories = new ArrayList<VGroup>();
//		this.selectedCategories = new ArrayList<VGroup>();
//	}
//
//	public void setSelectedGroups(List<VGroup> selectedCategories) {
//		this.selectedCategories = selectedCategories;
//	}
//
//	public boolean contains(String categoryName) {
//		for (VGroup category : this.selectedCategories) {
//			if (category.getName().equals(categoryName)) {
//				return true;
//			}
//		}
//
//		return false;
//	}
//
////	/**
////	 * 配置したモデル名を取得します。
////	 * @return 配置したモデル名
////	 */
////	public String getModelName() {
////	    return modelName;
////	}
//
//	public List<VGroup> getAllGroups() {
//		return this.allCategories;
//	}
//
//	public void addGroup(VGroup category) {
//		this.allCategories.add(category);
//		Collections.sort(this.allCategories);
//	}
//
//	public void addGroupAsSelected(VGroup category) {
//		this.addGroup(category);
//		this.selectedCategories.add(category);
//	}
//
//	public void removeGroup(VGroup category) {
//		this.allCategories.remove(category);
//		this.selectedCategories.remove(category);
//	}
//
//	public void removeGroup(int index) {
//		this.allCategories.remove(index);
//	}
//
//	public boolean isSelected(VGroup tableCategory) {
//		if (this.selectedCategories.contains(tableCategory)) {
//			return true;
//		}
//
//		return false;
//	}
//
//	public List<VGroup> getSelectedCategories() {
//		return selectedCategories;
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public Object clone() {
//		try {
//			VGroupSetting settings = (VGroupSetting) super.clone();
//			settings.allCategories = new ArrayList<VGroup>();
//			settings.selectedCategories = new ArrayList<VGroup>();
//
//			for (VGroup category : this.allCategories) {
//				VGroup clone = category.clone();
//				settings.allCategories.add(clone);
//
//				if (this.contains(category.getName())) {
//					settings.selectedCategories.add(clone);
//				}
//			}
//
//			return settings;
//
//		} catch (CloneNotSupportedException e) {
//			return null;
//		}
//	}
//
//	public void setAllGroups(List<VGroup> allCategories) {
//		this.allCategories = allCategories;
//		Collections.sort(this.allCategories);
//	}

}
