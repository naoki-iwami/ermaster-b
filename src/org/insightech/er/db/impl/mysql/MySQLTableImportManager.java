package org.insightech.er.db.impl.mysql;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.dbimport.ImportFromDBManagerBase;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;

public class MySQLTableImportManager extends ImportFromDBManagerBase {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getViewDefinitionSQL(String schema) {
		if (schema != null) {
			return "SELECT view_definition FROM information_schema.views WHERE table_schema = ? AND table_name = ?";

		} else {
			return "SELECT view_definition FROM information_schema.views WHERE table_name = ?";

		}
	}

	@Override
	protected List<Index> getIndexes(ERTable table, DatabaseMetaData metaData,
			List<PrimaryKeyData> primaryKeys) throws SQLException {

		List<Index> indexes = super.getIndexes(table, metaData, primaryKeys);

		for (Iterator<Index> iter = indexes.iterator(); iter.hasNext();) {
			Index index = iter.next();

			if ("PRIMARY".equalsIgnoreCase(index.getName())) {
				iter.remove();
			}
		}

		return indexes;
	}

	@Override
	protected String getConstraintName(PrimaryKeyData data) {
		return null;
	}

	@Override
	protected void cashOtherColumnData(String tableName, String schema,
			ColumnData columnData) throws SQLException {
		String tableNameWithSchema = this.dbSetting.getTableNameWithSchema(
				tableName, schema);

		SqlType sqlType = SqlType.valueOfId(columnData.type);

		if (sqlType != null && sqlType.doesNeedArgs()) {
			String restrictType = this.getRestrictType(tableNameWithSchema,
					columnData);

			Pattern p = Pattern.compile(columnData.type.toLowerCase()
					+ "\\((.*)\\)");
			Matcher m = p.matcher(restrictType);

			if (m.matches()) {
				columnData.enumData = m.group(1);
			}

		} else if (columnData.type.equals("year")) {
			String restrictType = this.getRestrictType(tableNameWithSchema,
					columnData);
			columnData.type = restrictType;
		}
	}

	private String getRestrictType(String tableNameWithSchema,
			ColumnData columnData) throws SQLException {
		String type = null;

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("SHOW COLUMNS FROM `"
					+ tableNameWithSchema + "` LIKE ?");

			ps.setString(1, columnData.columnName);
			rs = ps.executeQuery();

			if (rs.next()) {
				type = rs.getString("Type");
			}

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
		}

		return type;
	}

	@Override
	protected ColumnData createColumnData(ResultSet columnSet)
			throws SQLException {
		ColumnData columnData = super.createColumnData(columnSet);
		String type = columnData.type.toLowerCase();

		if (type.startsWith("decimal")) {
			if (columnData.size == 10 && columnData.decimalDegits == 0) {
				columnData.size = 0;
			}

		} else if (type.startsWith("double")) {
			if (columnData.size == 22 && columnData.decimalDegits == 0) {
				columnData.size = 0;
			}

		} else if (type.startsWith("float")) {
			if (columnData.size == 12 && columnData.decimalDegits == 0) {
				columnData.size = 0;
			}

		}

		return columnData;
	}
}
