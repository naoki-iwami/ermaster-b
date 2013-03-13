package org.insightech.er.editor.controller.editpolicy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.DirectEditRequest;
import org.insightech.er.Activator;
import org.insightech.er.editor.controller.command.common.NothingToDoCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.connection.relation.bendpoint.MoveBendpointCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.CreateElementCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.MoveElementCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.MoveVGroupCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.PlaceTableCommand;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.category.MoveCategoryCommand;
import org.insightech.er.editor.controller.editpart.element.AbstractModelEditPart;
import org.insightech.er.editor.controller.editpart.element.node.CategoryEditPart;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.controller.editpart.element.node.VGroupEditPart;
import org.insightech.er.editor.controller.editpolicy.element.node.NodeElementSelectionEditPolicy;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ERModelUtil;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.VGroup;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.view.drag_drop.ERDiagramTransferDragSourceListener;

public class ERDiagramLayoutEditPolicy extends XYLayoutEditPolicy {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void showSizeOnDropFeedback(CreateRequest request) {
		Point p = new Point(request.getLocation().getCopy());

		ZoomManager zoomManager = ((ScalableFreeformRootEditPart) this
				.getHost().getRoot()).getZoomManager();
		double zoom = zoomManager.getZoom();

		IFigure feedback = getSizeOnDropFeedback(request);

