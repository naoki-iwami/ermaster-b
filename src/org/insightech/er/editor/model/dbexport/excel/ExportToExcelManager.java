package org.insightech.er.editor.model.dbexport.excel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.StringObjectModel;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.AbstractSheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.AllIndicesSheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.AllSequencesSheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.AllTablesSheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.AllTriggerSheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.AllViewSheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.CategorySheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.ColumnSheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.HistorySheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.IndexSheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.PictureSheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.SequenceSheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.SheetIndexSheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.TableSheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.TriggerSheetGenerator;
import org.insightech.er.editor.model.dbexport.excel.sheet_generator.ViewSheetGenerator;
import org.insightech.er.util.POIUtils;
import org.insightech.er.util.io.FileUtils;

public class ExportToExcelManager implements IRunnableWithProgress {

	private static final String WORDS_SHEET_NAME = "words";

	private static final String LOOPS_SHEET_NAME = "loops";

	private Map<String, Integer> sheetNameMap;

	private Map<String, ObjectModel> sheetObjectMap;

	private static final List<AbstractSheetGenerator> SHHET_GENERATOR_LIST = new ArrayList<AbstractSheetGenerator>();

	static {
		SHHET_GENERATOR_LIST.add(new TableSheetGenerator());
		SHHET_GENERATOR_LIST.add(new IndexSheetGenerator());
		SHHET_GENERATOR_LIST.add(new SequenceSheetGenerator());
		SHHET_GENERATOR_LIST.add(new ViewSheetGenerator());
		SHHET_GENERATOR_LIST.add(new TriggerSheetGenerator());
		SHHET_GENERATOR_LIST.add(new ColumnSheetGenerator());
		SHHET_GENERATOR_LIST.add(new AllTablesSheetGenerator());
		SHHET_GENERATOR_LIST.add(new AllIndicesSheetGenerator());
		SHHET_GENERATOR_LIST.add(new AllSequencesSheetGenerator());
		SHHET_GENERATOR_LIST.add(new AllViewSheetGenerator());
		SHHET_GENERATOR_LIST.add(new AllTriggerSheetGenerator());
		SHHET_GENERATOR_LIST.add(new CategorySheetGenerator());
		SHHET_GENERATOR_LIST.add(new HistorySheetGenerator());
	}

	public static class LoopDefinition {

		public int startLine;

		public int spaceLine;

		public String sheetName;

		public LoopDefinition(int startLine, int spaceLine, String sheetName) {
			this.startLine = startLine;
			this.spaceLine = spaceLine;
			this.sheetName = sheetName;
		}
	}

	private PictureSheetGenerator pictureSheetGenerator;

	private SheetIndexSheetGenerator sheetIndexSheetGenerator;

	private Map<String, LoopDefinition> loopDefinitionMap;

	private String saveFilePath;

	private ERDiagram diagram;

	private InputStream template;

	private boolean useLogicalNameAsSheetName;

	private byte[] imageBuffer;

	private int excelPictureType;

	private Exception exception;

	public ExportToExcelManager(String saveFilePath, ERDiagram diagram,
			InputStream template, boolean useLogicalNameAsSheetName,
			byte[] imageBuffer, int excelPictureType) {
		super();

		this.saveFilePath = saveFilePath;
		this.diagram = diagram;
		this.template = template;
		this.useLogicalNameAsSheetName = useLogicalNameAsSheetName;
		this.imageBuffer = imageBuffer;
		this.excelPictureType = excelPictureType;

		this.sheetNameMap = new HashMap<String, Integer>();
		this.sheetObjectMap = new LinkedHashMap<String, ObjectModel>();

		this.loopDefinitionMap = new HashMap<String, LoopDefinition>();
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		try {
			save(monitor);

		} catch (InterruptedException e) {
			throw e;

		} catch (Exception e) {
			this.exception = e;
		}

		monitor.done();
	}

