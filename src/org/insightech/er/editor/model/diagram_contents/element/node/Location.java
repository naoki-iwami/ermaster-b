package org.insightech.er.editor.model.diagram_contents.element.node;

import java.io.Serializable;

public class Location implements Serializable, Cloneable {

	private static final long serialVersionUID = -6000221452172017444L;

	public int x;

	public int y;

	public int width;

	public int height;

	public Location(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Location clone() {
		try {
			return (Location) super.clone();

		} catch (CloneNotSupportedException ignore) {
		}

		return null;
	}

}
