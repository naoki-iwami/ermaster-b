package org.insightech.er.editor.controller.command.diagram_contents.not_element.sequence;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.SequenceSet;

public class DeleteSequenceCommand extends AbstractCommand {

	private SequenceSet sequenceSet;

	private Sequence sequence;

	public DeleteSequenceCommand(ERDiagram diagram, Sequence sequence) {
		this.sequenceSet = diagram.getDiagramContents().getSequenceSet();
		this.sequence = sequence;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.sequenceSet.remove(this.sequence);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.sequenceSet.addSequence(this.sequence);
	}
}
