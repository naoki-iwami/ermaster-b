package org.insightech.er.util;

import org.insightech.er.db.impl.mysql.MySQLDBManager;
import org.insightech.er.db.impl.postgres.PostgresDBManager;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;

public class Format {

	public static String formatType(SqlType sqlType, TypeData typeData,
			String database) {
		String type = null;

		if (sqlType != null) {
			type = sqlType.getAlias(database);
			if (type != null) {
				if (typeData.getLength() != null
						&& typeData.getDecimal() != null) {
					type = type.replaceAll("\\(.,.\\)", "("
							+ typeData.getLength() + ","
							+ typeData.getDecimal() + ")");

					type = type.replaceFirst("\\([a-z]\\)",
							"(" + typeData.getLength() + ")").replaceFirst(
							"\\([a-z]\\)", "(" + typeData.getDecimal() + ")");

				} else if (typeData.getLength() != null) {
					String len = null;

					if ("BLOB".equalsIgnoreCase(type)) {
						len = getFileSizeStr(typeData.getLength().longValue());
					} else {
						len = String.valueOf(typeData.getLength());
					}

					type = type.replaceAll("\\(.\\)", "(" + len + ")");

				}

				if (typeData.isArray() && PostgresDBManager.ID.equals(database)) {
					for (int i=0; i <typeData.getArrayDimension(); i++) {
						type += "[]";
					}
				}

				if (sqlType.isNumber() && typeData.isUnsigned()
						&& MySQLDBManager.ID.equals(database)) {
					type += " unsigned";
				}

				if (sqlType.doesNeedArgs()) {
					type += "(" + typeData.getArgs() + ")";
				}

			} else {
				type = "";
			}

		} else {
			type = "";
		}

		return type;
	}

	public static String getFileSizeStr(long fileSize) {
		long size = fileSize;
		String unit = "";

		if (size > 1024) {
			size = size / 1024;
			unit = "K";

			if (size > 1024) {
				size = size / 1024;
				unit = "M";

				if (size > 1024) {
					size = size / 1024;
					unit = "G";
				}
			}
		}

		return size + unit;
	}

	public static String null2blank(String str) {
		if (str == null) {
			return "";
		}
		return str;
	}

	public static String escapeSQL(String str) {
		str = str.replaceAll("'", "''");
		str = str.replaceAll("\\\\", "\\\\\\\\");

		return str;
	}

	public static String toString(Object value) {
		if (value == null) {
			return "";
		}

		return String.valueOf(value);
	}
}
