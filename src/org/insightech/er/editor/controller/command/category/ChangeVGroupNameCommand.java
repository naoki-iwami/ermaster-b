package org.insightech.er.editor.controller.command.category;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.VGroup;

public class ChangeVGroupNameCommand extends AbstractCommand {

	private ERDiagram diagram;

	private String oldName;

	private String newName;

	private VGroup category;

	public ChangeVGroupNameCommand(ERDiagram diagram, VGroup category,
			String newName) {
		this.diagram = diagram;
		this.category = category;
		this.newName = newName;

		this.oldName = category.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.category.setName(this.newName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.category.setName(this.oldName);
	}
	
}
