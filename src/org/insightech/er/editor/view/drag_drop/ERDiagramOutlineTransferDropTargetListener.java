package org.insightech.er.editor.view.drag_drop;

import java.util.Map;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.gef.dnd.TemplateTransfer;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.swt.dnd.Transfer;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;

public class ERDiagramOutlineTransferDropTargetListener extends
		AbstractTransferDropTargetListener {

	public ERDiagramOutlineTransferDropTargetListener(
			EditPartViewer dropTargetViewer, Transfer xfer) {
		super(dropTargetViewer, xfer);
	}

	@Override
	protected void updateTargetRequest() {
	}

	@Override
	protected Request createTargetRequest() {
		Object object = this.getTargetModel();

		if (object instanceof Map) {
			DirectEditRequest request = new DirectEditRequest(
					ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_COLUMN_GROUP);
			request.setDirectEditFeature(object);
			request.setLocation(this.getDropLocation());
			return request;

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
		}

		return super.createTargetRequest();
	}

	private Object getTargetModel() {
		TemplateTransfer transfer = (TemplateTransfer) this.getTransfer();
		return transfer.getObject();
	}
}
