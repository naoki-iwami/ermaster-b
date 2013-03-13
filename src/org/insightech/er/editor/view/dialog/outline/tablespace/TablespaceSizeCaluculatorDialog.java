package org.insightech.er.editor.view.dialog.outline.tablespace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.ListenerAppender;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.db.sqltype.SqlTypeManager;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.view.dialog.common.EditableTable;
import org.insightech.er.util.Format;

public class TablespaceSizeCaluculatorDialog extends AbstractDialog implements
		EditableTable {

	private static final int NAME_WIDTH = 200;

	private static final int NUM_WIDTH = 50;

	private static final int TABLE_NUM_WIDTH = 100;

	private static final int INDENT = 30;

	private Table tableTable;

	private TableEditor tableEditor;

	private String errorMessage;

	private ERDiagram diagram;

	private List<ERTable> tableList;

	private Map<ERTable, Integer> tableNumMap;

	private Integer kcbh;

	private Integer ub4;

	private Integer ktbbh;

	private Integer ktbit;

	private Integer kdbh;

	private Integer kdbt;

	private Integer ub1;

	private Integer sb2;

	private Integer dbBlockSize;

	private Text kcbhText;

	private Text ub4Text;

	private Text ktbbhText;

	private Text ktbitText;

	private Text kdbhText;

	private Text kdbtText;

	private Text ub1Text;

	private Text sb2Text;

	private Text dbBlockSizeText;

	Button restoreDefaultButton1;

	Button restoreDefaultButton2;

	private int initrans = 1;

	private int pctfree = 10;

	private Text tablespaceSizeText;

	private void setDefault() {
		this.kcbh = 20;
		this.ub4 = 4;
		this.ktbbh = 48;
		this.ktbit = 24;
		this.kdbh = 14;
		this.kdbt = 4;
		this.ub1 = 1;
		this.sb2 = 2;

		this.dbBlockSize = 8192;
	}

	public TablespaceSizeCaluculatorDialog() {
		this(4);
	}

	public TablespaceSizeCaluculatorDialog(int numColumns) {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				numColumns);
	}

	public void init(ERDiagram diagram) {
		this.diagram = diagram;
		this.tableList = new ArrayList<ERTable>(this.diagram
				.getDiagramContents().getContents().getTableSet().getList());
		Collections.sort(this.tableList);

		this.tableNumMap = new HashMap<ERTable, Integer>();
	}

	@Override
	protected void initialize(Composite composite) {
		CompositeFactory.createLabel(composite,
				"label.tablespace.size.calculate.1", 3);

		this.restoreDefaultButton1 = new Button(composite, SWT.NONE);
		this.restoreDefaultButton1.setText(ResourceString
				.getResourceString("label.restore.default"));

		CompositeFactory.filler(composite, 1, INDENT);
		this.kcbhText = CompositeFactory.createNumText(this, composite, "KCBH",
				1, NUM_WIDTH);
		CompositeFactory.filler(composite, 1);

		CompositeFactory.filler(composite, 1, INDENT);
		this.ub4Text = CompositeFactory.createNumText(this, composite, "UB4",
				1, NUM_WIDTH);
		CompositeFactory.filler(composite, 1);

		CompositeFactory.filler(composite, 1, INDENT);
		this.ktbbhText = CompositeFactory.createNumText(this, composite,
				"KTBBH", 1, NUM_WIDTH);
		CompositeFactory.filler(composite, 1);

		CompositeFactory.filler(composite, 1, INDENT);
		this.ktbitText = CompositeFactory.createNumText(this, composite,
				"KTBIT", 1, NUM_WIDTH);
		CompositeFactory.filler(composite, 1);

		CompositeFactory.filler(composite, 1);
		this.kdbhText = CompositeFactory.createNumText(this, composite, "KDBH",
				1, NUM_WIDTH);
		CompositeFactory.filler(composite, 1);

		CompositeFactory.filler(composite, 1, INDENT);
		this.kdbtText = CompositeFactory.createNumText(this, composite, "KDBT",
				1, NUM_WIDTH);
		CompositeFactory.filler(composite, 1);

		CompositeFactory.filler(composite, 1);
		this.ub1Text = CompositeFactory.createNumText(this, composite, "UB1",
				1, NUM_WIDTH);
		CompositeFactory.filler(composite, 1);

		CompositeFactory.filler(composite, 1, INDENT);
		this.sb2Text = CompositeFactory.createNumText(this, composite, "SB2",
				1, NUM_WIDTH);
		CompositeFactory.filler(composite, 1);

		CompositeFactory.filler(composite, 4);

		CompositeFactory.createLabel(composite,
				"label.tablespace.size.calculate.2", 3);
		this.restoreDefaultButton2 = new Button(composite, SWT.NONE);
		this.restoreDefaultButton2.setText(ResourceString
				.getResourceString("label.restore.default"));

		CompositeFactory.filler(composite, 1, INDENT);
		this.dbBlockSizeText = CompositeFactory.createNumText(this, composite,
				"DB_BLOCK_SIZE", 1, NUM_WIDTH);
		CompositeFactory.filler(composite, 1);

		CompositeFactory.filler(composite, 4);

		CompositeFactory.createLabel(composite,
				"label.tablespace.size.calculate.3", 4);

		CompositeFactory.filler(composite, 4);

		GridData tableGridData = new GridData();
		tableGridData.horizontalSpan = 4;
		tableGridData.horizontalAlignment = GridData.FILL;
		tableGridData.grabExcessHorizontalSpace = true;
		tableGridData.heightHint = 100;

		this.tableTable = new Table(composite, SWT.SINGLE | SWT.BORDER
				| SWT.FULL_SELECTION);
		this.tableTable.setLayoutData(tableGridData);
		this.tableTable.setHeaderVisible(true);
		this.tableTable.setLinesVisible(true);

		TableColumn tableLogicalName = new TableColumn(this.tableTable,
				SWT.NONE);
		tableLogicalName.setWidth(NAME_WIDTH);
		tableLogicalName.setText(ResourceString
				.getResourceString("label.table.logical.name"));

		TableColumn num = new TableColumn(this.tableTable, SWT.RIGHT);
		num.setWidth(TABLE_NUM_WIDTH);
		num.setText(ResourceString.getResourceString("label.record.num"));

		this.tableEditor = new TableEditor(this.tableTable);
		this.tableEditor.grabHorizontal = true;

		CompositeFactory.createLabel(composite,
				"label.tablespace.size.calculated", 2);

		this.tablespaceSizeText = new Text(composite, SWT.BORDER
				| SWT.READ_ONLY | SWT.RIGHT);
		GridData textGridData = new GridData();
		textGridData.horizontalAlignment = GridData.FILL;
		textGridData.grabExcessHorizontalSpace = true;
		this.tablespaceSizeText.setLayoutData(textGridData);

		CompositeFactory.filler(composite, 1);
	}

	@Override
	protected String getErrorMessage() {
		if (this.errorMessage == null) {
			this.calculate();
		}
		return this.errorMessage;
	}

	@Override
	protected String getTitle() {
		return "dialog.title.tablespace.size.calculator";
	}

	@Override
	protected void perfomeOK() {
	}

	@Override
	protected void setData() {
		for (ERTable table : this.tableList) {
			TableItem tableItem = new TableItem(this.tableTable, SWT.NONE);
			this
					.column2TableItem(table, this.tableNumMap.get(table),
							tableItem);
		}

		this.setDefault();

		setParameterData1();
		setParameterData2();
	}

	private void setParameterData1() {
		this.kcbhText.setText(Format.toString(this.kcbh));
		this.ub4Text.setText(Format.toString(this.ub4));
		this.ktbbhText.setText(Format.toString(this.ktbbh));
		this.ktbitText.setText(Format.toString(this.ktbit));
		this.kdbhText.setText(Format.toString(this.kdbh));
		this.kdbtText.setText(Format.toString(this.kdbt));
		this.ub1Text.setText(Format.toString(this.ub1));
		this.sb2Text.setText(Format.toString(this.sb2));
	}

	private void setParameterData2() {
		this.dbBlockSizeText.setText(Format.toString(this.dbBlockSize));
	}

	private void column2TableItem(ERTable table, Integer num,
			TableItem tableItem) {
		if (table != null) {
			tableItem.setText(0, Format.null2blank(table.getLogicalName()));
		}
		tableItem.setText(1, Format.toString(num));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addListener() {
		ListenerAppender.addTableEditListener(this.tableTable,
				this.tableEditor, this);

		this.restoreDefaultButton1.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				setParameterData1();
				calculate();
			}

		});

		this.restoreDefaultButton2.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				setParameterData2();
				calculate();
			}

		});
	}

	public Control getControl(Point xy) {
		if (xy.x == 1) {
			return new Text(this.tableTable, SWT.BORDER | SWT.RIGHT);
		}

		return null;
	}

	public void setData(Point xy, Control control) {
		this.errorMessage = null;

		String text = ((Text) control).getText().trim();

		try {
			if (!text.equals("")) {
				int num = Integer.parseInt(text);
				if (num < 0) {
					this.errorMessage = "error.record.num.zero";
					return;
				}

				this.tableNumMap.put(this.tableList.get(xy.y), num);

				TableItem tableItem = this.tableTable.getItem(xy.y);
				this.column2TableItem(null, num, tableItem);
			}

		} catch (NumberFormatException e) {
			this.errorMessage = "error.record.num.degit";
			return;
		}
	}

	private void calculate() {
		double bytesOfBlockHeader = this.getValue(this.kcbhText)
				+ this.getValue(this.ub4Text) + this.getValue(this.ktbbhText)
				+ ((initrans - 1) * this.getValue(this.ktbitText))
				+ this.getValue(this.kdbhText);

		// 20 + 4 + 48 + (1-1) * 24 + 14;

		// this.kcbh = 20;
		// this.ub4 = 4;
		// this.ktbbh = 48;
		// this.ktbit = 24;
		// this.kdbh = 14;
		// this.kdbt = 4;
		// this.ub1 = 1;
		// this.sb2 = 2;

		double bytesOfDataPerBlock = Math.ceil((this
				.getValue(this.dbBlockSizeText) - bytesOfBlockHeader)
				* (1 - (this.pctfree / 100)))
				+ this.getValue(this.kdbtText);

		int total = 0;

		for (ERTable table : this.tableList) {
			double bytesPerRow = 3 * this.getValue(this.ub1Text)
					+ this.getTotalColumnSize(table)
					+ this.getValue(this.sb2Text);

			double rowNumPerBlock = Math.floor(bytesOfDataPerBlock
					/ bytesPerRow);
			Integer recordNum = tableNumMap.get(table);
			if (recordNum == null) {
				recordNum = 0;
			}

			double totalBlockNum = Math.ceil(recordNum / rowNumPerBlock);
			int totalBytes = (int) (totalBlockNum * this
					.getValue(this.dbBlockSizeText));

			total += totalBytes;
		}

		this.tablespaceSizeText.setText(Format.toString(total));
	}

	private int getTotalColumnSize(ERTable table) {
		int total = 0;

		DBManager dbManager = DBManagerFactory.getDBManager(diagram);
		SqlTypeManager manager = dbManager.getSqlTypeManager();

		for (NormalColumn column : table.getExpandedColumns()) {
			total += manager.getByteLength(column.getType(), column
					.getTypeData().getLength(), column.getTypeData()
					.getDecimal());
		}

		return total;
	}

	private double getValue(Text text) {
		double value = 0;

		try {
			value = Double.parseDouble(text.getText());
		} catch (NumberFormatException e) {
		}

		return value;
	}

	public void onDoubleClicked(Point xy) {
	}
}
