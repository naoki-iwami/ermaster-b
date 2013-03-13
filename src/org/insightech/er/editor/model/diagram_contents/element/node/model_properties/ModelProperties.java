package org.insightech.er.editor.model.diagram_contents.element.node.model_properties;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.util.NameValue;

public class ModelProperties extends NodeElement implements Cloneable {

	private static final long serialVersionUID = 5311013351131568260L;

	public static final String PROPERTY_CHANGE_MODEL_PROPERTIES = "model_properties";

	private boolean display;

	private List<NameValue> properties;

	private Date creationDate;

	private Date updatedDate;

	public ModelProperties() {
		this.creationDate = new Date();
		this.updatedDate = new Date();

		this.setLocation(new Location(50, 50, -1, -1));

		this.properties = new ArrayList<NameValue>();
	}

	public void init() {
		properties.add(new NameValue(ResourceString
				.getResourceString("label.project.name"), ""));
		properties.add(new NameValue(ResourceString
				.getResourceString("label.model.name"), ""));
		properties.add(new NameValue(ResourceString
				.getResourceString("label.version"), ""));
		properties.add(new NameValue(ResourceString
				.getResourceString("label.company.name"), ""));
		properties.add(new NameValue(ResourceString
				.getResourceString("label.author"), ""));
	}

	public void clear() {
		this.properties.clear();
	}

	public List<NameValue> getProperties() {
		return properties;
	}

	public void addProperty(NameValue property) {
		this.properties.add(property);
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;

		this.firePropertyChange(PROPERTY_CHANGE_MODEL_PROPERTIES, null, null);
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;

		this.firePropertyChange(PROPERTY_CHANGE_MODEL_PROPERTIES, null, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLocation(Location location) {
		location.width = -1;
		location.height = -1;

		super.setLocation(location);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelProperties clone() {
		ModelProperties clone = (ModelProperties) super.clone();

		List<NameValue> list = new ArrayList<NameValue>();

		for (NameValue nameValue : this.properties) {
			list.add(nameValue.clone());
		}

		clone.properties = list;

		return clone;
	}

	public void setProperties(List<NameValue> properties) {
		this.properties = properties;

		this.firePropertyChange(PROPERTY_CHANGE_MODEL_PROPERTIES, null, null);
	}

	public String getDescription() {
		return null;
	}

	public String getName() {
		return null;
	}

	public String getObjectType() {
		return "model_properties";
	}

	@Override
	public boolean needsUpdateOtherModel() {
		return false;
	}
	
}
