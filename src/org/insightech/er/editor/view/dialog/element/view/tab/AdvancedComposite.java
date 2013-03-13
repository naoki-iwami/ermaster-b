package org.insightech.er.editor.view.dialog.element.view.tab;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.view.properties.ViewProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;

public class AdvancedComposite extends Composite {

	private Combo tableSpaceCombo;

	private Text schemaText;

	protected ViewProperties viewProperties;

	private ERDiagram diagram;

	public AdvancedComposite(Composite parent) {
		super(parent, SWT.NONE);
	}

	public final void initialize(ViewProperties viewProperties,
			ERDiagram diagram) {
		this.viewProperties = viewProperties;
		this.diagram = diagram;

		this.initComposite();
		this.setData();
	}

	protected void initComposite() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;

		this.setLayout(gridLayout);

		this.tableSpaceCombo = CompositeFactory.createReadOnlyCombo(null, this,
				"label.tablespace");
		this.schemaText = CompositeFactory.createText(null, this,
				"label.schema", 1, 120, false);

		this.initTablespaceCombo();
	}

	private void initTablespaceCombo() {
		this.tableSpaceCombo.add("");

		for (Tablespace tablespace : this.diagram.getDiagramContents()
				.getTablespaceSet()) {
			this.tableSpaceCombo.add(tablespace.getName());
		}
	}

	protected void setData() {
		Tablespace tablespace = this.viewProperties.getTableSpace();

		if (tablespace != null) {
			int index = this.diagram.getDiagramContents().getTablespaceSet()
					.getTablespaceList().indexOf(tablespace);
			this.tableSpaceCombo.select(index + 1);
		}

		if (this.viewProperties.getSchema() != null && this.schemaText != null) {
			this.schemaText.setText(this.viewProperties.getSchema());
		}
	}

	public boolean validate() {
		if (this.tableSpaceCombo != null) {
			int tablespaceIndex = this.tableSpaceCombo.getSelectionIndex();
			if (tablespaceIndex > 0) {
				Tablespace tablespace = this.diagram.getDiagramContents()
						.getTablespaceSet().getTablespaceList().get(
								tablespaceIndex - 1);
				this.viewProperties.setTableSpace(tablespace);

			} else {
				this.viewProperties.setTableSpace(null);
			}
		}

		if (this.schemaText != null) {
			this.viewProperties.setSchema(this.schemaText.getText());
		}

		return true;
	}

	public void setInitFocus() {
		this.tableSpaceCombo.setFocus();
	}
}
