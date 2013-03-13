package org.insightech.er.editor.model.dbexport.excel.sheet_generator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.eclipse.core.runtime.IProgressMonitor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.dbexport.excel.ExportToExcelManager.LoopDefinition;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.util.POIUtils;
import org.insightech.er.util.POIUtils.CellLocation;

public abstract class AbstractSheetGenerator {

	private static final int MAX_SHEET_NAME_LENGTH = 26;

	protected static final String KEYWORD_ORDER = "$ORD";

	protected static final String KEYWORD_LOGICAL_TABLE_NAME = "$LTN";

	protected static final String KEYWORD_PHYSICAL_TABLE_NAME = "$PTN";

	// 論理カラム名
	protected static final String KEYWORD_LOGICAL_COLUMN_NAME = "$LCN";

	// 物理カラム名
	protected static final String KEYWORD_PHYSICAL_COLUMN_NAME = "$PCN";

	// 型
	protected static final String KEYWORD_TYPE = "$TYP";

	// 長さ
	protected static final String KEYWORD_LENGTH = "$LEN";

	// 小数
	protected static final String KEYWORD_DECIMAL = "$DEC";

	// 主キー
	protected static final String KEYWORD_PRIMARY_KEY = "$PK";

	// Not Null
	protected static final String KEYWORD_NOT_NULL = "$NN";

	// ユニークキー
	protected static final String KEYWORD_UNIQUE_KEY = "$UK";

	// 外部キー
	protected static final String KEYWORD_FOREIGN_KEY = "$FK";

	// 参照テーブル.キー（論理名）
	protected static final String KEYWORD_LOGICAL_REFERENCE_TABLE_KEY = "$LRFTC";

	// 参照テーブル.キー（物理名）
	protected static final String KEYWORD_PHYSICAL_REFERENCE_TABLE_KEY = "$PRFTC";

	// 参照テーブル（論理名）
	protected static final String KEYWORD_LOGICAL_REFERENCE_TABLE = "$LRFT";

	// 参照テーブル（物理名）
	protected static final String KEYWORD_PHYSICAL_REFERENCE_TABLE = "$PRFT";

	// 参照キー（論理名）
	protected static final String KEYWORD_LOGICAL_REFERENCE_KEY = "$LRFC";

	// 参照キー（物理名）
	protected static final String KEYWORD_PHYSICAL_REFERENCE_KEY = "$PRFC";

	// オートインクリメント
	protected static final String KEYWORD_AUTO_INCREMENT = "$INC";

	// 説明
	protected static final String KEYWORD_DESCRIPTION = "$CDSC";

	// デフォルト値
	protected static final String KEYWORD_DEFAULT_VALUE = "$DEF";

	protected static final String KEYWORD_LOGICAL_FOREIGN_KEY_NAME = "$LFKN";

	protected static final String KEYWORD_PHYSICAL_FOREIGN_KEY_NAME = "$PFKN";

	protected static final String KEYWORD_TABLE_DESCRIPTION = "$TDSC";

	private static final String[] KEYWORDS_OF_COLUMN = { KEYWORD_ORDER,
			KEYWORD_LOGICAL_TABLE_NAME, KEYWORD_PHYSICAL_TABLE_NAME,
			KEYWORD_LOGICAL_COLUMN_NAME, KEYWORD_PHYSICAL_COLUMN_NAME,
			KEYWORD_TYPE, KEYWORD_LENGTH, KEYWORD_DECIMAL, KEYWORD_PRIMARY_KEY,
			KEYWORD_NOT_NULL, KEYWORD_UNIQUE_KEY, KEYWORD_FOREIGN_KEY,
			KEYWORD_LOGICAL_REFERENCE_TABLE_KEY,
			KEYWORD_PHYSICAL_REFERENCE_TABLE_KEY,
			KEYWORD_LOGICAL_REFERENCE_TABLE, KEYWORD_PHYSICAL_REFERENCE_TABLE,
			KEYWORD_LOGICAL_REFERENCE_KEY, KEYWORD_PHYSICAL_REFERENCE_KEY,
			KEYWORD_AUTO_INCREMENT, KEYWORD_DEFAULT_VALUE, KEYWORD_DESCRIPTION,
			KEYWORD_LOGICAL_FOREIGN_KEY_NAME, KEYWORD_PHYSICAL_FOREIGN_KEY_NAME };

