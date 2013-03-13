package org.insightech.er.editor.model.tracking;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.util.Check;

public class ChangeTrackingList implements Serializable {

	private static final long serialVersionUID = 3290113276160681941L;

	private List<ChangeTracking> changeTrackingList;

	private List<NodeElement> addedNodeElements;

	private List<UpdatedNodeElement> updatedNodeElements;

	private List<RemovedNodeElement> removedNodeElements;

	private boolean calculated;

	public ChangeTrackingList() {
		this.changeTrackingList = new ArrayList<ChangeTracking>();
		this.addedNodeElements = new ArrayList<NodeElement>();
		this.updatedNodeElements = new ArrayList<UpdatedNodeElement>();
		this.removedNodeElements = new ArrayList<RemovedNodeElement>();
	}

	public void addChangeTracking(ChangeTracking changeTracking) {
		this.changeTrackingList.add(changeTracking);
	}

	public void addChangeTracking(int index, ChangeTracking changeTracking) {
		this.changeTrackingList.add(index, changeTracking);
	}

	public void removeChangeTracking(int index) {
		if (index >= 0 && index < this.changeTrackingList.size()) {
			this.changeTrackingList.remove(index);
		}
	}

	public void removeChangeTracking(ChangeTracking changeTracking) {
		this.changeTrackingList.remove(changeTracking);
	}

	public List<ChangeTracking> getList() {
		return this.changeTrackingList;
	}

	public ChangeTracking get(int index) {
		return this.changeTrackingList.get(index);
	}

	public List<UpdatedNodeElement> getUpdatedNodeElementSet() {
		return this.updatedNodeElements;
	}

	public List<NodeElement> getAddedNodeElementSet() {
		return this.addedNodeElements;
	}

	public List<RemovedNodeElement> getRemovedNodeElementSet() {
		return this.removedNodeElements;
	}

	public void setCalculated(boolean calculated) {
		this.calculated = calculated;
	}

	public boolean isCalculated() {
		return this.calculated;
	}

	public void calculateUpdatedNodeElementSet(NodeSet oldList, NodeSet newList) {
		this.calculated = true;

		this.addedNodeElements.clear();
		this.updatedNodeElements.clear();
		this.removedNodeElements.clear();

		List<Note> oldNotes = new ArrayList<Note>();
		List<ERTable> oldTables = new ArrayList<ERTable>();

		for (NodeElement nodeElement : oldList) {
			if (nodeElement instanceof Note) {
				Note note = (Note) nodeElement;
				oldNotes.add(note);

			} else if (nodeElement instanceof ERTable) {
				oldTables.add((ERTable) nodeElement);
			}
		}

		for (NodeElement newNodeElement : newList) {
			if (newNodeElement instanceof Note) {
				Note newNote = (Note) newNodeElement;
				String newNoteText = newNote.getText();

				boolean exists = false;

				for (Iterator<Note> iter = oldNotes.iterator(); iter.hasNext();) {
					Note oldNote = iter.next();

					if (oldNote.getText() != null
							&& oldNote.getText().equals(newNoteText)) {
						iter.remove();
						exists = true;
						break;
					}
				}

				if (!exists) {
					this.addedNodeElements.add(newNote);
				}

			} else if (newNodeElement instanceof ERTable) {
				ERTable newTable = (ERTable) newNodeElement;
				ERTable oldTable = null;

				boolean exists = false;

				for (ERTable table : oldTables) {
					oldTable = table;

					if (oldTable.getPhysicalName().equals(
							newTable.getPhysicalName())) {
						exists = true;
						break;
					}
				}

				if (!exists) {
					this.addedNodeElements.add(newTable);

				} else {
					oldTables.remove(oldTable);

					Set<NormalColumn> addedColumns = new HashSet<NormalColumn>();
					Set<NormalColumn> updatedColumns = new HashSet<NormalColumn>();

					List<NormalColumn> oldColumns = new ArrayList<NormalColumn>(
							oldTable.getExpandedColumns());

					for (NormalColumn newColumn : newTable.getExpandedColumns()) {
						Column originalColumn = null;

						for (NormalColumn oldColumn : oldColumns) {
							if (newColumn.getName().equals(oldColumn.getName())) {
								originalColumn = oldColumn;
								oldColumns.remove(oldColumn);

								if (!this.compareColumn(
										(NormalColumn) oldColumn,
										(NormalColumn) newColumn)) {
									updatedColumns.add(newColumn);
								}

								break;
							}
						}

						if (originalColumn == null) {
							addedColumns.add(newColumn);
						}
					}

					if (!addedColumns.isEmpty() || !updatedColumns.isEmpty()
							|| !oldColumns.isEmpty()) {
						UpdatedNodeElement updatedNodeElement = new UpdatedNodeElement(
								newTable);
						this.updatedNodeElements.add(updatedNodeElement);

						updatedNodeElement.setAddedColumns(addedColumns);
						updatedNodeElement.setUpdatedColumns(updatedColumns);
						updatedNodeElement.setRemovedColumns(oldColumns);
					}
				}
			}
		}

		for (Note oldNote : oldNotes) {
			this.removedNodeElements.add(new RemovedNote(oldNote));
		}

		for (ERTable oldTable : oldTables) {
			this.removedNodeElements.add(new RemovedERTable(oldTable));
		}

	}

	private boolean compareColumn(NormalColumn oldColumn, NormalColumn newColumn) {
		if (!Check.equals(oldColumn.getPhysicalName(), newColumn
				.getPhysicalName())) {
			return false;
		}
		if (!Check.equals(oldColumn.getTypeData().getDecimal(), newColumn
				.getTypeData().getDecimal())) {
			return false;
		}
		if (!Check.equals(oldColumn.getDefaultValue(), newColumn
				.getDefaultValue())) {
			return false;
		}
		if (!Check.equals(oldColumn.getDescription(), newColumn
				.getDescription())) {
			return false;
		}
		if (!Check.equals(oldColumn.getTypeData().getLength(), newColumn
				.getTypeData().getLength())) {
			return false;
		}
		if (!Check.equals(oldColumn.getType(), newColumn.getType())) {
			return false;
		}
		if (oldColumn.isAutoIncrement() != newColumn.isAutoIncrement()) {
			return false;
		}
		if (oldColumn.isForeignKey() != newColumn.isForeignKey()) {
			return false;
		}
		if (oldColumn.isNotNull() != newColumn.isNotNull()) {
			return false;
		}
		if (oldColumn.isPrimaryKey() != newColumn.isPrimaryKey()) {
			return false;
		}
		if (oldColumn.isUniqueKey() != newColumn.isUniqueKey()) {
			return false;
		}

		return true;
	}

	public UpdatedNodeElement getUpdatedNodeElement(NodeElement nodeElement) {
		for (UpdatedNodeElement updatedNodeElement : this.updatedNodeElements) {
			if (updatedNodeElement.getNodeElement() == nodeElement) {
				return updatedNodeElement;
			}
		}

		return null;
	}

	public boolean isAdded(NodeElement nodeElement) {
		return this.addedNodeElements.contains(nodeElement);
	}

	public void restore(List<NodeElement> addedNodeElements,
			List<UpdatedNodeElement> updatedNodeElements,
			List<RemovedNodeElement> removedNodeElements) {
		this.addedNodeElements = addedNodeElements;
		this.updatedNodeElements = updatedNodeElements;
		this.removedNodeElements = removedNodeElements;
	}
}
