package org.insightech.er.editor.persistent.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.insightech.er.db.impl.db2.tablespace.DB2TablespaceProperties;
import org.insightech.er.db.impl.mysql.MySQLTableProperties;
import org.insightech.er.db.impl.mysql.tablespace.MySQLTablespaceProperties;
import org.insightech.er.db.impl.oracle.tablespace.OracleTablespaceProperties;
import org.insightech.er.db.impl.postgres.PostgresTableProperties;
import org.insightech.er.db.impl.postgres.tablespace.PostgresTablespaceProperties;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.DDLTarget;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.CommentConnection;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModelSet;
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
import org.insightech.er.editor.persistent.Persistent;
import org.insightech.er.util.Format;
import org.insightech.er.util.NameValue;

public class PersistentXmlImpl extends Persistent {

	public static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private class PersistentContext {
		private Map<ColumnGroup, Integer> columnGroupMap = new HashMap<ColumnGroup, Integer>();

		private Map<ConnectionElement, Integer> connectionMap = new HashMap<ConnectionElement, Integer>();

		private Map<Column, Integer> columnMap = new HashMap<Column, Integer>();

		private Map<ComplexUniqueKey, Integer> complexUniqueKeyMap = new HashMap<ComplexUniqueKey, Integer>();

		private Map<NodeElement, Integer> nodeElementMap = new HashMap<NodeElement, Integer>();

		private Map<ERModel, Integer> ermodelMap = new HashMap<ERModel, Integer>();

		private Map<Word, Integer> wordMap = new HashMap<Word, Integer>();

		private Map<Tablespace, Integer> tablespaceMap = new HashMap<Tablespace, Integer>();

		private Map<Environment, Integer> environmentMap = new HashMap<Environment, Integer>();
	}

	private PersistentContext getContext(DiagramContents diagramContents) {
		PersistentContext context = new PersistentContext();

		int columnGroupCount = 0;
		int columnCount = 0;
		for (ColumnGroup columnGroup : diagramContents.getGroups()) {
			context.columnGroupMap.put(columnGroup, new Integer(
					columnGroupCount));
			columnGroupCount++;

			for (NormalColumn normalColumn : columnGroup.getColumns()) {
				context.columnMap.put(normalColumn, new Integer(columnCount));
				columnCount++;
			}
		}

		int nodeElementCount = 0;
		int connectionCount = 0;
		int complexUniqueKeyCount = 0;

		for (NodeElement content : diagramContents.getContents()) {
			context.nodeElementMap.put(content, new Integer(nodeElementCount));
			nodeElementCount++;

			List<ConnectionElement> connections = content.getIncomings();

			for (ConnectionElement connection : connections) {
				context.connectionMap.put(connection, new Integer(
						connectionCount));
				connectionCount++;
			}

			if (content instanceof ERTable) {

				ERTable table = (ERTable) content;

				List<Column> columns = table.getColumns();

				for (Column column : columns) {
					if (column instanceof NormalColumn) {
						context.columnMap.put(column, new Integer(columnCount));

						columnCount++;
					}
				}

				for (ComplexUniqueKey complexUniqueKey : table
						.getComplexUniqueKeyList()) {
					context.complexUniqueKeyMap.put(complexUniqueKey,
							new Integer(complexUniqueKeyCount));

					complexUniqueKeyCount++;
				}

			}
		}

		int wordCount = 0;
		for (Word word : diagramContents.getDictionary().getWordList()) {
			context.wordMap.put(word, new Integer(wordCount));
			wordCount++;
		}

		int tablespaceCount = 0;
		for (Tablespace tablespace : diagramContents.getTablespaceSet()) {
			context.tablespaceMap.put(tablespace, new Integer(tablespaceCount));
			tablespaceCount++;
		}

		int environmentCount = 0;
		for (Environment environment : diagramContents.getSettings()
				.getEnvironmentSetting().getEnvironments()) {
			context.environmentMap.put(environment, new Integer(
					environmentCount));
			environmentCount++;
		}
		
		int virtualModelCount = 0;
		for (ERModel model : diagramContents.getModelSet()) {
			context.ermodelMap.put(model, new Integer(virtualModelCount));
			virtualModelCount++;
		}

		return context;
	}

	private PersistentContext getCurrentContext(ERDiagram diagram) {
		return this.getContext(diagram.getDiagramContents());
	}

	private PersistentContext getChangeTrackingContext(
			ChangeTracking changeTracking) {
		return this.getContext(changeTracking.getDiagramContents());
	}

