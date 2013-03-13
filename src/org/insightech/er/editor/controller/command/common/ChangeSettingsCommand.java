package org.insightech.er.editor.controller.command.common;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;

public class ChangeSettingsCommand extends AbstractCommand {

	private ERDiagram diagram;

	private Settings oldSettings;

	private Settings settings;

	public ChangeSettingsCommand(ERDiagram diagram, Settings settings) {
		this.diagram = diagram;
		this.oldSettings = this.diagram.getDiagramContents().getSettings();
		this.settings = settings;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.diagram.setSettings(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.diagram.setSettings(oldSettings);
	}

}
