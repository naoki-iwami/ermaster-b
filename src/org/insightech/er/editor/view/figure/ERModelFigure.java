package org.insightech.er.editor.view.figure;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ToolbarLayout;

public class ERModelFigure extends RectangleFigure {

	private Label label;

	public ERModelFigure(String name) {
		this.setOpaque(true);

		ToolbarLayout layout = new ToolbarLayout();
		this.setLayoutManager(layout);

		this.label = new Label();
		this.label.setText(name);
		this.label.setBorder(new MarginBorder(7));
		this.add(this.label);
	}

	@Override
	protected void fillShape(Graphics graphics) {
		graphics.setAlpha(100);
		super.fillShape(graphics);
	}

}
