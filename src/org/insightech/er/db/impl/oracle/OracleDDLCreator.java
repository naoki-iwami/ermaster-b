package org.insightech.er.db.impl.oracle;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.db.impl.oracle.tablespace.OracleTablespaceProperties;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.util.Check;

public class OracleDDLCreator extends DDLCreator {

	public OracleDDLCreator(ERDiagram diagram, boolean semicolon) {
		super(diagram, semicolon);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getCommentDDL(ERTable table) {
		List<String> ddlList = new ArrayList<String>();

		String tableComment = this.filterComment(table.getLogicalName(), table
				.getDescription(), false);

		if (!Check.isEmpty(tableComment)) {
			StringBuilder ddl = new StringBuilder();

			ddl.append("COMMENT ON TABLE ");
			ddl.append(filter(table.getNameWithSchema(this.getDiagram()
					.getDatabase())));
			ddl.append(" IS '");
			ddl.append(tableComment.replaceAll("'", "''"));
			ddl.append("'");
			if (this.semicolon) {
				ddl.append(";");
			}

			ddlList.add(ddl.toString());
		}

		for (Column column : table.getColumns()) {
			if (column instanceof NormalColumn) {
				NormalColumn normalColumn = (NormalColumn) column;

				String comment = this.filterComment(normalColumn
						.getLogicalName(), normalColumn.getDescription(), true);

				if (!Check.isEmpty(comment)) {
					StringBuilder ddl = new StringBuilder();

					ddl.append("COMMENT ON COLUMN ");
					ddl.append(filter(table.getNameWithSchema(this.getDiagram()
							.getDatabase())));
					ddl.append(".");
					ddl.append(filter(normalColumn.getPhysicalName()));
					ddl.append(" IS '");
					ddl.append(comment.replaceAll("'", "''"));
					ddl.append("'");
					if (this.semicolon) {
						ddl.append(";");
					}

					ddlList.add(ddl.toString());
				}

			} else {
				ColumnGroup columnGroup = (ColumnGroup) column;

				for (NormalColumn normalColumn : columnGroup.getColumns()) {
					String comment = this.filterComment(normalColumn
							.getLogicalName(), normalColumn.getDescription(),
							true);

					if (!Check.isEmpty(comment)) {
						StringBuilder ddl = new StringBuilder();

						ddl.append("COMMENT ON COLUMN ");
						ddl.append(filter(table.getNameWithSchema(this
								.getDiagram().getDatabase())));
						ddl.append(".");
						ddl.append(filter(normalColumn.getPhysicalName()));
						ddl.append(" IS '");
						ddl.append(comment.replaceAll("'", "''"));
						ddl.append("'");
						if (this.semicolon) {
							ddl.append(";");
						}

						ddlList.add(ddl.toString());
					}
				}
			}
		}

		return ddlList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDDL(Relation relation) {
		StringBuilder ddl = new StringBuilder();

		ddl.append("ALTER TABLE ");
		ddl.append(filter(relation.getTargetTableView().getNameWithSchema(
				this.getDiagram().getDatabase())));
		ddl.append("\r\n");
		ddl.append("\tADD ");
		if (relation.getName() != null && !relation.getName().trim().equals("")) {
			ddl.append("CONSTRAINT ");
			ddl.append(filter(relation.getName()));
			ddl.append(" ");
		}
		ddl.append("FOREIGN KEY (");

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

		for (NormalColumn foreignKeyColumn : relation.getForeignKeyColumns()) {
			if (!first) {
				ddl.append(", ");

			}

			for (NormalColumn referencedColumn : foreignKeyColumn
					.getReferencedColumnList()) {
				if (referencedColumn.getColumnHolder() == relation
						.getSourceTableView()) {
					ddl.append(filter(referencedColumn.getPhysicalName()));
					first = false;
					break;
				}
			}

		}

		ddl.append(")\r\n");
		if (!"RESTRICT".equalsIgnoreCase(relation.getOnDeleteAction())) {
			ddl.append("\tON DELETE ");
			ddl.append(filter(relation.getOnDeleteAction()));
			ddl.append("\r\n");
		}

		if (this.semicolon) {
			ddl.append(";");
		}

		return ddl.toString();
	}

	@Override
	protected String getDDL(Tablespace tablespace) {
		OracleTablespaceProperties tablespaceProperties = (OracleTablespaceProperties) tablespace
				.getProperties(this.environment, this.getDiagram());

		StringBuilder ddl = new StringBuilder();

		ddl.append("CREATE TABLESPACE ");
		ddl.append(filter(tablespace.getName()));
		ddl.append("\r\n");

		if (!Check.isEmpty(tablespaceProperties.getDataFile())) {
			ddl.append(" DATAFILE ");
			ddl.append(tablespaceProperties.getDataFile());

			if (!Check.isEmpty(tablespaceProperties.getFileSize())) {
				ddl.append(" SIZE ");
				ddl.append(tablespaceProperties.getFileSize());
			}

			ddl.append("\r\n");
		}

		if (tablespaceProperties.isAutoExtend()) {
			ddl.append(" AUTOEXTEND ON NEXT ");
			ddl.append(tablespaceProperties.getAutoExtendSize());

			if (!Check.isEmpty(tablespaceProperties.getAutoExtendMaxSize())) {
				ddl.append(" MAXSIZE ");
				ddl.append(tablespaceProperties.getAutoExtendMaxSize());
			}

			ddl.append("\r\n");
		}

		if (!Check.isEmpty(tablespaceProperties.getMinimumExtentSize())) {
			ddl.append(" MINIMUM EXTENT ");
			ddl.append(tablespaceProperties.getMinimumExtentSize());
			ddl.append("\r\n");
		}

		ddl.append(" DEFAULT STORAGE(\r\n");
		if (!Check.isEmpty(tablespaceProperties.getInitial())) {
			ddl.append("  INITIAL ");
			ddl.append(tablespaceProperties.getInitial());
			ddl.append("\r\n");
		}
		if (!Check.isEmpty(tablespaceProperties.getNext())) {
			ddl.append("  NEXT ");
			ddl.append(tablespaceProperties.getNext());
			ddl.append("\r\n");
		}
		if (!Check.isEmpty(tablespaceProperties.getMinExtents())) {
			ddl.append("  MINEXTENTS ");
			ddl.append(tablespaceProperties.getMinExtents());
			ddl.append("\r\n");
		}
		if (!Check.isEmpty(tablespaceProperties.getMaxExtents())) {
			ddl.append("  MAXEXTEMTS ");
			ddl.append(tablespaceProperties.getMaxExtents());
			ddl.append("\r\n");
		}
		if (!Check.isEmpty(tablespaceProperties.getPctIncrease())) {
			ddl.append("  PCTINCREASE ");
			ddl.append(tablespaceProperties.getPctIncrease());
			ddl.append("\r\n");
		}
		ddl.append(" )\r\n");

		if (tablespaceProperties.isLogging()) {
			ddl.append(" LOGGING ");
		} else {
			ddl.append(" NOLOGGING ");
		}
		ddl.append("\r\n");

		if (tablespaceProperties.isOffline()) {
			ddl.append(" OFFLINE ");
		} else {
			ddl.append(" ONLINE ");
		}
		ddl.append("\r\n");

		if (tablespaceProperties.isTemporary()) {
			ddl.append(" TEMPORARY");
		} else {
			ddl.append(" PERMANENT ");
		}
		ddl.append("\r\n");

		if (tablespaceProperties.isAutoSegmentSpaceManagement()) {
			ddl.append(" SEGMENT SPACE MANAGEMENT AUTO ");
		} else {
			ddl.append(" SEGMENT SPACE MANAGEMENT MANUAL ");
		}
		ddl.append("\r\n");

		if (this.semicolon) {
			ddl.append(";");
		}

		return ddl.toString();
	}

	@Override
	public String getDDL(Sequence sequence) {
		StringBuilder ddl = new StringBuilder();

		String description = sequence.getDescription();
		if (this.semicolon && !Check.isEmpty(description)
				&& this.ddlTarget.inlineTableComment) {
			ddl.append("-- ");
			ddl.append(description.replaceAll("\n", "\n-- "));
			ddl.append("\r\n");
		}

		ddl.append("CREATE ");
		ddl.append("SEQUENCE ");
		ddl.append(filter(this.getNameWithSchema(sequence.getSchema(), sequence
				.getName())));
		if (sequence.getIncrement() != null) {
			ddl.append(" INCREMENT BY ");
			ddl.append(sequence.getIncrement());
		}
		if (sequence.getMinValue() != null) {
			ddl.append(" MINVALUE ");
			ddl.append(sequence.getMinValue());
		}
		if (sequence.getMaxValue() != null) {
			ddl.append(" MAXVALUE ");
			ddl.append(sequence.getMaxValue());
		}
		if (sequence.getStart() != null) {
			ddl.append(" START WITH ");
			ddl.append(sequence.getStart());
		}
		if (sequence.getCache() != null) {
			ddl.append(" CACHE ");
			ddl.append(sequence.getCache());
		}
		if (sequence.isCycle()) {
			ddl.append(" CYCLE");
		}
		if (this.semicolon) {
			ddl.append(";");
		}

		return ddl.toString();
	}
}
