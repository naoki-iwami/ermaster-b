package org.insightech.er.editor.view.dialog.element.table.tab;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.common.widgets.ValidatableTabWrapper;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.util.Format;

public class DescriptionTabWrapper extends ValidatableTabWrapper {

	private ERTable copyData;

	private Text descriptionText;

	public DescriptionTabWrapper(AbstractDialog dialog, TabFolder parent,
			int style, ERTable copyData) {
		super(dialog, parent, style, "label.table.description");

		this.copyData = copyData;

		this.init();
	}

	@Override
	public void initComposite() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		this.setLayout(gridLayout);

		this.descriptionText = CompositeFactory.createTextArea(null, this,
				"label.table.description", -1, 400, 1, true);

		this.descriptionText.setText(Format.null2blank(copyData
				.getDescription()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validatePage() throws InputException {
		String text = descriptionText.getText().trim();
		this.copyData.setDescription(text);
	}

	@Override
	public void setInitFocus() {
		this.descriptionText.setFocus();
	}

	@Override
	public void perfomeOK() {
	}

}
