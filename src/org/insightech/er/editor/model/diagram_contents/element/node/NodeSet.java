package org.insightech.er.editor.model.diagram_contents.element.node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModelSet;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.VGroup;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.VGroupSet;
import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImageSet;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.diagram_contents.element.node.note.NoteSet;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableSet;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.element.node.view.ViewSet;

public class NodeSet extends AbstractModel implements Iterable<NodeElement> {

	private static final long serialVersionUID = -120487815554383179L;

	public static final String PROPERTY_CHANGE_CONTENTS = "contents";

	private NoteSet noteSet;

	private TableSet tableSet;
	
//	private VGroupSet groupSet;
	
//	private ERModelSet ermodelSet;

	private ViewSet viewSet;

	private List<NodeElement> nodeElementList;

	private InsertedImageSet insertedImageSet;

	public NodeSet() {
		this.tableSet = new TableSet();
//		this.groupSet = new VGroupSet();
//		this.ermodelSet = new ERModelSet();
		this.viewSet = new ViewSet();
		this.noteSet = new NoteSet();
		this.insertedImageSet = new InsertedImageSet();

		this.nodeElementList = new ArrayList<NodeElement>();
	}

	public void addNodeElement(NodeElement nodeElement) {
		if (nodeElement instanceof ERTable) {
			this.tableSet.add((ERTable) nodeElement);

		} else if (nodeElement instanceof View) {
			this.viewSet.add((View) nodeElement);

		} else if (nodeElement instanceof Note) {
			this.noteSet.add((Note) nodeElement);

		} else if (nodeElement instanceof InsertedImage) {
			this.insertedImageSet.add((InsertedImage) nodeElement);
			
		} else if (nodeElement instanceof VGroup) {
			// do nothing
//			this.groupSet.add((VGroup) nodeElement);
//		} else if (nodeElement instanceof ERModel) {
//			this.ermodelSet.add((ERModel) nodeElement);

		} else {
			System.out.println("not support " + nodeElement);
//			throw new RuntimeException("not support " + nodeElement);
		}

		this.nodeElementList.add(nodeElement);

		this.firePropertyChange(PROPERTY_CHANGE_CONTENTS, null, null);
	}

	public void remove(NodeElement nodeElement) {
//		if (nodeElement instanceof ERVirtualTable) {
//			this.tableSet.remove((ERVirtualTable) nodeElement);
//			
//		} else
		if (nodeElement instanceof ERTable) {
				this.tableSet.remove((ERTable) nodeElement);

		} else if (nodeElement instanceof View) {
			this.viewSet.remove((View) nodeElement);

		} else if (nodeElement instanceof Note) {
			this.noteSet.remove((Note) nodeElement);

		} else if (nodeElement instanceof InsertedImage) {
			this.insertedImageSet.remove((InsertedImage) nodeElement);

//		} else if (nodeElement instanceof VGroup) {
			// do nothing
//			this.groupSet.remove((VGroup) nodeElement);

//		} else if (nodeElement instanceof ERModel) {
//			this.ermodelSet.remove((ERModel) nodeElement);

		} else {
			throw new RuntimeException("not support " + nodeElement);
		}

		this.nodeElementList.remove(nodeElement);

		this.firePropertyChange(PROPERTY_CHANGE_CONTENTS, null, null);
	}

	public boolean contains(NodeElement nodeElement) {
		return this.nodeElementList.contains(nodeElement);
	}

	public void clear() {
		this.tableSet.getList().clear();
		this.viewSet.getList().clear();
		this.noteSet.getList().clear();
		this.insertedImageSet.getList().clear();

		this.nodeElementList.clear();
	}

	public boolean isEmpty() {
		return this.nodeElementList.isEmpty();
	}

	public List<NodeElement> getNodeElementList() {
		return this.nodeElementList;
	}

	public List<TableView> getTableViewList() {
		List<TableView> nodeElementList = new ArrayList<TableView>();

		nodeElementList.addAll(this.tableSet.getList());
		nodeElementList.addAll(this.viewSet.getList());

		return nodeElementList;
	}

	public Iterator<NodeElement> iterator() {
		return this.getNodeElementList().iterator();
	}

	public ViewSet getViewSet() {
		return viewSet;
	}

	public NoteSet getNoteSet() {
		return noteSet;
	}

	public TableSet getTableSet() {
		return tableSet;
	}

	public InsertedImageSet getInsertedImageSet() {
		return insertedImageSet;
	}
	
//	public ERModelSet getErmodelSet() {
//		return ermodelSet;
//	}

}
