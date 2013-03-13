package org.insightech.er.editor.view.figure.border;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.insightech.er.Resources;

public class ERDiagramLineBorder extends LineBorder {

	private static final int DELTA = 255 / 10;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(IFigure figure, Graphics graphics, Insets insets) {
		if (getColor() != null) {
			graphics.setForegroundColor(getColor());
		}

		tempRect.setBounds(getPaintRectangle(figure, insets));
		if (getWidth() % 2 == 1) {
			tempRect.width--;
			tempRect.height--;
		}
		tempRect.shrink(getWidth() / 2, getWidth() / 2);
		graphics.setLineWidth(1);

		int g = 9 * DELTA;
		int b = 9 * DELTA;

		for (int i = 0; i <= 5; i++) {
			Color color = Resources.getColor(new int[] { b, g, 255 });
			this.paint1(i, color, tempRect, graphics);

			g -= DELTA;
			b -= DELTA;
		}
	}

	private void paint1(int i, Color color, Rectangle tempRect,
			Graphics graphics) {
		tempRect.x++;
		tempRect.y++;
		tempRect.width -= 2;
		tempRect.height -= 2;

		graphics.setForegroundColor(color);
		graphics.drawRectangle(tempRect);
	}
}
