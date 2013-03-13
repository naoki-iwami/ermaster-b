package org.insightech.er.editor.controller.editpolicy.element.node.table_view;

import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.requests.DirectEditRequest;
import org.insightech.er.Activator;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view.AddColumnGroupCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view.AddWordCommand;
import org.insightech.er.editor.controller.editpart.element.node.TableViewEditPart;
import org.insightech.er.editor.controller.editpolicy.element.node.NodeElementComponentEditPolicy;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.view.drag_drop.ERDiagramTransferDragSourceListener;

public class TableViewComponentEditPolicy extends
		NodeElementComponentEditPolicy {

	@Override
	public void showTargetFeedback(Request request) {
		super.showTargetFeedback(request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EditPart getTargetEditPart(Request request) {
		if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_COLUMN_GROUP
				.equals(request.getType())
				|| ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN_GROUP
						.equals(request.getType())) {
			DirectEditRequest editRequest = (DirectEditRequest) request;

			TableView tableView = (TableView) this.getHost().getModel();
			ColumnGroup columnGroup = (ColumnGroup) ((Map) editRequest
					.getDirectEditFeature()).get("group");

			if (!tableView.getColumns().contains(columnGroup)) {
				return getHost();
			}

		} else if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_WORD
				.equals(request.getType())) {
			return getHost();

		} else if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN
				.equals(request.getType())) {
			return getHost();

		}

		return super.getTargetEditPart(request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Command getCommand(Request request) {
		try {
			if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_COLUMN_GROUP
					.equals(request.getType())) {
				DirectEditRequest editRequest = (DirectEditRequest) request;

				TableView tableView = (TableView) this.getHost().getModel();
				ColumnGroup columnGroup = (ColumnGroup) ((Map) editRequest
						.getDirectEditFeature()).get("group");

				if (!tableView.getColumns().contains(columnGroup)) {
					return new AddColumnGroupCommand(tableView, columnGroup,
							this.getColumnIndex(editRequest));
				}

			} else if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_ADD_WORD
					.equals(request.getType())) {
				DirectEditRequest editRequest = (DirectEditRequest) request;

				TableView table = (TableView) this.getHost().getModel();
				Word word = (Word) editRequest.getDirectEditFeature();

				return new AddWordCommand(table, word, this
						.getColumnIndex(editRequest));

			} else if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN
					.equals(request.getType())) {
				DirectEditRequest editRequest = (DirectEditRequest) request;

				return ColumnSelectionHandlesEditPolicy
						.createMoveColumnCommand(editRequest, this.getHost()
								.getViewer(), (TableView) this.getHost()
								.getModel(), this.getColumnIndex(editRequest));

			} else if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_MOVE_COLUMN_GROUP
					.equals(request.getType())) {
				DirectEditRequest editRequest = (DirectEditRequest) request;

				return ColumnSelectionHandlesEditPolicy
						.createMoveColumnGroupCommand(editRequest,
								(TableView) this.getHost().getModel(), this
										.getColumnIndex(editRequest));
			}

		} catch (Exception e) {
			Activator.showExceptionDialog(e);
		}

		return super.getCommand(request);
	}

	private int getColumnIndex(DirectEditRequest editRequest) {
		ZoomManager zoomManager = ((ScalableFreeformRootEditPart) this
				.getHost().getRoot()).getZoomManager();
		double zoom = zoomManager.getZoom();

		IFigure figure = ((TableViewEditPart) this.getHost()).getFigure();

		int center = (int) (figure.getBounds().y + (figure.getBounds().height / 2)
				* zoom);

		int index = 0;

		if (editRequest.getLocation().y >= center) {
			TableView newTableView = (TableView) this.getHost().getModel();

			index = newTableView.getColumns().size();
		}

		return index;
	}
}
