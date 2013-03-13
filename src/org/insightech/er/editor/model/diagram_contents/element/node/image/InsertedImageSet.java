package org.insightech.er.editor.model.diagram_contents.element.node.image;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ObjectListModel;

public class InsertedImageSet extends AbstractModel implements ObjectListModel,
		Iterable<InsertedImage> {

	private static final long serialVersionUID = 6136074447375448999L;

	public static final String PROPERTY_CHANGE_INSERTED_IMAGE_SET = "InsertedImageSet";

	private List<InsertedImage> insertedImageList;

	public InsertedImageSet() {
		this.insertedImageList = new ArrayList<InsertedImage>();
	}

	public void add(InsertedImage insertedImage) {
		this.insertedImageList.add(insertedImage);
		this.firePropertyChange(PROPERTY_CHANGE_INSERTED_IMAGE_SET, null, null);
	}

	public int remove(InsertedImage insertedImage) {
		int index = this.insertedImageList.indexOf(insertedImage);
		this.insertedImageList.remove(index);
		this.firePropertyChange(PROPERTY_CHANGE_INSERTED_IMAGE_SET, null, null);

		return index;
	}

	public List<InsertedImage> getList() {
		return this.insertedImageList;
	}

	public Iterator<InsertedImage> iterator() {
		return this.insertedImageList.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InsertedImageSet clone() {
		InsertedImageSet insertedImageSet = (InsertedImageSet) super.clone();
		List<InsertedImage> newInsertedImageList = new ArrayList<InsertedImage>();

		for (InsertedImage insertedImage : this.insertedImageList) {
			InsertedImage newInsertedImage = (InsertedImage) insertedImage
					.clone();
			newInsertedImageList.add(newInsertedImage);
		}

		insertedImageSet.insertedImageList = newInsertedImageList;

		return insertedImageSet;
	}

	public String getDescription() {
		return "";
	}

	public String getName() {
		return null;
	}

	public String getObjectType() {
		return "list";
	}

}
