package org.insightech.er.editor.view.action.edit;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.controller.editpart.element.node.ModelPropertiesEditPart;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.edit.CopyManager;

/**
 * コピーアクション
 * 
 * @author nakajima
 * 
 */
public class CopyAction extends SelectionAction {

	/**
	 * コンストラクタ
	 * 
	 * @param part
	 */
	public CopyAction(IWorkbenchPart part) {
		super(part);

		this.setText(ResourceString.getResourceString("action.title.copy"));

		ISharedImages sharedImages = PlatformUI.getWorkbench()
				.getSharedImages();
		setImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		setDisabledImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));

		this.setId(ActionFactory.COPY.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected boolean calculateEnabled() {
		List<EditPart> list = new ArrayList<EditPart>(this.getSelectedObjects());

		if (list.isEmpty()) {
			return false;
		}
		if (list.size() == 1 && list.get(0) instanceof ModelPropertiesEditPart
				|| list.get(0) instanceof ERDiagramEditPart) {
			return false;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		copy();
	}

	/**
	 * コピーを実行します。 <br>
	 * この時点のコピー対象をコピー領域に複製しておきます<br>
	 */
	private void copy() {
		if (!calculateEnabled()) {
			return;
		}

		// コピー情報をクリアします。
		CopyManager.clear();

		// 選択されているノードのEditPartを取得します
		NodeSet nodeElementList = new NodeSet();

		for (Object object : this.getSelectedObjects()) {
			if (object instanceof NodeElementEditPart) {
				NodeElementEditPart editPart = (NodeElementEditPart) object;

				NodeElement nodeElement = (NodeElement) editPart.getModel();
				nodeElementList.addNodeElement(nodeElement);
			}
		}

		CopyManager.copy(nodeElementList);
	}

}
