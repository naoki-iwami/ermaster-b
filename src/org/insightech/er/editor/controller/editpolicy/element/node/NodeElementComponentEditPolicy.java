package org.insightech.er.editor.controller.editpolicy.element.node;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.insightech.er.Activator;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.DeleteConnectionCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.DeleteRelationCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.DeleteElementCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.category.DeleteCategoryCommand;
import org.insightech.er.editor.controller.editpart.DeleteableEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ERModelUtil;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERVirtualTable;

public class NodeElementComponentEditPolicy extends ComponentEditPolicy {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command createDeleteCommand(GroupRequest request) {
		try {
			if (this.getHost() instanceof DeleteableEditPart) {
				DeleteableEditPart editPart = (DeleteableEditPart) this
						.getHost();

				if (!editPart.isDeleteable()) {
					return null;
				}

			} else {
				return null;
			}

			Set<NodeElement> targets = new HashSet<NodeElement>();

			for (Object object : request.getEditParts()) {
				EditPart editPart = (EditPart) object;

				Object model = editPart.getModel();

				if (model instanceof NodeElement) {
					targets.add((NodeElement) model);
				}
			}

			ERDiagram diagram = ERModelUtil.getDiagram(this.getHost().getRoot().getContents());
			NodeElement element = (NodeElement) this.getHost().getModel();

			if (element instanceof Category) {
				return new DeleteCategoryCommand(diagram, (Category) element);
			}

			ERVirtualTable virtualTable = null;
			if (element instanceof ERVirtualTable) {
				virtualTable = (ERVirtualTable) element;
				element = ((ERVirtualTable) element).getRawTable();
			}
			if (!diagram.getDiagramContents().getContents().contains(element)
					&& !(element instanceof Category)) {
				return null;
			}

			CompoundCommand command = new CompoundCommand();

			if (virtualTable == null) {
				for (ConnectionElement connection : element.getIncomings()) {
					if (connection instanceof Relation) {
						command.add(new DeleteRelationCommand(
								(Relation) connection, true));

					} else {
						command.add(new DeleteConnectionCommand(connection));
					}
				}

				for (ConnectionElement connection : element.getOutgoings()) {

					NodeElement target = connection.getTarget();

					if (!targets.contains(target)) {
						if (connection instanceof Relation) {
							command.add(new DeleteRelationCommand(
									(Relation) connection, true));
						} else {
							command.add(new DeleteConnectionCommand(connection));
						}
					}
				}

				command.add(new DeleteElementCommand(diagram, element));
			} else {
				// ビュー上でテーブルを消しても実際には消さず、ビューから消すだけにする
				command.add(new DeleteElementCommand(diagram, virtualTable));
				// ただしリレーションは消す
			}

			return command.unwrap();

		} catch (Exception e) {
			Activator.showExceptionDialog(e);
		}

		return null;
	}

}
