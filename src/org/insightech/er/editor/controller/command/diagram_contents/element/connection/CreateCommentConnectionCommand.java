package org.insightech.er.editor.controller.command.diagram_contents.element.connection;

import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERVirtualTable;

public class CreateCommentConnectionCommand extends CreateConnectionCommand {

	public CreateCommentConnectionCommand(ConnectionElement connection) {
		super(connection);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canExecute() {
		if (!super.canExecute()) {
			return false;
		}

		if (!(this.getSourceModel() instanceof Note)
				&& !(this.getTargetModel() instanceof Note)) {
			return false;
		}

		return true;
	}

	@Override
	protected void doExecute() {
		NodeElement source = (NodeElement) this.source.getModel();
		NodeElement target = (NodeElement) this.target.getModel();

		// TableìØémÇÃÉäÉåÅ[ÉVÉáÉìÇÕÅATable <=> Table Ç≈åqÇÆ
		if (source instanceof ERVirtualTable) {
			source = ((ERVirtualTable)source).getRawTable();
		}
		if (target instanceof ERVirtualTable) {
			target = ((ERVirtualTable)target).getRawTable();
		}
		
		connection.setSource(source);
		connection.setTarget(target);
		
		if (source instanceof Note) {
			Note note = (Note) source;
			note.getModel().changeAll();
		}
	}

}
