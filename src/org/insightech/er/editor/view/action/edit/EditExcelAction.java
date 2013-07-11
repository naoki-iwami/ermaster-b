package org.insightech.er.editor.view.action.edit;

import java.util.List;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbenchPart;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.controller.command.common.EditExcelCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ViewableModel;

public class EditExcelAction extends SelectionAction {

	public static final String ID = EditExcelAction.class.getName();

	public EditExcelAction(IWorkbenchPart part, ERDiagram diagram) {
		super(part, Action.AS_UNSPECIFIED);
		this.setId(ID);

		this.setText(ResourceString
				.getResourceString("action.title.change.background.color"));
		this.setToolTipText(ResourceString
				.getResourceString("action.title.change.background.color"));

//		super(ID, ResourceString.getResourceString("dialog.title.edit.excel"), editor);
		this.setImageDescriptor(Activator.getImageDescriptor(ImageKey.EDIT_EXCEL));
	}

	@Override
	public void runWithEvent(Event event) {
		Command command = this.createCommand(this.getSelectedObjects());
		this.getCommandStack().execute(command);
		setChecked(false);
	}

	private Command createCommand(List objects) {
		if (objects.isEmpty()) {
			return null;
		}

		if (!(objects.get(0) instanceof GraphicalEditPart)) {
			return null;
		}

		CompoundCommand command = new CompoundCommand();

		for (int i = 0; i < objects.size(); i++) {
			GraphicalEditPart part = (GraphicalEditPart) objects.get(i);
			command.add(new EditExcelCommand((ViewableModel) part.getModel()));
		}

		return command;
	}

	@Override
	protected boolean calculateEnabled() {
		List objects = this.getSelectedObjects();

		if (objects.isEmpty()) {
			return false;
		}

		if (!(objects.get(0) instanceof GraphicalEditPart)) {
			return false;
		}

		return true;
	}


}
