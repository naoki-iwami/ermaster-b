package org.insightech.er.editor.model.dbexport.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.insightech.er.ResourceString;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.settings.export.ExportJavaSetting;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;
import org.insightech.er.util.io.FileUtils;
import org.insightech.er.util.io.IOUtils;

public class ExportToJavaManager {

	private static final String TEMPLATE_DIR = "java" + File.separator;

	private static final String[] KEYWORDS = { "java.template.constructor",
			"java.template.getter.description",
			"java.template.set.adder.description",
			"java.template.set.getter.description",
			"java.template.set.property.description",
			"java.template.set.setter.description",
			"java.template.setter.description", };

	private static final String TEMPLATE;

	private static final String IMPLEMENTS;

	private static final String PROPERTIES;

	private static final String SET_PROPERTIES;

	private static final String SETTER_GETTER;

	private static final String SETTER_GETTER_ADDER;

	private static final String HASHCODE_EQUALS;

	private static final String HASHCODE_LOGIC;

	private static final String EQUALS_LOGIC;

	private static final String EXTENDS;

	private static final String HIBERNATE_TEMPLATE;

	private static final String HIBERNATE_PROPERTY;

	private static final String HIBERNATE_ID;

	private static final String HIBERNATE_COMPOSITE_ID;

	private static final String HIBERNATE_COMPOSITE_ID_KEY;

	static {
		try {
			TEMPLATE = loadResource("template");
			IMPLEMENTS = loadResource("@implements");
			PROPERTIES = loadResource("@properties");
			SET_PROPERTIES = loadResource("@set_properties");
			SETTER_GETTER = loadResource("@setter_getter");
			SETTER_GETTER_ADDER = loadResource("@setter_getter_adder");
			HASHCODE_EQUALS = loadResource("@hashCode_equals");
			HASHCODE_LOGIC = loadResource("@hashCode logic");
			EQUALS_LOGIC = loadResource("@equals logic");
			EXTENDS = loadResource("@extends");

			HIBERNATE_TEMPLATE = loadResource("hibernate" + File.separator
					+ "hbm");
			HIBERNATE_PROPERTY = loadResource("hibernate" + File.separator
					+ "@property");
			HIBERNATE_ID = loadResource("hibernate" + File.separator + "@id");
			HIBERNATE_COMPOSITE_ID = loadResource("hibernate" + File.separator
					+ "@composite_id");
			HIBERNATE_COMPOSITE_ID_KEY = loadResource("hibernate"
					+ File.separator + "@composite_id_key");

		} catch (IOException e) {
			e.printStackTrace();
			throw new ExceptionInInitializerError(e);
		}
	}

	private ExportJavaSetting exportJavaSetting;

	protected ERDiagram diagram;

	private String packageDir;

	private Set<String> importClasseNames;

	private Set<String> sets;

	public ExportToJavaManager(ExportJavaSetting exportJavaSetting,
			ERDiagram diagram) {
		this.packageDir = exportJavaSetting.getPackageName().replaceAll("\\.",
				"\\/");

		this.exportJavaSetting = exportJavaSetting;
		this.diagram = diagram;

		this.importClasseNames = new TreeSet<String>();
		this.sets = new TreeSet<String>();
	}

	protected void doPreTask(ERTable table) {
	}

	protected void doPostTask() throws InterruptedException {
	}

	public void doProcess() throws IOException, InterruptedException {
		for (ERTable table : diagram.getDiagramContents().getContents()
				.getTableSet().getList()) {
			this.doPreTask(table);

			String className = this.getClassName(table);
			String compositeIdClassName = null;

			if (this.exportJavaSetting.isWithHibernate()) {
				if (table.getPrimaryKeySize() > 1) {
					compositeIdClassName = this.getCamelCaseName(table) + "Id";

					String compositeIdContent = this
							.generateCompositeIdContent(diagram, table,
									compositeIdClassName);

					this.writeOut(File.separator + this.packageDir
							+ File.separator + compositeIdClassName + ".java",
							compositeIdContent);
				}

				String hbmContent = this.generateHbmContent(diagram, table,
						compositeIdClassName);

				this.writeOut(File.separator + this.packageDir + File.separator
						+ className + ".hbm.xml", hbmContent);
			}

			String content = this.generateContent(diagram, table,
					compositeIdClassName);

			this.writeOut(File.separator + this.packageDir + File.separator
					+ className + ".java", content);

		}
	}

