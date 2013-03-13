package org.insightech.er.editor.view.outline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;
import org.insightech.er.Activator;
import org.insightech.er.ResourceString;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.controller.editpart.outline.ermodel.ERModelOutlineEditPart;
import org.insightech.er.editor.controller.editpart.outline.group.GroupSetOutlineEditPart;
import org.insightech.er.editor.controller.editpart.outline.sequence.SequenceSetOutlineEditPart;
import org.insightech.er.editor.controller.editpart.outline.table.TableOutlineEditPart;
import org.insightech.er.editor.controller.editpart.outline.tablespace.TablespaceSetOutlineEditPart;
import org.insightech.er.editor.controller.editpart.outline.trigger.TriggerSetOutlineEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.action.group.GroupManageAction;
import org.insightech.er.editor.view.action.outline.ChangeNameAction;
import org.insightech.er.editor.view.action.outline.index.CreateIndexAction;
import org.insightech.er.editor.view.action.outline.notation.type.ChangeOutlineViewToBothAction;
import org.insightech.er.editor.view.action.outline.notation.type.ChangeOutlineViewToLogicalAction;
import org.insightech.er.editor.view.action.outline.notation.type.ChangeOutlineViewToPhysicalAction;
import org.insightech.er.editor.view.action.outline.orderby.ChangeOutlineViewOrderByLogicalNameAction;
import org.insightech.er.editor.view.action.outline.orderby.ChangeOutlineViewOrderByPhysicalNameAction;
import org.insightech.er.editor.view.action.outline.sequence.CreateSequenceAction;
import org.insightech.er.editor.view.action.outline.tablespace.CreateTablespaceAction;
import org.insightech.er.editor.view.action.outline.trigger.CreateTriggerAction;

public class ERDiagramOutlinePopupMenuManager extends MenuManager {

	private static Map<Class, String> ACTION_MAP = new HashMap<Class, String>();

	static {
		ACTION_MAP.put(SequenceSetOutlineEditPart.class,
				CreateSequenceAction.ID);
		ACTION_MAP.put(TriggerSetOutlineEditPart.class, CreateTriggerAction.ID);
		ACTION_MAP.put(GroupSetOutlineEditPart.class, GroupManageAction.ID);
		ACTION_MAP.put(TableOutlineEditPart.class, CreateIndexAction.ID);
		ACTION_MAP.put(TablespaceSetOutlineEditPart.class,
				CreateTablespaceAction.ID);
		ACTION_MAP.put(ERModelOutlineEditPart.class, ChangeNameAction.ID);
	}

	private ActionRegistry actionRegistry;

	private ActionRegistry outlineActionRegistry;

