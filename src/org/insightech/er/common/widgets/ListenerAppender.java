package org.insightech.er.common.widgets;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.dialog.common.EditableTable;

public class ListenerAppender {

	public static void addTextAreaListener(final Text text,
			final AbstractDialog dialog, boolean selectAll, boolean imeOn) {
		addFocusListener(text, selectAll, imeOn);
		addTraverseListener(text);
		if (dialog != null) {
			addModifyListener(text, dialog);
		}
	}

	public static void addTextListener(final Text text,
			final AbstractDialog dialog, boolean imeOn) {
		addFocusListener(text, imeOn);
		if (dialog != null) {
			addModifyListener(text, dialog);
		}
	}

	public static void addFocusListener(final Text text, final boolean imeOn) {
		addFocusListener(text, true, imeOn);
	}

	public static void addFocusListener(final Text text,
			final boolean selectAll, final boolean imeOn) {
		text.addFocusListener(new FocusAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void focusGained(FocusEvent e) {
				ERDiagram diagram = (ERDiagram) PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage()
						.getActiveEditor().getAdapter(ERDiagram.class);

				if (diagram != null) {
					if (diagram.getDiagramContents().getSettings()
							.isAutoImeChange()) {
						if (imeOn) {
							text.getShell().setImeInputMode(
									SWT.DBCS | SWT.NATIVE);

						} else {
							text.getShell().setImeInputMode(SWT.ALPHA);
						}
					}
				}

				if (selectAll) {
					text.selectAll();
				}

				super.focusGained(e);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void focusLost(FocusEvent e) {
				// text.clearSelection();
				super.focusLost(e);
			}
		});
	}

	public static void addTraverseListener(final Text textArea) {
		textArea.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_TAB_NEXT
						|| e.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
					e.doit = true;
				}
			}
		});
	}

	public static void addModifyListener(final Text text,
			final AbstractDialog dialog) {
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialog.validate();
			}

		});
	}

	public static void addComboListener(final Combo combo,
			final AbstractDialog dialog, final boolean imeOn) {
		if (dialog != null) {
			combo.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					dialog.validate();
				}
			});
		}

		combo.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				ERDiagram diagram = (ERDiagram) PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage()
						.getActiveEditor().getAdapter(ERDiagram.class);

				if (diagram != null) {
					if (diagram.getDiagramContents().getSettings()
							.isAutoImeChange()) {
						if (imeOn) {
							combo.getShell().setImeInputMode(
									SWT.DBCS | SWT.NATIVE);

						} else {
							combo.getShell().setImeInputMode(SWT.ALPHA);
						}
					}
				}
			}

			public void focusLost(FocusEvent e) {
			}
		});
	}

	public static void addCheckBoxListener(final Button button,
			final AbstractDialog dialog) {
		button.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				this.widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				dialog.validate();
			}
		});

		button.addFocusListener(new FocusAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void focusGained(FocusEvent e) {
				ERDiagram diagram = (ERDiagram) PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage()
						.getActiveEditor().getAdapter(ERDiagram.class);

				if (diagram != null) {
					if (diagram.getDiagramContents().getSettings()
							.isAutoImeChange()) {
						button.getShell().setImeInputMode(SWT.ALPHA);
					}
				}
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void focusLost(FocusEvent e) {
			}
		});
	}

	public static void addTableEditListener(final Table table,
			final TableEditor tableEditor, final EditableTable editableTable) {

		table.addMouseListener(new MouseAdapter() {

			private Point getSelectedCell(MouseEvent e) {
				int vIndex = table.getSelectionIndex();
				if (vIndex != -1) {
					TableItem item = table.getItem(vIndex);
					for (int hIndex = 0; hIndex < table.getColumnCount(); hIndex++) {
						if (item.getBounds(hIndex).contains(e.x, e.y)) {
							Point xy = new Point(hIndex, vIndex);
							xy.y = vIndex;
							xy.x = hIndex;
							return xy;
						}
					}
				}
				return null;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void mouseDown(MouseEvent event) {
				if (!editableTable.validate()) {
					return;
				}
				try {
					final Point xy = getSelectedCell(event);
					if (xy != null) {
						TableItem tableItem = table.getItem(xy.y);
						createEditor(table, tableItem, tableEditor, xy,
								editableTable);
					}
				} catch (Exception e) {
					Activator.log(e);
				}
			}

			@Override
			public void mouseDoubleClick(MouseEvent event) {
				try {
					Point xy = getSelectedCell(event);
					if (xy != null) {
						editableTable.onDoubleClicked(xy);
					}
				} catch (Exception e) {
					Activator.log(e);
				}
			}
		});
	}

	private static void createEditor(final Table table,
			final TableItem tableItem, final TableEditor tableEditor,
			final Point xy, final EditableTable editableTable) {
		final Control control = editableTable.getControl(xy);
		if (control == null) {
			return;
		}

		if (control instanceof Text) {
			Text text = (Text) control;
			text.setText(tableItem.getText(xy.x));
		}

		// フォーカスが外れたときの処理
		control.addFocusListener(new FocusAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void focusLost(FocusEvent e) {
				setEditValue(control, tableItem, xy, editableTable);
			}

		});

		// ENTERとESCが押されたときの処理
		control.addKeyListener(new KeyAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void keyReleased(KeyEvent keyevent) {
				if (keyevent.character == SWT.CR) {
					setEditValue(control, tableItem, xy, editableTable);

				} else if (keyevent.character == SWT.ESC) {
					control.dispose();
				}

			}
		});

		tableEditor.setEditor(control, tableItem, xy.x);
		control.setFocus();
		table.setSelection(new int[0]);

		if (control instanceof Text) {
			Text text = (Text) control;
			text.selectAll();
		}
	}

	private static void setEditValue(Control control, TableItem tableItem,
			Point xy, EditableTable editableTable) {
		editableTable.setData(xy, control);
		if (editableTable.validate()) {
			control.dispose();
		}
	}

	public static void addTabListener(final TabFolder tabFolder,
			final List<ValidatableTabWrapper> tabWrapperList) {
		tabFolder.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				int index = tabFolder.getSelectionIndex();

				ValidatableTabWrapper selectedTabWrapper = tabWrapperList
						.get(index);
				selectedTabWrapper.setInitFocus();
			}

		});
	}

	public static void addModifyListener(final Scale scale,
			final Spinner spinner, final int diff, final AbstractDialog dialog) {
		if (dialog != null) {
			spinner.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					int value = spinner.getSelection();
					scale.setSelection(value - diff);
					dialog.validate();
				}
			});

			scale.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					int value = scale.getSelection();
					spinner.setSelection(value + diff);
					dialog.validate();
				}

			});
		}
	}
}
