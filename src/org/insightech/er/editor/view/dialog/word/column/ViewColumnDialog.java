package org.insightech.er.editor.view.dialog.word.column;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;

public class ViewColumnDialog extends AbstractColumnDialog {

	public ViewColumnDialog(Shell parentShell, View view) {
		super(parentShell, view.getDiagram());
	}

	protected int getStyle(int style) {
		if (this.foreignKey) {
			style |= SWT.READ_ONLY;
		}

		return style;
	}

	@Override
	protected String getTitle() {
		return "dialog.title.column";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeComposite(Composite parent) {
		super.initializeComposite(parent);

		if (this.foreignKey) {
			this.wordCombo.setEnabled(false);
			this.typeCombo.setEnabled(false);
			this.lengthText.setEnabled(false);
			this.decimalText.setEnabled(false);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void perfomeOK() {
		super.perfomeOK();

		this.returnColumn = new NormalColumn(this.returnWord, false, false,
				false, false, null, null, null, null, null);
	}

}
