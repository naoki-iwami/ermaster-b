package org.insightech.er.editor.view.dialog.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.Activator;
import org.insightech.er.ResourceString;
import org.insightech.er.Resources;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.settings.CategorySetting;
import org.insightech.er.editor.model.settings.Settings;

public class CategoryManageDialog extends AbstractDialog {

	private Table categoryTable = null;

	private Table nodeTable = null;

	private Button addCategoryButton;

	private Button updateCategoryButton;

	private Button deleteCategoryButton;

	private Text categoryNameText = null;

	private ERDiagram diagram;

	private CategorySetting categorySettings;

	private Map<Category, TableEditor> categoryCheckMap;

	private Map<NodeElement, TableEditor> nodeCheckMap;

	private Category targetCategory;

	private Button upButton;

	private Button downButton;

	public CategoryManageDialog(Shell parentShell, Settings settings,
			ERDiagram diagram) {
		super(parentShell, 2);

		this.diagram = diagram;
		this.categorySettings = settings.getCategorySetting();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite composite) {
		this.createCategoryGroup(composite);
		this.createNodeGroup(composite);
	}

	private void createCategoryGroup(Composite composite) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;

		Group group = new Group(composite, SWT.NONE);
		group.setText(ResourceString
				.getResourceString("label.category.message"));
		group.setLayout(gridLayout);

		CompositeFactory.filler(group, 4);

		GridData tableGridData = new GridData();
		tableGridData.heightHint = 200;
		tableGridData.horizontalSpan = 3;
		tableGridData.verticalSpan = 2;

		this.categoryTable = new Table(group, SWT.BORDER | SWT.FULL_SELECTION);
		this.categoryTable.setHeaderVisible(true);
		this.categoryTable.setLayoutData(tableGridData);
		this.categoryTable.setLinesVisible(true);

		GridData upButtonGridData = new GridData();
		upButtonGridData.grabExcessHorizontalSpace = false;
		upButtonGridData.verticalAlignment = GridData.END;
		upButtonGridData.grabExcessVerticalSpace = true;
		upButtonGridData.widthHint = Resources.BUTTON_WIDTH;

		GridData downButtonGridData = new GridData();
		downButtonGridData.grabExcessVerticalSpace = true;
		downButtonGridData.verticalAlignment = GridData.BEGINNING;
		downButtonGridData.widthHint = Resources.BUTTON_WIDTH;

		this.upButton = new Button(group, SWT.NONE);
		this.upButton.setText(ResourceString
				.getResourceString("label.up.arrow"));
		this.upButton.setLayoutData(upButtonGridData);

		this.downButton = new Button(group, SWT.NONE);
		this.downButton.setText(ResourceString
				.getResourceString("label.down.arrow"));
		this.downButton.setLayoutData(downButtonGridData);

		GridData textGridData = new GridData();
		textGridData.widthHint = 150;

		this.categoryNameText = new Text(group, SWT.BORDER);
		this.categoryNameText.setLayoutData(textGridData);

		GridData buttonGridData = new GridData();
		buttonGridData.widthHint = Resources.BUTTON_WIDTH;

		this.addCategoryButton = new Button(group, SWT.NONE);
		this.addCategoryButton.setLayoutData(buttonGridData);
		this.addCategoryButton.setText(ResourceString
				.getResourceString("label.button.add"));

		this.updateCategoryButton = new Button(group, SWT.NONE);
		this.updateCategoryButton.setLayoutData(buttonGridData);
		this.updateCategoryButton.setText(ResourceString
				.getResourceString("label.button.update"));

		this.deleteCategoryButton = new Button(group, SWT.NONE);
		this.deleteCategoryButton.setLayoutData(buttonGridData);
		this.deleteCategoryButton.setText(ResourceString
				.getResourceString("label.button.delete"));

