package org.insightech.er.editor.model.dbimport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.insightech.er.util.Format;

public class DBObjectSet implements Serializable {

	private static final long serialVersionUID = 5632573903492090359L;

	private Map<String, List<DBObject>> schemaDbObjectListMap;

	private List<DBObject> tablespaceList;

	private List<DBObject> noteList;

	private List<DBObject> groupList;

	public DBObjectSet() {
		this.schemaDbObjectListMap = new TreeMap<String, List<DBObject>>();
		this.tablespaceList = new ArrayList<DBObject>();
		this.noteList = new ArrayList<DBObject>();
		this.groupList = new ArrayList<DBObject>();
	}

	public Map<String, List<DBObject>> getSchemaDbObjectListMap() {
		return schemaDbObjectListMap;
	}

	public List<DBObject> getTablespaceList() {
		return tablespaceList;
	}

	public List<DBObject> getNoteList() {
		return noteList;
	}

	public List<DBObject> getGroupList() {
		return groupList;
	}

	public void addAll(List<DBObject> dbObjectList) {
		for (DBObject dbObject : dbObjectList) {
			this.add(dbObject);
		}
	}

	public void add(DBObject dbObject) {
		if (DBObject.TYPE_TABLESPACE.equals(dbObject.getType())) {
			this.tablespaceList.add(dbObject);

		} else if (DBObject.TYPE_NOTE.equals(dbObject.getType())) {
			this.noteList.add(dbObject);

		} else if (DBObject.TYPE_GROUP.equals(dbObject.getType())) {
			this.groupList.add(dbObject);

		} else {
			String schema = Format.null2blank(dbObject.getSchema());
			List<DBObject> dbObjectList = this.schemaDbObjectListMap
					.get(schema);
			if (dbObjectList == null) {
				dbObjectList = new ArrayList<DBObject>();
				this.schemaDbObjectListMap.put(schema, dbObjectList);
			}

			dbObjectList.add(dbObject);
		}
	}

}
