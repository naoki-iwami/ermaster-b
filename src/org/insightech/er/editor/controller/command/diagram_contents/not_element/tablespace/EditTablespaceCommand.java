package org.insightech.er.editor.controller.command.diagram_contents.not_element.tablespace;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;

public class EditTablespaceCommand extends AbstractCommand {

	private TablespaceSet tablespaceSet;

	private Tablespace tablespace;

	private Tablespace oldTablespace;

	private Tablespace newTablespace;

	public EditTablespaceCommand(ERDiagram diagram, Tablespace tablespace,
			Tablespace newTablespace) {
		this.tablespaceSet = diagram.getDiagramContents().getTablespaceSet();
		this.tablespace = tablespace;
		this.oldTablespace = (Tablespace) this.tablespace.clone();
		this.newTablespace = newTablespace;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.newTablespace.copyTo(this.tablespace);
		this.tablespaceSet.addTablespace(this.tablespace);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.oldTablespace.copyTo(this.tablespace);
		this.tablespaceSet.addTablespace(this.tablespace);
	}
}
