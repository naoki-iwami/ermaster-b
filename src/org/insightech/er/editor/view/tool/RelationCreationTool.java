package org.insightech.er.editor.view.tool;

import org.eclipse.gef.tools.ConnectionCreationTool;
import org.insightech.er.Activator;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.CreateRelationCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public class RelationCreationTool extends ConnectionCreationTool {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean handleCreateConnection() {
		CreateRelationCommand command = (CreateRelationCommand) this
				.getCommand();

		if (command == null) {
			return false;
		}

		TableView source = (TableView) command.getSourceModel();
		TableView target = (TableView) command.getTargetModel();

		if (ERTable.isRecursive(source, target)) {
			Activator.showErrorDialog("error.recursive.relation");

			this.eraseSourceFeedback();

			return false;
		}

		return super.handleCreateConnection();
	}

}
