package org.insightech.er.editor.view.action.dbexport;

import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.action.AbstractBaseAction;
import org.insightech.er.editor.view.dialog.dbexport.ExportToTranslationDictionaryDialog;

public class ExportToTranslationDictionaryAction extends AbstractBaseAction {

	public static final String ID = ExportToTranslationDictionaryAction.class
			.getName();

	public ExportToTranslationDictionaryAction(ERDiagramEditor editor) {
		super(
				ID,
				ResourceString
						.getResourceString("action.title.export.translation.dictionary"),
				editor);
	}

	@Override
	public void execute(Event event) {
		ERDiagram diagram = this.getDiagram();

		ExportToTranslationDictionaryDialog dialog = new ExportToTranslationDictionaryDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				diagram);

		dialog.open();
	}

}
