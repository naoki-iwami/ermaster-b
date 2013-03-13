package org.insightech.er.editor.model.dbexport.testdata.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.testdata.TestDataCreator;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.testdata.RepeatTestData;
import org.insightech.er.editor.model.testdata.RepeatTestDataDef;
import org.insightech.er.util.Format;
import org.insightech.er.util.POIUtils;

public class DBUnitXLSTestDataCreator extends TestDataCreator {

	private HSSFWorkbook workbook;

	private Set<String> sheetNames;

	private HSSFSheet sheet;

	private int rowNum = 0;

	public DBUnitXLSTestDataCreator() {
	}

	@Override
	protected void openFile() throws IOException {
		this.workbook = new HSSFWorkbook();
		this.sheetNames = new HashSet<String>();
	}

	@Override
	protected void write() throws IOException {
		super.write();

		POIUtils.writeExcelFile(new File(this.exportTestDataSetting
				.getExportFilePath()
				+ File.separator + this.testData.getName() + ".xls"),
				this.workbook);
	}

	@Override
	protected void closeFile() throws IOException {
	}

	@Override
	protected boolean skipTable(ERTable table) {
		String sheetName = table.getPhysicalName();

		if (this.sheetNames.contains(sheetName)) {
			return true;
		}

		this.sheetNames.add(sheetName);

		return false;
	}

	@Override
	protected void writeTableHeader(ERDiagram diagram, ERTable table) {
		String sheetName = table.getPhysicalName();
		this.sheet = this.workbook.createSheet(sheetName);

		this.rowNum = 0;
		HSSFRow row = this.sheet.createRow(this.rowNum++);

		int col = 0;

		for (NormalColumn column : table.getExpandedColumns()) {
			HSSFCell cell = row.createCell(col++);
			cell.setCellValue(new HSSFRichTextString(column.getPhysicalName()));
		}
	}

	@Override
	protected void writeTableFooter(ERTable table) {
	}

	@Override
	protected void writeDirectTestData(ERTable table,
			Map<NormalColumn, String> data, String database) {
		HSSFRow row = this.sheet.createRow(this.rowNum++);

		int col = 0;

		for (NormalColumn column : table.getExpandedColumns()) {
			HSSFCell cell = row.createCell(col++);

			String value = Format.null2blank(data.get(column));

			if (value == null || "null".equals(value.toLowerCase())) {

			} else {
				cell.setCellValue(new HSSFRichTextString(value));
			}
		}
	}

	@Override
	protected void writeRepeatTestData(ERTable table,
			RepeatTestData repeatTestData, String database) {

		for (int i = 0; i < repeatTestData.getTestDataNum(); i++) {
			HSSFRow row = this.sheet.createRow(this.rowNum++);

			int col = 0;

			for (NormalColumn column : table.getExpandedColumns()) {
				HSSFCell cell = row.createCell(col++);

				RepeatTestDataDef repeatTestDataDef = repeatTestData
						.getDataDef(column);

				String value = this.getMergedRepeatTestDataValue(i,
						repeatTestDataDef, column);

				if (value == null || "null".equals(value.toLowerCase())) {

				} else {
					cell.setCellValue(new HSSFRichTextString(value));
				}
			}
		}

	}

}
