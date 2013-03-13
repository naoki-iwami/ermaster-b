package org.insightech.er.editor.model.dbexport.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.dbexport.ddl.DDLTarget;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.settings.DBSetting;
import org.insightech.er.editor.model.settings.Environment;

public abstract class PreTableExportManager {

	protected Connection con;

	protected DatabaseMetaData metaData;

	protected DBSetting dbSetting;

	private ERDiagram diagram;

	private Exception exception;

	private String errorSql;

	private String ddl;

	private Environment environment;

	private String ifExistsOption;

	private Set<String> newViewNames;

	protected Set<String> newTableNames;

	private Set<String> newSequenceNames;

	public void init(Connection con, DBSetting dbSetting, ERDiagram diagram,
			Environment environment) throws SQLException {
		this.con = con;
		this.dbSetting = dbSetting;
		this.diagram = diagram;
		this.environment = environment;

		this.metaData = con.getMetaData();

		this.ifExistsOption = DBManagerFactory.getDBManager(this.diagram)
				.getDDLCreator(this.diagram, false).getIfExistsOption();

		this.prepareNewNames();
	}

	protected void prepareNewNames() {
		this.newTableNames = new HashSet<String>();

		for (ERTable table : this.diagram.getDiagramContents().getContents()
				.getTableSet()) {
			this.newTableNames.add(this.dbSetting.getTableNameWithSchema(table
					.getPhysicalName(), table.getTableViewProperties()
					.getSchema()));
		}

		this.newViewNames = new HashSet<String>();

		for (View view : this.diagram.getDiagramContents().getContents()
				.getViewSet()) {
			this.newViewNames.add(this.dbSetting.getTableNameWithSchema(view
					.getPhysicalName(), view.getTableViewProperties()
					.getSchema()));
		}

		this.newSequenceNames = new HashSet<String>();

		for (Sequence sequence : this.diagram.getDiagramContents()
				.getSequenceSet()) {
			this.newSequenceNames.add(this.dbSetting.getTableNameWithSchema(
					sequence.getName(), sequence.getSchema()));
		}
	}

	public void run() {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(this.dropViews());
			sb.append(this.dropForeignKeys());
			sb.append(this.dropTables());
			sb.append(this.dropSequences());

			sb.append(this.executeDDL());

			this.ddl = sb.toString();

		} catch (Exception e) {
			this.exception = e;
		}
	}

	private String dropSequences() throws SQLException {
		StringBuilder ddl = new StringBuilder();

		ResultSet sequenceSet = null;

		try {
			sequenceSet = metaData.getTables(null, null, null,
					new String[] { "SEQUENCE" });

			while (sequenceSet.next()) {
				String name = sequenceSet.getString("TABLE_NAME");
				String schema = sequenceSet.getString("TABLE_SCHEM");
				name = this.dbSetting.getTableNameWithSchema(name, schema);

				if (this.newSequenceNames == null
						|| this.newSequenceNames.contains(name)) {
					ddl.append(this.dropSequence(name));
					ddl.append("\r\n");
				}
			}

		} finally {
			if (sequenceSet != null) {
				sequenceSet.close();
			}

		}

		return ddl.toString();
	}

	private String dropSequence(String sequenceName) throws SQLException {
		String sql = "DROP SEQUENCE " + this.ifExistsOption + sequenceName
				+ ";";

		return sql;
	}

	private String dropViews() throws SQLException {
		StringBuilder ddl = new StringBuilder();

		ResultSet viewSet = null;

		try {
			viewSet = metaData.getTables(null, null, null,
					new String[] { "VIEW" });

			while (viewSet.next()) {
				String name = viewSet.getString("TABLE_NAME");
				String schema = viewSet.getString("TABLE_SCHEM");
				name = this.dbSetting.getTableNameWithSchema(name, schema);

				if (this.newViewNames == null
						|| this.newViewNames.contains(name)) {
					ddl.append(this.dropView(name));
					ddl.append("\r\n");
				}
			}

		} finally {
			if (viewSet != null) {
				viewSet.close();
			}

		}

		return ddl.toString();
	}

	private String dropView(String viewName) throws SQLException {
		String sql = "DROP VIEW " + this.ifExistsOption + viewName + ";";

		return sql;
	}

	protected String dropForeignKeys() throws SQLException {
		StringBuilder ddl = new StringBuilder();

		ResultSet foreignKeySet = null;

		try {
			foreignKeySet = metaData.getImportedKeys(null, null, null);

			Set<String> fkNameSet = new HashSet<String>();

			while (foreignKeySet.next()) {
				String constraintName = foreignKeySet.getString("FK_NAME");
				if (fkNameSet.contains(constraintName)) {
					continue;
				}
				fkNameSet.add(constraintName);

				String tableName = foreignKeySet.getString("FKTABLE_NAME");
				String schema = foreignKeySet.getString("FKTABLE_SCHEM");

				tableName = this.dbSetting.getTableNameWithSchema(tableName,
						schema);

				if (this.newTableNames == null
						|| this.newTableNames.contains(tableName)) {
					ddl.append(this.dropForeignKey(tableName, constraintName));
					ddl.append("\r\n");
				}
			}

		} finally {
			if (foreignKeySet != null) {
				foreignKeySet.close();
			}
		}

		return ddl.toString();
	}

	private String dropForeignKey(String tableName, String constraintName)
			throws SQLException {
		String sql = "ALTER TABLE " + tableName + " DROP CONSTRAINT "
				+ constraintName + ";";

		return sql;
	}

	private String dropTables() throws SQLException, InterruptedException {
		StringBuilder ddl = new StringBuilder();

		ResultSet tableSet = null;

		try {
			tableSet = metaData.getTables(null, null, null,
					new String[] { "TABLE" });

			while (tableSet.next()) {
				String tableName = tableSet.getString("TABLE_NAME");
				String schema = tableSet.getString("TABLE_SCHEM");
				tableName = this.dbSetting.getTableNameWithSchema(tableName,
						schema);

				if (this.newTableNames == null
						|| this.newTableNames.contains(tableName)) {
					try {
						this.checkTableExist(con, tableName);
					} catch (SQLException e) {
						// テーブル情報が取得できない場合（他のユーザの所有物などの場合）、
						// このテーブルは使用しない。
						continue;
					}

					ddl.append(this.dropTable(tableName));
					ddl.append("\r\n");
				}
			}

		} finally {
			if (tableSet != null) {
				tableSet.close();
			}
		}

		return ddl.toString();
	}

	private String dropTable(String tableName) throws SQLException {
		String sql = "DROP TABLE " + this.ifExistsOption + tableName + ";";

		return sql;
	}

	private String executeDDL() throws SQLException {
		DDLCreator ddlCreator = DBManagerFactory.getDBManager(this.diagram)
				.getDDLCreator(this.diagram, true);
		ddlCreator.init(this.environment, new DDLTarget());

		return ddlCreator.getCreateDDL(this.diagram);
	}

	protected void checkTableExist(Connection con, String tableNameWithSchema)
			throws SQLException {
	}

	public Exception getException() {
		return exception;
	}

	/**
	 * errorSql を取得します.
	 * 
	 * @return errorSql
	 */
	public String getErrorSql() {
		return errorSql;
	}

	/**
	 * ddl を取得します.
	 * 
	 * @return ddl
	 */
	public String getDdl() {
		return ddl;
	}

}
