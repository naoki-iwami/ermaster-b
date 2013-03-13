package org.insightech.er.editor.view.action.line;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.AlignmentRequest;
import org.eclipse.gef.tools.ToolUtilities;
import org.eclipse.gef.ui.actions.AlignmentAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.editor.controller.editpart.element.node.column.NormalColumnEditPart;

public class ERDiagramAlignmentAction extends SelectionAction {

	/**
	 * Indicates that the bottom edges should be aligned.
	 */
	public static final String ID_ALIGN_BOTTOM = GEFActionConstants.ALIGN_BOTTOM;

	/**
	 * Indicates that the horizontal centers should be aligned.
	 */
	public static final String ID_ALIGN_CENTER = GEFActionConstants.ALIGN_CENTER;

	/**
	 * Indicates that the left edges should be aligned.
	 */
	public static final String ID_ALIGN_LEFT = GEFActionConstants.ALIGN_LEFT;

	/**
	 * Indicates that the vertical midpoints should be aligned.
	 */
	public static final String ID_ALIGN_MIDDLE = GEFActionConstants.ALIGN_MIDDLE;

	/**
	 * Indicates that the right edges should be aligned.
	 */
	public static final String ID_ALIGN_RIGHT = GEFActionConstants.ALIGN_RIGHT;

	/**
	 * Indicates that the top edges should be aligned.
	 */
	public static final String ID_ALIGN_TOP = GEFActionConstants.ALIGN_TOP;
	private int alignment;

	private List operationSet;

	private static final AlignmentAction ALIGNMENT_ACTION_LEFT = new AlignmentAction(
			(IWorkbenchPart) null, PositionConstants.LEFT);

	private static final AlignmentAction ALIGNMENT_ACTION_RIGHT = new AlignmentAction(
			(IWorkbenchPart) null, PositionConstants.RIGHT);

	private static final AlignmentAction ALIGNMENT_ACTION_TOP = new AlignmentAction(
			(IWorkbenchPart) null, PositionConstants.TOP);

	private static final AlignmentAction ALIGNMENT_ACTION_BOTTOM = new AlignmentAction(
			(IWorkbenchPart) null, PositionConstants.BOTTOM);

	private static final AlignmentAction ALIGNMENT_ACTION_CENTER = new AlignmentAction(
			(IWorkbenchPart) null, PositionConstants.CENTER);

	private static final AlignmentAction ALIGNMENT_ACTION_MIDDLE = new AlignmentAction(
			(IWorkbenchPart) null, PositionConstants.MIDDLE);

	/**
	 * Constructs an AlignmentAction with the given part and alignment ID. The
	 * alignment ID must by one of:
	 * <UL>
	 * <LI>GEFActionConstants.ALIGN_LEFT
	 * <LI>GEFActionConstants.ALIGN_RIGHT
	 * <LI>GEFActionConstants.ALIGN_CENTER
	 * <LI>GEFActionConstants.ALIGN_TOP
	 * <LI>GEFActionConstants.ALIGN_BOTTOM
	 * <LI>GEFActionConstants.ALIGN_MIDDLE
	 * </UL>
	 * 
	 * @param part
	 *            the workbench part used to obtain context
	 * @param align
	 *            the aligment ID.
	 */
	public ERDiagramAlignmentAction(IWorkbenchPart part, int align) {
		super(part);
		alignment = align;
		initUI();
	}

	/**
	 * Returns the alignment rectangle to which all selected parts should be
	 * aligned.
	 * 
	 * @param request
	 *            the alignment Request
	 * @return the alignment rectangle
	 */
	protected Rectangle calculateAlignmentRectangle(Request request) {
		List editparts = getOperationSet(request);
		if (editparts == null || editparts.isEmpty())
			return null;
		GraphicalEditPart part = (GraphicalEditPart) editparts.get(editparts
				.size() - 1);
		Rectangle rect = new PrecisionRectangle(part.getFigure().getBounds());
		part.getFigure().translateToAbsolute(rect);
		return rect;
	}

	/**
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
	 */
	@Override
	protected boolean calculateEnabled() {
		operationSet = null;
		Command cmd = createAlignmentCommand();
		if (cmd == null)
			return false;
		return cmd.canExecute();
	}

	private Command createAlignmentCommand() {
		AlignmentRequest request = new AlignmentRequest(
				RequestConstants.REQ_ALIGN);
		request.setAlignmentRectangle(calculateAlignmentRectangle(request));
		request.setAlignment(alignment);
		List editparts = getOperationSet(request);
		if (editparts.size() < 2)
			return null;

		CompoundCommand command = new CompoundCommand();
		command.setDebugLabel(getText());
		for (int i = 0; i < editparts.size(); i++) {
			EditPart editpart = (EditPart) editparts.get(i);
			command.add(editpart.getCommand(request));
		}
		return command;
	}