	@Override
	public ERDiagram load(InputStream in) throws Exception {
		XMLLoader loader = new XMLLoader();
		return loader.load(in);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream createInputStream(ERDiagram diagram) throws IOException {
		InputStream inputStream = null;

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		String xml = this.createXML(diagram);

		out.write(xml.getBytes("UTF-8"));

		inputStream = new ByteArrayInputStream(out.toByteArray());

		return inputStream;
	}

	private static String tab(String str) {
		str = str.replaceAll("\n\t", "\n\t\t");
		str = str.replaceAll("\n<", "\n\t<");

		return "\t" + str;
	}

	private String createXML(ERDiagram diagram) {
		StringBuilder xml = new StringBuilder();

		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		xml.append("<diagram>\n");

		if (diagram.getDbSetting() != null) {
			xml.append("\t<dbsetting>\n")
					.append(tab(tab(this.createXML(diagram.getDbSetting()))))
					.append("\t</dbsetting>\n");
		}
		if (diagram.getPageSetting() != null) {
			xml.append("\t<page_setting>\n")
					.append(tab(tab(this.createXML(diagram.getPageSetting()))))
					.append("\t</page_setting>\n");
		}

		xml.append("\t<category_index>")
				.append(diagram.getCurrentCategoryIndex())
				.append("</category_index>\n");
		if (diagram.getCurrentErmodel() != null) {
			xml.append("\t<current_ermodel>").append(diagram.getCurrentErmodel().getName()).append("</current_ermodel>\n");
		}
		
		xml.append("\t<zoom>").append(diagram.getZoom()).append("</zoom>\n");
		xml.append("\t<x>").append(diagram.getX()).append("</x>\n");
		xml.append("\t<y>").append(diagram.getY()).append("</y>\n");

		appendColor(xml, "default_color", diagram.getDefaultColor());
		
		xml.append(tab(this.createXMLColor(diagram.getColor())));
		xml.append("\t<font_name>").append(escape(diagram.getFontName()))
				.append("</font_name>\n");
		xml.append("\t<font_size>").append(diagram.getFontSize())
				.append("</font_size>\n");

		PersistentContext context = this.getCurrentContext(diagram);

		xml.append(tab(this.createXML(diagram.getDiagramContents(), context)));
		xml.append(tab(this.createXML(diagram.getChangeTrackingList())));

		xml.append("</diagram>\n");

		return xml.toString();
	}

	private void appendColor(StringBuilder xml, String tagName, int[] defaultColor) {
		if (defaultColor == null) {
			return;
		}
		xml.append("\t<" + tagName + ">\n");
		xml.append("\t\t<r>").append(defaultColor[0]).append("</r>\n");
		xml.append("\t\t<g>").append(defaultColor[1]).append("</g>\n");
		xml.append("\t\t<b>").append(defaultColor[2]).append("</b>\n");
		xml.append("\t</" + tagName + ">\n");
	}

	private String createXML(DBSetting dbSetting) {
		StringBuilder xml = new StringBuilder();

		xml.append("<dbsystem>").append(escape(dbSetting.getDbsystem()))
				.append("</dbsystem>\n");
		xml.append("<server>").append(escape(dbSetting.getServer()))
				.append("</server>\n");
		xml.append("<port>").append(dbSetting.getPort()).append("</port>\n");
		xml.append("<database>").append(escape(dbSetting.getDatabase()))
				.append("</database>\n");
		xml.append("<user>").append(escape(dbSetting.getUser()))
				.append("</user>\n");
		xml.append("<password>").append(escape(dbSetting.getPassword()))
				.append("</password>\n");
		xml.append("<use_default_driver>")
				.append(dbSetting.isUseDefaultDriver())
				.append("</use_default_driver>\n");
		xml.append("<url>").append(escape(dbSetting.getUrl()))
				.append("</url>\n");
		xml.append("<driver_class_name>")
				.append(escape(dbSetting.getDriverClassName()))
				.append("</driver_class_name>\n");

		return xml.toString();
	}

	private String createXML(PageSetting pageSetting) {
		StringBuilder xml = new StringBuilder();

		xml.append("<direction_horizontal>")
				.append(pageSetting.isDirectionHorizontal())
				.append("</direction_horizontal>\n");
		xml.append("<scale>").append(pageSetting.getScale())
				.append("</scale>\n");
		xml.append("<paper_size>").append(escape(pageSetting.getPaperSize()))
				.append("</paper_size>\n");
		xml.append("<top_margin>").append(pageSetting.getTopMargin())
				.append("</top_margin>\n");
		xml.append("<left_margin>").append(pageSetting.getLeftMargin())
				.append("</left_margin>\n");
		xml.append("<bottom_margin>").append(pageSetting.getBottomMargin())
				.append("</bottom_margin>\n");
		xml.append("<right_margin>").append(pageSetting.getRightMargin())
				.append("</right_margin>\n");

		return xml.toString();
	}

	private String createXML(DiagramContents diagramContents,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append(this.createXML(diagramContents.getSettings(), context));
		xml.append(this.createXML(diagramContents.getDictionary(), context));
		xml.append(this.createXML(diagramContents.getTablespaceSet(), context));
		xml.append(this.createXML(diagramContents.getContents(), context));
		xml.append(this.createXMLERModel(diagramContents.getModelSet(), context));
		xml.append(this.createXML(diagramContents.getGroups(), context));
		xml.append(this.createXML(diagramContents.getTestDataList(), context));

		xml.append(this.createXML(diagramContents.getSequenceSet()));
		xml.append(this.createXML(diagramContents.getTriggerSet()));

		return xml.toString();
	}

	private String createXML(GroupSet columnGroups, PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<column_groups>\n");

		for (ColumnGroup columnGroup : columnGroups) {
			xml.append(tab(tab(this.createXML(columnGroup, context))));
		}

		xml.append("</column_groups>\n");

		return xml.toString();
	}

	private String createXML(List<TestData> testDataList,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<test_data_list>\n");

		for (TestData testData : testDataList) {
			xml.append(tab(tab(this.createXML(testData, context))));
		}

		xml.append("</test_data_list>\n");

		return xml.toString();
	}

	private String createXML(TriggerSet triggerSet) {
		StringBuilder xml = new StringBuilder();

		xml.append("<trigger_set>\n");

		for (Trigger trigger : triggerSet) {
			xml.append(tab(this.createXML(trigger)));
		}

		xml.append("</trigger_set>\n");

		return xml.toString();
	}

	private String createXML(Trigger trigger) {
		StringBuilder xml = new StringBuilder();

		xml.append("<trigger>\n");

		xml.append("\t<name>").append(escape(trigger.getName()))
				.append("</name>\n");
		xml.append("\t<schema>").append(escape(trigger.getSchema()))
				.append("</schema>\n");
		xml.append("\t<sql>").append(escape(trigger.getSql()))
				.append("</sql>\n");
		xml.append("\t<description>").append(escape(trigger.getDescription()))
				.append("</description>\n");

		xml.append("</trigger>\n");

		return xml.toString();
	}

	private String createXML(SequenceSet sequenceSet) {
		StringBuilder xml = new StringBuilder();

		xml.append("<sequence_set>\n");

		for (Sequence sequence : sequenceSet) {
			xml.append(tab(this.createXML(sequence)));
		}

		xml.append("</sequence_set>\n");

		return xml.toString();
	}

	private String createXML(Sequence sequence) {
		StringBuilder xml = new StringBuilder();

		xml.append("<sequence>\n");

		xml.append("\t<name>").append(escape(sequence.getName()))
				.append("</name>\n");
		xml.append("\t<schema>").append(escape(sequence.getSchema()))
				.append("</schema>\n");
		xml.append("\t<increment>")
				.append(Format.toString(sequence.getIncrement()))
				.append("</increment>\n");
		xml.append("\t<min_value>")
				.append(Format.toString(sequence.getMinValue()))
				.append("</min_value>\n");
		xml.append("\t<max_value>")
				.append(Format.toString(sequence.getMaxValue()))
				.append("</max_value>\n");
		xml.append("\t<start>").append(Format.toString(sequence.getStart()))
				.append("</start>\n");
		xml.append("\t<cache>").append(Format.toString(sequence.getCache()))
				.append("</cache>\n");
		xml.append("\t<cycle>").append(sequence.isCycle()).append("</cycle>\n");
		xml.append("\t<order>").append(sequence.isOrder()).append("</order>\n");
		xml.append("\t<description>").append(escape(sequence.getDescription()))
				.append("</description>\n");
		xml.append("\t<data_type>").append(escape(sequence.getDataType()))
				.append("</data_type>\n");
		xml.append("\t<decimal_size>")
				.append(Format.toString(sequence.getDecimalSize()))
				.append("</decimal_size>\n");

		xml.append("</sequence>\n");

		return xml.toString();
	}

	private String createXML(TablespaceSet tablespaceSet,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<tablespace_set>\n");

		for (Tablespace tablespace : tablespaceSet) {
			xml.append(tab(this.createXML(tablespace, context)));
		}

		xml.append("</tablespace_set>\n");

		return xml.toString();
	}

	private String createXML(Tablespace tablespace, PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<tablespace>\n");

		if (context != null) {
			xml.append("\t<id>").append(context.tablespaceMap.get(tablespace))
					.append("</id>\n");
		}
		xml.append("\t<name>").append(escape(tablespace.getName()))
				.append("</name>\n");

		for (Map.Entry<Environment, TablespaceProperties> entry : tablespace
				.getPropertiesMap().entrySet()) {
			Environment environment = entry.getKey();
			TablespaceProperties tablespaceProperties = entry.getValue();

			xml.append("\t<properties>\n");

			xml.append("\t\t<environment_id>")
					.append(context.environmentMap.get(environment))
					.append("</environment_id>\n");

			if (tablespaceProperties instanceof DB2TablespaceProperties) {
				xml.append(tab(tab(this
						.createXML((DB2TablespaceProperties) tablespaceProperties))));

			} else if (tablespaceProperties instanceof MySQLTablespaceProperties) {
				xml.append(tab(tab(this
						.createXML((MySQLTablespaceProperties) tablespaceProperties))));

			} else if (tablespaceProperties instanceof OracleTablespaceProperties) {
				xml.append(tab(tab(this
						.createXML((OracleTablespaceProperties) tablespaceProperties))));

			} else if (tablespaceProperties instanceof PostgresTablespaceProperties) {
				xml.append(tab(tab(this
						.createXML((PostgresTablespaceProperties) tablespaceProperties))));
			}

			xml.append("\t</properties>\n");
		}

		xml.append("</tablespace>\n");

		return xml.toString();
	}

	private String createXML(DB2TablespaceProperties tablespace) {
		StringBuilder xml = new StringBuilder();

		xml.append("<buffer_pool_name>")
				.append(escape(tablespace.getBufferPoolName()))
				.append("</buffer_pool_name>\n");
		xml.append("<container>").append(escape(tablespace.getContainer()))
				.append("</container>\n");
		// xml.append("<container_device_path>").append(
		// escape(tablespace.getContainerDevicePath())).append(
		// "</container_device_path>\n");
		// xml.append("<container_directory_path>").append(
		// escape(tablespace.getContainerDirectoryPath())).append(
		// "</container_directory_path>\n");
		// xml.append("<container_file_path>").append(
		// escape(tablespace.getContainerFilePath())).append(
		// "</container_file_path>\n");
		// xml.append("<container_page_num>").append(
		// escape(tablespace.getContainerPageNum())).append(
		// "</container_page_num>\n");
		xml.append("<extent_size>").append(escape(tablespace.getExtentSize()))
				.append("</extent_size>\n");
		xml.append("<managed_by>").append(escape(tablespace.getManagedBy()))
				.append("</managed_by>\n");
		xml.append("<page_size>").append(escape(tablespace.getPageSize()))
				.append("</page_size>\n");
		xml.append("<prefetch_size>")
				.append(escape(tablespace.getPrefetchSize()))
				.append("</prefetch_size>\n");
		xml.append("<type>").append(escape(tablespace.getType()))
				.append("</type>\n");

		return xml.toString();
	}

	private String createXML(MySQLTablespaceProperties tablespace) {
		StringBuilder xml = new StringBuilder();

		xml.append("<data_file>").append(escape(tablespace.getDataFile()))
				.append("</data_file>\n");
		xml.append("<engine>").append(escape(tablespace.getEngine()))
				.append("</engine>\n");
		xml.append("<extent_size>").append(escape(tablespace.getExtentSize()))
				.append("</extent_size>\n");
		xml.append("<initial_size>")
				.append(escape(tablespace.getInitialSize()))
				.append("</initial_size>\n");
		xml.append("<log_file_group>")
				.append(escape(tablespace.getLogFileGroup()))
				.append("</log_file_group>\n");

		return xml.toString();
	}

	private String createXML(OracleTablespaceProperties tablespace) {
		StringBuilder xml = new StringBuilder();

		xml.append("<auto_extend>").append(tablespace.isAutoExtend())
				.append("</auto_extend>\n");
		xml.append("<auto_segment_space_management>")
				.append(tablespace.isAutoSegmentSpaceManagement())
				.append("</auto_segment_space_management>\n");
		xml.append("<logging>").append(tablespace.isLogging())
				.append("</logging>\n");
		xml.append("<offline>").append(tablespace.isOffline())
				.append("</offline>\n");
		xml.append("<temporary>").append(tablespace.isTemporary())
				.append("</temporary>\n");
		xml.append("<auto_extend_max_size>")
				.append(escape(tablespace.getAutoExtendMaxSize()))
				.append("</auto_extend_max_size>\n");
		xml.append("<auto_extend_size>")
				.append(escape(tablespace.getAutoExtendSize()))
				.append("</auto_extend_size>\n");
		xml.append("<data_file>").append(escape(tablespace.getDataFile()))
				.append("</data_file>\n");
		xml.append("<file_size>").append(escape(tablespace.getFileSize()))
				.append("</file_size>\n");
		xml.append("<initial>").append(escape(tablespace.getInitial()))
				.append("</initial>\n");
		xml.append("<max_extents>").append(escape(tablespace.getMaxExtents()))
				.append("</max_extents>\n");
		xml.append("<min_extents>").append(escape(tablespace.getMinExtents()))
				.append("</min_extents>\n");
		xml.append("<minimum_extent_size>")
				.append(escape(tablespace.getMinimumExtentSize()))
				.append("</minimum_extent_size>\n");
		xml.append("<next>").append(escape(tablespace.getNext()))
				.append("</next>\n");
		xml.append("<pct_increase>")
				.append(escape(tablespace.getPctIncrease()))
				.append("</pct_increase>\n");

		return xml.toString();
	}

	private String createXML(PostgresTablespaceProperties tablespace) {
		StringBuilder xml = new StringBuilder();

		xml.append("<location>").append(escape(tablespace.getLocation()))
				.append("</location>\n");
		xml.append("<owner>").append(escape(tablespace.getOwner()))
				.append("</owner>\n");

		return xml.toString();
	}

	private String createXML(Settings settings, PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<settings>\n");

		xml.append("\t<database>").append(escape(settings.getDatabase()))
				.append("</database>\n");
		xml.append("\t<capital>").append(settings.isCapital())
				.append("</capital>\n");
		xml.append("\t<table_style>").append(escape(settings.getTableStyle()))
				.append("</table_style>\n");
		xml.append("\t<notation>").append(escape(settings.getNotation()))
				.append("</notation>\n");
		xml.append("\t<notation_level>").append(settings.getNotationLevel())
				.append("</notation_level>\n");
		xml.append("\t<notation_expand_group>")
				.append(settings.isNotationExpandGroup())
				.append("</notation_expand_group>\n");
		xml.append("\t<view_mode>").append(settings.getViewMode())
				.append("</view_mode>\n");
		xml.append("\t<outline_view_mode>")
				.append(settings.getOutlineViewMode())
				.append("</outline_view_mode>\n");
		xml.append("\t<view_order_by>").append(settings.getViewOrderBy())
				.append("</view_order_by>\n");

		xml.append("\t<auto_ime_change>").append(settings.isAutoImeChange())
				.append("</auto_ime_change>\n");
		xml.append("\t<validate_physical_name>")
				.append(settings.isValidatePhysicalName())
				.append("</validate_physical_name>\n");
		xml.append("\t<use_bezier_curve>").append(settings.isUseBezierCurve())
				.append("</use_bezier_curve>\n");
		xml.append("\t<suspend_validator>")
				.append(settings.isSuspendValidator())
				.append("</suspend_validator>\n");

		xml.append(tab(this.createXML(settings.getExportSetting(), context)));
		xml.append(tab(this.createXML(settings.getCategorySetting(), context)));
//		xml.append(tab(this.createXML(settings.getGroupSetting(), context)));
		xml.append(tab(this.createXML(settings.getTranslationSetting(), context)));
		xml.append(tab(this.createXML(settings.getModelProperties(), context)));
		xml.append(tab(this.createXML(
				(TableProperties) settings.getTableViewProperties(), context)));
		xml.append(tab(this.createXML(settings.getEnvironmentSetting(), context)));

		xml.append("</settings>\n");

		return xml.toString();
	}

	private String createXML(ColumnGroup columnGroup, PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<column_group>\n");

		xml.append("\t<id>").append(context.columnGroupMap.get(columnGroup))
				.append("</id>\n");

		xml.append("\t<group_name>").append(escape(columnGroup.getGroupName()))
				.append("</group_name>\n");

		xml.append("\t<columns>\n");

		for (NormalColumn normalColumn : columnGroup.getColumns()) {
			xml.append(tab(tab(this.createXML(normalColumn, context))));
		}

		xml.append("\t</columns>\n");

		xml.append("</column_group>\n");

		return xml.toString();
	}

	private String createXML(TestData testData, PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<test_data>\n");

		xml.append("\t<name>").append(escape(testData.getName()))
				.append("</name>\n");
		xml.append("\t<export_order>").append(testData.getExportOrder())
				.append("</export_order>\n");

		Map<ERTable, TableTestData> tableTestDataMap = testData
				.getTableTestDataMap();
		for (Map.Entry<ERTable, TableTestData> entry : tableTestDataMap
				.entrySet()) {
			ERTable table = entry.getKey();
			TableTestData tableTestData = entry.getValue();

			xml.append(tab(createXML(tableTestData, table, context)));
		}

		xml.append("</test_data>\n");

		return xml.toString();
	}

	private String createXML(TableTestData tableTestData, ERTable table,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<table_test_data>\n");

		xml.append("\t<table_id>").append(context.nodeElementMap.get(table))
				.append("</table_id>\n");

		DirectTestData directTestData = tableTestData.getDirectTestData();
		RepeatTestData repeatTestData = tableTestData.getRepeatTestData();

		xml.append(tab(createXML(directTestData, table, context)));
		xml.append(tab(createXML(repeatTestData, table, context)));

		xml.append("</table_test_data>\n");

		return xml.toString();
	}

	private String createXML(DirectTestData directTestData, ERTable table,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<direct_test_data>\n");

		for (Map<NormalColumn, String> data : directTestData.getDataList()) {
			xml.append("\t<data>\n");
			for (NormalColumn normalColumn : table.getExpandedColumns()) {
				xml.append("\t\t<column_data>\n");
				xml.append("\t\t\t<column_id>")
						.append(context.columnMap.get(normalColumn))
						.append("</column_id>\n");
				xml.append("\t\t\t<value>")
						.append(escape(data.get(normalColumn)))
						.append("</value>\n");
				xml.append("\t\t</column_data>\n");
			}
			xml.append("\t</data>\n");
		}

		xml.append("</direct_test_data>\n");

		return xml.toString();
	}

	private String createXML(RepeatTestData repeatTestData, ERTable table,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<repeat_test_data>\n");
		xml.append("\t<test_data_num>").append(repeatTestData.getTestDataNum())
				.append("</test_data_num>\n");
		xml.append("\t<data_def_list>\n");

		for (NormalColumn normalColumn : table.getExpandedColumns()) {
			xml.append(tab(tab(this.createXML(
					repeatTestData.getDataDef(normalColumn), normalColumn,
					context))));
		}

		xml.append("\t</data_def_list>\n");
		xml.append("</repeat_test_data>\n");

		return xml.toString();
	}

	private String createXML(RepeatTestDataDef repeatTestDataDef,
			NormalColumn column, PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		Integer columnId = context.columnMap.get(column);

		if (columnId != null) {
			xml.append("<data_def>\n");
			xml.append("\t<column_id>").append(columnId)
					.append("</column_id>\n");
			xml.append("\t<type>").append(escape(repeatTestDataDef.getType()))
					.append("</type>\n");
			xml.append("\t<repeat_num>")
					.append(Format.toString((repeatTestDataDef.getRepeatNum())))
					.append("</repeat_num>\n");
			xml.append("\t<template>")
					.append(escape(repeatTestDataDef.getTemplate()))
					.append("</template>\n");
			xml.append("\t<from>")
					.append(Format.toString((repeatTestDataDef.getFrom())))
					.append("</from>\n");
			xml.append("\t<to>")
					.append(Format.toString((repeatTestDataDef.getTo())))
					.append("</to>\n");
			xml.append("\t<increment>")
					.append(Format.toString((repeatTestDataDef.getIncrement())))
					.append("</increment>\n");
			for (String select : repeatTestDataDef.getSelects()) {
				xml.append("\t<select>").append(escape(select))
						.append("</select>\n");
			}
			xml.append("\t<modified_values>\n");
			for (Integer modifiedRow : repeatTestDataDef.getModifiedValues()
					.keySet()) {
				xml.append("\t\t<modified_value>\n");
				xml.append("\t\t\t<row>").append(modifiedRow)
						.append("</row>\n");
				xml.append("\t\t\t<value>")
						.append(escape(repeatTestDataDef.getModifiedValues()
								.get(modifiedRow))).append("</value>\n");
				xml.append("\t\t</modified_value>\n");
			}
			xml.append("\t</modified_values>\n");

			xml.append("</data_def>\n");
		}

		return xml.toString();
	}

	private String createXML(ChangeTrackingList changeTrackingList) {
		StringBuilder xml = new StringBuilder();

		xml.append("<change_tracking_list>\n");

		for (ChangeTracking changeTracking : changeTrackingList.getList()) {
			xml.append(tab(this.createXML(changeTracking)));
		}

		xml.append("</change_tracking_list>\n");

		return xml.toString();
	}

	private String createXML(ChangeTracking changeTracking) {
		StringBuilder xml = new StringBuilder();

		xml.append("<change_tracking>\n");

		xml.append("\t<updated_date>")
				.append(DATE_FORMAT.format(changeTracking.getUpdatedDate()))
				.append("</updated_date>\n");
		xml.append("\t<comment>").append(escape(changeTracking.getComment()))
				.append("</comment>\n");

		PersistentContext context = this
				.getChangeTrackingContext(changeTracking);

		xml.append(tab(this.createXML(changeTracking.getDiagramContents(),
				context)));

		xml.append("</change_tracking>\n");

		return xml.toString();
	}

	private String createXML(ExportSetting exportSetting,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<export_setting>\n");

		xml.append("\t<category_name_to_export>")
				.append(escape(exportSetting.getCategoryNameToExport()))
				.append("</category_name_to_export>\n");
		xml.append("\t<ddl_output>")
				.append(escape(exportSetting.getDdlOutput()))
				.append("</ddl_output>\n");
		xml.append("\t<excel_output>")
				.append(escape(exportSetting.getExcelOutput()))
				.append("</excel_output>\n");
		xml.append("\t<excel_template>")
				.append(escape(exportSetting.getExcelTemplate()))
				.append("</excel_template>\n");
		xml.append("\t<image_output>")
				.append(escape(exportSetting.getImageOutput()))
				.append("</image_output>\n");
		xml.append("\t<put_diagram_on_excel>")
				.append(exportSetting.isPutERDiagramOnExcel())
				.append("</put_diagram_on_excel>\n");
		xml.append("\t<use_logical_name_as_sheet>")
				.append(exportSetting.isUseLogicalNameAsSheet())
				.append("</use_logical_name_as_sheet>\n");
		xml.append("\t<open_after_saved>")
				.append(exportSetting.isOpenAfterSaved())
				.append("</open_after_saved>\n");

		DDLTarget ddlTarget = exportSetting.getDdlTarget();

		xml.append("\t<create_comment>").append(ddlTarget.createComment)
				.append("</create_comment>\n");
		xml.append("\t<create_foreignKey>").append(ddlTarget.createForeignKey)
				.append("</create_foreignKey>\n");
		xml.append("\t<create_index>").append(ddlTarget.createIndex)
				.append("</create_index>\n");
		xml.append("\t<create_sequence>").append(ddlTarget.createSequence)
				.append("</create_sequence>\n");
		xml.append("\t<create_table>").append(ddlTarget.createTable)
				.append("</create_table>\n");
		xml.append("\t<create_tablespace>").append(ddlTarget.createTablespace)
				.append("</create_tablespace>\n");
		xml.append("\t<create_trigger>").append(ddlTarget.createTrigger)
				.append("</create_trigger>\n");
		xml.append("\t<create_view>").append(ddlTarget.createView)
				.append("</create_view>\n");

		xml.append("\t<drop_index>").append(ddlTarget.dropIndex)
				.append("</drop_index>\n");
		xml.append("\t<drop_sequence>").append(ddlTarget.dropSequence)
				.append("</drop_sequence>\n");
		xml.append("\t<drop_table>").append(ddlTarget.dropTable)
				.append("</drop_table>\n");
		xml.append("\t<drop_tablespace>").append(ddlTarget.dropTablespace)
				.append("</drop_tablespace>\n");
		xml.append("\t<drop_trigger>").append(ddlTarget.dropTrigger)
				.append("</drop_trigger>\n");
		xml.append("\t<drop_view>").append(ddlTarget.dropView)
				.append("</drop_view>\n");

		xml.append("\t<inline_column_comment>")
				.append(ddlTarget.inlineColumnComment)
				.append("</inline_column_comment>\n");
		xml.append("\t<inline_table_comment>")
				.append(ddlTarget.inlineTableComment)
				.append("</inline_table_comment>\n");

		xml.append("\t<comment_value_description>")
				.append(ddlTarget.commentValueDescription)
				.append("</comment_value_description>\n");
		xml.append("\t<comment_value_logical_name>")
				.append(ddlTarget.commentValueLogicalName)
				.append("</comment_value_logical_name>\n");
		xml.append("\t<comment_value_logical_name_description>")
				.append(ddlTarget.commentValueLogicalNameDescription)
				.append("</comment_value_logical_name_description>\n");
		xml.append("\t<comment_replace_line_feed>")
				.append(ddlTarget.commentReplaceLineFeed)
				.append("</comment_replace_line_feed>\n");
		xml.append("\t<comment_replace_string>")
				.append(Format.null2blank(ddlTarget.commentReplaceString))
				.append("</comment_replace_string>\n");

		xml.append(tab(this.createXML(exportSetting.getExportJavaSetting(),
				context)));
		xml.append(tab(this.createXML(exportSetting.getExportTestDataSetting(),
				context)));

		xml.append("</export_setting>\n");

		return xml.toString();
	}

	private String createXML(ExportJavaSetting exportJavaSetting,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<export_java_setting>\n");

		xml.append("\t<java_output>")
				.append(escape(exportJavaSetting.getJavaOutput()))
				.append("</java_output>\n");
		xml.append("\t<package_name>")
				.append(escape(exportJavaSetting.getPackageName()))
				.append("</package_name>\n");
		xml.append("\t<class_name_suffix>")
				.append(escape(exportJavaSetting.getClassNameSuffix()))
				.append("</class_name_suffix>\n");
		xml.append("\t<src_file_encoding>")
				.append(escape(exportJavaSetting.getSrcFileEncoding()))
				.append("</src_file_encoding>\n");
		xml.append("\t<with_hibernate>")
				.append(exportJavaSetting.isWithHibernate())
				.append("</with_hibernate>\n");

		xml.append("</export_java_setting>\n");

		return xml.toString();
	}

	private String createXML(ExportTestDataSetting exportTestDataSetting,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<export_testdata_setting>\n");

		xml.append("\t<file_encoding>")
				.append(escape(exportTestDataSetting.getExportFileEncoding()))
				.append("</file_encoding>\n");
		xml.append("\t<file_path>")
				.append(escape(exportTestDataSetting.getExportFilePath()))
				.append("</file_path>\n");
		xml.append("\t<format>")
				.append(exportTestDataSetting.getExportFormat())
				.append("</format>\n");

		xml.append("</export_testdata_setting>\n");

		return xml.toString();
	}

	private String createXML(CategorySetting categorySettings,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<category_settings>\n");
		xml.append("\t<free_layout>").append(categorySettings.isFreeLayout())
				.append("</free_layout>\n");
		xml.append("\t<show_referred_tables>")
				.append(categorySettings.isShowReferredTables())
				.append("</show_referred_tables>\n");

		xml.append("\t<categories>\n");

		for (Category category : categorySettings.getAllCategories()) {
			xml.append(tab(tab(this.createXML(category,
					categorySettings.isSelected(category), context))));
		}

		xml.append("\t</categories>\n");

		xml.append("</category_settings>\n");

		return xml.toString();
	}


//	private String createXML(VGroupSetting groupSettings, PersistentContext context) {
//		StringBuilder xml = new StringBuilder();
//
//		xml.append("<group_settings>\n");
//		xml.append("\t<free_layout>").append(groupSettings.isFreeLayout()).append("</free_layout>\n");
//		xml.append("\t<show_referred_tables>").append(groupSettings.isShowReferredTables()).append("</show_referred_tables>\n");
//
//		xml.append("\t<groups>\n");
//		for (VGroup group : groupSettings.getAllGroups()) {
//			xml.append(tab(tab(this.createXML(group, groupSettings.isSelected(group), context))));
//		}
//		xml.append("\t</groups>\n");
//
//		xml.append("</group_settings>\n");
//
//		return xml.toString();
//	}

	private String createXML(TranslationSetting translationSettings,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<translation_settings>\n");
		xml.append("\t<use>").append(translationSettings.isUse())
				.append("</use>\n");

		xml.append("\t<translations>\n");

		for (String translation : translationSettings.getSelectedTranslations()) {
			xml.append(tab(tab(this.createTranslationXML(translation, context))));
		}

		xml.append("\t</translations>\n");

		xml.append("</translation_settings>\n");

		return xml.toString();
	}

	private String createXML(Category category, boolean isSelected,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<category>\n");

		xml.append(tab(this.createXMLNodeElement(category, context)));

		xml.append("\t<name>").append(escape(category.getName()))
				.append("</name>\n");
		xml.append("\t<selected>").append(isSelected).append("</selected>\n");

		for (NodeElement nodeElement : category.getContents()) {
			xml.append("\t<node_element>")
					.append(context.nodeElementMap.get(nodeElement))
					.append("</node_element>\n");
		}

		xml.append("</category>\n");

		return xml.toString();
	}

	
	private String createXML(VGroup group, PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<group>\n");

		xml.append(tab(this.createXMLNodeElement(group, context)));

		xml.append("\t<name>").append(escape(group.getName())).append("</name>\n");
//		xml.append("\t<selected>").append(isSelected).append("</selected>\n");

		for (NodeElement nodeElement : group.getContents()) {
			xml.append("\t<node_element>")
					.append(context.nodeElementMap.get(((ERVirtualTable)nodeElement).getRawTable()))
					.append("</node_element>\n");
		}

		xml.append("</group>\n");

		return xml.toString();
	}

//	private String createXML(VGroup group, boolean isSelected,
//			PersistentContext context) {
//		StringBuilder xml = new StringBuilder();
//
//		xml.append("<group>\n");
//
//		xml.append(tab(this.createXMLNodeElement(group, context)));
//
//		xml.append("\t<name>").append(escape(group.getName()))
//				.append("</name>\n");
//		xml.append("\t<selected>").append(isSelected).append("</selected>\n");
//
//		for (NodeElement nodeElement : group.getContents()) {
//			xml.append("\t<node_element>")
//					.append(context.nodeElementMap.get(nodeElement))
//					.append("</node_element>\n");
//		}
//
//		xml.append("</group>\n");
//
//		return xml.toString();
//	}

	private String createTranslationXML(String translation,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<translation>\n");
		xml.append("\t<name>").append(escape(translation)).append("</name>\n");
		xml.append("</translation>\n");

		return xml.toString();
	}

	private String createXML(NodeSet contents, PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<contents>\n");

		for (NodeElement content : contents) {
			String subxml = null;

			if (content instanceof ERTable) {
				subxml = this.createXML((ERTable) content, context);

			} else if (content instanceof ERModel) {
				// do nothing
//				subxml = this.createXMLERModel((ERModel) content, context);
				
			} else if (content instanceof Note) {
//				subxml = this.createXML((Note) content, context);

			} else if (content instanceof View) {
				subxml = this.createXML((View) content, context);

			} else if (content instanceof InsertedImage) {
				subxml = this.createXML((InsertedImage) content, context);

			} else if (content instanceof VGroup) {
				// do nothing
//				subxml = this.createXML((VGroup) content, context);

			} else {
				throw new RuntimeException("not support " + content);
			}

			if (subxml != null) xml.append(tab(subxml));
		}

		xml.append("</contents>\n");

		return xml.toString();
	}

	private String createXMLERModel(ERModelSet modelSet, PersistentContext context) {
		StringBuilder xml = new StringBuilder();
		xml.append("<ermodels>\n");
		
		for (ERModel erModel : modelSet) {
			xml.append("\t<ermodel>\n");
			xml.append("\t\t<id>").append(context.ermodelMap.get(erModel)).append("</id>\n");
			xml.append("\t\t<name>").append(erModel.getName()).append("</name>\n");
			appendColor(xml, "color", erModel.getColor());
			
			xml.append("\t\t<vtables>\n");
			for (ERVirtualTable table : erModel.getTables()) {
				xml.append("\t\t\t<vtable>\n");
				xml.append("\t\t\t\t<id>").append(context.nodeElementMap.get(table.getRawTable())).append("</id>\n");
				xml.append("\t\t\t\t<x>").append(table.getX()).append("</x>\n");
				xml.append("\t\t\t\t<y>").append(table.getY()).append("</y>\n");
				appendFont(xml, table);
				xml.append("\t\t\t</vtable>\n");
			}
			xml.append("\t\t</vtables>\n");

			xml.append("\t\t<groups>\n");
			for (VGroup group : erModel.getGroups()) {
				xml.append(createXML(group, context));
			}
			xml.append("\t\t</groups>\n");

			xml.append("\t\t<notes>\n");
			for (Note note : erModel.getNotes()) {
				xml.append(createXML(note, context));
			}
			xml.append("\t\t</notes>\n");

			xml.append("\t</ermodel>\n");
		}
		
		xml.append("</ermodels>\n");
		return xml.toString();
	}

	private void appendFont(StringBuilder xml, NodeElement nodeElement) {
		xml.append("\t<font_name>").append(escape(nodeElement.getFontName())).append("</font_name>\n");
		xml.append("\t<font_size>").append(nodeElement.getFontSize()).append("</font_size>\n");
	}

//	private String createXMLERModel(ERModel erModel, PersistentContext context) {
//		StringBuilder xml = new StringBuilder();
////		xml.append("<ermodels>\n");
////		
////		for (ERModel erModel : ermodels) {
//			xml.append("\t<ermodel>\n");
//			xml.append("\t\t<id>").append(context.nodeElementMap.get(erModel)).append("</id>\n");
//			xml.append("\t\t<name>").append(erModel.getName()).append("</name>\n");
//			xml.append("\t\t<vtables>\n");
//			for (ERVirtualTable table : erModel.getTables()) {
//				xml.append("\t\t\t<vtable>\n");
//				xml.append("\t\t\t\t<id>").append(context.nodeElementMap.get(table.getRawTable())).append("</id>\n");
//				xml.append("\t\t\t\t<x>").append(table.getX()).append("</x>\n");
//				xml.append("\t\t\t\t<y>").append(table.getY()).append("</y>\n");
//				xml.append("\t\t\t</vtable>\n");
//			}
//			xml.append("\t\t</vtables>\n");
//			xml.append("\t</ermodel>\n");
////		}
////		
////		xml.append("</ermodels>\n");
//		return xml.toString();
//	}

	private String createXMLNodeElement(NodeElement nodeElement,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<id>")
				.append(Format.toString(context.nodeElementMap.get(nodeElement)))
				.append("</id>\n");
		xml.append("<height>").append(nodeElement.getHeight())
				.append("</height>\n");
		xml.append("<width>").append(nodeElement.getWidth())
				.append("</width>\n");
		xml.append("\t<font_name>").append(escape(nodeElement.getFontName()))
				.append("</font_name>\n");
		xml.append("\t<font_size>").append(nodeElement.getFontSize())
				.append("</font_size>\n");
		xml.append("<x>").append(nodeElement.getX()).append("</x>\n");
		xml.append("<y>").append(nodeElement.getY()).append("</y>\n");
		xml.append(this.createXMLColor(nodeElement.getColor()));

		List<ConnectionElement> incomings = nodeElement.getIncomings();
		xml.append(this.createXMLConnections(incomings, context));

		return xml.toString();
	}

	private String createXMLColor(int[] colors) {
		StringBuilder xml = new StringBuilder();

		if (colors != null) {
			xml.append("<color>\n");
			xml.append("\t<r>").append(colors[0]).append("</r>\n");
			xml.append("\t<g>").append(colors[1]).append("</g>\n");
			xml.append("\t<b>").append(colors[2]).append("</b>\n");
			xml.append("</color>\n");
		}

		return xml.toString();
	}

	private String createXML(ERTable table, PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<table>\n");

		xml.append(tab(this.createXMLNodeElement(table, context)));

		xml.append("\t<physical_name>").append(escape(table.getPhysicalName()))
				.append("</physical_name>\n");
		xml.append("\t<logical_name>").append(escape(table.getLogicalName()))
				.append("</logical_name>\n");
		xml.append("\t<description>").append(escape(table.getDescription()))
				.append("</description>\n");
		xml.append("\t<constraint>").append(escape(table.getConstraint()))
				.append("</constraint>\n");
		xml.append("\t<primary_key_name>")
				.append(escape(table.getPrimaryKeyName()))
				.append("</primary_key_name>\n");
		xml.append("\t<option>").append(escape(table.getOption()))
				.append("</option>\n");

		List<Column> columns = table.getColumns();
		xml.append(tab(this.createXMLColumns(columns, context)));

		List<Index> indexes = table.getIndexes();
		xml.append(tab(this.createXMLIndexes(indexes, context)));

		List<ComplexUniqueKey> complexUniqueKeyList = table
				.getComplexUniqueKeyList();
		xml.append(tab(this.createXMLComplexUniqueKeyList(complexUniqueKeyList,
				context)));

		TableProperties tableProperties = (TableProperties) table
				.getTableViewProperties();
		xml.append(tab(this.createXML(tableProperties, context)));

		xml.append("</table>\n");

		return xml.toString();
	}

	private String createXML(View view, PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<view>\n");

		xml.append(tab(this.createXMLNodeElement(view, context)));

		xml.append("\t<physical_name>").append(escape(view.getPhysicalName()))
				.append("</physical_name>\n");
		xml.append("\t<logical_name>").append(escape(view.getLogicalName()))
				.append("</logical_name>\n");
		xml.append("\t<description>").append(escape(view.getDescription()))
				.append("</description>\n");
		xml.append("\t<sql>").append(escape(view.getSql())).append("</sql>\n");

		List<Column> columns = view.getColumns();
		xml.append(tab(this.createXMLColumns(columns, context)));

		ViewProperties viewProperties = (ViewProperties) view
				.getTableViewProperties();
		xml.append(tab(this.createXML(viewProperties, context)));

		xml.append("</view>\n");

		return xml.toString();
	}

	private String createXML(ModelProperties modelProperties,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<model_properties>\n");

		xml.append(tab(this.createXMLNodeElement(modelProperties, context)));

		xml.append("\t<display>").append(modelProperties.isDisplay())
				.append("</display>\n");
		xml.append("\t<creation_date>")
				.append(DATE_FORMAT.format(modelProperties.getCreationDate()))
				.append("</creation_date>\n");
		xml.append("\t<updated_date>")
				.append(DATE_FORMAT.format(modelProperties.getUpdatedDate()))
				.append("</updated_date>\n");

		for (NameValue property : modelProperties.getProperties()) {
			xml.append(tab(this.createXML(property, context)));
		}

		xml.append("</model_properties>\n");

		return xml.toString();
	}

	private String createXML(NameValue property, PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<model_property>\n");

		xml.append("\t<name>").append(escape(property.getName()))
				.append("</name>\n");
		xml.append("\t<value>").append(escape(property.getValue()))
				.append("</value>\n");

		xml.append("</model_property>\n");

		return xml.toString();
	}

	private String createXML(Note note, PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<note>\n");

		xml.append(tab(this.createXMLNodeElement(note, context)));
		xml.append("\t<text>").append(escape(note.getText()))
				.append("</text>\n");

		xml.append("</note>\n");

		return xml.toString();
	}

	private String createXML(InsertedImage insertedImage,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<image>\n");

		xml.append(tab(this.createXMLNodeElement(insertedImage, context)));
		xml.append("\t<data>").append(insertedImage.getBase64EncodedData())
				.append("</data>\n");
		xml.append("\t<hue>").append(insertedImage.getHue()).append("</hue>\n");
		xml.append("\t<saturation>").append(insertedImage.getSaturation())
				.append("</saturation>\n");
		xml.append("\t<brightness>").append(insertedImage.getBrightness())
				.append("</brightness>\n");
		xml.append("\t<alpha>").append(insertedImage.getAlpha())
				.append("</alpha>\n");
		xml.append("\t<fix_aspect_ratio>")
				.append(insertedImage.isFixAspectRatio())
				.append("</fix_aspect_ratio>\n");

		xml.append("</image>\n");

		return xml.toString();
	}

	private String createXMLColumns(List<Column> columns,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<columns>\n");

		for (Column column : columns) {

			if (column instanceof ColumnGroup) {
				xml.append(tab(this.createXMLId((ColumnGroup) column, context)));

			} else if (column instanceof NormalColumn) {
				xml.append(tab(this.createXML((NormalColumn) column, context)));

			}
		}

		xml.append("</columns>\n");

		return xml.toString();
	}

	private String createXMLId(ColumnGroup columnGroup,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<column_group>")
				.append(context.columnGroupMap.get(columnGroup))
				.append("</column_group>\n");

		return xml.toString();
	}

	private String createXML(NormalColumn normalColumn,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<normal_column>\n");

		Integer wordId = null;

		if (context != null) {
			wordId = context.wordMap.get(normalColumn.getWord());
			if (wordId != null) {
				xml.append("\t<word_id>").append(wordId).append("</word_id>\n");
			}

			xml.append("\t<id>").append(context.columnMap.get(normalColumn))
					.append("</id>\n");
			for (NormalColumn referencedColumn : normalColumn
					.getReferencedColumnList()) {
				xml.append("\t<referenced_column>")
						.append(Format.toString(context.columnMap
								.get(referencedColumn)))
						.append("</referenced_column>\n");
			}
			for (Relation relation : normalColumn.getRelationList()) {
				xml.append("\t<relation>")
						.append(context.connectionMap.get(relation))
						.append("</relation>\n");
			}
		}

		String description = normalColumn.getForeignKeyDescription();
		String logicalName = normalColumn.getForeignKeyLogicalName();
		String physicalName = normalColumn.getForeignKeyPhysicalName();
		SqlType sqlType = normalColumn.getType();

		xml.append("\t<description>").append(escape(description))
				.append("</description>\n");
		xml.append("\t<unique_key_name>")
				.append(escape(normalColumn.getUniqueKeyName()))
				.append("</unique_key_name>\n");
		xml.append("\t<logical_name>").append(escape(logicalName))
				.append("</logical_name>\n");
		xml.append("\t<physical_name>").append(escape(physicalName))
				.append("</physical_name>\n");

		String type = "";
		if (sqlType != null) {
			type = sqlType.getId();
		}
		xml.append("\t<type>").append(type).append("</type>\n");

		xml.append("\t<constraint>")
				.append(escape(normalColumn.getConstraint()))
				.append("</constraint>\n");
		xml.append("\t<default_value>")
				.append(escape(normalColumn.getDefaultValue()))
				.append("</default_value>\n");

		xml.append("\t<auto_increment>").append(normalColumn.isAutoIncrement())
				.append("</auto_increment>\n");
		xml.append("\t<foreign_key>").append(normalColumn.isForeignKey())
				.append("</foreign_key>\n");
		xml.append("\t<not_null>").append(normalColumn.isNotNull())
				.append("</not_null>\n");
		xml.append("\t<primary_key>").append(normalColumn.isPrimaryKey())
				.append("</primary_key>\n");
		xml.append("\t<unique_key>").append(normalColumn.isUniqueKey())
				.append("</unique_key>\n");

		xml.append("\t<character_set>")
				.append(escape(normalColumn.getCharacterSet()))
				.append("</character_set>\n");
		xml.append("\t<collation>").append(escape(normalColumn.getCollation()))
				.append("</collation>\n");

		xml.append(tab(this.createXML(normalColumn.getAutoIncrementSetting())));
		xml.append("</normal_column>\n");

		return xml.toString();
	}

	private String createXMLConnections(List<ConnectionElement> incomings,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<connections>\n");

		for (ConnectionElement connection : incomings) {

			if (connection instanceof CommentConnection) {
				xml.append(tab(this.createXML((CommentConnection) connection,
						context)));

			} else if (connection instanceof Relation) {
				xml.append(tab(this.createXML((Relation) connection, context)));
			}

		}

		xml.append("</connections>\n");

		return xml.toString();
	}

	private String createXMLConnectionElement(ConnectionElement connection,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<id>").append(context.connectionMap.get(connection))
				.append("</id>\n");
		xml.append("<source>")
				.append(context.nodeElementMap.get(connection.getSource()))
				.append("</source>\n");
		xml.append("<target>")
				.append(context.nodeElementMap.get(connection.getTarget()))
				.append("</target>\n");

		for (Bendpoint bendpoint : connection.getBendpoints()) {
			xml.append(tab(this.createXML(bendpoint)));
		}

		return xml.toString();
	}

	private String createXML(Bendpoint bendpoint) {
		StringBuilder xml = new StringBuilder();

		xml.append("<bendpoint>\n");

		xml.append("\t<relative>").append(bendpoint.isRelative())
				.append("</relative>\n");
		xml.append("\t<x>").append(bendpoint.getX()).append("</x>\n");
		xml.append("\t<y>").append(bendpoint.getY()).append("</y>\n");

		xml.append("</bendpoint>\n");

		return xml.toString();
	}

	private String createXML(CommentConnection connection,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<comment_connection>\n");

		xml.append(tab(this.createXMLConnectionElement(connection, context)));

		xml.append("</comment_connection>\n");

		return xml.toString();
	}

	private String createXML(Relation relation, PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<relation>\n");

		xml.append(tab(this.createXMLConnectionElement(relation, context)));

		xml.append("\t<child_cardinality>")
				.append(escape(relation.getChildCardinality()))
				.append("</child_cardinality>\n");
		xml.append("\t<parent_cardinality>")
				.append(escape(relation.getParentCardinality()))
				.append("</parent_cardinality>\n");
		xml.append("\t<reference_for_pk>").append(relation.isReferenceForPK())
				.append("</reference_for_pk>\n");
		xml.append("\t<name>").append(escape(relation.getName()))
				.append("</name>\n");
		xml.append("\t<on_delete_action>")
				.append(escape(relation.getOnDeleteAction()))
				.append("</on_delete_action>\n");
		xml.append("\t<on_update_action>")
				.append(escape(relation.getOnUpdateAction()))
				.append("</on_update_action>\n");
		xml.append("\t<source_xp>").append(relation.getSourceXp())
				.append("</source_xp>\n");
		xml.append("\t<source_yp>").append(relation.getSourceYp())
				.append("</source_yp>\n");
		xml.append("\t<target_xp>").append(relation.getTargetXp())
				.append("</target_xp>\n");
		xml.append("\t<target_yp>").append(relation.getTargetYp())
				.append("</target_yp>\n");
		xml.append("\t<referenced_column>")
				.append(context.columnMap.get(relation.getReferencedColumn()))
				.append("</referenced_column>\n");
		xml.append("\t<referenced_complex_unique_key>")
				.append(context.complexUniqueKeyMap.get(relation
						.getReferencedComplexUniqueKey()))
				.append("</referenced_complex_unique_key>\n");

		xml.append("</relation>\n");

		return xml.toString();
	}

	private String createXMLIndexes(List<Index> indexes,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<indexes>\n");

		for (Index index : indexes) {
			xml.append(tab(this.createXML(index, context)));
		}

		xml.append("</indexes>\n");

		return xml.toString();
	}

	private String createXMLComplexUniqueKeyList(
			List<ComplexUniqueKey> complexUniqueKeyList,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<complex_unique_key_list>\n");

		for (ComplexUniqueKey complexUniqueKey : complexUniqueKeyList) {
			xml.append(tab(this.createXML(complexUniqueKey, context)));
		}

		xml.append("</complex_unique_key_list>\n");

		return xml.toString();
	}

	private String createXML(EnvironmentSetting environmentSetting,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<environment_setting>\n");

		for (Environment environment : environmentSetting.getEnvironments()) {
			xml.append("\t<environment>\n");

			Integer environmentId = context.environmentMap.get(environment);
			xml.append("\t\t<id>").append(environmentId).append("</id>\n");
			xml.append("\t\t<name>").append(environment.getName())
					.append("</name>\n");

			xml.append("\t</environment>\n");
		}

		xml.append("</environment_setting>\n");

		return xml.toString();
	}

	private String createXML(TableProperties tableProperties,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<table_properties>\n");

		Integer tablespaceId = context.tablespaceMap.get(tableProperties
				.getTableSpace());
		if (tablespaceId != null) {
			xml.append("\t<tablespace_id>").append(tablespaceId)
					.append("</tablespace_id>\n");
		}

		xml.append("\t<schema>").append(escape(tableProperties.getSchema()))
				.append("</schema>\n");

		if (tableProperties instanceof MySQLTableProperties) {
			xml.append(tab(this
					.createXML((MySQLTableProperties) tableProperties)));

		} else if (tableProperties instanceof PostgresTableProperties) {
			xml.append(tab(this
					.createXML((PostgresTableProperties) tableProperties)));
		}

		xml.append("</table_properties>\n");

		return xml.toString();
	}

	private String createXML(MySQLTableProperties tableProperties) {
		StringBuilder xml = new StringBuilder();

		xml.append("<character_set>")
				.append(escape(tableProperties.getCharacterSet()))
				.append("</character_set>\n");
		xml.append("<collation>")
				.append(escape(tableProperties.getCollation()))
				.append("</collation>\n");
		xml.append("<storage_engine>")
				.append(escape(tableProperties.getStorageEngine()))
				.append("</storage_engine>\n");
		xml.append("<primary_key_length_of_text>")
				.append(tableProperties.getPrimaryKeyLengthOfText())
				.append("</primary_key_length_of_text>\n");

		return xml.toString();
	}

	private String createXML(PostgresTableProperties tableProperties) {
		StringBuilder xml = new StringBuilder();

		xml.append("<without_oids>").append(tableProperties.isWithoutOIDs())
				.append("</without_oids>\n");

		return xml.toString();
	}

	private String createXML(ViewProperties viewProperties,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<view_properties>\n");

		Integer tablespaceId = context.tablespaceMap.get(viewProperties
				.getTableSpace());
		if (tablespaceId != null) {
			xml.append("\t<tablespace_id>").append(tablespaceId)
					.append("</tablespace_id>\n");
		}

		xml.append("<schema>").append(escape(viewProperties.getSchema()))
				.append("</schema>\n");

		xml.append("</view_properties>\n");

		return xml.toString();
	}

	private String createXML(Index index, PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<inidex>\n");

		xml.append("\t<full_text>").append(index.isFullText())
				.append("</full_text>\n");
		xml.append("\t<non_unique>").append(index.isNonUnique())
				.append("</non_unique>\n");
		xml.append("\t<name>").append(escape(index.getName()))
				.append("</name>\n");
		xml.append("\t<type>").append(escape(index.getType()))
				.append("</type>\n");
		xml.append("\t<description>").append(escape(index.getDescription()))
				.append("</description>\n");

		xml.append("\t<columns>\n");

		List<Boolean> descs = index.getDescs();

		int count = 0;

		for (Column column : index.getColumns()) {
			xml.append("\t\t<column>\n");
			xml.append("\t\t\t<id>").append(context.columnMap.get(column))
					.append("</id>\n");

			Boolean desc = Boolean.FALSE;

			if (descs.size() > count) {
				desc = descs.get(count);
			}
			xml.append("\t\t\t<desc>").append(desc).append("</desc>\n");
			xml.append("\t\t</column>\n");

			count++;
		}

		xml.append("\t</columns>\n");

		xml.append("</inidex>\n");

		return xml.toString();
	}

	private String createXML(ComplexUniqueKey complexUniqueKey,
			PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<complex_unique_key>\n");

		xml.append("\t<id>")
				.append(context.complexUniqueKeyMap.get(complexUniqueKey))
				.append("</id>\n");
		xml.append("\t<name>")
				.append(Format.null2blank(complexUniqueKey.getUniqueKeyName()))
				.append("</name>\n");
		xml.append("\t<columns>\n");

		for (NormalColumn column : complexUniqueKey.getColumnList()) {
			xml.append("\t\t<column>\n");
			xml.append("\t\t\t<id>").append(context.columnMap.get(column))
					.append("</id>\n");
			xml.append("\t\t</column>\n");
		}

		xml.append("\t</columns>\n");

		xml.append("</complex_unique_key>\n");

		return xml.toString();
	}

	private String createXML(Dictionary dictionary, PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<dictionary>\n");

		for (Word word : dictionary.getWordList()) {
			xml.append(tab(this.createXML(word, context)));
		}

		xml.append("</dictionary>\n");

		return xml.toString();
	}

	private String createXML(Word word, PersistentContext context) {
		StringBuilder xml = new StringBuilder();

		xml.append("<word>\n");

		if (context != null) {
			xml.append("\t<id>").append(context.wordMap.get(word))
					.append("</id>\n");
		}

		xml.append("\t<length>").append(word.getTypeData().getLength())
				.append("</length>\n");
		xml.append("\t<decimal>").append(word.getTypeData().getDecimal())
				.append("</decimal>\n");

		Integer arrayDimension = word.getTypeData().getArrayDimension();
		xml.append("\t<array>").append(word.getTypeData().isArray())
				.append("</array>\n");
		xml.append("\t<array_dimension>").append(arrayDimension)
				.append("</array_dimension>\n");

		xml.append("\t<unsigned>").append(word.getTypeData().isUnsigned())
				.append("</unsigned>\n");
		xml.append("\t<args>").append(escape(word.getTypeData().getArgs()))
				.append("</args>\n");

		xml.append("\t<description>").append(escape(word.getDescription()))
				.append("</description>\n");
		xml.append("\t<logical_name>").append(escape(word.getLogicalName()))
				.append("</logical_name>\n");
		xml.append("\t<physical_name>").append(escape(word.getPhysicalName()))
				.append("</physical_name>\n");

		String type = "";
		if (word.getType() != null) {
			type = word.getType().getId();
		}
		xml.append("\t<type>").append(type).append("</type>\n");

		xml.append("</word>\n");

		return xml.toString();
	}

	public static String escape(String s) {
		if (s == null) {
			return "";
		}

		StringBuilder result = new StringBuilder(s.length() + 10);
		for (int i = 0; i < s.length(); ++i) {
			appendEscapedChar(result, s.charAt(i));
		}
		return result.toString();
	}

	private static void appendEscapedChar(StringBuilder buffer, char c) {
		String replacement = getReplacement(c);
		if (replacement != null) {
			buffer.append('&');
			buffer.append(replacement);
			buffer.append(';');
		} else {
			buffer.append(c);
		}
	}

	private static String getReplacement(char c) {
		// Encode special XML characters into the equivalent character
		// references.
		// The first five are defined by default for all XML documents.
		// The next three (#xD, #xA, #x9) are encoded to avoid them
		// being converted to spaces on deserialization
		switch (c) {
		case '<':
			return "lt"; //$NON-NLS-1$
		case '>':
			return "gt"; //$NON-NLS-1$
		case '"':
			return "quot"; //$NON-NLS-1$
		case '\'':
			return "apos"; //$NON-NLS-1$
		case '&':
			return "amp"; //$NON-NLS-1$
		case '\r':
			return "#x0D"; //$NON-NLS-1$
		case '\n':
			return "#x0A"; //$NON-NLS-1$
		case '\u0009':
			return "#x09"; //$NON-NLS-1$
		}
		return null;
	}

}