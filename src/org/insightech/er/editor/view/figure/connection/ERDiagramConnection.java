package org.insightech.er.editor.view.figure.connection;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Geometry;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.insightech.er.Resources;

public class ERDiagramConnection extends PolylineConnection {

	private static final double DELTA = 0.01;

	private static final int TOLERANCE = 2;

	private boolean selected;

	private boolean bezier;

	public ERDiagramConnection(boolean bezier) {
		this.bezier = bezier;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void setBezier(boolean bezier) {
		this.bezier = bezier;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void outlineShape(Graphics g) {
		g.setAntialias(SWT.ON);

		g.setForegroundColor(ColorConstants.black);
		g.setLineWidth(1);

		if (this.selected) {
			if (this.bezier) {
				g.setForegroundColor(ColorConstants.gray);

				PointList points = getPoints();
				g.drawPolyline(points);
			}

			g.setForegroundColor(Resources.LINE_COLOR);
			g.setLineWidth(7);
		}

		PointList points = getBezierPoints();

		int width = g.getLineWidth();

		Color color = g.getForegroundColor();

		int lineRed = color.getRed();
		int lineGreen = color.getGreen();
		int lineBlue = color.getBlue();

		int deltaRed = (255 - lineRed) * 2 / width;
		int deltaGreen = (255 - lineGreen) * 2 / width;
		int deltaBlue = (255 - lineBlue) * 2 / width;

		int red = 255;
		int green = 255;
		int blue = 255;

		while (width > 0) {
			red -= deltaRed;
			green -= deltaGreen;
			blue -= deltaBlue;

			if (red < lineRed) {
				red = lineRed;
			}
			if (green < lineGreen) {
				green = lineGreen;
			}
			if (blue < lineBlue) {
				blue = lineBlue;
			}

			color = new Color(Display.getCurrent(), red, green, blue);

			g.setLineWidth(width);
			g.setForegroundColor(color);
			g.drawPolyline(points);

			width -= 2;
		}
	}

	public PointList getBezierPoints() {
		PointList controlPoints = this.getPoints();

		if (this.bezier && controlPoints.size() >= 3) {
			int index = 0;

			PointList pointList = new PointList();

			Point p0 = controlPoints.getPoint(index++);
			Point p1 = controlPoints.getPoint(index++);
			Point p2 = null;
			Point nextPoint = controlPoints.getPoint(index++);

			while (true) {
				if (index != controlPoints.size()) {
					p2 = new Point((p1.x + nextPoint.x) / 2,
							(p1.y + nextPoint.y) / 2);

				} else {
					p2 = nextPoint;
				}

				for (double t = 0.0; t <= 1.0; t = t + DELTA) {
					Point point = new Point();

					point.x = (int) (p0.x * (1 - t) * (1 - t) + 2 * p1.x * t
							* (1 - t) + p2.x * t * t);

					point.y = (int) (p0.y * (1 - t) * (1 - t) + 2 * p1.y * t
							* (1 - t) + p2.y * t * t);

					pointList.addPoint(point);
				}

				pointList.addPoint(p2);

				if (index == controlPoints.size()) {
					break;
				}

				p0 = p2;
				p1 = nextPoint;
				nextPoint = controlPoints.getPoint(index++);
			}

			return pointList;
		}

		return controlPoints;
	}

	@Override
	protected boolean shapeContainsPoint(int x, int y) {
		return Geometry.polylineContainsPoint(this.getBezierPoints(), x, y,
				TOLERANCE);
	}
}
