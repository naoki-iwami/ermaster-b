package org.insightech.er.editor.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public abstract class AbstractModel implements Serializable, Cloneable {

	private static final long serialVersionUID = 4266969076408212298L;

	private PropertyChangeSupport support;

	public AbstractModel() {
		this.support = new PropertyChangeSupport(this);
	}

	protected void firePropertyChange(String name, Object oldValue,
			Object newValue) {
		this.support.firePropertyChange(name, oldValue, newValue);
	}

	protected void firePropertyChange(String name, int oldValue, int newValue) {
		this.support.firePropertyChange(name, oldValue, newValue);
	}

	protected void firePropertyChange(String name, boolean oldValue,
			boolean newValue) {
		this.support.firePropertyChange(name, oldValue, newValue);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.support.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.support.removePropertyChangeListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractModel clone() {
		AbstractModel clone = null;
		try {
			clone = (AbstractModel) super.clone();

			clone.support = new PropertyChangeSupport(clone);

		} catch (CloneNotSupportedException e) {
		}

		return clone;
	}
}
