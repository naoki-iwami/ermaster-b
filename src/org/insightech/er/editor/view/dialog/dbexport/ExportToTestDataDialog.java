package org.insightech.er.editor.view.dialog.dbexport;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.insightech.er.Activator;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.DirectoryText;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.testdata.TestDataCreator;
import org.insightech.er.editor.model.dbexport.testdata.impl.DBUnitFlatXmlTestDataCreator;
import org.insightech.er.editor.model.dbexport.testdata.impl.DBUnitTestDataCreator;
import org.insightech.er.editor.model.dbexport.testdata.impl.DBUnitXLSTestDataCreator;
import org.insightech.er.editor.model.dbexport.testdata.impl.SQLTestDataCreator;
import org.insightech.er.editor.model.settings.export.ExportTestDataSetting;
import org.insightech.er.editor.model.testdata.TestData;
import org.insightech.er.util.Check;

public class ExportToTestDataDialog extends AbstractDialog {

	private Table testDataTable;

	private Button formatSqlRadio;

	private Button formatDBUnitRadio;

	private Button formatDBUnitFlatXmlRadio;

	private Button formatDBUnitXlsRadio;

	private DirectoryText outputDirectoryText;

	private Combo fileEncodingCombo;

	private ERDiagram diagram;

	private IEditorPart editorPart;

	private List<TestData> testDataList;

	private int targetIndex;

	private ExportTestDataSetting exportTestDataSetting;

	public ExportToTestDataDialog(Shell parentShell, IEditorPart editorPart,
			ERDiagram diagram, List<TestData> testDataList, int targetIndex) {
		super(parentShell, 3);

		this.testDataList = testDataList;
		this.targetIndex = targetIndex;
		this.editorPart = editorPart;
		this.diagram = diagram;

		this.exportTestDataSetting = diagram.getDiagramContents().getSettings()
				.getExportSetting().getExportTestDataSetting().clone();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite parent) {
		this.createTestDataTableGroup(parent);
		this.createFormatGroup(parent);
		this.createFileGroup(parent);
	}

	private void createTestDataTableGroup(Composite parent) {
		GridData groupGridData = new GridData();
		groupGridData.horizontalAlignment = GridData.FILL;
		groupGridData.grabExcessHorizontalSpace = true;
		groupGridData.horizontalSpan = 3;

		GridLayout groupLayout = new GridLayout();
		groupLayout.marginWidth = 5;
		groupLayout.marginHeight = 5;

		Group group = new Group(parent, SWT.NONE);
		group.setText(ResourceString.getResourceString("label.testdata.list"));
		group.setLayoutData(groupGridData);
		group.setLayout(groupLayout);

		this.testDataTable = new Table(group, SWT.CHECK | SWT.BORDER);

		GridData tableGridData = new GridData();
		tableGridData.horizontalAlignment = GridData.FILL;
		tableGridData.grabExcessHorizontalSpace = true;
		tableGridData.horizontalSpan = 3;
		tableGridData.heightHint = 80;
		this.testDataTable.setLayoutData(tableGridData);
	}

	private void createFormatGroup(Composite parent) {
		GridData formatGroupGridData = new GridData();
		formatGroupGridData.horizontalAlignment = GridData.FILL;
		formatGroupGridData.grabExcessHorizontalSpace = true;
		formatGroupGridData.horizontalSpan = 3;

		GridLayout formatGroupLayout = new GridLayout();
		formatGroupLayout.marginWidth = 15;
		formatGroupLayout.marginHeight = 15;

		Group formatGroup = new Group(parent, SWT.NONE);
		formatGroup.setText(ResourceString.getResourceString("label.format"));
		formatGroup.setLayoutData(formatGroupGridData);
		formatGroup.setLayout(formatGroupLayout);

		this.formatSqlRadio = CompositeFactory.createRadio(this, formatGroup,
				"label.sql");
		this.formatDBUnitRadio = CompositeFactory.createRadio(this,
				formatGroup, "label.dbunit");
		this.formatDBUnitFlatXmlRadio = CompositeFactory.createRadio(this,
				formatGroup, "label.dbunit.flat.xml");
		this.formatDBUnitXlsRadio = CompositeFactory.createRadio(this,
				formatGroup, "label.dbunit.xls");
	}

	private void createFileGroup(Composite parent) {
		GridData groupGridData = new GridData();
		groupGridData.horizontalAlignment = GridData.FILL;
		groupGridData.grabExcessHorizontalSpace = true;
		groupGridData.horizontalSpan = 3;

		GridLayout groupLayout = new GridLayout();
		groupLayout.marginWidth = 5;
		groupLayout.marginHeight = 5;
		groupLayout.numColumns = 3;

		Group group = new Group(parent, SWT.NONE);
		group.setLayoutData(groupGridData);
		group.setLayout(groupLayout);

		CompositeFactory.createLabel(group, "label.output.dir");
		this.outputDirectoryText = new DirectoryText(group, SWT.BORDER);

		GridData layoutData = new GridData();
		layoutData.widthHint = 200;
		this.outputDirectoryText.setLayoutData(layoutData);

		this.fileEncodingCombo = CompositeFactory.createFileEncodingCombo(
				this.editorPart, this, group, "label.output.file.encoding", 1);
		CompositeFactory.filler(group, 1);
	}

	@Override
	protected String getErrorMessage() {
		boolean itemChecked = false;

		for (TableItem item : this.testDataTable.getItems()) {
			if (item.getChecked()) {
				itemChecked = true;
				break;
			}
		}

		if (!itemChecked) {
			return "error.testdata.not.selected";
		}

		if (this.outputDirectoryText.isBlank()) {
			return "error.output.dir.is.empty";
		}

		return null;
	}

