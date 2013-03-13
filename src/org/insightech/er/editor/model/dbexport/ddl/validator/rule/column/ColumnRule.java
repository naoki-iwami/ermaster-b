package org.insightech.er.editor.model.dbexport.ddl.validator.rule.column;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.table.TableRule;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;

public abstract class ColumnRule extends TableRule {

	private List<ValidateResult> errorList;

	public ColumnRule() {
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validate(ERTable table) {
		for (Column column : table.getColumns()) {
			if (column instanceof NormalColumn) {
				NormalColumn normalColumn = (NormalColumn) column;

				if (!this.validate(table, normalColumn)) {
					return false;
				}

			} else {
				ColumnGroup columnGroup = (ColumnGroup) column;

				for (NormalColumn normalColumn : columnGroup.getColumns()) {
					if (!this.validate(table, normalColumn)) {
						return false;
					}
				}
			}
		}

		return true;
	}

	abstract public boolean validate(ERTable table, NormalColumn column);
}
