package org.insightech.er.db.impl.mysql;

import java.sql.SQLException;

import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;

public class MySQLPreTableExportManager extends PreTableExportManager {

	@Override
	protected String dropForeignKeys() throws SQLException {
		StringBuilder ddl = new StringBuilder();
		ddl.append("SET SESSION FOREIGN_KEY_CHECKS=0;\r\n\r\n");

		return ddl.toString();
	}

	@Override
	protected void prepareNewNames() {
	}

}
