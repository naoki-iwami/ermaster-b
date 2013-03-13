package org.insightech.er.editor.model.settings;

import java.io.Serializable;

import org.insightech.er.editor.model.dbexport.ddl.DDLTarget;
import org.insightech.er.editor.model.settings.export.ExportJavaSetting;
import org.insightech.er.editor.model.settings.export.ExportTestDataSetting;

public class ExportSetting implements Serializable, Cloneable {

	private static final long serialVersionUID = 3669486436464233526L;

	private String excelTemplate;

	private String excelOutput;

	private String imageOutput;

	private String ddlOutput;

	private boolean useLogicalNameAsSheet;

	private boolean putERDiagramOnExcel;

	private boolean openAfterSaved;

	private String categoryNameToExport;

	private ExportJavaSetting exportJavaSetting = new ExportJavaSetting();

	private ExportTestDataSetting exportTestDataSetting = new ExportTestDataSetting();

	private DDLTarget ddlTarget = new DDLTarget();

	public ExportJavaSetting getExportJavaSetting() {
		return exportJavaSetting;
	}

	public void setExportJavaSetting(ExportJavaSetting exportJavaSetting) {
		this.exportJavaSetting = exportJavaSetting;
	}

	public ExportTestDataSetting getExportTestDataSetting() {
		return exportTestDataSetting;
	}

	public void setExportTestDataSetting(
			ExportTestDataSetting exportTestDataSetting) {
		this.exportTestDataSetting = exportTestDataSetting;
	}

	public DDLTarget getDdlTarget() {
		return ddlTarget;
	}

	public void setDdlTarget(DDLTarget ddlTarget) {
		this.ddlTarget = ddlTarget;
	}

	/**
	 * excelOutput ÇéÊìæÇµÇ‹Ç∑.
	 * 
	 * @return excelOutput
	 */
	public String getExcelOutput() {
		return excelOutput;
	}

	/**
	 * excelOutput Çê›íËÇµÇ‹Ç∑.
	 * 
	 * @param excelOutput
	 *            excelOutput
	 */
	public void setExcelOutput(String excelOutput) {
		this.excelOutput = excelOutput;
	}

	/**
	 * imageOutput ÇéÊìæÇµÇ‹Ç∑.
	 * 
	 * @return imageOutput
	 */
	public String getImageOutput() {
		return imageOutput;
	}

	/**
	 * imageOutput Çê›íËÇµÇ‹Ç∑.
	 * 
	 * @param imageOutput
	 *            imageOutput
	 */
	public void setImageOutput(String imageOutput) {
		this.imageOutput = imageOutput;
	}

	/**
	 * excelTemplate ÇéÊìæÇµÇ‹Ç∑.
	 * 
	 * @return excelTemplate
	 */
	public String getExcelTemplate() {
		return excelTemplate;
	}

	/**
	 * excelTemplate Çê›íËÇµÇ‹Ç∑.
	 * 
	 * @param excelTemplate
	 *            excelTemplate
	 */
	public void setExcelTemplate(String excelTemplate) {
		this.excelTemplate = excelTemplate;
	}

	/**
	 * useLogicalNameAsSheet ÇéÊìæÇµÇ‹Ç∑.
	 * 
	 * @return useLogicalNameAsSheet
	 */
	public boolean isUseLogicalNameAsSheet() {
		return useLogicalNameAsSheet;
	}

	/**
	 * useLogicalNameAsSheet Çê›íËÇµÇ‹Ç∑.
	 * 
	 * @param useLogicalNameAsSheet
	 *            useLogicalNameAsSheet
	 */
	public void setUseLogicalNameAsSheet(boolean useLogicalNameAsSheet) {
		this.useLogicalNameAsSheet = useLogicalNameAsSheet;
	}

	/**
	 * putERDiagramOnExcel ÇéÊìæÇµÇ‹Ç∑.
	 * 
	 * @return putERDiagramOnExcel
	 */
	public boolean isPutERDiagramOnExcel() {
		return putERDiagramOnExcel;
	}

	/**
	 * putERDiagramOnExcel Çê›íËÇµÇ‹Ç∑.
	 * 
	 * @param putERDiagramOnExcel
	 *            putERDiagramOnExcel
	 */
	public void setPutERDiagramOnExcel(boolean putERDiagramOnExcel) {
		this.putERDiagramOnExcel = putERDiagramOnExcel;
	}

	public boolean isOpenAfterSaved() {
		return openAfterSaved;
	}

	public void setOpenAfterSaved(boolean openAfterSaved) {
		this.openAfterSaved = openAfterSaved;
	}

	/**
	 * categoryNameToExportÇéÊìæÇµÇ‹Ç∑ÅB
	 * 
	 * @return categoryNameToExport
	 */
	public String getCategoryNameToExport() {
		return categoryNameToExport;
	}

	/**
	 * categoryNameToExportÇê›íËÇµÇ‹Ç∑ÅB
	 * 
	 * @param categoryNameToExport
	 *            categoryNameToExport
	 */
	public void setCategoryNameToExport(String categoryNameToExport) {
		this.categoryNameToExport = categoryNameToExport;
	}

	public String getDdlOutput() {
		return ddlOutput;
	}

	public void setDdlOutput(String ddlOutput) {
		this.ddlOutput = ddlOutput;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExportSetting other = (ExportSetting) obj;
		if (categoryNameToExport == null) {
			if (other.categoryNameToExport != null)
				return false;
		} else if (!categoryNameToExport.equals(other.categoryNameToExport))
			return false;
		if (ddlOutput == null) {
			if (other.ddlOutput != null)
				return false;
		} else if (!ddlOutput.equals(other.ddlOutput))
			return false;
		if (ddlTarget == null) {
			if (other.ddlTarget != null)
				return false;
		} else if (!ddlTarget.equals(other.ddlTarget))
			return false;
		if (excelOutput == null) {
			if (other.excelOutput != null)
				return false;
		} else if (!excelOutput.equals(other.excelOutput))
			return false;
		if (excelTemplate == null) {
			if (other.excelTemplate != null)
				return false;
		} else if (!excelTemplate.equals(other.excelTemplate))
			return false;
		if (exportJavaSetting == null) {
			if (other.exportJavaSetting != null)
				return false;
		} else if (!exportJavaSetting.equals(other.exportJavaSetting))
			return false;
		if (exportTestDataSetting == null) {
			if (other.exportTestDataSetting != null)
				return false;
		} else if (!exportTestDataSetting.equals(other.exportTestDataSetting))
			return false;
		if (imageOutput == null) {
			if (other.imageOutput != null)
				return false;
		} else if (!imageOutput.equals(other.imageOutput))
			return false;
		if (openAfterSaved != other.openAfterSaved)
			return false;
		if (putERDiagramOnExcel != other.putERDiagramOnExcel)
			return false;
		if (useLogicalNameAsSheet != other.useLogicalNameAsSheet)
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExportSetting clone() {
		try {
			ExportSetting setting = (ExportSetting) super.clone();

			setting.setDdlTarget(this.ddlTarget.clone());
			setting.setExportJavaSetting(this.exportJavaSetting.clone());
			setting
					.setExportTestDataSetting(this.exportTestDataSetting
							.clone());
			return setting;

		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
