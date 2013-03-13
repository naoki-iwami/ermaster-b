package org.insightech.er.editor.controller.command.common;

import org.eclipse.gef.commands.Command;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.model.ERDiagram;

public class WithoutUpdateCommandWrapper extends Command {

	private Command command;

	private ERDiagram diagram;

	public WithoutUpdateCommandWrapper(Command command, ERDiagram diagram) {
		this.command = command;
		this.diagram = diagram;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		ERDiagramEditPart.setUpdateable(false);

		command.execute();

		ERDiagramEditPart.setUpdateable(true);

		this.diagram.changeAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		ERDiagramEditPart.setUpdateable(false);

		command.undo();

		ERDiagramEditPart.setUpdateable(true);

		this.diagram.changeAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canExecute() {
		return command.canExecute();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canUndo() {
		return command.canUndo();
	}

}
