package org.insightech.er.db.impl.db2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.insightech.er.db.DBManagerBase;
import org.insightech.er.db.impl.db2.tablespace.DB2TablespaceProperties;
import org.insightech.er.db.sqltype.SqlTypeManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.dbimport.ImportFromDBManager;
import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class DB2DBManager extends DBManagerBase {

	public static final String ID = "DB2";

	public String getId() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDriverClassName() {
		return "com.ibm.db2.jcc.DB2Driver";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getURL() {
		return "jdbc:db2://<SERVER NAME>:<PORT>/<DB NAME>";
	}

	public int getDefaultPort() {
		return 50000;
	}

	public SqlTypeManager getSqlTypeManager() {
		return new DB2SqlTypeManager();
	}

	public TableProperties createTableProperties(TableProperties tableProperties) {
		if (tableProperties != null
				&& tableProperties instanceof DB2TableProperties) {
			return tableProperties;
		}

		return new DB2TableProperties();
	}

	public DDLCreator getDDLCreator(ERDiagram diagram, boolean semicolon) {
		return new DB2DDLCreator(diagram, semicolon);
	}

	public List<String> getIndexTypeList(ERTable table) {
		List<String> list = new ArrayList<String>();

		list.add("BTREE");

		return list;
	}

	@Override
	protected int[] getSupportItems() {
		return new int[] { SUPPORT_AUTO_INCREMENT, SUPPORT_SCHEMA,
				SUPPORT_SEQUENCE };
	}

	public ImportFromDBManager getTableImportManager() {
		return new DB2TableImportManager();
	}

	public PreImportFromDBManager getPreTableImportManager() {
		return new DB2PreTableImportManager();
	}

	public PreTableExportManager getPreTableExportManager() {
		return new DB2PreTableExportManager();
	}

	public TablespaceProperties createTablespaceProperties() {
		return new DB2TablespaceProperties();
	}

	public TablespaceProperties checkTablespaceProperties(
			TablespaceProperties tablespaceProperties) {

		if (!(tablespaceProperties instanceof DB2TablespaceProperties)) {
			return new DB2TablespaceProperties();
		}

		return tablespaceProperties;
	}

	public String[] getCurrentTimeValue() {
		return new String[] { "CURRENT TIMESTAMP" };
	}

	@Override
	public List<String> getSystemSchemaList() {
		List<String> list = new ArrayList<String>();

		list.add("nullid");
		list.add("sqlj");
		list.add("syscat");
		list.add("sysfun");
		list.add("sysibm");
		list.add("sysibmadm");
		list.add("sysibminternal");
		list.add("sysibmts");
		list.add("sysproc");
		list.add("syspublic");
		list.add("sysstat");
		list.add("systools");

		return list;
	}

	public BigDecimal getSequenceMaxValue() {
		return null;
	}

}
