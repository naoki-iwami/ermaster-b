package org.insightech.er.editor.view.dialog.element.view.tab;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.ValidatableTabWrapper;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.view.dialog.element.view.ViewDialog;
import org.insightech.er.util.Format;

public class SqlTabWrapper extends ValidatableTabWrapper {

	private View copyData;

	private Text sqlText;

	private ViewDialog viewDialog;

	public SqlTabWrapper(ViewDialog viewDialog, TabFolder parent, int style,
			View copyData) {
		super(viewDialog, parent, style, "label.sql");

		this.viewDialog = viewDialog;
		this.copyData = copyData;

		this.init();
	}

	@Override
	public void initComposite() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		this.setLayout(gridLayout);

		this.sqlText = CompositeFactory.createTextArea(this.viewDialog, this,
				"label.sql", 400, 400, 1, true);

		this.sqlText.setText(Format.null2blank(copyData.getSql()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validatePage() throws InputException {
		String text = sqlText.getText().trim();

		if (text.equals("")) {
			throw new InputException("error.view.sql.empty");
		}

		this.copyData.setSql(text);
	}

	@Override
	public void setInitFocus() {
		this.sqlText.setFocus();
	}

	@Override
	public void perfomeOK() {
	}

}
