package org.insightech.er.editor.view.action.printer;

import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.view.action.AbstractBaseAction;

public class PrintAction extends AbstractBaseAction {

	public static final String ID = PrintAction.class.getName();

	public PrintAction(ERDiagramEditor editor) {
		super(ID, ResourceString.getResourceString("action.title.find"), editor);
		this.setActionDefinitionId("org.eclipse.ui.edit.findReplace");
	}

	@Override
	public void execute(Event event) throws Exception {
		PrintDialog dialog = new PrintDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), 0);
		dialog.open();
	}

}
