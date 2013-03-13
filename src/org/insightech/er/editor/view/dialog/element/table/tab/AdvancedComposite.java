package org.insightech.er.editor.view.dialog.element.table.tab;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.properties.TableProperties;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;

public abstract class AdvancedComposite extends Composite {

	private Combo tableSpaceCombo;

	private Text schemaText;

	protected TableProperties tableProperties;

	protected ERDiagram diagram;

	protected AbstractDialog dialog;

	protected ERTable table;

	public AdvancedComposite(Composite parent) {
		super(parent, SWT.NONE);
	}

	public final void initialize(AbstractDialog dialog,
			TableProperties tableProperties, ERDiagram diagram, ERTable table) {
		this.dialog = dialog;
		this.tableProperties = tableProperties;
		this.diagram = diagram;
		this.table = table;
		
		this.initComposite();
		this.addListener();
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

	protected void addListener() {
	}

	private void initTablespaceCombo() {
		this.tableSpaceCombo.add("");

		for (Tablespace tablespace : this.diagram.getDiagramContents()
				.getTablespaceSet()) {
			this.tableSpaceCombo.add(tablespace.getName());
		}
	}

	protected void setData() {
		Tablespace tablespace = this.tableProperties.getTableSpace();

		if (tablespace != null) {
			int index = this.diagram.getDiagramContents().getTablespaceSet()
					.getTablespaceList().indexOf(tablespace);
			this.tableSpaceCombo.select(index + 1);
		}

		if (this.tableProperties.getSchema() != null && this.schemaText != null) {
			this.schemaText.setText(this.tableProperties.getSchema());
		}
	}

	public void validate() throws InputException {
		if (this.tableSpaceCombo != null) {
			int tablespaceIndex = this.tableSpaceCombo.getSelectionIndex();
			if (tablespaceIndex > 0) {
				Tablespace tablespace = this.diagram.getDiagramContents()
						.getTablespaceSet().getTablespaceList()
						.get(tablespaceIndex - 1);
				this.tableProperties.setTableSpace(tablespace);

			} else {
				this.tableProperties.setTableSpace(null);
			}
		}

		if (this.schemaText != null) {
			this.tableProperties.setSchema(this.schemaText.getText());
		}
	}

	public void setInitFocus() {
		this.tableSpaceCombo.setFocus();
	}
}
