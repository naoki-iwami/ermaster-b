package org.insightech.er.editor.view.dialog.element.view.tab;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.ValidatableTabWrapper;
import org.insightech.er.editor.model.diagram_contents.element.node.view.View;
import org.insightech.er.editor.model.diagram_contents.element.node.view.properties.ViewProperties;

public class AdvancedTabWrapper extends ValidatableTabWrapper {

	private View view;

	private AdvancedComposite composite;

	public AdvancedTabWrapper(AbstractDialog dialog, TabFolder parent,
			int style, View view) {
		super(dialog, parent, style, "label.advanced.settings");

		this.view = view;

		this.init();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validatePage() throws InputException {
		this.composite.validate();
	}

	@Override
	public void initComposite() {
		this.setLayout(new GridLayout());
		this.composite = new AdvancedComposite(this);
		this.composite.initialize((ViewProperties) this.view
				.getTableViewProperties(), this.view.getDiagram());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInitFocus() {
		this.composite.setInitFocus();
	}

	@Override
	public void perfomeOK() {
	}
}
