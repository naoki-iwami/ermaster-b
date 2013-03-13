package org.insightech.er.editor.view.tool;

import org.eclipse.gef.tools.ConnectionCreationTool;
import org.insightech.er.Activator;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.CreateRelationByExistingColumnsCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public class RelationByExistingColumnsCreationTool extends
		ConnectionCreationTool {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean handleCreateConnection() {
		try {
			CreateRelationByExistingColumnsCommand command = (CreateRelationByExistingColumnsCommand) this
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

			this.eraseSourceFeedback();
			CreateRelationByExistingColumnsCommand endCommand = (CreateRelationByExistingColumnsCommand) this
					.getCommand();

			if (!endCommand.selectColumns()) {
				return false;
			}

			this.setCurrentCommand(endCommand);
			this.executeCurrentCommand();
		
		} catch (Exception e) {
			Activator.showExceptionDialog(e);
		}
		
		return true;
	}

}
