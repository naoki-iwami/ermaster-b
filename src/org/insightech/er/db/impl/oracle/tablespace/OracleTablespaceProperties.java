package org.insightech.er.db.impl.oracle.tablespace;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.insightech.er.util.Check;

public class OracleTablespaceProperties implements TablespaceProperties {

	private static final long serialVersionUID = -6976279893674797115L;

	private String dataFile;

	private String fileSize;

	private boolean autoExtend;

	private String autoExtendSize;

	private String autoExtendMaxSize;

	private String minimumExtentSize;

	private String initial;

	private String next;

	private String minExtents;

	private String maxExtents;

	private String pctIncrease;

	private boolean logging;

	private boolean offline;

	private boolean temporary;

	private boolean autoSegmentSpaceManagement;

	/**
	 * dataFile を取得します.
	 * 
	 * @return dataFile
	 */
	public String getDataFile() {
		return dataFile;
	}

	/**
	 * dataFile を設定します.
	 * 
	 * @param dataFile
	 *            dataFile
	 */
	public void setDataFile(String dataFile) {
		this.dataFile = dataFile;
	}

	/**
	 * fileSize を取得します.
	 * 
	 * @return fileSize
	 */
	public String getFileSize() {
		return fileSize;
	}

	/**
	 * fileSize を設定します.
	 * 
	 * @param fileSize
	 *            fileSize
	 */
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * autoExtend を取得します.
	 * 
	 * @return autoExtend
	 */
	public boolean isAutoExtend() {
		return autoExtend;
	}

	/**
	 * autoExtend を設定します.
	 * 
	 * @param autoExtend
	 *            autoExtend
	 */
	public void setAutoExtend(boolean autoExtend) {
		this.autoExtend = autoExtend;
	}

	/**
	 * autoExtendSize を取得します.
	 * 
	 * @return autoExtendSize
	 */
	public String getAutoExtendSize() {
		return autoExtendSize;
	}

	/**
	 * autoExtendSize を設定します.
	 * 
	 * @param autoExtendSize
	 *            autoExtendSize
	 */
	public void setAutoExtendSize(String autoExtendSize) {
		this.autoExtendSize = autoExtendSize;
	}

	/**
	 * autoExtendMaxSize を取得します.
	 * 
	 * @return autoExtendMaxSize
	 */
	public String getAutoExtendMaxSize() {
		return autoExtendMaxSize;
	}

	/**
	 * autoExtendMaxSize を設定します.
	 * 
	 * @param autoExtendMaxSize
	 *            autoExtendMaxSize
	 */
	public void setAutoExtendMaxSize(String autoExtendMaxSize) {
		this.autoExtendMaxSize = autoExtendMaxSize;
	}

	/**
	 * minimumExtentSize を取得します.
	 * 
	 * @return minimumExtentSize
	 */
	public String getMinimumExtentSize() {
		return minimumExtentSize;
	}

	/**
	 * minimumExtentSize を設定します.
	 * 
	 * @param minimumExtentSize
	 *            minimumExtentSize
	 */
	public void setMinimumExtentSize(String minimumExtentSize) {
		this.minimumExtentSize = minimumExtentSize;
	}

	/**
	 * logging を取得します.
	 * 
	 * @return logging
	 */
	public boolean isLogging() {
		return logging;
	}

	/**
	 * logging を設定します.
	 * 
	 * @param logging
	 *            logging
	 */
	public void setLogging(boolean logging) {
		this.logging = logging;
	}

	/**
	 * offline を取得します.
	 * 
	 * @return offline
	 */
	public boolean isOffline() {
		return offline;
	}

	/**
	 * offline を設定します.
	 * 
	 * @param offline
	 *            offline
	 */
	public void setOffline(boolean offline) {
		this.offline = offline;
	}

	/**
	 * temporary を取得します.
	 * 
	 * @return temporary
	 */
	public boolean isTemporary() {
		return temporary;
	}

	/**
	 * temporary を設定します.
	 * 
	 * @param temporary
	 *            temporary
	 */
	public void setTemporary(boolean temporary) {
		this.temporary = temporary;
	}

	/**
	 * autoSegmentSpaceManagement を取得します.
	 * 
	 * @return autoSegmentSpaceManagement
	 */
	public boolean isAutoSegmentSpaceManagement() {
		return autoSegmentSpaceManagement;
	}