	protected String getClassName(ERTable table) {
		return this.getCamelCaseName(table)
				+ this.getCamelCaseName(this.exportJavaSetting
						.getClassNameSuffix(), true);
	}

	protected String getCamelCaseName(ERTable table) {
		return this.getCamelCaseName(table.getPhysicalName(), true);
	}

	protected String getCamelCaseName(String name, boolean capital) {
		String className = name.toLowerCase();

		if (capital && className.length() > 0) {
			String first = className.substring(0, 1);
			String other = className.substring(1);

			className = first.toUpperCase() + other;
		}

		while (className.indexOf("_") == 0) {
			className = className.substring(1);
		}

		int index = className.indexOf("_");

		while (index != -1) {
			String before = className.substring(0, index);
			if (className.length() == index + 1) {
				className = before;
				break;
			}

			String target = className.substring(index + 1, index + 2);

			String after = null;

			if (className.length() == index + 1) {
				after = "";

			} else {
				after = className.substring(index + 2);
			}

			className = before + target.toUpperCase() + after;

			index = className.indexOf("_");
		}

		return className;
	}

	private static String loadResource(String templateName) throws IOException {
		String resourceName = TEMPLATE_DIR + templateName + ".txt";

		InputStream in = ExportToJavaManager.class.getClassLoader()
				.getResourceAsStream(resourceName);

		if (in == null) {
			throw new FileNotFoundException(resourceName);
		}

		try {
			String content = IOUtils.toString(in);

			for (String keyword : KEYWORDS) {
				content = content.replaceAll(keyword, ResourceString
						.getResourceString(keyword));
			}

			return content;

		} finally {
			in.close();
		}
	}

	private String generateContent(ERDiagram diagram, ERTable table,
			String compositeIdClassName) throws IOException {
		this.importClasseNames.clear();
		this.importClasseNames.add("java.io.Serializable");
		this.sets.clear();

		String content = TEMPLATE;
		content = content.replace("@implements", IMPLEMENTS);

		content = this.replacePropertiesInfo(content, table,
				compositeIdClassName);
		content = this.replaceHashCodeEqualsInfo(content, table,
				compositeIdClassName);

		String classDescription = ResourceString.getResourceString(
				"java.template.class.description").replaceAll(
				"@LogicalTableName", table.getLogicalName());

		content = this.replaceClassInfo(content, classDescription, this
				.getCamelCaseName(table), this.exportJavaSetting
				.getClassNameSuffix());
		content = this.replaceExtendInfo(content);
		content = this.replaceImportInfo(content);
		content = this.replaceConstructorInfo(content);

		return content;
	}

	private String generateCompositeIdContent(ERDiagram diagram, ERTable table,
			String compositeIdClassName) throws IOException {
		this.importClasseNames.clear();
		this.importClasseNames.add("java.io.Serializable");
		this.sets.clear();

		String content = TEMPLATE;
		content = content.replace("@implements", IMPLEMENTS);

		content = this.replacePropertiesInfo(content, null, table
				.getPrimaryKeys(), null, null);
		content = this.replaceHashCodeEqualsInfo(content, table, null);

		String classDescription = ResourceString.getResourceString(
				"java.template.composite.id.class.description").replaceAll(
				"@LogicalTableName", table.getLogicalName());

		content = this.replaceClassInfo(content, classDescription,
				compositeIdClassName, "");
		content = this.replaceExtendInfo(content);
		content = this.replaceImportInfo(content);
		content = this.replaceConstructorInfo(content);

		return content;
	}

	private String replacePropertiesInfo(String content, ERTable table,
			String compositeIdClassName) throws IOException {
		return replacePropertiesInfo(content, table,
				table.getExpandedColumns(), table.getReferringElementList(),
				compositeIdClassName);
	}

