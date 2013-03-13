package org.insightech.er.db.impl.hsqldb;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.dbimport.DBObject;
import org.insightech.er.editor.model.dbimport.PreImportFromDBManager;

public class HSQLDBPreTableImportManager extends PreImportFromDBManager {

	@Override
	protected List<DBObject> importSequences() throws SQLException {
		List<DBObject> list = new ArrayList<DBObject>();

		ResultSet resultSet = null;
		PreparedStatement stmt = null;

		if (this.schemaList.isEmpty()) {
			this.schemaList.add(null);
		}

		for (String schemaPattern : this.schemaList) {
			try {
				if (schemaPattern == null) {
					stmt = con
							.prepareStatement("SELECT SEQUENCE_SCHEMA, SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES");

				} else {
					stmt = con
							.prepareStatement("SELECT SEQUENCE_SCHEMA, SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA = ?");
					stmt.setString(1, schemaPattern);
				}

				resultSet = stmt.executeQuery();

				while (resultSet.next()) {
					String schema = resultSet.getString("SEQUENCE_SCHEMA");
					String name = resultSet.getString("SEQUENCE_NAME");

					DBObject dbObject = new DBObject(schema, name,
							DBObject.TYPE_SEQUENCE);
					list.add(dbObject);
				}

			} finally {
				if (resultSet != null) {
					resultSet.close();
					resultSet = null;
				}
				if (stmt != null) {
					stmt.close();
					stmt = null;
				}
			}
		}

		return list;

	}
}
