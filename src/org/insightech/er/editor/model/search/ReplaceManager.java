package org.insightech.er.editor.model.search;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.util.Check;
import org.insightech.er.util.NameValue;

public class ReplaceManager {

	private static final int[] ALPHABET_TYPES = new int[] {
			SearchResultRow.TYPE_RELATION_NAME,
			SearchResultRow.TYPE_INDEX_NAME,
			SearchResultRow.TYPE_TABLE_PHYSICAL_NAME,
			SearchResultRow.TYPE_WORD_PHYSICAL_NAME,
			SearchResultRow.TYPE_COLUMN_PHYSICAL_NAME,
			SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_PHYSICAL_NAME };

	private static final int[] DEGIT_TYPES = new int[] {
			SearchResultRow.TYPE_WORD_LENGTH,
			SearchResultRow.TYPE_WORD_DECIMAL,
			SearchResultRow.TYPE_COLUMN_LENGTH,
			SearchResultRow.TYPE_COLUMN_DECIMAL,
			SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_LENGTH,
			SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_DECIMAL };

	private static final int[] REQUIRED_TYPES = new int[] {
			SearchResultRow.TYPE_INDEX_NAME,
			SearchResultRow.TYPE_TABLE_LOGICAL_NAME,
			SearchResultRow.TYPE_WORD_LOGICAL_NAME,
			SearchResultRow.TYPE_COLUMN_LOGICAL_NAME,
			SearchResultRow.TYPE_COLUMN_GROUP_NAME,
			SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_LOGICAL_NAME };

	private static final int[] EXCLUDE_TYPES = new int[] {
			SearchResultRow.TYPE_INDEX_COLUMN_NAME,
			SearchResultRow.TYPE_WORD_TYPE, SearchResultRow.TYPE_COLUMN_TYPE,
			SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_TYPE };

	private static final List<String> replaceWordList = new ArrayList<String>();

