package org.insightech.er.editor.view.action.dbexport;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.view.action.AbstractBaseAction;

public abstract class AbstractExportAction extends AbstractBaseAction {

	public AbstractExportAction(String id, String label, ERDiagramEditor editor) {
		super(id, label, editor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Event event) throws Exception {
		this.save(this.getEditorPart(), this.getGraphicalViewer());
	}

	protected void save(IEditorPart editorPart, GraphicalViewer viewer)
			throws Exception {

		String saveFilePath = this.getSaveFilePath(editorPart, viewer);
		if (saveFilePath == null) {
			return;
		}

		File file = new File(saveFilePath);
		if (file.exists()) {
			MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), SWT.ICON_WARNING
					| SWT.OK | SWT.CANCEL);
			messageBox.setText(ResourceString
					.getResourceString("dialog.title.warning"));
			messageBox.setMessage(ResourceString.getResourceString(this
					.getConfirmOverrideMessage()));

			if (messageBox.open() == SWT.CANCEL) {
				return;
			}
		}

		this.save(editorPart, viewer, saveFilePath);
		this.refreshProject();
	}

	protected String getConfirmOverrideMessage() {
		return "dialog.message.update.file";
	}

	protected String getSaveFilePath(IEditorPart editorPart,
			GraphicalViewer viewer) {

		IFile file = ((IFileEditorInput) editorPart.getEditorInput()).getFile();

		FileDialog fileDialog = new FileDialog(editorPart.getEditorSite()
				.getShell(), SWT.SAVE);

		IProject project = file.getProject();

		fileDialog.setFilterPath(project.getLocation().toString());

		String[] filterExtensions = this.getFilterExtensions();
		fileDialog.setFilterExtensions(filterExtensions);

		String fileName = this.getDiagramFileName(editorPart);

		fileDialog.setFileName(fileName);

		return fileDialog.open();
	}

	protected String getDiagramFileName(IEditorPart editorPart) {
		IFile file = ((IFileEditorInput) editorPart.getEditorInput()).getFile();
		String fileName = file.getName();
		return fileName.substring(0, fileName.lastIndexOf("."))
				+ this.getDefaultExtension();
	}

	protected abstract String getDefaultExtension();

	protected String getSaveDirPath(IEditorPart editorPart,
			GraphicalViewer viewer) {

		IFile file = ((IFileEditorInput) editorPart.getEditorInput()).getFile();

		DirectoryDialog directoryDialog = new DirectoryDialog(editorPart
				.getEditorSite().getShell(), SWT.SAVE);

		IProject project = file.getProject();

		directoryDialog.setFilterPath(project.getLocation().toString());

		return directoryDialog.open();
	}

	protected abstract String[] getFilterExtensions();

	protected abstract void save(IEditorPart editorPart,
			GraphicalViewer viewer, String saveFilePath) throws Exception;
}
