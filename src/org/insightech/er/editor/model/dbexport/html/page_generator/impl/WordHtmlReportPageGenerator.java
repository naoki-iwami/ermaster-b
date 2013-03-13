package org.insightech.er.editor.model.dbexport.html.page_generator.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.page_generator.AbstractHtmlReportPageGenerator;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.ColumnHolder;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.util.Format;

public class WordHtmlReportPageGenerator extends
		AbstractHtmlReportPageGenerator {

	public WordHtmlReportPageGenerator(Map<Object, Integer> idMap) {
		super(idMap);
	}

	public String getType() {
		return "word";
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getObjectList(ERDiagram diagram) {
		List list = diagram.getDiagramContents().getDictionary().getWordList();

		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getContentArgs(ERDiagram diagram, Object object)
			throws IOException {
		Word word = (Word) object;

		String logicalName = word.getLogicalName();
		String physicalName = word.getPhysicalName();
		String type = "";
		if (word.getType() != null) {
			type = Format.formatType(word.getType(), word.getTypeData(),
					diagram.getDatabase());
		}

		String description = word.getDescription();

		List<TableView> usedTableList = new ArrayList<TableView>();

		List<NormalColumn> normalColumnList = diagram.getDiagramContents()
				.getDictionary().getColumnList(word);
		for (NormalColumn normalColumn : normalColumnList) {
			ColumnHolder columnHolder = normalColumn.getColumnHolder();
			if (columnHolder instanceof TableView) {
				usedTableList.add((TableView) columnHolder);

			} else {
				ColumnGroup columnGroup = (ColumnGroup) columnHolder;
				usedTableList.addAll(columnGroup.getUsedTalbeList(diagram));
			}
		}

		String usedTableTable = this.generateUsedTableTable(usedTableList);

		return new String[] { logicalName, physicalName, type, description,
				usedTableTable };
	}

	public String getObjectName(Object object) {
		Word word = (Word) object;

		return word.getLogicalName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getObjectSummary(Object object) {
		return null;
	}
}
