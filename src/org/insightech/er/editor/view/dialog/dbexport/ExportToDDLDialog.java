package org.insightech.er.editor.view.dialog.dbexport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
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
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.dbexport.ddl.DDLTarget;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.Validator;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.settings.Environment;
import org.insightech.er.editor.model.settings.ExportSetting;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public class ExportToDDLDialog extends AbstractDialog {

	private Combo environmentCombo;

	private FileText outputFileText;

	private Combo fileEncodingCombo;

	private Combo categoryCombo;

	private Button inlineTableComment;

	private Button inlineColumnComment;

	private ERDiagram diagram;

	private IEditorPart editorPart;

	private Button dropTablespace;

	private Button dropSequence;

	private Button dropTrigger;

	private Button dropView;

	private Button dropIndex;

	private Button dropTable;

	private Button createTablespace;

	private Button createSequence;

	private Button createTrigger;

	private Button createView;

	private Button createIndex;

	private Button createTable;

	private Button createForeignKey;

	private Button createComment;

	private Button commentValueDescription;

	private Button commentValueLogicalName;

	private Button commentValueLogicalNameDescription;

	private Button commentReplaceLineFeed;

	private Text commentReplaceString;

	private Button openAfterSavedButton;

	private ExportSetting exportSetting;

	public ExportToDDLDialog(Shell parentShell, ERDiagram diagram,
			IEditorPart editorPart, GraphicalViewer viewer) {
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

		this.environmentCombo = CompositeFactory.createReadOnlyCombo(this,
				parent, "label.tablespace.environment", 2, -1);

		CompositeFactory.createLabel(parent, "label.output.file");
		this.outputFileText = new FileText(parent, SWT.BORDER, ".sql");
		this.outputFileText.setLayoutData(gridData);

		this.fileEncodingCombo = CompositeFactory.createFileEncodingCombo(
				this.editorPart, this, parent, "label.output.file.encoding", 2);

		this.categoryCombo = CompositeFactory.createReadOnlyCombo(this, parent,
				"label.category", 2, -1);
		this.initCategoryCombo();

		this.createCheckboxComposite(parent);

		this.createCommentComposite(parent);

		GridData optionCheckGridData = new GridData();
		optionCheckGridData.horizontalSpan = 3;

		this.openAfterSavedButton = new Button(parent, SWT.CHECK);
		this.openAfterSavedButton.setText(ResourceString
				.getResourceString("label.open.after.saved"));
		this.openAfterSavedButton.setLayoutData(optionCheckGridData);
	}

	private void initCategoryCombo() {
		this.categoryCombo.add(ResourceString.getResourceString("label.all"));

		for (Category category : this.diagram.getDiagramContents()
				.getSettings().getCategorySetting().getAllCategories()) {
			this.categoryCombo.add(category.getName());
		}
	}

	private void createCheckboxComposite(Composite parent) {
		GridData gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(gridData);

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		this.createDropCheckboxGroup(composite);
		this.createCreateCheckboxGroup(composite);
	}

	private void createDropCheckboxGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		group.setLayoutData(gridData);

		group.setText("DROP");

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		group.setLayout(layout);

		this.dropTablespace = CompositeFactory.createCheckbox(this, group,
				"label.tablespace");
		this.dropSequence = CompositeFactory.createCheckbox(this, group,
				"label.sequence");
		this.dropTrigger = CompositeFactory.createCheckbox(this, group,
				"label.trigger");
		this.dropView = CompositeFactory.createCheckbox(this, group,
				"label.view");
		this.dropIndex = CompositeFactory.createCheckbox(this, group,
				"label.index");
		this.dropTable = CompositeFactory.createCheckbox(this, group,
				"label.table");
	}

	private void createCreateCheckboxGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		group.setLayoutData(gridData);

		group.setText("CREATE");

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		group.setLayout(layout);

		this.createTablespace = CompositeFactory.createCheckbox(this, group,
				"label.tablespace");
		this.createSequence = CompositeFactory.createCheckbox(this, group,
				"label.sequence");
		this.createTrigger = CompositeFactory.createCheckbox(this, group,
				"label.trigger");
		this.createView = CompositeFactory.createCheckbox(this, group,
				"label.view");
		this.createIndex = CompositeFactory.createCheckbox(this, group,
				"label.index");
		this.createTable = CompositeFactory.createCheckbox(this, group,
				"label.table");
		this.createForeignKey = CompositeFactory.createCheckbox(this, group,
				"label.foreign.key");
		this.createComment = CompositeFactory.createCheckbox(this, group,
				"label.comment");
	}

	private void createCommentComposite(Composite parent) {
		GridData gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(gridData);

		GridLayout compositeLayout = new GridLayout();
		composite.setLayout(compositeLayout);

		Group group = new Group(composite, SWT.NONE);
		group.setLayoutData(gridData);
		group.setText(ResourceString.getResourceString("label.comment"));

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		group.setLayout(layout);

		GridData commentValueGridData = new GridData();
		commentValueGridData.horizontalSpan = 3;
		commentValueGridData.horizontalAlignment = GridData.FILL;
		commentValueGridData.grabExcessHorizontalSpace = true;

		Group commentValueGroup = new Group(group, SWT.NONE);
		commentValueGroup.setLayoutData(commentValueGridData);
		commentValueGroup.setText(ResourceString
				.getResourceString("label.comment.value"));

		GridLayout commentValueLayout = new GridLayout();
		commentValueLayout.numColumns = 1;
		commentValueGroup.setLayout(commentValueLayout);

		this.commentValueDescription = CompositeFactory.createRadio(this,
				commentValueGroup, "label.comment.value.description");
		this.commentValueLogicalName = CompositeFactory.createRadio(this,
				commentValueGroup, "label.comment.value.logical.name");
		this.commentValueLogicalNameDescription = CompositeFactory.createRadio(
				this, commentValueGroup,
				"label.comment.value.logical.name.description");

		this.commentReplaceLineFeed = CompositeFactory.createCheckbox(this,
				group, "label.comment.replace.line.feed");
		this.commentReplaceString = CompositeFactory.createText(this, group,
				"label.comment.replace.string", 1, 20, false);

		this.inlineTableComment = CompositeFactory.createCheckbox(this, group,
				"label.comment.inline.table", 4);
		this.inlineColumnComment = CompositeFactory.createCheckbox(this, group,
				"label.comment.inline.column", 4);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addListener() {
		super.addListener();

		this.environmentCombo.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				validate();
			}
		});

		this.outputFileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});

		this.fileEncodingCombo.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validate();
			}
		});
	}

	@Override
	protected String getErrorMessage() {
		if (isBlank(this.environmentCombo)) {
			return "error.tablespace.environment.empty";
		}

		if (this.outputFileText.isBlank()) {
			return "error.output.file.is.empty";
		}

		if (!Charset.isSupported(this.fileEncodingCombo.getText())) {
			return "error.file.encoding.is.not.supported";
		}

		return null;
	}

	@Override
	protected void perfomeOK() throws InputException {
		String saveFilePath = this.outputFileText.getFilePath();

		DDLTarget ddlTarget = new DDLTarget();
		ddlTarget.dropTablespace = this.dropTablespace.getSelection();
		ddlTarget.dropSequence = this.dropSequence.getSelection();
		ddlTarget.dropTrigger = this.dropTrigger.getSelection();
		ddlTarget.dropView = this.dropView.getSelection();
		ddlTarget.dropIndex = this.dropIndex.getSelection();
		ddlTarget.dropTable = this.dropTable.getSelection();
		ddlTarget.createTablespace = this.createTablespace.getSelection();
		ddlTarget.createSequence = this.createSequence.getSelection();
		ddlTarget.createTrigger = this.createTrigger.getSelection();
		ddlTarget.createView = this.createView.getSelection();
		ddlTarget.createIndex = this.createIndex.getSelection();
		ddlTarget.createTable = this.createTable.getSelection();
		ddlTarget.createForeignKey = this.createForeignKey.getSelection();
		ddlTarget.createComment = this.createComment.getSelection();
		ddlTarget.inlineTableComment = this.inlineTableComment.getSelection();
		ddlTarget.inlineColumnComment = this.inlineColumnComment.getSelection();
		ddlTarget.commentReplaceLineFeed = this.commentReplaceLineFeed
				.getSelection();
		ddlTarget.commentReplaceString = this.commentReplaceString.getText();
		ddlTarget.commentValueDescription = this.commentValueDescription
				.getSelection();
		ddlTarget.commentValueLogicalName = this.commentValueLogicalName
				.getSelection();
		ddlTarget.commentValueLogicalNameDescription = this.commentValueLogicalNameDescription
				.getSelection();

		boolean openAfterSaved = this.openAfterSavedButton.getSelection();

		this.exportSetting = this.diagram.getDiagramContents().getSettings()
				.getExportSetting().clone();

		this.exportSetting.setDdlOutput(saveFilePath);
		this.exportSetting.setDdlTarget(ddlTarget);
		this.exportSetting
				.setCategoryNameToExport(this.categoryCombo.getText());
		this.exportSetting.setOpenAfterSaved(openAfterSaved);

		Validator validator = new Validator();

		List<ValidateResult> errorList = validator.validate(this.diagram);

		if (!errorList.isEmpty()) {
			ExportWarningDialog dialog = new ExportWarningDialog(PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getShell(),
					errorList);

			if (dialog.open() != IDialogConstants.OK_ID) {
				return;
			}
		}

		Category currentCategory = this.diagram.getCurrentCategory();
		int currentCategoryIndex = this.diagram.getCurrentCategoryIndex();

		setCurrentCategory();

		PrintWriter out = null;

		try {
			DDLCreator ddlCreator = DBManagerFactory.getDBManager(this.diagram)
					.getDDLCreator(this.diagram, true);

			int index = this.environmentCombo.getSelectionIndex();
			Environment environment = this.diagram.getDiagramContents()
					.getSettings().getEnvironmentSetting().getEnvironments()
					.get(index);

			ddlCreator.init(environment, ddlTarget);

			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(saveFilePath), getEncoding())));

			out.println(ddlCreator.getDropDDL(this.diagram));
			out.println(ddlCreator.getCreateDDL(this.diagram));

		} catch (Exception e) {
			Activator.log(e);
			Activator.showMessageDialog(e.getMessage());

		} finally {
			this.diagram.setCurrentCategory(currentCategory,
					currentCategoryIndex);

			if (out != null) {
				out.close();
			}
		}

		if (openAfterSaved) {
			try {
				File fileToOpen = new File(saveFilePath);
				URI uri = URIUtil.fromString(fileToOpen.toURL().toString());

				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				IFileStore fileStore = EFS.getStore(uri);
				IDE.openEditorOnFileStore(page, fileStore);

			} catch (Exception e) {
				Activator.showExceptionDialog(e);
			}
		}
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setData() {
		Settings settings = this.diagram.getDiagramContents().getSettings();

		for (Environment environment : settings.getEnvironmentSetting()
				.getEnvironments()) {
			this.environmentCombo.add(environment.getName());
		}
		this.environmentCombo.select(0);

		ExportSetting exportSetting = settings.getExportSetting();

		String outputFile = Format.null2blank(exportSetting.getDdlOutput());

		if (Check.isEmpty(outputFile)) {
			IFile file = ((IFileEditorInput) editorPart.getEditorInput())
					.getFile();
			outputFile = file.getLocation().toOSString();
			outputFile = outputFile.substring(0, outputFile.lastIndexOf("."))
					+ ".sql";
		}

		this.outputFileText.setText(outputFile);

		this.categoryCombo.select(0);

		// set previous selected category
		if (exportSetting.getCategoryNameToExport() != null) {
			for (int i = 1; i < this.categoryCombo.getItemCount(); i++) {
				if (exportSetting.getCategoryNameToExport().equals(
						this.categoryCombo.getItem(i))) {
					this.categoryCombo.select(i);
					break;
				}
			}
		}

		DDLTarget ddlTarget = exportSetting.getDdlTarget();

		this.dropIndex.setSelection(ddlTarget.dropIndex);
		this.dropSequence.setSelection(ddlTarget.dropSequence);
		this.dropTable.setSelection(ddlTarget.dropTable);
		this.dropTablespace.setSelection(ddlTarget.dropTablespace);
		this.dropTrigger.setSelection(ddlTarget.dropTrigger);
		this.dropView.setSelection(ddlTarget.dropView);
		this.createComment.setSelection(ddlTarget.createComment);
		this.createForeignKey.setSelection(ddlTarget.createForeignKey);
		this.createIndex.setSelection(ddlTarget.createIndex);
		this.createSequence.setSelection(ddlTarget.createSequence);
		this.createTable.setSelection(ddlTarget.createTable);
		this.createTablespace.setSelection(ddlTarget.createTablespace);
		this.createTrigger.setSelection(ddlTarget.createTrigger);
		this.createView.setSelection(ddlTarget.createView);
		this.inlineColumnComment.setSelection(ddlTarget.inlineColumnComment);
		this.inlineTableComment.setSelection(ddlTarget.inlineTableComment);
		this.commentReplaceLineFeed
				.setSelection(ddlTarget.commentReplaceLineFeed);
		this.commentReplaceString.setText(Format
				.null2blank(ddlTarget.commentReplaceString));
		this.commentValueDescription
				.setSelection(ddlTarget.commentValueDescription);
		this.commentValueLogicalName
				.setSelection(ddlTarget.commentValueLogicalName);
		this.commentValueLogicalNameDescription
				.setSelection(ddlTarget.commentValueLogicalNameDescription);

		if (!ddlTarget.commentValueDescription
				&& !ddlTarget.commentValueLogicalName
				&& !ddlTarget.commentValueLogicalNameDescription) {
			this.commentValueDescription.setSelection(true);
		}

		this.openAfterSavedButton
				.setSelection(exportSetting.isOpenAfterSaved());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getTitle() {
		return "dialog.title.export.ddl";
	}

	/**
	 * Get DDL file encoding.
	 * 
	 * @return
	 * @throws CoreException
	 */
	private String getEncoding() throws CoreException {
		return this.fileEncodingCombo.getText();
	}

	public ExportSetting getExportSetting() {
		return this.exportSetting;
	}
}