	public static ReplaceResult replace(int type, Object object,
			String keyword, String replaceWord, String database) {

		addReplaceWord(replaceWord);

		for (int excludeType : EXCLUDE_TYPES) {
			if (type == excludeType) {
				return null;
			}
		}

		checkAlphabet(type, replaceWord);
		checkDegit(type, replaceWord);

		if (type == SearchResultRow.TYPE_RELATION_NAME) {
			Relation relation = (Relation) object;
			String original = relation.getName();

			String str = replace(original, keyword, replaceWord);

			if (!checkRequired(type, str)) {
				return null;
			}

			relation.setName(str);

			return new ReplaceResult(original);

		} else if (type == SearchResultRow.TYPE_INDEX_NAME) {
			Index index = (Index) object;
			String original = index.getName();

			String str = replace(original, keyword, replaceWord);

			if (!checkRequired(type, str)) {
				return null;
			}

			index.setName(str);

			return new ReplaceResult(original);

		} else if (type == SearchResultRow.TYPE_INDEX_COLUMN_NAME) {

			return null;

		} else if (type == SearchResultRow.TYPE_NOTE) {
			Note note = (Note) object;
			String original = note.getText();

			String str = replace(original, keyword, replaceWord);

			if (!checkRequired(type, str)) {
				return null;
			}

			note.setText(str);

			return new ReplaceResult(original);

		} else if (type == SearchResultRow.TYPE_MODEL_PROPERTY_NAME) {
			NameValue property = (NameValue) object;
			String original = property.getName();

			String str = replace(original, keyword, replaceWord);

			if (!checkRequired(type, str)) {
				return null;
			}

			property.setName(str);

			return new ReplaceResult(original);

		} else if (type == SearchResultRow.TYPE_MODEL_PROPERTY_VALUE) {
			NameValue property = (NameValue) object;
			String original = property.getValue();

			String str = replace(original, keyword, replaceWord);

			if (!checkRequired(type, str)) {
				return null;
			}

			property.setValue(str);

			return new ReplaceResult(original);

		} else if (type == SearchResultRow.TYPE_TABLE_PHYSICAL_NAME) {
			ERTable table = (ERTable) object;
			String original = table.getPhysicalName();

			String str = replace(original, keyword, replaceWord);

			if (!checkRequired(type, str)) {
				return null;
			}

			table.setPhysicalName(str);

			return new ReplaceResult(original);

		} else if (type == SearchResultRow.TYPE_TABLE_LOGICAL_NAME) {
			ERTable table = (ERTable) object;
			String original = table.getLogicalName();

			String str = replace(original, keyword, replaceWord);

			if (!checkRequired(type, str)) {
				return null;
			}

			table.setLogicalName(str);

			return new ReplaceResult(original);

		} else if (type == SearchResultRow.TYPE_COLUMN_PHYSICAL_NAME
				|| type == SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_PHYSICAL_NAME) {
			NormalColumn column = (NormalColumn) object;
			String original = column.getForeignKeyPhysicalName();

			String str = replace(original, keyword, replaceWord);

			if (!checkRequired(type, str)) {
				return null;
			}

			column.setForeignKeyPhysicalName(str);

			return new ReplaceResult(original);

		} else if (type == SearchResultRow.TYPE_COLUMN_LOGICAL_NAME
				|| type == SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_LOGICAL_NAME) {
			NormalColumn column = (NormalColumn) object;
			String original = column.getForeignKeyLogicalName();

			String str = replace(original, keyword, replaceWord);

			checkRequired(type, str);

			column.setForeignKeyLogicalName(str);

			return new ReplaceResult(original);

		} else if (type == SearchResultRow.TYPE_COLUMN_DEFAULT_VALUE
				|| type == SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_DEFAULT_VALUE) {

			NormalColumn column = (NormalColumn) object;
			String original = column.getDefaultValue();

			String str = replace(original, keyword, replaceWord);

			if (!checkRequired(type, str)) {
				return null;
			}

			column.setDefaultValue(str);

			return new ReplaceResult(original);

		} else if (type == SearchResultRow.TYPE_COLUMN_COMMENT) {
			NormalColumn column = (NormalColumn) object;
			String original = column.getForeignKeyDescription();

			String str = replace(original, keyword, replaceWord);

			if (!checkRequired(type, str)) {
				return null;
			}

			column.setForeignKeyDescription(str);

			return new ReplaceResult(original);

		} else if (type == SearchResultRow.TYPE_COLUMN_GROUP_NAME
				|| type == SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_COMMENT) {
			ColumnGroup group = (ColumnGroup) object;
			String original = group.getGroupName();

			String str = replace(original, keyword, replaceWord);

			if (!checkRequired(type, str)) {
				return null;
			}

			group.setGroupName(str);

			return new ReplaceResult(original);
		} else if (type == SearchResultRow.TYPE_WORD_PHYSICAL_NAME) {
			Word word = (Word) object;
			String original = word.getPhysicalName();

			String str = replace(original, keyword, replaceWord);

			if (!checkRequired(type, str)) {
				return null;
			}

			word.setPhysicalName(str);

			return new ReplaceResult(original);

		} else if (type == SearchResultRow.TYPE_WORD_LOGICAL_NAME) {
			Word word = (Word) object;
			String original = word.getLogicalName();

			String str = replace(original, keyword, replaceWord);

			checkRequired(type, str);

			word.setLogicalName(str);

			return new ReplaceResult(original);

		} else if (type == SearchResultRow.TYPE_WORD_LENGTH) {
			Word word = (Word) object;
			String original = String.valueOf(word.getTypeData().getLength());

			String str = replace(original, keyword, replaceWord);

			if (!checkRequired(type, str)) {
				return null;
			}

			if (!str.equals("")) {
				TypeData oldTypeData = word.getTypeData();
				TypeData newTypeData = new TypeData(Integer.parseInt(str),
						oldTypeData.getDecimal(), oldTypeData.isArray(),
						oldTypeData.getArrayDimension(), oldTypeData
								.isUnsigned(), oldTypeData.getArgs());

				word.setType(word.getType(), newTypeData, database);
			}

			return new ReplaceResult(original);

		} else if (type == SearchResultRow.TYPE_WORD_DECIMAL) {
			Word word = (Word) object;
			String original = String.valueOf(word.getTypeData().getDecimal());

			String str = replace(original, keyword, replaceWord);

			if (!checkRequired(type, str)) {
				return null;
			}

			if (!str.equals("")) {
				TypeData oldTypeData = word.getTypeData();
				TypeData newTypeData = new TypeData(oldTypeData.getLength(),
						Integer.parseInt(str), oldTypeData.isArray(),
						oldTypeData.getArrayDimension(), oldTypeData
								.isUnsigned(), oldTypeData.getArgs());

				word.setType(word.getType(), newTypeData, database);
			}

			return new ReplaceResult(original);

		} else if (type == SearchResultRow.TYPE_WORD_COMMENT) {
			Word word = (Word) object;
			String original = word.getDescription();

			String str = replace(original, keyword, replaceWord);

			if (!checkRequired(type, str)) {
				return null;
			}

			word.setDescription(str);

			return new ReplaceResult(original);
		}

		return null;
	}

