package org.insightech.er.preference.translation;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.insightech.er.ResourceString;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.preference.PreferenceInitializer;

public class TranslationPreferencePage extends
		PreferencePage implements
		IWorkbenchPreferencePage {

	private TranslationFileListEditor fileListEditor;

	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		Composite buttonComposite = new Composite(composite, SWT.NONE);
		this.createButtonComposite(buttonComposite);

		CompositeFactory.filler(composite, 4);

		this.fileListEditor = new TranslationFileListEditor(
				PreferenceInitializer.TRANSLATION_FILE_LIST,
				ResourceString
						.getResourceString("label.custom.dictionary.for.translation"),
				composite);
		this.fileListEditor.load();

		CompositeFactory.filler(composite, 2);

		Label label = new Label(composite, SWT.NONE);
		label.setText(ResourceString
				.getResourceString("dialog.message.translation.file.store"));
		new Label(composite, SWT.NONE);

		label = new Label(composite, SWT.NONE);
		label.setText(ResourceString
				.getResourceString("dialog.message.translation.file.encode"));
		new Label(composite, SWT.NONE);

		return composite;
	}

	private void createButtonComposite(Composite composite) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		new Label(composite, SWT.NONE);
	}

	@Override
	protected void performDefaults() {
		this.fileListEditor.loadDefault();

		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		this.fileListEditor.store();

		return super.performOk();
	}

	
}
