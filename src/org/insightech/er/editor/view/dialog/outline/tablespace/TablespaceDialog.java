package org.insightech.er.editor.view.dialog.outline.tablespace;

import java.util.List;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Resources;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.Tablespace;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.insightech.er.editor.model.settings.Environment;
import org.insightech.er.util.Check;

public abstract class TablespaceDialog extends AbstractDialog {

	private Combo environmentCombo;

	private Text nameText;

	private Tablespace result;

	protected ERDiagram diagram;

	private Environment currentEnvironment;

	protected static final int NUM_TEXT_WIDTH = 60;

	public TablespaceDialog() {
		this(2);
	}

	public TablespaceDialog(int numColumns) {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				numColumns);
	}

	public void init(Tablespace tablespace, ERDiagram diagram) {
		if (tablespace == null) {
			this.result = new Tablespace();

		} else {
			this.result = tablespace.clone();
		}

		this.diagram = diagram;
	}

	@Override
	protected void initialize(Composite composite) {
		this.environmentCombo = CompositeFactory.createReadOnlyCombo(this,
				composite, "label.tablespace.environment",
				this.getNumColumns() - 1, -1);
		this.nameText = CompositeFactory.createText(this, composite,
				"label.tablespace.name", this.getNumColumns() - 1,
				Resources.DESCRIPTION_WIDTH, false);
	}

	@Override
	protected String getErrorMessage() {
		String text = this.nameText.getText().trim();
		if (text.equals("")) {
			return "error.tablespace.name.empty";
		}

		if (!Check.isAlphabet(text)) {
			return "error.tablespace.name.not.alphabet";
		}

		return null;
	}

	@Override
	protected String getTitle() {
		return "dialog.title.tablespace";
	}

	@Override
	protected void perfomeOK() {
		this.result.setName(this.nameText.getText().trim());

		TablespaceProperties tablespaceProperties = this
				.setTablespaceProperties();

		this.result
				.putProperties(this.currentEnvironment, tablespaceProperties);
	}

	protected abstract TablespaceProperties setTablespaceProperties();

	@Override
	protected void setData() {
		List<Environment> environmentList = this.diagram.getDiagramContents()
				.getSettings().getEnvironmentSetting().getEnvironments();

		for (Environment environment : environmentList) {
			this.environmentCombo.add(environment.getName());
		}

		this.environmentCombo.select(0);
		this.currentEnvironment = environmentList.get(0);

		if (this.result.getName() != null) {
			this.nameText.setText(this.result.getName());
		}

		this.setPropertiesData();
	}

	private void setPropertiesData() {
		this.currentEnvironment = this.getSelectedEnvironment();

		TablespaceProperties tablespaceProperties = this.result.getProperties(
				this.currentEnvironment, this.diagram);

		this.setData(tablespaceProperties);
	}

	protected abstract void setData(TablespaceProperties tablespaceProperties);

	public Tablespace getResult() {
		return result;
	}

	protected Environment getSelectedEnvironment() {
		int index = this.environmentCombo.getSelectionIndex();

		List<Environment> environmentList = this.diagram.getDiagramContents()
				.getSettings().getEnvironmentSetting().getEnvironments();

		return environmentList.get(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addListener() {
		this.environmentCombo.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				perfomeOK();
				setPropertiesData();
			}

		});
	}

}
