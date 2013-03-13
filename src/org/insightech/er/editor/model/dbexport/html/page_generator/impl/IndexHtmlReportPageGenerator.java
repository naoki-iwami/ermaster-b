package org.insightech.er.editor.model.dbexport.html.page_generator.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.page_generator.AbstractHtmlReportPageGenerator;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.util.Format;

public class IndexHtmlReportPageGenerator extends
		AbstractHtmlReportPageGenerator {

	public IndexHtmlReportPageGenerator(Map<Object, Integer> idMap) {
		super(idMap);
	}

	public String getType() {
		return "index";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Object> getObjectList(ERDiagram diagram) {
		List<Object> list = new ArrayList<Object>();

		for (NodeElement nodeElement : diagram.getDiagramContents()
				.getContents()) {
			if (nodeElement instanceof ERTable) {
				ERTable table = (ERTable) nodeElement;
				list.addAll(table.getIndexes());
			}
		}

		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getContentArgs(ERDiagram diagram, Object object)
			throws IOException {
		Index index = (Index) object;

		ERTable table = index.getTable();

		String description = Format.null2blank(index.getDescription());
		String tableId = this.getObjectId(table);
		String tableName = Format.null2blank(table.getName());

		String unique = this.getUniqueString(index);

		List<NormalColumn> normalColumnList = index.getColumns();
		List<Boolean> descs = index.getDescs();
		
		String indexAttribute = this.generateIndexAttributeTable(table,
				normalColumnList, descs);

		return new String[] { description, tableId, tableName,
				this.getType(index), unique, indexAttribute };
	}

	private String getType(Index index) {
		if (index.isFullText()) {
			return "FULLTEXT";
		}

		return Format.null2blank(index.getType());
	}

	public String getObjectName(Object object) {
		Index index = (Index) object;

		return index.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getObjectSummary(Object object) {
		Index index = (Index) object;

		return index.getDescription();
	}

	private String getUniqueString(Index index) {
		if (!index.isNonUnique()) {
			return "UNIQUE";
		} else {
			return "";
		}
	}
}
