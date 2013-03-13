package org.insightech.er.editor.controller.command.common.notation;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;

public class ChangeDesignCommand extends AbstractCommand {

	private ERDiagram diagram;

	private String oldDesign;

	private String newDesign;

	private Settings settings;

	public ChangeDesignCommand(ERDiagram diagram, String design) {
		this.diagram = diagram;
		this.settings = this.diagram.getDiagramContents().getSettings();
		this.newDesign = design;
		this.oldDesign = this.settings.getTableStyle();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.settings.setTableStyle(this.newDesign);
		this.diagram.change();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.settings.setTableStyle(this.oldDesign);
		this.diagram.change();
	}
}
