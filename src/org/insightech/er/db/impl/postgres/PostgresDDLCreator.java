package org.insightech.er.db.impl.postgres;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.insightech.er.db.impl.postgres.tablespace.PostgresTablespaceProperties;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.util.Check;

public class PostgresDDLCreator extends DDLCreator {

	private static final Pattern DROP_TRIGGER_TABLE_PATTERN = Pattern
			.compile(".*\\s[oO][nN]\\s+(.+)\\s.*");

	public PostgresDDLCreator(ERDiagram diagram, boolean semicolon) {
		super(diagram, semicolon);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPostDDL(ERTable table) {
		PostgresTableProperties commonTableProperties = (PostgresTableProperties) this
				.getDiagram().getDiagramContents().getSettings()
				.getTableViewProperties();

		PostgresTableProperties tableProperties = (PostgresTableProperties) table
				.getTableViewProperties();

		boolean isWithoutOIDs = tableProperties.isWithoutOIDs();
		if (!isWithoutOIDs) {
			isWithoutOIDs = commonTableProperties.isWithoutOIDs();
		}

		StringBuilder postDDL = new StringBuilder();

		if (isWithoutOIDs) {
			postDDL.append(" WITHOUT OIDS");
		}

		postDDL.append(super.getPostDDL(table));

		return postDDL.toString();
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

	@Override
	protected String getDDL(Tablespace tablespace) {
		PostgresTablespaceProperties tablespaceProperties = (PostgresTablespaceProperties) tablespace
				.getProperties(this.environment, this.getDiagram());

		StringBuilder ddl = new StringBuilder();

		ddl.append("CREATE TABLESPACE ");
		ddl.append(filter(tablespace.getName()));
		ddl.append("\r\n");

		if (!Check.isEmpty(tablespaceProperties.getOwner())) {
			ddl.append(" OWNER ");
			ddl.append(tablespaceProperties.getOwner());
			ddl.append("\r\n");
		}

		ddl.append(" LOCATION '");
		ddl.append(tablespaceProperties.getLocation());
		ddl.append("'\r\n");

		if (this.semicolon) {
			ddl.append(";");
		}

		return ddl.toString();
	}

	private String getAutoIncrementSettingDDL(ERTable table, NormalColumn column) {
		StringBuilder ddl = new StringBuilder();

		Sequence sequence = column.getAutoIncrementSetting();

		if (sequence.getIncrement() != null || sequence.getMinValue() != null
				|| sequence.getMaxValue() != null
				|| sequence.getStart() != null || sequence.getCache() != null
				|| sequence.isCycle()) {

			ddl.append("ALTER SEQUENCE ");
			ddl.append(filter(table.getNameWithSchema(this.getDiagram()
					.getDatabase())
					+ "_" + column.getPhysicalName() + "_SEQ"));

			if (sequence.getIncrement() != null) {
				ddl.append(" INCREMENT ");
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
				ddl.append(" START ");
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
		}

		return ddl.toString();
	}

	@Override
	protected String getTableSettingDDL(ERTable table) {
		StringBuilder ddl = new StringBuilder();

		boolean first = true;

		for (NormalColumn column : table.getNormalColumns()) {
			if (SqlType.SQL_TYPE_ID_SERIAL.equals(column.getType().getId())
					|| SqlType.SQL_TYPE_ID_BIG_SERIAL.equals(column.getType()
							.getId())) {
				String autoIncrementSettingDDL = getAutoIncrementSettingDDL(
						table, column);
				if (!Check.isEmpty(autoIncrementSettingDDL)) {
					ddl.append(autoIncrementSettingDDL);
					ddl.append("\r\n");
					first = false;
				}
			}
		}

		if (!first) {
			ddl.append("\r\n");
			ddl.append("\r\n");
		}

		return ddl.toString();
	}

	@Override
	public String getDropDDL(Trigger trigger) {
		StringBuilder ddl = new StringBuilder();

		ddl.append("DROP TRIGGER ");
		ddl.append(this.getIfExistsOption());
		ddl.append(filter(trigger.getName()));
		ddl.append(" ON ");

		Matcher matcher = DROP_TRIGGER_TABLE_PATTERN.matcher(trigger.getSql());
		if (matcher.find()) {
			ddl.append(matcher.group(1));
		}

		if (this.semicolon) {
			ddl.append(";");
		}

		return ddl.toString();
	}

	@Override
	public String getIfExistsOption() {
		return "IF EXISTS ";
	}
}