	@Override
	protected void perfomeOK() throws InputException {
		if (this.formatSqlRadio.getSelection()) {
			this.exportTestDataSetting
					.setExportFormat(TestData.EXPORT_FORMT_SQL);

		} else if (this.formatDBUnitRadio.getSelection()) {
			this.exportTestDataSetting
					.setExportFormat(TestData.EXPORT_FORMT_DBUNIT);

		} else if (this.formatDBUnitFlatXmlRadio.getSelection()) {
			this.exportTestDataSetting
					.setExportFormat(TestData.EXPORT_FORMT_DBUNIT_FLAT_XML);

		} else if (this.formatDBUnitXlsRadio.getSelection()) {
			this.exportTestDataSetting
					.setExportFormat(TestData.EXPORT_FORMT_DBUNIT_XLS);

		}

		this.exportTestDataSetting.setExportFilePath(this.outputDirectoryText
				.getFilePath());
		this.exportTestDataSetting.setExportFileEncoding(this.fileEncodingCombo
				.getText());

		try {
			for (int i = 0; i < this.testDataTable.getItemCount(); i++) {
				TableItem item = this.testDataTable.getItem(i);
				if (item.getChecked()) {
					exportTestData(this.diagram, this.exportTestDataSetting,
							testDataList.get(i));
				}
			}

			Activator.showMessageDialog("dialog.message.export.finish");

		} catch (IOException e) {
			Activator.showExceptionDialog(e);
		}

		this.refreshProject();
	}

	public static void exportTestData(ERDiagram diagram,
			ExportTestDataSetting exportTestDataSetting, TestData testData)
			throws IOException, InputException {
		TestDataCreator testDataCreator = null;

		int format = exportTestDataSetting.getExportFormat();

		if (format == TestData.EXPORT_FORMT_DBUNIT) {
			testDataCreator = new DBUnitTestDataCreator(exportTestDataSetting
					.getExportFileEncoding());

		} else if (format == TestData.EXPORT_FORMT_DBUNIT_FLAT_XML) {
			testDataCreator = new DBUnitFlatXmlTestDataCreator(
					exportTestDataSetting.getExportFileEncoding());

		} else if (format == TestData.EXPORT_FORMT_SQL) {
			testDataCreator = new SQLTestDataCreator();

		} else if (format == TestData.EXPORT_FORMT_DBUNIT_XLS) {
			testDataCreator = new DBUnitXLSTestDataCreator();

		}

		testDataCreator.init(testData);

		File dir = new File(exportTestDataSetting.getExportFilePath()
				+ File.separator);
		if (dir.isDirectory() || dir.mkdirs()) {
			testDataCreator.write(exportTestDataSetting, diagram);

		} else {
			throw new InputException("error.output.dir.can.not.be.made");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setData() {
		for (TestData testData : this.testDataList) {
			TableItem tableItem = new TableItem(this.testDataTable, SWT.NONE);

			tableItem.setText(0, testData.getName());
		}

		if (this.targetIndex >= 0) {
			this.testDataTable.getItem(this.targetIndex).setChecked(true);
		}

		if (this.exportTestDataSetting.getExportFormat() == TestData.EXPORT_FORMT_DBUNIT) {
			this.formatDBUnitRadio.setSelection(true);

		} else if (this.exportTestDataSetting.getExportFormat() == TestData.EXPORT_FORMT_DBUNIT_FLAT_XML) {
			this.formatDBUnitFlatXmlRadio.setSelection(true);

		} else if (this.exportTestDataSetting.getExportFormat() == TestData.EXPORT_FORMT_DBUNIT_XLS) {
			this.formatDBUnitXlsRadio.setSelection(true);

		} else {
			this.formatSqlRadio.setSelection(true);

		}

		String outputDirectoryPath = this.exportTestDataSetting
				.getExportFilePath();

		if (Check.isEmpty(outputDirectoryPath)) {
			IFile file = ((IFileEditorInput) editorPart.getEditorInput())
					.getFile();
			outputDirectoryPath = file.getLocation().toOSString();

			outputDirectoryPath = outputDirectoryPath.substring(0,
					outputDirectoryPath.lastIndexOf(File.separator))
					+ File.separator + "testdata";
		}

		this.outputDirectoryText.setText(outputDirectoryPath);

		String outputFileEncoding = this.exportTestDataSetting
				.getExportFileEncoding();

		if (Check.isEmpty(outputFileEncoding)) {
			outputFileEncoding = "UTF-8";
		}

		this.fileEncodingCombo.setText(outputFileEncoding);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		this.createButton(parent, IDialogConstants.OK_ID, ResourceString
				.getResourceString("label.button.export"), true);
		this.createButton(parent, IDialogConstants.CLOSE_ID,
				IDialogConstants.CLOSE_LABEL, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getTitle() {
		return "dialog.title.export.testdata";
	}

	private void refreshProject() {
		IFile file = ((IFileEditorInput) editorPart.getEditorInput()).getFile();
		IProject project = file.getProject();

		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);

		} catch (CoreException e) {
			Activator.showExceptionDialog(e);
		}
	}

	@Override
	protected void addListener() {
		super.addListener();

		this.testDataTable.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				validate();
			}

		});
	}

	public ExportTestDataSetting getExportTestDataSetting() {
		return exportTestDataSetting;
	}

}
