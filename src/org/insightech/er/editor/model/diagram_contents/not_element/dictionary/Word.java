package org.insightech.er.editor.model.diagram_contents.not_element.dictionary;

import java.util.Comparator;

import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.util.Format;

public class Word extends AbstractModel implements ObjectModel,
		Comparable<Word> {

	private static final long serialVersionUID = 4315217440968295922L;

	private static final Comparator<Word> WITHOUT_NAME_COMPARATOR = new WordWithoutNameComparator();

	public static final Comparator<Word> PHYSICAL_NAME_COMPARATOR = new WordPhysicalNameComparator();

	public static final Comparator<Word> LOGICAL_NAME_COMPARATOR = new WordLogicalNameComparator();

	private String physicalName;

	private String logicalName;

	private SqlType type;

	private TypeData typeData;

	private String description;

	public Word(String physicalName, String logicalName, SqlType type,
			TypeData typeData, String description, String database) {
		this.physicalName = physicalName;
		this.logicalName = logicalName;
		this.setType(type, typeData, database);
		this.description = description;
	}

	public Word(Word word) {
		this.physicalName = word.physicalName;
		this.logicalName = word.logicalName;
		this.type = word.type;
		this.typeData = word.typeData.clone();
		this.description = word.description;
	}

	public String getLogicalName() {
		return logicalName;
	}

	public String getPhysicalName() {
		return physicalName;
	}

	public SqlType getType() {
		return type;
	}

	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}

	public void setPhysicalName(String physicalName) {
		this.physicalName = physicalName;
	}

	public void setType(SqlType type, TypeData typeData, String database) {
		this.type = type;
		this.typeData = typeData.clone();

		if (type != null && type.isNeedLength(database)) {
			if (this.typeData.getLength() == null) {
				this.typeData.setLength(0);
			}
		} else {
			this.typeData.setLength(null);
		}

		if (type != null && type.isNeedDecimal(database)) {
			if (this.typeData.getDecimal() == null) {
				this.typeData.setDecimal(0);
			}
		} else {
			this.typeData.setDecimal(null);
		}

	}

	public TypeData getTypeData() {
		return typeData;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void copyTo(Word to) {
		to.physicalName = this.physicalName;
		to.logicalName = this.logicalName;
		to.description = this.description;
		to.type = this.type;
		to.typeData = this.typeData.clone();
	}

	public int compareTo(Word o) {
		return PHYSICAL_NAME_COMPARATOR.compare(this, o);
	}

	public String getName() {
		return this.getLogicalName();
	}

	public String getObjectType() {
		return "word";
	}

	private static class WordWithoutNameComparator implements Comparator<Word> {

		public int compare(Word o1, Word o2) {
			if (o1 == o2) {
				return 0;
			}
			if (o2 == null) {
				return -1;
			}
			if (o1 == null) {
				return 1;
			}

			if (o1.type == null) {
				if (o2.type != null) {
					return 1;
				}
			} else {
				if (o2.type == null) {
					return -1;
				}
				int value = o1.type.getId().compareTo(o2.type.getId());
				if (value != 0) {
					return value;
				}
			}

			if (o1.typeData == null) {
				if (o2.typeData != null) {
					return 1;
				}
			} else {
				if (o2.typeData == null) {
					return -1;
				}
				int value = o1.typeData.compareTo(o2.typeData);
				if (value != 0) {
					return value;
				}
			}

			int value = Format.null2blank(o1.description).compareTo(
					Format.null2blank(o2.description));
			if (value != 0) {
				return value;
			}

			return 0;
		}

	}

	private static class WordPhysicalNameComparator implements Comparator<Word> {

		public int compare(Word o1, Word o2) {
			if (o1 == o2) {
				return 0;
			}
			if (o2 == null) {
				return -1;
			}
			if (o1 == null) {
				return 1;
			}

			int value = 0;

			value = Format.null2blank(o1.physicalName).toUpperCase().compareTo(
					Format.null2blank(o2.physicalName).toUpperCase());
			if (value != 0) {
				return value;
			}

			value = Format.null2blank(o1.logicalName).toUpperCase().compareTo(
					Format.null2blank(o2.logicalName).toUpperCase());
			if (value != 0) {
				return value;
			}

			return WITHOUT_NAME_COMPARATOR.compare(o1, o2);
		}

	}

	private static class WordLogicalNameComparator implements Comparator<Word> {

		public int compare(Word o1, Word o2) {
			if (o1 == o2) {
				return 0;
			}
			if (o2 == null) {
				return -1;
			}
			if (o1 == null) {
				return 1;
			}

			int value = 0;

			value = Format.null2blank(o1.logicalName).toUpperCase().compareTo(
					Format.null2blank(o2.logicalName).toUpperCase());
			if (value != 0) {
				return value;
			}

			value = Format.null2blank(o1.physicalName).toUpperCase().compareTo(
					Format.null2blank(o2.physicalName).toUpperCase());
			if (value != 0) {
				return value;
			}

			return WITHOUT_NAME_COMPARATOR.compare(o1, o2);
		}

	}

}
