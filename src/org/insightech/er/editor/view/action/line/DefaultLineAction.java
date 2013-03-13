package org.insightech.er.editor.view.action.line;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.swt.widgets.Event;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.DefaultLineCommand;
import org.insightech.er.editor.controller.editpart.element.node.IResizable;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.view.action.AbstractBaseSelectionAction;

public class DefaultLineAction extends AbstractBaseSelectionAction {

	public static final String ID = DefaultLineAction.class.getName();

	public DefaultLineAction(ERDiagramEditor editor) {
		super(ID, ResourceString.getResourceString("action.title.default"),
				editor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Command> getCommand(EditPart editPart, Event event) {
		List<Command> commandList = new ArrayList<Command>();

		if (editPart instanceof IResizable) {
			NodeElementEditPart nodeElementEditPart = (NodeElementEditPart) editPart;

			for (Object obj : nodeElementEditPart.getSourceConnections()) {
				AbstractConnectionEditPart connectionEditPart = (AbstractConnectionEditPart) obj;

				if (connectionEditPart.getSource() != connectionEditPart
						.getTarget()) {
					commandList.add(new DefaultLineCommand(this.getDiagram(),
							(ConnectionElement) connectionEditPart.getModel()));
				}
			}

		} else if (editPart instanceof AbstractConnectionEditPart) {
			AbstractConnectionEditPart connectionEditPart = (AbstractConnectionEditPart) editPart;

			if (connectionEditPart.getSource() != connectionEditPart
					.getTarget()) {
				commandList.add(new DefaultLineCommand(this.getDiagram(),
						(ConnectionElement) connectionEditPart.getModel()));
			}
		}

		return commandList;
	}

	@Override
	protected boolean calculateEnabled() {
		GraphicalViewer viewer = this.getGraphicalViewer();

		for (Object object : viewer.getSelectedEditParts()) {
			if (object instanceof ConnectionEditPart) {
				return true;

			} else if (object instanceof NodeElementEditPart) {
				NodeElementEditPart nodeElementEditPart = (NodeElementEditPart) object;

				if (!nodeElementEditPart.getSourceConnections().isEmpty()) {
					return true;
				}
			}
		}

		return false;
	}
}