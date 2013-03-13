package org.insightech.er.editor.model.dbexport.ddl.validator.rule.all;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.rule.BaseRule;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;

public class DuplicatedLogicalNameRule extends BaseRule {

	public boolean validate(ERDiagram diagram) {
		Set<String> nameSet = new HashSet<String>();

		for (ERTable table : diagram.getDiagramContents().getContents()
				.getTableSet()) {
			String name = table.getLogicalName().toLowerCase();

			if (nameSet.contains(name)) {
				ValidateResult validateResult = new ValidateResult();
				validateResult.setMessage(ResourceString
						.getResourceString("error.validate.duplicated.name")
						+ " ["
						+ table.getObjectType().toUpperCase()
						+ "] "
						+ name);
				validateResult.setLocation(table.getLogicalName());
				validateResult.setSeverity(IMarker.SEVERITY_WARNING);
				validateResult.setObject(table);

				this.addError(validateResult);

			}
			nameSet.add(name);

			for (Index index : table.getIndexes()) {
				String indexName = index.getName().toLowerCase();

				if (nameSet.contains(indexName)) {
					ValidateResult validateResult = new ValidateResult();
					validateResult
							.setMessage(ResourceString
									.getResourceString("error.validate.duplicated.name")
									+ " [INDEX] "
									+ indexName
									+ " ("
									+ table.getLogicalName() + ")");
					validateResult.setLocation(indexName);
					validateResult.setSeverity(IMarker.SEVERITY_WARNING);
					validateResult.setObject(index);

					this.addError(validateResult);
				}
				nameSet.add(indexName);
			}
		}

		for (Sequence sequence : diagram.getDiagramContents().getSequenceSet()) {
			String name = sequence.getName().toLowerCase();

			if (nameSet.contains(name)) {
				ValidateResult validateResult = new ValidateResult();
				validateResult.setMessage(ResourceString
						.getResourceString("error.validate.duplicated.name")
						+ " [SEQUENCE] " + name);
				validateResult.setLocation(name);
				validateResult.setSeverity(IMarker.SEVERITY_WARNING);
				validateResult.setObject(sequence);

				this.addError(validateResult);
			}
			nameSet.add(name);
		}

		for (View view : diagram.getDiagramContents().getContents()
				.getViewSet()) {
			String name = view.getName().toLowerCase();

			if (nameSet.contains(name)) {
				ValidateResult validateResult = new ValidateResult();
				validateResult.setMessage(ResourceString
						.getResourceString("error.validate.duplicated.name")
						+ " [VIEW] " + name);
				validateResult.setLocation(name);
				validateResult.setSeverity(IMarker.SEVERITY_WARNING);
				validateResult.setObject(view);

				this.addError(validateResult);
			}
			nameSet.add(name);
		}

		for (Trigger trigger : diagram.getDiagramContents().getTriggerSet()) {
			String name = trigger.getName().toLowerCase();

			if (nameSet.contains(name)) {
				ValidateResult validateResult = new ValidateResult();
				validateResult.setMessage(ResourceString
						.getResourceString("error.validate.duplicated.name")
						+ " [TRIGGER] " + name);
				validateResult.setLocation(name);
				validateResult.setSeverity(IMarker.SEVERITY_WARNING);
				validateResult.setObject(trigger);

				this.addError(validateResult);
			}
			nameSet.add(name);
		}

		return true;
	}
}
