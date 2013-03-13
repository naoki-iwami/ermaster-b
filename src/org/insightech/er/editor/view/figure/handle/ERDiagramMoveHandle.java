package org.insightech.er.editor.view.figure.handle;

import org.eclipse.draw2d.Cursors;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.handles.MoveHandle;
import org.insightech.er.editor.view.figure.border.ERDiagramLineBorder;

public class ERDiagramMoveHandle extends MoveHandle {

	public ERDiagramMoveHandle(GraphicalEditPart owner) {
		super(owner);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize() {
		setOpaque(false);
		setBorder(new ERDiagramLineBorder());
		setCursor(Cursors.SIZEALL);
	}
}
