package org.insightech.er.editor.view.tool;

import org.eclipse.gef.tools.ConnectionCreationTool;
import org.eclipse.swt.SWT;
import org.insightech.er.Activator;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.CreateSelfRelationCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;

public class SelfRelationCreationTool extends ConnectionCreationTool {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean handleButtonDown(int button) {
		if (button == SWT.KeyDown) {
			return handleCreateConnection();
		}

		return super.handleButtonDown(button);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean handleCreateConnection() {
		CreateSelfRelationCommand command = (CreateSelfRelationCommand) this
				.getCommand();

		ERTable target = (ERTable) command.getSourceModel();

		if (!target.isReferable()) {
			Activator.showErrorDialog("error.no.referenceable.column");

			this.eraseSourceFeedback();

			return false;
		}

		return super.handleCreateConnection();
	}
}
