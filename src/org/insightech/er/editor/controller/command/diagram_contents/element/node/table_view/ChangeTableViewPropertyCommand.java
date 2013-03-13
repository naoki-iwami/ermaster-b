package org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public class ChangeTableViewPropertyCommand extends AbstractCommand {

	private TableView oldCopyTableView;

	private TableView tableView;

	private TableView newCopyTableView;

	public ChangeTableViewPropertyCommand(TableView tableView,
			TableView newCopyTableView) {
		this.tableView = tableView;
		this.oldCopyTableView = tableView.copyData();
		this.newCopyTableView = newCopyTableView;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		if (tableView instanceof ERVirtualTable) {
			ERVirtualTable vtable = (ERVirtualTable) tableView;
			
			// メインビューを更新（枠の再生成）
			this.newCopyTableView.restructureData(vtable.getRawTable());
			// TableView.firePropertyChange(PROPERTY_CHANGE_COLUMNS, null, null);
			
			// サブビューも更新
			vtable.doChangeTable();

			// テーブルの更新（線も含めた再生成）
			this.tableView.getDiagram().changeTable(newCopyTableView);
			// ERDiagram.firePropertyChange(PROPERTY_CHANGE_TABLE)
			
		} else {
			// メインビューを更新
			this.newCopyTableView.restructureData(tableView);
			this.tableView.getDiagram().changeTable(newCopyTableView);
			
			// サブビューも更新
			tableView.getDiagram().doChangeTable(newCopyTableView);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.oldCopyTableView.restructureData(tableView);
		this.tableView.getDiagram().changeAll();
	}

}
