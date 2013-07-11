package org.insightech.er.editor.view.contributor;

import java.util.List;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.AlignmentRetargetAction;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.MatchHeightRetargetAction;
import org.eclipse.gef.ui.actions.MatchWidthRetargetAction;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ViewableModel;
import org.insightech.er.editor.view.action.dbexport.ExportToDBAction;
import org.insightech.er.editor.view.action.dbexport.ExportToDBAction.ExportToDBRetargetAction;
import org.insightech.er.editor.view.action.dbexport.ExportToDDLAction;
import org.insightech.er.editor.view.action.edit.ChangeBackgroundColorAction;
import org.insightech.er.editor.view.action.edit.ChangeBackgroundColorAction.ChangeBackgroundColorRetargetAction;
import org.insightech.er.editor.view.action.edit.EditExcelAction;
import org.insightech.er.editor.view.action.line.HorizontalLineAction;
import org.insightech.er.editor.view.action.line.HorizontalLineAction.HorizontalLineRetargetAction;
import org.insightech.er.editor.view.action.line.VerticalLineAction;
import org.insightech.er.editor.view.action.line.VerticalLineAction.VerticalLineRetargetAction;
import org.insightech.er.editor.view.action.option.notation.LockEditAction;
import org.insightech.er.editor.view.action.option.notation.ToggleMainColumnAction;
import org.insightech.er.editor.view.action.zoom.ZoomAdjustAction;
import org.insightech.er.editor.view.action.zoom.ZoomAdjustRetargetAction;

public class ERDiagramActionBarContributor extends ActionBarContributor {

	private ZoomComboContributionItem zoomComboContributionItem;