	private String replacePropertiesInfo(String content, ERTable table,
			List<NormalColumn> columns, List<NodeElement> referringElementList,
			String compositeIdClassName) throws IOException {
		StringBuilder properties = new StringBuilder();
		StringBuilder setterGetters = new StringBuilder();

		if (compositeIdClassName != null) {
			this.addCompositeIdContent(properties, PROPERTIES,
					compositeIdClassName, table);
			this.addCompositeIdContent(setterGetters, SETTER_GETTER,
					compositeIdClassName, table);
		}

		for (NormalColumn normalColumn : columns) {
			if (compositeIdClassName == null || !normalColumn.isPrimaryKey()
					|| normalColumn.isForeignKey()) {
				this.addContent(properties, PROPERTIES, normalColumn);
				this.addContent(setterGetters, SETTER_GETTER, normalColumn);
			}
		}

		if (referringElementList != null) {
			for (NodeElement referringElement : referringElementList) {
				if (referringElement instanceof TableView) {
					TableView tableView = (TableView) referringElement;

					this.addContent(properties, SET_PROPERTIES, tableView);
					this.addContent(setterGetters, SETTER_GETTER_ADDER,
							tableView);

					this.sets.add(tableView.getPhysicalName());
				}
			}
		}

		content = content.replaceAll("@properties\r\n", properties.toString());
		content = content.replaceAll("@setter_getter\r\n", setterGetters
				.toString());

		return content;
	}

	private String replaceHashCodeEqualsInfo(String content, ERTable table,
			String compositeIdClassName) throws IOException {
		if (compositeIdClassName != null) {
			StringBuilder hashCodes = new StringBuilder();
			StringBuilder equals = new StringBuilder();

			this.addCompositeIdContent(hashCodes, HASHCODE_LOGIC,
					compositeIdClassName, table);
			this.addCompositeIdContent(equals, EQUALS_LOGIC,
					compositeIdClassName, table);

			String hashCodeEquals = HASHCODE_EQUALS;
			hashCodeEquals = hashCodeEquals.replaceAll("@hashCode logic\r\n",
					hashCodes.toString());
			hashCodeEquals = hashCodeEquals.replaceAll("@equals logic\r\n",
					equals.toString());

			content = content.replaceAll("@hashCode_equals\r\n", hashCodeEquals
					.toString());

		} else if (table.getPrimaryKeySize() > 0) {
			StringBuilder hashCodes = new StringBuilder();
			StringBuilder equals = new StringBuilder();

			for (NormalColumn primaryKey : table.getPrimaryKeys()) {
				this.addContent(hashCodes, HASHCODE_LOGIC, primaryKey);
				this.addContent(equals, EQUALS_LOGIC, primaryKey);
			}

			String hashCodeEquals = HASHCODE_EQUALS;
			hashCodeEquals = hashCodeEquals.replaceAll("@hashCode logic\r\n",
					hashCodes.toString());
			hashCodeEquals = hashCodeEquals.replaceAll("@equals logic\r\n",
					equals.toString());

			content = content.replaceAll("@hashCode_equals\r\n", hashCodeEquals
					.toString());

		} else {
			content = content.replaceAll("@hashCode_equals\r\n", "");
		}

		return content;
	}

	private String replaceClassInfo(String content, String classDescription,
			String className, String classNameSufix) {
		if (Check.isEmptyTrim(this.exportJavaSetting.getPackageName())) {
			content = content.replaceAll("package @package;\r\n\r\n", "");

		} else {
			content = content.replaceAll("@package", this.exportJavaSetting
					.getPackageName());
		}

		content = content.replaceAll("@classDescription", classDescription);
		content = content.replaceAll("@PhysicalTableName", className);
		content = content.replaceAll("@suffix", this.getCamelCaseName(
				classNameSufix, true));
		content = content.replaceAll("@version", "@version \\$Id\\$");

		return content;
	}

	private String replaceExtendInfo(String content) throws IOException {
		if (Check.isEmpty(this.exportJavaSetting.getExtendsClass())) {
			content = content.replaceAll("@import extends\r\n", "");
			content = content.replaceAll("@extends ", "");

		} else {
			this.importClasseNames
					.add(this.exportJavaSetting.getExtendsClass());

			content = content.replaceAll("@extends", EXTENDS);

			int index = this.exportJavaSetting.getExtendsClass().lastIndexOf(
					".");

			String extendsClassWithoutPackage = null;

			if (index == -1) {
				extendsClassWithoutPackage = this.exportJavaSetting
						.getExtendsClass();

			} else {
				extendsClassWithoutPackage = this.exportJavaSetting
						.getExtendsClass().substring(index + 1);
			}

			content = content.replaceAll("@extendsClassWithoutPackage",
					extendsClassWithoutPackage);
			content = content.replaceAll("@extendsClass",
					this.exportJavaSetting.getExtendsClass());
		}

		return content;
	}

