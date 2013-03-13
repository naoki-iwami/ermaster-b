package org.insightech.er.editor.controller.command.tracking;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.tracking.ChangeTracking;

/**
 * 変更履歴追加コマンド
 */
public class AddChangeTrackingCommand extends AbstractCommand {

	private ERDiagram diagram;

	// 変更履歴
	private ChangeTracking changeTracking;

	/**
	 * 変更履歴追加コマンドを作成します。
	 * 
	 * @param diagram
	 * @param nodeElements
	 */
	public AddChangeTrackingCommand(ERDiagram diagram,
			ChangeTracking changeTracking) {
		this.diagram = diagram;

		this.changeTracking = changeTracking;
	}

	/**
	 * 変更履歴追加処理を実行する
	 */
	@Override
	protected void doExecute() {
		this.diagram.getChangeTrackingList().addChangeTracking(changeTracking);
	}

	/**
	 * 変更履歴追加処理を元に戻す
	 */
	@Override
	protected void doUndo() {
		this.diagram.getChangeTrackingList().removeChangeTracking(
				changeTracking);
	}

}
