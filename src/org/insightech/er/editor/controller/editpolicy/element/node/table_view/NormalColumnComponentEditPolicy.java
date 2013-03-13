package org.insightech.er.editor.controller.editpolicy.element.node.table_view;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.insightech.er.Activator;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view.ChangeTableViewPropertyCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;

public class NormalColumnComponentEditPolicy extends ComponentEditPolicy {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command createDeleteCommand(GroupRequest request) {
		try {
			if (request.getEditParts().size() == 1) {
				if (this.getHost().getModel() instanceof NormalColumn) {
					NormalColumn normalColumn = (NormalColumn) this.getHost()
							.getModel();

					if (normalColumn.getColumnHolder() instanceof TableView) {
						if (!normalColumn.isForeignKey()
								&& !normalColumn.isReferedStrictly()) {

							TableView table = (TableView) normalColumn
									.getColumnHolder();

							TableView newCopyTable = table.copyData();
							for (NormalColumn copyColumn : newCopyTable
									.getNormalColumns()) {
								CopyColumn targetColumn = (CopyColumn) copyColumn;
								if (targetColumn.getOriginalColumn() == normalColumn) {
									newCopyTable.removeColumn(targetColumn);
									break;
								}
							}

							ChangeTableViewPropertyCommand command = new ChangeTableViewPropertyCommand(
									table, newCopyTable);

							return command;
						}

					} else if (normalColumn.getColumnHolder() instanceof ColumnGroup) {
						ColumnGroup columnGroup = (ColumnGroup) normalColumn
								.getColumnHolder();

						// ColumnGroup の ColumnHolder からはテーブルは取得できないので注意
						TableView table = (TableView) this.getHost()
								.getParent().getModel();

						TableView newCopyTable = table.copyData();

						for (Column copyColumn : newCopyTable.getColumns()) {
							if (copyColumn == columnGroup) {
								newCopyTable.removeColumn(copyColumn);
								break;
							}
						}

						ChangeTableViewPropertyCommand command = new ChangeTableViewPropertyCommand(
								table, newCopyTable);

						return command;
					}

				} else if (this.getHost().getModel() instanceof ColumnGroup) {
					ColumnGroup columnGroup = (ColumnGroup) this.getHost()
							.getModel();

					// ColumnGroup の ColumnHolder からはテーブルは取得できないので注意
					TableView table = (TableView) this.getHost().getParent()
							.getModel();

					TableView newCopyTable = table.copyData();

					for (Column copyColumn : newCopyTable.getColumns()) {
						if (copyColumn == columnGroup) {
							newCopyTable.removeColumn(copyColumn);
							break;
						}
					}

					ChangeTableViewPropertyCommand command = new ChangeTableViewPropertyCommand(
							table, newCopyTable);

					return command;
				}
			}

		} catch (Exception e) {
			Activator.showExceptionDialog(e);
		}

		return null;
	}

}
