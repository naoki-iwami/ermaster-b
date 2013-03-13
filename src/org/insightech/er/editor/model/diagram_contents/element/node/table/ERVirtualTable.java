package org.insightech.er.editor.model.diagram_contents.element.node.table;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;


/**
 * ダイアグラム（ERModel）内に定義された仮想テーブルです。
 * @author iwami
 */
public class ERVirtualTable extends ERTable {

	/** 親モデル */
	private ERModel model;
	
	/** テーブル実体 */
	private ERTable rawTable;
	
	@Override
	public void setLocation(Location location) {
		// TODO Auto-generated method stub
		super.setLocation(location);
	}
	
	public ERVirtualTable(ERModel model, ERTable rawTable) {
		super();
		this.model = model;
		this.rawTable = rawTable;
//		setDiagram(rawTable.getDiagram());
//		this.tableViewProperties = DBManagerFactory.getDBManager(
//				this.getDiagram()).createTableProperties(
//				(TableProperties) this.tableViewProperties);
//
//		Dictionary dictionary = this.getDiagram().getDiagramContents()
//				.getDictionary();
//
//		setPhysicalName(rawTable.getPhysicalName());
//		setLogicalName(this.getLogicalName());
//		setDescription(this.getDescription());
//
//		for (NormalColumn toColumn : to.getNormalColumns()) {
//			dictionary.remove(toColumn);
//		}
//
//		List<Column> columns = new ArrayList<Column>();
//
//		List<NormalColumn> newPrimaryKeyColumns = new ArrayList<NormalColumn>();
//
//		for (Column fromColumn : this.getColumns()) {
//			if (fromColumn instanceof NormalColumn) {
//				CopyColumn copyColumn = (CopyColumn) fromColumn;
//
//				CopyWord copyWord = copyColumn.getWord();
//				if (copyColumn.isForeignKey()) {
//					copyWord = null;
//				}
//
//				if (copyWord != null) {
//					Word originalWord = copyColumn.getOriginalWord();
//					dictionary.copyTo(copyWord, originalWord);
//				}
//
//				NormalColumn restructuredColumn = copyColumn
//						.getRestructuredColumn();
//
//				restructuredColumn.setColumnHolder(this);
//				if (copyWord == null) {
//					restructuredColumn.setWord(null);
//				}
//				columns.add(restructuredColumn);
//
//				if (restructuredColumn.isPrimaryKey()) {
//					newPrimaryKeyColumns.add(restructuredColumn);
//				}
//
//				dictionary.add(restructuredColumn);
//
//			} else {
//				columns.add(fromColumn);
//			}
//		}
//
//		this.setTargetTableRelation(to, newPrimaryKeyColumns);
//
//		to.setColumns(columns);

//		rawTable.copyTableViewData(this).restructureData(this);
//		rawTable.clone().restructureData(this);
//		rawTable.restructureData(this);
//		restructureData(rawTable);
	}

	// ---------------------------------------------------------------- Delegete Methods

	@Override
	public void setFontSize(int fontSize) {
		// TODO Auto-generated method stub
		super.setFontSize(fontSize);
	}
	
	@Override
	public int getFontSize() {
		// TODO Auto-generated method stub
		return super.getFontSize();
	}
	
//	@Override
//	public int getFontSize() {
//		if (super.getFontSize() == 0) {
//			return super.getFontSize();
//		}
//		return rawTable.getFontSize();
//	}
//	
//	@Override
//	public String getFontName() {
//		if (super.getFontName() == null) {
//			return super.getFontName();
//		}
//		return rawTable.getFontName();
//	}

	@Override
	public void setColor(int red, int green, int blue) {
		rawTable.setColor(red, green, blue);
	}
	
	@Override
	public int[] getColor() {
		return rawTable.getColor();
	}

	@Override
	public ERDiagram getDiagram() {
		return rawTable.getDiagram();
	}

	public void setPoint(int x, int y) {
		this.setLocation(new Location(x, y, getWidth(), getHeight()));
	}
	
//	@Override
//	public int getX() {
//		return rawTable.getX();
//	}
//
//	@Override
//	public int getY() {
//		return rawTable.getY();
//	}

	@Override
	public int getWidth() {
		return rawTable.getWidth();
	}

	@Override
	public int getHeight() {
		return rawTable.getHeight();
	}

	@Override
	public List<ConnectionElement> getIncomings() {
		System.out.println("ERVirtualTable::getIncomings");
		List<ConnectionElement> elements = new ArrayList<ConnectionElement>();
		List<ERVirtualTable> modelTables = model.getTables();
		for (ConnectionElement el : rawTable.getIncomings()) {
			NodeElement findEl = el.getSource();
			if (findEl instanceof Note) {
				if (((Note)findEl).getModel().equals(model)) {
					elements.add(el);
				}
//				elements.add(el);
			} else {
				for (ERVirtualTable vtable : modelTables) {
					if (vtable.getRawTable().equals(findEl)) {
						elements.add(el);
						break;
					}
				}
			}
		}
		return elements;
	}

