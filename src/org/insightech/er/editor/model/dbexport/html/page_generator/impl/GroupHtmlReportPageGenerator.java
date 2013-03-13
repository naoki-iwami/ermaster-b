package org.insightech.er.editor.model.dbexport.html.page_generator.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.page_generator.AbstractHtmlReportPageGenerator;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;

public class GroupHtmlReportPageGenerator extends
		AbstractHtmlReportPageGenerator {

	public GroupHtmlReportPageGenerator(Map<Object, Integer> idMap) {
		super(idMap);
	}

	public String getType() {
		return "group";
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getObjectList(ERDiagram diagram) {
		List list = diagram.getDiagramContents().getGroups().getGroupList();

		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getContentArgs(ERDiagram diagram, Object object)
			throws IOException {
		ColumnGroup columnGroup = (ColumnGroup) object;

		List<NormalColumn> normalColumnList = columnGroup.getColumns();

		String attributeTable = this.generateAttributeTable(diagram,
				normalColumnList);

		List<TableView> usedTableList = columnGroup.getUsedTalbeList(diagram);

		String usedTableTable = this.generateUsedTableTable(usedTableList);

		String attributeDetailTable = this.generateAttributeDetailTable(
				diagram, normalColumnList);

		return new String[] { attributeTable, usedTableTable,
				attributeDetailTable };
	}

	public String getObjectName(Object object) {
		ColumnGroup columnGroup = (ColumnGroup) object;

		return columnGroup.getGroupName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getObjectSummary(Object object) {
		return null;
	}

}
