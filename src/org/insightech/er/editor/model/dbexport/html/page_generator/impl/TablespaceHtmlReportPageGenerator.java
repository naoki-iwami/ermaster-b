package org.insightech.er.editor.model.dbexport.html.page_generator.impl;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.ExportToHtmlManager;
import org.insightech.er.editor.model.dbexport.html.page_generator.AbstractHtmlReportPageGenerator;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.insightech.er.editor.model.settings.Environment;
import org.insightech.er.util.Format;

public class TablespaceHtmlReportPageGenerator extends
		AbstractHtmlReportPageGenerator {

	public TablespaceHtmlReportPageGenerator(Map<Object, Integer> idMap) {
		super(idMap);
	}

	public String getType() {
		return "tablespace";
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getObjectList(ERDiagram diagram) {
		List list = diagram.getDiagramContents().getTablespaceSet()
				.getTablespaceList();

		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getContentArgs(ERDiagram diagram, Object object)
			throws IOException {
		Tablespace tablespace = (Tablespace) object;

		String environments = this
				.generateEnvironmentTable(diagram, tablespace);

		Tablespace defaultTablespace = diagram.getDiagramContents()
				.getSettings().getTableViewProperties().getTableSpace();

		List<TableView> usedTableList = new ArrayList<TableView>();

		for (ERTable table : diagram.getDiagramContents().getContents()
				.getTableSet()) {
			Tablespace useTablespace = table.getTableViewProperties()
					.getTableSpace();
			if (useTablespace == null) {
				if (defaultTablespace == tablespace) {
					usedTableList.add(table);
				}
			} else {
				if (useTablespace == tablespace) {
					usedTableList.add(table);
				}
			}
		}

		String usedTableTable = this.generateUsedTableTable(usedTableList);

		return new String[] { environments, usedTableTable };
	}

	public String getObjectName(Object object) {
		Tablespace tablespace = (Tablespace) object;

		return tablespace.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getObjectSummary(Object object) {
		return null;
	}

	private String generateEnvironmentTable(ERDiagram diagram,
			Tablespace tablespace) throws IOException {
		StringBuilder sb = new StringBuilder();

		String template = ExportToHtmlManager
				.getTemplate("types/environment_row_template.html");

		for (Environment environment : diagram.getDiagramContents()
				.getSettings().getEnvironmentSetting().getEnvironments()) {
			TablespaceProperties properties = tablespace.getPropertiesMap()
					.get(environment);
			if (properties == null) {
				continue;
			}

			Object[] args = { Format.null2blank(environment.getName()),
					this.generateValueTable(properties) };
			String row = MessageFormat.format(template, args);

			sb.append(row);
		}

		return sb.toString();
	}

	private String generateValueTable(TablespaceProperties properties)
			throws IOException {
		StringBuilder sb = new StringBuilder();

		String template = ExportToHtmlManager
				.getTemplate("types/value_row_template.html");

		for (Map.Entry<String, String> entry : properties.getPropertiesMap()
				.entrySet()) {
			Object[] args = { ResourceString.getResourceString(entry.getKey()),
					Format.null2blank(entry.getValue()) };
			String row = MessageFormat.format(template, args);

			sb.append(row);
		}

		return sb.toString();
	}

}
