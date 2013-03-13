package org.insightech.er.editor.view.action.line;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.LabelRetargetAction;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.MoveElementCommand;
import org.insightech.er.editor.controller.editpart.element.node.ERTableEditPart;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.controller.editpart.element.node.NoteEditPart;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.view.action.AbstractBaseSelectionAction;

public class VerticalLineAction extends AbstractBaseSelectionAction {

	public static final String ID = VerticalLineAction.class.getName();

	public VerticalLineAction(ERDiagramEditor editor) {
		super(ID, ResourceString
				.getResourceString("action.title.vertical.line"), editor);

		this.setImageDescriptor(Activator
				.getImageDescriptor(ImageKey.VERTICAL_LINE));
//		this.setDisabledImageDescriptor(Activator
//				.getImageDescriptor(ImageKey.VERTICAL_LINE_DISABLED));
		this.setToolTipText(ResourceString
				.getResourceString("action.title.vertical.line"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean calculateEnabled() {
		Command cmd = this.createCommand();
		if (cmd == null) {
			return false;
		}
		return cmd.canExecute();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void execute(Event event) {
		execute(createCommand());
	}

	private Command createCommand() {
		Command command = null;
		try {
			List<NodeElementEditPart> list = new ArrayList<NodeElementEditPart>();

			for (Object object : this.getSelectedObjects()) {
				if (object instanceof ERTableEditPart
						|| object instanceof NoteEditPart) {
					list.add((NodeElementEditPart) object);
				}
			}

			if (list.size() < 3) {
				return null;
			}

			NodeElementEditPart firstEditPart = this.getFirstEditPart(list);
			list.remove(firstEditPart);

			Collections.sort(list, comparator);

			Rectangle firstRectangle = firstEditPart.getFigure().getBounds();
			int start = firstRectangle.y;
			int top = firstRectangle.y + firstRectangle.height;

			Rectangle lastRectangle = list.remove(list.size() - 1).getFigure()
					.getBounds();
			int bottom = lastRectangle.y;

			if (top > bottom) {
				command = this.alignToStart(start, list);

			} else {
				command = this.adjustSpace(start, top, bottom, list);
			}
		} catch (Exception e) {
			Activator.log(e);
		}
		
		return command;
	}

	private Command alignToStart(int start, List<NodeElementEditPart> list) {
		CompoundCommand command = new CompoundCommand();

		for (NodeElementEditPart editPart : list) {
			NodeElement nodeElement = (NodeElement) editPart.getModel();

			MoveElementCommand moveCommand = new MoveElementCommand(this
					.getDiagram(), editPart.getFigure().getBounds(),
					nodeElement.getX(), start, nodeElement.getWidth(),
					nodeElement.getHeight(), nodeElement);

			command.add(moveCommand);
		}

		return command.unwrap();
	}

	private Command adjustSpace(int start, int top, int bottom,
			List<NodeElementEditPart> list) {
		CompoundCommand command = new CompoundCommand();

		int totalHeight = 0;

		for (NodeElementEditPart editPart : list) {
			totalHeight += editPart.getFigure().getBounds().height;
		}

		int space = (bottom - top - totalHeight) / (list.size() + 1);

		int y = top;

		for (NodeElementEditPart editPart : list) {
			NodeElement nodeElement = (NodeElement) editPart.getModel();

			y += space;

			int nextY = y + editPart.getFigure().getBounds().height;

			if (y < start) {
				y = start;
			}

			MoveElementCommand moveCommand = new MoveElementCommand(this
					.getDiagram(), editPart.getFigure().getBounds(),
					nodeElement.getX(), y, nodeElement.getWidth(), nodeElement
							.getHeight(), nodeElement);

			command.add(moveCommand);

			y = nextY;
		}

		return command.unwrap();
	}

	private NodeElementEditPart getFirstEditPart(List<NodeElementEditPart> list) {
		NodeElementEditPart firstEditPart = null;

		for (NodeElementEditPart editPart : list) {
			if (firstEditPart == null) {
				firstEditPart = editPart;

			} else {
				if (firstEditPart.getFigure().getBounds().y > editPart
						.getFigure().getBounds().y) {
					firstEditPart = editPart;
				}
			}
		}

		return firstEditPart;
	}

	private static final Comparator<NodeElementEditPart> comparator = new NodeElementEditPartVerticalComparator();

	private static class NodeElementEditPartVerticalComparator implements
			Comparator<NodeElementEditPart> {

		public int compare(NodeElementEditPart o1, NodeElementEditPart o2) {
			if (o1 == null) {
				return -1;
			}
			if (o2 == null) {
				return 1;
			}

			Rectangle bounds1 = o1.getFigure().getBounds();
			Rectangle bounds2 = o2.getFigure().getBounds();

			int rightY1 = bounds1.y + bounds1.height;
			int rightY2 = bounds2.y + bounds2.height;

			return rightY1 - rightY2;
		}

	}

	public static class VerticalLineRetargetAction extends LabelRetargetAction {
		public VerticalLineRetargetAction() {
			super(ID, ResourceString
					.getResourceString("action.title.vertical.line"));

			this.setImageDescriptor(Activator
					.getImageDescriptor(ImageKey.VERTICAL_LINE));
//			this.setDisabledImageDescriptor(Activator
//					.getImageDescriptor(ImageKey.VERTICAL_LINE_DISABLED));
			this.setToolTipText(ResourceString
					.getResourceString("action.title.vertical.line"));
		}
	}

	@Override
	protected List<Command> getCommand(EditPart editPart, Event event) {
		return null;
	}
}
