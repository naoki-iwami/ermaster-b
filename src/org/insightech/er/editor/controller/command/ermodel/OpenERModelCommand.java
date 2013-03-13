package org.insightech.er.editor.controller.command.ermodel;

import org.eclipse.ui.PartInitException;
import org.insightech.er.Activator;
import org.insightech.er.editor.ERDiagramMultiPageEditor;
import org.insightech.er.editor.EROneDiagramEditor;
import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;

public class OpenERModelCommand extends AbstractCommand {

	private ERModel model;
	private ERDiagram diagram;

	public OpenERModelCommand(ERDiagram diagram, ERModel model) {
		this.diagram = diagram;
		this.model = model;
	}

	@Override
	protected void doExecute() {
		ERDiagramMultiPageEditor editor = diagram.getEditor();
		
		editor.setCurrentErmodel(model);
		
		
//		editor.setActiveEditor(editorPart);
//
//		EROneDiagramEditor diagramEditor = new EROneDiagramEditor(
//				this.diagram, model, editor.getEditPartFactory(),
//				editor.getZoomComboContributionItem(), editor.getOutlinePage());
//
//		try {
//			editor.addPage(diagramEditor, editor.getEditorInput(), model.getName());
//			editor.setActiveEditor(diagramEditor);
//			
//		} catch (PartInitException e) {
//			Activator.showExceptionDialog(e);
//		}
	}

	@Override
	protected void doUndo() {
		// not support
	}

}
