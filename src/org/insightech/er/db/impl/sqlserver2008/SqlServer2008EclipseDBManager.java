package org.insightech.er.db.impl.sqlserver2008;

import org.insightech.er.db.impl.sqlserver.SqlServerEclipseDBManager;

public class SqlServer2008EclipseDBManager extends SqlServerEclipseDBManager {

	@Override
	public String getId() {
		return SqlServer2008DBManager.ID;
	}

}
