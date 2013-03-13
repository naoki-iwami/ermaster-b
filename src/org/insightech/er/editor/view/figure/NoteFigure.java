package org.insightech.er.editor.view.figure;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.ParagraphTextLayout;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.swt.graphics.Color;

public class NoteFigure extends Shape {

	private static final long serialVersionUID = 7613144432550730126L;

	public static final int RETURN_WIDTH = 15;

	private TextFlow label;

	private Color foregroundColor;

	public NoteFigure() {
		this.create();
		this.setMinimumSize(new Dimension(RETURN_WIDTH * 2, RETURN_WIDTH * 2));
	}

	public void create() {
		this.setBorder(new MarginBorder(RETURN_WIDTH));
		this.setLayoutManager(new BorderLayout());
		FlowPage page = new FlowPage();

		label = new TextFlow();
		ParagraphTextLayout layout = new ParagraphTextLayout(label,
				ParagraphTextLayout.WORD_WRAP_SOFT);
		label.setLayoutManager(layout);
		label.setOpaque(false);

		page.add(label);

		this.add(page, BorderLayout.CENTER);
	}

	public void setText(String text, int[] color) {
		this.decideColor(color);
		this.setForegroundColor(this.foregroundColor);
		this.label.setText(text);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void fillShape(Graphics graphics) {
		graphics.setAlpha(200);

		Rectangle bounds = this.getBounds();

		Point topRight1 = bounds.getTopRight().translate(0, RETURN_WIDTH);
		Point topRight2 = bounds.getTopRight().translate(-RETURN_WIDTH, 0);

		PointList pointList = new PointList();
		pointList.addPoint(bounds.getTopLeft());
		pointList.addPoint(bounds.getBottomLeft());
		pointList.addPoint(bounds.getBottomRight());
		pointList.addPoint(topRight1);
		pointList.addPoint(topRight2);
		pointList.addPoint(bounds.getTopLeft());

		graphics.fillPolygon(pointList);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void outlineShape(Graphics graphics) {
		Rectangle r = getBounds();
		int x = r.x + getLineWidth() / 2;
		int y = r.y + getLineWidth() / 2;
		int w = r.width - Math.max(1, getLineWidth());
		int h = r.height - Math.max(1, getLineWidth());

		Rectangle bounds = new Rectangle(x, y, w, h);

		Point topRight1 = bounds.getTopRight().translate(0, RETURN_WIDTH);
		Point topRight2 = bounds.getTopRight().translate(-RETURN_WIDTH, 0);
		Point topRight3 = bounds.getTopRight().translate(-RETURN_WIDTH,
				RETURN_WIDTH);

		graphics.drawLine(bounds.getTopLeft(), bounds.getBottomLeft());
		graphics.drawLine(bounds.getBottomLeft(), bounds.getBottomRight());
		graphics.drawLine(bounds.getBottomRight(), topRight1);
		graphics.drawLine(topRight1, topRight2);
		graphics.drawLine(topRight2, bounds.getTopLeft());
		graphics.drawLine(topRight2, topRight3);
		graphics.drawLine(topRight3, topRight1);
	}

	private void decideColor(int[] color) {
		if (color != null) {
			int sum = color[0] + color[1] + color[2];

			if (sum > 255) {
				this.foregroundColor = ColorConstants.black;
			} else {
				this.foregroundColor = ColorConstants.white;
			}
		}
	}
}
