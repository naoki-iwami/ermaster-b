package org.insightech.er.editor.controller.editpolicy.not_element.sequence;

import org.eclipse.gef.commands.Command;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.sequence.DeleteSequenceCommand;
import org.insightech.er.editor.controller.editpolicy.not_element.NotElementComponentEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;

public class SequenceComponentEditPolicy extends NotElementComponentEditPolicy {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command createDeleteCommand(ERDiagram diagram, Object model) {
		return new DeleteSequenceCommand(diagram, (Sequence) model);
	}
}
