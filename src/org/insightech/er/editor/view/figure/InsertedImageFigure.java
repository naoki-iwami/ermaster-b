package org.insightech.er.editor.view.figure;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

public class InsertedImageFigure extends Figure {

	private Image image;

	private boolean fixAspectRatio;

	private Dimension imageSize;

	private int alpha;

	public InsertedImageFigure(Image image, boolean fixAspectRatio, int alpha) {
		this.image = image;
		this.fixAspectRatio = fixAspectRatio;
		this.alpha = alpha;
		this.imageSize = new Dimension(this.image.getBounds().width, this.image
				.getBounds().height);
	}

	public void setImg(Image image, boolean fixAspectRatio, int alpha) {
		this.image = image;
		this.fixAspectRatio = fixAspectRatio;
		this.alpha = alpha;

		this.imageSize = new Dimension(this.image.getBounds().width, this.image
				.getBounds().height);
	}

	@Override
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);

		graphics.setAlpha(alpha);

		Rectangle area = getClientArea();

		if (this.fixAspectRatio) {
			Rectangle destination = new Rectangle();

			double dw = (double) this.imageSize.width / (double) area.width;
			double dh = (double) this.imageSize.height / (double) area.height;

			if (dw > dh) {
				// we must limit the size by the width
				destination.width = area.width;
				destination.height = (int) (this.imageSize.height / dw);

			} else {
				// we must limit the size by the height
				destination.width = (int) (this.imageSize.width / dh);
				destination.height = area.height;

			}

			destination.x = (area.width - destination.width) / 2 + area.x;
			destination.y = (area.height - destination.height) / 2 + area.y;

			graphics.drawImage(this.image,
					new Rectangle(this.image.getBounds()), destination);

		} else {
			graphics.drawImage(this.image,
					new Rectangle(this.image.getBounds()), area);

		}

	}

}
