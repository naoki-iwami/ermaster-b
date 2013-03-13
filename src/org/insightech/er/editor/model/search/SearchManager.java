package org.insightech.er.editor.model.search;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.util.NameValue;

public class SearchManager {

	private static final int COLUMN_TYPE_NORMAL = 1;

	private static final int COLUMN_TYPE_GROUP = 2;

	private ERDiagram diagram;

	// 単語
	private boolean physicalWordNameCheckBox;

	private boolean logicalWordNameCheckBox;

	private boolean wordTypeCheckBox;

	private boolean wordLengthCheckBox;

	private boolean wordDecimalCheckBox;

	private boolean wordDescriptionCheckBox;

	// テーブル
	private boolean physicalTableNameCheckBox;

	private boolean logicalTableNameCheckBox;

	private boolean physicalColumnNameCheckBox;

	private boolean logicalColumnNameCheckBox;

	private boolean columnTypeCheckBox;

	private boolean columnLengthCheckBox;

	private boolean columnDecimalCheckBox;

	private boolean columnDefaultValueCheckBox;

	private boolean columnGroupNameCheckBox;

	// グループ
	private boolean groupNameCheckBox;

	private boolean physicalGroupColumnNameCheckBox;

	private boolean logicalGroupColumnNameCheckBox;

	private boolean groupColumnDefaultValueCheckBox;

	// その他
	private boolean indexCheckBox;

	private boolean noteCheckBox;

	private boolean modelPropertiesCheckBox;

	private boolean relationCheckBox;

	private Object currentTarget;

	private String currentKeyword;

	private boolean all;

	private static final List<String> keywordList = new ArrayList<String>();

	public SearchManager(ERDiagram diagram) {
		this.diagram = diagram;
	}