	private String replaceImportInfo(String content) {
		StringBuilder imports = new StringBuilder();
		for (String importClasseName : this.importClasseNames) {
			imports.append("import ");
			imports.append(importClasseName);
			imports.append(";\r\n");
		}

		content = content.replaceAll("@import\r\n", imports.toString());

		return content;
	}

	private String replaceConstructorInfo(String content) {
		StringBuilder constructor = new StringBuilder();
		for (String tableName : this.sets) {
			constructor.append("\t\tthis.");
			constructor.append(this.getCamelCaseName(tableName, false));
			constructor.append("Set = new HashSet<");
			constructor.append(this.getCamelCaseName(tableName, true)
					+ this.getCamelCaseName(this.exportJavaSetting
							.getClassNameSuffix(), true));
			constructor.append(">();\r\n");
		}

		content = content
				.replaceAll("@constructor\r\n", constructor.toString());

		return content;
	}

	private void addContent(StringBuilder contents, String template,
			NormalColumn normalColumn) {

		String value = null;

		if (normalColumn.isForeignKey()) {
			NormalColumn referencedColumn = normalColumn
					.getRootReferencedColumn();

			ERTable referencedTable = (ERTable) referencedColumn
					.getColumnHolder();
			String className = this.getClassName(referencedTable);

			value = template.replaceAll("@type", className);
			value = value.replaceAll("@logicalColumnName", referencedTable
					.getName());

			String physicalName = normalColumn.getPhysicalName().toLowerCase();
			physicalName = physicalName.replaceAll(referencedColumn
					.getPhysicalName().toLowerCase(), "");
			if (physicalName.indexOf(referencedTable.getPhysicalName()
					.toLowerCase()) == -1) {
				physicalName = physicalName + referencedTable.getPhysicalName();
			}

			value = value.replaceAll("@physicalColumnName", this
					.getCamelCaseName(physicalName, false));
			value = value.replaceAll("@PhysicalColumnName", this
					.getCamelCaseName(physicalName, true));

		} else {
			value = template.replaceAll("@type", this.getClassName(normalColumn
					.getType()));
			value = value.replaceAll("@logicalColumnName", normalColumn
					.getLogicalName());
			value = value.replaceAll("@physicalColumnName", this
					.getCamelCaseName(normalColumn.getPhysicalName(), false));
			value = value.replaceAll("@PhysicalColumnName", this
					.getCamelCaseName(normalColumn.getPhysicalName(), true));

		}

		contents.append(value);
		contents.append("\r\n");
	}

	private void addContent(StringBuilder contents, String template,
			TableView tableView) {

		String value = template;

		this.importClasseNames.add("java.util.Set");
		this.importClasseNames.add("java.util.HashSet");

		value = value.replaceAll("@setType", "Set<"
				+ this.getCamelCaseName(tableView.getPhysicalName(), true)
				+ this.getCamelCaseName(this.exportJavaSetting
						.getClassNameSuffix(), true) + ">");
		value = value.replaceAll("@type", this.getCamelCaseName(tableView
				.getPhysicalName(), true)
				+ this.getCamelCaseName(this.exportJavaSetting
						.getClassNameSuffix(), true));
		value = value.replaceAll("@logicalColumnName", tableView.getName());

		value = value.replaceAll("@physicalColumnName", this.getCamelCaseName(
				tableView.getPhysicalName(), false));
		value = value.replaceAll("@PhysicalColumnName", this.getCamelCaseName(
				tableView.getPhysicalName(), true));

		contents.append(value);
		contents.append("\r\n");
	}

