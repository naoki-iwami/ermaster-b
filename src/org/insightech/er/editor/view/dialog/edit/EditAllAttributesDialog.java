package org.insightech.er.editor.view.dialog.edit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CenteredContentCellPaint;
import org.insightech.er.common.widgets.ListenerAppender;
import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.DiagramContents;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.Column;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.CopyWord;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Dictionary;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.TypeData;
import org.insightech.er.editor.model.diagram_contents.not_element.dictionary.Word;
import org.insightech.er.editor.model.edit.CopyManager;
import org.insightech.er.editor.view.dialog.common.EditableTable;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public class EditAllAttributesDialog extends AbstractDialog implements
		EditableTable {

	private static final int KEY_WIDTH = 45;

	private static final int NAME_WIDTH = 150;

	private static final int TYPE_WIDTH = 100;

	private static final int NOT_NULL_WIDTH = 80;

	private static final int UNIQUE_KEY_WIDTH = 70;

	private Table attributeTable;

	private TableEditor tableEditor;

	private ERDiagram diagram;

	private DiagramContents diagramContents;

	private List<Column> columnList;

	private List<Word> wordList;

	private String errorMessage;

	// private Map<Column, TableEditor[]> columnNotNullCheckMap = new
	// HashMap<Column, TableEditor[]>();

	public EditAllAttributesDialog(Shell parentShell, ERDiagram diagram) {
		super(parentShell, 2);
		this.diagram = diagram;

		CopyManager copyManager = new CopyManager();

		this.diagramContents = copyManager.copy(this.diagram
				.getDiagramContents());
		this.columnList = new ArrayList<Column>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getErrorMessage() {
		return errorMessage;
	}

	// @Override
	// protected void addListener() {
	// super.addListener();
	//
	// this.attributeTable.addListener(SWT.EraseItem, new Listener() {
	// public void handleEvent(Event event) {
	// // event.detail &= ~SWT.HOT;
	// if ((event.detail & SWT.SELECTED) == 0) {
	// Table table = (Table) event.widget;
	// TableItem item = (TableItem) event.item;
	//
	// if (item.getBackground().equals(
	// Resources.SELECTED_FOREIGNKEY_COLUMN)) {
	// int clientWidth = table.getClientArea().width;
	//
	// GC gc = event.gc;
	// // Color oldForeground = gc.getForeground();
	// Color oldBackground = gc.getBackground();
	//
	// gc.setBackground(Resources.SELECTED_FOREIGNKEY_COLUMN);
	// // gc.setForeground(colorForeground);
	// gc.fillRectangle(0, event.y, clientWidth, event.height);
	//
	// // gc.setForeground(oldForeground);
	// gc.setBackground(oldBackground);
	// // event.detail &= ~SWT.SELECTED;
	// }
	// }
	// }
	// });
	// }

	@Override
	protected String getTitle() {
		return "dialog.title.edit.all.attributes";
	}

	@Override
	protected void initialize(Composite composite) {
		this.createTable(composite);
	}

	/**
	 * This method initializes composite2
	 * 
	 */
	private void createTable(Composite composite) {
		GridData tableGridData = new GridData();
		tableGridData.horizontalSpan = 3;
		tableGridData.heightHint = 400;
		tableGridData.horizontalAlignment = GridData.FILL;
		tableGridData.grabExcessHorizontalSpace = true;

		this.attributeTable = new Table(composite, SWT.SINGLE | SWT.BORDER
				| SWT.FULL_SELECTION);
		this.attributeTable.setLayoutData(tableGridData);
		this.attributeTable.setHeaderVisible(true);
		this.attributeTable.setLinesVisible(true);

		TableColumn columnLogicalName = new TableColumn(this.attributeTable,
				SWT.NONE);
		columnLogicalName.setWidth(NAME_WIDTH);
		columnLogicalName.setText(ResourceString
				.getResourceString("label.column.logical.name"));

		TableColumn columnPhysicalName = new TableColumn(this.attributeTable,
				SWT.NONE);
		columnPhysicalName.setWidth(NAME_WIDTH);
		columnPhysicalName.setText(ResourceString
				.getResourceString("label.column.physical.name"));

		TableColumn tableLogicalName = new TableColumn(this.attributeTable,
				SWT.NONE);
		tableLogicalName.setWidth(NAME_WIDTH);
		tableLogicalName.setText(ResourceString
				.getResourceString("label.table.logical.name"));

		TableColumn tablePhysicalName = new TableColumn(this.attributeTable,
				SWT.NONE);
		tablePhysicalName.setWidth(NAME_WIDTH);
		tablePhysicalName.setText(ResourceString
				.getResourceString("label.table.physical.name"));

		TableColumn tableWord = new TableColumn(this.attributeTable, SWT.NONE);
		tableWord.setWidth(NAME_WIDTH);
		tableWord.setText(ResourceString.getResourceString("label.word"));

		TableColumn columnType = new TableColumn(this.attributeTable, SWT.NONE);
		columnType.setWidth(TYPE_WIDTH);
		columnType.setText(ResourceString
				.getResourceString("label.column.type"));

		TableColumn columnLength = new TableColumn(this.attributeTable,
				SWT.RIGHT);
		columnLength.setWidth(TYPE_WIDTH);
		columnLength.setText(ResourceString
				.getResourceString("label.column.length"));

		TableColumn columnDecimal = new TableColumn(this.attributeTable,
				SWT.RIGHT);
		columnDecimal.setWidth(TYPE_WIDTH);
		columnDecimal.setText(ResourceString
				.getResourceString("label.column.decimal"));

		TableColumn columnKey = new TableColumn(this.attributeTable, SWT.CENTER);
		columnKey.setText("PK");
		columnKey.setWidth(KEY_WIDTH);
		new CenteredContentCellPaint(this.attributeTable, 8);

		TableColumn columnForeignKey = new TableColumn(this.attributeTable,
				SWT.CENTER);
		columnForeignKey.setText("FK");
		columnForeignKey.setWidth(KEY_WIDTH);
		new CenteredContentCellPaint(this.attributeTable, 9);

		TableColumn columnNotNull = new TableColumn(this.attributeTable,
				SWT.CENTER);
		columnNotNull.setWidth(NOT_NULL_WIDTH);
		columnNotNull.setText(ResourceString
				.getResourceString("label.not.null"));
		new CenteredContentCellPaint(this.attributeTable, 10);

		TableColumn columnUnique = new TableColumn(this.attributeTable,
				SWT.CENTER);
		columnUnique.setWidth(UNIQUE_KEY_WIDTH);
		columnUnique.setText(ResourceString
				.getResourceString("label.unique.key"));
		new CenteredContentCellPaint(this.attributeTable, 11);

		this.tableEditor = new TableEditor(this.attributeTable);
		this.tableEditor.grabHorizontal = true;

		ListenerAppender.addTableEditListener(this.attributeTable,
				this.tableEditor, this);
	}

	private Combo createTypeCombo(NormalColumn targetColumn) {
		GridData gridData = new GridData();
		gridData.widthHint = 100;

		final Combo typeCombo = new Combo(this.attributeTable, SWT.READ_ONLY);
		initializeTypeCombo(typeCombo);
		typeCombo.setLayoutData(gridData);

		typeCombo.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent event) {
				validate();
			}

		});

		SqlType sqlType = targetColumn.getType();

		String database = this.diagram.getDatabase();

		if (sqlType != null && sqlType.getAlias(database) != null) {
			typeCombo.setText(sqlType.getAlias(database));
		}

		return typeCombo;
	}

	private void initializeTypeCombo(Combo combo) {
		combo.setVisibleItemCount(20);

		combo.add("");

		String database = this.diagram.getDatabase();

		for (String alias : SqlType.getAliasList(database)) {
			combo.add(alias);
		}
	}

	protected Combo createWordCombo(NormalColumn targetColumn) {
		GridData gridData = new GridData();
		gridData.widthHint = 100;

		final Combo wordCombo = new Combo(this.attributeTable, SWT.READ_ONLY);
		initializeWordCombo(wordCombo);
		wordCombo.setLayoutData(gridData);
		this.setWordValue(wordCombo, targetColumn);

		return wordCombo;
	}

	private void initializeWordCombo(Combo combo) {
		combo.setVisibleItemCount(20);

		combo.add("");

		this.wordList = this.diagramContents.getDictionary().getWordList();
		Collections.sort(this.wordList);

		for (Word word : this.wordList) {
			combo.add(Format.null2blank(word.getLogicalName()));
		}
	}

	private void setWordValue(Combo combo, NormalColumn targetColumn) {
		Word word = targetColumn.getWord();
		while (word instanceof CopyWord) {
			word = ((CopyWord) word).getOriginal();
		}

		if (word != null) {
			int index = wordList.indexOf(word);

			combo.select(index + 1);
		}
	}

	@Override
	protected void perfomeOK() throws InputException {
	}

	@Override
	protected void setData() {
		for (NodeElement nodeElement : this.diagramContents.getContents()) {
			if (nodeElement instanceof ERTable) {
				ERTable table = (ERTable) nodeElement;

				for (Column column : table.getColumns()) {
					TableItem tableItem = new TableItem(this.attributeTable,
							SWT.NONE);
					this.column2TableItem(table, column, tableItem);
					this.columnList.add(column);
				}
			}
		}
	}

	private void column2TableItem(ERTable table, Column column,
			TableItem tableItem) {
		// this.disposeCheckBox(column);

		if (table != null) {
			tableItem.setText(2, Format.null2blank(table.getLogicalName()));
			tableItem.setText(3, Format.null2blank(table.getPhysicalName()));
		}

		if (column instanceof NormalColumn) {

			NormalColumn normalColumn = (NormalColumn) column;

			// if (normalColumn.isForeignKey()) {
			// tableItem.setBackground(Resources.SELECTED_FOREIGNKEY_COLUMN);
			//
			// } else {
			// tableItem.setBackground(ColorConstants.white);
			// }

			Color keyColor = ColorConstants.black;
			Color color = ColorConstants.black;

			if (normalColumn.isPrimaryKey() && normalColumn.isForeignKey()) {
				keyColor = ColorConstants.blue;
				color = ColorConstants.gray;

			} else if (normalColumn.isPrimaryKey()) {
				keyColor = ColorConstants.red;

			} else if (normalColumn.isForeignKey()) {
				keyColor = ColorConstants.darkGreen;
				color = ColorConstants.gray;
			}

			tableItem.setForeground(color);

			int colCount = 0;

			tableItem.setForeground(colCount, keyColor);
			tableItem.setText(colCount, Format.null2blank(normalColumn
					.getLogicalName()));

			colCount++;
			tableItem.setForeground(colCount, keyColor);
			tableItem.setText(colCount, Format.null2blank(normalColumn
					.getPhysicalName()));

			colCount++;
			tableItem.setForeground(colCount, ColorConstants.gray);

			colCount++;
			tableItem.setForeground(colCount, ColorConstants.gray);

			colCount++;
			if (normalColumn.getWord() != null) {
				tableItem.setText(colCount, Format.null2blank(normalColumn
						.getWord().getLogicalName()));
			}

			colCount++;
			SqlType sqlType = normalColumn.getType();

			if (sqlType != null) {
				String database = this.diagram.getDatabase();

				if (sqlType.getAlias(database) != null) {
					tableItem.setText(colCount, sqlType.getAlias(database));
				} else {
					tableItem.setText(colCount, "");
				}

			} else {
				tableItem.setText(colCount, "");
			}

			colCount++;
			if (normalColumn.getTypeData().getLength() != null) {
				tableItem.setText(colCount, normalColumn.getTypeData()
						.getLength().toString());
			} else {
				tableItem.setText(colCount, "");
			}

			colCount++;
			if (normalColumn.getTypeData().getDecimal() != null) {
				tableItem.setText(colCount, normalColumn.getTypeData()
						.getDecimal().toString());
			} else {
				tableItem.setText(colCount, "");
			}

			colCount++;
			if (normalColumn.isPrimaryKey()) {
				tableItem.setImage(colCount, Activator
						.getImage(ImageKey.PRIMARY_KEY));
			} else {
				tableItem.setImage(colCount, null);
			}

			colCount++;
			if (normalColumn.isForeignKey()) {
				tableItem.setImage(colCount, Activator
						.getImage(ImageKey.FOREIGN_KEY));

				// CLabel imageLabel = new CLabel(this.attributeTable,
				// SWT.NONE);
				// imageLabel.setImage(Activator.getImage(ImageKey.FOREIGN_KEY));
				// imageLabel.pack();
				// TableEditor editor = new TableEditor(this.attributeTable);
				// editor.minimumWidth = imageLabel.getSize().x;
				// editor.horizontalAlignment = SWT.CENTER;
				// editor.setEditor(imageLabel, tableItem, 9);

			} else {
				tableItem.setImage(colCount, null);
			}

			colCount++;
			if (normalColumn.isNotNull()) {
				if (normalColumn.isPrimaryKey()) {
					tableItem.setImage(colCount, Activator
							.getImage(ImageKey.CHECK_GREY));
				} else {
					tableItem.setImage(colCount, Activator
							.getImage(ImageKey.CHECK));
				}

			} else {
				tableItem.setImage(colCount, null);
			}

			colCount++;
			if (normalColumn.isUniqueKey()) {

				if (table != null && normalColumn.isRefered()) {
					tableItem.setImage(colCount, Activator
							.getImage(ImageKey.CHECK_GREY));
				} else {
					tableItem.setImage(colCount, Activator
							.getImage(ImageKey.CHECK));
				}

			} else {
				tableItem.setImage(colCount, null);
			}

			// this.setTableEditor(table, normalColumn, tableItem);

		} else {
			// group column

			// tableItem.setBackground(ColorConstants.white);
			tableItem.setForeground(ColorConstants.gray);

			// tableItem.setImage(2, Activator
			// .getImage(ImageKey.COLUMN_GROUP_IMAGE));

			tableItem.setText(0, column.getName());

			tableItem.setImage(8, null);
			tableItem.setImage(9, null);
		}
	}

	// private void disposeCheckBox(Column column) {
	// TableEditor[] oldEditors = this.columnNotNullCheckMap.get(column);
	//
	// if (oldEditors != null) {
	// for (TableEditor oldEditor : oldEditors) {
	// if (oldEditor.getEditor() != null) {
	// oldEditor.getEditor().dispose();
	// oldEditor.dispose();
	// }
	// }
	//
	// this.columnNotNullCheckMap.remove(column);
	// }
	// }

	// private void setTableEditor(ERTable table, final NormalColumn
	// normalColumn,
	// TableItem tableItem) {
	//
	// TableEditor[] editors = new TableEditor[] {
	// CompositeFactory.createCheckBoxTableEditor(tableItem,
	// normalColumn.isNotNull(), 10),
	// CompositeFactory.createCheckBoxTableEditor(tableItem,
	// normalColumn.isUniqueKey(), 11) };
	//
	// final Button notNullCheckButton = (Button) editors[0].getEditor();
	// final Button uniqueCheckButton = (Button) editors[1].getEditor();
	//
	// if (normalColumn.isPrimaryKey()) {
	// notNullCheckButton.setEnabled(false);
	// }
	//
	// if (table != null) {
	// if (normalColumn.isRefered()) {
	// uniqueCheckButton.setEnabled(false);
	// }
	// }
	//
	// this.columnNotNullCheckMap.put(normalColumn, editors);
	//
	// notNullCheckButton.addSelectionListener(new SelectionAdapter() {
	//
	// /**
	// * {@inheritDoc}
	// */
	// @Override
	// public void widgetSelected(SelectionEvent e) {
	// normalColumn.setNotNull(notNullCheckButton.getSelection());
	// super.widgetSelected(e);
	// }
	// });
	//
	// uniqueCheckButton.addSelectionListener(new SelectionAdapter() {
	//
	// /**
	// * {@inheritDoc}
	// */
	// @Override
	// public void widgetSelected(SelectionEvent e) {
	// normalColumn.setUniqueKey(uniqueCheckButton.getSelection());
	// super.widgetSelected(e);
	// }
	// });
	// }

	public Control getControl(Point xy) {
		Column column = this.getColumn(xy);

		if (column instanceof NormalColumn) {
			NormalColumn targetColumn = (NormalColumn) column;

			String database = this.diagram.getDatabase();

			if (xy.x == 4) {
				if (targetColumn.isForeignKey()) {
					return null;
				}
				return this.createWordCombo(targetColumn);
			}

			if (xy.x == 0 || xy.x == 1) {
				return new Text(this.attributeTable, SWT.BORDER);
			}
			if (xy.x == 6) {
				if (targetColumn.isForeignKey()) {
					return null;
				}
				if (targetColumn.getType() != null
						&& targetColumn.getType().isNeedLength(database)) {
					return new Text(this.attributeTable, SWT.BORDER | SWT.RIGHT);
				}
			}
			if (xy.x == 7) {
				if (targetColumn.isForeignKey()) {
					return null;
				}
				if (targetColumn.getType() != null
						&& targetColumn.getType().isNeedDecimal(database)) {
					return new Text(this.attributeTable, SWT.BORDER | SWT.RIGHT);
				}
			}

			if (xy.x == 5) {
				if (targetColumn.isForeignKey()) {
					return null;
				}
				return this.createTypeCombo(targetColumn);
			}
		}

		return null;
	}

	private Column getColumn(Point xy) {
		return this.columnList.get(xy.y);
	}

	public void setData(Point xy, Control control) {
		this.errorMessage = null;

		Column column = this.getColumn(xy);

		if (column instanceof NormalColumn) {
			NormalColumn targetColumn = (NormalColumn) column;

			String database = this.diagram.getDatabase();

			Word word = targetColumn.getWord();

			if (xy.x == 4) {
				if (targetColumn.isForeignKey()) {
					return;
				}

				int index = ((Combo) control).getSelectionIndex();

				Dictionary dictionary = this.diagramContents.getDictionary();
				dictionary.remove(targetColumn);

				if (index == 0) {
					word = new Word(word);
					word.setLogicalName("");

				} else {
					Word selectedWord = this.wordList.get(index - 1);
					if (word != selectedWord) {
						word = selectedWord;
					}
				}

				targetColumn.setWord(word);
				dictionary.add(targetColumn);

				this.resetNormalColumn(targetColumn);

			} else {
				if (xy.x == 0) {
					String text = ((Text) control).getText();

					if (targetColumn.isForeignKey()) {
						targetColumn.setForeignKeyLogicalName(text);

					} else {
						word.setLogicalName(text);
					}

				} else if (xy.x == 1) {
					String text = ((Text) control).getText();

					if (!Check.isAlphabet(text)) {
						if (this.diagram.getDiagramContents().getSettings()
								.isValidatePhysicalName()) {
							this.errorMessage = "error.column.physical.name.not.alphabet";
							return;
						}
					}

					if (targetColumn.isForeignKey()) {
						targetColumn.setForeignKeyPhysicalName(text);

					} else {

						word.setPhysicalName(text);
					}

				} else if (xy.x == 5) {
					if (targetColumn.isForeignKey()) {
						return;
					}

					SqlType selectedType = SqlType.valueOf(database,
							((Combo) control).getText());
					word.setType(selectedType, word.getTypeData(), database);

				} else if (xy.x == 6) {
					if (targetColumn.isForeignKey()) {
						return;
					}

					String text = ((Text) control).getText().trim();

					try {
						if (!text.equals("")) {
							int len = Integer.parseInt(text);
							if (len < 0) {
								this.errorMessage = "error.column.length.zero";
								return;
							}

							TypeData oldTypeData = word.getTypeData();
							TypeData newTypeData = new TypeData(Integer
									.parseInt(((Text) control).getText()),
									oldTypeData.getDecimal(), oldTypeData
											.isArray(), oldTypeData
											.getArrayDimension(), oldTypeData
											.isUnsigned(), oldTypeData
											.getArgs());

							word.setType(word.getType(), newTypeData, database);
						}

					} catch (NumberFormatException e) {
						this.errorMessage = "error.column.length.degit";
						return;
					}

				} else if (xy.x == 7) {
					if (targetColumn.isForeignKey()) {
						return;
					}

					String text = ((Text) control).getText().trim();

					try {
						if (!text.equals("")) {
							int decimal = Integer.parseInt(text);
							if (decimal < 0) {
								this.errorMessage = "error.column.decimal.zero";
								return;
							}

							TypeData oldTypeData = word.getTypeData();
							TypeData newTypeData = new TypeData(oldTypeData
									.getLength(), decimal, oldTypeData
									.isArray(),
									oldTypeData.getArrayDimension(),
									oldTypeData.isUnsigned(), oldTypeData
											.getArgs());

							word.setType(word.getType(), newTypeData, database);
						}

					} catch (NumberFormatException e) {
						this.errorMessage = "error.column.decimal.degit";
						return;
					}
				}

				this.resetRowUse(word, targetColumn);
			}
		}

		return;
	}

	private void resetRowUse(Word word, NormalColumn targetColumn) {
		if (targetColumn.isForeignKey()) {
			this.resetNormalColumn(targetColumn);

		} else {
			for (int i = 0; i < this.columnList.size(); i++) {
				Column column = this.columnList.get(i);
				if (column instanceof NormalColumn) {
					NormalColumn normalColumn = (NormalColumn) column;
					if (word.equals(normalColumn.getWord())) {
						this.resetNormalColumn(normalColumn);
					}
				}
			}
		}
	}

	private void resetNormalColumn(NormalColumn normalColumn) {
		for (int i = 0; i < this.columnList.size(); i++) {
			if (this.columnList.get(i) == normalColumn) {
				TableItem tableItem = this.attributeTable.getItem(i);
				this.column2TableItem(null, normalColumn, tableItem);
				break;
			}
		}

		List<NormalColumn> foreignKeyList = normalColumn.getForeignKeyList();

		for (NormalColumn foreignKey : foreignKeyList) {
			this.resetNormalColumn(foreignKey);
		}
	}

	/**
	 * diagramContents ‚ðŽæ“¾‚µ‚Ü‚·.
	 * 
	 * @return diagramContents
	 */
	public DiagramContents getDiagramContents() {
		return diagramContents;
	}

	public void onDoubleClicked(Point xy) {
		Column column = getColumn(xy);

		if (column instanceof NormalColumn) {
			NormalColumn normalColumn = (NormalColumn) column;

			if (normalColumn.isPrimaryKey()) {
				return;
			}

			if (normalColumn.isRefered()) {
				return;
			}

			if (xy.x == 10) {
				normalColumn.setNotNull(!normalColumn.isNotNull());
				resetNormalColumn(normalColumn);

			} else if (xy.x == 11) {
				normalColumn.setUniqueKey(!normalColumn.isUniqueKey());
				resetNormalColumn(normalColumn);
			}
		}
	}
}
