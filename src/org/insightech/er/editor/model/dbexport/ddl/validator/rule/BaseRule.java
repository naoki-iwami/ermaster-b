package org.insightech.er.editor.model.dbexport.ddl.validator.rule;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.Validator;

public abstract class BaseRule implements Rule {

	private List<ValidateResult> errorList;

	public BaseRule() {
		this.errorList = new ArrayList<ValidateResult>();
		Validator.addRule(this);
	}

	protected void addError(ValidateResult errorMessage) {
		this.errorList.add(errorMessage);
	}

	public List<ValidateResult> getErrorList() {
		return this.errorList;
	}

	public void clear() {
		this.errorList.clear();
	}
}
