package org.insightech.er.editor.model.settings;

import java.io.Serializable;
import java.math.BigDecimal;

import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TablePropertiesHolder;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableViewProperties;

public class Settings implements Serializable, Cloneable, TablePropertiesHolder {

	private static final long serialVersionUID = -3921093777077765516L;

	public static final int VIEW_MODE_LOGICAL = 0;

	public static final int VIEW_MODE_PHYSICAL = 1;

	public static final int VIEW_MODE_BOTH = 2;

	public static final int NOTATION_LEVLE_DETAIL = 0;

	public static final int NOTATION_LEVLE_TITLE = 1;

	public static final int NOTATION_LEVLE_COLUMN = 2;

	public static final int NOTATION_LEVLE_KEY = 3;

	public static final int NOTATION_LEVLE_EXCLUDE_TYPE = 4;

	public static final int NOTATION_LEVLE_NAME_AND_KEY = 5;

	public static final String NOTATION_IE = "IE";

	public static final String NOTATION_IDEF1X = "IDEF1X";

	private boolean capital;

	private boolean notationExpandGroup;

	private String tableStyle;

	private ModelProperties modelProperties;

	private CategorySetting categorySetting;
//	private VGroupSetting groupSetting;

	private TranslationSetting translationSetting;

	private EnvironmentSetting environmentSetting;

	private TableProperties tableProperties;

	private ExportSetting exportSetting;

	private String database;

	private String notation;

	private int notationLevel;

	private int viewMode;

	private int viewOrderBy;

	private int outlineViewMode;

	private BigDecimal titleFontEm;

	private boolean autoImeChange;

	private boolean validatePhysicalName;

	private boolean useBezierCurve;

	private boolean suspendValidator;

	private String masterDataBasePath;

	public int getNotationLevel() {
		return notationLevel;
	}

	public void setNotationLevel(int notationLevel) {
		this.notationLevel = notationLevel;
	}

	public Settings() {
		this.capital = true;
		this.notationExpandGroup = true;

		this.tableStyle = null;
		this.viewMode = VIEW_MODE_PHYSICAL;
		this.outlineViewMode = VIEW_MODE_PHYSICAL;
		this.viewOrderBy = VIEW_MODE_PHYSICAL;

		this.modelProperties = new ModelProperties();
		this.categorySetting = new CategorySetting();
//		this.groupSetting = new VGroupSetting();
		this.translationSetting = new TranslationSetting();
		this.environmentSetting = new EnvironmentSetting();
		this.exportSetting = new ExportSetting();

		this.autoImeChange = false;
		this.validatePhysicalName = true;
		this.useBezierCurve = false;
		this.suspendValidator = false;
		this.titleFontEm = new BigDecimal("1.5");
		this.masterDataBasePath = "";
	}

	public boolean isCapital() {
		return capital;
	}

	public void setCapital(boolean capital) {
		this.capital = capital;
	}

	public boolean isNotationExpandGroup() {
		return notationExpandGroup;
	}

	public void setNotationExpandGroup(boolean notationExpandGroup) {
		this.notationExpandGroup = notationExpandGroup;
	}

	public String getTableStyle() {
		return tableStyle;
	}

	public void setTableStyle(String tableStyle) {
		this.tableStyle = tableStyle;
	}

	public ModelProperties getModelProperties() {
		return modelProperties;
	}

	public CategorySetting getCategorySetting() {
		return categorySetting;
	}

//	public VGroupSetting getGroupSetting() {
//		return groupSetting;
//	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public TableViewProperties getTableViewProperties() {
		this.tableProperties = DBManagerFactory.getDBManager(database)
				.createTableProperties(this.tableProperties);

		return tableProperties;
	}

	public String getNotation() {
		return notation;
	}

	public void setNotation(String notation) {
		this.notation = notation;
	}

	public int getViewMode() {
		return this.viewMode;
	}

	public void setViewMode(int viewMode) {
		this.viewMode = viewMode;
	}

	public int getOutlineViewMode() {
		return outlineViewMode;
	}

	public void setOutlineViewMode(int outlineViewMode) {
		this.outlineViewMode = outlineViewMode;
	}

	public int getViewOrderBy() {
		return viewOrderBy;
	}

	public void setViewOrderBy(int viewOrderBy) {
		this.viewOrderBy = viewOrderBy;
	}

	/**
	 * titleFontEmを取得します。
	 * @return titleFontEm
	 */
	public BigDecimal getTitleFontEm() {
	    return titleFontEm;
	}

	/**
	 * titleFontEmを設定します。
	 * @param titleFontEm titleFontEm
	 */
	public void setTitleFontEm(BigDecimal titleFontEm) {
	    this.titleFontEm = titleFontEm;
	}

	public boolean isAutoImeChange() {
		return autoImeChange;
	}

	public void setAutoImeChange(boolean autoImeChange) {
		this.autoImeChange = autoImeChange;
	}

	public boolean isValidatePhysicalName() {
		return validatePhysicalName;
	}

	public void setValidatePhysicalName(boolean validatePhysicalName) {
		this.validatePhysicalName = validatePhysicalName;
	}

	public boolean isUseBezierCurve() {
		return useBezierCurve;
	}

	public void setUseBezierCurve(boolean useBezierCurve) {
		this.useBezierCurve = useBezierCurve;
	}

	public boolean isSuspendValidator() {
		return suspendValidator;
	}

	public void setSuspendValidator(boolean suspendValidator) {
		this.suspendValidator = suspendValidator;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object clone() {
		Settings clone = null;
		try {
			clone = (Settings) super.clone();
			clone.modelProperties = (ModelProperties) modelProperties.clone();
			clone.categorySetting = (CategorySetting) categorySetting.clone();
			clone.translationSetting = (TranslationSetting) translationSetting
					.clone();
			clone.environmentSetting = (EnvironmentSetting) environmentSetting
					.clone();
			clone.exportSetting = exportSetting.clone();

			if (this.database != null) {
				clone.tableProperties = (TableProperties) this
						.getTableViewProperties().clone();
			}

		} catch (CloneNotSupportedException e) {
		}

		return clone;
	}

	public void setModelProperties(ModelProperties modelProperties) {
		this.modelProperties = modelProperties;
	}

	/**
	 * translationSettings を取得します.
	 *
	 * @return translationSettings
	 */
	public TranslationSetting getTranslationSetting() {
		return translationSetting;
	}

	/**
	 * environmentSetting を取得します.
	 *
	 * @return environmentSetting
	 */
	public EnvironmentSetting getEnvironmentSetting() {
		return environmentSetting;
	}

	public ExportSetting getExportSetting() {
		return exportSetting;
	}

	public void setExportSetting(ExportSetting exportSetting) {
		this.exportSetting = exportSetting;
	}

	/**
	 * masterDataBasePathを取得します。
	 * @return masterDataBasePath
	 */
	public String getMasterDataBasePath() {
	    return masterDataBasePath;
	}

	/**
	 * masterDataBasePathを設定します。
	 * @param masterDataBasePath masterDataBasePath
	 */
	public void setMasterDataBasePath(String masterDataBasePath) {
	    this.masterDataBasePath = masterDataBasePath;
	}

}
