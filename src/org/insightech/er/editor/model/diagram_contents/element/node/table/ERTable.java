package org.insightech.er.editor.model.diagram_contents.element.node.table;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.ResourceString;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.ColumnHolder;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.CopyIndex;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.IndexSet;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TablePropertiesHolder;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.CopyComplexUniqueKey;

/**
 * テーブルのモデル
 * 
 * @author nakajima
 * 
 */
public class ERTable extends TableView implements TablePropertiesHolder,
		ColumnHolder, ObjectModel {

	private static final long serialVersionUID = 11185865758118654L;

	public static final String NEW_PHYSICAL_NAME = ResourceString
			.getResourceString("new.table.physical.name");

	public static final String NEW_LOGICAL_NAME = ResourceString
			.getResourceString("new.table.logical.name");

	private String constraint;

	private String primaryKeyName;

	private String option;

	private List<Index> indexes;

	private List<ComplexUniqueKey> complexUniqueKeyList;

	public ERTable() {
		this.indexes = new ArrayList<Index>();
		this.complexUniqueKeyList = new ArrayList<ComplexUniqueKey>();
	}

	@Override
	public String toString() {
		return getPhysicalName();
	}
	
	public NormalColumn getAutoIncrementColumn() {
		for (Column column : columns) {
			if (column instanceof NormalColumn) {
				NormalColumn normalColumn = (NormalColumn) column;
				if (normalColumn.isAutoIncrement()) {
					return normalColumn;
				}
			}
		}

		return null;
	}

	@Override
	public TableViewProperties getTableViewProperties() {
		this.tableViewProperties = DBManagerFactory.getDBManager(
				this.getDiagram()).createTableProperties(
				(TableProperties) this.tableViewProperties);

		return this.tableViewProperties;
	}

	public TableViewProperties getTableViewProperties(String database) {
		this.tableViewProperties = DBManagerFactory.getDBManager(database)
				.createTableProperties(
						(TableProperties) this.tableViewProperties);

		return this.tableViewProperties;
	}

	public void addIndex(Index index) {
		this.indexes.add(index);
	}

	@Override
	public ERTable copyData() {
		ERTable to = new ERTable();

		to.setConstraint(this.getConstraint());
		to.setPrimaryKeyName(this.getPrimaryKeyName());
		to.setOption(this.getOption());

		super.copyTableViewData(to);

		List<Index> indexes = new ArrayList<Index>();

		for (Index fromIndex : this.getIndexes()) {
			indexes.add(new CopyIndex(to, fromIndex, to.getColumns()));
		}

		to.setIndexes(indexes);

		List<ComplexUniqueKey> complexUniqueKeyList = new ArrayList<ComplexUniqueKey>();

		for (ComplexUniqueKey complexUniqueKey : this.getComplexUniqueKeyList()) {
			complexUniqueKeyList.add(new CopyComplexUniqueKey(complexUniqueKey,
					to.getColumns()));
		}

		to.complexUniqueKeyList = complexUniqueKeyList;

		to.tableViewProperties = (TableProperties) this
				.getTableViewProperties().clone();

		return to;
	}

	@Override
	public void restructureData(TableView to) {
		ERTable table = (ERTable) to;

		table.setConstraint(this.getConstraint());
		table.setPrimaryKeyName(this.getPrimaryKeyName());
		table.setOption(this.getOption());

		super.restructureData(to);

		List<Index> indexes = new ArrayList<Index>();

		for (Index fromIndex : this.getIndexes()) {
			CopyIndex copyIndex = (CopyIndex) fromIndex;
			Index restructuredIndex = copyIndex.getRestructuredIndex(table);
			indexes.add(restructuredIndex);
		}
		table.setIndexes(indexes);

		List<ComplexUniqueKey> complexUniqueKeyList = new ArrayList<ComplexUniqueKey>();

		for (ComplexUniqueKey complexUniqueKey : this.getComplexUniqueKeyList()) {
			CopyComplexUniqueKey copyComplexUniqueKey = (CopyComplexUniqueKey) complexUniqueKey;
			if (!copyComplexUniqueKey.isRemoved(this.getNormalColumns())) {
				ComplexUniqueKey restructuredComplexUniqueKey = copyComplexUniqueKey
						.restructure();
				complexUniqueKeyList.add(restructuredComplexUniqueKey);
			}
		}
		table.complexUniqueKeyList = complexUniqueKeyList;

		table.tableViewProperties = (TableProperties) this.tableViewProperties
				.clone();
	}

	public int getPrimaryKeySize() {
		int count = 0;

		for (Column column : this.columns) {
			if (column instanceof NormalColumn) {
				NormalColumn normalColumn = (NormalColumn) column;

				if (normalColumn.isPrimaryKey()) {
					count++;
				}
			}
		}

		return count;
	}

	public List<NormalColumn> getPrimaryKeys() {
		List<NormalColumn> primaryKeys = new ArrayList<NormalColumn>();

		for (Column column : this.columns) {
			if (column instanceof NormalColumn) {
				NormalColumn normalColumn = (NormalColumn) column;

				if (normalColumn.isPrimaryKey()) {
					primaryKeys.add(normalColumn);
				}
			}
		}

		return primaryKeys;
	}

	public boolean isReferable() {
		if (this.getPrimaryKeySize() > 0) {
			return true;
		}

		if (this.complexUniqueKeyList.size() > 0) {
			return true;
		}

		for (Column column : this.columns) {
			if (column instanceof NormalColumn) {
				NormalColumn normalColumn = (NormalColumn) column;

				if (normalColumn.isUniqueKey()) {
					return true;
				}
			}
		}

		return false;
	}

	public Index getIndex(int index) {
		return this.indexes.get(index);
	}

	public void removeIndex(int index) {
		this.indexes.remove(index);
	}

	public List<Index> getIndexes() {
		return indexes;
	}

	public void setIndexes(List<Index> indexes) {
		this.indexes = indexes;

		if (this.getDiagram() != null) {
			this.firePropertyChange(IndexSet.PROPERTY_CHANGE_INDEXES, null,
					null);
			this.getDiagram().getDiagramContents().getIndexSet().update();
		}
	}

	public void setComplexUniqueKeyList(
			List<ComplexUniqueKey> complexUniqueKeyList) {
		this.complexUniqueKeyList = complexUniqueKeyList;
	}

	public List<ComplexUniqueKey> getComplexUniqueKeyList() {
		return complexUniqueKeyList;
	}

	public void setTableViewProperties(TableProperties tableProperties) {
		this.tableViewProperties = tableProperties;
	}

	/**
	 * テーブルを複製します。<br>
	 * 複製する情報は、名前と、テーブルプロパティのみ。<br>
	 * 列および、インデックスは複製対象外とし、後から複製する。<br>
	 */
	@Override
	public ERTable clone() {
		ERTable clone = (ERTable) super.clone();

		// テーブルプロパティを複製します。
		TableProperties cloneTableProperties = (TableProperties) this
				.getTableViewProperties().clone();
		clone.tableViewProperties = cloneTableProperties;

		return clone;
	}

	/**
	 * constraint を取得します.
	 * 
	 * @return constraint
	 */
	public String getConstraint() {
		return constraint;
	}

	/**
	 * constraint を設定します.
	 * 
	 * @param constraint
	 *            constraint
	 */
	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}

	public String getPrimaryKeyName() {
		return primaryKeyName;
	}

	public void setPrimaryKeyName(String primaryKeyName) {
		this.primaryKeyName = primaryKeyName;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public static boolean isRecursive(TableView source, TableView target) {
		for (Relation relation : source.getIncomingRelations()) {
			TableView temp = relation.getSourceTableView();
			if (temp.equals(source)) {
				continue;
			}

			if (temp.equals(target)) {
				return true;
			}

			if (isRecursive(temp, target)) {
				return true;
			}
		}

		return false;
	}

	public Relation createRelation() {
		boolean referenceForPK = false;
		ComplexUniqueKey referencedComplexUniqueKey = null;
		NormalColumn referencedColumn = null;

		if (this.getPrimaryKeySize() > 0) {
			referenceForPK = true;

		} else if (this.getComplexUniqueKeyList().size() > 0) {
			referencedComplexUniqueKey = this.getComplexUniqueKeyList().get(0);

		} else {
			for (NormalColumn normalColumn : this.getNormalColumns()) {
				if (normalColumn.isUniqueKey()) {
					referencedColumn = normalColumn;
					break;
				}
			}
		}

		return new Relation(referenceForPK, referencedComplexUniqueKey,
				referencedColumn);
	}

	public String getObjectType() {
		return "table";
	}

	@Override
	public boolean needsUpdateOtherModel() {
		return true;
	}
}