	protected static final String[] FIND_KEYWORDS_OF_COLUMN = {
			KEYWORD_LOGICAL_COLUMN_NAME, KEYWORD_PHYSICAL_COLUMN_NAME };

	protected Map<String, String> keywordsValueMap;

	public static class ColumnTemplate {
		public Map<Integer, String> columnTemplateMap = new HashMap<Integer, String>();

		public List<HSSFCellStyle> topRowCellStyleList;

		public List<HSSFCellStyle> middleRowCellStyleList;

		public List<HSSFCellStyle> bottomRowCellStyleList;
	}

	public static class MatrixCellStyle {
		public HSSFCellStyle headerTemplateCellStyle;

		public HSSFCellStyle style11;
		public HSSFCellStyle style12;
		public HSSFCellStyle style13;
		public HSSFCellStyle style21;
		public HSSFCellStyle style22;
		public HSSFCellStyle style23;
		public HSSFCellStyle style31;
		public HSSFCellStyle style32;
		public HSSFCellStyle style33;
	}

	protected Map<String, String> buildKeywordsValueMap(HSSFSheet wordsSheet,
			int columnNo, String[] keywords) {
		Map<String, String> keywordsValueMap = new HashMap<String, String>();

		for (String keyword : keywords) {
			CellLocation location = POIUtils.findCell(wordsSheet, keyword,
					columnNo);
			if (location != null) {
				HSSFRow row = wordsSheet.getRow(location.r);

				HSSFCell cell = row.getCell(location.c + 2);
				String value = cell.getRichStringCellValue().getString();

				if (value != null) {
					keywordsValueMap.put(keyword, value);
				}
			}
		}

		return keywordsValueMap;
	}

	protected String getValue(Map<String, String> keywordsValueMap,
			String keyword, Object obj) {
		if (obj instanceof Boolean) {
			if (Boolean.TRUE.equals(obj)) {
				String value = keywordsValueMap.get(keyword);

				if (value != null && !"".equals(value)) {
					return value;
				}
			} else {
				return "";
			}
		}

		if (obj == null) {
			return "";
		}

		return obj.toString();
	}

	protected void setColumnData(Map<String, String> keywordsValueMap,
			ColumnTemplate columnTemplate, HSSFRow row,
			NormalColumn normalColumn, TableView tableView, int order) {

		for (int columnNum : columnTemplate.columnTemplateMap.keySet()) {
			HSSFCell cell = row.createCell(columnNum);
			String template = columnTemplate.columnTemplateMap.get(columnNum);

			String value = null;
			if (KEYWORD_ORDER.equals(template)) {
				value = String.valueOf(order);

			} else {
				value = this.getColumnValue(keywordsValueMap, normalColumn,
						tableView, template);
			}

			try {
				double num = Double.parseDouble(value);
				cell.setCellValue(num);

			} catch (NumberFormatException e) {
				HSSFRichTextString text = new HSSFRichTextString(value);
				cell.setCellValue(text);
			}
		}
	}

	private String getColumnValue(Map<String, String> keywordsValueMap,
			NormalColumn normalColumn, TableView tableView, String template) {
		String str = template;

		for (String keyword : KEYWORDS_OF_COLUMN) {
			str = str.replaceAll("\\" + keyword, this.getKeywordValue(
					keywordsValueMap, normalColumn, tableView, keyword));
		}

		return str;
	}