	private void addCompositeIdContent(StringBuilder contents, String template,
			String compositeIdClassName, ERTable table) {

		String compositeIdPropertyName = compositeIdClassName.substring(0, 1)
				.toLowerCase()
				+ compositeIdClassName.substring(1);

		String propertyDescription = ResourceString.getResourceString(
				"java.template.composite.id.property.description").replaceAll(
				"@LogicalTableName", table.getLogicalName());

		String value = template;

		value = value.replaceAll("@type", compositeIdClassName);
		value = value.replaceAll("@logicalColumnName", propertyDescription);

		value = value
				.replaceAll("@physicalColumnName", compositeIdPropertyName);
		value = value.replaceAll("@PhysicalColumnName", compositeIdClassName);

		contents.append(value);
		contents.append("\r\n");
	}

	private String getClassName(SqlType type) {
		if (type == null) {
			return "";
		}
		Class clazz = type.getJavaClass();

		String name = clazz.getCanonicalName();
		if (!name.startsWith("java.lang")) {
			this.importClasseNames.add(name);
		}

		return clazz.getSimpleName();
	}

	private String getFullClassName(SqlType type) {
		if (type == null) {
			return "";
		}
		Class clazz = type.getJavaClass();

		String name = clazz.getCanonicalName();

		return name;
	}

	private void writeOut(String dstPath, String content) throws IOException {
		dstPath = this.exportJavaSetting.getJavaOutput() + File.separator
				+ "src" + dstPath;
		File file = new File(dstPath);
		file.getParentFile().mkdirs();

		FileUtils.writeStringToFile(file, content, this.exportJavaSetting
				.getSrcFileEncoding());
	}

	private String generateHbmContent(ERDiagram diagram, ERTable table,
			String compositeIdClassName) throws IOException {
		String content = HIBERNATE_TEMPLATE;

		content = content.replaceAll("@package", this.exportJavaSetting
				.getPackageName());
		content = content.replaceAll("@PhysicalTableName", this
				.getCamelCaseName(table));
		content = content.replaceAll("@suffix", Format
				.null2blank(this.exportJavaSetting.getClassNameSuffix()));
		content = content.replaceAll("@realTableName", table.getPhysicalName());

		StringBuilder properties = new StringBuilder();

		if (table.getPrimaryKeySize() == 1) {
			for (NormalColumn column : table.getPrimaryKeys()) {
				String property = HIBERNATE_ID;
				property = property.replaceAll("@physicalColumnName", this
						.getCamelCaseName(column.getPhysicalName(), false));
				property = property.replaceAll("@realColumnName", column
						.getPhysicalName());
				property = property.replaceAll("@type", this
						.getFullClassName(column.getType()));
				property = property.replaceAll("@generator", "assigned");

				properties.append(property);
			}

		} else if (table.getPrimaryKeySize() > 1) {
			String property = HIBERNATE_COMPOSITE_ID;

			StringBuilder keys = new StringBuilder();

			for (NormalColumn column : table.getPrimaryKeys()) {
				String key = HIBERNATE_COMPOSITE_ID_KEY;
				key = key.replaceAll("@physicalColumnName", this
						.getCamelCaseName(column.getPhysicalName(), false));
				key = key.replaceAll("@realColumnName", column
						.getPhysicalName());
				key = key.replaceAll("@type", this.getFullClassName(column
						.getType()));

				keys.append(key);
			}

			String compositeIdPropertyName = compositeIdClassName.substring(0,
					1).toLowerCase()
					+ compositeIdClassName.substring(1);

			property = property.replaceAll("@compositeIdPropertyName",
					compositeIdPropertyName);
			property = property.replaceAll("@compositeIdClassName",
					compositeIdClassName);
			property = property.replaceAll("@key_properties", keys.toString());

			properties.append(property);
		}

		for (NormalColumn column : table.getExpandedColumns()) {
			if (!column.isPrimaryKey()) {
				String property = HIBERNATE_PROPERTY;
				property = property.replaceAll("@physicalColumnName", this
						.getCamelCaseName(column.getPhysicalName(), false));
				property = property.replaceAll("@realColumnName", column
						.getPhysicalName());
				property = property.replaceAll("@type", this
						.getFullClassName(column.getType()));
				property = property.replaceAll("@not-null", String
						.valueOf(column.isNotNull()));

				properties.append(property);
			}
		}

		content = content.replaceAll("@properties\r\n", properties.toString());

		return content;
	}
}
