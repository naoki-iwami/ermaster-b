package org.insightech.er.editor.view.figure.table.style.frame;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.TitleBarBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;

public class ImageTitleBarBorder extends TitleBarBorder {

	private int width;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(IFigure figure, Graphics g, Insets insets) {
		tempRect.setBounds(getPaintRectangle(figure, insets));
		Rectangle rec = tempRect;

		// ïùÇéÊìæ
		this.width = getTextExtents(figure).width + getPadding().getWidth();

		rec.height = Math.min(rec.height, getTextExtents(figure).height
				+ getPadding().getHeight());
		g.clipRect(rec);
		g.fillRectangle(rec);

		// îwåiÉCÉÅÅ[ÉWÇï`âÊ
		Image image = Activator.getImage(ImageKey.TITLEBAR_BACKGROUND);
		g.drawImage(image, 0, 0, image.getImageData().width, image
				.getImageData().height, rec.x, rec.y, rec.width, rec.height);

		int x = rec.x + getPadding().left;
		int y = rec.y + getPadding().top;
		int textWidth = getTextExtents(figure).width;
		int freeSpace = rec.width - getPadding().getWidth() - textWidth;
		if (getTextAlignment() == 2) {
			freeSpace /= 2;
		}
		if (getTextAlignment() != 1) {
			x += freeSpace;
		}
		g.setFont(getFont(figure));
		g.setForegroundColor(getTextColor());
		g.drawString(getLabel(), x, y);

	}

	public int getWidth(IFigure figure) {
		if (getFont(figure) != null) {
			this.width = getTextExtents(figure).width + getPadding().getWidth();
		}

		return this.width;
	}
}