	private String getKeywordValue(Map<String, String> keywordsValueMap,
			NormalColumn normalColumn, TableView tableView, String keyword) {
		Object obj = null;

		if (KEYWORD_LOGICAL_TABLE_NAME.equals(keyword)) {
			obj = tableView.getLogicalName();

		} else if (KEYWORD_PHYSICAL_TABLE_NAME.equals(keyword)) {
			obj = tableView.getPhysicalName();

		} else if (KEYWORD_LOGICAL_COLUMN_NAME.equals(keyword)) {
			obj = normalColumn.getLogicalName();

		} else if (KEYWORD_PHYSICAL_COLUMN_NAME.equals(keyword)) {
			obj = normalColumn.getPhysicalName();

		} else if (KEYWORD_TYPE.equals(keyword)) {
			if (normalColumn.getType() == null) {
				obj = null;
			} else {
				obj = normalColumn.getType().getAlias(
						tableView.getDiagram().getDatabase());
			}
		} else if (KEYWORD_LENGTH.equals(keyword)) {
			obj = normalColumn.getTypeData().getLength();

		} else if (KEYWORD_DECIMAL.equals(keyword)) {
			obj = normalColumn.getTypeData().getDecimal();

		} else if (KEYWORD_PRIMARY_KEY.equals(keyword)) {
			obj = normalColumn.isPrimaryKey();

		} else if (KEYWORD_NOT_NULL.equals(keyword)) {
			obj = normalColumn.isNotNull();

		} else if (KEYWORD_FOREIGN_KEY.equals(keyword)) {
			List<Relation> relationList = normalColumn.getRelationList();

			if (relationList == null || relationList.isEmpty()) {
				obj = false;

			} else {
				obj = true;
			}

		} else if (KEYWORD_LOGICAL_REFERENCE_TABLE_KEY.equals(keyword)) {
			List<Relation> relationList = normalColumn.getRelationList();

			if (relationList == null || relationList.isEmpty()) {
				obj = null;

			} else {
				Relation relation = relationList.get(0);

				TableView referencedTable = relation.getSourceTableView();
				obj = referencedTable.getLogicalName()
						+ "."
						+ normalColumn.getReferencedColumn(relation)
								.getLogicalName();
			}

		} else if (KEYWORD_PHYSICAL_REFERENCE_TABLE_KEY.equals(keyword)) {
			List<Relation> relationList = normalColumn.getRelationList();

			if (relationList == null || relationList.isEmpty()) {
				obj = null;

			} else {
				Relation relation = relationList.get(0);

				TableView referencedTable = relation.getSourceTableView();
				obj = referencedTable.getPhysicalName()
						+ "."
						+ normalColumn.getReferencedColumn(relation)
								.getPhysicalName();
			}

		} else if (KEYWORD_LOGICAL_REFERENCE_TABLE.equals(keyword)) {
			List<Relation> relationList = normalColumn.getRelationList();

			if (relationList == null || relationList.isEmpty()) {
				obj = null;

			} else {
				TableView referencedTable = relationList.get(0)
						.getSourceTableView();
				obj = referencedTable.getLogicalName();
			}

		} else if (KEYWORD_PHYSICAL_REFERENCE_TABLE.equals(keyword)) {
			List<Relation> relationList = normalColumn.getRelationList();

			if (relationList == null || relationList.isEmpty()) {
				obj = null;

			} else {
				TableView referencedTable = relationList.get(0)
						.getSourceTableView();
				obj = referencedTable.getPhysicalName();
			}

		} else if (KEYWORD_LOGICAL_REFERENCE_KEY.equals(keyword)) {
			List<Relation> relationList = normalColumn.getRelationList();

			if (relationList == null || relationList.isEmpty()) {
				obj = null;

			} else {
				Relation relation = relationList.get(0);

				obj = normalColumn.getReferencedColumn(relation)
						.getLogicalName();
			}

		} else if (KEYWORD_PHYSICAL_REFERENCE_KEY.equals(keyword)) {
			List<Relation> relationList = normalColumn.getRelationList();

			if (relationList == null || relationList.isEmpty()) {
				obj = null;

			} else {
				Relation relation = relationList.get(0);

				obj = normalColumn.getReferencedColumn(relation)
						.getPhysicalName();
			}

		} else if (KEYWORD_LOGICAL_FOREIGN_KEY_NAME.equals(keyword)) {
			obj = normalColumn.getLogicalName();

		} else if (KEYWORD_PHYSICAL_FOREIGN_KEY_NAME.equals(keyword)) {
			obj = normalColumn.getPhysicalName();

		} else if (KEYWORD_UNIQUE_KEY.equals(keyword)) {
			obj = normalColumn.isUniqueKey();

		} else if (KEYWORD_DESCRIPTION.equals(keyword)) {
			obj = normalColumn.getDescription();

		} else if (KEYWORD_DEFAULT_VALUE.equals(keyword)) {
			obj = normalColumn.getDefaultValue();

		} else if (KEYWORD_AUTO_INCREMENT.equals(keyword)) {
			obj = normalColumn.isAutoIncrement();

		}

		return this.getValue(keywordsValueMap, keyword, obj);
	}

