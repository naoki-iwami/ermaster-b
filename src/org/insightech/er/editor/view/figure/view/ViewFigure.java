package org.insightech.er.editor.view.figure.view;

import org.insightech.er.ImageKey;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.figure.table.TableFigure;

public class ViewFigure extends TableFigure {

	public ViewFigure(Settings settings) {
		super(settings);
	}

	@Override
	public String getImageKey() {
		return ImageKey.VIEW;
	}

}
