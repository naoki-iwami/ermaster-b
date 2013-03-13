package org.insightech.er.editor.model.dbexport.excel.sheet_generator;

import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.core.runtime.IProgressMonitor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.dbexport.excel.ExportToExcelManager.LoopDefinition;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.util.POIUtils;

public class TriggerSheetGenerator extends AbstractSheetGenerator {

	private static final String KEYWORD_TRIGGER_NAME = "$PTGN";

	private static final String KEYWORD_TRIGGER_DESCRIPTION = "$TGDSC";

	private static final String KEYWORD_TRIGGER_SQL = "$SQL";

	/**
	 * トリガーシートにデータを設定します.
	 * 
	 * @param workbook
	 * @param sheet
	 * @param view
	 */
	public void setTriggerData(HSSFWorkbook workbook, HSSFSheet sheet,
			Trigger trigger) {
		POIUtils
				.replace(sheet, KEYWORD_TRIGGER_NAME, this.getValue(
						this.keywordsValueMap, KEYWORD_TRIGGER_NAME, trigger
								.getName()));

		POIUtils.replace(sheet, KEYWORD_TRIGGER_DESCRIPTION, this.getValue(
				this.keywordsValueMap, KEYWORD_TRIGGER_DESCRIPTION, trigger
						.getDescription()));

		POIUtils.replace(sheet, KEYWORD_TRIGGER_SQL, this.getValue(
				this.keywordsValueMap, KEYWORD_TRIGGER_SQL, trigger.getSql()));
	}

	@Override
	public void generate(IProgressMonitor monitor, HSSFWorkbook workbook,
			int sheetNo, boolean useLogicalNameAsSheetName,
			Map<String, Integer> sheetNameMap,
			Map<String, ObjectModel> sheetObjectMap, ERDiagram diagram,
			Map<String, LoopDefinition> loopDefinitionMap) {
		for (Trigger trigger : diagram.getDiagramContents().getTriggerSet()) {
			String name = trigger.getName();
			HSSFSheet newSheet = createNewSheet(workbook, sheetNo, name,
					sheetNameMap);

			sheetObjectMap.put(workbook.getSheetName(workbook
					.getSheetIndex(newSheet)), trigger);

			this.setTriggerData(workbook, newSheet, trigger);
			monitor.worked(1);
		}
	}

	@Override
	public String getTemplateSheetName() {
		return "trigger_template";
	}

	@Override
	public String[] getKeywords() {
		return new String[] { KEYWORD_TRIGGER_NAME,
				KEYWORD_TRIGGER_DESCRIPTION, KEYWORD_TRIGGER_SQL };
	}

	@Override
	public int getKeywordsColumnNo() {
		return 16;
	}

	@Override
	public int count(ERDiagram diagram) {
		return diagram.getDiagramContents().getTriggerSet().getTriggerList()
				.size();
	}

}
