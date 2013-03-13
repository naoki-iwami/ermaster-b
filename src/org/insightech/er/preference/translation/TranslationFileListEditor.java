package org.insightech.er.preference.translation;

import org.eclipse.swt.widgets.Composite;
import org.insightech.er.preference.FileListEditor;
import org.insightech.er.preference.PreferenceInitializer;

public class TranslationFileListEditor extends FileListEditor {

	public TranslationFileListEditor(String name, String labelText,
			Composite parent) {
		super(name, labelText, parent, "*.txt");
	}

	@Override
	protected String getStorePath(String name) {
		return PreferenceInitializer.getTranslationPath(name);
	}

}
