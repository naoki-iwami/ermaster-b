package org.insightech.er.db.impl.postgres.tablespace;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.insightech.er.util.Check;

public class PostgresTablespaceProperties implements TablespaceProperties {

	private static final long serialVersionUID = -1168759105844875794L;

	private String location;

	private String owner;

	/**
	 * location を取得します.
	 * 
	 * @return location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * location を設定します.
	 * 
	 * @param location
	 *            location
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * owner を取得します.
	 * 
	 * @return owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * owner を設定します.
	 * 
	 * @param owner
	 *            owner
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Override
	public TablespaceProperties clone() {
		PostgresTablespaceProperties properties = new PostgresTablespaceProperties();

		properties.location = this.location;
		properties.owner = this.owner;

		return properties;
	}

	public LinkedHashMap<String, String> getPropertiesMap() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		map.put("label.tablespace.location", this.getLocation());
		map.put("label.tablespace.owner", this.getOwner());

		return map;
	}

	public List<String> validate() {
		List<String> errorMessage = new ArrayList<String>();

		if (Check.isEmptyTrim(this.getLocation())) {
			errorMessage.add("error.tablespace.location.empty");
		}

		return errorMessage;
	}
}