		Dimension size = request.getSize().getCopy();
		feedback.translateToRelative(size);
		feedback.setBounds(new Rectangle((int) (p.x * zoom),
				(int) (p.y * zoom), size.width, size.height)
				.expand(getCreationFeedbackOffset(request)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command createChangeConstraintCommand(
			ChangeBoundsRequest request, EditPart child, Object constraint) {
		if (!(child instanceof NodeElementEditPart)) {
			return null;
		}

		try {
			Rectangle rectangle = (Rectangle) constraint;

			List selectedEditParts = this.getHost().getViewer()
					.getSelectedEditParts();

			NodeElementEditPart editPart = (NodeElementEditPart) child;
			NodeElement nodeElement = (NodeElement) editPart.getModel();
			Rectangle currentRectangle = editPart.getFigure().getBounds();

			boolean move = false;

			if (rectangle.width == currentRectangle.width
					&& rectangle.height == currentRectangle.height) {
				move = true;
			}

			boolean nothingToDo = false;

			if (move && !(editPart instanceof CategoryEditPart)) {
				for (Object selectedEditPart : selectedEditParts) {
					if (selectedEditPart instanceof CategoryEditPart) {
						CategoryEditPart categoryEditPart = (CategoryEditPart) selectedEditPart;
						Category category = (Category) categoryEditPart
								.getModel();

						if (category.contains(nodeElement)) {
							nothingToDo = true;
						}
					}
				}
			}

			if (move && !(editPart instanceof VGroupEditPart)) {
				for (Object selectedEditPart : selectedEditParts) {
					if (selectedEditPart instanceof VGroupEditPart) {
						VGroupEditPart categoryEditPart = (VGroupEditPart) selectedEditPart;
						VGroup category = (VGroup) categoryEditPart.getModel();

						if (category.contains(nodeElement)) {
							nothingToDo = true;
						}
					}
				}
			}

			List<Command> bendpointMoveCommandList = new ArrayList<Command>();

			int oldX = nodeElement.getX();
			int oldY = nodeElement.getY();

			int diffX = rectangle.x - oldX;
			int diffY = rectangle.y - oldY;

			for (Object obj : editPart.getSourceConnections()) {
				AbstractConnectionEditPart connection = (AbstractConnectionEditPart) obj;

				if (selectedEditParts.contains(connection.getTarget())) {
					ConnectionElement connectionElement = (ConnectionElement) connection
							.getModel();

					List<Bendpoint> bendpointList = connectionElement
							.getBendpoints();

					for (int index = 0; index < bendpointList.size(); index++) {
						Bendpoint bendPoint = bendpointList.get(index);

						if (bendPoint.isRelative()) {
							break;
						}

						MoveBendpointCommand moveCommand = new MoveBendpointCommand(
								connection, bendPoint.getX() + diffX, bendPoint
										.getY()
										+ diffY, index);
						bendpointMoveCommandList.add(moveCommand);
					}

				}
			}

			CompoundCommand compoundCommand = new CompoundCommand();

			if (!nothingToDo) {
				Command changeConstraintCommand = this
						.createChangeConstraintCommand(editPart, rectangle);

				if (bendpointMoveCommandList.isEmpty()) {
					return changeConstraintCommand;

				}

				compoundCommand.add(changeConstraintCommand);

			} else {
				compoundCommand.add(new NothingToDoCommand());
			}

			for (Command command : bendpointMoveCommandList) {
				compoundCommand.add(command);
			}

			return compoundCommand;

		} catch (Exception e) {
			Activator.log(e);
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		Rectangle rectangle = (Rectangle) constraint;

		NodeElementEditPart editPart = (NodeElementEditPart) child;
		NodeElement nodeElement = (NodeElement) editPart.getModel();
		Rectangle currentRectangle = editPart.getFigure().getBounds();

		boolean move = false;

		if (rectangle.width == currentRectangle.width
				&& rectangle.height == currentRectangle.height) {
			move = true;
		}

		if (nodeElement instanceof Category) {
			Category category = (Category) nodeElement;
			List<Category> otherCategories = null;
			if (move) {
				if (this.getOtherCategory((Category) nodeElement) != null) {
					return null;
				}
				otherCategories = this.getOtherSelectedCategories(category);
			}
			ERDiagram diagram = ERModelUtil.getDiagram(getHost());
			return new MoveCategoryCommand(
					diagram, rectangle.x, rectangle.y, rectangle.width,
					rectangle.height, category, otherCategories, move);

		} else if (nodeElement instanceof VGroup) {
			VGroup vgroup = (VGroup) nodeElement;
			List<VGroup> otherGroups = null;
			if (move) {
				// TODO
//				if (this.getOtherCategory((VGroup) nodeElement) != null) {
//					return null;
//				}
				otherGroups = this.getOtherSelectedGroups(vgroup);
			}
			ERDiagram diagram = ERModelUtil.getDiagram(getHost());
			return new MoveVGroupCommand(
					diagram, rectangle.x, rectangle.y, rectangle.width,
					rectangle.height, vgroup, otherGroups, move);

		} else {
			ERDiagram diagram = ERModelUtil.getDiagram(getHost());
			return new MoveElementCommand(
					diagram, currentRectangle,
					rectangle.x, rectangle.y, rectangle.width,
					rectangle.height, nodeElement);
		}
	}

	private Category getOtherCategory(Category category) {
		ERDiagram diagram = ERModelUtil.getDiagram(getHost());

		List<Category> selectedCategories = diagram.getDiagramContents()
				.getSettings().getCategorySetting().getSelectedCategories();

		for (NodeElement nodeElement : category.getContents()) {
			for (Category otherCategory : selectedCategories) {
				if (otherCategory != category && !isSelected(otherCategory)) {
					if (otherCategory.contains(nodeElement)) {
						return otherCategory;
					}
				}
			}
		}

		return null;
	}
	
	private List<Category> getOtherSelectedCategories(Category category) {
		List<Category> otherCategories = new ArrayList<Category>();

		List selectedEditParts = this.getHost().getViewer()
				.getSelectedEditParts();

		for (Object object : selectedEditParts) {
			if (object instanceof CategoryEditPart) {
				CategoryEditPart categoryEditPart = (CategoryEditPart) object;
				Category otherCategory = (Category) categoryEditPart.getModel();

				if (otherCategory == category) {
					break;
				}

				otherCategories.add(otherCategory);
			}
		}

		return otherCategories;
	}



//	private Category getOtherGroup(VGroup vgroup) {
//		ERModel model = (ERModel) getHost().getModel();
//
//		List<VGroup> selectedCategories = diagram.getDiagramContents()
//				.getSettings().getCategorySetting().getSelectedCategories();
//
//		for (NodeElement nodeElement : vgroup.getContents()) {
//			for (VGroup otherCategory : selectedCategories) {
//				if (otherCategory != vgroup && !isSelected(otherCategory)) {
//					if (otherCategory.contains(nodeElement)) {
//						return otherCategory;
//					}
//				}
//			}
//		}
//
//		return null;
//	}

	private List<VGroup> getOtherSelectedGroups(VGroup group) {
		List<VGroup> otherCategories = new ArrayList<VGroup>();

		List selectedEditParts = this.getHost().getViewer()
				.getSelectedEditParts();

		for (Object object : selectedEditParts) {
			if (object instanceof VGroupEditPart) {
				VGroupEditPart categoryEditPart = (VGroupEditPart) object;
				VGroup otherCategory = (VGroup) categoryEditPart.getModel();

				if (otherCategory == group) {
					break;
				}

				otherCategories.add(otherCategory);
			}
		}

		return otherCategories;
	}

	private boolean isSelected(Category category) {
		List selectedEditParts = this.getHost().getViewer()
				.getSelectedEditParts();

		for (Object object : selectedEditParts) {
			if (object instanceof NodeElementEditPart) {
				NodeElementEditPart editPart = (NodeElementEditPart) object;
				if (editPart.getModel() == category) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getCreateCommand(CreateRequest request) {
//		if (getHost() instanceof ERModelEditPart) {
//			ERModelEditPart editPart = (ERModelEditPart) this.getHost();
//
//			Point point = request.getLocation();
//			editPart.getFigure().translateToRelative(point);
//
//			NodeElement element = (NodeElement) request.getNewObject();
//			ERDiagram diagram = (ERDiagram) editPart.getModel();
//
//			Dimension size = request.getSize();
//			List<NodeElement> enclosedElementList = new ArrayList<NodeElement>();
//
//			if (size != null) {
//				ZoomManager zoomManager = ((ScalableFreeformRootEditPart) this
//						.getHost().getRoot()).getZoomManager();
//				double zoom = zoomManager.getZoom();
//				size = new Dimension((int) (size.width / zoom),
//						(int) (size.height / zoom));
//
//				for (Object child : editPart.getChildren()) {
//					if (child instanceof NodeElementEditPart) {
//						NodeElementEditPart nodeElementEditPart = (NodeElementEditPart) child;
//						Rectangle bounds = nodeElementEditPart.getFigure()
//								.getBounds();
//
//						if (bounds.x > point.x
//								&& bounds.x + bounds.width < point.x + size.width
//								&& bounds.y > point.y
//								&& bounds.y + bounds.height < point.y + size.height) {
//							enclosedElementList
//									.add((NodeElement) nodeElementEditPart
//											.getModel());
//						}
//					}
//				}
//			}
//			return new CreateElementCommand(diagram, element, point.x, point.y,
//					size, enclosedElementList);
//		}
		AbstractModelEditPart editPart = (AbstractModelEditPart) this.getHost();

		Point point = request.getLocation();
		editPart.getFigure().translateToRelative(point);

		NodeElement element = (NodeElement) request.getNewObject();
		ERDiagram diagram = ERModelUtil.getDiagram(editPart);

		Dimension size = request.getSize();
		List<NodeElement> enclosedElementList = new ArrayList<NodeElement>();

		if (size != null) {
			ZoomManager zoomManager = ((ScalableFreeformRootEditPart) this
					.getHost().getRoot()).getZoomManager();
			double zoom = zoomManager.getZoom();
			size = new Dimension((int) (size.width / zoom),
					(int) (size.height / zoom));

			for (Object child : editPart.getChildren()) {
				if (child instanceof NodeElementEditPart) {
					NodeElementEditPart nodeElementEditPart = (NodeElementEditPart) child;
					Rectangle bounds = nodeElementEditPart.getFigure()
							.getBounds();

					if (bounds.x > point.x
							&& bounds.x + bounds.width < point.x + size.width
							&& bounds.y > point.y
							&& bounds.y + bounds.height < point.y + size.height) {
						enclosedElementList
								.add((NodeElement) nodeElementEditPart
										.getModel());
					}
				}
			}
		}
		return new CreateElementCommand(diagram, element, point.x, point.y,
				size, enclosedElementList);
	}

	@Override
	protected EditPolicy createChildEditPolicy(EditPart child) {
		return new NodeElementSelectionEditPolicy();
	}
	
	@Override
	public Command getCommand(Request request) {

		if (ERDiagramTransferDragSourceListener.REQUEST_TYPE_PLACE_TABLE
				.equals(request.getType())) {
			
			DirectEditRequest editRequest = (DirectEditRequest) request;
			Object feature = editRequest.getDirectEditFeature();
			if (feature instanceof ERTable) {
				ERTable ertable = (ERTable) feature;
				return new PlaceTableCommand(ertable);
			}
			if (feature instanceof List) {
				List list = (List) feature;
				return new PlaceTableCommand(list);
			}
		}

		// TODO Auto-generated method stub
		return super.getCommand(request);
	}

}
