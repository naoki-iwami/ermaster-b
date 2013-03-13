package org.insightech.er.editor.model.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public class CategorySetting implements Serializable, Cloneable {

	private static final long serialVersionUID = -7691417386790834828L;

	private List<Category> allCategories;

	private List<Category> selectedCategories;

	private boolean freeLayout;

	private boolean showReferredTables;

	/**
	 * freeLayout ÇéÊìæÇµÇ‹Ç∑.
	 * 
	 * @return freeLayout
	 */
	public boolean isFreeLayout() {
		return freeLayout;
	}

	/**
	 * freeLayout Çê›íËÇµÇ‹Ç∑.
	 * 
	 * @param freeLayout
	 *            freeLayout
	 */
	public void setFreeLayout(boolean freeLayout) {
		this.freeLayout = freeLayout;
	}

	/**
	 * showReferredTables ÇéÊìæÇµÇ‹Ç∑.
	 * 
	 * @return showReferredTables
	 */
	public boolean isShowReferredTables() {
		return showReferredTables;
	}

	/**
	 * showReferredTables Çê›íËÇµÇ‹Ç∑.
	 * 
	 * @param showReferredTables
	 *            showReferredTables
	 */
	public void setShowReferredTables(boolean showReferredTables) {
		this.showReferredTables = showReferredTables;
	}

	public CategorySetting() {
		this.allCategories = new ArrayList<Category>();
		this.selectedCategories = new ArrayList<Category>();
	}

	public void setSelectedCategories(List<Category> selectedCategories) {
		this.selectedCategories = selectedCategories;
	}

	public boolean contains(String categoryName) {
		for (Category category : this.selectedCategories) {
			if (category.getName().equals(categoryName)) {
				return true;
			}
		}

		return false;
	}

	public List<Category> getAllCategories() {
		return this.allCategories;
	}

	public void addCategory(Category category) {
		this.allCategories.add(category);
		Collections.sort(this.allCategories);
	}

	public void addCategoryAsSelected(Category category) {
		this.addCategory(category);
		this.selectedCategories.add(category);
	}

	public void removeCategory(Category category) {
		this.allCategories.remove(category);
		this.selectedCategories.remove(category);
	}

	public void removeCategory(int index) {
		this.allCategories.remove(index);
	}

	public boolean isSelected(Category tableCategory) {
		if (this.selectedCategories.contains(tableCategory)) {
			return true;
		}

		return false;
	}

	public List<Category> getSelectedCategories() {
		return selectedCategories;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object clone() {
		try {
			CategorySetting settings = (CategorySetting) super.clone();
			settings.allCategories = new ArrayList<Category>();
			settings.selectedCategories = new ArrayList<Category>();

			for (Category category : this.allCategories) {
				Category clone = category.clone();
				settings.allCategories.add(clone);

				if (this.contains(category.getName())) {
					settings.selectedCategories.add(clone);
				}
			}

			return settings;

		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public void setAllCategories(List<Category> allCategories) {
		this.allCategories = allCategories;
		Collections.sort(this.allCategories);
	}

}
