package org.insightech.er.editor.view.dialog.category;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public class CategoryNameChangeDialog extends AbstractDialog {

	private Text categoryNameText = null;

	private Category targetCategory;

	private String categoryName;

	public CategoryNameChangeDialog(Shell parentShell, Category category) {
		super(parentShell, 2);
		this.targetCategory = category;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite composite) {
		this.categoryNameText = CompositeFactory.createText(this, composite,
				"label.category.name", true);
	}

	@Override
	protected String getTitle() {
		return "dialog.title.change.category.name";
	}

	@Override
	protected void perfomeOK() throws InputException {
	}

	@Override
	protected void setData() {
		this.categoryNameText.setText(this.targetCategory.getName());
	}

	@Override
	protected String getErrorMessage() {
		String text = categoryNameText.getText().trim();

		if ("".equals(text)) {
			return "error.category.name.empty";
		}

		this.categoryName = text;

		return null;
	}

	public String getCategoryName() {
		return this.categoryName;
	}
}
