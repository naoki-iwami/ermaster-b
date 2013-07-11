package org.insightech.er.editor.controller.command.diagram_contents.element.node;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ERModelUtil;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;

public class DeleteElementCommand extends AbstractCommand {

	private ERDiagram container;

	private NodeElement element;

	public DeleteElementCommand(ERDiagram container, NodeElement element) {
		this.container = container;
		this.element = element;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.container.removeContent(this.element);
		ERModelUtil.refreshDiagram(element.getDiagram()); // TODO ‚¤‚Ü‚­ƒŠƒtƒŒƒbƒVƒ…‚ªŒø‚©‚È‚¢

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.container.addContent(this.element);
	}
}
