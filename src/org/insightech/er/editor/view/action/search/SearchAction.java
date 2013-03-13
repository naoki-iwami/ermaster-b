package org.insightech.er.editor.view.action.search;

import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.action.AbstractBaseAction;
import org.insightech.er.editor.view.dialog.search.SearchDialog;

public class SearchAction extends AbstractBaseAction {

	public static final String ID = SearchAction.class.getName();

	public SearchAction(ERDiagramEditor editor) {
		super(ID, ResourceString.getResourceString("action.title.find"), editor);
		this.setActionDefinitionId("org.eclipse.ui.edit.findReplace");
		this.setImageDescriptor(Activator.getImageDescriptor(ImageKey.FIND));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Event event) {
		ERDiagram diagram = this.getDiagram();

		SearchDialog dialog = new SearchDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), this
				.getGraphicalViewer(), getEditorPart(), diagram);

		dialog.open();
	}

}
