package org.insightech.er.editor.controller.editpolicy.element.node;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Handle;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.handles.NonResizableHandleKit;
import org.eclipse.gef.handles.ResizableHandleKit;
import org.eclipse.gef.handles.ResizeHandle;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ERModelUtil;
import org.insightech.er.editor.model.ViewableModel;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.insightech.er.editor.view.figure.handle.ERDiagramMoveHandle;
import org.insightech.er.editor.view.figure.handle.ERDiagramResizeHandle;

public class NodeElementSelectionEditPolicy extends ResizableEditPolicy {

	@SuppressWarnings("unchecked")
	@Override
	protected List createSelectionHandles() {
		List selectedEditParts = this.getHost().getViewer()
				.getSelectedEditParts();
		if (selectedEditParts.size() == 1) {
			ViewableModel currentElement = (ViewableModel) getHost().getModel();
			if (!(currentElement instanceof Category)
					&& !(currentElement instanceof ModelProperties)) {
				ERDiagram diagram = ERModelUtil.getDiagram(getHost().getRoot().getContents());

				ViewableModel targetElement = currentElement;
				if (currentElement instanceof ERVirtualTable) {
					// ビュー上ではテーブル実体を更新する
					targetElement = ((ERVirtualTable)currentElement).getRawTable();
				}
				List<NodeElement> nodeElementList = diagram
						.getDiagramContents().getContents()
						.getNodeElementList();
				nodeElementList.remove(targetElement);
				nodeElementList.add((NodeElement) targetElement);
				getHost().getRoot().getContents().refresh();
			}
		}

		List list = new ArrayList();

		int directions = this.getResizeDirections();

		if (directions == 0) {
//			NonResizableHandleKit.addHandles((GraphicalEditPart) getHost(),
//					list);

		} else if (directions != -1) {
			// 0
			list.add(new ERDiagramMoveHandle((GraphicalEditPart) getHost()));

			// 1
			if ((directions & PositionConstants.EAST) != 0) {
				ResizableHandleKit.addHandle((GraphicalEditPart) getHost(),
						list, PositionConstants.EAST);
			} else {
				NonResizableHandleKit.addHandle((GraphicalEditPart) getHost(),
						list, PositionConstants.EAST);
			}

			// 2
			if ((directions & PositionConstants.SOUTH_EAST) == PositionConstants.SOUTH_EAST) {
				ResizableHandleKit.addHandle((GraphicalEditPart) getHost(),
						list, PositionConstants.SOUTH_EAST);
			} else {
				NonResizableHandleKit.addHandle((GraphicalEditPart) getHost(),
						list, PositionConstants.SOUTH_EAST);
			}

			// 3
			if ((directions & PositionConstants.SOUTH) != 0) {
				ResizableHandleKit.addHandle((GraphicalEditPart) getHost(),
						list, PositionConstants.SOUTH);
			} else {
				NonResizableHandleKit.addHandle((GraphicalEditPart) getHost(),
						list, PositionConstants.SOUTH);
			}

			// 4
			if ((directions & PositionConstants.SOUTH_WEST) == PositionConstants.SOUTH_WEST) {
				ResizableHandleKit.addHandle((GraphicalEditPart) getHost(),
						list, PositionConstants.SOUTH_WEST);
			} else {
				NonResizableHandleKit.addHandle((GraphicalEditPart) getHost(),
						list, PositionConstants.SOUTH_WEST);
			}

			// 5
			if ((directions & PositionConstants.WEST) != 0) {
				ResizableHandleKit.addHandle((GraphicalEditPart) getHost(),
						list, PositionConstants.WEST);
			} else {
				NonResizableHandleKit.addHandle((GraphicalEditPart) getHost(),
						list, PositionConstants.WEST);
			}

			// 6
			if ((directions & PositionConstants.NORTH_WEST) == PositionConstants.NORTH_WEST) {
				ResizableHandleKit.addHandle((GraphicalEditPart) getHost(),
						list, PositionConstants.NORTH_WEST);
			} else {
				NonResizableHandleKit.addHandle((GraphicalEditPart) getHost(),
						list, PositionConstants.NORTH_WEST);
			}

			// 7
			if ((directions & PositionConstants.NORTH) != 0) {
				ResizableHandleKit.addHandle((GraphicalEditPart) getHost(),
						list, PositionConstants.NORTH);
			} else {
				NonResizableHandleKit.addHandle((GraphicalEditPart) getHost(),
						list, PositionConstants.NORTH);
			}

			// 8
			if ((directions & PositionConstants.NORTH_EAST) == PositionConstants.NORTH_EAST) {
				ResizableHandleKit.addHandle((GraphicalEditPart) getHost(),
						list, PositionConstants.NORTH_EAST);
			} else {
				NonResizableHandleKit.addHandle((GraphicalEditPart) getHost(),
						list, PositionConstants.NORTH_EAST);
			}

		} else {
			addHandles((GraphicalEditPart) getHost(), list);
		}

		return list;
	}

	@SuppressWarnings("unchecked")
	public static void addHandles(GraphicalEditPart part, List handles) {
		handles.add(new ERDiagramMoveHandle(part));
		handles.add(createHandle(part, PositionConstants.EAST));
		handles.add(createHandle(part, PositionConstants.SOUTH_EAST));
		handles.add(createHandle(part, PositionConstants.SOUTH));
		handles.add(createHandle(part, PositionConstants.SOUTH_WEST));
		handles.add(createHandle(part, PositionConstants.WEST));
		handles.add(createHandle(part, PositionConstants.NORTH_WEST));
		handles.add(createHandle(part, PositionConstants.NORTH));
		handles.add(createHandle(part, PositionConstants.NORTH_EAST));
	}

	static Handle createHandle(GraphicalEditPart owner, int direction) {
		ResizeHandle handle = new ERDiagramResizeHandle(owner, direction);
		return handle;
	}

}
