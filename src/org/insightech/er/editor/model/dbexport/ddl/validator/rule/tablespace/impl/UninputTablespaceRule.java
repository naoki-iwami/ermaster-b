package org.insightech.er.editor.model.dbexport.ddl.validator.rule.tablespace.impl;

import org.eclipse.core.resources.IMarker;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.tablespace.TablespaceRule;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.insightech.er.editor.model.settings.Environment;

public class UninputTablespaceRule extends TablespaceRule {

	@Override
	public boolean validate(ERDiagram diagram, Tablespace tablespace,
			Environment environment) {
		TablespaceProperties tablespaceProperties = tablespace.getProperties(
				environment, diagram);

		for (String errorMessage : tablespaceProperties.validate()) {
			ValidateResult validateResult = new ValidateResult();
			validateResult.setMessage(ResourceString
					.getResourceString(errorMessage)
					+ this.getMessageSuffix(tablespace, environment));
			validateResult.setLocation(tablespace.getName());
			validateResult.setSeverity(IMarker.SEVERITY_WARNING);
			validateResult.setObject(tablespace);

			this.addError(validateResult);
		}

		return true;
	}

	protected String getMessageSuffix(Tablespace tablespace,
			Environment environment) {
		StringBuilder suffix = new StringBuilder();
		suffix.append(" ");
		suffix.append(ResourceString
				.getResourceString("error.tablespace.suffix.1"));
		suffix.append(tablespace.getName());
		suffix.append(ResourceString.getResourceString("error.tablespace.suffix.2"));
		suffix.append(environment.getName());

		return suffix.toString();
	}
}
