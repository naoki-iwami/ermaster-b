package org.insightech.er.db.impl.hsqldb;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.insightech.er.editor.model.dbimport.ImportFromDBManagerBase;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;

public class HSQLDBTableImportManager extends ImportFromDBManagerBase {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getViewDefinitionSQL(String schema) {
		return "SELECT VIEW_DEFINITION FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? ";
	}

	@Override
	protected ColumnData createColumnData(ResultSet columnSet)
			throws SQLException {
		ColumnData columnData = super.createColumnData(columnSet);
		String type = columnData.type.toLowerCase();

		if (type.startsWith("decimal")) {
			if (columnData.size == 128 && columnData.decimalDegits == 0) {
				columnData.size = 0;

			} else if (columnData.size == 646456993
					&& columnData.decimalDegits == 0) {
				columnData.size = 0;
			}

		} else if (type.startsWith("numeric")) {
			if (columnData.size == 128 && columnData.decimalDegits == 0) {
				columnData.size = 0;

			} else if (columnData.size == 646456993
					&& columnData.decimalDegits == 0) {
				columnData.size = 0;
			}

		} else if (type.startsWith("float")) {
			if (columnData.size == 17) {
				columnData.size = 0;

			} else if (columnData.size == 646456993
					&& columnData.decimalDegits == 0) {
				columnData.size = 0;
			}

		} else if (type.startsWith("clob")) {
			if (columnData.size == 16777216) {
				columnData.size = 0;
			}

		} else if (type.startsWith("blob")) {
			if (columnData.size == 16777216) {
				columnData.size = 0;
			}

		} else if (type.startsWith("varchar")) {
			if (columnData.size == 16777216) {
				columnData.size = 0;
				columnData.type = "longvarchar";
			}

		} else if (type.startsWith("varbinary")) {
			if (columnData.size == 16777216) {
				columnData.size = 0;
				columnData.type = "longvarbinary";
			}

		} else if (type.startsWith("timestamp")) {
			columnData.size = columnData.size - 20;

			if (columnData.size == 6) {
				columnData.size = 0;
			}

		}

		return columnData;
	}

	@Override
	protected Sequence importSequence(String schema, String sequenceName)
			throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = con
					.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA = ? AND SEQUENCE_NAME = ?");
			stmt.setString(1, schema);
			stmt.setString(2, sequenceName);

			rs = stmt.executeQuery();

			if (rs.next()) {
				Sequence sequence = new Sequence();

				sequence.setName(sequenceName);
				sequence.setSchema(schema);
				sequence.setIncrement(rs.getInt("INCREMENT"));

				Long minValue = rs.getLong("MINIMUM_VALUE");
				Long maxValue = rs.getLong("MAXIMUM_VALUE");

				String dataType = rs.getString("DATA_TYPE");
				sequence.setDataType(dataType);

				if ("INTEGER".equals(dataType)) {
					if (maxValue.intValue() == Integer.MAX_VALUE) {
						maxValue = null;
					}
					if (minValue.intValue() == Integer.MIN_VALUE) {
						minValue = null;
					}

				} else if ("BIGINT".equals(dataType)) {
					if (maxValue.longValue() == Long.MAX_VALUE) {
						maxValue = null;
					}
					if (minValue.intValue() == Long.MIN_VALUE) {
						minValue = null;
					}

				}

				sequence.setMinValue(minValue);

				if (maxValue != null) {
					sequence.setMaxValue(new BigDecimal(maxValue));

				} else {
					sequence.setMaxValue(null);

				}

				sequence.setStart(rs.getLong("START_WITH"));

				boolean cycle = false;
				if ("YES".equals(rs.getString("CYCLE_OPTION"))) {
					cycle = true;
				}
				sequence.setCycle(cycle);

				return sequence;
			}

			return null;

		} finally {
			this.close(rs);
			this.close(stmt);
		}
	}
}