		TableColumn tableColumn = new TableColumn(categoryTable, SWT.NONE);
		tableColumn.setWidth(30);
		tableColumn.setResizable(false);
		TableColumn tableColumn1 = new TableColumn(categoryTable, SWT.NONE);
		tableColumn1.setWidth(230);
		tableColumn1.setResizable(false);
		tableColumn1.setText(ResourceString
				.getResourceString("label.category.name"));
	}

	private void createNodeGroup(Composite composite) {
		Group group = new Group(composite, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setText(ResourceString
				.getResourceString("label.category.object.message"));

		GridData gridData1 = new GridData();
		gridData1.heightHint = 15;

		Label label = new Label(group, SWT.NONE);
		label.setText("");
		label.setLayoutData(gridData1);

		GridData tableGridData = new GridData();
		tableGridData.heightHint = 200;

		this.nodeTable = new Table(group, SWT.BORDER | SWT.HIDE_SELECTION);
		this.nodeTable.setHeaderVisible(true);
		this.nodeTable.setLayoutData(tableGridData);
		this.nodeTable.setLinesVisible(true);

		GridData gridData2 = new GridData();
		gridData2.heightHint = 22;

		label = new Label(group, SWT.NONE);
		label.setText("");
		label.setLayoutData(gridData2);

		TableColumn tableColumn2 = new TableColumn(this.nodeTable, SWT.NONE);
		tableColumn2.setWidth(30);
		tableColumn2.setResizable(false);
		tableColumn2.setText("");
		TableColumn tableColumn3 = new TableColumn(this.nodeTable, SWT.NONE);
		tableColumn3.setWidth(80);
		tableColumn3.setResizable(false);
		tableColumn3.setText(ResourceString
				.getResourceString("label.object.type"));
		TableColumn tableColumn4 = new TableColumn(this.nodeTable, SWT.NONE);
		tableColumn4.setWidth(200);
		tableColumn4.setResizable(false);
		tableColumn4.setText(ResourceString
				.getResourceString("label.object.name"));
	}

	private void initCategoryTable() {
		this.categoryTable.removeAll();

		if (this.categoryCheckMap != null) {
			for (TableEditor editor : this.categoryCheckMap.values()) {
				editor.getEditor().dispose();
				editor.dispose();
			}

			this.categoryCheckMap.clear();
		} else {
			this.categoryCheckMap = new HashMap<Category, TableEditor>();
		}

		for (Category category : categorySettings.getAllCategories()) {
			TableItem tableItem = new TableItem(this.categoryTable, SWT.NONE);

			Button selectCheckButton = new Button(this.categoryTable, SWT.CHECK);
			selectCheckButton.pack();

			TableEditor editor = new TableEditor(this.categoryTable);

			editor.minimumWidth = selectCheckButton.getSize().x;
			editor.horizontalAlignment = SWT.CENTER;
			editor.setEditor(selectCheckButton, tableItem, 0);

			tableItem.setText(1, category.getName());

			if (categorySettings.isSelected(category)) {
				selectCheckButton.setSelection(true);
			}

			this.categoryCheckMap.put(category, editor);

			if (this.targetCategory == category) {
				categoryTable.setSelection(tableItem);
			}
		}

		if (this.targetCategory != null) {
			initNodeList(targetCategory);

		} else {
			deleteNodeList();
		}
	}

	private void initNodeTable() {
		this.nodeTable.removeAll();

		this.nodeCheckMap = new HashMap<NodeElement, TableEditor>();

		for (NodeElement nodeElement : this.diagram.getDiagramContents()
				.getContents()) {
			TableItem tableItem = new TableItem(this.nodeTable, SWT.NONE);

			Button selectCheckButton = new Button(this.nodeTable, SWT.CHECK);
			selectCheckButton.pack();

			TableEditor editor = new TableEditor(this.nodeTable);

			editor.minimumWidth = selectCheckButton.getSize().x;
			editor.horizontalAlignment = SWT.CENTER;
			editor.setEditor(selectCheckButton, tableItem, 0);

			tableItem.setText(1, ResourceString
					.getResourceString("label.object.type."
							+ nodeElement.getObjectType()));
			tableItem.setText(2, nodeElement.getName());

			this.nodeCheckMap.put(nodeElement, editor);
		}
	}

	private void initNodeList(Category category) {
		this.categoryNameText.setText(category.getName());

		for (NodeElement nodeElement : this.nodeCheckMap.keySet()) {
			Button selectCheckButton = (Button) this.nodeCheckMap.get(
					nodeElement).getEditor();

			if (category.contains(nodeElement)) {
				selectCheckButton.setSelection(true);

			} else {
				selectCheckButton.setSelection(false);
			}
		}
	}

	private void deleteNodeList() {
		this.categoryNameText.setText("");

		this.nodeTable.removeAll();

		if (this.nodeCheckMap != null) {
			for (TableEditor editor : this.nodeCheckMap.values()) {
				editor.getEditor().dispose();
				editor.dispose();
			}

			this.nodeCheckMap.clear();
		}
	}

	@Override
	protected void addListener() {
		super.addListener();

		this.categoryTable.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = categoryTable.getSelectionIndex();
				if (index == -1) {
					return;
				}

				validatePage();

				if (targetCategory == null) {
					initNodeTable();
				}

				targetCategory = categorySettings.getAllCategories().get(index);
				initNodeList(targetCategory);
			}
		});

		this.addCategoryButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				String name = categoryNameText.getText().trim();

				if (name.equals("")) {
					return;
				}

				validatePage();

				if (targetCategory == null) {
					initNodeTable();
				}

				Category addCategory = new Category();
				int[] color = diagram.getDefaultColor();
				addCategory.setColor(color[0], color[1], color[2]);
				addCategory.setName(name);
				categorySettings.addCategoryAsSelected(addCategory);
				targetCategory = addCategory;

				initCategoryTable();
			}

		});

		this.updateCategoryButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				String name = categoryNameText.getText().trim();

				if (name.equals("")) {
					return;
				}

				validatePage();

				targetCategory.setName(name);

				initCategoryTable();
			}

		});

		this.deleteCategoryButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				try {
					int index = categoryTable.getSelectionIndex();

					validatePage();

					categorySettings.removeCategory(index);

					if (categoryTable.getItemCount() > index + 1) {

					} else if (categoryTable.getItemCount() != 0) {
						index = categoryTable.getItemCount() - 2;

					} else {
						index = -1;
					}

					if (index != -1) {
						targetCategory = categorySettings.getAllCategories()
								.get(index);
					} else {
						targetCategory = null;
					}

					initCategoryTable();

				} catch (Exception e) {
					Activator.log(e);
				}
			}
		});

		this.upButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = categoryTable.getSelectionIndex();

				if (index == -1 || index == 0) {
					return;
				}

				validatePage();
				changeColumn(index - 1, index);
				initCategoryTable();
			}

		});

		this.downButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = categoryTable.getSelectionIndex();

				if (index == -1 || index == categoryTable.getItemCount() - 1) {
					return;
				}

				validatePage();
				changeColumn(index, index + 1);
				initCategoryTable();
			}

		});
	}

	public void changeColumn(int index1, int index2) {
		List<Category> allCategories = this.categorySettings.getAllCategories();

		Category category1 = allCategories.remove(index1);
		Category category2 = null;

		if (index1 < index2) {
			category2 = allCategories.remove(index2 - 1);
			allCategories.add(index1, category2);
			allCategories.add(index2, category1);

		} else if (index1 > index2) {
			category2 = allCategories.remove(index2);
			allCategories.add(index1 - 1, category2);
			allCategories.add(index2, category1);
		}
	}

	@Override
	protected String getTitle() {
		return "label.category";
	}

	@Override
	protected void perfomeOK() throws InputException {
		validatePage();
	}

	@Override
	protected void setData() {
		this.initCategoryTable();
	}

	@Override
	protected String getErrorMessage() {
		return null;
	}

	public void validatePage() {
		if (targetCategory != null) {
			List<NodeElement> selectedNodeElementList = new ArrayList<NodeElement>();

			for (NodeElement table : this.nodeCheckMap.keySet()) {
				Button selectCheckButton = (Button) this.nodeCheckMap
						.get(table).getEditor();

				if (selectCheckButton.getSelection()) {
					selectedNodeElementList.add(table);
				}
			}

			targetCategory.setContents(selectedNodeElementList);
		}

		List<Category> selectedCategories = new ArrayList<Category>();

		for (Category category : categorySettings.getAllCategories()) {
			Button button = (Button) this.categoryCheckMap.get(category)
					.getEditor();

			if (button.getSelection()) {
				selectedCategories.add(category);
			}
		}

		categorySettings.setSelectedCategories(selectedCategories);
	}
}
