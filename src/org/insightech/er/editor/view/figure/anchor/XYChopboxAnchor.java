package org.insightech.er.editor.view.figure.anchor;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

public class XYChopboxAnchor extends ChopboxAnchor {

	private Point location;

	public XYChopboxAnchor(IFigure owner) {
		super(owner);
	}

	public void setLocation(Point location) {
		this.location = location;
		fireAnchorMoved();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point getLocation(Point reference) {
		if (this.location != null) {
			Point point = new Point(this.location);
			getOwner().translateToAbsolute(point);
			return point;
		}

		return super.getLocation(reference);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point getReferencePoint() {
		if (this.location != null) {
			Point point = new Point(this.location);
			getOwner().translateToAbsolute(point);
			return point;
		}

		return super.getReferencePoint();
	}

}
