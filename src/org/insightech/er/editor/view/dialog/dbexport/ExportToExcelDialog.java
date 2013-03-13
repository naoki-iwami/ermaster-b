package org.insightech.er.editor.view.dialog.dbexport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.insightech.er.Activator;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.FileText;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.excel.ExportToExcelManager;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.settings.ExportSetting;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.action.dbexport.ExportToImageAction;
import org.insightech.er.preference.PreferenceInitializer;
import org.insightech.er.preference.template.TemplatePreferencePage;
import org.insightech.er.util.Format;
import org.insightech.er.util.io.FileUtils;

public class ExportToExcelDialog extends AbstractDialog {

	private Combo templateCombo;

	private FileText outputExcelFileText;

	private FileText outputImageFileText;

	private Combo categoryCombo;

	private Button useLogicalNameAsSheetNameButton;

	private Button outputImageButton;

	private Button openAfterSavedButton;

	private ERDiagram diagram;

	private IEditorPart editorPart;

	private GraphicalViewer viewer;

	private ExportSetting exportSetting;

	public ExportToExcelDialog(Shell parentShell, ERDiagram diagram,
			IEditorPart editorPart, GraphicalViewer viewer) {
		super(parentShell, 3);

		this.diagram = diagram;
		this.editorPart = editorPart;
		this.viewer = viewer;
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

		CompositeFactory.createLabel(parent, "label.template");

		this.createTemplateCombo(parent);

		CompositeFactory.createLabel(parent, "label.output.excel.file");

		this.outputExcelFileText = new FileText(parent, SWT.BORDER, ".xls");
		this.outputExcelFileText.setLayoutData(gridData);

		this.outputExcelFileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});

		CompositeFactory.createLabel(parent, "label.output.image.file");

		this.outputImageFileText = new FileText(parent, SWT.BORDER,
				new String[] { "*.png", "*.jpeg" });
		this.outputImageFileText.setLayoutData(gridData);

		this.outputImageFileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});

		CompositeFactory.createLabel(parent, "label.category");

		createCategoryCombo(parent);

		GridData optionCheckGridData = new GridData();
		optionCheckGridData.horizontalSpan = 3;

		this.useLogicalNameAsSheetNameButton = new Button(parent, SWT.CHECK);
		this.useLogicalNameAsSheetNameButton.setText(ResourceString
				.getResourceString("label.use.logical.name.as.sheet.name"));
		this.useLogicalNameAsSheetNameButton.setLayoutData(optionCheckGridData);

		this.outputImageButton = new Button(parent, SWT.CHECK);
		this.outputImageButton.setText(ResourceString
				.getResourceString("label.output.image.to.excel"));
		this.outputImageButton.setLayoutData(optionCheckGridData);

		this.openAfterSavedButton = new Button(parent, SWT.CHECK);
		this.openAfterSavedButton.setText(ResourceString
				.getResourceString("label.open.after.saved"));
		this.openAfterSavedButton.setLayoutData(optionCheckGridData);
	}

	private void createTemplateCombo(Composite parent) {
		GridData gridData = new GridData();
		gridData.widthHint = 200;
		gridData.horizontalSpan = 2;

		this.templateCombo = new Combo(parent, SWT.READ_ONLY);
		this.templateCombo.setLayoutData(gridData);
		this.templateCombo.setVisibleItemCount(20);

		this.templateCombo.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				validate();
			}
		});
	}

	private void createCategoryCombo(Composite parent) {
		GridData gridData = new GridData();
		gridData.widthHint = 200;
		gridData.horizontalSpan = 2;

		this.categoryCombo = new Combo(parent, SWT.READ_ONLY);
		this.categoryCombo.setLayoutData(gridData);
		this.categoryCombo.setVisibleItemCount(20);
	}

	@Override
	protected String getErrorMessage() {
		if (isBlank(this.templateCombo)) {
			return "error.template.is.empty";
		}
		if (this.outputExcelFileText.isBlank()) {
			return "error.output.excel.file.is.empty";
		}
		if (this.outputImageButton.getSelection()
				&& this.outputImageFileText.isBlank()) {
			return "error.output.image.file.is.empty";
		}

		return null;
	}

	@Override
	protected void perfomeOK() throws InputException {
		Category currentCategory = this.diagram.getCurrentCategory();
		int currentCategoryIndex = this.diagram.getCurrentCategoryIndex();

		setCurrentCategory();

		InputStream stream = null;

		try {
			ProgressMonitorDialog monitor = new ProgressMonitorDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getShell());

			String templateName = this.templateCombo.getText();
			String outputExcelFilePath = this.outputExcelFileText.getFilePath();
			String outputImageFilePath = this.outputImageFileText.getFilePath();
			byte[] imageBuffer = null;
			int excelPictureType = -1;

			boolean outputImage = this.outputImageButton.getSelection();

			if (outputImage) {
				int imageFormat = ExportToImageAction.outputImage(monitor,
						this.viewer, outputImageFilePath);

				if (imageFormat == -1) {
					throw new InputException(null);

				} else {
					imageBuffer = FileUtils.readFileToByteArray(new File(
							outputImageFilePath));

					if (imageFormat == SWT.IMAGE_JPEG) {
						excelPictureType = HSSFWorkbook.PICTURE_TYPE_JPEG;

					} else if (imageFormat == SWT.IMAGE_PNG) {
						excelPictureType = HSSFWorkbook.PICTURE_TYPE_PNG;

					} else {
						Activator
								.showMessageDialog("dialog.message.export.image.not.supported");
						throw new InputException(null);
					}
				}
			}

			stream = this.getTemplate();

			ExportToExcelManager manager = new ExportToExcelManager(
					outputExcelFilePath, diagram, stream,
					this.useLogicalNameAsSheetNameButton.getSelection(),
					imageBuffer, excelPictureType);
			monitor.run(true, true, manager);

			boolean openAfterSaved = this.openAfterSavedButton.getSelection();

			if (openAfterSaved) {
				File fileToOpen = new File(outputExcelFilePath);
				URI uri = URIUtil.fromString(fileToOpen.toURL().toString());

				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();

				IDE.openEditor(page, uri,
						IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID, true);
			}

			this.exportSetting = this.diagram.getDiagramContents()
					.getSettings().getExportSetting().clone();

			this.exportSetting.setExcelOutput(outputExcelFilePath);
			this.exportSetting.setImageOutput(outputImageFilePath);
			this.exportSetting.setExcelTemplate(templateName);

			this.exportSetting
					.setUseLogicalNameAsSheet(this.useLogicalNameAsSheetNameButton
							.getSelection());
			this.exportSetting.setPutERDiagramOnExcel(outputImage);

			this.exportSetting.setCategoryNameToExport(this.categoryCombo
					.getText());
			this.exportSetting.setOpenAfterSaved(openAfterSaved);

			if (manager.getException() != null) {
				throw manager.getException();
			}

		} catch (IOException e) {
			Activator.showMessageDialog(e.getMessage());

		} catch (InterruptedException e) {

		} catch (Exception e) {
			Activator.showExceptionDialog(e);

		} finally {
			this.diagram.setCurrentCategory(currentCategory,
					currentCategoryIndex);

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

	private void setCurrentCategory() {
		if (this.categoryCombo.getSelectionIndex() == 0) {
			this.diagram.setCurrentCategory(null, 0);
			return;
		}

		Category currentCategory = this.diagram.getDiagramContents()
				.getSettings().getCategorySetting().getAllCategories().get(
						this.categoryCombo.getSelectionIndex() - 1);

		this.diagram.setCurrentCategory(currentCategory, this.categoryCombo
				.getSelectionIndex());
	}

	private InputStream getTemplate() {
		int index = this.templateCombo.getSelectionIndex();

		if (index == 0) {
			return TemplatePreferencePage.getDefaultExcelTemplateEn();

		} else if (index == 1) {
			return TemplatePreferencePage.getDefaultExcelTemplateJa();

		} else {
			String templateName = this.templateCombo.getText();

			File file = new File(PreferenceInitializer
					.getTemplatePath(templateName));
			try {
				return new FileInputStream(file);

			} catch (FileNotFoundException e) {
				Activator.showExceptionDialog(e);
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setData() {
		Settings settings = this.diagram.getDiagramContents().getSettings();

		String outputExcel = Format.null2blank(settings.getExportSetting()
				.getExcelOutput());
		String outputImage = Format.null2blank(settings.getExportSetting()
				.getImageOutput());
		String template = settings.getExportSetting().getExcelTemplate();

		this.templateCombo.add(ResourceString
				.getResourceString("label.template.default.en"));
		this.templateCombo.add(ResourceString
				.getResourceString("label.template.default.ja"));

		// select the previous
		for (int i = 0; i < this.templateCombo.getItemCount(); i++) {
			String item = this.templateCombo.getItem(i);
			if (item != null
					&& item.equals(settings.getExportSetting()
							.getExcelTemplate())) {
				this.templateCombo.select(i);
				break;
			}
		}

		String str = Activator.getDefault().getPreferenceStore().getString(
				PreferenceInitializer.TEMPLATE_FILE_LIST);
		List<String> fileNames = this.parseString(str);

		int index = 1;
		for (String fileName : fileNames) {
			File file = new File(PreferenceInitializer
					.getTemplatePath(fileName));
			if (file.exists()) {
				this.templateCombo.add(fileName);

				if (fileName.equals(template)) {
					this.templateCombo.select(index);
				}

				index++;
			}
		}

		if (this.templateCombo.getSelectionIndex() == -1) {
			this.templateCombo.select(0);
		}

		if ("".equals(outputExcel)) {
			IFile file = ((IFileEditorInput) editorPart.getEditorInput())
					.getFile();
			outputExcel = file.getLocation().toOSString();
		}
		outputExcel = outputExcel.substring(0, outputExcel.lastIndexOf("."))
				+ ".xls";

		if ("".equals(outputImage)) {
			IFile file = ((IFileEditorInput) editorPart.getEditorInput())
					.getFile();
			outputImage = file.getLocation().toOSString();
			outputImage = outputImage
					.substring(0, outputImage.lastIndexOf("."))
					+ ".png";
		}

		this.outputExcelFileText.setText(outputExcel);
		this.outputImageFileText.setText(outputImage);

		// set categories combo
		this.categoryCombo.add(ResourceString.getResourceString("label.all"));

		for (Category category : this.diagram.getDiagramContents()
				.getSettings().getCategorySetting().getAllCategories()) {
			this.categoryCombo.add(category.getName());
		}

		this.categoryCombo.select(0);

		// set previous selected category
		if (settings.getExportSetting().getCategoryNameToExport() != null) {
			for (int i = 1; i < this.categoryCombo.getItemCount(); i++) {
				if (settings.getExportSetting().getCategoryNameToExport()
						.equals(this.categoryCombo.getItem(i))) {
					this.categoryCombo.select(i);
					break;
				}
			}
		}

		ExportSetting exportSetting = settings.getExportSetting();

		this.useLogicalNameAsSheetNameButton.setSelection(exportSetting
				.isUseLogicalNameAsSheet());
		this.outputImageButton.setSelection(exportSetting
				.isPutERDiagramOnExcel());
		this.openAfterSavedButton
				.setSelection(exportSetting.isOpenAfterSaved());
	}

	protected List<String> parseString(String stringList) {
		StringTokenizer st = new StringTokenizer(stringList, "/");
		List<String> list = new ArrayList<String>();

		while (st.hasMoreElements()) {
			list.add(st.nextToken());
		}

		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getTitle() {
		return "dialog.title.export.excel";
	}

}