	public ERDiagramOutlinePopupMenuManager(final ERDiagram diagram,
			ActionRegistry actionRegistry,
			ActionRegistry outlineActionRegistry,
			final EditPartViewer editPartViewer) {
		try {
			this.actionRegistry = actionRegistry;
			this.outlineActionRegistry = outlineActionRegistry;

			this.add(this.getAction(ChangeNameAction.ID));
			this.add(this.getAction(GroupManageAction.ID));
			this.add(this.getAction(CreateTriggerAction.ID));
			this.add(this.getAction(CreateSequenceAction.ID));
			this.add(this.getAction(CreateIndexAction.ID));
			this.add(this.getAction(CreateTablespaceAction.ID));

			this.add(new Separator());

			MenuManager viewModeMenu = new MenuManager(ResourceString
					.getResourceString("label.outline.view.mode"));
			viewModeMenu.add(this
					.getAction(ChangeOutlineViewToPhysicalAction.ID));
			viewModeMenu.add(this
					.getAction(ChangeOutlineViewToLogicalAction.ID));
			viewModeMenu.add(this.getAction(ChangeOutlineViewToBothAction.ID));
			this.add(viewModeMenu);

			MenuManager orderByMenu = new MenuManager(ResourceString
					.getResourceString("label.order.by"));
			orderByMenu.add(this
					.getAction(ChangeOutlineViewOrderByPhysicalNameAction.ID));
			orderByMenu.add(this
					.getAction(ChangeOutlineViewOrderByLogicalNameAction.ID));
			this.add(orderByMenu);

			this.add(new Separator());
			this.add(this.getAction(ActionFactory.DELETE));

			this.addMenuListener(new IMenuListener() {

				public void menuAboutToShow(IMenuManager manager) {
					try {
						List selectedEditParts = editPartViewer
								.getSelectedEditParts();
						if (selectedEditParts.isEmpty()) {
							for (IContributionItem menuItem : getItems()) {
								if (menuItem.getId() != null
										&& !menuItem
												.getId()
												.equals(
														ChangeOutlineViewToPhysicalAction.ID)
										&& !menuItem
												.getId()
												.equals(
														ChangeOutlineViewToLogicalAction.ID)
										&& !menuItem
												.getId()
												.equals(
														ChangeOutlineViewToBothAction.ID)
										&& !menuItem
												.getId()
												.equals(
														ChangeOutlineViewOrderByPhysicalNameAction.ID)
										&& !menuItem
												.getId()
												.equals(
														ChangeOutlineViewOrderByLogicalNameAction.ID)) {
									enabled(menuItem.getId(), false);
									// menuItem.setVisible(false);
								}
							}

						} else {
							EditPart editPart = (EditPart) selectedEditParts
									.get(0);
							for (Class clazz : ACTION_MAP.keySet()) {
								String actionId = ACTION_MAP.get(clazz);

								if (!clazz.isInstance(editPart)) {
									enabled(actionId, false);

								} else {
									if (CreateSequenceAction.ID
											.equals(actionId)
											&& !DBManagerFactory.getDBManager(
													diagram).isSupported(
													DBManager.SUPPORT_SEQUENCE)) {
										enabled(actionId, false);

									} else {
										enabled(actionId, true);

									}
								}
							}
						}

						Settings settings = diagram.getDiagramContents()
								.getSettings();

						IAction action0 = getAction(ChangeOutlineViewToPhysicalAction.ID);
						IAction action1 = getAction(ChangeOutlineViewToLogicalAction.ID);
						IAction action2 = getAction(ChangeOutlineViewToBothAction.ID);

						if (settings.getOutlineViewMode() == Settings.VIEW_MODE_PHYSICAL) {
							action0.setChecked(true);
							action1.setChecked(false);
							action2.setChecked(false);

						} else if (settings.getOutlineViewMode() == Settings.VIEW_MODE_LOGICAL) {
							action0.setChecked(false);
							action1.setChecked(true);
							action2.setChecked(false);

						} else {
							action0.setChecked(false);
							action1.setChecked(false);
							action2.setChecked(true);
						}

						action0 = getAction(ChangeOutlineViewOrderByPhysicalNameAction.ID);
						action1 = getAction(ChangeOutlineViewOrderByLogicalNameAction.ID);

						if (settings.getViewOrderBy() == Settings.VIEW_MODE_PHYSICAL) {
							action0.setChecked(true);
							action1.setChecked(false);

						} else {
							action0.setChecked(false);
							action1.setChecked(true);
						}

						manager.update(true);
						
					} catch (Exception e) {
						Activator.showExceptionDialog(e);
					}
				}

			});

		} catch (Exception e) {
			Activator.showExceptionDialog(e);
		}
	}

	private IAction getAction(ActionFactory actionFactory) {
		return this.actionRegistry.getAction(actionFactory.getId());
	}

	private IAction getAction(String id) {
		IAction action = this.actionRegistry.getAction(id);

		if (action == null) {
			action = this.outlineActionRegistry.getAction(id);
		}

		return action;
	}

	private void enabled(String id, boolean enabled) {
		IAction action = getAction(id);
		action.setEnabled(enabled);

		// for (IContributionItem menuItem : getItems()) {
		// if (menuItem.getId().equals(id)) {
		// menuItem.setVisible(enabled);
		// break;
		// }
		// }
	}

}