	protected ColumnTemplate loadColumnTemplate(HSSFWorkbook workbook,
			HSSFSheet templateSheet, CellLocation location) {
		if (location == null) {
			return null;
		}

		ColumnTemplate columnTemplate = new ColumnTemplate();

		HSSFRow row = templateSheet.getRow(location.r);
		HSSFRow bottomRow = templateSheet.getRow(location.r + 1);

		for (int colNum = row.getFirstCellNum(); colNum <= row.getLastCellNum(); colNum++) {

			HSSFCell cell = row.getCell(colNum);

			if (cell != null) {
				columnTemplate.columnTemplateMap.put(colNum, cell
						.getRichStringCellValue().getString());
			}
		}

		columnTemplate.topRowCellStyleList = POIUtils.copyCellStyle(workbook,
				row);
		columnTemplate.middleRowCellStyleList = POIUtils.copyCellStyle(
				workbook, row);
		columnTemplate.bottomRowCellStyleList = POIUtils.copyCellStyle(
				workbook, row);

		for (short i = 0; i < columnTemplate.middleRowCellStyleList.size(); i++) {
			HSSFCellStyle middleRowCellStyle = columnTemplate.middleRowCellStyleList
					.get(i);
			if (middleRowCellStyle != null) {
				HSSFCellStyle topRowCellStyle = columnTemplate.topRowCellStyleList
						.get(i);
				HSSFCellStyle bottomRowCellStyle = columnTemplate.bottomRowCellStyleList
						.get(i);

				HSSFCell bottomCell = bottomRow.getCell(row.getFirstCellNum()
						+ i);

				topRowCellStyle.setBorderBottom(bottomCell.getCellStyle()
						.getBorderTop());
				middleRowCellStyle.setBorderTop(bottomCell.getCellStyle()
						.getBorderTop());
				middleRowCellStyle.setBorderBottom(bottomCell.getCellStyle()
						.getBorderTop());
				bottomRowCellStyle.setBorderTop(bottomCell.getCellStyle()
						.getBorderTop());
				bottomRowCellStyle.setBorderBottom(bottomCell.getCellStyle()
						.getBorderBottom());
			}
		}

		return columnTemplate;
	}

