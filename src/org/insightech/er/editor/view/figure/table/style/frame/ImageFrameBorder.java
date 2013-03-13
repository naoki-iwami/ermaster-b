package org.insightech.er.editor.view.figure.table.style.frame;

import org.eclipse.draw2d.FrameBorder;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SchemeBorder;

public class ImageFrameBorder extends FrameBorder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createBorders() {
		inner = new ImageTitleBarBorder();
		outer = new SchemeBorder(SCHEME_FRAME);
	}

	/**
	 * タイトル領域の幅を返します
	 * 
	 * @return タイトル領域の幅
	 */
	public int getTitleBarWidth(IFigure figure) {
		return ((ImageTitleBarBorder) this.inner).getWidth(figure);
	}
}
