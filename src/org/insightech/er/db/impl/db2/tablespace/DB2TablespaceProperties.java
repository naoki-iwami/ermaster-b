package org.insightech.er.db.impl.db2.tablespace;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;

public class DB2TablespaceProperties implements TablespaceProperties {

	private static final long serialVersionUID = 3581869274788998047L;

	// (REGULAR/LARGI/SYSTEM TEMPORARY/USER TEMPORARY)
	private String type;

	private String pageSize;

	private String managedBy;

	private String container;

	// private String containerDirectoryPath;
	//
	// private String containerFilePath;
	//
	// private String containerPageNum;
	//
	// private String containerDevicePath;

	private String extentSize;

	private String prefetchSize;

	private String bufferPoolName;

	/**
	 * type を取得します.
	 * 
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * type を設定します.
	 * 
	 * @param type
	 *            type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * pageSize を取得します.
	 * 
	 * @return pageSize
	 */
	public String getPageSize() {
		return pageSize;
	}

	/**
	 * pageSize を設定します.
	 * 
	 * @param pageSize
	 *            pageSize
	 */
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * managedBy を取得します.
	 * 
	 * @return managedBy
	 */
	public String getManagedBy() {
		return managedBy;
	}

	/**
	 * managedBy を設定します.
	 * 
	 * @param managedBy
	 *            managedBy
	 */
	public void setManagedBy(String managedBy) {
		this.managedBy = managedBy;
	}

	/**
	 * extentSize を取得します.
	 * 
	 * @return extentSize
	 */
	public String getExtentSize() {
		return extentSize;
	}

	/**
	 * extentSize を設定します.
	 * 
	 * @param extentSize
	 *            extentSize
	 */
	public void setExtentSize(String extentSize) {
		this.extentSize = extentSize;
	}

	/**
	 * prefetchSize を取得します.
	 * 
	 * @return prefetchSize
	 */
	public String getPrefetchSize() {
		return prefetchSize;
	}

	/**
	 * prefetchSize を設定します.
	 * 
	 * @param prefetchSize
	 *            prefetchSize
	 */
	public void setPrefetchSize(String prefetchSize) {
		this.prefetchSize = prefetchSize;
	}

	/**
	 * bufferPoolName を取得します.
	 * 
	 * @return bufferPoolName
	 */
	public String getBufferPoolName() {
		return bufferPoolName;
	}

	/**
	 * bufferPoolName を設定します.
	 * 
	 * @param bufferPoolName
	 *            bufferPoolName
	 */
	public void setBufferPoolName(String bufferPoolName) {
		this.bufferPoolName = bufferPoolName;
	}

	/**
	 * container を取得します.
	 * 
	 * @return container
	 */
	public String getContainer() {
		return container;
	}

	/**
	 * container を設定します.
	 * 
	 * @param container
	 *            container
	 */
	public void setContainer(String container) {
		this.container = container;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TablespaceProperties clone() {
		DB2TablespaceProperties properties = new DB2TablespaceProperties();

		properties.bufferPoolName = this.bufferPoolName;
		properties.container = this.container;
		// properties.containerDevicePath = this.containerDevicePath;
		// properties.containerDirectoryPath = this.containerDirectoryPath;
		// properties.containerFilePath = this.containerFilePath;
		// properties.containerPageNum = this.containerPageNum;
		properties.extentSize = this.extentSize;
		properties.managedBy = this.managedBy;
		properties.pageSize = this.pageSize;
		properties.prefetchSize = this.prefetchSize;
		properties.type = this.type;

		return properties;
	}

	public LinkedHashMap<String, String> getPropertiesMap() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		map.put("label.tablespace.type", this.getType());
		map.put("label.tablespace.page.size", this.getPageSize());
		map.put("label.tablespace.managed.by", this.getManagedBy());
		map.put("label.tablespace.container", this.getContainer());
		map.put("label.tablespace.extent.size", this.getExtentSize());
		map.put("label.tablespace.prefetch.size", this.getPrefetchSize());
		map.put("label.tablespace.buffer.pool.name", this.getBufferPoolName());

		return map;
	}

	public List<String> validate() {
		List<String> errorMessage = new ArrayList<String>();
		return errorMessage;
	}
}
