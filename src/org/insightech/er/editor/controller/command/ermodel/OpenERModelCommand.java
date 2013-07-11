package org.insightech.er.editor.controller.command.ermodel;

import org.eclipse.gef.EditPart;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.insightech.er.editor.ERDiagramMultiPageEditor;
import org.insightech.er.editor.EROneDiagramEditor;
import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;

public class OpenERModelCommand extends AbstractCommand {

	private ERModel model;
	private ERDiagram diagram;
//	private EditPart editPart;
	private ERTable table;

	public OpenERModelCommand(ERDiagram diagram, ERModel model) {
		this.diagram = diagram;
		this.model = model;
	}

	@Override
	protected void doExecute() {
		ERDiagramMultiPageEditor editor = diagram.getEditor();

		editor.setCurrentErmodel(model);

		((EROneDiagramEditor) editor.getActiveEditor()).reveal(table);
//		IEditorInput editorInput = editor2.getEditorInput();
//
//
//		if (editPart != null) {
//			editor2.getGraphicalViewer().reveal(editPart);
//		}


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

	/**
	 * tableÇê›íËÇµÇ‹Ç∑ÅB
	 * @param table table
	 */
	public void setTable(ERTable table) {
	    this.table = table;
	}

}
