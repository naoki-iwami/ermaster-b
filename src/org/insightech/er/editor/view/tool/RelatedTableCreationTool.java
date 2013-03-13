package org.insightech.er.editor.view.tool;

import org.eclipse.gef.tools.ConnectionCreationTool;
import org.insightech.er.Activator;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.CreateRelatedTableCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;

public class RelatedTableCreationTool extends ConnectionCreationTool {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean handleCreateConnection() {
		CreateRelatedTableCommand command = (CreateRelatedTableCommand) this
				.getCommand();

		if (command != null) {
			ERTable target = (ERTable) command.getTargetModel();

			if (!target.isReferable()) {
				Activator.showErrorDialog("error.no.referenceable.column");

				this.eraseSourceFeedback();

				return false;
			}
		}

		return super.handleCreateConnection();
	}

}
