package org.insightech.er.db.impl.standard_sql;

import org.insightech.er.editor.model.dbimport.ImportFromDBManagerBase;

public class StandardSQLTableImportManager extends ImportFromDBManagerBase {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getViewDefinitionSQL(String schema) {
		return null;
	}
}
