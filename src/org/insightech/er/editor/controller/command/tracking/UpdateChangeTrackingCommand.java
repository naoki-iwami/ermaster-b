package org.insightech.er.editor.controller.command.tracking;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.tracking.ChangeTracking;

/**
 * 変更履歴更新コマンド
 */
public class UpdateChangeTrackingCommand extends AbstractCommand {

	// 変更履歴
	private ChangeTracking changeTracking;

	private String oldComment;

	private String newComment;

	/**
	 * 変更履歴更新コマンドを作成します。
	 * 
	 * @param changeTracking
	 * @param comment
	 */
	public UpdateChangeTrackingCommand(ChangeTracking changeTracking,
			String comment) {
		this.changeTracking = changeTracking;

		this.oldComment = changeTracking.getComment();
		this.newComment = comment;
	}

	/**
	 * 変更履歴更新処理を実行する
	 */
	@Override
	protected void doExecute() {
		this.changeTracking.setComment(newComment);
	}

	/**
	 * 変更履歴更新処理を元に戻す
	 */
	@Override
	protected void doUndo() {
		this.changeTracking.setComment(oldComment);
	}

}
