package org.insightech.er.editor.controller.command.diagram_contents.element.node.note;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;

public class NoteEditCommand extends AbstractCommand {

	private String oldText;

	private String text;

	private Note note;

	public NoteEditCommand(Note note, String text) {
		this.note = note;
		this.oldText = this.note.getText();
		this.text = text;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.note.setText(text);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.note.setText(oldText);
	}
}
