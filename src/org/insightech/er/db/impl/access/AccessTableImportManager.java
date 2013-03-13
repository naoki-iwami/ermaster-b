package org.insightech.er.db.impl.access;

import org.insightech.er.editor.model.dbimport.ImportFromDBManagerBase;

public class AccessTableImportManager extends ImportFromDBManagerBase {

	@Override
	protected String getViewDefinitionSQL(String schema) {
		return null;
	}
}
