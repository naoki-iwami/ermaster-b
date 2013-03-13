package org.insightech.er.editor.model.dbexport.html.page_generator;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.ExportToHtmlManager;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.util.Format;

public abstract class AbstractHtmlReportPageGenerator implements
		HtmlReportPageGenerator {

	private Map<Object, Integer> idMap;

	public AbstractHtmlReportPageGenerator(Map<Object, Integer> idMap) {
		this.idMap = idMap;
	}

	public String getObjectId(Object object) {
		Integer id = (Integer) idMap.get(object);

		if (id == null) {
			id = new Integer(idMap.size());
			this.idMap.put(object, id);
		}

		return String.valueOf(id);
	}

	public String getPageTitle() {
		return ResourceString.getResourceString("html.report.page.title."
				+ this.getType());
	}

	public String generatePackageFrame(ERDiagram diagram) throws IOException {
		String template = ExportToHtmlManager
				.getTemplate("types/package-frame/package-frame_template.html");

		Object[] args = { this.getPageTitle(),
				this.generatePackageFrameTable(diagram) };
		return MessageFormat.format(template, args);
	}

	private String generatePackageFrameTable(ERDiagram diagram)
			throws IOException {
		StringBuilder sb = new StringBuilder();

		String template = ExportToHtmlManager
				.getTemplate("types/package-frame/package-frame_row_template.html");

		for (Object object : this.getObjectList(diagram)) {
			Object[] args = this.getPackageFrameRowArgs(object);
			String row = MessageFormat.format(template, args);

			sb.append(row);
		}

		return sb.toString();
	}

	public String[] getPackageFrameRowArgs(Object object) {
		return new String[] { this.getObjectId(object),
				this.getObjectName(object) };
	}

	public abstract List<Object> getObjectList(ERDiagram diagram);

	public String generatePackageSummary(
			HtmlReportPageGenerator prevPageGenerator,
			HtmlReportPageGenerator nextPageGenerator, ERDiagram diagram)
			throws IOException {
		String template = ExportToHtmlManager
				.getTemplate("types/package-summary/package-summary_template.html");

		String prevPage = "<b>"
				+ ResourceString
						.getResourceString("html.report.prev.object.type")
				+ "</b>";

		if (prevPageGenerator != null) {
			prevPage = "<a HREF=\"../" + prevPageGenerator.getType()
					+ "/package-summary.html\" >" + prevPage + "</a>";
		}

		String nextPage = "<b>"
				+ ResourceString
						.getResourceString("html.report.next.object.type")
				+ "</b>";

		if (nextPageGenerator != null) {
			nextPage = "<a HREF=\"../" + nextPageGenerator.getType()
					+ "/package-summary.html\" >" + nextPage + "</a>";
		}

		Object[] args = { this.getPageTitle(), prevPage, nextPage,
				this.generatePackageSummaryTable(diagram) };

		return MessageFormat.format(template, args);
	}

	private String generatePackageSummaryTable(ERDiagram diagram)
			throws IOException {
		StringBuilder sb = new StringBuilder();

		String template = ExportToHtmlManager
				.getTemplate("types/package-summary/package-summary_row_template.html");

		for (Object object : this.getObjectList(diagram)) {
			Object[] args = this.getPackageSummaryRowArgs(object);
			String row = MessageFormat.format(template, args);

			sb.append(row);
		}

		return sb.toString();
	}

	public String[] getPackageSummaryRowArgs(Object object) {
		return new String[] { this.getObjectId(object),
				Format.null2blank(this.getObjectName(object)),
				Format.null2blank(this.getObjectSummary(object)) };
	}

	public String generateContent(ERDiagram diagram, Object object,
			Object prevObject, Object nextObject) throws IOException {
		String template = ExportToHtmlManager
				.getTemplate("types/contents_template.html");

		String pageTitle = this.getPageTitle();

		String prevPage = "<b>"
				+ ResourceString.getResourceString("html.report.prev.of")
				+ pageTitle + "</b>";

		if (prevObject != null) {
			prevPage = "<a HREF=\"" + this.getObjectId(prevObject)
					+ ".html\" >" + prevPage + "</a>";
		}

		String nextPage = "<b>"
				+ ResourceString.getResourceString("html.report.next.of")
				+ pageTitle + "</b>";

		if (nextObject != null) {
			nextPage = "<a HREF=\"" + this.getObjectId(nextObject)
					+ ".html\" >" + nextPage + "</a>";
		}

		String mainTemplate = ExportToHtmlManager.getTemplate("types/main/"
				+ this.getType() + "_template.html");

		Object[] contentArgs = getContentArgs(diagram, object);

		mainTemplate = MessageFormat.format(mainTemplate, contentArgs);

		Object[] args = new String[] { this.getObjectName(object), pageTitle,
				prevPage, nextPage, mainTemplate, this.getObjectId(object) };

		return MessageFormat.format(template, args);
	}

	public abstract String getObjectSummary(Object object);

	public abstract String[] getContentArgs(ERDiagram diagram, Object object)
			throws IOException;

	protected String generateAttributeTable(ERDiagram diagram,
			List<NormalColumn> normalColumnList) throws IOException {
		StringBuilder sb = new StringBuilder();

		String template = ExportToHtmlManager
				.getTemplate("types/attribute_row_template.html");

		for (NormalColumn normalColumn : normalColumnList) {
			String type = null;
			if (normalColumn.getType() != null) {
				type = Format.formatType(normalColumn.getType(), normalColumn
						.getTypeData(), diagram.getDatabase());
			} else {
				type = "";
			}

			Object[] args = { this.getObjectId(normalColumn),
					this.getPKString(normalColumn),
					this.getForeignKeyString(normalColumn),
					Format.null2blank(normalColumn.getLogicalName()),
					Format.null2blank(normalColumn.getPhysicalName()), type,
					this.getUniqueString(normalColumn),
					this.getNotNullString(normalColumn) };

			String row = MessageFormat.format(template, args);

			sb.append(row);
		}

		return sb.toString();
	}

	public String generateAttributeDetailTable(ERDiagram diagram,
			List<NormalColumn> normalColumnList) throws IOException {
		StringBuilder sb = new StringBuilder();

		String template = ExportToHtmlManager
				.getTemplate("types/attribute_detail_row_template.html");

		for (NormalColumn normalColumn : normalColumnList) {
			String type = null;

			if (normalColumn.getType() != null) {
				type = Format.formatType(normalColumn.getType(), normalColumn
						.getTypeData(), diagram.getDatabase());
			} else {
				type = "";
			}

			Object[] args = {
					this.getObjectId(normalColumn),
					this.getPKString(normalColumn),
					this.getForeignKeyString(normalColumn),
					Format.null2blank(normalColumn.getLogicalName()),
					Format.null2blank(normalColumn.getPhysicalName()),
					Format.null2blank(normalColumn.getDescription()),
					String.valueOf(normalColumn.isUniqueKey()).toUpperCase(),
					String.valueOf(normalColumn.isNotNull()).toUpperCase(),
					type,
					String.valueOf(normalColumn.isAutoIncrement())
							.toUpperCase(),
					Format.null2blank(normalColumn.getDefaultValue()),
					Format.null2blank(normalColumn.getConstraint()) };
			String row = MessageFormat.format(template, args);

			sb.append(row);
		}

		return sb.toString();
	}

	public String generateUsedTableTable(List<TableView> tableList)
			throws IOException {
		StringBuilder sb = new StringBuilder();

		String template = ExportToHtmlManager
				.getTemplate("types/use_table_row_template.html");

		for (TableView table : tableList) {
			Object[] args = { this.getObjectId(table), table.getObjectType(),
					Format.null2blank(table.getPhysicalName()),
					Format.null2blank(table.getLogicalName()) };
			String row = MessageFormat.format(template, args);

			sb.append(row);
		}

		return sb.toString();
	}

	public String generateIndexAttributeTable(ERTable table,
			List<NormalColumn> normalColumnList, List<Boolean> descs)
			throws IOException {
		StringBuilder sb = new StringBuilder();

		String template = ExportToHtmlManager
				.getTemplate("types/index_attribute_row_template.html");

		int i = 0;

		for (NormalColumn normalColumn : normalColumnList) {
			String tableId = this.getObjectId(table);
			String columnId = this.getObjectId(normalColumn);
			String columnPhysicalName = Format.null2blank(normalColumn
					.getPhysicalName());
			String columnLogicalName = Format.null2blank(normalColumn
					.getLogicalName());
			Boolean desc = descs.get(i);
			String descStr = null;
			if (desc != null) {
				if (desc.booleanValue()) {
					descStr = "DESC";

				} else {
					descStr = "ASC";
				}

			} else {
				descStr = "";
			}

			Object[] args = { tableId, columnId, columnPhysicalName,
					columnLogicalName, descStr };
			String row = MessageFormat.format(template, args);

			sb.append(row);
			i++;
		}

		return sb.toString();
	}

	protected String generateForeignKeyTable(List<NormalColumn> foreignKeyList)
			throws IOException {
		StringBuilder sb = new StringBuilder();

		String template = ExportToHtmlManager
				.getTemplate("types/foreign_key_row_template.html");

		for (NormalColumn normalColumn : foreignKeyList) {
			for (Relation relation : normalColumn.getRelationList()) {
				TableView sourceTable = relation.getSourceTableView();

				Object[] args = {
						this.getObjectId(normalColumn),
						Format.null2blank(normalColumn.getName()),
						this.getObjectId(sourceTable),
						Format.null2blank(sourceTable.getName()),
						this.getObjectId(normalColumn
								.getReferencedColumn(relation)),
						Format.null2blank(normalColumn.getReferencedColumn(
								relation).getName()),
						relation.getOnUpdateAction(),
						relation.getOnDeleteAction(),
						Format.null2blank(relation.getParentCardinality()),
						Format.null2blank(relation.getChildCardinality()) };

				String row = MessageFormat.format(template, args);

				sb.append(row);
			}
		}

		return sb.toString();
	}

	protected String generateReferenceKeyTable(List<NormalColumn> foreignKeyList)
			throws IOException {
		StringBuilder sb = new StringBuilder();

		String template = ExportToHtmlManager
				.getTemplate("types/foreign_key_row_template.html");

		for (NormalColumn normalColumn : foreignKeyList) {
			for (Relation relation : normalColumn.getRelationList()) {
				TableView targetTable = relation.getTargetTableView();

				Object[] args = {
						this.getObjectId(normalColumn
								.getReferencedColumn(relation)),
						Format.null2blank(normalColumn.getReferencedColumn(
								relation).getName()),
						this.getObjectId(targetTable),
						Format.null2blank(targetTable.getName()),
						this.getObjectId(normalColumn),
						Format.null2blank(normalColumn.getName()),
						relation.getOnUpdateAction(),
						relation.getOnDeleteAction(),
						Format.null2blank(relation.getParentCardinality()),
						Format.null2blank(relation.getChildCardinality()) };

				String row = MessageFormat.format(template, args);

				sb.append(row);
			}
		}

		return sb.toString();
	}

	public String generateIndexSummaryTable(List<Index> indexList)
			throws IOException {
		StringBuilder sb = new StringBuilder();

		String template = ExportToHtmlManager
				.getTemplate("types/index_summary_row_template.html");

		for (Index index : indexList) {
			String id = this.getObjectId(index);
			String name = Format.null2blank(index.getName());
			String type = Format.null2blank(index.getType());
			String unique = null;
			if (!index.isNonUnique()) {
				unique = "UNIQUE";
			} else {
				unique = "";
			}

			Object[] args = { id, name, type, unique };
			String row = MessageFormat.format(template, args);

			sb.append(row);
		}

		return sb.toString();
	}

	public String generateIndexMatrix(List<Index> indexList,
			List<NormalColumn> normalColumnList) throws IOException {

		if (indexList.isEmpty()) {
			return "";
		}

		String template = ExportToHtmlManager
				.getTemplate("types/index_matrix/index_matrix_template.html");

		String headerTemplate = ExportToHtmlManager
				.getTemplate("types/index_matrix/index_matrix_header_column_template.html");

		StringBuilder header = new StringBuilder();

		for (Index index : indexList) {
			String name = index.getName();

			Object[] args = { name };
			String column = MessageFormat.format(headerTemplate, args);

			header.append(column);
		}

		String rowTemplate = ExportToHtmlManager
				.getTemplate("types/index_matrix/index_matrix_data_row_template.html");

		String dataColumnTemplate = ExportToHtmlManager
				.getTemplate("types/index_matrix/index_matrix_data_column_template.html");

		StringBuilder body = new StringBuilder();

		for (NormalColumn normalColumn : normalColumnList) {
			String name = normalColumn.getName();
			StringBuilder rowContent = new StringBuilder();

			for (Index index : indexList) {
				int no = 1;
				String noString = "";

				for (NormalColumn indexColumn : index.getColumns()) {
					if (indexColumn == normalColumn) {
						noString = String.valueOf(no);
						break;
					}
					no++;
				}

				Object[] args = { noString };
				String column = MessageFormat.format(dataColumnTemplate, args);

				rowContent.append(column);
			}

			Object[] args = { name, rowContent.toString() };
			String row = MessageFormat.format(rowTemplate, args);

			body.append(row);
		}

		template = MessageFormat.format(template, new Object[] {
				header.toString(), body.toString() });

		return template;
	}

	public String generateComplexUniqueKeyMatrix(
			List<ComplexUniqueKey> complexUniqueKeyList,
			List<NormalColumn> normalColumnList) throws IOException {

		String template = ExportToHtmlManager
				.getTemplate("types/complex_unique_key_matrix/complex_unique_key_matrix_template.html");

		String headerTemplate = ExportToHtmlManager
				.getTemplate("types/complex_unique_key_matrix/complex_unique_key_matrix_header_column_template.html");

		StringBuilder header = new StringBuilder();

		for (ComplexUniqueKey complexUniqueKey : complexUniqueKeyList) {
			String name = Format
					.null2blank(complexUniqueKey.getUniqueKeyName());

			Object[] args = { name };
			String column = MessageFormat.format(headerTemplate, args);

			header.append(column);
		}

		String rowTemplate = ExportToHtmlManager
				.getTemplate("types/complex_unique_key_matrix/complex_unique_key_matrix_data_row_template.html");

		String dataColumnTemplate = ExportToHtmlManager
				.getTemplate("types/complex_unique_key_matrix/complex_unique_key_matrix_data_column_template.html");

		StringBuilder body = new StringBuilder();

		if (!complexUniqueKeyList.isEmpty()) {
			for (NormalColumn normalColumn : normalColumnList) {
				String name = normalColumn.getName();
				StringBuilder rowContent = new StringBuilder();

				for (ComplexUniqueKey complexUniqueKey : complexUniqueKeyList) {
					int no = 1;
					String noString = "";

					for (NormalColumn complexUniqueKeyColumn : complexUniqueKey
							.getColumnList()) {
						if (complexUniqueKeyColumn == normalColumn) {
							noString = String.valueOf(no);
							break;
						}
						no++;
					}

					Object[] args = { noString };
					String column = MessageFormat.format(dataColumnTemplate,
							args);

					rowContent.append(column);
				}

				Object[] args = { name, rowContent.toString() };
				String row = MessageFormat.format(rowTemplate, args);

				body.append(row);
			}
		}

		template = MessageFormat.format(template, new Object[] {
				header.toString(), body.toString(),
				complexUniqueKeyList.size() + 1 });

		return template;
	}

	private String getPKString(NormalColumn normalColumn) {
		if (normalColumn.isPrimaryKey()) {
			return "<img src=\"../image/" + ExportToHtmlManager.ICON_FILES[0]
					+ "\">";
		} else {
			return "";
		}
	}

	private String getForeignKeyString(NormalColumn normalColumn) {
		if (normalColumn.isForeignKey()) {
			return "<img src=\"../image/" + ExportToHtmlManager.ICON_FILES[1]
					+ "\">";
		} else {
			return "";
		}
	}

	private String getUniqueString(NormalColumn normalColumn) {
		if (normalColumn.isUniqueKey()) {
			return "UNIQUE";
		} else {
			return "";
		}
	}

	private String getNotNullString(NormalColumn normalColumn) {
		if (normalColumn.isNotNull()) {
			return "NOT NULL";
		} else {
			return "";
		}
	}

}