	/**
	 * autoSegmentSpaceManagement を設定します.
	 * 
	 * @param autoSegmentSpaceManagement
	 *            autoSegmentSpaceManagement
	 */
	public void setAutoSegmentSpaceManagement(boolean autoSegmentSpaceManagement) {
		this.autoSegmentSpaceManagement = autoSegmentSpaceManagement;
	}

	/**
	 * initial を取得します.
	 * 
	 * @return initial
	 */
	public String getInitial() {
		return initial;
	}

	/**
	 * initial を設定します.
	 * 
	 * @param initial
	 *            initial
	 */
	public void setInitial(String initial) {
		this.initial = initial;
	}

	/**
	 * next を取得します.
	 * 
	 * @return next
	 */
	public String getNext() {
		return next;
	}

	/**
	 * next を設定します.
	 * 
	 * @param next
	 *            next
	 */
	public void setNext(String next) {
		this.next = next;
	}

	/**
	 * minExtents を取得します.
	 * 
	 * @return minExtents
	 */
	public String getMinExtents() {
		return minExtents;
	}

	/**
	 * minExtents を設定します.
	 * 
	 * @param minExtents
	 *            minExtents
	 */
	public void setMinExtents(String minExtents) {
		this.minExtents = minExtents;
	}

	/**
	 * maxExtents を取得します.
	 * 
	 * @return maxExtents
	 */
	public String getMaxExtents() {
		return maxExtents;
	}

	/**
	 * maxExtents を設定します.
	 * 
	 * @param maxExtents
	 *            maxExtents
	 */
	public void setMaxExtents(String maxExtents) {
		this.maxExtents = maxExtents;
	}

	/**
	 * pctIncrease を取得します.
	 * 
	 * @return pctIncrease
	 */
	public String getPctIncrease() {
		return pctIncrease;
	}

	/**
	 * pctIncrease を設定します.
	 * 
	 * @param pctIncrease
	 *            pctIncrease
	 */
	public void setPctIncrease(String pctIncrease) {
		this.pctIncrease = pctIncrease;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TablespaceProperties clone() {
		OracleTablespaceProperties properties = new OracleTablespaceProperties();

		properties.autoExtend = this.autoExtend;
		properties.autoExtendMaxSize = this.autoExtendMaxSize;
		properties.autoExtendSize = this.autoExtendSize;
		properties.autoSegmentSpaceManagement = this.autoSegmentSpaceManagement;
		properties.dataFile = this.dataFile;
		properties.fileSize = this.fileSize;
		properties.initial = this.initial;
		properties.logging = this.logging;
		properties.maxExtents = this.maxExtents;
		properties.minExtents = this.minExtents;
		properties.minimumExtentSize = this.minimumExtentSize;
		properties.next = this.next;
		properties.offline = this.offline;
		properties.pctIncrease = this.pctIncrease;
		properties.temporary = this.temporary;

		return properties;
	}

	public LinkedHashMap<String, String> getPropertiesMap() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		map.put("label.tablespace.data.file", this.getDataFile());
		map.put("label.size", this.getFileSize());
		map.put("label.tablespace.auto.extend", String.valueOf(this
				.isAutoExtend()));
		map.put("label.size", this.getAutoExtendSize());
		map.put("label.max.size", this.getAutoExtendMaxSize());
		map.put("label.tablespace.minimum.extent.size", this
				.getMinimumExtentSize());
		map.put("label.tablespace.initial", this.getInitial());
		map.put("label.tablespace.next", this.getNext());
		map.put("label.tablespace.min.extents", this.getMinExtents());
		map.put("label.tablespace.pct.increase", this.getPctIncrease());
		map.put("label.tablespace.logging", String.valueOf(this.isLogging()));
		map.put("label.tablespace.offline", String.valueOf(this.isOffline()));
		map.put("label.tablespace.temporary", String
				.valueOf(this.isTemporary()));
		map.put("label.tablespace.auto.segment.space.management", String
				.valueOf(this.isAutoSegmentSpaceManagement()));

		return map;
	}

	public List<String> validate() {
		List<String> errorMessage = new ArrayList<String>();

		if (this.isAutoExtend() && Check.isEmptyTrim(this.getAutoExtendSize())) {
			errorMessage.add("error.tablespace.auto.extend.size.empty");
		}

		return errorMessage;
	}
}
