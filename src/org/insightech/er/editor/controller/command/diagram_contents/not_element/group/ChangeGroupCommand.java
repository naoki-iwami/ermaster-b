package org.insightech.er.editor.controller.command.diagram_contents.not_element.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.CopyGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;

public class ChangeGroupCommand extends AbstractCommand {

	private GroupSet groupSet;

	private List<CopyGroup> oldCopyGroups;

	private List<CopyGroup> newGroups;

	private Map<TableView, List<Column>> oldColumnListMap;

	private ERDiagram diagram;

	public ChangeGroupCommand(ERDiagram diagram, GroupSet groupSet,
			List<CopyGroup> newGroups) {
		this.diagram = diagram;

		this.groupSet = groupSet;

		this.newGroups = newGroups;

		this.oldCopyGroups = new ArrayList<CopyGroup>();
		this.oldColumnListMap = new HashMap<TableView, List<Column>>();

		for (ColumnGroup columnGroup : groupSet) {
			CopyGroup oldCopyGroup = new CopyGroup(columnGroup);
			this.oldCopyGroups.add(oldCopyGroup);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		ERDiagram diagram = this.diagram;

		this.groupSet.clear();
		this.oldColumnListMap.clear();

		for (CopyGroup oldCopyColumnGroup : oldCopyGroups) {
			for (NormalColumn column : oldCopyColumnGroup.getColumns()) {
				diagram.getDiagramContents().getDictionary().remove(
						((CopyColumn) column).getOriginalColumn());
			}
		}

		for (CopyGroup newCopyColumnGroup : newGroups) {
			this.groupSet.add(newCopyColumnGroup.restructure(diagram));
		}

		for (TableView tableView : this.diagram.getDiagramContents()
				.getContents().getTableViewList()) {
			List<Column> columns = tableView.getColumns();
			List<Column> oldColumns = new ArrayList<Column>(columns);

			this.oldColumnListMap.put(tableView, oldColumns);

			for (Iterator<Column> iter = columns.iterator(); iter.hasNext();) {
				Column column = iter.next();

				if (column instanceof ColumnGroup) {
					if (!this.groupSet.contains((ColumnGroup) column)) {
						iter.remove();
					}
				}
			}

			tableView.setColumns(columns);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		ERDiagram diagram = this.diagram;

		this.groupSet.clear();

		for (CopyGroup newCopyColumnGroup : newGroups) {
			for (NormalColumn column : newCopyColumnGroup.getColumns()) {
				diagram.getDiagramContents().getDictionary().remove(
						((CopyColumn) column).getOriginalColumn());
			}
		}

		for (CopyGroup copyGroup : oldCopyGroups) {
			ColumnGroup group = copyGroup.restructure(diagram);
			this.groupSet.add(group);
		}

		for (TableView tableView : this.oldColumnListMap.keySet()) {
			List<Column> oldColumns = this.oldColumnListMap.get(tableView);
			tableView.setColumns(oldColumns);
		}
	}
}
