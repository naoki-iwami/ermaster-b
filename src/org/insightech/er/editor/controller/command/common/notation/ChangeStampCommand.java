package org.insightech.er.editor.controller.command.common.notation;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.model_properties.ModelProperties;

public class ChangeStampCommand extends AbstractCommand {

	private ERDiagram diagram;

	private boolean oldStamp;

	private boolean newStamp;

	private ModelProperties modelProperties;

	public ChangeStampCommand(ERDiagram diagram, boolean isDisplay) {
		this.diagram = diagram;
		this.modelProperties = this.diagram.getDiagramContents().getSettings()
				.getModelProperties();
		this.newStamp = isDisplay;
		this.oldStamp = this.modelProperties.isDisplay();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.modelProperties.setDisplay(this.newStamp);
		this.diagram.changeAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.modelProperties.setDisplay(this.oldStamp);
		this.diagram.changeAll();
	}
}
