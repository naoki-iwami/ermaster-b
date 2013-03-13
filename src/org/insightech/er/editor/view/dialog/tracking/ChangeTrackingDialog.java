package org.insightech.er.editor.view.dialog.tracking;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ResourceString;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.controller.command.tracking.AddChangeTrackingCommand;
import org.insightech.er.editor.controller.command.tracking.CalculateChangeTrackingCommand;
import org.insightech.er.editor.controller.command.tracking.ChangeTrackingCommand;
import org.insightech.er.editor.controller.command.tracking.DeleteChangeTrackingCommand;
import org.insightech.er.editor.controller.command.tracking.ResetChangeTrackingCommand;
import org.insightech.er.editor.controller.command.tracking.UpdateChangeTrackingCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.tracking.ChangeTracking;
import org.insightech.er.util.Check;

public class ChangeTrackingDialog extends Dialog {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");

	private Table changeTrackingTable;

	private Text textArea = null;

	private Button registerButton;

	private Button updateButton;

	private Button deleteButton;

	private Button replaceButton;

	private Button comparisonDisplayButton;

	private Button comparisonResetButton;

	private GraphicalViewer viewer;

	private ERDiagram diagram;

	public ChangeTrackingDialog(Shell parentShell, GraphicalViewer viewer,
			ERDiagram diagram) {
		super(parentShell);

		this.viewer = viewer;
		this.diagram = diagram;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		this.getShell().setText(
				ResourceString
						.getResourceString("dialog.title.change.tracking"));

		Composite composite = (Composite) super.createDialogArea(parent);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 6;

		composite.setLayout(gridLayout);

		this.initialize(composite);

		this.setData();

		return composite;
	}

