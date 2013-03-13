package org.insightech.er.editor.view.dialog.dbexport;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.DirectoryText;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.java.ExportToJavaWithProgressManager;
import org.insightech.er.editor.model.settings.ExportSetting;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.settings.export.ExportJavaSetting;
import org.insightech.er.util.Format;

public class ExportToJavaDialog extends AbstractDialog {

	private DirectoryText outputDirText;

	private Text packageText;

	private Text classNameSuffixText;

	private Button withHibernateButton;

	private ERDiagram diagram;

	private IEditorPart editorPart;

	private ExportSetting exportSetting;

	private Combo fileEncodingCombo;

	public ExportToJavaDialog(Shell parentShell, ERDiagram diagram,
			IEditorPart editorPart) {
		super(parentShell, 3);

		this.diagram = diagram;
		this.editorPart = editorPart;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initLayout(GridLayout layout) {
		super.initLayout(layout);

		layout.verticalSpacing = 15;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite parent) {
		GridData gridData = new GridData();
		gridData.widthHint = 200;

		this.packageText = CompositeFactory.createText(this, parent,
				"label.package.name", 2, false);
		this.classNameSuffixText = CompositeFactory.createText(this, parent,
				"label.class.name.suffix", 2, false);

		CompositeFactory.createLabel(parent, "label.output.dir");
		this.outputDirText = new DirectoryText(parent, SWT.BORDER);
		this.outputDirText.setLayoutData(gridData);

		this.outputDirText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});

		this.fileEncodingCombo = CompositeFactory.createFileEncodingCombo(
				this.editorPart, this, parent, "label.output.file.encoding", 1);

		this.withHibernateButton = CompositeFactory.createCheckbox(this,
				parent, "label.with.hibernate", 2);
	}

	@Override
	protected String getErrorMessage() {
		if (this.outputDirText.isBlank()) {
			return "error.output.dir.is.empty";
		}

		return null;
	}

	@Override
	protected void perfomeOK() throws InputException {
		InputStream stream = null;

		try {
			ProgressMonitorDialog monitor = new ProgressMonitorDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getShell());

			String outputDirPath = this.outputDirText.getFilePath();
			String packageName = this.packageText.getText();
			String classNameSuffix = this.classNameSuffixText.getText();
			String fileEncoding = this.fileEncodingCombo.getText();
			boolean withHibernate = this.withHibernateButton.getSelection();

			this.exportSetting = this.diagram.getDiagramContents()
					.getSettings().getExportSetting().clone();
			ExportJavaSetting exportJavaSetting = this.exportSetting
					.getExportJavaSetting();

			exportJavaSetting.setJavaOutput(outputDirPath);
			exportJavaSetting.setPackageName(packageName);
			exportJavaSetting.setClassNameSuffix(classNameSuffix);
			exportJavaSetting.setSrcFileEncoding(fileEncoding);
			exportJavaSetting.setWithHibernate(withHibernate);

			ExportToJavaWithProgressManager manager = new ExportToJavaWithProgressManager(
					exportJavaSetting, diagram);
			monitor.run(true, true, manager);

			if (manager.getException() != null) {
				throw manager.getException();
			}

		} catch (IOException e) {
			Activator.showMessageDialog(e.getMessage());

		} catch (InterruptedException e) {

		} catch (Exception e) {
			Activator.showExceptionDialog(e);

		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					Activator.showExceptionDialog(e);
				}
			}
		}
	}

	public ExportSetting getExportSetting() {
		return this.exportSetting;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setData() {
		Settings settings = this.diagram.getDiagramContents().getSettings();
		ExportJavaSetting exportSetting = settings.getExportSetting()
				.getExportJavaSetting();

		String outputDir = Format.null2blank(exportSetting.getJavaOutput());

		if ("".equals(outputDir)) {
			IFile file = ((IFileEditorInput) editorPart.getEditorInput())
					.getFile();
			outputDir = file.getParent().getLocation().toOSString();
		}

		this.outputDirText.setText(outputDir);

		this.packageText.setText(Format.null2blank(exportSetting
				.getPackageName()));
		this.classNameSuffixText.setText(Format.null2blank(exportSetting
				.getClassNameSuffix()));

		String srcFileEncoding = Format.null2blank(exportSetting
				.getSrcFileEncoding());
		if (!"".equals(srcFileEncoding)) {
			this.fileEncodingCombo.setText(srcFileEncoding);
		}

		this.withHibernateButton.setSelection(exportSetting.isWithHibernate());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getTitle() {
		return "dialog.title.export.java";
	}
}
