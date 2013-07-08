package org.insightech.er.editor.view;

import java.math.BigDecimal;

import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.CategorySetting;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.action.category.CategoryManageAction;
import org.insightech.er.editor.view.action.category.ChangeFreeLayoutAction;
import org.insightech.er.editor.view.action.category.ChangeShowReferredTablesAction;
import org.insightech.er.editor.view.action.dbexport.ExportToDDLAction;
import org.insightech.er.editor.view.action.dbexport.ExportToDictionaryAction;
import org.insightech.er.editor.view.action.dbexport.ExportToExcelAction;
import org.insightech.er.editor.view.action.dbexport.ExportToHtmlAction;
import org.insightech.er.editor.view.action.dbexport.ExportToImageAction;
import org.insightech.er.editor.view.action.dbexport.ExportToJavaAction;
import org.insightech.er.editor.view.action.dbexport.ExportToTestDataAction;
import org.insightech.er.editor.view.action.dbexport.ExportToTranslationDictionaryAction;
import org.insightech.er.editor.view.action.dbimport.ImportFromDBAction;
import org.insightech.er.editor.view.action.dbimport.ImportFromFileAction;
import org.insightech.er.editor.view.action.edit.EditAllAttributesAction;
import org.insightech.er.editor.view.action.ermodel.ERModelAddAction;
import org.insightech.er.editor.view.action.ermodel.PlaceTableAction;
import org.insightech.er.editor.view.action.line.DefaultLineAction;
import org.insightech.er.editor.view.action.line.ResizeModelAction;
import org.insightech.er.editor.view.action.line.RightAngleLineAction;
import org.insightech.er.editor.view.action.option.OptionSettingAction;
import org.insightech.er.editor.view.action.option.notation.ChangeCapitalAction;
import org.insightech.er.editor.view.action.option.notation.ChangeNotationExpandGroupAction;
import org.insightech.er.editor.view.action.option.notation.ChangeStampAction;
import org.insightech.er.editor.view.action.option.notation.ChangeTitleFontSizeAction;
import org.insightech.er.editor.view.action.option.notation.design.ChangeDesignToFrameAction;
import org.insightech.er.editor.view.action.option.notation.design.ChangeDesignToFunnyAction;
import org.insightech.er.editor.view.action.option.notation.design.ChangeDesignToSimpleAction;
import org.insightech.er.editor.view.action.option.notation.level.ChangeNotationLevelToColumnAction;
import org.insightech.er.editor.view.action.option.notation.level.ChangeNotationLevelToDetailAction;
import org.insightech.er.editor.view.action.option.notation.level.ChangeNotationLevelToExcludeTypeAction;
import org.insightech.er.editor.view.action.option.notation.level.ChangeNotationLevelToNameAndKeyAction;
import org.insightech.er.editor.view.action.option.notation.level.ChangeNotationLevelToOnlyKeyAction;
import org.insightech.er.editor.view.action.option.notation.level.ChangeNotationLevelToOnlyTitleAction;
import org.insightech.er.editor.view.action.option.notation.system.ChangeToIDEF1XNotationAction;
import org.insightech.er.editor.view.action.option.notation.system.ChangeToIENotationAction;
import org.insightech.er.editor.view.action.option.notation.type.ChangeViewToBothAction;
import org.insightech.er.editor.view.action.option.notation.type.ChangeViewToLogicalAction;
import org.insightech.er.editor.view.action.option.notation.type.ChangeViewToPhysicalAction;
import org.insightech.er.editor.view.action.printer.PageSettingAction;
import org.insightech.er.editor.view.action.search.SearchAction;
import org.insightech.er.editor.view.action.testdata.TestDataCreateAction;
import org.insightech.er.editor.view.action.tracking.ChangeTrackingAction;
import org.insightech.er.editor.view.action.translation.TranslationManageAction;

public class ERDiagramPopupMenuManager extends MenuManager {

