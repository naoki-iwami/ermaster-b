package org.insightech.er.editor.controller.editpolicy.not_element.index;

import org.eclipse.gef.commands.Command;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.index.DeleteIndexCommand;
import org.insightech.er.editor.controller.editpolicy.not_element.NotElementComponentEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;

public class IndexComponentEditPolicy extends NotElementComponentEditPolicy {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command createDeleteCommand(ERDiagram diagram, Object model) {
		return new DeleteIndexCommand(diagram, (Index) model);
	}

}
