package org.insightech.er.editor.model;

public class StringObjectModel implements ObjectModel {

	private String name;

	public StringObjectModel(String name) {
		this.name = name;
	}

	public String getDescription() {
		return "";
	}

	public String getName() {
		return this.name;
	}

	public String getObjectType() {
		return "other";
	}

}
