package org.insightech.er.editor.controller.command.diagram_contents.not_element.dictionary;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;

public class EditWordCommand extends AbstractCommand {

	private Word oldWord;

	private Word word;

	private Word newWord;

	private ERDiagram diagram;

	private Dictionary dictionary;

	public EditWordCommand(Word word, Word newWord, ERDiagram diagram) {
		this.oldWord = new Word(word.getPhysicalName(), word.getLogicalName(),
				word.getType(), word.getTypeData().clone(), word
						.getDescription(), diagram.getDatabase());
		this.diagram = diagram;
		this.word = word;
		this.newWord = newWord;

		this.dictionary = this.diagram.getDiagramContents().getDictionary();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.dictionary.copyTo(newWord, word);
		this.diagram.changeAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.dictionary.copyTo(oldWord, word);
		this.diagram.changeAll();
	}

}
