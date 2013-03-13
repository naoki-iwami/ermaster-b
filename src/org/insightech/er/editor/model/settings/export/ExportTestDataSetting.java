package org.insightech.er.editor.model.settings.export;

import java.io.Serializable;

public class ExportTestDataSetting implements Serializable, Cloneable {

	private static final long serialVersionUID = -1781076310117636599L;

	private int exportFormat;

	private String exportFilePath;

	private String exportFileEncoding;

	public int getExportFormat() {
		return exportFormat;
	}

	public void setExportFormat(int exportFormat) {
		this.exportFormat = exportFormat;
	}

	public String getExportFilePath() {
		return exportFilePath;
	}

	public String getExportFileEncoding() {
		return exportFileEncoding;
	}

	public void setExportFileEncoding(String exportFileEncoding) {
		this.exportFileEncoding = exportFileEncoding;
	}

	public void setExportFilePath(String exportFilePath) {
		this.exportFilePath = exportFilePath;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExportTestDataSetting other = (ExportTestDataSetting) obj;
		if (exportFileEncoding == null) {
			if (other.exportFileEncoding != null)
				return false;
		} else if (!exportFileEncoding.equals(other.exportFileEncoding))
			return false;
		if (exportFilePath == null) {
			if (other.exportFilePath != null)
				return false;
		} else if (!exportFilePath.equals(other.exportFilePath))
			return false;
		if (exportFormat != other.exportFormat)
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExportTestDataSetting clone() {
		try {
			ExportTestDataSetting clone = (ExportTestDataSetting) super.clone();

			return clone;

		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
