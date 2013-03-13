package org.insightech.er.editor.controller.command.common;

import java.util.List;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.insightech.er.util.NameValue;

public class ChangeModelPropertiesCommand extends AbstractCommand {

	private List<NameValue> oldProperties;

	private List<NameValue> newProperties;

	private ModelProperties modelProperties;

	public ChangeModelPropertiesCommand(ERDiagram diagram,
			ModelProperties properties) {
		this.modelProperties = diagram.getDiagramContents().getSettings()
				.getModelProperties();

		this.oldProperties = this.modelProperties.getProperties();
		this.newProperties = properties.getProperties();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.modelProperties.setProperties(newProperties);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.modelProperties.setProperties(oldProperties);
	}

}
