package org.insightech.er.db.impl.sqlserver;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.insightech.er.editor.model.dbimport.ImportFromDBManagerBase;

public class SqlServerTableImportManager extends ImportFromDBManagerBase {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getViewDefinitionSQL(String schema) {
		return "SELECT VIEW_DEFINITION FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?";
	}

	@Override
	protected ColumnData createColumnData(ResultSet columnSet)
			throws SQLException {
		ColumnData columnData = super.createColumnData(columnSet);
		String type = columnData.type.toLowerCase();

		if (type.startsWith("decimal")) {
			if (columnData.size == 18 && columnData.decimalDegits == 0) {
				columnData.size = 0;
			}

		} else if (type.startsWith("numeric")) {
			if (columnData.size == 18 && columnData.decimalDegits == 0) {
				columnData.size = 0;
			}

		} else if (type.startsWith("time")) {
			columnData.size = columnData.size - 9;

			if (columnData.size == 7) {
				columnData.size = 0;
			}

		} else if (type.startsWith("datetime2")) {
			columnData.size = columnData.size - 20;

		}

		return columnData;
	}
}
