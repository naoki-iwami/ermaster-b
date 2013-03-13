package org.insightech.er.common.widgets.table;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.DefaultCellEditor;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.insightech.er.ResourceString;

public class CustomCellEditor extends DefaultCellEditor implements
		TableCellEditor {

	private static final long serialVersionUID = 1715411332743091739L;

	public CustomCellEditor(final JTable table) {
		super(new JTextField());

		final JTextField component = (JTextField) getComponent();
		component.setName("Table.editor");

		component.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
					if (e.getKeyCode() == ';') {
						SimpleDateFormat format = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss.SSS");
						component.setText(format.format(new Date()));

					} else if (e.getKeyCode() == 'v' || e.getKeyCode() == 'V') {
						component.paste();

					} else if (e.getKeyCode() == 'c' || e.getKeyCode() == 'C') {
						component.copy();

					} else if (e.getKeyCode() == 'x' || e.getKeyCode() == 'X') {
						component.cut();

					}
				}

				super.keyPressed(e);
			}

		});

		component.setComponentPopupMenu(new TextFieldPopupMenu());
	}

	// public Component getTableCellEditorComponent(JTable table, Object value,
	// boolean isSelected, int rowIndex, int vColIndex) {
	// if (value == null) {
	// value = "";
	// }
	//
	// this.component.setText(String.valueOf(value));
	//
	// return this.component;
	// }
	//
	// public Object getCellEditorValue() {
	// return this.component.getText();
	// }

	private static class TextFieldPopupMenu extends JPopupMenu {

		private static final long serialVersionUID = 5180658114688605208L;

		private TextFieldPopupMenu() {
			FontData fontData = Display.getCurrent().getSystemFont()
					.getFontData()[0];

			Font font = new Font(fontData.getName(), Font.PLAIN, 12);

			JMenuItem cutMenuItem = this.add(new CutAction());
			cutMenuItem.setFont(font);

			JMenuItem copyMenuItem = this.add(new CopyAction());
			copyMenuItem.setFont(font);

			JMenuItem pasteMenuItem = this.add(new PasteAction());
			pasteMenuItem.setFont(font);
		}

	}

	private static class CutAction extends TextAction {

		private static final long serialVersionUID = 9018455792592465382L;

		public CutAction() {
			super(ResourceString.getResourceString("action.title.cut"));
		}

		public void actionPerformed(ActionEvent e) {
			JTextComponent target = getTextComponent(e);
			if (target != null) {
				target.cut();
			}
		}
	}

	private static class CopyAction extends TextAction {

		private static final long serialVersionUID = 9018455792592465382L;

		public CopyAction() {
			super(ResourceString.getResourceString("action.title.copy"));
		}

		public void actionPerformed(ActionEvent e) {
			JTextComponent target = getTextComponent(e);
			if (target != null) {
				target.copy();
			}
		}
	}

	private static class PasteAction extends TextAction {

		private static final long serialVersionUID = 9018455792592465382L;

		public PasteAction() {
			super(ResourceString.getResourceString("action.title.paste"));
		}

		public void actionPerformed(ActionEvent e) {
			JTextComponent target = getTextComponent(e);
			if (target != null) {
				target.paste();
			}
		}
	}
}
