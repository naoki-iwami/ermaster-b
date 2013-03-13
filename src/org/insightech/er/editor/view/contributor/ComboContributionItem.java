package org.insightech.er.editor.view.contributor;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchPage;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.ERDiagramMultiPageEditor;
import org.insightech.er.editor.model.ViewableModel;

public abstract class ComboContributionItem extends ContributionItem {

	private Combo combo;

	private ToolItem toolitem;

	private IWorkbenchPage workbenchPage;

	public ComboContributionItem(String id, IWorkbenchPage workbenchPage) {
		super(id);

		this.workbenchPage = workbenchPage;
	}

	@Override
	public final void fill(Composite parent) {
		this.createControl(parent);
	}

	@Override
	public void fill(ToolBar parent, int index) {
		this.toolitem = new ToolItem(parent, SWT.SEPARATOR, index);
		Control control = this.createControl(parent);
		this.toolitem.setControl(control);
	}

	protected Control createControl(Composite parent) {
		this.combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		// FontData fontData =
		// Display.getCurrent().getSystemFont().getFontData()[0];
		// Font font = new Font(Display.getCurrent(), fontData.getName(), 7,
		// SWT.NORMAL);
		// this.combo.setFont(font);
		this.setData(this.combo);

		this.combo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				List selectedEditParts = ((IStructuredSelection) workbenchPage
						.getSelection()).toList();

				CompoundCommand compoundCommand = new CompoundCommand();

				for (Object editPart : selectedEditParts) {

					Object model = ((EditPart) editPart).getModel();

					if (model instanceof ViewableModel) {
						ViewableModel viewableModel = (ViewableModel) model;

						Command command = createCommand(viewableModel);

						if (command != null) {
							compoundCommand.add(command);
						}
					}
				}

				if (!compoundCommand.getCommands().isEmpty()) {
					executeCommand(compoundCommand);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		this.combo.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
			}
		});

		this.toolitem.setWidth(this.computeWidth(this.combo));
		return combo;
	}

	abstract protected Command createCommand(ViewableModel viewableModel);

	private int computeWidth(Control control) {
		return control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
	}

	abstract protected void setData(Combo combo);

	private void executeCommand(Command command) {
		ERDiagramMultiPageEditor multiPageEditor = (ERDiagramMultiPageEditor) this.workbenchPage
				.getActiveEditor();
		ERDiagramEditor editor = (ERDiagramEditor) multiPageEditor
				.getActiveEditor();
		editor.getGraphicalViewer().getEditDomain().getCommandStack().execute(
				command);
	}

	public void setText(String text) {
		if (this.combo != null && !this.combo.isDisposed() && text != null) {
			this.combo.setText(text);
		}
	}

	public String getText() {
		return this.combo.getText();
	}
}
