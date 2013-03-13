package org.insightech.er.db.impl.standard_sql;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;

public class StandardSQLDDLCreator extends DDLCreator {

	public StandardSQLDDLCreator(ERDiagram diagram, boolean semicolon) {
		super(diagram, semicolon);
	}

	@Override
	protected String getDDL(Tablespace tablespace) {
		return null;
	}
}
