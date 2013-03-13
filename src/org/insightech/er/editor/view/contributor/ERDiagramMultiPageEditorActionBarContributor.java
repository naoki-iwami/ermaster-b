package org.insightech.er.editor.view.contributor;

import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.insightech.er.editor.ERDiagramEditor;

public class ERDiagramMultiPageEditorActionBarContributor extends
		MultiPageEditorActionBarContributor {

	@Override
	public void setActivePage(IEditorPart activeEditor) {
		ERDiagramEditor editor = (ERDiagramEditor) activeEditor;

		ERDiagramActionBarContributor actionBarContributor = editor
				.getActionBarContributor();

		IActionBars actionBars = this.getActionBars();
		actionBars.clearGlobalActionHandlers();
		actionBars.getToolBarManager().removeAll();

		actionBarContributor.init(actionBars, editor.getEditorSite().getPage());
		actionBarContributor.setActiveEditor(editor);
		ZoomComboContributionItem item = (ZoomComboContributionItem) getActionBars()
				.getToolBarManager().find(
						GEFActionConstants.ZOOM_TOOLBAR_WIDGET);
		if (item != null) {
			ZoomManager zoomManager = (ZoomManager) editor
					.getAdapter(ZoomManager.class);
			item.setZoomManager(zoomManager);
		}

		getActionBars().updateActionBars();
	}

}