	public SearchResult search(String keyword, boolean all,
			boolean physicalWordNameCheckBox, boolean logicalWordNameCheckBox,
			boolean wordTypeCheckBox, boolean wordLengthCheckBox,
			boolean wordDecimalCheckBox, boolean wordDescriptionCheckBox,
			boolean physicalTableNameCheckBox,
			boolean logicalTableNameCheckBox,
			boolean physicalColumnNameCheckBox,
			boolean logicalColumnNameCheckBox, boolean columnTypeCheckBox,
			boolean columnLengthCheckBox, boolean columnDecimalCheckBox,
			boolean columnDefaultValueCheckBox,
			boolean columnDescriptionCheckBox, boolean columnGroupNameCheckBox,
			boolean indexCheckBox, boolean noteCheckBox,
			boolean modelPropertiesCheckBox, boolean relationCheckBox,
			boolean groupNameCheckBox, boolean physicalGroupColumnNameCheckBox,
			boolean logicalGroupColumnNameCheckBox,
			boolean groupColumnTypeCheckBox, boolean groupColumnLengthCheckBox,
			boolean groupColumnDecimalCheckBox,
			boolean groupColumnDefaultValueCheckBox,
			boolean groupColumnDescriptionCheckBox) {

		// 単語
		this.physicalWordNameCheckBox = physicalWordNameCheckBox;
		this.logicalWordNameCheckBox = logicalWordNameCheckBox;
		this.wordTypeCheckBox = wordTypeCheckBox;
		this.wordLengthCheckBox = wordLengthCheckBox;
		this.wordDecimalCheckBox = wordDecimalCheckBox;
		this.wordDescriptionCheckBox = wordDescriptionCheckBox;
		// テーブル
		this.physicalTableNameCheckBox = physicalTableNameCheckBox;
		this.logicalTableNameCheckBox = logicalTableNameCheckBox;
		this.physicalColumnNameCheckBox = physicalColumnNameCheckBox;
		this.logicalColumnNameCheckBox = logicalColumnNameCheckBox;
		this.columnTypeCheckBox = columnTypeCheckBox;
		this.columnLengthCheckBox = columnLengthCheckBox;
		this.columnDecimalCheckBox = columnDecimalCheckBox;
		this.columnDefaultValueCheckBox = columnDefaultValueCheckBox;
		this.columnGroupNameCheckBox = columnGroupNameCheckBox;
		// その他
		this.indexCheckBox = indexCheckBox;
		this.noteCheckBox = noteCheckBox;
		this.modelPropertiesCheckBox = modelPropertiesCheckBox;
		this.relationCheckBox = relationCheckBox;
		// グループ
		this.groupNameCheckBox = groupNameCheckBox;
		this.physicalGroupColumnNameCheckBox = physicalGroupColumnNameCheckBox;
		this.logicalGroupColumnNameCheckBox = logicalGroupColumnNameCheckBox;
		this.groupColumnDefaultValueCheckBox = groupColumnDefaultValueCheckBox;

		// すべて検索（置換）
		this.all = all;

		if (keyword.equals("")) {
			return null;
		}

		addKeyword(keyword);
		this.currentKeyword = keyword.toUpperCase();

		SearchResult result = null;
		List<SearchResultRow> rows = new ArrayList<SearchResultRow>();

		// 現在の検索候補が設定されている場合は、その検索候補まで、検索をスキップします
		boolean skip = false;
		if (this.currentTarget != null) {
			skip = true;
		}

		boolean loop = true;

		while (loop) {
			for (Word word : this.diagram.getDiagramContents().getDictionary()
					.getWordList()) {
				if (skip) {
					// スキップ中の場合
					if (word != this.currentTarget) {
						continue;

					} else {
						skip = false;
						continue;
					}
				} else {
					// 次の検索候補を探し中
					if (word == this.currentTarget) {
						// 現在の検索候補まで戻ってきてしまった場合
						loop = false;
					}
				}

				rows.addAll(this.search(word, this.currentKeyword,
						ResourceString.getResourceString("label.dictionary")));

				if (!rows.isEmpty() && !all) {
					// 検索候補が見つかって、すべて検索ではない場合

					// 検索結果を作成して終了
					result = new SearchResult(word, rows);
					loop = false;
				}

				if (!loop) {
					break;
				}
			}

			if (!loop) {
				break;
			}

			for (NodeElement nodeElement : this.diagram.getDiagramContents()
					.getContents()) {
				if (skip) {
					if (nodeElement != this.currentTarget) {
						continue;

					} else {
						skip = false;
						continue;
					}
				} else {
					if (nodeElement == this.currentTarget) {
						loop = false;
					}
				}

				if (nodeElement instanceof ERTable) {
					rows.addAll(this.search((ERTable) nodeElement,
							this.currentKeyword));

				} else if (nodeElement instanceof Note) {
					rows.addAll(this.search((Note) nodeElement,
							this.currentKeyword));

				} else if (nodeElement instanceof ModelProperties) {
					rows.addAll(this.search((ModelProperties) nodeElement,
							this.currentKeyword));
				}

				if (!rows.isEmpty() && !all) {
					result = new SearchResult(nodeElement, rows);
					loop = false;
				}

				if (!loop) {
					break;
				}
			}

			if (!loop) {
				break;
			}

			if (this.relationCheckBox) {
				for (NodeElement nodeElement : this.diagram
						.getDiagramContents().getContents()) {
					if (nodeElement instanceof ERTable) {
						ERTable table = (ERTable) nodeElement;

						for (Relation relation : table.getIncomingRelations()) {
							if (skip) {
								if (relation != this.currentTarget) {
									continue;

								} else {
									skip = false;
									continue;
								}
							} else {
								if (relation == this.currentTarget) {
									loop = false;
								}
							}

							rows.addAll(this.search(relation, keyword));
							if (!rows.isEmpty() && !all) {
								result = new SearchResult(relation, rows);
								loop = false;
							}

							if (!loop) {
								break;
							}
						}

					}

					if (!loop) {
						break;
					}
				}
			}

			if (!loop) {
				break;
			}

			for (ColumnGroup columnGroup : this.diagram.getDiagramContents()
					.getGroups()) {
				if (skip) {
					if (columnGroup != this.currentTarget) {
						continue;

					} else {
						skip = false;
						continue;
					}
				} else {
					if (columnGroup == this.currentTarget) {
						loop = false;
					}
				}

				rows.addAll(this.search(columnGroup, keyword));
				if (!rows.isEmpty() && !all) {
					result = new SearchResult(columnGroup, rows);
					loop = false;
				}

				if (!loop) {
					break;
				}
			}

			if (skip || this.currentTarget == null) {
				// 前回の検索対象がなくなってしまった場合
				// または、最初の検索が１件もヒットしなかった場合
				loop = false;
			}
		}

		if (result != null) {
			this.currentTarget = result.getResultObject();

		} else if (!rows.isEmpty()) {
			result = new SearchResult(null, rows);
		}

		return result;
	}

