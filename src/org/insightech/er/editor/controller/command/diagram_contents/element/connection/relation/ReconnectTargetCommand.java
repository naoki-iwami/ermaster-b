package org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;

public class ReconnectTargetCommand extends AbstractCommand {

	private Relation relation;

	int xp;

	int yp;

	int oldXp;

	int oldYp;

	public ReconnectTargetCommand(Relation relation, int xp, int yp) {
		this.relation = relation;

		this.xp = xp;
		this.yp = yp;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.oldXp = relation.getTargetXp();
		this.oldYp = relation.getTargetYp();

		relation.setTargetLocationp(this.xp, this.yp);
		relation.setParentMove();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		relation.setTargetLocationp(this.oldXp, this.oldYp);
		relation.setParentMove();
	}
}
