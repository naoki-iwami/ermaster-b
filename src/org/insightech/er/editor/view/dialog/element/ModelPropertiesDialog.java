package org.insightech.er.editor.view.dialog.element;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.editor.model.diagram_contents.element.node.model_properties.ModelProperties;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;
import org.insightech.er.util.NameValue;

public class ModelPropertiesDialog extends AbstractDialog {

	private static final int BUTTON_WIDTH = 60;

	private Table table;

	private Button addButton;

	private Button deleteButton;

	private Button upButton;

	private Button downButton;

	private ModelProperties modelProperties;

	private TableEditor tableEditor;

	int targetColumn = -1;

	public ModelPropertiesDialog(Shell parentShell,
			ModelProperties modelProperties) {
		super(parentShell, 2);

		this.modelProperties = modelProperties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite composite) {
		this.createTableComposite(composite);
		this.createButtonComposite(composite);
	}

	/**
	 * This method initializes composite1
	 * 
	 */
	private void createTableComposite(Composite parent) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;

		GridData gridData = new GridData();
		gridData.heightHint = 320;

		GridData tableGridData = new GridData();
		tableGridData.horizontalSpan = 3;
		tableGridData.heightHint = 185;

		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(gridLayout);
		composite.setLayoutData(gridData);

		table = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLayoutData(tableGridData);
		table.setLinesVisible(true);

		TableColumn tableColumn0 = new TableColumn(table, SWT.NONE);
		tableColumn0.setWidth(200);
		tableColumn0.setText(ResourceString
				.getResourceString("label.property.name"));
		TableColumn tableColumn1 = new TableColumn(table, SWT.NONE);
		tableColumn1.setWidth(200);
		tableColumn1.setText(ResourceString
				.getResourceString("label.property.value"));

		this.tableEditor = new TableEditor(table);
		this.tableEditor.grabHorizontal = true;

		this.table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent event) {
				int index = table.getSelectionIndex();
				if (index == -1) {
					return;
				}

				TableItem item = table.getItem(index);
				Point selectedPoint = new Point(event.x, event.y);

				targetColumn = -1;

				for (int i = 0; i < table.getColumnCount(); i++) {
					Rectangle rect = item.getBounds(i);
					if (rect.contains(selectedPoint)) {
						targetColumn = i;
						break;
					}
				}

				if (targetColumn == -1) {
					return;
				}

				edit(item, tableEditor);
			}

		});
	}

	private void edit(final TableItem item, final TableEditor tableEditor) {
		final Text text = new Text(table, SWT.NONE);
		text.setText(item.getText(targetColumn));

		text.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				item.setText(targetColumn, text.getText());
				text.dispose();
			}

		});

		tableEditor.setEditor(text, item, targetColumn);
		text.setFocus();
		text.selectAll();
	}

	private void addRow() {
		TableItem item = new TableItem(table, SWT.NULL);
		item.setText(0, "");
		item.setText(1, "");
		this.targetColumn = 0;

		edit(item, tableEditor);
	}

	/**
	 * This method initializes composite2
	 * 
	 */
	private void createButtonComposite(Composite parent) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 6;

		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(gridData);
		composite.setLayout(gridLayout);

		GridData buttonGridData = new GridData();
		buttonGridData.widthHint = BUTTON_WIDTH;

		this.addButton = new Button(composite, SWT.NONE);
		this.addButton.setText(ResourceString
				.getResourceString("label.button.add"));
		this.addButton.setLayoutData(buttonGridData);

		this.addButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				addRow();
			}

		});

		this.deleteButton = new Button(composite, SWT.NONE);
		this.deleteButton.setText(ResourceString
				.getResourceString("label.button.delete"));
		this.deleteButton.setLayoutData(buttonGridData);

		this.deleteButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeColumn();
			}

		});

		Label filler = new Label(composite, SWT.NONE);
		GridData fillerGridData = new GridData();
		fillerGridData.widthHint = 30;
		filler.setLayoutData(fillerGridData);

		this.upButton = new Button(composite, SWT.NONE);
		this.upButton.setText(ResourceString
				.getResourceString("label.up.arrow"));
		this.upButton.setLayoutData(buttonGridData);

		this.upButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				upColumn();
			}

		});

		this.downButton = new Button(composite, SWT.NONE);
		this.downButton.setText(ResourceString
				.getResourceString("label.down.arrow"));
		this.downButton.setLayoutData(buttonGridData);

		this.downButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				downColumn();
			}

		});
	}

	private void removeColumn() {
		int index = this.table.getSelectionIndex();

		if (index != -1) {
			this.table.remove(index);
		}

		this.validate();
	}

	private void upColumn() {
		int index = this.table.getSelectionIndex();

		if (index != -1 && index != 0) {
			this.changeColumn(index - 1, index);
			this.table.setSelection(index - 1);
		}
	}

	private void downColumn() {
		int index = this.table.getSelectionIndex();

		if (index != -1 && index != table.getItemCount() - 1) {
			this.changeColumn(index, index + 1);
			table.setSelection(index + 1);
		}
	}

	private void changeColumn(int index1, int index2) {
		TableItem item1 = this.table.getItem(index1);
		TableItem item2 = this.table.getItem(index2);

		String name1 = item1.getText(0);
		String value1 = item1.getText(1);

		item1.setText(0, item2.getText(0));
		item1.setText(1, item2.getText(1));

		item2.setText(0, name1);
		item2.setText(1, value1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getErrorMessage() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void perfomeOK() {
		this.modelProperties.clear();

		for (int i = 0; i < this.table.getItemCount(); i++) {
			TableItem item = this.table.getItem(i);

			if (Check.isEmpty(item.getText(0))
					&& Check.isEmpty(item.getText(1))) {
				continue;
			}

			NameValue property = new NameValue(item.getText(0), item.getText(1));
			this.modelProperties.addProperty(property);
		}
	}

	@Override
	protected String getTitle() {
		return "label.search.range.model.property";
	}

	@Override
	protected void setData() {
		for (NameValue property : this.modelProperties.getProperties()) {
			TableItem item = new TableItem(table, SWT.NULL);
			item.setText(0, Format.null2blank(property.getName()));
			item.setText(1, Format.null2blank(property.getValue()));
		}
	}
}
