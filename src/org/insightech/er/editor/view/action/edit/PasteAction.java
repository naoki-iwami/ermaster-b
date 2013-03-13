package org.insightech.er.editor.view.action.edit;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.insightech.er.Activator;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.common.PasteCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.model.edit.CopyManager;

/**
 * 貼り付けアクション
 *
 * @author nakajima
 *
 */
public class PasteAction extends SelectionAction {

	private ERDiagramEditor editor;

	/**
	 * コンストラクタ
	 *
	 * @param part
	 */
	public PasteAction(IWorkbenchPart part) {
		super(part);

		this.setText(ResourceString.getResourceString("action.title.paste"));
		ISharedImages sharedImages = PlatformUI.getWorkbench()
				.getSharedImages();
		setImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		setDisabledImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));

		this.setId(ActionFactory.PASTE.getId());

		ERDiagramEditor editor = (ERDiagramEditor) part;

		this.editor = editor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean calculateEnabled() {
		return CopyManager.canCopy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		try {
			execute(createCommand());
		} catch (Exception e) {
			Activator.log(e);
		}
	}

	/**
	 * 貼り付けコマンドを作成します。<br>
	 * コピー領域に複製されているノードをさらに複製して貼り付けます<br>
	 *
	 * @return 貼り付けコマンド
	 */
	private Command createCommand() {

		// 貼り付け不可の場合
		if (!calculateEnabled()) {
			return null;
		}

		// 貼り付け対象のノード一覧
		NodeSet pasteList = CopyManager.paste();

		int numberOfCopy = CopyManager.getNumberOfCopy();

		// 貼り付けコマンドを作成します。
		boolean first = true;
		int x = 0;
		int y = 0;

		for (NodeElement nodeElement : pasteList) {
			if (first || x > nodeElement.getX()) {
				x = nodeElement.getX();
			}
			if (first || y > nodeElement.getY()) {
				y = nodeElement.getY();
			}

			first = false;
		}

		EditPart editPart = this.editor.getGraphicalViewer().getContents();
		Object model = editPart.getModel();

		if (model instanceof ERDiagram) {
			ERDiagram diagram = (ERDiagram) model;

			Command command = new PasteCommand(editor, pasteList,
					diagram.mousePoint.x - x + (numberOfCopy - 1) * 20,
					diagram.mousePoint.y - y + (numberOfCopy - 1) * 20);

			return command;
		}
		if (model instanceof ERModel) {
			ERModel erModel = (ERModel) model;
			ERDiagram diagram = erModel.getDiagram();


			Command command = new PasteCommand(editor, pasteList,
					diagram.mousePoint.x - x + (numberOfCopy - 1) * 20,
					diagram.mousePoint.y - y + (numberOfCopy - 1) * 20);

			return command;
		}
		return null;
	}

}
