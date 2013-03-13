package org.insightech.er.editor.controller.command.diagram_contents.element.connection;

import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;

public class CreateConnectionCommand extends AbstractCreateConnectionCommand {

	protected ConnectionElement connection;

	public CreateConnectionCommand(ConnectionElement connection) {
		super();
		this.connection = connection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		
		NodeElement sourceTable = (NodeElement) this.source.getModel();
		NodeElement targetTable = (NodeElement) this.target.getModel();

		// TableìØémÇÃÉäÉåÅ[ÉVÉáÉìÇÕÅATable <=> Table Ç≈åqÇÆ
		
		if (sourceTable instanceof ERVirtualTable) {
			sourceTable = ((ERVirtualTable)sourceTable).getRawTable();
		}
		if (targetTable instanceof ERVirtualTable) {
			targetTable = ((ERVirtualTable)targetTable).getRawTable();
		}
		
		connection.setSource(sourceTable);
		connection.setTarget(targetTable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		connection.setSource(null);
		connection.setTarget(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String validate() {
		return null;
	}

}
