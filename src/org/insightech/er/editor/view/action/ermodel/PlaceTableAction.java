package org.insightech.er.editor.view.action.ermodel;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.EROneDiagramEditor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.insightech.er.editor.view.action.AbstractBaseAction;

public class PlaceTableAction extends AbstractBaseAction {

	public static final String ID = PlaceTableAction.class.getName();
	private EROneDiagramEditor oneEditor;

	public PlaceTableAction(EROneDiagramEditor editor) {
		super(ID, ResourceString
				.getResourceString("action.title.ermodel.place.table"), editor);
		this.oneEditor = editor;
	}

	@Override
	public void execute(Event event) throws Exception {
		ERDiagram diagram = this.getDiagram();
		ERModel model = oneEditor.getModel();
		
		List<ERTable> input = new ArrayList<ERTable>();
		input.addAll(diagram.getDiagramContents().getContents().getTableSet().getList());
		
		NodeSelectionDialog dialog = new NodeSelectionDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				diagram);
		
		FigureCanvas canvas = (FigureCanvas) oneEditor.getGraphicalViewer().getControl();
		Point point = new Point(
				canvas.getHorizontalBar().getSelection() + canvas.getClientArea().width / 2,
				canvas.getVerticalBar().getSelection() + canvas.getClientArea().height / 2);
		
		if (dialog.open() == IDialogConstants.OK_ID) {
			Object[] results = dialog.getResult();
			for (Object result : results) {
				ERTable curTable = (ERTable) result;
				ERVirtualTable virtualTable = new ERVirtualTable(model, curTable);
				virtualTable.setPoint(point.x, point.y);
				model.addTable(virtualTable);
//				oneEditor.setContents(model);
//				oneEditor.refreshContents();
//				model.changeAll();
			}
			
		}

	}

}
