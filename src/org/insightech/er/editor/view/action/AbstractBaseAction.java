package org.insightech.er.editor.view.action;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IFileEditorInput;
import org.insightech.er.Activator;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.common.ChangeSettingsCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.model.settings.Settings;

public abstract class AbstractBaseAction extends Action {

	private ERDiagramEditor editor;

	public AbstractBaseAction(String id, String text, ERDiagramEditor editor) {
		this(id, text, SWT.NONE, editor);
	}

	public AbstractBaseAction(String id, String text, int style,
			ERDiagramEditor editor) {
		super(text, style);
		this.setId(id);

		this.editor = editor;
	}

	protected void refreshProject() {
		IFile iFile = ((IFileEditorInput) this.getEditorPart().getEditorInput())
				.getFile();
		IProject project = iFile.getProject();

		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);

		} catch (CoreException e) {
			Activator.showExceptionDialog(e);
		}
	}

	protected ERDiagram getDiagram() {
		EditPart editPart = this.editor.getGraphicalViewer().getContents();
		Object model = editPart.getModel();
		if (model instanceof ERDiagram) {
			return (ERDiagram) model;
		}
		return ((ERModel)model).getDiagram();
	}

	protected GraphicalViewer getGraphicalViewer() {
		return this.editor.getGraphicalViewer();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void runWithEvent(Event event) {
		try {
			execute(event);

		} catch (Exception e) {
			Activator.showExceptionDialog(e);

		} finally {
			Settings newSettings = this.getChangedSettings();

			if (newSettings != null
					&& !this.getDiagram().getDiagramContents().getSettings()
							.equals(newSettings)) {
				ChangeSettingsCommand command = new ChangeSettingsCommand(this
						.getDiagram(), newSettings);

				this.execute(command);
			}
		}
	}

	abstract public void execute(Event event) throws Exception;

	protected void execute(Command command) {
		this.editor.getGraphicalViewer().getEditDomain().getCommandStack()
				.execute(command);
	}

	protected Settings getChangedSettings() {
		return null;
	}

	protected ERDiagramEditor getEditorPart() {
		return this.editor;
	}
}
