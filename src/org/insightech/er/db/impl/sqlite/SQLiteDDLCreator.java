package org.insightech.er.db.impl.sqlite;

import java.util.LinkedHashSet;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;

public class SQLiteDDLCreator extends DDLCreator {

	public SQLiteDDLCreator(ERDiagram diagram, boolean semicolon) {
		super(diagram, semicolon);
	}

	@Override
	protected String getDDL(Tablespace tablespace) {
		return null;
	}

	@Override
	protected String getColulmnDDL(NormalColumn normalColumn) {
		StringBuilder ddl = new StringBuilder();

		ddl.append(super.getColulmnDDL(normalColumn));

		if (normalColumn.isAutoIncrement()) {
			ddl.append(" PRIMARY KEY AUTOINCREMENT");
		}

		return ddl.toString();
	}

	@Override
	protected String getPrimaryKeyDDL(ERTable table) {
		StringBuilder ddl = new StringBuilder();

		for (Relation relation : table.getIncomingRelations()) {
			ddl.append(",\r\n\tFOREIGN KEY (");

			boolean first = true;

			for (NormalColumn column : relation.getForeignKeyColumns()) {
				if (!first) {
					ddl.append(", ");

				}
				ddl.append(filter(column.getPhysicalName()));
				first = false;
			}

			ddl.append(")\r\n");
			ddl.append("\tREFERENCES ");
			ddl.append(filter(relation.getSourceTableView().getNameWithSchema(
					this.getDiagram().getDatabase())));
			ddl.append(" (");

			first = true;

			for (NormalColumn foreignKeyColumn : relation
					.getForeignKeyColumns()) {
				if (!first) {
					ddl.append(", ");

				}

				ddl.append(filter(foreignKeyColumn
						.getReferencedColumn(relation).getPhysicalName()));
				first = false;
			}

			ddl.append(")");
		}

		return ddl.toString();
	}

	@Override
	protected Iterable<ERTable> getTablesForCreateDDL() {
		LinkedHashSet<ERTable> results = new LinkedHashSet<ERTable>();

		for (ERTable table : this.getDiagram().getDiagramContents()
				.getContents().getTableSet()) {
			if (!results.contains(table)) {
				this.getReferedTables(results, table);
				results.add(table);
			}
		}

		return results;
	}

	private void getReferedTables(LinkedHashSet<ERTable> referedTables,
			ERTable table) {
		for (NodeElement nodeElement : table.getReferedElementList()) {
			if (nodeElement instanceof ERTable) {
				if (nodeElement != table) {
					ERTable referedTable = (ERTable) nodeElement;
					if (!referedTables.contains(referedTable)) {
						this.getReferedTables(referedTables, referedTable);
						referedTables.add(referedTable);
					}
				}
			}
		}
	}

	@Override
	protected String getCreateForeignKeys(ERDiagram diagram) {
		return "";
	}
	
	
}
