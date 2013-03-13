package org.insightech.er.editor.persistent.impl;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.insightech.er.ResourceString;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.db.impl.db2.DB2DBManager;
import org.insightech.er.db.impl.db2.tablespace.DB2TablespaceProperties;
import org.insightech.er.db.impl.mysql.MySQLDBManager;
import org.insightech.er.db.impl.mysql.MySQLTableProperties;
import org.insightech.er.db.impl.mysql.tablespace.MySQLTablespaceProperties;
import org.insightech.er.db.impl.oracle.OracleDBManager;
import org.insightech.er.db.impl.oracle.tablespace.OracleTablespaceProperties;
import org.insightech.er.db.impl.postgres.PostgresDBManager;
import org.insightech.er.db.impl.postgres.PostgresTableProperties;
import org.insightech.er.db.impl.postgres.tablespace.PostgresTablespaceProperties;
import org.insightech.er.db.impl.standard_sql.StandardSQLDBManager;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ViewableModel;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.CommentConnection;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.VGroup;
import org.insightech.er.editor.model.diagram_contents.element.node.image.InsertedImage;
import org.insightech.er.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.note.Note;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERVirtualTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.index.Index;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.element.node.view.properties.ViewProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.UniqueWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.SequenceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceSet;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.TriggerSet;
import org.insightech.er.editor.model.settings.CategorySetting;
import org.insightech.er.editor.model.settings.DBSetting;
import org.insightech.er.editor.model.settings.Environment;
import org.insightech.er.editor.model.settings.EnvironmentSetting;
import org.insightech.er.editor.model.settings.ExportSetting;
import org.insightech.er.editor.model.settings.PageSetting;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.settings.TranslationSetting;
import org.insightech.er.editor.model.settings.export.ExportJavaSetting;
import org.insightech.er.editor.model.settings.export.ExportTestDataSetting;
import org.insightech.er.editor.model.testdata.DirectTestData;
import org.insightech.er.editor.model.testdata.RepeatTestData;
import org.insightech.er.editor.model.testdata.RepeatTestDataDef;
import org.insightech.er.editor.model.testdata.TableTestData;
import org.insightech.er.editor.model.testdata.TestData;
import org.insightech.er.editor.model.tracking.ChangeTracking;
import org.insightech.er.editor.model.tracking.ChangeTrackingList;
import org.insightech.er.util.Format;
import org.insightech.er.util.NameValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLLoader {

	private ERDiagram diagram;

	private String database;

	private class LoadContext {
		private Map<String, NodeElement> nodeElementMap;

		private Map<String, NormalColumn> columnMap;

		private Map<String, ComplexUniqueKey> complexUniqueKeyMap;

		private Map<NormalColumn, String[]> columnRelationMap;

		private Map<NormalColumn, String[]> columnReferencedColumnMap;

		private Map<String, ColumnGroup> columnGroupMap;

		private Map<String, ERModel> ermodelMap;

		private Map<Relation, String> referencedColumnMap;

		private Map<Relation, String> referencedComplexUniqueKeyMap;

		private Map<ConnectionElement, String> connectionSourceMap;

		private Map<ConnectionElement, String> connectionTargetMap;

		private Map<String, ConnectionElement> connectionMap;

		private Map<String, Word> wordMap;

		private Map<String, Tablespace> tablespaceMap;

		private Map<String, Environment> environmentMap;

		private Map<UniqueWord, Word> uniqueWordMap;

		private Dictionary dictionary;

		private LoadContext(Dictionary dictionary) {
			this.nodeElementMap = new HashMap<String, NodeElement>();
			this.columnMap = new HashMap<String, NormalColumn>();
			this.complexUniqueKeyMap = new HashMap<String, ComplexUniqueKey>();
			this.columnRelationMap = new HashMap<NormalColumn, String[]>();
			this.columnReferencedColumnMap = new HashMap<NormalColumn, String[]>();
			this.ermodelMap = new HashMap<String, ERModel>();
			this.columnGroupMap = new HashMap<String, ColumnGroup>();
			this.referencedColumnMap = new HashMap<Relation, String>();
			this.referencedComplexUniqueKeyMap = new HashMap<Relation, String>();
			this.connectionMap = new HashMap<String, ConnectionElement>();
			this.connectionSourceMap = new HashMap<ConnectionElement, String>();
			this.connectionTargetMap = new HashMap<ConnectionElement, String>();
			this.wordMap = new HashMap<String, Word>();
			this.tablespaceMap = new HashMap<String, Tablespace>();
			this.environmentMap = new HashMap<String, Environment>();
			this.uniqueWordMap = new HashMap<UniqueWord, Word>();

			this.dictionary = dictionary;
			this.dictionary.clear();
		}

		private void resolve() {
			for (ConnectionElement connection : this.connectionSourceMap
					.keySet()) {
				String id = this.connectionSourceMap.get(connection);

				NodeElement nodeElement = this.nodeElementMap.get(id);
				if (nodeElement == null) {
					System.out.println("error");
				}
				connection.setSource(nodeElement);
			}

			for (ConnectionElement connection : this.connectionTargetMap
					.keySet()) {
				String id = this.connectionTargetMap.get(connection);

				NodeElement nodeElement = this.nodeElementMap.get(id);
				if (nodeElement == null) {
					System.out.println("error");
				}
				connection.setTarget(nodeElement);
			}

			for (Relation relation : this.referencedColumnMap.keySet()) {
				String id = this.referencedColumnMap.get(relation);

				NormalColumn column = this.columnMap.get(id);
				if (column == null) {
					System.out.println("error");
				}
				relation.setReferencedColumn(column);
			}

			for (Relation relation : this.referencedComplexUniqueKeyMap
					.keySet()) {
				String id = this.referencedComplexUniqueKeyMap.get(relation);

				ComplexUniqueKey complexUniqueKey = this.complexUniqueKeyMap
						.get(id);
				relation.setReferencedComplexUniqueKey(complexUniqueKey);
			}

			Set<NormalColumn> foreignKeyColumnSet = this.columnReferencedColumnMap
					.keySet();

			while (!foreignKeyColumnSet.isEmpty()) {
				NormalColumn foreignKeyColumn = foreignKeyColumnSet.iterator()
						.next();
				reduce(foreignKeyColumnSet, foreignKeyColumn);
			}
		}

		private void reduce(Set<NormalColumn> foreignKeyColumnSet,
				NormalColumn foreignKeyColumn) {
			String[] referencedColumnIds = this.columnReferencedColumnMap
					.get(foreignKeyColumn);
			String[] relationIds = this.columnRelationMap.get(foreignKeyColumn);

			List<NormalColumn> referencedColumnList = new ArrayList<NormalColumn>();

			if (referencedColumnIds != null) {
				for (String referencedColumnId : referencedColumnIds) {
					try {
						Integer.parseInt(referencedColumnId);

						NormalColumn referencedColumn = this.columnMap
								.get(referencedColumnId);
						referencedColumnList.add(referencedColumn);

						if (foreignKeyColumnSet.contains(referencedColumn)) {
							reduce(foreignKeyColumnSet, referencedColumn);
						}

					} catch (NumberFormatException e) {
					}
				}
			}

			if (relationIds != null) {
				for (String relationId : relationIds) {
					try {
						Integer.parseInt(relationId);

						Relation relation = (Relation) this.connectionMap
								.get(relationId);
						for (NormalColumn referencedColumn : referencedColumnList) {
							if (referencedColumn.getColumnHolder() == relation
									.getSourceTableView()) {
								foreignKeyColumn.addReference(referencedColumn,
										relation);
								break;
							}
						}

					} catch (NumberFormatException e) {
					}
				}
			}
			foreignKeyColumnSet.remove(foreignKeyColumn);
		}
	}

	public ERDiagram load(InputStream in) throws Exception {
		DocumentBuilder parser = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();

		Document document = parser.parse(in);

		Node root = document.getFirstChild();

		while (root.getNodeType() == Node.COMMENT_NODE) {
			document.removeChild(root);
			root = document.getFirstChild();
		}

		load((Element) root);

		return this.diagram;
	}

	private String getStringValue(Element element, String tagname) {
		NodeList nodeList = element.getElementsByTagName(tagname);

		if (nodeList.getLength() == 0) {
			return null;
		}

		Node node = nodeList.item(0);

		if (node.getFirstChild() == null) {
			return "";
		}

		return node.getFirstChild().getNodeValue();
	}

	private String[] getTagValues(Element element, String tagname) {
		NodeList nodeList = element.getElementsByTagName(tagname);

		String[] values = new String[nodeList.getLength()];

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getFirstChild() != null) {
				values[i] = node.getFirstChild().getNodeValue();
			}
		}

		return values;
	}

	private boolean getBooleanValue(Element element, String tagname) {
		return getBooleanValue(element, tagname, false);
	}

	private boolean getBooleanValue(Element element, String tagname,
			boolean defaultValue) {
		NodeList nodeList = element.getElementsByTagName(tagname);

		if (nodeList.getLength() == 0) {
			return defaultValue;
		}

		Node node = nodeList.item(0);

		String value = node.getFirstChild().getNodeValue();

		return Boolean.valueOf(value).booleanValue();
	}

	private int getIntValue(Element element, String tagname) {
		return getIntValue(element, tagname, 0);
	}

	private int getIntValue(Element element, String tagname, int defaultValue) {
		NodeList nodeList = element.getElementsByTagName(tagname);

		if (nodeList.getLength() == 0) {
			return defaultValue;
		}

		Node node = nodeList.item(0);

		if (node.getFirstChild() == null) {
			return defaultValue;
		}

		String value = node.getFirstChild().getNodeValue();

		return Integer.valueOf(value).intValue();
	}

	private Integer getIntegerValue(Element element, String tagname) {
		NodeList nodeList = element.getElementsByTagName(tagname);

		if (nodeList.getLength() == 0) {
			return null;
		}

		Node node = nodeList.item(0);

		if (node.getFirstChild() == null) {
			return null;
		}

		String value = node.getFirstChild().getNodeValue();

		try {
			return Integer.valueOf(value);

		} catch (NumberFormatException e) {
			return null;
		}
	}

	private Long getLongValue(Element element, String tagname) {
		NodeList nodeList = element.getElementsByTagName(tagname);

		if (nodeList.getLength() == 0) {
			return null;
		}

		Node node = nodeList.item(0);

		if (node.getFirstChild() == null) {
			return null;
		}

		String value = node.getFirstChild().getNodeValue();

		try {
			return Long.valueOf(value);

		} catch (NumberFormatException e) {
			return null;
		}
	}

	private BigDecimal getBigDecimalValue(Element element, String tagname) {
		String value = this.getStringValue(element, tagname);

		try {
			return new BigDecimal(value);
		} catch (Exception e) {
		}

		return null;
	}

	private double getDoubleValue(Element element, String tagname) {
		NodeList nodeList = element.getElementsByTagName(tagname);

		if (nodeList.getLength() == 0) {
			return 0;
		}

		Node node = nodeList.item(0);

		if (node.getFirstChild() == null) {
			return 0;
		}

		String value = node.getFirstChild().getNodeValue();

		return Double.valueOf(value).doubleValue();
	}

	private Date getDateValue(Element element, String tagname) {
		NodeList nodeList = element.getElementsByTagName(tagname);

		if (nodeList.getLength() == 0) {
			return null;
		}

		Node node = nodeList.item(0);

		if (node.getFirstChild() == null) {
			return null;
		}

		String value = node.getFirstChild().getNodeValue();

		try {
			return PersistentXmlImpl.DATE_FORMAT.parse(value);

		} catch (ParseException e) {
			return null;
		}
	}

	private Element getElement(Element element, String tagname) {
		NodeList nodeList = element.getChildNodes();

		if (nodeList.getLength() == 0) {
			return null;
		}

		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element ele = (Element) nodeList.item(i);
				if (ele.getTagName().equals(tagname)) {
					return ele;
				}
			}
		}

		return null;
	}

	private void load(Element root) {
		Element settings = this.getElement(root, "settings");
		this.database = this.loadDatabase(settings);

		this.diagram = new ERDiagram(this.database);

		this.loadDBSetting(this.diagram, root);
		this.loadPageSetting(this.diagram, root);

		this.loadColor(this.diagram, root);
		this.loadDefaultColor(this.diagram, root);
		this.loadFont(this.diagram, root);

		DiagramContents diagramContents = this.diagram.getDiagramContents();
		this.loadDiagramContents(diagramContents, root);

		this.diagram.setCurrentCategory(null, this.getIntValue(root, "category_index"));
		this.diagram.setCurrentErmodel(null, this.getStringValue(root, "current_ermodel"));

		double zoom = this.getDoubleValue(root, "zoom");
		this.diagram.setZoom(zoom);

		int x = this.getIntValue(root, "x");
		int y = this.getIntValue(root, "y");
		this.diagram.setLocation(x, y);

		this.loadChangeTrackingList(this.diagram.getChangeTrackingList(), root);

		this.diagram.getDiagramContents().getSettings().getTranslationSetting()
				.load();
	}

	private String loadDatabase(Element settingsElement) {
		String database = this.getStringValue(settingsElement, "database");
		if (database == null) {
			database = DBManagerFactory.getAllDBList().get(0);
		}

		return database;
	}

	private void loadDiagramContents(DiagramContents diagramContents,
			Element parent) {
		Dictionary dictionary = diagramContents.getDictionary();

		LoadContext context = new LoadContext(dictionary);

		this.loadDictionary(dictionary, parent, context);

		Settings settings = diagramContents.getSettings();
		this.loadEnvironmentSetting(settings.getEnvironmentSetting(), parent,
				context);

		this.loadTablespaceSet(diagramContents.getTablespaceSet(), parent,
				context);

		GroupSet columnGroups = diagramContents.getGroups();
		columnGroups.clear();

		this.loadColumnGroups(columnGroups, parent, context);
		this.loadContents(diagramContents.getContents(), parent, context);
		diagramContents.getModelSet().addModels(this.loadErmodels(parent, context));
		this.loadTestDataList(diagramContents.getTestDataList(), parent,
				context);
		this.loadSequenceSet(diagramContents.getSequenceSet(), parent);
		this.loadTriggerSet(diagramContents.getTriggerSet(), parent);

		this.loadSettings(settings, parent, context);

		context.resolve();
	}

	private List<ERModel> loadErmodels(Element parent, LoadContext context) {
		List<ERModel> results = new ArrayList<ERModel>();
		
		Element element = this.getElement(parent, "ermodels");

		if (element != null) {
			NodeList nodeList = element.getElementsByTagName("ermodel");

			for (int i = 0; i < nodeList.getLength(); i++) {
				Element modelElement = (Element) nodeList.item(i);

				ERModel model = new ERModel(diagram);
				model.setName(getStringValue(modelElement, "name"));
				loadColor(model, modelElement);

				List<ERVirtualTable> tables = new ArrayList<ERVirtualTable>();
				Element vtables = getElement(modelElement, "vtables");
				if (vtables != null) {
					NodeList tableEls = vtables.getElementsByTagName("vtable");
					for (int k = 0; k < tableEls.getLength(); k++) {
						Element tableElement = (Element) tableEls.item(k);
						tables.add(loadVirtualTable(model, tableElement, context));
					}
				}
				model.setTables(tables);

				List<Note> notes = new ArrayList<Note>();
				Element elNotes = getElement(modelElement, "notes");
				if (elNotes != null) {
					NodeList noteEls = elNotes.getElementsByTagName("note");
					for (int k = 0; k < noteEls.getLength(); k++) {
						Element noteElement = (Element) noteEls.item(k);
						Note note = loadNote(model, noteElement, context);
						
						String id = this.getStringValue(noteElement, "id");
						context.nodeElementMap.put(id, note);
						notes.add(note);
						diagram.getDiagramContents().getContents().addNodeElement(note);
					}
				}
				model.setNotes(notes);
				
				List<VGroup> groups = new ArrayList<VGroup>();
				Element elGroups = getElement(modelElement, "groups");
				if (elGroups != null) {
					NodeList groupEls = elGroups.getElementsByTagName("group");
					for (int k = 0; k < groupEls.getLength(); k++) {
						Element groupElement = (Element) groupEls.item(k);
						VGroup group = loadGroup(model, groupElement, context);
						
						String id = this.getStringValue(groupElement, "id");
						context.nodeElementMap.put(id, group);
						groups.add(group);
					}
				}
				model.setGroups(groups);

				String id = this.getStringValue(modelElement, "id");
				context.ermodelMap.put(id, model);
				results.add(model);
			}
		}
		return results;
	}

	private ERModel loadErmodel(Element parent, LoadContext context) {
		ERModel model = new ERModel(diagram);
		model.setName(getStringValue(parent, "name"));
		List<ERVirtualTable> tables = new ArrayList<ERVirtualTable>();

		Element vtables = getElement(parent, "vtables");
		if (vtables != null) {
			NodeList tableEls = vtables.getElementsByTagName("vtable");
			for (int k = 0; k < tableEls.getLength(); k++) {
				Element tableElement = (Element) tableEls.item(k);
				tables.add(loadVirtualTable(model, tableElement, context));
			}
		}
		model.setTables(tables);

		String id = this.getStringValue(parent, "id");
		context.ermodelMap.put(id, model);
		return model;
	}

	private void loadSequenceSet(SequenceSet sequenceSet, Element parent) {
		Element element = this.getElement(parent, "sequence_set");

		if (element != null) {
			NodeList nodeList = element.getElementsByTagName("sequence");

			for (int i = 0; i < nodeList.getLength(); i++) {
				Element sequenceElemnt = (Element) nodeList.item(i);
				Sequence sequence = this.loadSequence(sequenceElemnt);

				sequenceSet.addSequence(sequence);
			}
		}
	}

	private Sequence loadSequence(Element element) {
		Sequence sequence = new Sequence();

		sequence.setName(this.getStringValue(element, "name"));
		sequence.setSchema(this.getStringValue(element, "schema"));
		sequence.setIncrement(this.getIntegerValue(element, "increment"));
		sequence.setMinValue(this.getLongValue(element, "min_value"));
		sequence.setMaxValue(this.getBigDecimalValue(element, "max_value"));
		sequence.setStart(this.getLongValue(element, "start"));
		sequence.setCache(this.getIntegerValue(element, "cache"));
		sequence.setCycle(this.getBooleanValue(element, "cycle"));
		sequence.setOrder(this.getBooleanValue(element, "order"));
		sequence.setDescription(this.getStringValue(element, "description"));
		sequence.setDataType(this.getStringValue(element, "data_type"));
		sequence.setDecimalSize(this.getIntValue(element, "decimal_size"));

		return sequence;
	}

	private void loadTriggerSet(TriggerSet triggerSet, Element parent) {
		Element element = this.getElement(parent, "trigger_set");

		if (element != null) {
			NodeList nodeList = element.getElementsByTagName("trigger");

			for (int i = 0; i < nodeList.getLength(); i++) {
				Element triggerElemnt = (Element) nodeList.item(i);
				Trigger trigger = this.loadTrigger(triggerElemnt);

				triggerSet.addTrigger(trigger);
			}
		}
	}

	private Trigger loadTrigger(Element element) {
		Trigger trigger = new Trigger();

		trigger.setName(this.getStringValue(element, "name"));
		trigger.setSchema(this.getStringValue(element, "schema"));
		trigger.setSql(this.getStringValue(element, "sql"));
		trigger.setDescription(this.getStringValue(element, "description"));

		return trigger;
	}

	private void loadTablespaceSet(TablespaceSet tablespaceSet, Element parent,
			LoadContext context) {
		Element element = this.getElement(parent, "tablespace_set");

		if (element != null) {
			NodeList nodeList = element.getElementsByTagName("tablespace");

			for (int i = 0; i < nodeList.getLength(); i++) {
				Element tablespaceElemnt = (Element) nodeList.item(i);
				Tablespace tablespace = this.loadTablespace(tablespaceElemnt,
						context);
				if (tablespace != null) {
					tablespaceSet.addTablespace(tablespace);
				}
			}
		}
	}

	private Tablespace loadTablespace(Element element, LoadContext context) {
		String id = this.getStringValue(element, "id");

		Tablespace tablespace = new Tablespace();
		tablespace.setName(this.getStringValue(element, "name"));

		NodeList nodeList = element.getElementsByTagName("properties");

		for (int i = 0; i < nodeList.getLength(); i++) {
			Element propertiesElemnt = (Element) nodeList.item(i);

			String environmentId = this.getStringValue(propertiesElemnt,
					"environment_id");
			Environment environment = context.environmentMap.get(environmentId);

			TablespaceProperties tablespaceProperties = null;

			if (DB2DBManager.ID.equals(this.database)) {
				tablespaceProperties = this
						.loadTablespacePropertiesDB2(propertiesElemnt);

			} else if (MySQLDBManager.ID.equals(this.database)) {
				tablespaceProperties = this
						.loadTablespacePropertiesMySQL(propertiesElemnt);

			} else if (OracleDBManager.ID.equals(this.database)) {
				tablespaceProperties = this
						.loadTablespacePropertiesOracle(propertiesElemnt);

			} else if (PostgresDBManager.ID.equals(this.database)) {
				tablespaceProperties = this
						.loadTablespacePropertiesPostgres(propertiesElemnt);

			}

			tablespace.putProperties(environment, tablespaceProperties);
		}

		if (id != null) {
			context.tablespaceMap.put(id, tablespace);
		}

		return tablespace;
	}

	private TablespaceProperties loadTablespacePropertiesDB2(Element element) {
		DB2TablespaceProperties properties = new DB2TablespaceProperties();

		properties.setBufferPoolName(this.getStringValue(element,
				"buffer_pool_name"));
		properties.setContainer(this.getStringValue(element, "container"));
		// properties.setContainerDevicePath(this.getStringValue(element,
		// "container_device_path"));
		// properties.setContainerDirectoryPath(this.getStringValue(element,
		// "container_directory_path"));
		// properties.setContainerFilePath(this.getStringValue(element,
		// "container_file_path"));
		// properties.setContainerPageNum(this.getStringValue(element,
		// "container_page_num"));
		properties.setExtentSize(this.getStringValue(element, "extent_size"));
		properties.setManagedBy(this.getStringValue(element, "managed_by"));
		properties.setPageSize(this.getStringValue(element, "page_size"));
		properties.setPrefetchSize(this
				.getStringValue(element, "prefetch_size"));
		properties.setType(this.getStringValue(element, "type"));

		return properties;
	}

	private TablespaceProperties loadTablespacePropertiesMySQL(Element element) {
		MySQLTablespaceProperties properties = new MySQLTablespaceProperties();

		properties.setDataFile(this.getStringValue(element, "data_file"));
		properties.setEngine(this.getStringValue(element, "engine"));
		properties.setExtentSize(this.getStringValue(element, "extent_size"));
		properties.setInitialSize(this.getStringValue(element, "initial_size"));
		properties.setLogFileGroup(this.getStringValue(element,
				"log_file_group"));

		return properties;
	}

	private TablespaceProperties loadTablespacePropertiesOracle(Element element) {
		OracleTablespaceProperties properties = new OracleTablespaceProperties();

		properties.setAutoExtend(this.getBooleanValue(element, "auto_extend"));
		properties.setAutoExtendMaxSize(this.getStringValue(element,
				"auto_extend_max_size"));
		properties.setAutoExtendSize(this.getStringValue(element,
				"auto_extend_size"));
		properties.setAutoSegmentSpaceManagement(this.getBooleanValue(element,
				"auto_segment_space_management"));
		properties.setDataFile(this.getStringValue(element, "data_file"));
		properties.setFileSize(this.getStringValue(element, "file_size"));
		properties.setInitial(this.getStringValue(element, "initial"));
		properties.setLogging(this.getBooleanValue(element, "logging"));
		properties.setMaxExtents(this.getStringValue(element, "max_extents"));
		properties.setMinExtents(this.getStringValue(element, "min_extents"));
		properties.setMinimumExtentSize(this.getStringValue(element,
				"minimum_extent_size"));
		properties.setNext(this.getStringValue(element, "next"));
		properties.setOffline(this.getBooleanValue(element, "offline"));
		properties.setPctIncrease(this.getStringValue(element, "pct_increase"));
		properties.setTemporary(this.getBooleanValue(element, "temporary"));

		return properties;
	}

	private TablespaceProperties loadTablespacePropertiesPostgres(
			Element element) {
		PostgresTablespaceProperties properties = new PostgresTablespaceProperties();

		properties.setLocation(this.getStringValue(element, "location"));
		properties.setOwner(this.getStringValue(element, "owner"));

		return properties;
	}

	private void loadChangeTrackingList(ChangeTrackingList changeTrackingList,
			Element parent) {
		Element element = this.getElement(parent, "change_tracking_list");

		if (element != null) {
			NodeList nodeList = element.getElementsByTagName("change_tracking");

			for (int i = 0; i < nodeList.getLength(); i++) {
				Element changeTrackingElemnt = (Element) nodeList.item(i);
				ChangeTracking changeTracking = this
						.loadChangeTracking(changeTrackingElemnt);

				changeTrackingList.addChangeTracking(changeTracking);
			}
		}
	}

	private ChangeTracking loadChangeTracking(Element element) {
		DiagramContents diagramContents = new DiagramContents();

		loadDiagramContents(diagramContents, element);

		ChangeTracking changeTracking = new ChangeTracking(diagramContents);

		changeTracking.setComment(this.getStringValue(element, "comment"));
		changeTracking.setUpdatedDate(this
				.getDateValue(element, "updated_date"));

		return changeTracking;
	}

	private void loadColumnGroups(GroupSet columnGroups, Element parent,
			LoadContext context) {

		Element element = this.getElement(parent, "column_groups");

		NodeList nodeList = element.getElementsByTagName("column_group");

		for (int i = 0; i < nodeList.getLength(); i++) {
			Element columnGroupElement = (Element) nodeList.item(i);

			ColumnGroup columnGroup = new ColumnGroup();

			columnGroup.setGroupName(this.getStringValue(columnGroupElement,
					"group_name"));

			List<Column> columns = this
					.loadColumns(columnGroupElement, context);
			for (Column column : columns) {
				columnGroup.addColumn((NormalColumn) column);
			}

			columnGroups.add(columnGroup);

			String id = this.getStringValue(columnGroupElement, "id");
			context.columnGroupMap.put(id, columnGroup);
		}

	}

	private void loadTestDataList(List<TestData> testDataList, Element parent,
			LoadContext context) {

		Element element = this.getElement(parent, "test_data_list");

		if (element != null) {
			NodeList nodeList = element.getElementsByTagName("test_data");

			for (int i = 0; i < nodeList.getLength(); i++) {
				Element testDataElement = (Element) nodeList.item(i);

				TestData testData = new TestData();
				this.loadTestData(testData, testDataElement, context);
				testDataList.add(testData);
			}
		}
	}

	private void loadTestData(TestData testData, Element element,
			LoadContext context) {

		testData.setName(this.getStringValue(element, "name"));
		testData.setExportOrder(this.getIntValue(element, "export_order"));

		NodeList nodeList = element.getElementsByTagName("table_test_data");

		for (int i = 0; i < nodeList.getLength(); i++) {
			Element tableTestDataElement = (Element) nodeList.item(i);

			TableTestData tableTestData = new TableTestData();

			String tableId = this.getStringValue(tableTestDataElement,
					"table_id");
			ERTable table = (ERTable) context.nodeElementMap.get(tableId);
			if (table != null) {
				this.loadDirectTestData(tableTestData.getDirectTestData(),
						tableTestDataElement, context);
				this.loadRepeatTestData(tableTestData.getRepeatTestData(),
						tableTestDataElement, context);

				testData.putTableTestData(table, tableTestData);
			}
		}

	}

	private void loadDirectTestData(DirectTestData directTestData,
			Element parent, LoadContext context) {
		Element element = this.getElement(parent, "direct_test_data");

		NodeList nodeList = element.getElementsByTagName("data");

		List<Map<NormalColumn, String>> dataList = directTestData.getDataList();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element dataElement = (Element) nodeList.item(i);

			NodeList columnNodeList = dataElement
					.getElementsByTagName("column_data");

			Map<NormalColumn, String> data = new HashMap<NormalColumn, String>();

			for (int j = 0; j < columnNodeList.getLength(); j++) {
				Element columnDataElement = (Element) columnNodeList.item(j);

				String columnId = this.getStringValue(columnDataElement,
						"column_id");
				NormalColumn column = context.columnMap.get(columnId);

				String value = this.getStringValue(columnDataElement, "value");

				data.put(column, value);
			}

			dataList.add(data);
		}
	}

	private void loadRepeatTestData(RepeatTestData repeatTestData,
			Element parent, LoadContext context) {
		Element element = this.getElement(parent, "repeat_test_data");

		int testDataNum = this.getIntegerValue(element, "test_data_num");
		repeatTestData.setTestDataNum(testDataNum);

		Element dataDefListElement = this.getElement(element, "data_def_list");

		NodeList nodeList = dataDefListElement.getElementsByTagName("data_def");

		for (int i = 0; i < nodeList.getLength(); i++) {
			Element dataDefElement = (Element) nodeList.item(i);

			String columnId = this.getStringValue(dataDefElement, "column_id");
			NormalColumn column = context.columnMap.get(columnId);

			RepeatTestDataDef dataDef = new RepeatTestDataDef();

			dataDef.setType(this.getStringValue(dataDefElement, "type"));
			dataDef.setRepeatNum(this.getIntValue(dataDefElement, "repeat_num"));
			dataDef.setTemplate(this.getStringValue(dataDefElement, "template"));
			dataDef.setFrom(this.getIntValue(dataDefElement, "from"));
			dataDef.setTo(this.getIntValue(dataDefElement, "to"));
			dataDef.setIncrement(this.getIntValue(dataDefElement, "increment"));
			dataDef.setSelects(this.getTagValues(dataDefElement, "select"));

			Element modifiedValuesElement = this.getElement(dataDefElement,
					"modified_values");
			if (modifiedValuesElement != null) {
				NodeList modifiedValueNodeList = modifiedValuesElement
						.getElementsByTagName("modified_value");

				for (int j = 0; j < modifiedValueNodeList.getLength(); j++) {
					Element modifiedValueNode = (Element) modifiedValueNodeList
							.item(j);

					Integer row = this.getIntValue(modifiedValueNode, "row");
					String value = this.getStringValue(modifiedValueNode,
							"value");

					dataDef.setModifiedValue(row, value);
				}
			}

			repeatTestData.setDataDef(column, dataDef);
		}
	}

	private void loadDictionary(Dictionary dictionary, Element parent,
			LoadContext context) {

		Element element = this.getElement(parent, "dictionary");

		if (element != null) {
			NodeList nodeList = element.getElementsByTagName("word");

			for (int i = 0; i < nodeList.getLength(); i++) {
				Element wordElement = (Element) nodeList.item(i);

				this.loadWord(wordElement, context);
			}
		}
	}

	private Word loadWord(Element element, LoadContext context) {

		String id = this.getStringValue(element, "id");

		String type = this.getStringValue(element, "type");

		TypeData typeData = new TypeData(
				this.getIntegerValue(element, "length"), this.getIntegerValue(
						element, "decimal"), this.getBooleanValue(element,
						"array"), this.getIntegerValue(element,
						"array_dimension"), this.getBooleanValue(element,
						"unsigned"), this.getStringValue(element, "args"));

		Word word = new Word(Format.null2blank(this.getStringValue(element,
				"physical_name")), Format.null2blank(this.getStringValue(
				element, "logical_name")), SqlType.valueOfId(type), typeData,
				Format.null2blank(this.getStringValue(element, "description")),
				this.database);

		context.wordMap.put(id, word);

		return word;
	}

	private List<Column> loadColumns(Element parent, LoadContext context) {
		List<Column> columns = new ArrayList<Column>();

		Element element = this.getElement(parent, "columns");

		NodeList groupList = element.getChildNodes();

		for (int i = 0; i < groupList.getLength(); i++) {
			if (groupList.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Element columnElement = (Element) groupList.item(i);

			if ("column_group".equals(columnElement.getTagName())) {
				ColumnGroup column = this.loadColumnGroup(columnElement,
						context);
				columns.add(column);

			} else if ("normal_column".equals(columnElement.getTagName())) {
				NormalColumn column = this.loadNormalColumn(columnElement,
						context);
				columns.add(column);
			}
		}

		return columns;
	}

	private ColumnGroup loadColumnGroup(Element element, LoadContext context) {
		String key = element.getFirstChild().getNodeValue();

		return context.columnGroupMap.get(key);
	}

	private NormalColumn loadNormalColumn(Element element, LoadContext context) {

		String id = this.getStringValue(element, "id");

		String type = this.getStringValue(element, "type");

		String wordId = this.getStringValue(element, "word_id");

		Word word = context.wordMap.get(wordId);

		NormalColumn normalColumn = null;

		if (word == null) {
			word = new Word(this.getStringValue(element, "physical_name"),
					this.getStringValue(element, "logical_name"),
					SqlType.valueOfId(type), new TypeData(null, null, false,
							null, false, null), this.getStringValue(element,
							"description"), database);

			UniqueWord uniqueWord = new UniqueWord(word);

			if (context.uniqueWordMap.containsKey(uniqueWord)) {
				word = context.uniqueWordMap.get(uniqueWord);
			} else {
				context.uniqueWordMap.put(uniqueWord, word);
			}
		}

		normalColumn = new NormalColumn(word, this.getBooleanValue(element,
				"not_null"), this.getBooleanValue(element, "primary_key"),
				this.getBooleanValue(element, "unique_key"),
				this.getBooleanValue(element, "auto_increment"),
				this.getStringValue(element, "default_value"),
				this.getStringValue(element, "constraint"),
				this.getStringValue(element, "unique_key_name"),
				this.getStringValue(element, "character_set"),
				this.getStringValue(element, "collation"));

		Element autoIncrementSettingElement = this.getElement(element,
				"sequence");
		if (autoIncrementSettingElement != null) {
			Sequence autoIncrementSetting = this
					.loadSequence(autoIncrementSettingElement);
			normalColumn.setAutoIncrementSetting(autoIncrementSetting);
		}

		boolean isForeignKey = false;

		String[] relationIds = this.getTagValues(element, "relation");
		if (relationIds != null) {
			context.columnRelationMap.put(normalColumn, relationIds);
		}

		String[] referencedColumnIds = this.getTagValues(element,
				"referenced_column");
		List<String> temp = new ArrayList<String>();
		for (String str : referencedColumnIds) {
			try {
				if (str != null) {
					Integer.parseInt(str);
					temp.add(str);
				}

			} catch (NumberFormatException e) {
			}
		}

		referencedColumnIds = temp.toArray(new String[temp.size()]);

		if (referencedColumnIds.length != 0) {
			context.columnReferencedColumnMap.put(normalColumn,
					referencedColumnIds);
			isForeignKey = true;
		}

		if (!isForeignKey) {
			context.dictionary.add(normalColumn);
		}

		context.columnMap.put(id, normalColumn);

		return normalColumn;
	}

	private void loadSettings(Settings settings, Element parent,
			LoadContext context) {
		Element element = this.getElement(parent, "settings");

		if (element != null) {
			settings.setDatabase(this.loadDatabase(element));
			settings.setCapital(this.getBooleanValue(element, "capital"));
			settings.setTableStyle(Format.null2blank(this.getStringValue(
					element, "table_style")));

			settings.setNotation(this.getStringValue(element, "notation"));
			settings.setNotationLevel(this.getIntValue(element,
					"notation_level"));
			settings.setNotationExpandGroup(this.getBooleanValue(element,
					"notation_expand_group"));

			settings.setViewMode(this.getIntValue(element, "view_mode"));
			settings.setOutlineViewMode(this.getIntValue(element,
					"outline_view_mode"));
			settings.setViewOrderBy(this.getIntValue(element, "view_order_by"));

			settings.setAutoImeChange(this.getBooleanValue(element,
					"auto_ime_change"));
			settings.setValidatePhysicalName(this.getBooleanValue(element,
					"validate_physical_name", true));
			settings.setUseBezierCurve(this.getBooleanValue(element,
					"use_bezier_curve"));
			settings.setSuspendValidator(this.getBooleanValue(element,
					"suspend_validator"));

			ExportSetting exportSetting = settings.getExportSetting();
			this.loadExportSetting(exportSetting, element, context);

			CategorySetting categorySetting = settings.getCategorySetting();
			this.loadCategorySetting(categorySetting, element, context);

			TranslationSetting translationSetting = settings
					.getTranslationSetting();
			this.loadTranslationSetting(translationSetting, element, context);

			ModelProperties modelProperties = settings.getModelProperties();
			this.loadModelProperties(modelProperties, element);

			this.loadTableProperties(
					(TableProperties) settings.getTableViewProperties(),
					element, context);

		}
	}

	private void loadExportSetting(ExportSetting exportSetting, Element parent,
			LoadContext context) {
		Element element = this.getElement(parent, "export_setting");

		if (element != null) {
			exportSetting.setCategoryNameToExport(this.getStringValue(element,
					"category_name_to_export"));
			exportSetting.setDdlOutput(this.getStringValue(element,
					"ddl_output"));
			exportSetting.setExcelOutput(this.getStringValue(element,
					"excel_output"));
			exportSetting.setExcelTemplate(this.getStringValue(element,
					"excel_template"));
			exportSetting.setImageOutput(this.getStringValue(element,
					"image_output"));
			exportSetting.setPutERDiagramOnExcel(this.getBooleanValue(element,
					"put_diagram_on_excel"));
			exportSetting.setUseLogicalNameAsSheet(this.getBooleanValue(
					element, "use_logical_name_as_sheet"));
			exportSetting.setOpenAfterSaved(this.getBooleanValue(element,
					"open_after_saved"));

			exportSetting.getDdlTarget().createComment = this.getBooleanValue(
					element, "create_comment");
			exportSetting.getDdlTarget().createForeignKey = this
					.getBooleanValue(element, "create_foreignKey");
			exportSetting.getDdlTarget().createIndex = this.getBooleanValue(
					element, "create_index");
			exportSetting.getDdlTarget().createSequence = this.getBooleanValue(
					element, "create_sequence");
			exportSetting.getDdlTarget().createTable = this.getBooleanValue(
					element, "create_table");
			exportSetting.getDdlTarget().createTablespace = this
					.getBooleanValue(element, "create_tablespace");
			exportSetting.getDdlTarget().createTrigger = this.getBooleanValue(
					element, "create_trigger");
			exportSetting.getDdlTarget().createView = this.getBooleanValue(
					element, "create_view");

			exportSetting.getDdlTarget().dropIndex = this.getBooleanValue(
					element, "drop_index");
			exportSetting.getDdlTarget().dropSequence = this.getBooleanValue(
					element, "drop_sequence");
			exportSetting.getDdlTarget().dropTable = this.getBooleanValue(
					element, "drop_table");
			exportSetting.getDdlTarget().dropTablespace = this.getBooleanValue(
					element, "drop_tablespace");
			exportSetting.getDdlTarget().dropTrigger = this.getBooleanValue(
					element, "drop_trigger");
			exportSetting.getDdlTarget().dropView = this.getBooleanValue(
					element, "drop_view");

			exportSetting.getDdlTarget().inlineColumnComment = this
					.getBooleanValue(element, "inline_column_comment");
			exportSetting.getDdlTarget().inlineTableComment = this
					.getBooleanValue(element, "inline_table_comment");

			exportSetting.getDdlTarget().commentValueDescription = this
					.getBooleanValue(element, "comment_value_description");
			exportSetting.getDdlTarget().commentValueLogicalName = this
					.getBooleanValue(element, "comment_value_logical_name");
			exportSetting.getDdlTarget().commentValueLogicalNameDescription = this
					.getBooleanValue(element,
							"comment_value_logical_name_description");
			exportSetting.getDdlTarget().commentReplaceLineFeed = this
					.getBooleanValue(element, "comment_replace_line_feed");
			exportSetting.getDdlTarget().commentReplaceString = this
					.getStringValue(element, "comment_replace_string");
			this.loadExportJavaSetting(exportSetting.getExportJavaSetting(),
					element, context);
			this.loadExportTestDataSetting(
					exportSetting.getExportTestDataSetting(), element, context);
		}
	}

	private void loadExportJavaSetting(ExportJavaSetting exportJavaSetting,
			Element parent, LoadContext context) {
		Element element = this.getElement(parent, "export_java_setting");

		if (element != null) {
			exportJavaSetting.setJavaOutput(this.getStringValue(element,
					"java_output"));
			exportJavaSetting.setPackageName(Format.null2blank(this
					.getStringValue(element, "package_name")));
			exportJavaSetting.setClassNameSuffix(Format.null2blank(this
					.getStringValue(element, "class_name_suffix")));
			exportJavaSetting.setSrcFileEncoding(this.getStringValue(element,
					"src_file_encoding"));
			exportJavaSetting.setWithHibernate(this.getBooleanValue(element,
					"with_hibernate"));
		}
	}

	private void loadExportTestDataSetting(
			ExportTestDataSetting exportTestDataSetting, Element parent,
			LoadContext context) {
		Element element = this.getElement(parent, "export_testdata_setting");

		if (element != null) {
			exportTestDataSetting.setExportFileEncoding(this.getStringValue(
					element, "file_encoding"));
			exportTestDataSetting.setExportFilePath(this.getStringValue(
					element, "file_path"));
			exportTestDataSetting.setExportFormat(this.getIntValue(element,
					"format"));
		}
	}

	private void loadCategorySetting(CategorySetting categorySetting,
			Element parent, LoadContext context) {
		Element element = this.getElement(parent, "category_settings");
		categorySetting.setFreeLayout(this.getBooleanValue(element,
				"free_layout"));
		categorySetting.setShowReferredTables(this.getBooleanValue(element,
				"show_referred_tables"));

		Element categoriesElement = this.getElement(element, "categories");

		NodeList nodeList = categoriesElement.getChildNodes();

		List<Category> selectedCategories = new ArrayList<Category>();

		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Element categoryElement = (Element) nodeList.item(i);

			Category category = new Category();

			this.loadNodeElement(category, categoryElement, context);
			category.setName(this.getStringValue(categoryElement, "name"));
			boolean isSelected = this.getBooleanValue(categoryElement,
					"selected");

			String[] keys = this.getTagValues(categoryElement, "node_element");

			List<NodeElement> nodeElementList = new ArrayList<NodeElement>();

			for (String key : keys) {
				NodeElement nodeElement = context.nodeElementMap.get(key);
				if (nodeElement != null) {
					nodeElementList.add(nodeElement);
				}
			}

			category.setContents(nodeElementList);
			categorySetting.addCategory(category);

			if (isSelected) {
				selectedCategories.add(category);
			}
		}

		categorySetting.setSelectedCategories(selectedCategories);
	}


	private VGroup loadGroup(ERModel model, Element node, LoadContext context) {
		VGroup group = new VGroup();
		this.loadNodeElement(group, node, context);
		group.setName(getStringValue(node, "name"));
		
		List<ERVirtualTable> vtables = model.getTables();
		String[] keys = this.getTagValues(node, "node_element");
		List<NodeElement> nodeElementList = new ArrayList<NodeElement>();
		for (String key : keys) {
			NodeElement nodeElement = context.nodeElementMap.get(key);
			if (nodeElement != null) {
				for (ERVirtualTable vtable : vtables) {
					if (vtable.getRawTable().equals(nodeElement)) {
						nodeElementList.add(vtable);
						break;
					}
				}
			}
		}
		group.setContents(nodeElementList);

		return group;
	}
	
	private void loadTranslationSetting(TranslationSetting translationSetting,
			Element parent, LoadContext context) {
		Element element = this.getElement(parent, "translation_settings");
		if (element != null) {
			translationSetting.setUse(this.getBooleanValue(element, "use"));

			Element translationsElement = this.getElement(element,
					"translations");

			NodeList nodeList = translationsElement.getChildNodes();

			List<String> selectedTranslations = new ArrayList<String>();

			for (int i = 0; i < nodeList.getLength(); i++) {
				if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}

				Element translationElement = (Element) nodeList.item(i);

				selectedTranslations.add(this.getStringValue(
						translationElement, "name"));
			}

			translationSetting.setSelectedTranslations(selectedTranslations);
		}
	}

	private void loadEnvironmentSetting(EnvironmentSetting environmentSetting,
			Element parent, LoadContext context) {
		Element settingElement = this.getElement(parent, "settings");
		Element element = this
				.getElement(settingElement, "environment_setting");

		List<Environment> environmentList = new ArrayList<Environment>();

		if (element != null) {
			NodeList nodeList = element.getChildNodes();

			for (int i = 0; i < nodeList.getLength(); i++) {
				if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}

				Element environmentElement = (Element) nodeList.item(i);

				String id = this.getStringValue(environmentElement, "id");
				String name = this.getStringValue(environmentElement, "name");
				Environment environment = new Environment(name);

				environmentList.add(environment);
				context.environmentMap.put(id, environment);
			}
		}

		if (environmentList.isEmpty()) {
			Environment environment = new Environment(
					ResourceString.getResourceString("label.default"));
			environmentList.add(environment);
			context.environmentMap.put("", environment);
		}

		environmentSetting.setEnvironments(environmentList);
	}

	private void loadModelProperties(ModelProperties modelProperties,
			Element parent) {
		Element element = this.getElement(parent, "model_properties");

		this.loadLocation(modelProperties, element);
		this.loadColor(modelProperties, element);

		modelProperties.setDisplay(this.getBooleanValue(element, "display"));
		modelProperties.setCreationDate(this.getDateValue(element,
				"creation_date"));
		modelProperties.setUpdatedDate(this.getDateValue(element,
				"updated_date"));

		NodeList nodeList = element.getElementsByTagName("model_property");

		for (int i = 0; i < nodeList.getLength(); i++) {
			Element propertyElement = (Element) nodeList.item(i);

			NameValue nameValue = new NameValue(this.getStringValue(
					propertyElement, "name"), this.getStringValue(
					propertyElement, "value"));

			modelProperties.addProperty(nameValue);
		}
	}

	private void loadLocation(NodeElement nodeElement, Element element) {

		int x = this.getIntValue(element, "x");
		int y = this.getIntValue(element, "y");
		int width = this.getIntValue(element, "width");
		int height = this.getIntValue(element, "height");

		nodeElement.setLocation(new Location(x, y, width, height));
	}

	private void loadFont(ViewableModel viewableModel, Element element) {
		if (getElement(element, "font_name") == null) {
			return;
		}
		String fontName = this.getStringValue(element, "font_name");
		int fontSize = this.getIntValue(element, "font_size");

		viewableModel.setFontName(fontName);
		viewableModel.setFontSize(fontSize);
	}

	private void loadContents(NodeSet contents, Element parent,
			LoadContext context) {
		Element element = this.getElement(parent, "contents");

		NodeList nodeList = element.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Node node = nodeList.item(i);

			if ("table".equals(node.getNodeName())) {
				ERTable table = this.loadTable((Element) node, context);
				contents.addNodeElement(table);

			} else if ("view".equals(node.getNodeName())) {
				View view = this.loadView((Element) node, context);
				contents.addNodeElement(view);

//			} else if ("note".equals(node.getNodeName())) {
//				Note note = this.loadNote((Element) node, context);
//				contents.addNodeElement(note);

			} else if ("image".equals(node.getNodeName())) {
				InsertedImage insertedImage = this.loadInsertedImage(
						(Element) node, context);
				contents.addNodeElement(insertedImage);
				
			} else if ("ermodel".equals(node.getNodeName())) {
				ERModel ermodel = this.loadErmodel((Element) node, context);
				contents.addNodeElement(ermodel);

//			} else if ("group".equals(node.getNodeName())) {
//				VGroup group = this.loadGroup((Element) node, context);
//				contents.addNodeElement(group);

			} else {
				throw new RuntimeException("not support " + node);
			}
		}
	}


	private ERTable loadTable(Element element, LoadContext context) {
		ERTable table = new ERTable();

		table.setDiagram(this.diagram);

		this.loadNodeElement(table, element, context);
		table.setPhysicalName(this.getStringValue(element, "physical_name"));
		table.setLogicalName(this.getStringValue(element, "logical_name"));
		table.setDescription(this.getStringValue(element, "description"));
		table.setConstraint(this.getStringValue(element, "constraint"));
		table.setPrimaryKeyName(this
				.getStringValue(element, "primary_key_name"));
		table.setOption(this.getStringValue(element, "option"));

		List<Column> columns = this.loadColumns(element, context);
		table.setColumns(columns);

		List<Index> indexes = this.loadIndexes(element, table, context);
		table.setIndexes(indexes);

		List<ComplexUniqueKey> complexUniqueKeyList = this
				.loadComplexUniqueKeyList(element, table, context);
		table.setComplexUniqueKeyList(complexUniqueKeyList);

		this.loadTableProperties(
				(TableProperties) table.getTableViewProperties(), element,
				context);

		return table;
	}


	private ERVirtualTable loadVirtualTable(ERModel model, Element element, LoadContext context) {
		String tableId = getStringValue(element, "id");
		ERTable rawTable = (ERTable) context.nodeElementMap.get(tableId);
		ERVirtualTable vtable = new ERVirtualTable(model, rawTable);
		loadLocation(vtable, element);
		loadFont(vtable, element);
		return vtable;
	}

	private View loadView(Element element, LoadContext context) {
		View view = new View();

		view.setDiagram(this.diagram);

		this.loadNodeElement(view, element, context);
		view.setPhysicalName(this.getStringValue(element, "physical_name"));
		view.setLogicalName(this.getStringValue(element, "logical_name"));
		view.setDescription(this.getStringValue(element, "description"));
		view.setSql(this.getStringValue(element, "sql"));

		List<Column> columns = this.loadColumns(element, context);
		view.setColumns(columns);

		this.loadViewProperties((ViewProperties) view.getTableViewProperties(),
				element, context);

		return view;
	}

	private List<Index> loadIndexes(Element parent, ERTable table,
			LoadContext context) {
		List<Index> indexes = new ArrayList<Index>();

		Element element = this.getElement(parent, "indexes");

		NodeList nodeList = element.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Element indexElement = (Element) nodeList.item(i);

			String type = this.getStringValue(indexElement, "type");
			if ("null".equals(type)) {
				type = null;
			}

			Index index = new Index(table, this.getStringValue(indexElement,
					"name"), this.getBooleanValue(indexElement, "non_unique"),
					type, this.getStringValue(indexElement, "description"));

			index.setFullText(this.getBooleanValue(indexElement, "full_text"));

			this.loadIndexColumns(index, indexElement, context);

			indexes.add(index);
		}

		return indexes;
	}

	private void loadIndexColumns(Index index, Element parent,
			LoadContext context) {
		Element element = this.getElement(parent, "columns");
		NodeList nodeList = element.getChildNodes();

		List<Boolean> descs = new ArrayList<Boolean>();

		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Element columnElement = (Element) nodeList.item(i);

			String id = this.getStringValue(columnElement, "id");
			NormalColumn column = context.columnMap.get(id);

			Boolean desc = new Boolean(this.getBooleanValue(columnElement,
					"desc"));

			index.addColumn(column);
			descs.add(desc);
		}

		index.setDescs(descs);
	}

	private List<ComplexUniqueKey> loadComplexUniqueKeyList(Element parent,
			ERTable table, LoadContext context) {
		List<ComplexUniqueKey> complexUniqueKeyList = new ArrayList<ComplexUniqueKey>();

		Element element = this.getElement(parent, "complex_unique_key_list");
		if (element == null) {
			return complexUniqueKeyList;
		}

		NodeList nodeList = element.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Element complexUniqueKeyElement = (Element) nodeList.item(i);

			String id = this.getStringValue(complexUniqueKeyElement, "id");
			String name = this.getStringValue(complexUniqueKeyElement, "name");

			ComplexUniqueKey complexUniqueKey = new ComplexUniqueKey(name);

			this.loadComplexUniqueKeyColumns(complexUniqueKey,
					complexUniqueKeyElement, context);

			complexUniqueKeyList.add(complexUniqueKey);

			context.complexUniqueKeyMap.put(id, complexUniqueKey);
		}

		return complexUniqueKeyList;
	}

	private void loadComplexUniqueKeyColumns(ComplexUniqueKey complexUniqueKey,
			Element parent, LoadContext context) {
		Element element = this.getElement(parent, "columns");
		NodeList nodeList = element.getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Element columnElement = (Element) nodeList.item(i);

			String id = this.getStringValue(columnElement, "id");
			NormalColumn column = context.columnMap.get(id);

			complexUniqueKey.addColumn(column);
		}
	}

	private void loadTableProperties(TableProperties tableProperties,
			Element parent, LoadContext context) {
		Element element = this.getElement(parent, "table_properties");

		String tablespaceId = this.getStringValue(element, "tablespace_id");
		Tablespace tablespace = context.tablespaceMap.get(tablespaceId);
		tableProperties.setTableSpace(tablespace);

		tableProperties.setSchema(this.getStringValue(element, "schema"));

		if (tableProperties instanceof MySQLTableProperties) {
			this.loadTablePropertiesMySQL(
					(MySQLTableProperties) tableProperties, element);

		} else if (tableProperties instanceof PostgresTableProperties) {
			this.loadTablePropertiesPostgres(
					(PostgresTableProperties) tableProperties, element);

		}
	}

	private void loadTablePropertiesMySQL(MySQLTableProperties tableProperties,
			Element element) {

		tableProperties.setCharacterSet(this.getStringValue(element,
				"character_set"));
		tableProperties.setCollation(this.getStringValue(element, "collation"));
		tableProperties.setStorageEngine(this.getStringValue(element,
				"storage_engine"));
		tableProperties.setPrimaryKeyLengthOfText(this.getIntegerValue(element,
				"primary_key_length_of_text"));
	}

	private void loadTablePropertiesPostgres(
			PostgresTableProperties tableProperties, Element element) {
		tableProperties.setWithoutOIDs(this.getBooleanValue(element,
				"without_oids"));
	}

	private void loadViewProperties(ViewProperties viewProperties,
			Element parent, LoadContext context) {
		Element element = this.getElement(parent, "view_properties");

		if (element != null) {
			String tablespaceId = this.getStringValue(element, "tablespace_id");
			Tablespace tablespace = context.tablespaceMap.get(tablespaceId);
			viewProperties.setTableSpace(tablespace);

			viewProperties.setSchema(this.getStringValue(element, "schema"));
		}
	}

	private Note loadNote(ERModel model, Element element, LoadContext context) {
		Note note = new Note();
		note.setModel(model);

		note.setText(this.getStringValue(element, "text"));
		this.loadNodeElement(note, element, context);

		return note;
	}

	private InsertedImage loadInsertedImage(Element element, LoadContext context) {
		InsertedImage insertedImage = new InsertedImage();

		insertedImage
				.setBase64EncodedData(this.getStringValue(element, "data"));
		insertedImage.setHue(this.getIntValue(element, "hue"));
		insertedImage.setSaturation(this.getIntValue(element, "saturation"));
		insertedImage.setBrightness(this.getIntValue(element, "brightness"));
		insertedImage.setAlpha(this.getIntValue(element, "alpha", 255));
		insertedImage.setFixAspectRatio(this.getBooleanValue(element,
				"fix_aspect_ratio"));

		this.loadNodeElement(insertedImage, element, context);

		return insertedImage;
	}

	private void loadNodeElement(NodeElement nodeElement, Element element,
			LoadContext context) {
		String id = this.getStringValue(element, "id");

		this.loadLocation(nodeElement, element);
		this.loadColor(nodeElement, element);
		this.loadFont(nodeElement, element);

		context.nodeElementMap.put(id, nodeElement);

		this.loadConnections(element, context);
	}

	private void loadConnections(Element parent, LoadContext context) {
		Element element = this.getElement(parent, "connections");

		if (element != null) {
			NodeList nodeList = element.getChildNodes();

			for (int i = 0; i < nodeList.getLength(); i++) {
				if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}

				Element connectionElement = (Element) nodeList.item(i);

				if ("relation".equals(connectionElement.getTagName())) {
					this.loadRelation(connectionElement, context);

				} else if ("comment_connection".equals(connectionElement
						.getTagName())) {
					this.loadCommentConnection(connectionElement, context);
				}
			}
		}
	}

	private void loadRelation(Element element, LoadContext context) {
		boolean referenceForPK = this.getBooleanValue(element,
				"reference_for_pk");
		Relation connection = new Relation(referenceForPK, null, null);

		this.load(connection, element, context);

		connection.setChildCardinality(this.getStringValue(element,
				"child_cardinality"));
		connection.setParentCardinality(this.getStringValue(element,
				"parent_cardinality"));
		connection.setName(this.getStringValue(element, "name"));
		connection.setOnDeleteAction(this.getStringValue(element,
				"on_delete_action"));
		connection.setOnUpdateAction(this.getStringValue(element,
				"on_update_action"));
		connection.setSourceLocationp(this.getIntValue(element, "source_xp"),
				this.getIntValue(element, "source_yp"));
		connection.setTargetLocationp(this.getIntValue(element, "target_xp"),
				this.getIntValue(element, "target_yp"));

		String referencedComplexUniqueKeyId = this.getStringValue(element,
				"referenced_complex_unique_key");
		if (!"null".equals(referencedComplexUniqueKeyId)) {
			context.referencedComplexUniqueKeyMap.put(connection,
					referencedComplexUniqueKeyId);
		}
		String referencedColumnId = this.getStringValue(element,
				"referenced_column");
		if (!"null".equals(referencedColumnId)) {
			context.referencedColumnMap.put(connection, referencedColumnId);
		}
	}

	private void loadCommentConnection(Element element, LoadContext context) {
		CommentConnection connection = new CommentConnection();

		this.load(connection, element, context);
	}

	private void load(ConnectionElement connection, Element element,
			LoadContext context) {
		String id = this.getStringValue(element, "id");

		context.connectionMap.put(id, connection);

		String source = this.getStringValue(element, "source");
		String target = this.getStringValue(element, "target");

		context.connectionSourceMap.put(connection, source);
		context.connectionTargetMap.put(connection, target);

		NodeList nodeList = element.getElementsByTagName("bendpoint");

		for (int i = 0; i < nodeList.getLength(); i++) {
			Element bendPointElement = (Element) nodeList.item(i);

			Bendpoint bendpoint = new Bendpoint(this.getIntValue(
					bendPointElement, "x"), this.getIntValue(bendPointElement,
					"y"));

			bendpoint.setRelative(this.getBooleanValue(bendPointElement,
					"relative"));

			connection.addBendpoint(i, bendpoint);
		}
	}

	private void loadDBSetting(ERDiagram diagram, Element element) {
		Element dbSettingElement = this.getElement(element, "dbsetting");

		if (dbSettingElement != null) {
			String dbsystem = this.getStringValue(element, "dbsystem");
			String server = this.getStringValue(element, "server");
			int port = this.getIntValue(element, "port");
			String database = this.getStringValue(element, "database");
			String user = this.getStringValue(element, "user");
			String password = this.getStringValue(element, "password");
			boolean useDefaultDriver = this.getBooleanValue(element,
					"use_default_driver", true);
			if (StandardSQLDBManager.ID.equals(dbsystem)) {
				useDefaultDriver = false;
			}

			String url = this.getStringValue(element, "url");
			String driverClassName = this.getStringValue(element,
					"driver_class_name");

			DBSetting dbSetting = new DBSetting(dbsystem, server, port,
					database, user, password, useDefaultDriver, url,
					driverClassName);
			diagram.setDbSetting(dbSetting);
		}
	}

	private void loadPageSetting(ERDiagram diagram, Element element) {
		Element dbSettingElement = this.getElement(element, "page_setting");

		if (dbSettingElement != null) {
			boolean directionHorizontal = this.getBooleanValue(element,
					"direction_horizontal");
			int scale = this.getIntValue(element, "scale");
			String paperSize = this.getStringValue(element, "paper_size");
			int topMargin = this.getIntValue(element, "top_margin");
			int leftMargin = this.getIntValue(element, "left_margin");
			int bottomMargin = this.getIntValue(element, "bottom_margin");
			int rightMargin = this.getIntValue(element, "right_margin");

			PageSetting pageSetting = new PageSetting(directionHorizontal,
					scale, paperSize, topMargin, rightMargin, bottomMargin,
					leftMargin);
			diagram.setPageSetting(pageSetting);
		}
	}

	private void loadColor(ViewableModel model, Element element) {
		int[] rgb = new int[] { 255, 255, 255 };
		Element color = this.getElement(element, "color");

		if (color != null) {
			rgb[0] = this.getIntValue(color, "r");
			rgb[1] = this.getIntValue(color, "g");
			rgb[2] = this.getIntValue(color, "b");
		}

		model.setColor(rgb[0], rgb[1], rgb[2]);
	}

	private void loadDefaultColor(ERDiagram diagram, Element element) {
		int[] rgb = new int[] { 255, 255, 255 };
		Element color = this.getElement(element, "default_color");

		if (color != null) {
			rgb[0] = this.getIntValue(color, "r");
			rgb[1] = this.getIntValue(color, "g");
			rgb[2] = this.getIntValue(color, "b");
		}

		diagram.setDefaultColor(rgb[0], rgb[1], rgb[2]);
	}
}