	public SearchResult research() {
		SearchResult result = null;
		List<SearchResultRow> rows = new ArrayList<SearchResultRow>();

		boolean skip = false;
		if (this.currentTarget != null) {
			skip = true;
		}

		boolean loop = true;

		while (loop) {
			for (Word word : this.diagram.getDiagramContents().getDictionary()
					.getWordList()) {
				if (skip) {
					// スキップ中の場合
					if (word != this.currentTarget) {
						continue;

					} else {
						skip = false;
					}
				} else {
					// 次の検索候補を探し中
					if (word == this.currentTarget) {
						// 現在の検索候補まで戻ってきてしまった場合
						loop = false;
						break;
					}
				}

				rows.addAll(this.search(word, this.currentKeyword,
						ResourceString.getResourceString("label.dictionary")));

				if (!rows.isEmpty() && !all) {
					// 検索候補が見つかって、すべて検索ではない場合

					// 検索結果を作成して終了
					result = new SearchResult(word, rows);
					loop = false;
				}

				if (!loop) {
					break;
				}
			}

			if (!loop) {
				break;
			}

			for (NodeElement nodeElement : this.diagram.getDiagramContents()
					.getContents()) {
				if (skip) {
					if (nodeElement != this.currentTarget) {
						continue;

					} else {
						skip = false;
					}
				} else {
					if (nodeElement == this.currentTarget) {
						loop = false;
						break;
					}
				}

				if (nodeElement instanceof ERTable) {
					rows.addAll(this.search((ERTable) nodeElement,
							this.currentKeyword));

				} else if (nodeElement instanceof Note) {
					rows.addAll(this.search((Note) nodeElement,
							this.currentKeyword));

				} else if (nodeElement instanceof ModelProperties) {
					rows.addAll(this.search((ModelProperties) nodeElement,
							this.currentKeyword));
				}

				if (!rows.isEmpty() && !this.all) {
					result = new SearchResult(nodeElement, rows);
					loop = false;
				}

				if (!loop) {
					break;
				}
			}

			if (!loop) {
				break;
			}

			if (this.relationCheckBox) {
				for (NodeElement nodeElement : this.diagram
						.getDiagramContents().getContents()) {
					if (nodeElement instanceof ERTable) {
						ERTable table = (ERTable) nodeElement;

						for (Relation relation : table.getIncomingRelations()) {
							if (skip) {
								if (relation != this.currentTarget) {
									continue;

								} else {
									skip = false;
								}
							} else {
								if (relation == this.currentTarget) {
									loop = false;
									break;
								}
							}

							rows.addAll(this.search(relation,
									this.currentKeyword));
							if (!rows.isEmpty() && !this.all) {
								result = new SearchResult(relation, rows);
								loop = false;
							}

							if (!loop) {
								break;
							}
						}

					}

					if (!loop) {
						break;
					}
				}
			}

			if (!loop) {
				break;
			}

			for (ColumnGroup columnGroup : this.diagram.getDiagramContents()
					.getGroups()) {
				if (skip) {
					if (columnGroup != this.currentTarget) {
						continue;

					} else {
						skip = false;
					}
				} else {
					if (columnGroup == this.currentTarget) {
						loop = false;
						break;
					}
				}

				rows.addAll(this.search(columnGroup, this.currentKeyword));
				if (!rows.isEmpty() && !this.all) {
					result = new SearchResult(columnGroup, rows);
					loop = false;
				}

				if (!loop) {
					break;
				}
			}

			if (skip || this.currentTarget == null) {
				loop = false;
			}
		}

		if (result != null) {
			this.currentTarget = result.getResultObject();

		} else if (!rows.isEmpty()) {
			result = new SearchResult(null, rows);
		}

		return result;
	}

	private List<SearchResultRow> search(ERTable table, String keyword) {
		List<SearchResultRow> rows = new ArrayList<SearchResultRow>();

		String path = table.getLogicalName();

		if (this.physicalTableNameCheckBox) {
			if (this.search(table.getPhysicalName(), keyword)) {
				rows.add(new SearchResultRow(
						SearchResultRow.TYPE_TABLE_PHYSICAL_NAME, table
								.getPhysicalName(), path, table, table));
			}
		}

		if (this.logicalTableNameCheckBox) {
			if (this.search(table.getLogicalName(), keyword)) {
				rows.add(new SearchResultRow(
						SearchResultRow.TYPE_TABLE_LOGICAL_NAME, table
								.getLogicalName(), path, table, table));
			}
		}

		if (this.physicalColumnNameCheckBox || this.logicalColumnNameCheckBox
				|| this.columnTypeCheckBox || this.columnLengthCheckBox
				|| this.columnDecimalCheckBox
				|| this.columnDefaultValueCheckBox
				|| this.columnGroupNameCheckBox) {

			for (Column column : table.getColumns()) {
				if (column instanceof NormalColumn) {
					NormalColumn normalColumn = (NormalColumn) column;

					rows.addAll(search(table, normalColumn, keyword,
							COLUMN_TYPE_NORMAL, path));

				} else if (column instanceof ColumnGroup) {
					if (this.columnGroupNameCheckBox) {
						if (this.search(column.getName(), keyword)) {
							String childPath = path + column.getName();

							rows
									.add(new SearchResultRow(
											SearchResultRow.TYPE_COLUMN_GROUP_NAME,
											column.getName(), childPath,
											column, table));
						}
					}
				}
			}
		}

		if (this.indexCheckBox) {
			for (Index index : table.getIndexes()) {
				rows.addAll(search(table, index, keyword, path));
			}
		}

		return rows;
	}

