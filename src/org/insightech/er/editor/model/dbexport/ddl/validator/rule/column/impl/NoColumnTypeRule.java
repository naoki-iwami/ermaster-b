package org.insightech.er.editor.model.dbexport.ddl.validator.rule.column.impl;

import org.eclipse.core.resources.IMarker;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.column.ColumnRule;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;

public class NoColumnTypeRule extends ColumnRule {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validate(ERTable table, NormalColumn column) {
		if (column.getType() == null
				|| column.getType().getAlias(table.getDiagram().getDatabase()) == null) {
			ValidateResult validateResult = new ValidateResult();
			validateResult
					.setMessage(ResourceString
							.getResourceString("error.validate.no.column.type1")
							+ table.getPhysicalName()
							+ ResourceString
									.getResourceString("error.validate.no.column.type2")
							+ column.getPhysicalName());
			validateResult.setLocation(table.getLogicalName());
			validateResult.setSeverity(IMarker.SEVERITY_WARNING);
			validateResult.setObject(table);

			this.addError(validateResult);
		}

		return true;
	}
}
