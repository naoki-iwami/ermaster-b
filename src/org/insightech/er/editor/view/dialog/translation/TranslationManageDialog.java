package org.insightech.er.editor.view.dialog.translation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.settings.TranslationSetting;

public class TranslationManageDialog extends AbstractDialog {

	private static final int BUTTON_WIDTH = 60;

	private Table dictionaryTable = null;

	private TranslationSetting translationSettings;

	private Map<String, TableEditor> translationCheckMap;

	private Button useButton;

	private List<String> allTranslations;

	public TranslationManageDialog(Shell parentShell, Settings settings,
			ERDiagram diagram) {
		super(parentShell, 1);

		this.translationSettings = settings.getTranslationSetting();
		this.allTranslations = this.translationSettings.getAllTranslations();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite composite) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;

		Group group = new Group(composite, SWT.NONE);
		group.setText(ResourceString
				.getResourceString("label.translation.message"));
		group.setLayout(gridLayout);

		GridData gridData = new GridData();
		gridData.horizontalSpan = 4;

		this.useButton = new Button(group, SWT.CHECK);
		this.useButton.setText(ResourceString
				.getResourceString("label.translation.use"));
		this.useButton.setLayoutData(gridData);

		GridData tableGridData = new GridData();
		tableGridData.heightHint = 200;
		tableGridData.horizontalSpan = 3;
		tableGridData.verticalSpan = 2;

		this.dictionaryTable = new Table(group, SWT.BORDER | SWT.FULL_SELECTION);
		this.dictionaryTable.setHeaderVisible(true);
		this.dictionaryTable.setLayoutData(tableGridData);
		this.dictionaryTable.setLinesVisible(true);

		GridData upButtonGridData = new GridData();
		upButtonGridData.grabExcessHorizontalSpace = false;
		upButtonGridData.verticalAlignment = GridData.END;
		upButtonGridData.grabExcessVerticalSpace = true;
		upButtonGridData.widthHint = BUTTON_WIDTH;

		GridData downButtonGridData = new GridData();
		downButtonGridData.grabExcessVerticalSpace = true;
		downButtonGridData.verticalAlignment = GridData.BEGINNING;
		downButtonGridData.widthHint = BUTTON_WIDTH;

		GridData textGridData = new GridData();
		textGridData.widthHint = 150;

		TableColumn tableColumn = new TableColumn(dictionaryTable, SWT.NONE);
		tableColumn.setWidth(30);
		tableColumn.setResizable(false);
		TableColumn tableColumn1 = new TableColumn(dictionaryTable, SWT.NONE);
		tableColumn1.setWidth(230);
		tableColumn1.setResizable(false);
		tableColumn1.setText(ResourceString
				.getResourceString("label.translation.file.name"));
	}

	private void setUse(boolean use) {
		this.dictionaryTable.setEnabled(use);
	}

	private void initTranslationTable() {
		this.dictionaryTable.removeAll();

		if (this.translationCheckMap != null) {
			for (TableEditor editor : this.translationCheckMap.values()) {
				editor.getEditor().dispose();
				editor.dispose();
			}

			this.translationCheckMap.clear();
		} else {
			this.translationCheckMap = new HashMap<String, TableEditor>();
		}

		for (String translation : this.allTranslations) {
			TableItem tableItem = new TableItem(this.dictionaryTable, SWT.NONE);

			Button selectCheckButton = new Button(this.dictionaryTable,
					SWT.CHECK);
			selectCheckButton.pack();

			TableEditor editor = new TableEditor(this.dictionaryTable);

			editor.minimumWidth = selectCheckButton.getSize().x;
			editor.horizontalAlignment = SWT.CENTER;
			editor.setEditor(selectCheckButton, tableItem, 0);

			tableItem.setText(1, translation);

			if (translationSettings.isSelected(translation)) {
				selectCheckButton.setSelection(true);
			}

			this.translationCheckMap.put(translation, editor);
		}
	}

	@Override
	protected void addListener() {
		this.useButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				setUse(useButton.getSelection());
			}
		});
	}

	@Override
	protected String getTitle() {
		return "label.translation";
	}

	@Override
	protected void perfomeOK() throws InputException {
		validatePage();
	}

	@Override
	protected void setData() {
		this.initTranslationTable();
		this.useButton.setSelection(this.translationSettings.isUse());
		this.setUse(this.translationSettings.isUse());
	}

	@Override
	protected String getErrorMessage() {
		return null;
	}

	public void validatePage() {
		List<String> selectedTranslations = new ArrayList<String>();

		for (String translation : this.allTranslations) {
			Button button = (Button) this.translationCheckMap.get(translation)
					.getEditor();

			if (button.getSelection()) {
				selectedTranslations.add(translation);
			}
		}

		this.translationSettings.setSelectedTranslations(selectedTranslations);
		this.translationSettings.setUse(this.useButton.getSelection());
	}
}
