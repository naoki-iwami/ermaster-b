package org.insightech.er.db.impl.postgres;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.insightech.er.editor.model.dbimport.ImportFromDBManagerBase;

public class PostgresTableImportManager extends ImportFromDBManagerBase {

	@Override
	protected String getTableNameWithSchema(String schema, String tableName) {
		return this.dbSetting.getTableNameWithSchema("\"" + tableName + "\"",
				schema);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getViewDefinitionSQL(String schema) {
		if (schema != null) {
			return "SELECT definition FROM pg_views WHERE schemaname = ? and viewname = ? ";

		} else {
			return "SELECT definition FROM pg_views WHERE viewname = ? ";

		}
	}

	@Override
	protected ColumnData createColumnData(ResultSet columnSet)
			throws SQLException {
		ColumnData columnData = super.createColumnData(columnSet);
		String type = columnData.type.toLowerCase();

		if (type.startsWith("time")) {
			if (columnData.decimalDegits == 6) {
				columnData.size = 0;
			} else {
				columnData.size = columnData.decimalDegits;
			}

			columnData.decimalDegits = 0;

		} else if (type.equals("numeric")) {
			if (columnData.size == 131089 && columnData.decimalDegits == 0) {
				columnData.size = 0;
			}

		}

		return columnData;
	}

	@Override
	protected void cashOtherColumnData(String tableName, String schema,
			ColumnData columnData) throws SQLException {

		if (columnData.type.equals("interval")) {
			String restrictType = this.getRestrictType(tableName, schema,
					columnData);

			if (restrictType != null && restrictType.indexOf("(") != -1) {
				columnData.size = columnData.decimalDegits;

			} else {
				columnData.size = 0;

			}

			columnData.type = restrictType;
			columnData.decimalDegits = 0;
		}
	}

	private String getRestrictType(String tableName, String schema,
			ColumnData columnData) throws SQLException {
		String type = null;

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("select atttypmod from pg_attribute"
					+ " inner join pg_stat_user_tables "
					+ " on pg_stat_user_tables.relid = pg_attribute.attrelid "
					+ " where pg_stat_user_tables.relname = ? "
					+ " and pg_stat_user_tables.schemaname=? "
					+ " and pg_attribute.attname = ?");

			ps.setString(1, tableName);
			ps.setString(2, schema);
			ps.setString(3, columnData.columnName);

			rs = ps.executeQuery();

			if (rs.next()) {
				int atttypmod = rs.getInt("atttypmod");

				if (atttypmod == 196607) {
					type = "interval month";

				} else if (atttypmod == 327679) {
					type = "interval year";

				} else if (atttypmod == 458751) {
					type = "interval year to month";

				} else if (atttypmod == 589823) {
					type = "interval day";

				} else if (atttypmod == 67174399) {
					type = "interval hour";

				} else if (atttypmod == 67698687) {
					type = "interval day to hour";

				} else if (atttypmod == 134283263) {
					type = "interval minute";

				} else if (atttypmod == 201916415) {
					type = "interval day to minute";

				} else if (atttypmod == 201392127) {
					type = "interval hour to minute";

				} else if (atttypmod == 268500991) {
					type = "interval second";

				} else if (atttypmod >= 268435457 && atttypmod <= 268435462) {
					type = "interval second(p)";

				} else if (atttypmod == 402718719) {
					type = "interval minute to second";

				} else if (atttypmod >= 402653185 && atttypmod <= 402653190) {
					type = "interval minute to second(p)";

				} else if (atttypmod == 469827583) {
					type = "interval hour to second";

				} else if (atttypmod >= 469762049 && atttypmod <= 469762054) {
					type = "interval hour to second(p)";

				} else if (atttypmod == 470351871) {
					type = "interval day to second";

				} else if (atttypmod >= 470286337 && atttypmod <= 470286342) {
					type = "interval day to second(p)";

				} else if (atttypmod >= 2147418113 && atttypmod <= 2147418118) {
					type = "interval(p)";

				} else {
					type = "interval";

				}
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
}
