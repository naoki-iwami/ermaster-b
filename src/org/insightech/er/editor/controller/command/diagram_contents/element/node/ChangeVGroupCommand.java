package org.insightech.er.editor.controller.command.diagram_contents.element.node;

import java.util.List;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.VGroup;

public class ChangeVGroupCommand extends AbstractCommand {

	private ERModel model;

	private List<VGroup> oldVgroups;

	private List<VGroup> vgroups;

	public ChangeVGroupCommand(ERModel model, List<VGroup> vgroups) {
		this.model = model;
		this.oldVgroups = model.getGroups();
		this.vgroups = vgroups;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		model.setGroups(vgroups);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		model.setGroups(oldVgroups);
	}

}