	/**
	 * �f�[�^�x�[�X��`��G�N�Z���ɏ����o���܂��B
	 * 
	 * @param editorPart
	 * @param viewer
	 * @param saveFilePath
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void save(IProgressMonitor monitor) throws IOException,
			InterruptedException {
		File excelFile = new File(this.saveFilePath);

		this.backup(excelFile, true);

		HSSFWorkbook workbook = this.loadTemplateWorkbook(this.template,
				this.diagram);

		// �t�@�C����J���Ă����ۂ̏������݃G���[�𑁂��o�����߂Ɉ�U�����ŏ������ݏ�����s��
		POIUtils.writeExcelFile(excelFile, workbook);

		int count = this.countSheetFromTemplate(workbook, this.diagram);
		monitor.beginTask(ResourceString
				.getResourceString("dialog.message.export.excel"), count);

		this.pictureSheetGenerator = new PictureSheetGenerator(workbook,
				imageBuffer, excelPictureType);

		this.createSheetFromTemplate(monitor, workbook, diagram,
				useLogicalNameAsSheetName);

		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			workbook.getSheetAt(i).setSelected(false);
		}

		if (workbook.getNumberOfSheets() > 0) {
			workbook.getSheetAt(0).setSelected(true);
			workbook.setActiveSheet(0);
			workbook.setFirstVisibleTab(0);
		}

		POIUtils.writeExcelFile(excelFile, workbook);
	}

	private HSSFWorkbook loadTemplateWorkbook(InputStream template,
			ERDiagram diagram) throws IOException {

		HSSFWorkbook workbook = POIUtils.readExcelBook(template);

		if (workbook == null) {
			throw new IOException(ResourceString
					.getResourceString("error.read.file"));
		}

		HSSFSheet wordsSheet = workbook.getSheet(WORDS_SHEET_NAME);

		if (wordsSheet == null) {
			throw new IOException(ResourceString
					.getResourceString("error.not.found.words.sheet"));
		}

		HSSFSheet loopsSheet = workbook.getSheet(LOOPS_SHEET_NAME);

		if (loopsSheet == null) {
			throw new IOException(ResourceString
					.getResourceString("error.not.found.loops.sheet"));
		}

		this.initLoopDefinitionMap(loopsSheet);

		for (AbstractSheetGenerator sheetGenerator : SHHET_GENERATOR_LIST) {
			sheetGenerator.init(wordsSheet);
		}

		this.sheetIndexSheetGenerator = new SheetIndexSheetGenerator();
		this.sheetIndexSheetGenerator.init(wordsSheet);

		return workbook;
	}

	private void initLoopDefinitionMap(HSSFSheet loopsSheet) {
		for (int i = 2; i <= loopsSheet.getLastRowNum(); i++) {
			String templateSheetName = POIUtils.getCellValue(loopsSheet, i, 0);
			if (templateSheetName == null) {
				break;
			}

			int firstLine = POIUtils.getIntCellValue(loopsSheet, i, 1);
			int spaceLine = POIUtils.getIntCellValue(loopsSheet, i, 2);
			String sheetName = POIUtils.getCellValue(loopsSheet, i, 3);

			this.loopDefinitionMap.put(templateSheetName, new LoopDefinition(
					firstLine, spaceLine, sheetName));
		}
	}

	private AbstractSheetGenerator getSheetGenerator(String templateSheetName) {
		for (AbstractSheetGenerator sheetGenerator : SHHET_GENERATOR_LIST) {
			if (sheetGenerator.getTemplateSheetName().equals(templateSheetName)) {
				return sheetGenerator;
			}
		}

		return null;
	}

	private void createSheetFromTemplate(IProgressMonitor monitor,
			HSSFWorkbook workbook, ERDiagram diagram,
			boolean useLogicalNameAsSheetName) throws InterruptedException {
		int originalSheetNum = workbook.getNumberOfSheets();

		int sheetIndexSheetNo = -1;

		while (originalSheetNum > 0) {
			String templateSheetName = workbook.getSheetName(0);

			AbstractSheetGenerator sheetGenerator = this
					.getSheetGenerator(templateSheetName);

			if (sheetGenerator != null) {
				sheetGenerator.generate(monitor, workbook, 0,
						useLogicalNameAsSheetName, this.sheetNameMap,
						this.sheetObjectMap, diagram, loopDefinitionMap);
				workbook.removeSheetAt(0);

			} else {
				System.out.println("non sheet generator :  " + templateSheetName);
				if (!isExcludeTarget(templateSheetName)) {
					moveSheet(workbook, 0);
					HSSFSheet sheet = workbook.getSheetAt(workbook.getNumberOfSheets() - 1);

					this.sheetObjectMap.put(templateSheetName,
							new StringObjectModel(templateSheetName));

					this.pictureSheetGenerator.setImage(workbook, sheet);

					if (this.sheetIndexSheetGenerator.getTemplateSheetName()
							.equals(templateSheetName)) {
						sheetIndexSheetNo = workbook.getNumberOfSheets()
								- originalSheetNum;

						String name = this.sheetIndexSheetGenerator
								.getSheetName();

						workbook.setSheetName(workbook.getNumberOfSheets() - 1,
								AbstractSheetGenerator.decideSheetName(name,
										sheetNameMap));
					}

				} else {
					workbook.removeSheetAt(0);
				}

				monitor.worked(1);
			}

			originalSheetNum--;

			if (monitor.isCanceled()) {
				throw new InterruptedException("Cancel has been requested.");
			}
		}

		if (sheetIndexSheetNo != -1) {
			this.sheetIndexSheetGenerator.generate(monitor, workbook,
					sheetIndexSheetNo, useLogicalNameAsSheetName,
					this.sheetNameMap, this.sheetObjectMap, diagram,
					loopDefinitionMap);
		}
	}

	public static HSSFSheet moveSheet(HSSFWorkbook workbook, int sheetNo) {
		HSSFSheet oldSheet = workbook.getSheetAt(sheetNo);
		String sheetName = oldSheet.getSheetName();
		
		HSSFSheet newSheet = workbook.cloneSheet(sheetNo);
		int newSheetNo = workbook.getSheetIndex(newSheet);
		
		workbook.removeSheetAt(sheetNo);

		workbook.setSheetName(newSheetNo - 1, sheetName);

		return newSheet;
	}

	
	private int countSheetFromTemplate(HSSFWorkbook workbook, ERDiagram diagram) {
		int count = 0;

		for (int sheetNo = 0; sheetNo < workbook.getNumberOfSheets(); sheetNo++) {
			String templateSheetName = workbook.getSheetName(sheetNo);

			AbstractSheetGenerator sheetGenerator = this
					.getSheetGenerator(templateSheetName);

			if (sheetGenerator != null) {
				count += sheetGenerator.count(diagram);

			} else {
				count++;
			}
		}

		return count;
	}

	private boolean backup(File file, boolean isBackupEnable)
			throws IOException {
		if (!isBackupEnable || !file.exists()) {
			return true;
		}

		String path;
		try {
			path = file.getCanonicalPath();

			String backupFilePath = path.substring(0, path.indexOf(".xls"))
					+ "_back.xls";
			File backupFile = new File(backupFilePath);
			FileUtils.copyFile(file, backupFile);

		} catch (IOException e) {
			throw new IOException(ResourceString
					.getResourceString("error.backup.excel.file"));
		}

		return true;
	}

	private boolean isExcludeTarget(String templateSheetName) {
		if (WORDS_SHEET_NAME.equals(templateSheetName)
				|| LOOPS_SHEET_NAME.equals(templateSheetName)) {
			return true;
		}

		return false;
	}

	public Exception getException() {
		return exception;
	}
}
