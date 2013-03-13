package org.insightech.er.editor.model.dbexport.excel.sheet_generator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.core.runtime.IProgressMonitor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.StringObjectModel;
import org.insightech.er.editor.model.dbexport.excel.ExportToExcelManager.LoopDefinition;
import org.insightech.er.editor.model.tracking.ChangeTracking;
import org.insightech.er.util.POIUtils;
import org.insightech.er.util.POIUtils.CellLocation;

public class HistorySheetGenerator extends AbstractSheetGenerator {

	// 更新日
	private static final String KEYWORD_DATE = "$DATE";

	// 変更内容
	private static final String KEYWORD_CONTENTS = "$CON";

	// 日付フォーマット
	private static final String KEYWORD_DATE_FORMAT = "$FMT";

	// シート名
	private static final String KEYWORD_SHEET_NAME = "$SHTN";

	private static final String[] FIND_KEYWORDS_LIST = { KEYWORD_DATE,
			KEYWORD_CONTENTS };

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void generate(IProgressMonitor monitor, HSSFWorkbook workbook,
			int sheetNo, boolean useLogicalNameAsSheetName,
			Map<String, Integer> sheetNameMap,
			Map<String, ObjectModel> sheetObjectMap, ERDiagram diagram,
			Map<String, LoopDefinition> loopDefinitionMap) {

		String sheetName = this.getSheetName();

		HSSFSheet newSheet = createNewSheet(workbook, sheetNo, sheetName,
				sheetNameMap);

		sheetObjectMap.put(workbook.getSheetName(workbook
				.getSheetIndex(newSheet)), new StringObjectModel(sheetName));

		this.setHistoryListData(workbook, newSheet, sheetObjectMap, diagram);
		monitor.worked(1);
	}

	public void setHistoryListData(HSSFWorkbook workbook, HSSFSheet sheet,
			Map<String, ObjectModel> sheetObjectMap, ERDiagram diagram) {
		CellLocation cellLocation = POIUtils
				.findCell(sheet, FIND_KEYWORDS_LIST);

		if (cellLocation != null) {
			int rowNum = cellLocation.r;
			HSSFRow templateRow = sheet.getRow(rowNum);

			ColumnTemplate columnTemplate = this.loadColumnTemplate(workbook,
					sheet, cellLocation);
			int order = 1;

			HSSFFont linkCellFont = null;
			int linkCol = -1;

			for (ChangeTracking changeTracking : diagram
					.getChangeTrackingList().getList()) {
				HSSFRow row = POIUtils.insertRow(sheet, rowNum++);

				for (int columnNum : columnTemplate.columnTemplateMap.keySet()) {
					HSSFCell cell = row.createCell(columnNum);
					String template = columnTemplate.columnTemplateMap
							.get(columnNum);

					String value = null;
					if (KEYWORD_ORDER.equals(template)) {
						value = String.valueOf(order);

					} else {
						if (KEYWORD_DATE.equals(template)) {
							DateFormat format = new SimpleDateFormat(
									this.keywordsValueMap
											.get(KEYWORD_DATE_FORMAT));
							try {
								value = format.format(changeTracking
										.getUpdatedDate());

							} catch (Exception e) {
								value = changeTracking.getUpdatedDate()
										.toString();
							}

						} else if (KEYWORD_CONTENTS.equals(template)) {
							value = changeTracking.getComment();
						}

						HSSFRichTextString text = new HSSFRichTextString(value);
						cell.setCellValue(text);
					}

					order++;
				}
			}

			this.setCellStyle(columnTemplate, sheet, cellLocation.r, rowNum
					- cellLocation.r, templateRow.getFirstCellNum());

			if (linkCol != -1) {
				for (int row = cellLocation.r; row < rowNum; row++) {
					HSSFCell cell = sheet.getRow(row).getCell(linkCol);
					cell.getCellStyle().setFont(linkCellFont);
				}
			}
		}
	}

	public String getSheetName() {
		String name = this.keywordsValueMap.get(KEYWORD_SHEET_NAME);

		if (name == null) {
			name = "dialog.title.change.tracking";
		}

		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTemplateSheetName() {
		return "history_template";
	}

	@Override
	public String[] getKeywords() {
		return new String[] { KEYWORD_DATE, KEYWORD_CONTENTS, KEYWORD_ORDER,
				KEYWORD_DATE_FORMAT, KEYWORD_SHEET_NAME };
	}

	@Override
	public int getKeywordsColumnNo() {
		return 28;
	}

	@Override
	public int count(ERDiagram diagram) {
		return 1;
	}
}
