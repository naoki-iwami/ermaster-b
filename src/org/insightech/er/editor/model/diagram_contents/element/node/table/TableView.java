package org.insightech.er.editor.model.diagram_contents.element.node.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.ColumnHolder;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.CopyWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.util.Format;

public abstract class TableView extends NodeElement implements ObjectModel,
		ColumnHolder, Comparable<TableView> {

	private static final long serialVersionUID = -4492787972500741281L;

	public static final String PROPERTY_CHANGE_PHYSICAL_NAME = "table_view_physicalName";

	public static final String PROPERTY_CHANGE_LOGICAL_NAME = "table_view_logicalName";

	public static final String PROPERTY_CHANGE_COLUMNS = "columns";

	public static final int DEFAULT_WIDTH = 120;

	public static final int DEFAULT_HEIGHT = 75;

	public static final Comparator<TableView> PHYSICAL_NAME_COMPARATOR = new TableViewPhysicalNameComparator();

	public static final Comparator<TableView> LOGICAL_NAME_COMPARATOR = new TableViewLogicalNameComparator();

	private String physicalName;

	private String logicalName;

	private String description;

	protected List<Column> columns;

	protected TableViewProperties tableViewProperties;

	public TableView() {
		this.columns = new ArrayList<Column>();
	}

	public String getPhysicalName() {
		return physicalName;
	}

	public void setPhysicalName(String physicalName) {
		String old = this.physicalName;
		this.physicalName = physicalName;

		this.firePropertyChange(PROPERTY_CHANGE_PHYSICAL_NAME, old,
				physicalName);
	}

	public String getLogicalName() {
		return logicalName;
	}

	public void setLogicalName(String logicalName) {
		String old = this.logicalName;
		this.logicalName = logicalName;

		this.firePropertyChange(PROPERTY_CHANGE_LOGICAL_NAME, old, logicalName);
	}

	public String getName() {
		return this.getLogicalName();
	}

	/**
	 * description を取得します.
	 * 
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * description を設定します.
	 * 
	 * @param description
	 *            description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public List<Column> getColumns() {
		return this.columns;
	}

//	public Collection<? extends Object> getIndexes() {
//		return new ArrayList<Object>();
//	}
	
	public TableViewProperties getTableViewProperties() {
		return this.tableViewProperties;
	}

	public List<NormalColumn> getExpandedColumns() {
		List<NormalColumn> expandedColumns = new ArrayList<NormalColumn>();

		for (Column column : this.getColumns()) {
			if (column instanceof NormalColumn) {
				NormalColumn normalColumn = (NormalColumn) column;
				expandedColumns.add(normalColumn);

			} else if (column instanceof ColumnGroup) {
				ColumnGroup groupColumn = (ColumnGroup) column;

				expandedColumns.addAll(groupColumn.getColumns());
			}
		}

		return expandedColumns;
	}

	public List<Relation> getIncomingRelations() {
		List<Relation> relations = new ArrayList<Relation>();

		for (ConnectionElement connection : this.getIncomings()) {
			if (connection instanceof Relation) {
				relations.add((Relation) connection);
			}
		}

		return relations;
	}

	public List<Relation> getOutgoingRelations() {
		List<Relation> relations = new ArrayList<Relation>();

		for (ConnectionElement connection : this.getOutgoings()) {
			if (connection instanceof Relation) {
				relations.add((Relation) connection);
			}
		}

		Collections.sort(relations);

		return relations;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLocation(Location location) {
		super.setLocation(location);

		if (this.getDiagram() != null) {
			for (Relation relation : this.getOutgoingRelations()) {
				relation.setParentMove();
			}
			for (Relation relation : this.getIncomingRelations()) {
				relation.setParentMove();
			}
		}
	}

	public List<NormalColumn> getNormalColumns() {
		List<NormalColumn> normalColumns = new ArrayList<NormalColumn>();

		for (Column column : this.columns) {
			if (column instanceof NormalColumn) {
				normalColumns.add((NormalColumn) column);
			}
		}
		return normalColumns;
	}

	public Column getColumn(int index) {
		return this.columns.get(index);
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;

		for (Column column : columns) {
			column.setColumnHolder(this);
		}
		this.firePropertyChange(PROPERTY_CHANGE_COLUMNS, null, null);
	}

	public void setDirty() {
		this.firePropertyChange(PROPERTY_CHANGE_COLUMNS, null, null);
	}

	public void addColumn(Column column) {
		this.columns.add(column);
		column.setColumnHolder(this);

		this.firePropertyChange(PROPERTY_CHANGE_COLUMNS, null, null);
	}

	public void addColumn(int index, Column column) {
		this.columns.add(index, column);
		column.setColumnHolder(this);

		this.firePropertyChange(PROPERTY_CHANGE_COLUMNS, null, null);
	}

	public void removeColumn(Column column) {
		this.columns.remove(column);
		this.firePropertyChange(PROPERTY_CHANGE_COLUMNS, null, null);
	}

	public TableView copyTableViewData(TableView to) {
		to.setDiagram(this.getDiagram());

		to.setPhysicalName(this.getPhysicalName());
		to.setLogicalName(this.getLogicalName());
		to.setDescription(this.getDescription());

		List<Column> columns = new ArrayList<Column>();

		for (Column fromColumn : this.getColumns()) {
			if (fromColumn instanceof NormalColumn) {
				NormalColumn normalColumn = (NormalColumn) fromColumn;
				NormalColumn copyColumn = new CopyColumn(normalColumn);
				if (normalColumn.getWord() != null) {
					copyColumn.setWord(new CopyWord(normalColumn.getWord()));
				}
				columns.add(copyColumn);

			} else {
				columns.add(fromColumn);
			}
		}

		to.setColumns(columns);

		to.setOutgoing(this.getOutgoings());
		to.setIncoming(this.getIncomings());

		return to;
	}

	public void restructureData(TableView to) {
		Dictionary dictionary = this.getDiagram().getDiagramContents()
				.getDictionary();

		to.setPhysicalName(this.getPhysicalName());
		to.setLogicalName(this.getLogicalName());
		to.setDescription(this.getDescription());

		for (NormalColumn toColumn : to.getNormalColumns()) {
			dictionary.remove(toColumn);
		}

		List<Column> columns = new ArrayList<Column>();

		List<NormalColumn> newPrimaryKeyColumns = new ArrayList<NormalColumn>();

		for (Column fromColumn : this.getColumns()) {
			if (fromColumn instanceof NormalColumn) {
				CopyColumn copyColumn = (CopyColumn) fromColumn;

				CopyWord copyWord = copyColumn.getWord();
				if (copyColumn.isForeignKey()) {
					copyWord = null;
				}

				if (copyWord != null) {
					Word originalWord = copyColumn.getOriginalWord();
					dictionary.copyTo(copyWord, originalWord);
				}

				NormalColumn restructuredColumn = copyColumn
						.getRestructuredColumn();

				restructuredColumn.setColumnHolder(this);
				if (copyWord == null) {
					restructuredColumn.setWord(null);
				}
				columns.add(restructuredColumn);

				if (restructuredColumn.isPrimaryKey()) {
					newPrimaryKeyColumns.add(restructuredColumn);
				}

				dictionary.add(restructuredColumn);

			} else {
				columns.add(fromColumn);
			}
		}

		this.setTargetTableRelation(to, newPrimaryKeyColumns);

		to.setColumns(columns);
	}

	private void setTargetTableRelation(TableView sourceTable,
			List<NormalColumn> newPrimaryKeyColumns) {
		for (Relation relation : sourceTable.getOutgoingRelations()) {

			// 関連がPKを参照している場合
			if (relation.isReferenceForPK()) {
				// 参照するテーブル
				TableView targetTable = relation.getTargetTableView();

				// 外部キーリスト
				List<NormalColumn> foreignKeyColumns = relation
						.getForeignKeyColumns();

				boolean isPrimary = true;
				boolean isPrimaryChanged = false;

				// 参照されるテーブルのPKに対して処理を行う
				for (NormalColumn primaryKeyColumn : newPrimaryKeyColumns) {
					boolean isReferenced = false;

					for (Iterator<NormalColumn> iter = foreignKeyColumns
							.iterator(); iter.hasNext();) {

						// 外部キー
						NormalColumn foreignKeyColumn = iter.next();

						if (isPrimary) {
							isPrimary = foreignKeyColumn.isPrimaryKey();
						}

						// 外部キーの参照列がPK列と同じ場合
						for (NormalColumn referencedColumn : foreignKeyColumn
								.getReferencedColumnList()) {
							if (referencedColumn == primaryKeyColumn) {
								isReferenced = true;
								iter.remove();
								break;
							}
						}

						if (isReferenced) {
							break;
						}
					}

					if (!isReferenced) {
						if (isPrimary) {
							isPrimaryChanged = true;
						}
						NormalColumn foreignKeyColumn = new NormalColumn(
								primaryKeyColumn, primaryKeyColumn, relation,
								isPrimary);

						targetTable.addColumn(foreignKeyColumn);
					}
				}

				for (NormalColumn removedColumn : foreignKeyColumns) {
					if (removedColumn.isPrimaryKey()) {
						isPrimaryChanged = true;
					}
					targetTable.removeColumn(removedColumn);
				}

				if (isPrimaryChanged) {
					List<NormalColumn> nextNewPrimaryKeyColumns = ((ERTable) targetTable)
							.getPrimaryKeys();

					this.setTargetTableRelation(targetTable,
							nextNewPrimaryKeyColumns);
				}

				targetTable.setDirty();
			}
		}
	}

	public int compareTo(TableView other) {
		return PHYSICAL_NAME_COMPARATOR.compare(this, other);
	}

	public void replaceColumnGroup(ColumnGroup oldColumnGroup,
			ColumnGroup newColumnGroup) {
		int index = this.columns.indexOf(oldColumnGroup);
		if (index != -1) {
			this.columns.remove(index);
			this.columns.add(index, newColumnGroup);
		}
	}

	public String getNameWithSchema(String database) {
		StringBuilder sb = new StringBuilder();

		DBManager dbManager = DBManagerFactory.getDBManager(database);

		if (!dbManager.isSupported(DBManager.SUPPORT_SCHEMA)) {
			return Format.null2blank(this.getPhysicalName());
		}

		TableViewProperties commonTableViewProperties = this.getDiagram()
				.getDiagramContents().getSettings().getTableViewProperties();

		String schema = this.tableViewProperties.getSchema();

		if (schema == null || schema.equals("")) {
			schema = commonTableViewProperties.getSchema();
		}

		if (schema != null && !schema.equals("")) {
			sb.append(schema);
			sb.append(".");
		}

		sb.append(this.getPhysicalName());

		return sb.toString();
	}

	public abstract TableView copyData();

	private static class TableViewPhysicalNameComparator implements
			Comparator<TableView> {

		public int compare(TableView o1, TableView o2) {
			if (o1 == o2) {
				return 0;
			}
			if (o2 == null) {
				return -1;
			}
			if (o1 == null) {
				return 1;
			}

			int compareTo = Format.null2blank(
					o1.getTableViewProperties().getSchema()).toUpperCase()
					.compareTo(
							Format.null2blank(
									o2.getTableViewProperties().getSchema())
									.toUpperCase());

			if (compareTo != 0) {
				return compareTo;
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

			return 0;
		}

	}

	private static class TableViewLogicalNameComparator implements
			Comparator<TableView> {

		public int compare(TableView o1, TableView o2) {
			if (o1 == o2) {
				return 0;
			}
			if (o2 == null) {
				return -1;
			}
			if (o1 == null) {
				return 1;
			}

			int compareTo = Format.null2blank(
					o1.getTableViewProperties().getSchema()).toUpperCase()
					.compareTo(
							Format.null2blank(
									o2.getTableViewProperties().getSchema())
									.toUpperCase());

			if (compareTo != 0) {
				return compareTo;
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

			return 0;
		}
	}
}
