package org.insightech.er.editor.view.drag_drop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.dnd.AbstractTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransfer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.insightech.er.editor.controller.editpart.outline.table.TableOutlineEditPart;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;

public class ERDiagramTransferDragSourceListener extends
		AbstractTransferDragSourceListener {

	public static final String REQUEST_TYPE_MOVE_COLUMN = "move column";

	public static final String REQUEST_TYPE_MOVE_COLUMN_GROUP = "move column group";

	public static final String REQUEST_TYPE_ADD_COLUMN_GROUP = "add column group";

	public static final String MOVE_COLUMN_GROUP_PARAM_PARENT = "parent";

	public static final String MOVE_COLUMN_GROUP_PARAM_GROUP = "group";
	
	public static final String REQUEST_TYPE_ADD_WORD = "add word";

	public static final String REQUEST_TYPE_PLACE_TABLE = "place table";

	private EditPartViewer dragSourceViewer;

	public ERDiagramTransferDragSourceListener(EditPartViewer dragSourceViewer,
			Transfer xfer) {
		super(dragSourceViewer, xfer);

		this.dragSourceViewer = dragSourceViewer;
	}

	@Override
	public void dragStart(DragSourceEvent dragsourceevent) {
		super.dragStart(dragsourceevent);

		Object target = this.getTargetModel(dragsourceevent);

		if (target != null) {
			// && target == dragSourceViewer.findObjectAt(
			// new Point(dragsourceevent.x, dragsourceevent.y))
			// .getModel()) {
			TemplateTransfer transfer = (TemplateTransfer) this.getTransfer();
			transfer.setObject(target);

		} else {
			dragsourceevent.doit = false;
		}
	}

//	@Override
//	public void dragFinished(DragSourceEvent event) {
//		super.dragFinished(event);
//		
//		ERTable table = (ERTable) ((TemplateTransfer)getTransfer()).getObject();
//	}
	
	public void dragSetData(DragSourceEvent event) {
		event.data = this.getTargetModel(event);
	}

	private Object getTargetModel(DragSourceEvent event) {
		List editParts = dragSourceViewer.getSelectedEditParts();
		if (editParts.isEmpty()) {
			return null;
		}
		if (editParts.size() != 1) {
			List<Object> results = new ArrayList<Object>();
			for (Object partObj : editParts) {
				EditPart editPart = (EditPart) partObj;
				Object model = editPart.getModel();
				if (model instanceof ERTable && editPart instanceof TableOutlineEditPart) {
					// アウトラインからのテーブル複数選択
					results.add(model);
				}
				if (model instanceof ERVirtualTable) {
					// エディタ内からの仮想テーブル複数選択
					return null; // ここはドラッグはせず、上位の移動機構（？）に回す。これで複数テーブルをエディタ内で移動可能になる
				}
			}
			return results;
		}

		EditPart editPart = (EditPart) editParts.get(0);

		Object model = editPart.getModel();
		
		if (model instanceof NormalColumn) {
			NormalColumn normalColumn = (NormalColumn) model;
			if (normalColumn.getColumnHolder() instanceof ColumnGroup) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(MOVE_COLUMN_GROUP_PARAM_PARENT, editPart.getParent().getModel());
				map.put(MOVE_COLUMN_GROUP_PARAM_GROUP, normalColumn.getColumnHolder());
	
				return map;
			}
			
			return model;

		} else if (model instanceof ColumnGroup) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(MOVE_COLUMN_GROUP_PARAM_PARENT, editPart.getParent().getModel());
			map.put(MOVE_COLUMN_GROUP_PARAM_GROUP, model);

			return map;

		} else if (model instanceof Word) {
			return model;
		} else if (model instanceof ERTable
				&& editPart instanceof TableOutlineEditPart) {
			return model;
//		} else if (model instanceof TableOutlineEditPart) {
//			return model;
		}

		return null;
	}

}