	private ActionRegistry actionRegistry;

	public ERDiagramPopupMenuManager(ActionRegistry actionRegistry,
			final ERDiagram diagram) {
		ISharedImages sharedImages = PlatformUI.getWorkbench()
				.getSharedImages();

		this.actionRegistry = actionRegistry;

		final IAction changeViewToPhysicalAction = getAction(ChangeViewToPhysicalAction.ID);
		final IAction changeViewToLogicalAction = getAction(ChangeViewToLogicalAction.ID);
		final IAction changeViewToBothAction = getAction(ChangeViewToBothAction.ID);

		final IAction changeToIENotationAction = getAction(ChangeToIENotationAction.ID);
		final IAction changeToIDEF1XNotationAction = getAction(ChangeToIDEF1XNotationAction.ID);

		final IAction changeNotationLevelToOnlyTitleAction = getAction(ChangeNotationLevelToOnlyTitleAction.ID);
		final IAction changeNotationLevelToOnlyKeyAction = getAction(ChangeNotationLevelToOnlyKeyAction.ID);
		final IAction changeNotationLevelToColumnAction = getAction(ChangeNotationLevelToColumnAction.ID);
		final IAction changeNotationLevelToDetailAction = getAction(ChangeNotationLevelToDetailAction.ID);
		final IAction changeNotationLevelToExcludeTypeAction = getAction(ChangeNotationLevelToExcludeTypeAction.ID);
		final IAction changeNotationLevelToNameAndKeyAction = getAction(ChangeNotationLevelToNameAndKeyAction.ID);

		final IAction changeNotationExpandGroupAction = getAction(ChangeNotationExpandGroupAction.ID);

		final IAction changeDesignToFunnyAction = getAction(ChangeDesignToFunnyAction.ID);
		final IAction changeDesignToFrameAction = getAction(ChangeDesignToFrameAction.ID);
		final IAction changeDesignToSimpleAction = getAction(ChangeDesignToSimpleAction.ID);

		final IAction changeCapitalAction = getAction(ChangeCapitalAction.ID);
		final IAction changeTitleFontSizeAction = getAction(ChangeTitleFontSizeAction.ID);
		final IAction changeStampAction = getAction(ChangeStampAction.ID);

		final IAction changeFreeLayoutAction = getAction(ChangeFreeLayoutAction.ID);
		final IAction changeShowReferredTablesAction = getAction(ChangeShowReferredTablesAction.ID);

		final IAction undoAction = this.getAction(ActionFactory.UNDO);
		undoAction.setActionDefinitionId("org.eclipse.ui.edit.undo");

		final IAction redoAction = this.getAction(ActionFactory.REDO);
		redoAction.setActionDefinitionId("org.eclipse.ui.edit.redo");

		this.add(undoAction);
		this.add(redoAction);

		final IAction copyAction = this.getAction(ActionFactory.COPY);
		copyAction.setActionDefinitionId("org.eclipse.ui.edit.copy");
		this.add(copyAction);

		final IAction pasteAction = this.getAction(ActionFactory.PASTE);
		pasteAction.setActionDefinitionId("org.eclipse.ui.edit.paste");
		this.add(pasteAction);

		this.add(this.getAction(ActionFactory.DELETE));
		this.add(this.getAction(ActionFactory.SELECT_ALL));
		this.add(this.getAction(EditAllAttributesAction.ID));

		this.add(new Separator());

		this.add(this.getAction(ResizeModelAction.ID));
		this.add(this.getAction(RightAngleLineAction.ID));
		this.add(this.getAction(DefaultLineAction.ID));

		this.add(new Separator());

		this.add(this.getAction(SearchAction.ID));

		this.add(new Separator());

		MenuManager displayMenu = new MenuManager(ResourceString
				.getResourceString("label.display"));

		MenuManager viewModeMenu = new MenuManager(ResourceString
				.getResourceString("label.view.mode"));
		viewModeMenu.add(changeViewToPhysicalAction);
		viewModeMenu.add(changeViewToLogicalAction);
		viewModeMenu.add(changeViewToBothAction);

		displayMenu.add(viewModeMenu);

		MenuManager notationMenu = new MenuManager(ResourceString
				.getResourceString("label.notation"));
		notationMenu.add(changeToIENotationAction);
		notationMenu.add(changeToIDEF1XNotationAction);

		displayMenu.add(notationMenu);

		MenuManager notationLevelMenu = new MenuManager(ResourceString
				.getResourceString("label.notation.level"));
		notationLevelMenu.add(changeNotationLevelToOnlyTitleAction);
		notationLevelMenu.add(changeNotationLevelToOnlyKeyAction);
		notationLevelMenu.add(changeNotationLevelToColumnAction);
		notationLevelMenu.add(changeNotationLevelToNameAndKeyAction);
		notationLevelMenu.add(changeNotationLevelToExcludeTypeAction);
		notationLevelMenu.add(changeNotationLevelToDetailAction);

		notationLevelMenu.add(new Separator());

		notationLevelMenu.add(changeNotationExpandGroupAction);

		displayMenu.add(notationLevelMenu);

		MenuManager designMenu = new MenuManager(ResourceString
				.getResourceString("label.design"));

		designMenu.add(changeDesignToFunnyAction);
		designMenu.add(changeDesignToFrameAction);
		designMenu.add(changeDesignToSimpleAction);

		displayMenu.add(designMenu);

		displayMenu.add(changeCapitalAction);
		displayMenu.add(changeTitleFontSizeAction);
		displayMenu.add(changeStampAction);

		this.add(displayMenu);

		this.add(new Separator());

		MenuManager importMenu = new MenuManager(ResourceString
				.getResourceString("action.title.import"), sharedImages
				.getImageDescriptor("IMG_ETOOL_IMPORT_WIZ"), "Import");

		importMenu.add(this.getAction(ImportFromDBAction.ID));
		importMenu.add(this.getAction(ImportFromFileAction.ID));

		this.add(importMenu);

		MenuManager exportMenu = new MenuManager(ResourceString
				.getResourceString("action.title.export"), sharedImages
				.getImageDescriptor("IMG_ETOOL_EXPORT_WIZ"), "Export");

		exportMenu.add(this.getAction(ExportToImageAction.ID));
		exportMenu.add(this.getAction(ExportToExcelAction.ID));
		exportMenu.add(this.getAction(ExportToHtmlAction.ID));
		exportMenu.add(this.getAction(ExportToDDLAction.ID));
		exportMenu.add(this.getAction(ExportToDictionaryAction.ID));
		exportMenu.add(this.getAction(ExportToTranslationDictionaryAction.ID));
		exportMenu.add(this.getAction(ExportToTestDataAction.ID));
		exportMenu.add(this.getAction(ExportToJavaAction.ID));
		exportMenu.add(new GroupMarker("export"));

		this.add(exportMenu);

		this.add(new Separator());

		this.add(this.getAction(PageSettingAction.ID));
		this.add(this.getAction(ChangeTrackingAction.ID));
		this.add(this.getAction(TranslationManageAction.ID));
		this.add(this.getAction(TestDataCreateAction.ID));

		MenuManager categoryMenu = new MenuManager(ResourceString
				.getResourceString("label.category"));
		categoryMenu.add(this.getAction(CategoryManageAction.ID));
		// categoryMenu.add(changeFreeLayoutAction);
		categoryMenu.add(changeShowReferredTablesAction);

		this.add(categoryMenu);
		this.add(this.getAction(ERModelAddAction.ID)); // ERModel作成
//		this.add(this.getAction(PlaceTableAction.ID)); // テーブル配置

		this.add(this.getAction(OptionSettingAction.ID));

		this.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {
				undoAction.setText(ResourceString
						.getResourceString("action.title.undo"));
				redoAction.setText(ResourceString
						.getResourceString("action.title.redo"));

				Settings settings = diagram.getDiagramContents().getSettings();

				changeViewToPhysicalAction.setChecked(false);
				changeViewToLogicalAction.setChecked(false);
				changeViewToBothAction.setChecked(false);

				if (settings.getViewMode() == Settings.VIEW_MODE_PHYSICAL) {
					changeViewToPhysicalAction.setChecked(true);

				} else if (settings.getViewMode() == Settings.VIEW_MODE_LOGICAL) {
					changeViewToLogicalAction.setChecked(true);

				} else {
					changeViewToBothAction.setChecked(true);
				}

				changeToIENotationAction.setChecked(false);
				changeToIDEF1XNotationAction.setChecked(false);

				if (Settings.NOTATION_IDEF1X.equals(settings.getNotation())) {
					changeToIDEF1XNotationAction.setChecked(true);

				} else {
					changeToIENotationAction.setChecked(true);
				}

				changeNotationLevelToOnlyTitleAction.setChecked(false);
				changeNotationLevelToOnlyKeyAction.setChecked(false);
				changeNotationLevelToColumnAction.setChecked(false);
				changeNotationLevelToNameAndKeyAction.setChecked(false);
				changeNotationLevelToExcludeTypeAction.setChecked(false);
				changeNotationLevelToDetailAction.setChecked(false);

				if (settings.getNotationLevel() == Settings.NOTATION_LEVLE_TITLE) {
					changeNotationLevelToOnlyTitleAction.setChecked(true);

				} else if (settings.getNotationLevel() == Settings.NOTATION_LEVLE_COLUMN) {
					changeNotationLevelToColumnAction.setChecked(true);

				} else if (settings.getNotationLevel() == Settings.NOTATION_LEVLE_KEY) {
					changeNotationLevelToOnlyKeyAction.setChecked(true);

				} else if (settings.getNotationLevel() == Settings.NOTATION_LEVLE_NAME_AND_KEY) {
					changeNotationLevelToNameAndKeyAction.setChecked(true);

				} else if (settings.getNotationLevel() == Settings.NOTATION_LEVLE_EXCLUDE_TYPE) {
					changeNotationLevelToExcludeTypeAction.setChecked(true);

				} else {
					changeNotationLevelToDetailAction.setChecked(true);
				}

				if (settings.isNotationExpandGroup()) {
					changeNotationExpandGroupAction.setChecked(true);
				}

				changeDesignToFunnyAction.setChecked(false);
				changeDesignToFrameAction.setChecked(false);
				changeDesignToSimpleAction.setChecked(false);

				if (settings.getTableStyle().equals(
						ChangeDesignToFrameAction.TYPE)) {
					changeDesignToFrameAction.setChecked(true);

				} else if (settings.getTableStyle().equals(
						ChangeDesignToSimpleAction.TYPE)) {
					changeDesignToSimpleAction.setChecked(true);

				} else {
					changeDesignToFunnyAction.setChecked(true);
				}

				if (settings.isCapital()) {
					changeCapitalAction.setChecked(true);
				}
				if (new BigDecimal("1.5").equals(settings.getTitleFontEm())) {
					changeTitleFontSizeAction.setChecked(true);
				}

				if (settings.getModelProperties().isDisplay()) {
					changeStampAction.setChecked(true);
				}

				CategorySetting categorySettings = settings
						.getCategorySetting();
				if (categorySettings.isFreeLayout()) {
					changeFreeLayoutAction.setChecked(true);
				}
				if (categorySettings.isShowReferredTables()) {
					changeShowReferredTablesAction.setChecked(true);
				}
			}

		});
	}

	private IAction getAction(ActionFactory actionFactory) {
		return this.actionRegistry.getAction(actionFactory.getId());
	}

	private IAction getAction(String id) {
		return this.actionRegistry.getAction(id);
	}

}
