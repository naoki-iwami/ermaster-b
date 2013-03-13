package org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;

public class AddWordCommand extends AbstractCommand {

	private TableView tableView;

	private Dictionary dictionary;

	private Word word;

	private NormalColumn column;

	private int index;

	public AddWordCommand(TableView tableView, Word word, int index) {
		this.tableView = tableView;
		this.word = word;
		this.index = index;

		this.dictionary = this.tableView.getDiagram().getDiagramContents()
				.getDictionary();

		this.column = new NormalColumn(this.word, true, false, false, false,
				null, null, null, null, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.tableView.addColumn(this.index, this.column);
		this.dictionary.add(this.column);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.tableView.removeColumn(this.column);
		this.dictionary.remove(this.column);
	}

}
