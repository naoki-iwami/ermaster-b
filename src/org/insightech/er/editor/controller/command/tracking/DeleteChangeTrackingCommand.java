package org.insightech.er.editor.controller.command.tracking;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.tracking.ChangeTracking;

/**
 * 変更履歴削除コマンド
 */
public class DeleteChangeTrackingCommand extends AbstractCommand {

	private ERDiagram diagram;

	// 変更履歴
	private ChangeTracking changeTracking;

	private int index;

	/**
	 * 変更履歴削除コマンドを作成します。
	 * 
	 * @param diagram
	 * @param index
	 */
	public DeleteChangeTrackingCommand(ERDiagram diagram, int index) {
		this.diagram = diagram;

		this.index = index;
		this.changeTracking = this.diagram.getChangeTrackingList().get(index);
	}

	/**
	 * 変更履歴削除処理を実行する
	 */
	@Override
	protected void doExecute() {
		this.diagram.getChangeTrackingList().removeChangeTracking(index);
	}

	/**
	 * 変更履歴削除処理を元に戻す
	 */
	@Override
	protected void doUndo() {
		this.diagram.getChangeTrackingList().addChangeTracking(index,
				changeTracking);
	}

}
