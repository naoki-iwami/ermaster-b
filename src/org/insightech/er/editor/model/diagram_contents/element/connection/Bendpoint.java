package org.insightech.er.editor.model.diagram_contents.element.connection;

import java.io.Serializable;

public class Bendpoint implements Serializable, Cloneable {

	private static final long serialVersionUID = -5052242525570844155L;

	private int x;

	private int y;

	private boolean relative;

	public Bendpoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isRelative() {
		return this.relative;
	}

	public void setRelative(boolean relative) {
		this.relative = relative;

		this.validate();
	}

	public void validate() {
		if (this.relative) {
			if (this.x < 20) {
				this.x = 20;

			} else if (this.x > 180) {
				this.x = 180;
			}

			if (this.y < 20) {
				this.y = 20;

			} else if (this.y > 180) {
				this.y = 180;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object clone() {
		Bendpoint clone = null;
		try {
			clone = (Bendpoint) super.clone();

		} catch (CloneNotSupportedException e) {
		}

		return clone;
	}
}
