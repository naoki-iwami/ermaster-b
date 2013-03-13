package org.insightech.er.db.sqltype;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.insightech.er.util.Format;

public class SqlType implements Serializable {

	private static Logger logger = Logger.getLogger(SqlType.class.getName());

	private static final long serialVersionUID = -8273043043893517634L;

	public static final String SQL_TYPE_ID_SERIAL = "serial";

	public static final String SQL_TYPE_ID_BIG_SERIAL = "bigserial";

	public static final String SQL_TYPE_ID_INTEGER = "integer";

	public static final String SQL_TYPE_ID_BIG_INT = "bigint";

	private static final Pattern NEED_LENGTH_PATTERN = Pattern
			.compile(".+\\([a-zA-Z][,\\)].*");

	private static final Pattern NEED_DECIMAL_PATTERN1 = Pattern
			.compile(".+\\([a-zA-Z],[a-zA-Z]\\)");

	private static final Pattern NEED_DECIMAL_PATTERN2 = Pattern
			.compile(".+\\([a-zA-Z]\\).*\\([a-zA-Z]\\)");

	private static final List<SqlType> SQL_TYPE_LIST = new ArrayList<SqlType>();

	private String name;

	private Class javaClass;

	private boolean needArgs;

	boolean fullTextIndexable;

	private static Map<String, Map<TypeKey, SqlType>> dbSqlTypeMap = new HashMap<String, Map<TypeKey, SqlType>>();

	private static Map<String, Map<SqlType, String>> dbAliasMap = new HashMap<String, Map<SqlType, String>>();

	static {
		try {
			SqlTypeFactory.load();

		} catch (Exception e) {
			e.printStackTrace();
			throw new ExceptionInInitializerError(e);
		}
	}

	public static class TypeKey {
		private String alias;

		private int size;

		public TypeKey(String alias, int size) {
			if (alias != null) {
				alias = alias.toUpperCase();
			}

			this.alias = alias;

			if (size == 0) {
				this.size = 0;
			} else if (size == Integer.MAX_VALUE) {
				this.size = 0;
			} else if (size > 0) {
				this.size = 1;
			} else {
				this.size = -1;
			}
		}

		@Override
		public boolean equals(Object obj) {
			TypeKey other = (TypeKey) obj;

			if (this.alias == null) {
				if (other.alias == null) {
					if (this.size == other.size) {
						return true;
					}
					return false;

				} else {
					return false;
				}

			} else {
				if (this.alias.equals(other.alias) && this.size == other.size) {
					return true;
				}
			}

			return false;
		}

		@Override
		public int hashCode() {
			if (this.alias == null) {
				return this.size;
			}
			return (this.alias.hashCode() * 10) + this.size;
		}

		@Override
		public String toString() {
			return "TypeKey [alias=" + alias + ", size=" + size + "]";
		}

	}

	public SqlType(String name, Class javaClass, boolean needArgs,
			boolean fullTextIndexable) {
		this.name = name;
		this.javaClass = javaClass;
		this.needArgs = needArgs;
		this.fullTextIndexable = fullTextIndexable;

		SQL_TYPE_LIST.add(this);
	}

	public static void setDBAliasMap(
			Map<String, Map<SqlType, String>> dbAliasMap,
			Map<String, Map<TypeKey, SqlType>> dbSqlTypeMap) {
		SqlType.dbAliasMap = dbAliasMap;
		SqlType.dbSqlTypeMap = dbSqlTypeMap;
	}

	public void addToSqlTypeMap(String typeKeyId, String database) {
		int size = 0;

		if (!this.isUnsupported(database)) {
			if (this.isNeedLength(database)) {
				size = 1;
			}
			TypeKey typeKey = new TypeKey(typeKeyId, size);
			Map<TypeKey, SqlType> sqlTypeMap = dbSqlTypeMap.get(database);
			sqlTypeMap.put(typeKey, this);
		}
	}

	public String getId() {
		return this.name;
	}

	public Class getJavaClass() {
		return this.javaClass;
	}

	public boolean doesNeedArgs() {
		return this.needArgs;
	}

	public boolean isFullTextIndexable() {
		return this.fullTextIndexable;
	}

	protected static List<SqlType> getAllSqlType() {
		return SQL_TYPE_LIST;
	}

	public static SqlType valueOf(String database, String alias) {
		int size = 0;

		if (alias.indexOf("(") != -1) {
			size = 1;
		}

		return valueOf(database, alias, size);
	}

	public static SqlType valueOf(String database, String alias, int size) {
		if (alias == null) {
			return null;
		}

		Map<TypeKey, SqlType> sqlTypeMap = dbSqlTypeMap.get(database);

		// decimal(19,4) = money 等に対応
		TypeKey typeKey = new TypeKey(alias, size);
		SqlType sqlType = sqlTypeMap.get(typeKey);

		if (sqlType == null) {
			alias = alias.replaceAll("\\(.*\\)", "");
			alias = alias.replaceAll(" UNSIGNED", "");

			typeKey = new TypeKey(alias, size);
			sqlType = sqlTypeMap.get(typeKey);

			if (sqlType == null) {
				// db import の場合に、サイズが取得されていても、指定はできないケースがある
				typeKey = new TypeKey(alias, 0);
				sqlType = sqlTypeMap.get(typeKey);
			}
		}

		return sqlType;
	}

	public static SqlType valueOfId(String id) {
		SqlType sqlType = null;

		if (id == null) {
			return null;
		}

		for (SqlType type : SQL_TYPE_LIST) {
			if (id.equals(type.getId())) {
				sqlType = type;
			}
		}
		return sqlType;
	}

