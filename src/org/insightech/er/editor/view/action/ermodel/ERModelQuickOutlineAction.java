package org.insightech.er.editor.view.action.ermodel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IEditorPart;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.ErDiagramInformationControl;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.action.AbstractBaseAction;

public class ERModelQuickOutlineAction extends AbstractBaseAction {

	public static final String ID = ERModelQuickOutlineAction.class.getName();

	public ERModelQuickOutlineAction(ERDiagramEditor editor) {
		super(ID, ResourceString
				.getResourceString("action.title.ermodel.outline"), editor);
		this.setActionDefinitionId("org.insightech.er.quickOutline");
		setAccelerator(SWT.CTRL | 'O');

	}

	@Override
	public void execute(Event event) throws Exception {
		ERDiagram diagram = this.getDiagram();

		ErDiagramInformationControl quickOutline
				= new ErDiagramInformationControl(diagram, getEditorPart().getSite().getShell(), getEditorPart().getGraphicalViewer().getControl());


		quickOutline.setVisible(true);

	}

}
