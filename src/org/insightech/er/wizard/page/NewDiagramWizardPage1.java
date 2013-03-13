package org.insightech.er.wizard.page;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.insightech.er.Activator;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.persistent.Persistent;

public class NewDiagramWizardPage1 extends WizardNewFileCreationPage {

	private ERDiagram diagram;

	private static final String EXTENSION = ".erm";

	public NewDiagramWizardPage1(IStructuredSelection selection) {
		super(ResourceString.getResourceString("wizard.new.diagram.title"),
				selection);

		this.setTitle(ResourceString
				.getResourceString("wizard.new.diagram.title"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);

		this.setFileName("newfile");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean validatePage() {
		boolean valid = super.validatePage();
		if (valid) {
			String fileName = this.getFileName();
			if (fileName.indexOf(".") != -1 && !fileName.endsWith(EXTENSION)) {
				this.setErrorMessage(ResourceString
						.getResourceString("error.erm.extension"));
				valid = false;
			}
		}
		if (valid) {
			String fileName = this.getFileName();
			if (fileName.indexOf(".") == -1) {
				fileName = fileName + EXTENSION;
			}
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();

			IPath containerPath = this.getContainerFullPath();
			IPath newFilePath = containerPath.append(fileName);

			if (root.getFile(newFilePath).exists()) {
				this
						.setErrorMessage("'"
								+ fileName
								+ "' "
								+ ResourceString
										.getResourceString("error.file.already.exists"));
				valid = false;
			}
		}

		if (valid) {
			this.setMessage(ResourceString
					.getResourceString("wizard.new.diagram.message"));
		}

		return valid;
	}

	public void createERDiagram(String database) {
		this.diagram = new ERDiagram(database);
		this.diagram.init();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected InputStream getInitialContents() {
		Persistent persistent = Persistent.getInstance();

		try {
			InputStream in = persistent.createInputStream(this.diagram);
			return in;

		} catch (IOException e) {
			Activator.showExceptionDialog(e);
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFile createNewFile() {
		String fileName = this.getFileName();
		if (fileName.indexOf(".") == -1) {
			this.setFileName(fileName + EXTENSION);
		}

		return super.createNewFile();
	}

}
