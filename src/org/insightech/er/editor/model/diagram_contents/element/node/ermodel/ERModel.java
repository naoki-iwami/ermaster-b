package org.insightech.er.editor.model.diagram_contents.element.node.ermodel;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public class ERModel extends NodeElement {

	public static final String PROPERTY_CHANGE_VTABLES = "vtables";

//	/** 親ダイアグラム */
//	private ERDiagram diagram;

	private int[] defaultColor;

	private String name;
	private String description;

	private List<ERVirtualTable> tables;
	private List<Note> notes;
	private List<VGroup> groups;

	public ERModel(ERDiagram diagram) {
		setDiagram(diagram);
		tables = new ArrayList<ERVirtualTable>();
		notes = new ArrayList<Note>();
		groups = new ArrayList<VGroup>();
	}

//	@Override
//	public int getWidth() {
//		System.out.println("getWidth =  " + super.getWidth());
//		return super.getWidth();
//	}

	/**
	 * objectTypeを取得します。
	 * @return objectType
	 */
	public String getObjectType() {
	    return "ermodel";
	}

//	/**
//	 * 親ダイアグラムを取得します。
//	 * @return 親ダイアグラム
//	 */
//	public ERDiagram getDiagram() {
//	    return diagram;
//	}
//
//	/**
//	 * 親ダイアグラムを設定します。
//	 * @param diagram 親ダイアグラム
//	 */
//	public void setDiagram(ERDiagram diagram) {
//	    this.diagram = diagram;
//	}

	/**
	 * nameを取得します。
	 * @return name
	 */
	public String getName() {
	    return name;
	}
	/**
	 * nameを設定します。
	 * @param name name
	 */
	public void setName(String name) {
	    this.name = name;
	}
	/**
	 * descriptionを取得します。
	 * @return description
	 */
	public String getDescription() {
	    return description;
	}
	/**
	 * descriptionを設定します。
	 * @param description description
	 */
	public void setDescription(String description) {
	    this.description = description;
	}
	/**
	 * tablesを取得します。
	 * @return tables
	 */
	public List<ERVirtualTable> getTables() {
	    return tables;
	}
	/**
	 * tablesを設定します。
	 * @param tables tables
	 */
	public void setTables(List<ERVirtualTable> tables) {
	    this.tables = tables;
	}

	public boolean containsTable(ERTable table) {
		for (ERVirtualTable vtable : tables) {
			if (vtable.getRawTable().equals(table)) {
				return true;
			}
		}
		return false;
	}

	public void remove(ERVirtualTable element) {
		tables.remove(element);
		this.firePropertyChange(PROPERTY_CHANGE_VTABLES, null, null);

//		for (ERVirtualTable table : tables) {
//			if (table.equals(element)) {
//
//			}
//		}
	}

	public void changeAll() {
		this.firePropertyChange(PROPERTY_CHANGE_VTABLES, null, null);
	}

	public void addTable(ERVirtualTable virtualTable) {
		tables.add(virtualTable);
	}

	public int[] getDefaultColor() {
		return defaultColor;
	}

	public void setDefaultColor(int red, int green, int blue) {
		this.defaultColor = new int[3];
		this.defaultColor[0] = red;
		this.defaultColor[1] = green;
		this.defaultColor[2] = blue;
	}

	public ERVirtualTable findVirtualTable(TableView table) {
		for (ERVirtualTable vtable : tables) {
			if (vtable.getRawTable().getPhysicalName().equals(table.getPhysicalName())) {
				return vtable;
			}
		}
		return null;
	}

	public void deleteRelation(Relation relation) {
		for (ERVirtualTable vtable : tables) {
			vtable.removeOutgoing(relation);
			vtable.removeIncoming(relation);
		}
	}

	public void createRelation(Relation relation) {
		boolean dirty = false;
		for (ERVirtualTable vtable : tables) {
			if (relation.getSourceTableView().equals(vtable.getRawTable())) {
				dirty = true;
			} else if (relation.getTargetTableView().equals(vtable.getRawTable())) {
				dirty = true;
			}
		}
		if (dirty) {
			changeAll();
		}
	}

	@Override
	public boolean needsUpdateOtherModel() {
		return false;
	}

	public void addNewContent(NodeElement element) {
		getDiagram().addContent(element);
		if (element instanceof Note) {
			((Note)element).setModel(this);
		}

		int[] color = defaultColor;
		if (color == null) {
			color = getDiagram().getDefaultColor();
		}
		element.setColor(color[0], color[1], color[2]);

		if (getFontName() != null) {
			element.setFontName(this.getFontName());
		} else {
			element.setFontName(getDiagram().getFontName());
		}

		if (getFontSize() != 0) {
			element.setFontSize(this.getFontSize());
		} else {
			element.setFontSize(getDiagram().getFontSize());
		}

		if (element instanceof Note) {
			Note note = (Note) element;
			notes.add(note);
			this.firePropertyChange(PROPERTY_CHANGE_VTABLES, null, null);
		}
	}

	/**
	 * notesを取得します。
	 * @return notes
	 */
	public List<Note> getNotes() {
	    return notes;
	}

	/**
	 * notesを設定します。
	 * @param notes notes
	 */
	public void setNotes(List<Note> notes) {
	    this.notes = notes;
	}

	/**
	 * groupsを取得します。
	 * @return groups
	 */
	public List<VGroup> getGroups() {
	    return groups;
	}

	/**
	 * groupsを設定します。
	 * @param groups groups
	 */
	public void setGroups(List<VGroup> groups) {
	    this.groups = groups;
		this.firePropertyChange(PROPERTY_CHANGE_VTABLES, null, null);
	}

	public void addGroup(VGroup group) {
		groups.add(group);
		this.firePropertyChange(PROPERTY_CHANGE_VTABLES, null, null);
	}

	public void remove(VGroup element) {
		groups.remove(element);
		this.firePropertyChange(PROPERTY_CHANGE_VTABLES, null, null);
	}

	public void remove(Note element) {
		notes.remove(element);
		this.firePropertyChange(PROPERTY_CHANGE_VTABLES, null, null);
	}

}
