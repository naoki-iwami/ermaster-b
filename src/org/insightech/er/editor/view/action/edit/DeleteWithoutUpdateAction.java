package org.insightech.er.editor.view.action.edit;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.ui.IWorkbenchPart;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.common.WithoutUpdateCommandWrapper;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ERModelUtil;

public class DeleteWithoutUpdateAction extends DeleteAction {

	private ERDiagramEditor part;

	public DeleteWithoutUpdateAction(ERDiagramEditor part) {
		super((IWorkbenchPart) part);
		this.part = part;
		this.setText(ResourceString.getResourceString("action.title.delete"));
		this.setToolTipText(ResourceString
				.getResourceString("action.title.delete"));

		this.setActionDefinitionId("org.eclipse.ui.edit.delete");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Command createDeleteCommand(List objects) {
		Command command = super.createDeleteCommand(objects);

		if (command == null) {
			return null;
		}

		if (command instanceof CompoundCommand) {
			CompoundCommand compoundCommand = (CompoundCommand) command;
			if (compoundCommand.getCommands().isEmpty()) {
				return null;
			}
		}

		EditPart editPart = part.getGraphicalViewer().getContents();
		ERDiagram diagram = ERModelUtil.getDiagram(editPart);

		return new WithoutUpdateCommandWrapper(command, diagram);
	}

	@Override
	protected boolean calculateEnabled() {
		Command cmd = createDeleteCommand(getSelectedObjects());
		if (cmd == null) {
			return false;
		}

		return true;
	}

}
