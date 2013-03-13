package org.insightech.er.editor.model.settings;

import java.io.Serializable;

public class Environment implements Serializable, Cloneable {

	private static final long serialVersionUID = 2894497911334351672L;

	private String name;

	public Environment(String name) {
		this.name = name;
	}

	/**
	 * name ‚ğæ“¾‚µ‚Ü‚·.
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * name ‚ğİ’è‚µ‚Ü‚·.
	 * 
	 * @param name
	 *            name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Environment clone() {
		try {
			Environment environment = (Environment) super.clone();

			return environment;

		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}