	private static boolean checkAlphabet(int type, String str) {
		if (str == null || str.equals("")) {
			return true;
		}

		for (int alphabetType : ALPHABET_TYPES) {
			if (type == alphabetType) {
				if (!Check.isAlphabet(str)) {
					return false;
				}
			}
		}

		return true;
	}

	private static boolean checkDegit(int type, String str) {
		if (str == null || str.equals("")) {
			return true;
		}

		for (int degitType : DEGIT_TYPES) {
			if (type == degitType) {
				try {
					int len = Integer.parseInt(str);
					if (len < 0) {
						return false;
					}

				} catch (NumberFormatException e) {
					return false;
				}
			}
		}

		return true;
	}

	private static boolean checkRequired(int type, String str) {
		for (int requiredType : REQUIRED_TYPES) {
			if (type == requiredType) {
				if (str == null || str.trim().equals("")) {
					return false;
				}
			}
		}

		return true;
	}

	private static String replace(String str, String keyword, String replaceWord) {
		return str.replaceAll(keyword, replaceWord);
	}

	private static void addReplaceWord(String replaceWord) {
		if (!replaceWordList.contains(replaceWord)) {
			replaceWordList.add(0, replaceWord);
		}

		if (replaceWordList.size() > 20) {
			replaceWordList.remove(replaceWordList.size() - 1);
		}
	}

	public static List<String> getReplaceWordList() {
		return replaceWordList;
	}

	public static void undo(int type, Object object, String str) {

		if (type == SearchResultRow.TYPE_RELATION_NAME) {
			Relation relation = (Relation) object;

			relation.setName(str);

		} else if (type == SearchResultRow.TYPE_INDEX_NAME) {
			Index index = (Index) object;

			index.setName(str);

		} else if (type == SearchResultRow.TYPE_INDEX_COLUMN_NAME) {

		} else if (type == SearchResultRow.TYPE_NOTE) {
			Note note = (Note) object;

			note.setText(str);

		} else if (type == SearchResultRow.TYPE_MODEL_PROPERTY_NAME) {
			NameValue property = (NameValue) object;

			property.setName(str);

		} else if (type == SearchResultRow.TYPE_MODEL_PROPERTY_VALUE) {
			NameValue property = (NameValue) object;

			property.setValue(str);

		} else if (type == SearchResultRow.TYPE_TABLE_PHYSICAL_NAME) {
			ERTable table = (ERTable) object;

			table.setPhysicalName(str);

		} else if (type == SearchResultRow.TYPE_TABLE_LOGICAL_NAME) {
			ERTable table = (ERTable) object;

			table.setLogicalName(str);

		} else if (type == SearchResultRow.TYPE_COLUMN_PHYSICAL_NAME
				|| type == SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_PHYSICAL_NAME) {
			NormalColumn column = (NormalColumn) object;

			column.setForeignKeyPhysicalName(str);

		} else if (type == SearchResultRow.TYPE_COLUMN_LOGICAL_NAME
				|| type == SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_LOGICAL_NAME) {
			NormalColumn column = (NormalColumn) object;

			column.setForeignKeyLogicalName(str);

		} else if (type == SearchResultRow.TYPE_COLUMN_DEFAULT_VALUE
				|| type == SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_DEFAULT_VALUE) {
			NormalColumn column = (NormalColumn) object;

			column.setDefaultValue(str);

		} else if (type == SearchResultRow.TYPE_COLUMN_COMMENT) {
			NormalColumn column = (NormalColumn) object;

			column.setForeignKeyDescription(str);

		} else if (type == SearchResultRow.TYPE_COLUMN_GROUP_NAME
				|| type == SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_COMMENT) {
			ColumnGroup group = (ColumnGroup) object;

			group.setGroupName(str);

		}
	}
}
