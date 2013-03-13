package org.insightech.er.db.impl.oracle;

import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.db.sqltype.SqlTypeManagerBase;

public class OracleSqlTypeManager extends SqlTypeManagerBase {

	public int getByteLength(SqlType type, Integer length, Integer decimal) {
		if (type == null) {
			return 0;
		}

		// if (type.equals(SqlType.ALERT_TYPE)) {
		// return 0;
		// }
		// if (type.equals(SqlType.ANYDATA)) {
		// return 0;
		// }
		// if (type.equals(SqlType.BIGINT)) {
		// return 11;
		// }
		// if (type.equals(SqlType.BIG_SERIAL)) {
		// return 11;
		// }
		// if (type.equals(SqlType.BINARY_DOUBLE)) {
		// return 8;
		// }
		// if (type.equals(SqlType.BINARY_FLOAT)) {
		// return 4;
		// }
		// if (type.equals(SqlType.BINARY_N)) {
		// return 0;
		// }
		// if (type.equals(SqlType.BIT_N)) {
		// return 0;
		// }
		// if (type.equals(SqlType.BIT_VARYING_N)) {
		// return 0;
		// }
		// if (type.equals(SqlType.BLOB)) {
		// return 0;
		// }
		// if (type.equals(SqlType.BOOLEAN)) {
		// return 1 * 3;
		// }
		// if (type.equals(SqlType.CHARACTER_N)) {
		// return length * 3;
		// }
		// if (type.equals(SqlType.CLOB)) {
		// return 0;
		// }
		// if (type.equals(SqlType.DATE)) {
		// return 7;
		// }
		// if (type.equals(SqlType.DATE_TIME)) {
		// return 7;
		// }
		// if (type.equals(SqlType.DECIMAL_P_S)) {
		// return 1 + (int) Math.ceil(((double) length) / 2);
		// }
		// if (type.equals(SqlType.DOUBLE_PRECISION)) {
		// return 8;
		// }
		// if (type.equals(SqlType.FLOAT)) {
		// return 4;
		// }
		// if (type.equals(SqlType.FLOAT_P)) {
		// return (int) Math.ceil(((double) length) / 4);
		// }
		// if (type.equals(SqlType.FLOAT_M_D)) {
		// return (int) Math.ceil(((double) length) / 4);
		// }
		// if (type.equals(SqlType.INTEGER)) {
		// return 6;
		// }
		// if (type.equals(SqlType.INTERVAL_YEAR_TO_MONTH)) {
		// return 5;
		// }
		// if (type.equals(SqlType.INTERVAL_DAY_TO_SECOND)) {
		// return 11;
		// }
		// if (type.equals(SqlType.INTERVAL_YEAR_TO_MONTH_P)) {
		// return 5;
		// }
		// if (type.equals(SqlType.INTERVAL_DAY_TO_SECOND_P)) {
		// return 11;
		// }
		// if (type.equals(SqlType.LONG)) {
		// return length;
		// }
		// if (type.equals(SqlType.LONG_BLOB)) {
		// return 0;
		// }
		// if (type.equals(SqlType.LONG_RAW)) {
		// return length;
		// }
		// if (type.equals(SqlType.LONG_TEXT)) {
		// return 0;
		// }
		// if (type.equals(SqlType.MEDIUM_BLOB)) {
		// return 0;
		// }
		// if (type.equals(SqlType.MEDIUM_INT)) {
		// return 5;
		// }
		// if (type.equals(SqlType.MEDIUM_TEXT)) {
		// return 0;
		// }
		// if (type.equals(SqlType.NCHAR_N)) {
		// return length * 3;
		// }
		// if (type.equals(SqlType.NCLOB)) {
		// return 0;
		// }
		// if (type.equals(SqlType.NUMERIC)) {
		// return 20;
		// }
		// if (type.equals(SqlType.NUMERIC_P_S)) {
		// return 1 + (int) Math.ceil(((double) length) / 2);
		// }
		// if (type.equals(SqlType.NVARCHAR_N)) {
		// return length * 3;
		// }
		// if (type.equals(SqlType.REAL)) {
		// return 3;
		// }
		// if (type.equals(SqlType.REAL_M_D)) {
		// return 3;
		// }
		// if (type.equals(SqlType.SERIAL)) {
		// return 6;
		// }
		// if (type.equals(SqlType.SMALLINT)) {
		// return 4;
		// }
		// if (type.equals(SqlType.TEXT)) {
		// return 0;
		// }
		// if (type.equals(SqlType.TIME)) {
		// return 7;
		// }
		// if (type.equals(SqlType.TIME_P)) {
		// return 7;
		// }
		// if (type.equals(SqlType.TIME_WITH_TIME_ZONE)) {
		// return 13;
		// }
		// if (type.equals(SqlType.TIME_WITH_TIME_ZONE_P)) {
		// return 13;
		// }
		// if (type.equals(SqlType.TIMESTAMP)) {
		// return 11;
		// }
		// if (type.equals(SqlType.TIMESTAMP_P)) {
		// return 11;
		// }
		// if (type.equals(SqlType.TIMESTAMP_WITH_TIME_ZONE)) {
		// return 13;
		// }
		// if (type.equals(SqlType.TIMESTAMP_WITH_TIME_ZONE_P)) {
		// return 13;
		// }
		// if (type.equals(SqlType.TINY_BLOB)) {
		// return 0;
		// }
		// if (type.equals(SqlType.TINY_INT)) {
		// return 2;
		// }
		// if (type.equals(SqlType.TINY_TEXT)) {
		// return length * 3;
		// }
		// if (type.equals(SqlType.VARBINARY_N)) {
		// return length;
		// }
		// if (type.equals(SqlType.VARCHAR)) {
		// return length * 3;
		// }
		// if (type.equals(SqlType.VARCHAR_N)) {
		// return length * 3;
		// }
		// if (type.equals(SqlType.YEAR_2)) {
		// return 20;
		// }
		// if (type.equals(SqlType.YEAR_4)) {
		// return 20;
		// }
		// if (type.equals(SqlType.XML)) {
		// return length;
		// }
		return 0;
	}

}
