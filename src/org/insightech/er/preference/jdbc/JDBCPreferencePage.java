package org.insightech.er.preference.jdbc;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.ResourceString;
import org.insightech.er.Resources;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.settings.JDBCDriverSetting;
import org.insightech.er.preference.PreferenceInitializer;
import org.insightech.er.util.Format;

public class JDBCPreferencePage extends
		org.eclipse.jface.preference.PreferencePage implements
		IWorkbenchPreferencePage {

	private Table table;

	private Button addButton;

	private Button editButton;

	private Button deleteButton;

	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;

		composite.setLayout(gridLayout);

		this.initTable(composite);
		this.createButton(composite);
		this.addListener();

		return composite;
	}

	private void initTable(Composite parent) {
		this.table = new Table(parent, SWT.SINGLE | SWT.BORDER
				| SWT.FULL_SELECTION);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = 200;
		gridData.horizontalSpan = 3;

		this.table.setLayoutData(gridData);

		this.table.setLinesVisible(true);
		this.table.setHeaderVisible(true);

		TableColumn nameColumn = new TableColumn(table, SWT.NONE);
		nameColumn.setText(ResourceString.getResourceString("label.database"));
		nameColumn.setWidth(200);

		TableColumn driverClassNameColumn = new TableColumn(table, SWT.NONE);
		driverClassNameColumn.setText(ResourceString
				.getResourceString("label.driver.class.name"));
		driverClassNameColumn.setWidth(200);

		TableColumn pathColumn = new TableColumn(table, SWT.NONE);
		pathColumn.setText(ResourceString.getResourceString("label.path"));
		pathColumn.setWidth(200);

		this.setData();
	}

	private void createButton(Composite parent) {
		GridData buttonGridData = new GridData();
		buttonGridData.widthHint = Resources.BUTTON_WIDTH;

		this.addButton = new Button(parent, SWT.NONE);
		this.addButton.setLayoutData(buttonGridData);
		this.addButton.setText(ResourceString
				.getResourceString("label.button.add"));

		this.editButton = new Button(parent, SWT.NONE);
		this.editButton.setLayoutData(buttonGridData);
		this.editButton.setText(ResourceString
				.getResourceString("label.button.edit"));

		this.deleteButton = new Button(parent, SWT.NONE);
		this.deleteButton.setLayoutData(buttonGridData);
		this.deleteButton.setText(ResourceString
				.getResourceString("label.button.delete"));
		this.deleteButton.setEnabled(false);
	}

	private void setData() {
		this.table.removeAll();

		for (JDBCDriverSetting setting : PreferenceInitializer
				.getJDBCDriverSettingList()) {
			TableItem tableItem = new TableItem(this.table, SWT.NONE);
			tableItem.setBackground(ColorConstants.white);
			tableItem.setText(0, Format.null2blank(setting.getDb()));
			tableItem.setText(1, Format.null2blank(setting.getClassName()));
			tableItem.setText(2, Format.null2blank(setting.getPath()));
		}
	}

	@Override
	protected void performDefaults() {
		PreferenceInitializer.clearJDBCDriverInfo();

		setData();

		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		PreferenceInitializer.clearJDBCDriverInfo();

		for (int i = 0; i < this.table.getItemCount(); i++) {
			TableItem tableItem = this.table.getItem(i);

			String db = tableItem.getText(0);
			String driverClassName = tableItem.getText(1);
			String path = tableItem.getText(2);

			PreferenceInitializer.addJDBCDriver(db, driverClassName, path);
		}

		return super.performOk();
	}

	private void addListener() {
		this.table.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent selectionevent) {
				int index = table.getSelectionIndex();
				if (index == -1) {
					return;
				}

				TableItem item = table.getItem(index);

				String db = item.getText(0);
				String driverClassName = item.getText(1);

				DBManager dbManager = DBManagerFactory.getDBManager(db);

				if (!dbManager.getDriverClassName().equals(driverClassName)) {
					deleteButton.setEnabled(true);

				} else {
					deleteButton.setEnabled(false);
				}
			}

		});

		this.table.addMouseListener(new MouseAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				edit();
			}
		});

		this.addButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<JDBCDriverSetting> otherDriverSettingList = getOtherDriverSettingList(-1);

				JDBCPathDialog dialog = new JDBCPathDialog(PlatformUI
						.getWorkbench().getActiveWorkbenchWindow().getShell(),
						null, null, null, otherDriverSettingList, true);

				if (dialog.open() == IDialogConstants.OK_ID) {
					PreferenceInitializer.addJDBCDriver(dialog.getDatabase(),
							Format.null2blank(dialog.getDriverClassName()),
							Format.null2blank(dialog.getPath()));

					setData();
				}
			}

		});

		this.editButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				edit();
			}

		});

		this.deleteButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				try {
					int index = table.getSelectionIndex();

					if (index == -1) {
						return;
					}

					TableItem item = table.getItem(index);

					String db = item.getText(0);
					String driverClassName = item.getText(1);

					DBManager dbManager = DBManagerFactory.getDBManager(db);

					if (!dbManager.getDriverClassName().equals(driverClassName)) {
						table.remove(index);
					}

				} catch (Exception e) {
					Activator.showExceptionDialog(e);
				}
			}
		});
	}

	private List<JDBCDriverSetting> getOtherDriverSettingList(int index) {
		List<JDBCDriverSetting> list = new ArrayList<JDBCDriverSetting>();

		for (int i = 0; i < this.table.getItemCount(); i++) {
			if (i != index) {
				TableItem tableItem = this.table.getItem(i);

				String db = tableItem.getText(0);
				String driverClassName = tableItem.getText(1);
				String path = tableItem.getText(2);

				JDBCDriverSetting driverSetting = new JDBCDriverSetting(db,
						driverClassName, path);
				list.add(driverSetting);
			}
		}

		return list;
	}

	private void edit() {
		try {
			int index = table.getSelectionIndex();
			if (index == -1) {
				return;
			}

			TableItem item = table.getItem(index);

			List<JDBCDriverSetting> otherDriverSettingList = getOtherDriverSettingList(index);

			JDBCPathDialog dialog = new JDBCPathDialog(PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getShell(), item
					.getText(0), item.getText(1), item.getText(2),
					otherDriverSettingList, true);

			if (dialog.open() == IDialogConstants.OK_ID) {
				item.setText(1, dialog.getDriverClassName());
				item.setText(2, dialog.getPath());
			}

		} catch (Exception e) {
			Activator.showExceptionDialog(e);
		}
	}
}
