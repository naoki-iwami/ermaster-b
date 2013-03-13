package org.insightech.er.editor.controller.editpart.element;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.insightech.er.editor.controller.editpart.element.connection.CommentConnectionEditPart;
import org.insightech.er.editor.controller.editpart.element.connection.RelationEditPart;
import org.insightech.er.editor.controller.editpart.element.node.CategoryEditPart;
import org.insightech.er.editor.controller.editpart.element.node.ERModelEditPart;
import org.insightech.er.editor.controller.editpart.element.node.ERTableEditPart;
import org.insightech.er.editor.controller.editpart.element.node.ERVirtualTableEditPart;
import org.insightech.er.editor.controller.editpart.element.node.InsertedImageEditPart;
import org.insightech.er.editor.controller.editpart.element.node.ModelPropertiesEditPart;
import org.insightech.er.editor.controller.editpart.element.node.NoteEditPart;
import org.insightech.er.editor.controller.editpart.element.node.VGroupEditPart;
import org.insightech.er.editor.controller.editpart.element.node.ViewEditPart;
import org.insightech.er.editor.controller.editpart.element.node.column.GroupColumnEditPart;
import org.insightech.er.editor.controller.editpart.element.node.column.NormalColumnEditPart;
import org.insightech.er.editor.controller.editpart.element.node.index.IndexEditPart;
import org.insightech.er.editor.controller.editpart.element.node.removed.RemovedERTableEditPart;
import org.insightech.er.editor.controller.editpart.element.node.removed.RemovedNoteEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.CommentConnection;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.VGroup;
import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.insightech.er.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.tracking.RemovedERTable;
import org.insightech.er.editor.model.tracking.RemovedNote;

public class ERDiagramEditPartFactory implements EditPartFactory {

	public ERDiagramEditPartFactory() {
	}

	public EditPart createEditPart(EditPart context, Object model) {
		EditPart editPart = null;

		if (model instanceof ERModel) {
			editPart = new ERModelEditPart();
		} else if (model instanceof ERVirtualTable) {
			editPart = new ERVirtualTableEditPart();
		} else if (model instanceof ERTable) {
			editPart = new ERTableEditPart();

		} else if (model instanceof View) {
			editPart = new ViewEditPart();

		} else if (model instanceof ERDiagram) {
			editPart = new ERDiagramEditPart();

		} else if (model instanceof Relation) {
			editPart = new RelationEditPart();

		} else if (model instanceof Note) {
			editPart = new NoteEditPart();

		} else if (model instanceof Index) {
			editPart = new IndexEditPart();

		} else if (model instanceof ModelProperties) {
			editPart = new ModelPropertiesEditPart();

		} else if (model instanceof CommentConnection) {
			editPart = new CommentConnectionEditPart();

		} else if (model instanceof Category) {
			editPart = new CategoryEditPart();

		} else if (model instanceof RemovedERTable) {
			editPart = new RemovedERTableEditPart();

		} else if (model instanceof RemovedNote) {
			editPart = new RemovedNoteEditPart();

		} else if (model instanceof NormalColumn) {
			editPart = new NormalColumnEditPart();

		} else if (model instanceof ColumnGroup) {
			editPart = new GroupColumnEditPart();

		} else if (model instanceof InsertedImage) {
			editPart = new InsertedImageEditPart();

		} else if (model instanceof VGroup) {
			editPart = new VGroupEditPart();

		}

		if (editPart != null) {
			editPart.setModel(model);
		} else {
			System.out.println("error");
		}

		return editPart;
	}
}