	public ERDiagramActionBarContributor(
			ZoomComboContributionItem zoomComboContributionItem) {
		this.zoomComboContributionItem = zoomComboContributionItem;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void buildActions() {
		this.addRetargetAction(new RetargetAction(ActionFactory.SELECT_ALL
				.getId(), "selectAll"));
		this.addRetargetAction(new RetargetAction(ActionFactory.PRINT.getId(),
				"print"));

		this.addRetargetAction(new DeleteRetargetAction());
		this.addRetargetAction(new RetargetAction(ActionFactory.COPY.getId(),
				"copy"));
		this.addRetargetAction(new RetargetAction(ActionFactory.PASTE.getId(),
				"paste"));

		this.addRetargetAction(new UndoRetargetAction());
		this.addRetargetAction(new RedoRetargetAction());

		ZoomInRetargetAction zoomInAction = new ZoomInRetargetAction();
		zoomInAction.setImageDescriptor(Activator
				.getImageDescriptor(ImageKey.ZOOM_IN));
		ZoomOutRetargetAction zoomOutAction = new ZoomOutRetargetAction();
		zoomOutAction.setImageDescriptor(Activator
				.getImageDescriptor(ImageKey.ZOOM_OUT));
		this.addRetargetAction(zoomInAction);
		this.addRetargetAction(zoomOutAction);
		this.addRetargetAction(new ZoomAdjustRetargetAction());

		RetargetAction gridAction = new RetargetAction(
				GEFActionConstants.TOGGLE_GRID_VISIBILITY, ResourceString
						.getResourceString("action.title.grid"),
				IAction.AS_CHECK_BOX);
		gridAction.setImageDescriptor(Activator
				.getImageDescriptor(ImageKey.GRID));
		this.addRetargetAction(gridAction);

		RetargetAction tooltipAction = new RetargetAction(ToggleMainColumnAction.ID,
				ResourceString.getResourceString("action.title.tooltip"),
				IAction.AS_CHECK_BOX);
		tooltipAction.setImageDescriptor(Activator
				.getImageDescriptor(ImageKey.TOOLTIP));
		this.addRetargetAction(tooltipAction);

		RetargetAction toggleMainColumnAction = new RetargetAction(ToggleMainColumnAction.ID,
				ResourceString.getResourceString("action.title.mainColumn"),
				IAction.AS_CHECK_BOX);
		toggleMainColumnAction.setImageDescriptor(Activator.getImageDescriptor(ImageKey.MAIN_COLUMN));
		this.addRetargetAction(toggleMainColumnAction);

		RetargetAction exportDdlAction = new RetargetAction(ExportToDDLAction.ID,
				ResourceString.getResourceString("dialog.title.export.ddl"),
				IAction.AS_CHECK_BOX);
		exportDdlAction.setImageDescriptor(Activator.getImageDescriptor(ImageKey.EXPORT_DDL));
		this.addRetargetAction(exportDdlAction);

		RetargetAction editExcelAction = new RetargetAction(EditExcelAction.ID,
				ResourceString.getResourceString("dialog.title.edit.excel"),
				IAction.AS_CHECK_BOX);
		editExcelAction.setImageDescriptor(Activator.getImageDescriptor(ImageKey.EDIT_EXCEL));
		this.addRetargetAction(editExcelAction);

		RetargetAction lockEditAction = new RetargetAction(LockEditAction.ID,
				ResourceString.getResourceString("action.title.lock.edit"),
				IAction.AS_CHECK_BOX);
		lockEditAction.setImageDescriptor(Activator
				.getImageDescriptor(ImageKey.LOCK_EDIT));
		this.addRetargetAction(lockEditAction);

		this.addRetargetAction(new ExportToDBRetargetAction());

		AlignmentRetargetAction alignLeftAction = new AlignmentRetargetAction(
				PositionConstants.LEFT);
		alignLeftAction.setImageDescriptor(Activator
				.getImageDescriptor(ImageKey.ALIGN_LEFT));
		alignLeftAction.setDisabledImageDescriptor(null);
		this.addRetargetAction(alignLeftAction);
		AlignmentRetargetAction alignCenterAction = new AlignmentRetargetAction(
				PositionConstants.CENTER);
		alignCenterAction.setImageDescriptor(Activator
				.getImageDescriptor(ImageKey.ALIGN_CENTER));
		alignCenterAction.setDisabledImageDescriptor(null);
		this.addRetargetAction(alignCenterAction);
		AlignmentRetargetAction alignRightAction = new AlignmentRetargetAction(
				PositionConstants.RIGHT);
		alignRightAction.setImageDescriptor(Activator
				.getImageDescriptor(ImageKey.ALIGN_RIGHT));
		alignRightAction.setDisabledImageDescriptor(null);
		this.addRetargetAction(alignRightAction);
		AlignmentRetargetAction alignTopAction = new AlignmentRetargetAction(
				PositionConstants.TOP);
		alignTopAction.setImageDescriptor(Activator
				.getImageDescriptor(ImageKey.ALIGN_TOP));
		alignTopAction.setDisabledImageDescriptor(null);
		this.addRetargetAction(alignTopAction);
		AlignmentRetargetAction alignMiddleAction = new AlignmentRetargetAction(
				PositionConstants.MIDDLE);
		alignMiddleAction.setImageDescriptor(Activator
				.getImageDescriptor(ImageKey.ALIGN_MIDDLE));
		alignMiddleAction.setDisabledImageDescriptor(null);
		this.addRetargetAction(alignMiddleAction);
		AlignmentRetargetAction alignBottomAction = new AlignmentRetargetAction(
				PositionConstants.BOTTOM);
		alignBottomAction.setImageDescriptor(Activator
				.getImageDescriptor(ImageKey.ALIGN_BOTTOM));
		alignBottomAction.setDisabledImageDescriptor(null);
		this.addRetargetAction(alignBottomAction);

		MatchWidthRetargetAction matchWidthAction = new MatchWidthRetargetAction();
		matchWidthAction.setImageDescriptor(Activator
				.getImageDescriptor(ImageKey.MATCH_WIDTH));
		matchWidthAction.setDisabledImageDescriptor(null);
		this.addRetargetAction(matchWidthAction);
		MatchHeightRetargetAction matchHeightAction = new MatchHeightRetargetAction();
		matchHeightAction.setImageDescriptor(Activator
				.getImageDescriptor(ImageKey.MATCH_HEIGHT));
		matchHeightAction.setDisabledImageDescriptor(null);
		this.addRetargetAction(matchHeightAction);

		this.addRetargetAction(new HorizontalLineRetargetAction());
		this.addRetargetAction(new VerticalLineRetargetAction());

		this.addRetargetAction(new ChangeBackgroundColorRetargetAction());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		toolBarManager.add(this.getAction(ActionFactory.DELETE.getId()));
		toolBarManager.add(this.getAction(ActionFactory.UNDO.getId()));
		toolBarManager.add(this.getAction(ActionFactory.REDO.getId()));
		toolBarManager.add(new Separator());

		toolBarManager.add(getActionRegistry().getAction(
				GEFActionConstants.ZOOM_IN));
		toolBarManager.add(getActionRegistry().getAction(
				GEFActionConstants.ZOOM_OUT));
		toolBarManager.add(getActionRegistry().getAction(ZoomAdjustAction.ID));

		toolBarManager.add(zoomComboContributionItem);

		toolBarManager.add(new Separator());

		toolBarManager.add(this
				.getAction(GEFActionConstants.TOGGLE_GRID_VISIBILITY));
		toolBarManager.add(this.getAction(ToggleMainColumnAction.ID));
		toolBarManager.add(this.getAction(LockEditAction.ID));

		toolBarManager.add(new Separator());

		toolBarManager.add(this.getAction(ExportToDDLAction.ID));
		toolBarManager.add(this.getAction(ExportToDBAction.ID));

		toolBarManager.add(new Separator());

		toolBarManager.add(getActionRegistry().getAction(
				GEFActionConstants.ALIGN_LEFT));
		toolBarManager.add(getActionRegistry().getAction(
				GEFActionConstants.ALIGN_CENTER));
		toolBarManager.add(getActionRegistry().getAction(
				GEFActionConstants.ALIGN_RIGHT));

		toolBarManager.add(new Separator());

		toolBarManager.add(getActionRegistry().getAction(
				GEFActionConstants.ALIGN_TOP));
		toolBarManager.add(getActionRegistry().getAction(
				GEFActionConstants.ALIGN_MIDDLE));
		toolBarManager.add(getActionRegistry().getAction(
				GEFActionConstants.ALIGN_BOTTOM));

		toolBarManager.add(new Separator());

		toolBarManager.add(getActionRegistry().getAction(
				GEFActionConstants.MATCH_WIDTH));
		toolBarManager.add(getActionRegistry().getAction(
				GEFActionConstants.MATCH_HEIGHT));

		toolBarManager.add(new Separator());

		toolBarManager.add(getActionRegistry().getAction(
				HorizontalLineAction.ID));
		toolBarManager
				.add(getActionRegistry().getAction(VerticalLineAction.ID));

		toolBarManager.add(getActionRegistry().getAction(ChangeBackgroundColorAction.ID));
		toolBarManager.add(getActionRegistry().getAction(EditExcelAction.ID));

		toolBarManager.add(new Separator());

		final FontNameContributionItem fontNameContributionItem = new FontNameContributionItem(
				getPage());
		final FontSizeContributionItem fontSizeContributionItem = new FontSizeContributionItem(
				getPage());

		toolBarManager.add(fontNameContributionItem);
		toolBarManager.add(fontSizeContributionItem);

		this.getPage().addSelectionListener(new ISelectionListener() {

			public void selectionChanged(IWorkbenchPart part,
					ISelection selection) {
				if (selection instanceof IStructuredSelection) {
					List selectedEditParts = ((IStructuredSelection) selection)
							.toList();

					if (!selectedEditParts.isEmpty()) {
						if (selectedEditParts.get(0) instanceof EditPart) {
							Object model = ((EditPart) selectedEditParts.get(0))
									.getModel();

							if (model instanceof ViewableModel) {
								ViewableModel viewableModel = (ViewableModel) model;

								String fontName = viewableModel.getFontName();
								int fontSize = viewableModel.getFontSize();

								if (fontName != null) {
									fontNameContributionItem.setText(fontName);

								} else {
									FontData fonData = Display.getCurrent()
											.getSystemFont().getFontData()[0];
									fontNameContributionItem.setText(fonData
											.getName());
									viewableModel
											.setFontName(fonData.getName());
								}

								if (fontSize > 0) {
									fontSizeContributionItem.setText(String
											.valueOf(fontSize));

								} else {
									fontSizeContributionItem
											.setText(String
													.valueOf(ViewableModel.DEFAULT_FONT_SIZE));
									viewableModel.setFontSize(fontSize);
								}
							}
						}
					}
				}
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void declareGlobalActionKeys() {
		addGlobalActionKey(IWorkbenchActionConstants.PRINT_EXT);
	}

}
