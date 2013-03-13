package org.insightech.er.editor.model.diagram_contents.not_element.group;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.insightech.er.Activator;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;

public class GlobalGroupSet {

	private static final String COLUMN_GOURP_SETTINGS_FILENAME = "column_group.xml"; //$NON-NLS-1$

	public static GroupSet load() {
		GroupSet columnGroups = new GroupSet();

		try {
			IDialogSettings settings = new DialogSettings("column_group_list");
			String database = settings.get("database");
			if (database == null) {
				database = DBManagerFactory.getAllDBList().get(0);
			}
			columnGroups.setDatabase(database);

			String path = getPath();
			File columnGroupListFile = new File(path);

			if (columnGroupListFile.exists()) {
				settings.load(path);

				for (IDialogSettings columnGroupSection : settings
						.getSections()) {
					ColumnGroup columnGroup = new ColumnGroup();

					columnGroup.setGroupName(columnGroupSection
							.get("group_name"));

					for (IDialogSettings columnSection : columnGroupSection
							.getSections()) {
						String physicalName = columnSection
								.get("physical_name");
						String logicalName = columnSection.get("logical_name");
						SqlType sqlType = SqlType.valueOfId(columnSection
								.get("type"));
						String defaultValue = columnSection
								.get("default_value");
						String description = columnSection.get("description");
						String constraint = columnSection.get("constraint");
						boolean notNull = Boolean.valueOf(
								columnSection.get("not_null")).booleanValue();
						boolean unique = Boolean.valueOf(
								columnSection.get("unique")).booleanValue();
						Integer length = toInteger(columnSection.get("length"));
						Integer decimal = toInteger(columnSection
								.get("decimal"));
						boolean array = Boolean.valueOf(
								columnSection.get("array")).booleanValue();
						Integer arrayDimension = toInteger(columnSection
								.get("array_dimension"));
						boolean unsigned = Boolean.valueOf(
								columnSection.get("unsigned")).booleanValue();
						String args = columnSection.get("args");

						TypeData typeData = new TypeData(length, decimal,
								array, arrayDimension, unsigned, args);

						Word word = new Word(physicalName, logicalName,
								sqlType, typeData, description, database);

						NormalColumn column = new NormalColumn(word, notNull,
								false, unique, false, defaultValue, constraint,
								null, null, null);

						columnGroup.addColumn(column);
					}

					columnGroups.add(columnGroup);
				}
			}
		} catch (IOException e) {
			Activator.showExceptionDialog(e);
		}

		return columnGroups;
	}

	public static void save(GroupSet columnGroups) {
		try {
			IDialogSettings settings = new DialogSettings("column_group_list");

			settings.put("database", columnGroups.getDatabase());

			int index = 0;

			for (ColumnGroup columnGroup : columnGroups) {
				IDialogSettings columnGroupSection = new DialogSettings(
						"column_group_" + index);
				index++;

				columnGroupSection
						.put("group_name", columnGroup.getGroupName());

				int columnIndex = 0;

				for (NormalColumn normalColumn : columnGroup.getColumns()) {
					IDialogSettings columnSection = new DialogSettings(
							"column_" + columnIndex);
					columnIndex++;

					columnSection.put("physical_name", null2Blank(normalColumn
							.getPhysicalName()));
					columnSection.put("logical_name", null2Blank(normalColumn
							.getLogicalName()));
					columnSection.put("type",
							null2Blank(normalColumn.getType()));
					columnSection.put("length", null2Blank(normalColumn
							.getTypeData().getLength()));
					columnSection.put("decimal", null2Blank(normalColumn
							.getTypeData().getDecimal()));
					columnSection.put("array", normalColumn.getTypeData()
							.isArray());
					columnSection.put("array_dimension",
							null2Blank(normalColumn.getTypeData()
									.getArrayDimension()));
					columnSection.put("unsigned", normalColumn.getTypeData()
							.isUnsigned());

					columnSection.put("not_null", normalColumn.isNotNull());
					columnSection.put("unique", normalColumn.isUniqueKey());
					columnSection.put("default_value", null2Blank(normalColumn
							.getDefaultValue()));
					columnSection.put("constraint", null2Blank(normalColumn
							.getConstraint()));
					columnSection.put("description", null2Blank(normalColumn
							.getDescription()));

					columnGroupSection.addSection(columnSection);
				}

				settings.addSection(columnGroupSection);
			}

			settings.save(getPath());

		} catch (IOException e) {
			Activator.showExceptionDialog(e);
		}
	}

	private static String getPath() {
		IPath dataLocation = Activator.getDefault().getStateLocation();
		String path = dataLocation.append(COLUMN_GOURP_SETTINGS_FILENAME)
				.toOSString();
		return path;
	}

	private static String null2Blank(String str) {
		if (str == null) {
			return "";
		}

		return str;
	}

	private static String null2Blank(Object object) {
		if (object == null) {
			return "";
		}

		return object.toString();
	}

	private static String null2Blank(SqlType sqlType) {
		if (sqlType == null) {
			return "";
		}

		return sqlType.getId();
	}

	private static Integer toInteger(String str) {
		if (str == null || str.equals("")) {
			return null;
		}

		try {
			return Integer.valueOf(str);
		} catch (NumberFormatException e) {
		}

		return null;
	}
}
