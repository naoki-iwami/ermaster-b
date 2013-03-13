package org.insightech.er.editor.model.dbexport.excel.sheet_generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.core.runtime.IProgressMonitor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.dbexport.excel.ExportToExcelManager.LoopDefinition;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.util.POIUtils;

public class CategorySheetGenerator extends TableSheetGenerator {

	@Override
	public void generate(IProgressMonitor monitor, HSSFWorkbook workbook,
			int sheetNo, boolean useLogicalNameAsSheetName,
			Map<String, Integer> sheetNameMap,
			Map<String, ObjectModel> sheetObjectMap, ERDiagram diagram,
			Map<String, LoopDefinition> loopDefinitionMap) {
		this.clear();
		
		if (diagram.getCurrentCategory() != null) {
			return;
		}

		LoopDefinition loopDefinition = loopDefinitionMap.get(this
				.getTemplateSheetName());
		HSSFSheet oldSheet = workbook.getSheetAt(sheetNo);

		List<ERTable> allTables = new ArrayList<ERTable>(diagram
				.getDiagramContents().getContents().getTableSet().getList());

		for (Category category : diagram.getDiagramContents().getSettings()
				.getCategorySetting().getSelectedCategories()) {
			HSSFSheet newSheet = createNewSheet(workbook, sheetNo,
					category.getName(), sheetNameMap);

			sheetObjectMap.put(workbook.getSheetName(workbook
					.getSheetIndex(newSheet)), category);

			boolean first = true;

			for (ERTable table : category.getTableContents()) {
				allTables.remove(table);

				if (first) {
					first = false;

				} else {
					POIUtils.copyRow(oldSheet, newSheet,
							loopDefinition.startLine - 1, oldSheet
									.getLastRowNum(), newSheet.getLastRowNum()
									+ loopDefinition.spaceLine + 1);
				}

				this.setTableData(workbook, newSheet, table);

				newSheet.setRowBreak(newSheet.getLastRowNum()
						+ loopDefinition.spaceLine);
			}

			if (first) {
				int rowIndex = loopDefinition.startLine - 1;

				while (rowIndex <= newSheet.getLastRowNum()) {
					HSSFRow row = newSheet.getRow(rowIndex);
					if (row != null) {
						newSheet.removeRow(row);
					}
					
					rowIndex++;
				}
			}

			monitor.worked(1);
		}

		if (!allTables.isEmpty()) {
			HSSFSheet newSheet = createNewSheet(workbook, sheetNo,
					loopDefinition.sheetName, sheetNameMap);

			boolean first = true;

			for (ERTable table : allTables) {
				if (first) {
					first = false;

				} else {
					POIUtils.copyRow(oldSheet, newSheet,
							loopDefinition.startLine - 1, oldSheet
									.getLastRowNum(), newSheet.getLastRowNum()
									+ loopDefinition.spaceLine + 1);
				}

				this.setTableData(workbook, newSheet, table);
				newSheet.setRowBreak(newSheet.getLastRowNum()
						+ loopDefinition.spaceLine);

			}
		}
	}

	@Override
	public String getTemplateSheetName() {
		return "category_template";
	}

	@Override
	public int count(ERDiagram diagram) {
		return diagram.getDiagramContents().getSettings().getCategorySetting()
				.getSelectedCategories().size();
	}

}
