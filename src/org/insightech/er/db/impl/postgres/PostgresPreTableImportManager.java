package org.insightech.er.db.impl.postgres;

import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;

public class PostgresPreTableImportManager extends PreImportFromDBManager {

	@Override
	protected String getTableNameWithSchema(String schema, String tableName) {
		return this.dbSetting.getTableNameWithSchema("\"" + tableName + "\"",
				schema);
	}

}
