package org.insightech.er.editor.controller.command.diagram_contents.element.node;

import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.EROneDiagramEditor;
import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ERModelUtil;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.VGroup;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.view.dialog.dbexport.ErrorDialog;

public class CreateElementCommand extends AbstractCommand {

	private ERDiagram diagram;

	private NodeElement element;

	private List<NodeElement> enclosedElementList;

	public CreateElementCommand(ERDiagram diagram,
			NodeElement element, int x,
			int y, Dimension size, List<NodeElement> enclosedElementList) {
		this.diagram = diagram;
		this.element = element;

		if (this.element instanceof Category && size != null) {
			this.element
					.setLocation(new Location(x, y, size.width, size.height));
		} else {
			this.element.setLocation(new Location(x, y, ERTable.DEFAULT_WIDTH,
					ERTable.DEFAULT_HEIGHT));
		}

		if (element instanceof ERTable) {
			ERTable table = (ERTable) element;
			table.setLogicalName(ERTable.NEW_LOGICAL_NAME);
			table.setPhysicalName(ERTable.NEW_PHYSICAL_NAME);

		} else if (element instanceof View) {
			View view = (View) element;
			view.setLogicalName(View.NEW_LOGICAL_NAME);
			view.setPhysicalName(View.NEW_PHYSICAL_NAME);
		}

		this.enclosedElementList = enclosedElementList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		if (element instanceof VGroup) {
			VGroup group = (VGroup) this.element;
			group.setName(ResourceString.getResourceString("label.vgroup"));
			group.setContents(this.enclosedElementList);
			if (diagram.getCurrentErmodel() != null) {
				diagram.getCurrentErmodel().addGroup(group);
			} else {
				ErrorDialog dialog = new ErrorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						"全体ビューにグループは設定できません。");
				dialog.open();
			}
		} else {
			if (diagram.getCurrentErmodel() != null) {
				diagram.getCurrentErmodel().addNewContent(this.element);
				ERModelUtil.refreshDiagram(diagram, element);
			} else {
				this.diagram.addNewContent(this.element);
			}
		}

//		if (!(this.element instanceof VGroup)) {
//		} else {
//			VGroup group = (VGroup) this.element;
//			group.setName(ResourceString.getResourceString("label.vgroup"));
//			group.setContents(this.enclosedElementList);
//			this.diagram.addGroup(group);
//		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		if (!(this.element instanceof Category)) {
			this.diagram.removeContent(this.element);

		} else {
			Category category = (Category) this.element;
			category.getContents().clear();
			this.diagram.removeCategory(category);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canExecute() {
		if (this.element instanceof Category) {
			if (this.diagram.getCurrentCategory() != null) {
				return false;
			}
		}

		return super.canExecute();
	}

}
