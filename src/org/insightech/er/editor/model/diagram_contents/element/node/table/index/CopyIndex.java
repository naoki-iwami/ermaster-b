package org.insightech.er.editor.model.diagram_contents.element.node.table.index;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.CopyColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class CopyIndex extends Index {

	private static final long serialVersionUID = -7896024413398953097L;

	private Index originalIndex;

	public CopyIndex(ERTable copyTable, Index originalIndex,
			List<Column> copyColumns) {
		super(copyTable, originalIndex.getName(), originalIndex.isNonUnique(),
				originalIndex.getType(), originalIndex.getDescription());

		this.originalIndex = originalIndex;

		List<Boolean> descs = originalIndex.getDescs();

		int i = 0;

		for (NormalColumn originalIndexColumn : originalIndex.getColumns()) {
			Boolean desc = Boolean.FALSE;

			if (descs.size() > i) {
				desc = descs.get(i);
			}

			if (copyColumns != null) {

				boolean isGroupColumn = true;

				for (Column column : copyColumns) {
					if (column instanceof CopyColumn) {
						CopyColumn copyColumn = (CopyColumn) column;

						if (copyColumn.getOriginalColumn().equals(
								originalIndexColumn)) {
							this.addColumn(copyColumn, desc);
							isGroupColumn = false;
							break;
						}
					}
				}

				if (isGroupColumn) {
					this.addColumn(originalIndexColumn, desc);
				}

			} else {
				this.addColumn(originalIndexColumn, desc);
			}

			i++;
		}
	}

	public Index getRestructuredIndex(ERTable originalTable) {
		if (this.originalIndex == null) {
			this.originalIndex = new Index(originalTable, this.getName(), this
					.isNonUnique(), this.getType(), this.getDescription());
		}

		copyData(this, this.originalIndex);

		List<NormalColumn> indexColumns = new ArrayList<NormalColumn>();

		for (NormalColumn column : this.originalIndex.getColumns()) {
			if (column instanceof CopyColumn) {
				CopyColumn copyColumn = (CopyColumn) column;
				column = copyColumn.getOriginalColumn();
			}
			indexColumns.add(column);
		}

		this.originalIndex.setColumns(indexColumns);
		this.originalIndex.setTable(originalTable);

		return this.originalIndex;
	}

	public static void copyData(Index from, Index to) {
		to.setName(from.getName());
		to.setNonUnique(from.isNonUnique());
		to.setFullText(from.isFullText());
		to.setType(from.getType());
		to.setDescription(from.getDescription());

		to.getColumns().clear();
		to.getDescs().clear();

		List<Boolean> descs = from.getDescs();
		int i = 0;

		for (NormalColumn column : from.getColumns()) {
			Boolean desc = Boolean.FALSE;

			if (descs.size() > i) {
				desc = descs.get(i);
			}
			to.addColumn(column, desc);
			i++;
		}

	}

}
