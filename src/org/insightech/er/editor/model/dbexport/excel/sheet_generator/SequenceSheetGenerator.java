package org.insightech.er.editor.model.dbexport.excel.sheet_generator;

import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.core.runtime.IProgressMonitor;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.dbexport.excel.ExportToExcelManager.LoopDefinition;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.util.Format;
import org.insightech.er.util.POIUtils;

public class SequenceSheetGenerator extends AbstractSheetGenerator {

	private static final String KEYWORD_SEQUENCE_NAME = "$PSN";

	private static final String KEYWORD_SEQUENCE_DESCRIPTION = "$SDSC";

	private static final String KEYWORD_INCREMENT = "$INC";

	private static final String KEYWORD_MIN = "$MIN";

	private static final String KEYWORD_MAX = "$MAX";

	private static final String KEYWORD_START = "$STR";

	private static final String KEYWORD_CACHE = "$CACHE";

	private static final String KEYWORD_CYCLE = "$CYC";

	/**
	 * シーケンスシートにデータを設定します.
	 * 
	 * @param workbook
	 * @param sheet
	 * @param sequence
	 */
	public void setSequenceData(HSSFWorkbook workbook, HSSFSheet sheet,
			Sequence sequence) {
		POIUtils.replace(sheet, KEYWORD_SEQUENCE_NAME, this.getValue(
				this.keywordsValueMap, KEYWORD_SEQUENCE_NAME, sequence
						.getName()));
		POIUtils.replace(sheet, KEYWORD_SEQUENCE_DESCRIPTION, this.getValue(
				this.keywordsValueMap, KEYWORD_SEQUENCE_DESCRIPTION, sequence
						.getDescription()));
		POIUtils.replace(sheet, KEYWORD_INCREMENT, this.getValue(
				this.keywordsValueMap, KEYWORD_INCREMENT, Format
						.toString(sequence.getIncrement())));
		POIUtils.replace(sheet, KEYWORD_MIN, this.getValue(
				this.keywordsValueMap, KEYWORD_MIN, Format.toString(sequence
						.getMinValue())));
		POIUtils.replace(sheet, KEYWORD_MAX, this.getValue(
				this.keywordsValueMap, KEYWORD_MAX, Format.toString(sequence
						.getMaxValue())));
		POIUtils.replace(sheet, KEYWORD_START, this.getValue(
				this.keywordsValueMap, KEYWORD_START, Format.toString(sequence
						.getStart())));
		POIUtils.replace(sheet, KEYWORD_CACHE, this.getValue(
				this.keywordsValueMap, KEYWORD_CACHE, Format.toString(sequence
						.getCache())));
		POIUtils.replace(sheet, KEYWORD_CYCLE, this.getValue(
				this.keywordsValueMap, KEYWORD_CYCLE, sequence.isCycle()));
	}

	@Override
	public void generate(IProgressMonitor monitor, HSSFWorkbook workbook,
			int sheetNo, boolean useLogicalNameAsSheetName,
			Map<String, Integer> sheetNameMap,
			Map<String, ObjectModel> sheetObjectMap, ERDiagram diagram,
			Map<String, LoopDefinition> loopDefinitionMap) {

		for (Sequence sequence : diagram.getDiagramContents().getSequenceSet()) {
			String name = sequence.getName();

			HSSFSheet newSheet = createNewSheet(workbook, sheetNo, name,
					sheetNameMap);

			sheetObjectMap.put(workbook.getSheetName(workbook
					.getSheetIndex(newSheet)), sequence);

			this.setSequenceData(workbook, newSheet, sequence);
			monitor.worked(1);
		}
	}

	@Override
	public String getTemplateSheetName() {
		return "sequence_template";
	}

	@Override
	public String[] getKeywords() {
		return new String[] { KEYWORD_SEQUENCE_NAME,
				KEYWORD_SEQUENCE_DESCRIPTION, KEYWORD_INCREMENT, KEYWORD_MIN,
				KEYWORD_MAX, KEYWORD_START, KEYWORD_CACHE, KEYWORD_CYCLE };
	}

	@Override
	public int getKeywordsColumnNo() {
		return 8;
	}

	@Override
	public int count(ERDiagram diagram) {
		return diagram.getDiagramContents().getSequenceSet().getSequenceList()
				.size();
	}

}
