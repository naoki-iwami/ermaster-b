package org.insightech.er.editor.view.drag_drop;

import java.util.List;
import java.util.Map;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.gef.dnd.TemplateTransfer;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;

public class ERDiagramTransferDropTargetListener extends
		AbstractTransferDropTargetListener {

	public ERDiagramTransferDropTargetListener(EditPartViewer dropTargetViewer,
			Transfer xfer) {
		super(dropTargetViewer, xfer);
	}

	@Override
	protected void updateTargetRequest() {
	}
	
	@Override
	public void drop(DropTargetEvent event) {
		super.drop(event);
		
//		ERTable curTable = (ERTable) event.data;
//		ERDiagramMultiPageEditor editor = curTable.getDiagram().getEditor();
//		EROneDiagramEditor modelEditor = (EROneDiagramEditor) editor.getActiveEditor();
//
//		Point cursorLocation = Display.getCurrent().getCursorLocation();
//		Point point = modelEditor.getGraphicalViewer().getControl().toControl(cursorLocation);
//		FigureCanvas canvas = (FigureCanvas) modelEditor.getGraphicalViewer().getControl();
//		point.x += canvas.getHorizontalBar().getSelection();
//		point.y += canvas.getVerticalBar().getSelection();
//
//		PlaceTableCommand command = new PlaceTableCommand(curTable, point);
//		command.execute();
		
		
//		ERTable curTable = (ERTable) event.data;
//		ERDiagramMultiPageEditor editor = curTable.getDiagram().getEditor();
//		EROneDiagramEditor modelEditor = (EROneDiagramEditor) editor.getActiveEditor();
//		
//		Point cursorLocation = Display.getCurrent().getCursorLocation();
//		Point point = modelEditor.getGraphicalViewer().getControl().toControl(cursorLocation);
//		FigureCanvas canvas = (FigureCanvas) modelEditor.getGraphicalViewer().getControl();
//		point.x += canvas.getHorizontalBar().getSelection();
//		point.y += canvas.getVerticalBar().getSelection();
//
//		ERModel model = curTable.getDiagram().getCurrentErmodel();
//
//		ERVirtualTable virtualTable = new ERVirtualTable(model, curTable);
//		virtualTable.setPoint(point.x, point.y);
//		model.addTable(virtualTable);
//		modelEditor.setContents(model);
	}
	
	@Override
	protected Request createTargetRequest() {
		Object object = this.getTargetModel();

		if (object instanceof Map) {
			if (((Map) object)
					.get(ERDiagramTransferDragSourceListener.MOVE_COLUMN_GROUP_PARAM_PARENT) instanceof TableView) {
				DirectEditRequest request = new DirectEditRequest(
						ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN_GROUP);
				request.setDirectEditFeature(object);
				request.setLocation(this.getDropLocation());
				return request;

			} else {
				DirectEditRequest request = new DirectEditRequest(
						ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_COLUMN_GROUP);
				request.setDirectEditFeature(object);
				request.setLocation(this.getDropLocation());
				return request;
			}

		} else if (object instanceof Word) {
			DirectEditRequest request = new DirectEditRequest(
					ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_WORD);
			request.setDirectEditFeature(object);
			request.setLocation(this.getDropLocation());
			return request;

		} else if (object instanceof NormalColumn) {
			DirectEditRequest request = new DirectEditRequest(
					ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN);
			request.setDirectEditFeature(object);
			request.setLocation(this.getDropLocation());
			return request;
		} else if (object instanceof ERTable) {
			DirectEditRequest request = new DirectEditRequest(
					ERDiagramTransferDragSourceListener.REQUEST_TYPE_PLACE_TABLE);
			request.setDirectEditFeature(object);
			request.setLocation(this.getDropLocation());
			return request;
		} else if (object instanceof List) {
			DirectEditRequest request = new DirectEditRequest(
					ERDiagramTransferDragSourceListener.REQUEST_TYPE_PLACE_TABLE);
			request.setDirectEditFeature(object);
			request.setLocation(this.getDropLocation());
			return request;
		}

		return super.createTargetRequest();
	}

	private Object getTargetModel() {
		TemplateTransfer transfer = (TemplateTransfer) this.getTransfer();
		return transfer.getObject();
	}
	
	@Override
	public boolean isEnabled(DropTargetEvent event) {
		System.out.println("isEnabled");
		boolean result = super.isEnabled(event);
		System.out.println(result);
		return result;
	}
	
	@Override
	protected Request getTargetRequest() {
		System.out.println("getTargetRequest");
		Request result = super.getTargetRequest();
		System.out.println(result);
		return result;
	}
	
}
