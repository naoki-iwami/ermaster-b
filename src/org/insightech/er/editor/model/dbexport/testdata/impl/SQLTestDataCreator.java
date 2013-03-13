package org.insightech.er.editor.model.dbexport.testdata.impl;

import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.testdata.RepeatTestData;
import org.insightech.er.editor.model.testdata.RepeatTestDataDef;
import org.insightech.er.util.Format;

public class SQLTestDataCreator extends AbstractTextTestDataCreator {

	@Override
	protected void writeDirectTestData(ERTable table,
			Map<NormalColumn, String> data, String database) {
		StringBuilder sb = new StringBuilder();

		sb.append("INSERT INTO ");
		sb.append(table.getNameWithSchema(database));
		sb.append(" (");

		StringBuilder valueSb = new StringBuilder();

		boolean first = true;
		for (NormalColumn column : table.getExpandedColumns()) {
			if (!first) {
				sb.append(", ");
				valueSb.append(", ");
			}

			sb.append(column.getPhysicalName());

			String value = Format.null2blank(data.get(column));

			if (value != null && !"null".equals(value.toLowerCase())) {
				valueSb.append("'");
				valueSb.append(value);
				valueSb.append("'");

			} else {
				valueSb.append("null");
			}

			first = false;
		}

		sb.append(") VALUES (");
		sb.append(valueSb.toString());

		sb.append(");\r\n");

		out.print(sb.toString());
	}

	@Override
	protected void writeRepeatTestData(ERTable table,
			RepeatTestData repeatTestData, String database) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < repeatTestData.getTestDataNum(); i++) {
			sb.append("INSERT INTO ");
			sb.append(table.getNameWithSchema(database));
			sb.append(" (");

			StringBuilder valueSb = new StringBuilder();

			boolean first = true;
			for (NormalColumn column : table.getExpandedColumns()) {
				if (!first) {
					sb.append(", ");
					valueSb.append(", ");
				}

				sb.append(column.getPhysicalName());

				RepeatTestDataDef repeatTestDataDef = repeatTestData
						.getDataDef(column);

				String value = this.getMergedRepeatTestDataValue(i,
						repeatTestDataDef, column);

				if (value != null && !"null".equals(value.toLowerCase())) {
					valueSb.append("'");
					valueSb.append(value);
					valueSb.append("'");

				} else {
					valueSb.append("null");
				}

				first = false;
			}

			sb.append(") VALUES (");
			sb.append(valueSb.toString());

			sb.append(");\r\n");
		}

		out.print(sb.toString());
	}

	@Override
	protected String getFooter() {
		return "";
	}

	@Override
	protected String getHeader() {
		return "";
	}

	@Override
	protected void writeTableHeader(ERDiagram diagram, ERTable table) {
		StringBuilder sb = new StringBuilder();

		sb.append("-- ");
		sb.append(table.getLogicalName());
		sb.append("\r\n");

		out.print(sb.toString());
	}

	@Override
	protected void writeTableFooter(ERTable table) {
		StringBuilder sb = new StringBuilder();

		sb.append("\r\n");
		sb.append("\r\n");

		out.print(sb.toString());
	}

	@Override
	protected String getFileExtention() {
		return ".sql";
	}
}
