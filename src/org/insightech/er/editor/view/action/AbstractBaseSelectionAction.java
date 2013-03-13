package org.insightech.er.editor.view.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IEditorPart;
import org.insightech.er.Activator;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ERModelUtil;

public abstract class AbstractBaseSelectionAction extends SelectionAction {

	private ERDiagramEditor editor;

	public AbstractBaseSelectionAction(String id, String text,
			ERDiagramEditor editor) {
		this(id, text, SWT.NONE, editor);
	}

	public AbstractBaseSelectionAction(String id, String text, int style,
			ERDiagramEditor editor) {
		super(editor, style);
		this.setId(id);
		this.setText(text);

		this.editor = editor;
	}

	protected ERDiagram getDiagram() {
		EditPart editPart = this.editor.getGraphicalViewer().getContents();
		ERDiagram diagram = ERModelUtil.getDiagram(editPart);

		return diagram;
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
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void execute(Command command) {
		this.editor.getGraphicalViewer().getEditDomain().getCommandStack()
				.execute(command);
	}

	protected IEditorPart getEditorPart() {
		return this.editor;
	}

	protected void execute(Event event) {
		GraphicalViewer viewer = this.getGraphicalViewer();

		List<Command> commandList = new ArrayList<Command>();

		for (Object object : viewer.getSelectedEditParts()) {
			List<Command> subCommandList = this.getCommand((EditPart) object,
					event);
			commandList.addAll(subCommandList);
		}

		if (!commandList.isEmpty()) {
			CompoundCommand compoundCommand = new CompoundCommand();
			for (Command command : commandList) {
				compoundCommand.add(command);
			}

			this.execute(compoundCommand);
		}
	}

	abstract protected List<Command> getCommand(EditPart editPart, Event event);

	@Override
	protected boolean calculateEnabled() {
		GraphicalViewer viewer = this.getGraphicalViewer();

		if (viewer.getSelectedEditParts().isEmpty()) {
			return false;
		}

		return true;
	}
}