	@Override
	public List<ConnectionElement> getOutgoings() {
		List<ConnectionElement> elements = new ArrayList<ConnectionElement>();
		List<ERVirtualTable> modelTables = model.getTables();
		for (ConnectionElement el : rawTable.getOutgoings()) {
			NodeElement findEl = el.getTarget();
			if (findEl instanceof Note) {
				if (((Note)findEl).getModel().equals(model)) {
					elements.add(el);
				}
				elements.add(el);
			} else {
				for (ERVirtualTable vtable : modelTables) {
					if (vtable.getRawTable().equals(findEl)) {
						elements.add(el);
						break;
					}
				}
			}
		}
		return elements;
	}
	
	

	@Override
	public NormalColumn getAutoIncrementColumn() {
		return rawTable.getAutoIncrementColumn();
	}

	@Override
	public TableViewProperties getTableViewProperties() {
		return rawTable.getTableViewProperties();
	}

	@Override
	public String getPhysicalName() {
		return rawTable.getPhysicalName();
	}

	@Override
	public List<NodeElement> getReferringElementList() {
		return rawTable.getReferringElementList();
	}

	@Override
	public TableViewProperties getTableViewProperties(String database) {
		return rawTable.getTableViewProperties(database);
	}

	@Override
	public String getLogicalName() {
		return rawTable.getLogicalName();
	}

	@Override
	public List<NodeElement> getReferedElementList() {
		return rawTable.getReferedElementList();
	}

	@Override
	public String getName() {
		return rawTable.getName();
	}

	@Override
	public String getDescription() {
		return rawTable.getDescription();
	}

	@Override
	public List<Column> getColumns() {
		return rawTable.getColumns();
	}

	@Override
	public List<NormalColumn> getExpandedColumns() {
		return rawTable.getExpandedColumns();
	}

	@Override
	public List<Relation> getIncomingRelations() {
		List<Relation> elements = new ArrayList<Relation>();
		List<ERVirtualTable> modelTables = model.getTables();
		for (Relation el : rawTable.getIncomingRelations()) {
			NodeElement findEl = el.getSource();
			for (ERVirtualTable vtable : modelTables) {
				if (vtable.getRawTable().equals(findEl)) {
					elements.add(el);
					break;
				}
			}
		}
		return elements;
//		return rawTable.getIncomingRelations();
	}

	@Override
	public List<Relation> getOutgoingRelations() {
		List<Relation> elements = new ArrayList<Relation>();
		List<ERVirtualTable> modelTables = model.getTables();
		for (Relation el : rawTable.getOutgoingRelations()) {
			NodeElement findEl = el.getSource();
			for (ERVirtualTable vtable : modelTables) {
				if (vtable.getRawTable().equals(findEl)) {
					elements.add(el);
					break;
				}
			}
		}
		return elements;
//		return rawTable.getOutgoingRelations();
	}

	@Override
	public List<NormalColumn> getNormalColumns() {
		return rawTable.getNormalColumns();
	}

	@Override
	public int getPrimaryKeySize() {
		return rawTable.getPrimaryKeySize();
	}

	@Override
	public Column getColumn(int index) {
		return rawTable.getColumn(index);
	}

	@Override
	public List<NormalColumn> getPrimaryKeys() {
		return rawTable.getPrimaryKeys();
	}

	@Override
	public Index getIndex(int index) {
		return rawTable.getIndex(index);
	}

	@Override
	public List<Index> getIndexes() {
		return rawTable.getIndexes();
	}

	@Override
	public List<ComplexUniqueKey> getComplexUniqueKeyList() {
		return rawTable.getComplexUniqueKeyList();
	}

	@Override
	public String getConstraint() {
		return rawTable.getConstraint();
	}

	@Override
	public String getPrimaryKeyName() {
		return rawTable.getPrimaryKeyName();
	}

	@Override
	public String getOption() {
		return rawTable.getOption();
	}

	@Override
	public String getNameWithSchema(String database) {
		return rawTable.getNameWithSchema(database);
	}

	/**
	 * テーブル実体を取得します。
	 * @return テーブル実体
	 */
	public ERTable getRawTable() {
	    return rawTable;
	}

	/**
	 * テーブル実体を設定します。
	 * @param rawTable テーブル実体
	 */
	public void setRawTable(ERTable rawTable) {
	    this.rawTable = rawTable;
	}

	@Override
	public String getObjectType() {
		return "vtable";
	}

	/**
	 * テーブル実体を更新したときに呼ばれます。
	 */
	public void doChangeTable() {
		firePropertyChange(PROPERTY_CHANGE_COLUMNS, null, null);
	}

}
