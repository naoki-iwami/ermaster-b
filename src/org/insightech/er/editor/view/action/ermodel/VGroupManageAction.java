package org.insightech.er.editor.view.action.ermodel;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.EROneDiagramEditor;
import org.insightech.er.editor.controller.command.common.ChangeSettingsCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.ChangeVGroupCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.action.AbstractBaseAction;
import org.insightech.er.editor.view.dialog.category.VGroupManageDialog;

public class VGroupManageAction extends AbstractBaseAction {

	public static final String ID = VGroupManageAction.class.getName();

	public VGroupManageAction(EROneDiagramEditor editor) {
		super(ID, ResourceString
				.getResourceString("action.title.vgroup.manage"), editor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Event event) {
		ERDiagram diagram = this.getDiagram();

		ERModel model = ((EROneDiagramEditor)getEditorPart()).getModel();
		ERModel newModel = (ERModel) model.clone();
		
		VGroupManageDialog dialog = new VGroupManageDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				newModel);

		if (dialog.open() == IDialogConstants.OK_ID) {
			System.out.println("ok");
			execute(new ChangeVGroupCommand(model, newModel.getGroups()));
//			ChangeSettingsCommand command = new ChangeSettingsCommand(diagram,
//					settings);
//			this.execute(command);
		}
	}

}
