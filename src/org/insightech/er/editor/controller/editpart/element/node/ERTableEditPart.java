package org.insightech.er.editor.controller.editpart.element.node;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.db.impl.oracle.OracleDBManager;
import org.insightech.er.editor.controller.command.diagram_contents.element.node.table_view.ChangeTableViewPropertyCommand;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.sequence.CreateSequenceCommand;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.sequence.DeleteSequenceCommand;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.trigger.CreateTriggerCommand;
import org.insightech.er.editor.controller.command.diagram_contents.not_element.trigger.DeleteTriggerCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.TriggerSet;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.dialog.element.table.TableDialog;
import org.insightech.er.editor.view.figure.table.TableFigure;
import org.insightech.er.util.Check;

public class ERTableEditPart extends TableViewEditPart implements IResizable {

	public ERTableEditPart() {
		System.out.println("**ERTableEditPart**");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		ERDiagram diagram = this.getDiagram();
		Settings settings = diagram.getDiagramContents().getSettings();

		TableFigure figure = new TableFigure(settings.getTableStyle());

		this.changeFont(figure);

		return figure;
	}

	@Override
	public void doPropertyChange(PropertyChangeEvent event) {
		super.doPropertyChange(event);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performRequestOpen() {
		ERTable table = (ERTable) this.getModel();
		ERDiagram diagram = this.getDiagram();

		ERTable copyTable = table.copyData();

		TableDialog dialog = new TableDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), this.getViewer(),
				copyTable, diagram.getDiagramContents().getGroups());

		if (dialog.open() == IDialogConstants.OK_ID) {
			CompoundCommand command = createChangeTablePropertyCommand(diagram,
					table, copyTable);

			this.executeCommand(command.unwrap());
		}
	}

	public static CompoundCommand createChangeTablePropertyCommand(
			ERDiagram diagram, ERTable table, ERTable copyTable) {
		CompoundCommand command = new CompoundCommand();

		ChangeTableViewPropertyCommand changeTablePropertyCommand = new ChangeTableViewPropertyCommand(
				table, copyTable);
		command.add(changeTablePropertyCommand);

		String tableName = copyTable.getPhysicalName();

		if (OracleDBManager.ID.equals(diagram.getDatabase())
				&& !Check.isEmpty(tableName)) {
			NormalColumn autoIncrementColumn = copyTable
					.getAutoIncrementColumn();

			if (autoIncrementColumn != null) {
				String columnName = autoIncrementColumn.getPhysicalName();

				if (!Check.isEmpty(columnName)) {
					String triggerName = "TRI_" + tableName + "_" + columnName;
					String sequenceName = "SEQ_" + tableName + "_" + columnName;

					TriggerSet triggerSet = diagram.getDiagramContents()
							.getTriggerSet();
					SequenceSet sequenceSet = diagram.getDiagramContents()
							.getSequenceSet();

					if (!triggerSet.contains(triggerName)
							|| !sequenceSet.contains(sequenceName)) {
						if (Activator
								.showConfirmDialog("dialog.message.confirm.create.autoincrement.trigger")) {
							if (!triggerSet.contains(triggerName)) {
								// 繝医Μ繧ｬ繝ｼ縺ｮ菴懈�
								Trigger trigger = new Trigger();
								trigger.setName(triggerName);
								trigger.setSql("BEFORE INSERT ON " + tableName
										+ "\r\nFOR EACH ROW" + "\r\nBEGIN"
										+ "\r\n\tSELECT " + sequenceName
										+ ".nextval\r\n\tINTO :new."
										+ columnName + "\r\n\tFROM dual;"
										+ "\r\nEND");

								CreateTriggerCommand createTriggerCommand = new CreateTriggerCommand(
										diagram, trigger);
								command.add(createTriggerCommand);
							}

							if (!sequenceSet.contains(sequenceName)) {
								// 繧ｷ繝ｼ繧ｱ繝ｳ繧ｹ縺ｮ菴懈�
								Sequence sequence = new Sequence();
								sequence.setName(sequenceName);
								sequence.setStart(1L);
								sequence.setIncrement(1);

								CreateSequenceCommand createSequenceCommand = new CreateSequenceCommand(
										diagram, sequence);
								command.add(createSequenceCommand);
							}
						}
					}
				}
			}

			NormalColumn oldAutoIncrementColumn = table
					.getAutoIncrementColumn();

			if (oldAutoIncrementColumn != null) {
				if (autoIncrementColumn == null
						|| ((CopyColumn) autoIncrementColumn)
								.getOriginalColumn() != oldAutoIncrementColumn) {
					String oldTableName = table.getPhysicalName();
					String columnName = oldAutoIncrementColumn
							.getPhysicalName();

					if (!Check.isEmpty(columnName)) {
						String triggerName = "TRI_" + oldTableName + "_"
								+ columnName;
						String sequenceName = "SEQ_" + oldTableName + "_"
								+ columnName;

						TriggerSet triggerSet = diagram.getDiagramContents()
								.getTriggerSet();
						SequenceSet sequenceSet = diagram.getDiagramContents()
								.getSequenceSet();

						if (triggerSet.contains(triggerName)
								|| sequenceSet.contains(sequenceName)) {
							if (Activator
									.showConfirmDialog("dialog.message.confirm.remove.autoincrement.trigger")) {

								// 繝医Μ繧ｬ繝ｼ縺ｮ蜑企勁
								Trigger trigger = triggerSet.get(triggerName);

								if (trigger != null) {
									DeleteTriggerCommand deleteTriggerCommand = new DeleteTriggerCommand(
											diagram, trigger);
									command.add(deleteTriggerCommand);
								}

								// 繧ｷ繝ｼ繧ｱ繝ｳ繧ｹ縺ｮ菴懈�
								Sequence sequence = sequenceSet
										.get(sequenceName);

								if (sequence != null) {
									DeleteSequenceCommand deleteSequenceCommand = new DeleteSequenceCommand(
											diagram, sequence);
									command.add(deleteSequenceCommand);
								}
							}
						}
					}
				}
			}
		}

		return command;
	}

}
