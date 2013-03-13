package org.insightech.er.editor.controller.editpolicy.not_element.tablespace;

import org.eclipse.gef.commands.Command;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.tablespace.DeleteTablespaceCommand;
import org.insightech.er.editor.controller.editpolicy.not_element.NotElementComponentEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;

public class TablespaceComponentEditPolicy extends NotElementComponentEditPolicy {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command createDeleteCommand(ERDiagram diagram, Object model) {
		return new DeleteTablespaceCommand(diagram, (Tablespace) model);
	}

}
