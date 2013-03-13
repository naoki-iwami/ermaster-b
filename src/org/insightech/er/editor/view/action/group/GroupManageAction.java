package org.insightech.er.editor.view.action.group;

import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.group.ChangeGroupCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.group.CopyGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.view.action.AbstractBaseAction;
import org.insightech.er.editor.view.dialog.group.GroupManageDialog;

public class GroupManageAction extends AbstractBaseAction {

	public static final String ID = GroupManageAction.class.getName();

	public GroupManageAction(ERDiagramEditor editor) {
		super(ID,
				ResourceString.getResourceString("action.title.manage.group"),
				editor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Event event) {
		ERDiagram diagram = this.getDiagram();
		GroupSet groupSet = diagram.getDiagramContents().getGroups();

		GroupManageDialog dialog = new GroupManageDialog(PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getShell(),
				groupSet, diagram, false, -1);

		if (dialog.open() == IDialogConstants.OK_ID) {
			List<CopyGroup> newColumnGroups = dialog.getCopyColumnGroups();

			Command command = new ChangeGroupCommand(diagram, groupSet,
					newColumnGroups);

			this.execute(command);
		}
	}
}
