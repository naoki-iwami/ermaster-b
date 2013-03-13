package org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;

public class ChangeColumnOrderCommand extends AbstractCommand {

	private TableView tableView;

	private Column column;

	private int newIndex;
	
	private int oldIndex;

	public ChangeColumnOrderCommand(TableView tableView, Column column,
			int index) {
		this.tableView = tableView;
		this.column = column;
		this.newIndex = index;
		this.oldIndex = this.tableView.getColumns().indexOf(column);
		
		if (this.oldIndex < this.newIndex) {
			this.newIndex--;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.tableView.removeColumn(column);
		this.tableView.addColumn(newIndex, column);
		this.tableView.getDiagram().changeAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.tableView.removeColumn(column);
		this.tableView.addColumn(oldIndex, column);
		this.tableView.getDiagram().changeAll();
	}

}
