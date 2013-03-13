package org.insightech.er.editor.view.tool;

import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.tools.PanningSelectionTool;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.MoveElementCommand;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPart;
import org.insightech.er.editor.controller.editpart.element.node.ERTableEditPart;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.controller.editpart.element.node.NoteEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;

public class MovablePanningSelectionTool extends PanningSelectionTool {

	public static boolean shift = false;

	@Override
	protected boolean handleKeyUp(KeyEvent event) {
		if (event.keyCode == SWT.SHIFT) {
			shift = true;
		}

		return super.handleKeyUp(event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean handleKeyDown(KeyEvent event) {
		int dx = 0;
		int dy = 0;

		if (event.keyCode == SWT.SHIFT) {
			shift = true;
		}

		if (event.keyCode == SWT.ARROW_DOWN) {
			dy = 1;

		} else if (event.keyCode == SWT.ARROW_LEFT) {
			dx = -1;

		} else if (event.keyCode == SWT.ARROW_RIGHT) {
			dx = 1;

		} else if (event.keyCode == SWT.ARROW_UP) {
			dy = -1;
		}

		Object model = this.getCurrentViewer().getContents().getModel();
		if (model instanceof ERDiagram) {
			ERDiagram diagram = (ERDiagram) model;

			List selectedObject = this.getCurrentViewer().getSelectedEditParts();
			if (!selectedObject.isEmpty()) {

				CompoundCommand command = new CompoundCommand();

				for (Object object : selectedObject) {

					if (object instanceof ERTableEditPart
							|| object instanceof NoteEditPart) {
						NodeElementEditPart editPart = (NodeElementEditPart) object;

						NodeElement nodeElement = (NodeElement) editPart.getModel();

						MoveElementCommand moveElementCommand = new MoveElementCommand(
								diagram, editPart.getFigure().getBounds(),
								nodeElement.getX() + dx, nodeElement.getY() + dy,
								nodeElement.getWidth(), nodeElement.getHeight(),
								nodeElement);

						command.add(moveElementCommand);
					}
				}

				this.getCurrentViewer().getEditDomain().getCommandStack().execute(
						command.unwrap());
			}
		}

		return super.handleKeyDown(event);
	}

	@Override
	public void mouseDown(MouseEvent e, EditPartViewer viewer) {
		if (viewer.getContents() instanceof ERDiagramEditPart) {
			ERDiagramEditPart editPart = (ERDiagramEditPart) viewer
					.getContents();
			ERDiagram diagram = (ERDiagram) editPart.getModel();

			diagram.mousePoint = new Point(e.x, e.y);

			editPart.getFigure().translateToRelative(diagram.mousePoint);
		}
		
		super.mouseDown(e, viewer);
	}

}
