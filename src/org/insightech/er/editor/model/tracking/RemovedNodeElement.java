package org.insightech.er.editor.model.tracking;

import org.insightech.er.editor.model.ViewableModel;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;

public abstract class RemovedNodeElement extends ViewableModel {

	private static final long serialVersionUID = 25691567625239643L;

	private NodeElement nodeElement;

	public RemovedNodeElement(NodeElement nodeElement) {
		super();
		this.nodeElement = nodeElement;
	}

	public NodeElement getNodeElement() {
		return nodeElement;
	}
}
