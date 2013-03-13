package org.insightech.er.editor.controller.editpolicy.not_element.group;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.group.ChangeGroupCommand;
import org.insightech.er.editor.controller.editpolicy.not_element.NotElementComponentEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.CopyGroup;

public class GroupComponentEditPolicy extends NotElementComponentEditPolicy {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command createDeleteCommand(ERDiagram diagram, Object model) {
		ColumnGroup deleteColumnGroup = (ColumnGroup) model;

		List<CopyGroup> newColumnGroups = new ArrayList<CopyGroup>();

		for (ColumnGroup columnGroup : diagram.getDiagramContents().getGroups()) {
			if (columnGroup != deleteColumnGroup) {
				newColumnGroups.add(new CopyGroup(columnGroup));
			}
		}

		return new ChangeGroupCommand(diagram, diagram.getDiagramContents()
				.getGroups(), newColumnGroups);
	}

}
