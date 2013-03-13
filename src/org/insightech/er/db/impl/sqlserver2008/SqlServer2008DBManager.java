package org.insightech.er.db.impl.sqlserver2008;

import org.insightech.er.db.impl.sqlserver.SqlServerDBManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;

public class SqlServer2008DBManager extends SqlServerDBManager {

	public static final String ID = "SQLServer 2008";

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public DDLCreator getDDLCreator(ERDiagram diagram, boolean semicolon) {
		return new SqlServer2008DDLCreator(diagram, semicolon);
	}

}
