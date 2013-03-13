package org.insightech.er.editor.view.contributor;

import org.eclipse.gef.commands.Command;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.ui.IWorkbenchPage;
import org.insightech.er.editor.controller.command.common.ChangeFontCommand;
import org.insightech.er.editor.model.ViewableModel;

public class FontSizeContributionItem extends ComboContributionItem {

	public static final String ID = FontSizeContributionItem.class.getName();

	public FontSizeContributionItem(IWorkbenchPage workbenchPage) {
		super(ID, workbenchPage);
	}

	@Override
	protected Command createCommand(ViewableModel viewableModel) {
		String text = this.getText();

		try {
			int fontSize = Integer.parseInt(text);
			return new ChangeFontCommand(viewableModel, viewableModel
					.getFontName(), fontSize);

		} catch (NumberFormatException e) {
		}

		return null;
	}

	@Override
	protected void setData(Combo combo) {
		int minimumSize = 5;
		for (int i = minimumSize; i < 17; i++) {
			combo.add(String.valueOf(i));
		}
	}

}
