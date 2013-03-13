package org.insightech.er.db.impl.postgres.tablespace;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;
import org.insightech.er.util.Format;

public class PostgresTablespaceDialog extends TablespaceDialog {

	private Text location;

	private Text owner;

	@Override
	protected void initialize(Composite composite) {
		super.initialize(composite);

		this.location = CompositeFactory.createText(this, composite,
				"label.tablespace.location", false);
		CompositeFactory.filler(composite, 1);
		CompositeFactory.createExampleLabel(composite,
				"label.tablespace.data.file.example");
		this.owner = CompositeFactory.createText(this, composite,
				"label.tablespace.owner", false);
	}

	@Override
	protected TablespaceProperties setTablespaceProperties() {
		PostgresTablespaceProperties properties = new PostgresTablespaceProperties();

		properties.setLocation(this.location.getText().trim());
		properties.setOwner(this.owner.getText().trim());

		return properties;
	}

	@Override
	protected void setData(TablespaceProperties tablespaceProperties) {
		if (tablespaceProperties instanceof PostgresTablespaceProperties) {
			PostgresTablespaceProperties properties = (PostgresTablespaceProperties) tablespaceProperties;

			this.location.setText(Format.toString(properties.getLocation()));
			this.owner.setText(Format.toString(properties.getOwner()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getErrorMessage() {
		String errorMessage = super.getErrorMessage();
		if (errorMessage != null) {
			return errorMessage;
		}

		String text = this.location.getText().trim();
		if (text.equals("")) {
			return "error.tablespace.location.empty";
		}

		return null;
	}
}