	private List<SearchResultRow> search(Note note, String keyword) {
		List<SearchResultRow> rows = new ArrayList<SearchResultRow>();

		if (this.noteCheckBox) {

			String path = null;

			if (this.search(note.getText(), keyword)) {
				rows.add(new SearchResultRow(SearchResultRow.TYPE_NOTE, note
						.getText(), path, note, note));
			}
		}

		return rows;
	}

	private List<SearchResultRow> search(ModelProperties modelProperties,
			String keyword) {
		List<SearchResultRow> rows = new ArrayList<SearchResultRow>();

		if (this.modelPropertiesCheckBox) {

			String path = null;

			for (NameValue property : modelProperties.getProperties()) {
				if (this.search(property.getName(), keyword)) {
					rows
							.add(new SearchResultRow(
									SearchResultRow.TYPE_MODEL_PROPERTY_NAME,
									property.getName(), path, property,
									modelProperties));
				}

				if (this.search(property.getValue(), keyword)) {
					rows.add(new SearchResultRow(
							SearchResultRow.TYPE_MODEL_PROPERTY_VALUE, property
									.getValue(), path, property,
							modelProperties));
				}
			}
		}

		return rows;
	}

	private List<SearchResultRow> search(ERTable table,
			NormalColumn normalColumn, String keyword, int type,
			String parentPath) {
		List<SearchResultRow> rows = new ArrayList<SearchResultRow>();

		String path = parentPath + "/" + normalColumn.getLogicalName();

		if (type == COLUMN_TYPE_GROUP && this.physicalGroupColumnNameCheckBox) {
			if (this.search(normalColumn.getForeignKeyPhysicalName(), keyword)) {
				rows.add(new SearchResultRow(
						SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_PHYSICAL_NAME,
						normalColumn.getForeignKeyPhysicalName(), path,
						normalColumn, table));
			}
		} else if (physicalColumnNameCheckBox) {
			if (this.search(normalColumn.getForeignKeyPhysicalName(), keyword)) {
				rows.add(new SearchResultRow(
						SearchResultRow.TYPE_COLUMN_PHYSICAL_NAME, normalColumn
								.getForeignKeyPhysicalName(), path,
						normalColumn, table));
			}
		}
		if (type == COLUMN_TYPE_GROUP && this.logicalGroupColumnNameCheckBox) {
			if (this.search(normalColumn.getForeignKeyLogicalName(), keyword)) {
				rows.add(new SearchResultRow(
						SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_LOGICAL_NAME,
						normalColumn.getForeignKeyLogicalName(), path,
						normalColumn, table));
			}
		} else if (this.logicalColumnNameCheckBox) {
			if (this.search(normalColumn.getForeignKeyLogicalName(), keyword)) {
				rows.add(new SearchResultRow(
						SearchResultRow.TYPE_COLUMN_LOGICAL_NAME, normalColumn
								.getForeignKeyLogicalName(), path,
						normalColumn, table));
			}
		}

		if (type == COLUMN_TYPE_GROUP && this.groupColumnDefaultValueCheckBox) {
			if (this.search(normalColumn.getDefaultValue(), keyword)) {
				rows.add(new SearchResultRow(
						SearchResultRow.TYPE_COLUMN_GROUP_COLUMN_DEFAULT_VALUE,
						normalColumn.getDefaultValue(), path, normalColumn,
						table));
			}
		} else if (this.columnDefaultValueCheckBox) {
			if (this.search(normalColumn.getDefaultValue(), keyword)) {
				rows.add(new SearchResultRow(
						SearchResultRow.TYPE_COLUMN_DEFAULT_VALUE, normalColumn
								.getDefaultValue(), path, normalColumn, table));
			}
		}

		return rows;
	}