	/**
	 * @see org.eclipse.gef.Disposable#dispose()
	 */
	@Override
	public void dispose() {
		operationSet = Collections.EMPTY_LIST;
		super.dispose();
	}

	/**
	 * Returns the list of editparts which will participate in alignment.
	 * 
	 * @param request
	 *            the alignment request
	 * @return the list of parts which will be aligned
	 */
	@SuppressWarnings("unchecked")
	protected List getOperationSet(Request request) {
		if (operationSet != null)
			return operationSet;
		List editparts = new ArrayList(getSelectedObjects());
		for (Iterator iter = editparts.iterator(); iter.hasNext();) {
			if (iter.next() instanceof NormalColumnEditPart) {
				iter.remove();
			}
		}

		if (editparts.isEmpty()
				|| !(editparts.get(0) instanceof GraphicalEditPart))
			return Collections.EMPTY_LIST;
		Object primary = editparts.get(editparts.size() - 1);
		editparts = ToolUtilities.getSelectionWithoutDependants(editparts);
		ToolUtilities.filterEditPartsUnderstanding(editparts, request);
		if (editparts.size() < 2 || !editparts.contains(primary))
			return Collections.EMPTY_LIST;
		EditPart parent = ((EditPart) editparts.get(0)).getParent();
		for (int i = 1; i < editparts.size(); i++) {
			EditPart part = (EditPart) editparts.get(i);
			if (part.getParent() != parent)
				return Collections.EMPTY_LIST;
		}
		return editparts;
	}

	/**
	 * Initializes the actions UI presentation.
	 */
	protected void initUI() {
		switch (alignment) {
		case PositionConstants.LEFT:
			setId(GEFActionConstants.ALIGN_LEFT);
			setText(ALIGNMENT_ACTION_LEFT.getText());
			setToolTipText(ALIGNMENT_ACTION_LEFT.getToolTipText());
			setImageDescriptor(Activator
					.getImageDescriptor(ImageKey.ALIGN_LEFT));
			// setDisabledImageDescriptor(ALIGNMENT_ACTION_LEFT
			// .getDisabledImageDescriptor());
			break;

		case PositionConstants.RIGHT:
			setId(GEFActionConstants.ALIGN_RIGHT);
			setText(ALIGNMENT_ACTION_RIGHT.getText());
			setToolTipText(ALIGNMENT_ACTION_RIGHT.getToolTipText());
			setImageDescriptor(Activator
					.getImageDescriptor(ImageKey.ALIGN_RIGHT));
//			setDisabledImageDescriptor(ALIGNMENT_ACTION_RIGHT
//					.getDisabledImageDescriptor());
			break;

		case PositionConstants.TOP:
			setId(GEFActionConstants.ALIGN_TOP);
			setText(ALIGNMENT_ACTION_TOP.getText());
			setToolTipText(ALIGNMENT_ACTION_TOP.getToolTipText());
			setImageDescriptor(Activator.getImageDescriptor(ImageKey.ALIGN_TOP));
//			setDisabledImageDescriptor(ALIGNMENT_ACTION_TOP
//					.getDisabledImageDescriptor());
			break;

		case PositionConstants.BOTTOM:
			setId(GEFActionConstants.ALIGN_BOTTOM);
			setText(ALIGNMENT_ACTION_BOTTOM.getText());
			setToolTipText(ALIGNMENT_ACTION_BOTTOM.getToolTipText());
			setImageDescriptor(Activator
					.getImageDescriptor(ImageKey.ALIGN_BOTTOM));
//			setDisabledImageDescriptor(ALIGNMENT_ACTION_BOTTOM
//					.getDisabledImageDescriptor());
			break;

		case PositionConstants.CENTER:
			setId(GEFActionConstants.ALIGN_CENTER);
			setText(ALIGNMENT_ACTION_CENTER.getText());
			setToolTipText(ALIGNMENT_ACTION_CENTER.getToolTipText());
			setImageDescriptor(Activator
					.getImageDescriptor(ImageKey.ALIGN_CENTER));
//			setDisabledImageDescriptor(ALIGNMENT_ACTION_CENTER
//					.getDisabledImageDescriptor());
			break;

		case PositionConstants.MIDDLE:
			setId(GEFActionConstants.ALIGN_MIDDLE);
			setText(ALIGNMENT_ACTION_MIDDLE.getText());
			setToolTipText(ALIGNMENT_ACTION_MIDDLE.getToolTipText());
			setImageDescriptor(Activator
					.getImageDescriptor(ImageKey.ALIGN_MIDDLE));
//			setDisabledImageDescriptor(ALIGNMENT_ACTION_MIDDLE
//					.getDisabledImageDescriptor());
			break;
		}
	}

	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	@Override
	public void run() {
		operationSet = null;
		execute(createAlignmentCommand());
	}

}
