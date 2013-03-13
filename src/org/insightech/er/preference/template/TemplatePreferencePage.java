package org.insightech.er.preference.template;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.insightech.er.Activator;
import org.insightech.er.ResourceString;
import org.insightech.er.preference.PreferenceInitializer;
import org.insightech.er.util.io.IOUtils;

public class TemplatePreferencePage extends
		org.eclipse.jface.preference.PreferencePage implements
		IWorkbenchPreferencePage {

	private static final String DEFAULT_TEMPLATE_FILE_EN = "template_en.xls";

	private static final String DEFAULT_TEMPLATE_FILE_JA = "template_ja.xls";

	private TemplateFileListEditor fileListEditor;

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

		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);

		this.fileListEditor = new TemplateFileListEditor(
				PreferenceInitializer.TEMPLATE_FILE_LIST, ResourceString
						.getResourceString("label.custom.tempplate"), composite);
		this.fileListEditor.load();

		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);

		Label label = new Label(composite, SWT.NONE);
		label.setText(ResourceString
				.getResourceString("dialog.message.template.file.store"));

		return composite;
	}

	private void createButtonComposite(Composite composite) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		Button buttonEn = new Button(composite, SWT.NONE);
		buttonEn.setText(ResourceString
				.getResourceString("label.button.download.template.en"));
		buttonEn.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				download(DEFAULT_TEMPLATE_FILE_EN);
			}
		});

		Button buttonJa = new Button(composite, SWT.NONE);
		buttonJa.setText(ResourceString
				.getResourceString("label.button.download.template.ja"));
		buttonJa.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				download(DEFAULT_TEMPLATE_FILE_JA);
			}
		});

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void performDefaults() {
		this.fileListEditor.loadDefault();

		super.performDefaults();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performOk() {
		this.fileListEditor.store();

		return super.performOk();
	}

	private void download(String fileName) {
		String filePath = Activator.showSaveDialog(fileName,
				new String[] { ".xls" });

		if (filePath != null) {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = this.getClass().getResourceAsStream("/" + fileName);
				out = new FileOutputStream(filePath);

				IOUtils.copy(in, out);
			} catch (IOException ioe) {
				Activator.showExceptionDialog(ioe);

			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e1) {
						Activator.showExceptionDialog(e1);
					}

				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException e1) {
						Activator.showExceptionDialog(e1);
					}
				}

			}
		}
	}

	public static InputStream getDefaultExcelTemplateEn() {
		return TemplatePreferencePage.class.getResourceAsStream("/"
				+ DEFAULT_TEMPLATE_FILE_EN);
	}

	public static InputStream getDefaultExcelTemplateJa() {
		return TemplatePreferencePage.class.getResourceAsStream("/"
				+ DEFAULT_TEMPLATE_FILE_JA);
	}
}
