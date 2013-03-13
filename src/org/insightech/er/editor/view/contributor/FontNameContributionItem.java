package org.insightech.er.editor.view.contributor;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.gef.commands.Command;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.insightech.er.editor.controller.command.common.ChangeFontCommand;
import org.insightech.er.editor.model.ViewableModel;

public class FontNameContributionItem extends ComboContributionItem {

	public static final String ID = FontNameContributionItem.class.getName();

	public FontNameContributionItem(IWorkbenchPage workbenchPage) {
		super(ID, workbenchPage);
	}

	@Override
	protected Command createCommand(ViewableModel viewableModel) {
		return new ChangeFontCommand(viewableModel, this.getText(),
				viewableModel.getFontSize());
	}

	@Override
	protected void setData(Combo combo) {
		FontData[] fontDatas = Display.getCurrent().getFontList(null, true);
		Set<String> nameSet = new LinkedHashSet<String>();
		for (int i = 0; i < fontDatas.length; i++) {
			if (!fontDatas[i].getName().startsWith("@")) {
				nameSet.add(fontDatas[i].getName());
			}
		}

		for (String name : nameSet) {
			combo.add(name);
		}
	}

}
