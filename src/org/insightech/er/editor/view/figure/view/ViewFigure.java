package org.insightech.er.editor.view.figure.view;

import org.insightech.er.ImageKey;
import org.insightech.er.editor.view.figure.table.TableFigure;

public class ViewFigure extends TableFigure {

	public ViewFigure(String tableStyle) {
		super(tableStyle);
	}

	@Override
	public String getImageKey() {
		return ImageKey.VIEW;
	}

}
