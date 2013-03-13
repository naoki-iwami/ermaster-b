package org.insightech.er.preference.template;

import org.eclipse.swt.widgets.Composite;
import org.insightech.er.preference.FileListEditor;
import org.insightech.er.preference.PreferenceInitializer;

public class TemplateFileListEditor extends FileListEditor {

	public TemplateFileListEditor(String name, String labelText,
			Composite parent) {
		super(name, labelText, parent, "*.xls");
	}

	@Override
	protected String getStorePath(String name) {
		return PreferenceInitializer.getTemplatePath(name);
	}

}