	private List<SearchResultRow> search(Word word, String keyword,
			String parentPath) {
		List<SearchResultRow> rows = new ArrayList<SearchResultRow>();

		String path = parentPath + "/" + word.getLogicalName();

		if (physicalWordNameCheckBox) {
			if (this.search(word.getPhysicalName(), keyword)) {
				rows.add(new SearchResultRow(
						SearchResultRow.TYPE_WORD_PHYSICAL_NAME, word
								.getPhysicalName(), path, word, null));
			}
		}
		if (this.logicalWordNameCheckBox) {
			if (this.search(word.getLogicalName(), keyword)) {
				rows.add(new SearchResultRow(
						SearchResultRow.TYPE_WORD_LOGICAL_NAME, word
								.getLogicalName(), path, word, null));
			}
		}
		if (word.getType() != null
				&& word.getType().getAlias(this.diagram.getDatabase()) != null) {
			if (this.wordTypeCheckBox) {
				if (this.search(word.getType().getAlias(
						this.diagram.getDatabase()), keyword)) {
					rows.add(new SearchResultRow(
							SearchResultRow.TYPE_WORD_TYPE, word.getType()
									.getAlias(this.diagram.getDatabase()),
							path, word, null));
				}
			}
		}

		if (this.wordLengthCheckBox) {
			if (this.search(word.getTypeData().getLength(), keyword)) {
				rows.add(new SearchResultRow(SearchResultRow.TYPE_WORD_LENGTH,
						String.valueOf(word.getTypeData().getLength()), path,
						word, null));
			}
		}

		if (this.wordDecimalCheckBox) {
			if (this.search(word.getTypeData().getDecimal(), keyword)) {
				rows.add(new SearchResultRow(SearchResultRow.TYPE_WORD_DECIMAL,
						String.valueOf(word.getTypeData().getDecimal()), path,
						word, null));
			}
		}

		if (this.wordDescriptionCheckBox) {
			if (this.search(word.getDescription(), keyword)) {
				rows.add(new SearchResultRow(SearchResultRow.TYPE_WORD_COMMENT,
						word.getDescription(), path, word, null));
			}
		}

		return rows;
	}

	private List<SearchResultRow> search(ERTable table, Index index,
			String keyword, String parentPath) {
		List<SearchResultRow> rows = new ArrayList<SearchResultRow>();

		String path = parentPath + "/" + index.getName();

		if (this.search(index.getName(), keyword)) {
			rows.add(new SearchResultRow(SearchResultRow.TYPE_INDEX_NAME, index
					.getName(), path, index, table));
		}
		for (NormalColumn normalColumn : index.getColumns()) {
			if (this.search(normalColumn.getPhysicalName(), keyword)) {
				rows.add(new SearchResultRow(
						SearchResultRow.TYPE_INDEX_COLUMN_NAME, normalColumn
								.getPhysicalName(), path, normalColumn, table));
			}
		}

		return rows;
	}

	private List<SearchResultRow> search(Relation relation, String keyword) {
		List<SearchResultRow> rows = new ArrayList<SearchResultRow>();

		if (this.search(relation.getName(), keyword)) {
			String path = relation.getName();
			rows.add(new SearchResultRow(SearchResultRow.TYPE_RELATION_NAME,
					relation.getName(), path, relation, relation));

		}

		return rows;
	}

	private List<SearchResultRow> search(ColumnGroup columnGroup, String keyword) {
		List<SearchResultRow> rows = new ArrayList<SearchResultRow>();

		String path = columnGroup.getGroupName();

		if (this.groupNameCheckBox
				&& this.search(columnGroup.getName(), keyword)) {
			rows.add(new SearchResultRow(
					SearchResultRow.TYPE_COLUMN_GROUP_NAME, columnGroup
							.getName(), path, columnGroup, columnGroup));
		}

		for (NormalColumn normalColumn : columnGroup.getColumns()) {
			rows.addAll(search(null, normalColumn, keyword, COLUMN_TYPE_GROUP,
					path));
		}

		return rows;
	}

	private boolean search(String str, String keyword) {
		if (str == null) {
			return false;
		}

		if (str.toUpperCase().indexOf(keyword) != -1) {
			return true;
		}

		return false;
	}

	private boolean search(Integer num, String keyword) {
		if (num == null) {
			return false;
		}

		return search(String.valueOf(num), keyword);
	}

	private static void addKeyword(String keyword) {
		if (!keywordList.contains(keyword)) {
			keywordList.add(0, keyword);
		}

		if (keywordList.size() > 20) {
			keywordList.remove(keywordList.size() - 1);
		}
	}

	public static List<String> getKeywordList() {
		return keywordList;
	}
}
