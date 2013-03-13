package org.insightech.er.editor.controller.command.common.notation;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;

public class ChangeNotationLevelCommand extends AbstractCommand {

	private ERDiagram diagram;

	private int oldNotationLevel;

	private int newNotationLevel;
	
	private Settings settings;

	public ChangeNotationLevelCommand(ERDiagram diagram, int notationLevel) {
		this.diagram = diagram;
		this.settings = diagram.getDiagramContents().getSettings();
		this.newNotationLevel = notationLevel;
		this.oldNotationLevel = this.settings.getNotationLevel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.settings.setNotationLevel(this.newNotationLevel);
		this.diagram.changeAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.settings.setNotationLevel(this.oldNotationLevel);
		this.diagram.changeAll();
	}
}
