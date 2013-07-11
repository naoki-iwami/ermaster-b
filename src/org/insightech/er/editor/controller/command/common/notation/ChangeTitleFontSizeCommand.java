package org.insightech.er.editor.controller.command.common.notation;

import java.math.BigDecimal;

import org.eclipse.ui.IEditorPart;
import org.insightech.er.Activator;
import org.insightech.er.editor.EROneDiagramEditor;
import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ERModelUtil;
import org.insightech.er.editor.model.settings.Settings;

public class ChangeTitleFontSizeCommand extends AbstractCommand {

	private ERDiagram diagram;

	private boolean oldCapital;

	private boolean newCapital;

	private Settings settings;

	public ChangeTitleFontSizeCommand(ERDiagram diagram, boolean isCapital) {
		this.diagram = diagram;
		this.settings = this.diagram.getDiagramContents().getSettings();
		this.newCapital = isCapital;
		this.oldCapital = this.settings.isCapital();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.settings.setTitleFontEm(this.newCapital ? new BigDecimal("1.5") : new BigDecimal("1"));

		if (!ERModelUtil.refreshDiagram(diagram)) {
			Activator.showMessageDialog("テーブルタイトルのフォントサイズを変更しました。\nリアルタイムで変更が反映されないので、ermファイルを保存してもう一度開き直してください。");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		this.settings.setTitleFontEm(this.oldCapital ? new BigDecimal("1.5") : new BigDecimal("1"));
		this.diagram.changeAll();
	}
}