	protected void setCellStyle(ColumnTemplate columnTemplate, HSSFSheet sheet,
			int firstRowNum, int rowSize, int firstColNum) {

		sheet.removeRow(sheet.getRow(firstRowNum + rowSize));

		HSSFRow bottomRowTemplate = sheet.getRow(firstRowNum + rowSize + 1);
		sheet.removeRow(bottomRowTemplate);

		for (int r = firstRowNum + 1; r < firstRowNum + rowSize; r++) {
			HSSFRow row = sheet.getRow(r);

			for (int i = 0; i < columnTemplate.middleRowCellStyleList.size(); i++) {
				HSSFCell cell = row.getCell(firstColNum + i);
				if (cell != null
						&& columnTemplate.middleRowCellStyleList.get(i) != null) {
					cell.setCellStyle(columnTemplate.middleRowCellStyleList
							.get(i));
				}
			}
		}

		if (rowSize > 0) {
			HSSFRow topRow = sheet.getRow(firstRowNum);

			for (int i = 0; i < columnTemplate.topRowCellStyleList.size(); i++) {
				HSSFCell cell = topRow.getCell(firstColNum + i);
				if (cell != null) {
					if (columnTemplate.topRowCellStyleList.get(i) != null) {
						cell.setCellStyle(columnTemplate.topRowCellStyleList
								.get(i));
					}
				}
			}

			HSSFRow bottomRow = sheet.getRow(firstRowNum + rowSize - 1);

			for (int i = 0; i < columnTemplate.bottomRowCellStyleList.size(); i++) {
				HSSFCell bottomRowCell = bottomRow.getCell(firstColNum + i);
				if (bottomRowCell != null) {
					if (columnTemplate.bottomRowCellStyleList.get(i) != null) {
						bottomRowCell
								.setCellStyle(columnTemplate.bottomRowCellStyleList
										.get(i));
					}
				}
			}

		} else {
			HSSFRow bottomRow = sheet.getRow(firstRowNum - 1);

			if (bottomRow != null) {
				for (int i = 0; i < columnTemplate.bottomRowCellStyleList
						.size(); i++) {
					HSSFCell bottomRowCell = bottomRow.getCell(firstColNum + i);

					if (bottomRowCell != null) {
						HSSFCellStyle bottomRowCellStyle = bottomRowCell
								.getCellStyle();
						if (columnTemplate.bottomRowCellStyleList.get(i) != null) {
							bottomRowCellStyle
									.setBorderBottom(columnTemplate.bottomRowCellStyleList
											.get(i).getBorderBottom());
						}
					}
				}
			}
		}

		List<CellRangeAddress> regionList = POIUtils.getMergedRegionList(sheet,
				firstRowNum);

		for (int r = firstRowNum + 1; r < firstRowNum + rowSize; r++) {
			POIUtils.copyMergedRegion(sheet, regionList, r);
		}
	}

	public static HSSFSheet createNewSheet(HSSFWorkbook workbook, int sheetNo,
			String name, Map<String, Integer> sheetNameMap) {
		HSSFSheet sheet = workbook.cloneSheet(sheetNo);
		int newSheetNo = workbook.getSheetIndex(sheet);

		workbook.setSheetName(newSheetNo, decideSheetName(name,
				sheetNameMap));

		return sheet;
	}

	public static String decideSheetName(String name, Map<String, Integer> sheetNameMap) {
		if (name.length() > MAX_SHEET_NAME_LENGTH) {
			name = name.substring(0, MAX_SHEET_NAME_LENGTH);
		}

		String sheetName = null;

		Integer sameNameNum = sheetNameMap.get(name);
		if (sameNameNum == null) {
			sameNameNum = 0;
			sheetName = name;

		} else {
			do {
				sameNameNum++;
				sheetName = name + "(" + sameNameNum + ")";
			} while (sheetNameMap.containsKey(sheetName));
		}

		sheetNameMap.put(name, sameNameNum);

		return sheetName;
	}

	public void init(HSSFSheet wordsSheet) {
		this.keywordsValueMap = this.buildKeywordsValueMap(wordsSheet, this
				.getKeywordsColumnNo(), this.getKeywords());
	}

	public abstract void generate(IProgressMonitor monitor,
			HSSFWorkbook workbook, int sheetNo,
			boolean useLogicalNameAsSheetName,
			Map<String, Integer> sheetNameMap,
			Map<String, ObjectModel> sheetObjectMap, ERDiagram diagram,
			Map<String, LoopDefinition> loopDefinitionMap)
			throws InterruptedException;

	public abstract int count(ERDiagram diagram);

	public abstract String getTemplateSheetName();

	public abstract int getKeywordsColumnNo();

	public abstract String[] getKeywords();
}
