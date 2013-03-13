package org.insightech.er.editor.controller.editpolicy.element.connection;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.DeleteConnectionCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;

public class CommentConnectionEditPolicy extends ConnectionEditPolicy {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getDeleteCommand(GroupRequest grouprequest) {
		ConnectionElement connection = (ConnectionElement) this.getHost()
				.getModel();

		return new DeleteConnectionCommand(connection);
	}

}