	private void initialize(Composite composite) {
		GridData tableGridData = new GridData();
		tableGridData.widthHint = 520;
		tableGridData.horizontalSpan = 6;
		tableGridData.heightHint = 150;

		this.changeTrackingTable = new Table(composite, SWT.BORDER | SWT.SINGLE
				| SWT.FULL_SELECTION);
		this.changeTrackingTable.setHeaderVisible(true);
		this.changeTrackingTable.setLayoutData(tableGridData);
		this.changeTrackingTable.setLinesVisible(true);

		CompositeFactory.createLabel(composite, "label.contents.of.change", 6);

		this.textArea = CompositeFactory.createTextArea(null, composite, null,
				-1, 100, 6, true);

		this.registerButton = new Button(composite, SWT.NONE);
		this.registerButton.setText(ResourceString
				.getResourceString("label.button.register"));

		this.updateButton = new Button(composite, SWT.NONE);
		this.updateButton.setText(ResourceString
				.getResourceString("label.button.update"));

		this.deleteButton = new Button(composite, SWT.NONE);
		this.deleteButton.setText(ResourceString
				.getResourceString("label.button.delete"));

		this.replaceButton = new Button(composite, SWT.NONE);
		this.replaceButton.setText(ResourceString
				.getResourceString("label.button.change.tracking"));

		this.comparisonDisplayButton = new Button(composite, SWT.NONE);
		this.comparisonDisplayButton.setText(ResourceString
				.getResourceString("label.button.comparison.display"));

		this.comparisonResetButton = new Button(composite, SWT.NONE);
		this.comparisonResetButton.setText(ResourceString
				.getResourceString("label.button.comparison.reset"));

		TableColumn tableColumn0 = new TableColumn(this.changeTrackingTable,
				SWT.LEFT);
		tableColumn0.setWidth(150);
		tableColumn0.setText(ResourceString.getResourceString("label.date"));

		TableColumn tableColumn1 = new TableColumn(this.changeTrackingTable,
				SWT.LEFT);
		tableColumn1.setWidth(400);
		tableColumn1.setText(ResourceString
				.getResourceString("label.contents.of.change"));

		this.changeTrackingTable.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = changeTrackingTable.getSelectionIndex();
				if (index == -1) {
					return;
				}

				selectChangeTracking(index);
			}
		});

		this.registerButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				ChangeTracking changeTracking = new ChangeTracking(diagram
						.getDiagramContents());
				changeTracking.setComment(textArea.getText());

				Command command = new AddChangeTrackingCommand(diagram,
						changeTracking);

				viewer.getEditDomain().getCommandStack().execute(command);

				int index = changeTrackingTable.getItemCount();

				setData();

				selectChangeTracking(index);
			}
		});

		this.updateButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = changeTrackingTable.getSelectionIndex();
				if (index == -1) {
					return;
				}

				ChangeTracking changeTracking = diagram.getChangeTrackingList()
						.get(index);

				Command command = new UpdateChangeTrackingCommand(
						changeTracking, textArea.getText());

				viewer.getEditDomain().getCommandStack().execute(command);

				setData();

				selectChangeTracking(index);
			}
		});

		this.deleteButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = changeTrackingTable.getSelectionIndex();
				if (index == -1) {
					return;
				}

				Command command = new DeleteChangeTrackingCommand(diagram,
						index);

				viewer.getEditDomain().getCommandStack().execute(command);

				setData();

				if (index >= changeTrackingTable.getItemCount()) {
					index = changeTrackingTable.getItemCount() - 1;
				}

				selectChangeTracking(index);
			}
		});

		this.replaceButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = changeTrackingTable.getSelectionIndex();
				if (index == -1) {
					return;
				}
				MessageBox messageBox = new MessageBox(PlatformUI
						.getWorkbench().getActiveWorkbenchWindow().getShell(),
						SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				messageBox.setText(ResourceString
						.getResourceString("dialog.title.change.tracking"));
				messageBox.setMessage(ResourceString
						.getResourceString("dialog.message.change.tracking"));

				if (messageBox.open() == SWT.YES) {
					ChangeTracking changeTracking = new ChangeTracking(diagram
							.getDiagramContents());
					changeTracking.setComment("");

					diagram.getChangeTrackingList().addChangeTracking(
							changeTracking);

					setData();

					changeTrackingTable.select(index);
				}

				ChangeTracking changeTracking = diagram.getChangeTrackingList()
						.get(index);

				ChangeTracking copy = new ChangeTracking(changeTracking
						.getDiagramContents());

				Command command = new ChangeTrackingCommand(diagram, copy
						.getDiagramContents());

				viewer.getEditDomain().getCommandStack().execute(command);
			}
		});

		this.comparisonDisplayButton
				.addSelectionListener(new SelectionAdapter() {

					/**
					 * {@inheritDoc}
					 */
					@Override
					public void widgetSelected(SelectionEvent e) {
						int index = changeTrackingTable.getSelectionIndex();
						if (index == -1) {
							return;
						}

						ChangeTracking changeTracking = diagram
								.getChangeTrackingList().get(index);

						NodeSet nodeElementList = changeTracking
								.getDiagramContents().getContents();

						Command command = new CalculateChangeTrackingCommand(
								diagram, nodeElementList);

						viewer.getEditDomain().getCommandStack().execute(
								command);

						comparisonResetButton.setEnabled(true);
					}
				});

		this.comparisonResetButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				Command command = new ResetChangeTrackingCommand(diagram);
				viewer.getEditDomain().getCommandStack().execute(command);

				comparisonResetButton.setEnabled(false);
			}
		});

		this.textArea.setFocus();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		this.createButton(parent, IDialogConstants.CLOSE_ID,
				IDialogConstants.CLOSE_LABEL, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CLOSE_ID) {
			setReturnCode(buttonId);
			close();
		}

		super.buttonPressed(buttonId);
	}

	private void setData() {
		this.changeTrackingTable.removeAll();

		this.setButtonEnabled(false);
		this.comparisonDisplayButton.setEnabled(false);

		for (ChangeTracking changeTracking : this.diagram
				.getChangeTrackingList().getList()) {
			TableItem tableItem = new TableItem(this.changeTrackingTable,
					SWT.NONE);

			String date = DATE_FORMAT.format(changeTracking.getUpdatedDate());
			tableItem.setText(0, date);

			if (!Check.isEmpty(changeTracking.getComment())) {
				tableItem.setText(1, changeTracking.getComment());
			} else {
				tableItem.setText(1, "*** empty log message ***");
			}
		}

		this.comparisonResetButton.setEnabled(this.diagram
				.getChangeTrackingList().isCalculated());
	}

	private void setButtonEnabled(boolean enabled) {
		this.updateButton.setEnabled(enabled);
		this.deleteButton.setEnabled(enabled);
		this.replaceButton.setEnabled(enabled);
		this.comparisonDisplayButton.setEnabled(enabled);
	}

	private void selectChangeTracking(int index) {
		this.changeTrackingTable.select(index);

		ChangeTracking changeTracking = this.diagram.getChangeTrackingList()
				.get(index);

		if (changeTracking.getComment() != null) {
			this.textArea.setText(changeTracking.getComment());
		} else {
			this.textArea.setText("");
		}

		if (index >= 0) {
			this.setButtonEnabled(true);
		} else {
			this.setButtonEnabled(false);
		}
	}
}
