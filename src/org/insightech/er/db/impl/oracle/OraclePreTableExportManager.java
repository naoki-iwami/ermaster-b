package org.insightech.er.db.impl.oracle;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.insightech.er.editor.model.dbexport.db.PreTableExportManager;

public class OraclePreTableExportManager extends PreTableExportManager {

	@Override
	protected void checkTableExist(Connection con, String tableNameWithSchema)
			throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT 1 FROM " + tableNameWithSchema);

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}

		}
	}
}
