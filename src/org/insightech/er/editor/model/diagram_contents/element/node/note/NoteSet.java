package org.insightech.er.editor.model.diagram_contents.element.node.note;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ObjectListModel;

public class NoteSet extends AbstractModel implements ObjectListModel,
		Iterable<Note> {

	private static final long serialVersionUID = -7000722010136664297L;

	public static final String PROPERTY_CHANGE_NOTE_SET = "NoteSet";

	private List<Note> noteList;

	public NoteSet() {
		this.noteList = new ArrayList<Note>();
	}

	public void add(Note note) {
		this.noteList.add(note);
		this.firePropertyChange(PROPERTY_CHANGE_NOTE_SET, null, null);
	}

	public int remove(Note note) {
		int index = this.noteList.indexOf(note);
		this.noteList.remove(index);
		this.firePropertyChange(PROPERTY_CHANGE_NOTE_SET, null, null);

		return index;
	}

	public List<Note> getList() {
		return this.noteList;
	}

	public Iterator<Note> iterator() {
		return this.noteList.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NoteSet clone() {
		NoteSet noteSet = (NoteSet) super.clone();
		List<Note> newNoteList = new ArrayList<Note>();

		for (Note note : this.noteList) {
			Note newNote = (Note) note.clone();
			newNoteList.add(newNote);
		}

		noteSet.noteList = newNoteList;

		return noteSet;
	}

	public String getDescription() {
		return "";
	}

	public String getName() {
		return ResourceString.getResourceString("label.object.type.note_list");
	}

	public String getObjectType() {
		return "list";
	}

}
