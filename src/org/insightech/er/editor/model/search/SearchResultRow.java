package org.insightech.er.editor.model.search;

public class SearchResultRow {

	public static final int TYPE_RELATION_NAME = 1;

	public static final int TYPE_INDEX_NAME = 2;

	public static final int TYPE_INDEX_COLUMN_NAME = 3;

	public static final int TYPE_NOTE = 4;

	public static final int TYPE_MODEL_PROPERTY_NAME = 5;

	public static final int TYPE_MODEL_PROPERTY_VALUE = 6;

	// public static final int TYPE_PROJECT_NAME = 5;
	//
	// public static final int TYPE_MODEL_NAME = 6;
	//
	// public static final int TYPE_COMPANY = 7;
	//
	// public static final int TYPE_AUTHOR = 8;
	//
	// public static final int TYPE_VERSION = 9;

	public static final int TYPE_TABLE_PHYSICAL_NAME = 11;

	public static final int TYPE_TABLE_LOGICAL_NAME = 12;

	public static final int TYPE_COLUMN_PHYSICAL_NAME = 13;

	public static final int TYPE_COLUMN_LOGICAL_NAME = 14;

	public static final int TYPE_COLUMN_TYPE = 15;

	public static final int TYPE_COLUMN_LENGTH = 16;

	public static final int TYPE_COLUMN_DECIMAL = 17;

	public static final int TYPE_COLUMN_DEFAULT_VALUE = 18;

	public static final int TYPE_COLUMN_COMMENT = 19;

	public static final int TYPE_COLUMN_GROUP_NAME = 20;

	public static final int TYPE_COLUMN_GROUP_COLUMN_PHYSICAL_NAME = 21;

	public static final int TYPE_COLUMN_GROUP_COLUMN_LOGICAL_NAME = 22;

	public static final int TYPE_COLUMN_GROUP_COLUMN_TYPE = 23;

	public static final int TYPE_COLUMN_GROUP_COLUMN_LENGTH = 24;

	public static final int TYPE_COLUMN_GROUP_COLUMN_DECIMAL = 25;

	public static final int TYPE_COLUMN_GROUP_COLUMN_DEFAULT_VALUE = 26;

	public static final int TYPE_COLUMN_GROUP_COLUMN_COMMENT = 27;

	public static final int TYPE_WORD_PHYSICAL_NAME = 28;

	public static final int TYPE_WORD_LOGICAL_NAME = 29;

	public static final int TYPE_WORD_TYPE = 30;

	public static final int TYPE_WORD_LENGTH = 31;

	public static final int TYPE_WORD_DECIMAL = 32;

	public static final int TYPE_WORD_COMMENT = 33;

	private int type;

	private String text;

	private String path;

	private Object target;

	private Object targetNode;

	public SearchResultRow(int type, String text, String path, Object target, Object targetNode) {
		this.type = type;
		this.text = text;
		this.path = path;
		this.target = target;
		this.targetNode = targetNode;
	}

	public String getText() {
		return text;
	}

	public int getType() {
		return type;
	}

	public String getPath() {
		return path;
	}

	public Object getTarget() {
		return this.target;
	}

	/**
	 * targetNode ‚ðŽæ“¾‚µ‚Ü‚·.
	 *
	 * @return targetNode
	 */
	public Object getTargetNode() {
		return targetNode;
	}
}
