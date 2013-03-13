package org.insightech.er.editor.controller.command.tracking;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;

/**
 * 変更履歴の置換コマンド
 */
public class ChangeTrackingCommand extends AbstractCommand {

	private ERDiagram diagram;

	private DiagramContents oldDiagramContents;

	private DiagramContents newDiagramContents;

	/**
	 * 置換コマンドを作成します。
	 * 
	 * @param diagram
	 * @param nodeElements
	 * @param columnGroups
	 */
	public ChangeTrackingCommand(ERDiagram diagram,
			DiagramContents newDiagramContents) {
		this.diagram = diagram;

		this.oldDiagramContents = this.diagram.getDiagramContents();
		this.newDiagramContents = newDiagramContents;
	}

	/**
	 * 置換処理を実行する
	 */
	@Override
	protected void doExecute() {
		// 描画更新をとめます。
		ERDiagramEditPart.setUpdateable(false);

		this.diagram.replaceContents(newDiagramContents);

		// 描画更新を再開します。
		ERDiagramEditPart.setUpdateable(true);

		this.diagram.changeAll();
	}

	/**
	 * 置換処理を元に戻す
	 */
	@Override
	protected void doUndo() {
		// 描画更新をとめます。
		ERDiagramEditPart.setUpdateable(false);

		this.diagram.replaceContents(oldDiagramContents);

		// 描画更新を再開します。
		ERDiagramEditPart.setUpdateable(true);

		this.diagram.changeAll();
	}

}
