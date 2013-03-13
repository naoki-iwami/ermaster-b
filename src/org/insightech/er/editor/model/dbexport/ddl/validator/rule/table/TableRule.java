package org.insightech.er.editor.model.dbexport.ddl.validator.rule.table;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.BaseRule;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;

public abstract class TableRule extends BaseRule {

	private List<ValidateResult> errorList;

	private String database;

	public TableRule() {
		this.errorList = new ArrayList<ValidateResult>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addError(ValidateResult errorMessage) {
		this.errorList.add(errorMessage);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ValidateResult> getErrorList() {
		return this.errorList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		this.errorList.clear();
	}

	public boolean validate(ERDiagram diagram) {
		this.database = diagram.getDatabase();

		for (ERTable table : diagram.getDiagramContents().getContents()
				.getTableSet()) {
			if (!this.validate(table)) {
				return false;
			}
		}

		return true;
	}

	protected DBManager getDBManager() {
		return DBManagerFactory.getDBManager(this.database);
	}

	abstract public boolean validate(ERTable table);
}
