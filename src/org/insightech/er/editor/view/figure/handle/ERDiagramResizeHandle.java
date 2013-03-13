package org.insightech.er.editor.view.figure.handle;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.handles.ResizeHandle;
import org.eclipse.swt.graphics.Color;

public class ERDiagramResizeHandle extends ResizeHandle {

	public ERDiagramResizeHandle(GraphicalEditPart owner, int direction) {
		super(owner, direction);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void init() {
		setPreferredSize(new Dimension(7, 7));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Color getBorderColor() {
		return ColorConstants.gray;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Color getFillColor() {
		return ColorConstants.white;
	}
}