	public boolean isNeedLength(String database) {
		String alias = this.getAlias(database);
		if (alias == null) {
			return false;
		}

		Matcher matcher = NEED_LENGTH_PATTERN.matcher(alias);

		if (matcher.matches()) {
			return true;
		}

		return false;
	}

	public boolean isNeedDecimal(String database) {
		String alias = this.getAlias(database);
		if (alias == null) {
			return false;
		}

		Matcher matcher = NEED_DECIMAL_PATTERN1.matcher(alias);

		if (matcher.matches()) {
			return true;
		}

		matcher = NEED_DECIMAL_PATTERN2.matcher(alias);

		if (matcher.matches()) {
			return true;
		}

		return false;
	}

	public boolean isTimestamp() {
		if (this.javaClass == Date.class) {
			return true;
		}

		return false;
	}

	public boolean isNumber() {
		if (Number.class.isAssignableFrom(this.javaClass)) {
			return true;
		}

		return false;
	}

	public static List<String> getAliasList(String database) {
		Map<SqlType, String> aliasMap = dbAliasMap.get(database);

		Set<String> aliases = new LinkedHashSet<String>();

		for (Entry<SqlType, String> entry : aliasMap.entrySet()) {
			String alias = entry.getValue();
			aliases.add(alias);
		}

		List<String> list = new ArrayList<String>(aliases);
		
		Collections.sort(list);
		
		return list;
	}

	public String getAlias(String database) {
		Map<SqlType, String> aliasMap = dbAliasMap.get(database);

		return aliasMap.get(this);
	}

	public boolean isUnsupported(String database) {
		String alias = this.getAlias(database);

		if (alias == null) {
			return true;
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof SqlType)) {
			return false;
		}

		SqlType type = (SqlType) obj;

		return this.name.equals(type.name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.getId();
	}

	public static void main(String[] args) {
		int maxIdLength = 37;

		StringBuilder msg = new StringBuilder();

		msg.append("\n");

		List<SqlType> list = getAllSqlType();

		List<String> dbList = DBManagerFactory.getAllDBList();

		String str = "ID";
		msg.append(str);

		for (String db : dbList) {
			int spaceLength = maxIdLength - str.length();
			if (spaceLength < 4) {
				spaceLength = 4;
			}

			for (int i = 0; i < spaceLength; i++) {
				msg.append(" ");
			}

			str = db;
			msg.append(db);
		}

		msg.append("\n");
		msg.append("\n");

		StringBuilder builder = new StringBuilder();
		int errorCount = 0;

		for (SqlType type : list) {
			builder.append(type.name);
			int spaceLength = maxIdLength - type.name.length();
			if (spaceLength < 4) {
				spaceLength = 4;
			}

			for (String db : dbList) {
				for (int i = 0; i < spaceLength; i++) {
					builder.append(" ");
				}

				String alias = type.getAlias(db);

				if (alias != null) {
					builder.append(type.getAlias(db));
					spaceLength = maxIdLength - type.getAlias(db).length();
					if (spaceLength < 4) {
						spaceLength = 4;
					}

				} else {
					if (type.isUnsupported(db)) {
						builder.append("□□□□□□");
					} else {
						builder.append("■■■■■■");
						errorCount++;
					}

					spaceLength = maxIdLength - "□□□□□□".length();
					if (spaceLength < 4) {
						spaceLength = 4;
					}
				}
			}

			builder.append("\r\n");
		}

		String allColumn = builder.toString();
		msg.append(allColumn + "\n");

		int errorCount2 = 0;
		int errorCount3 = 0;

		for (String db : dbList) {
			msg.append("-- for " + db + "\n");
			msg.append("CREATE TABLE TYPE_TEST (\n");

			int count = 0;

			for (SqlType type : list) {
				String alias = type.getAlias(db);
				if (alias == null) {
					continue;
				}

				if (count != 0) {
					msg.append(",\n");
				}
				msg.append("\tCOL_" + count + " ");

				if (type.isNeedLength(db) && type.isNeedDecimal(db)) {
					TypeData typeData = new TypeData(new Integer(1),
							new Integer(1), false, null, false, null);

					str = Format.formatType(type, typeData, db);
					if (str.equals(alias)) {
						errorCount3++;
						msg.append("×3");
					}

				} else if (type.isNeedLength(db)) {
					TypeData typeData = new TypeData(new Integer(1), null,
							false, null, false, null);

					str = Format.formatType(type, typeData, db);

					if (str.equals(alias)) {
						errorCount3++;
						msg.append("×3");
					}

				} else if (type.isNeedDecimal(db)) {
					TypeData typeData = new TypeData(null, new Integer(1),
							false, null, false, null);

					str = Format.formatType(type, typeData, db);

					if (str.equals(alias)) {
						errorCount3++;
						msg.append("×3");
					}
				} else if (type.doesNeedArgs()) {
					str = alias + "('1')";

				} else {
					str = alias;
				}

				if (str != null) {

					Matcher m1 = NEED_LENGTH_PATTERN.matcher(str);
					Matcher m2 = NEED_DECIMAL_PATTERN1.matcher(str);
					Matcher m3 = NEED_DECIMAL_PATTERN2.matcher(str);

					if (m1.matches() || m2.matches() || m3.matches()) {
						errorCount2++;
						msg.append("×2");
					}
				}

				msg.append(str);

				count++;
			}
			msg.append("\n");
			msg.append(");\n");
			msg.append("\n");
		}

		msg.append("\n");
		msg.append(errorCount + " 個の型が変換できませんでした。\n");
		msg.append(errorCount2 + " 個の数字型の指定が不足しています。\n");
		msg.append(errorCount3 + " 個の数字型の指定が余分です。\n");

		logger.info(msg.toString());
	}
}
