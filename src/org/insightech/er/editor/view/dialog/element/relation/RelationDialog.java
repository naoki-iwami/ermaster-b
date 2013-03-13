package org.insightech.er.editor.view.dialog.element.relation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.element.node.table.unique_key.ComplexUniqueKey;
import org.insightech.er.util.Check;
import org.insightech.er.util.Format;

public class RelationDialog extends AbstractDialog {

	private Relation relation;

	private Text nameText;

	private Text parentTableNameText;

	private Combo columnCombo;

	private Combo parentCardinalityCombo;

	private Combo childCardinalityCombo;

	private Combo onUpdateCombo;

	private Combo onDeleteCombo;

	private ColumnComboInfo columnComboInfo;

	public RelationDialog(Shell parentShell, Relation relation) {
		super(parentShell, 2);

		this.relation = relation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite composite) {
		CompositeFactory.createLabel(composite, "label.constraint.name", 2);
		this.nameText = CompositeFactory.createText(this, composite, null, 2,
				false);

		createMethodGroup(composite);

		int size = createParentGroup(composite);
		createChildGroup(composite, size);
	}

	private void createMethodGroup(Composite composite) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;

		Group group = new Group(composite, SWT.NONE);
		group.setLayoutData(gridData);
		group.setLayout(gridLayout);
		group.setText(ResourceString
				.getResourceString("label.reference.operation"));

		Label label1 = new Label(group, SWT.NONE);
		label1.setText("ON UPDATE");
		createOnUpdateCombo(group);

		Label label2 = new Label(group, SWT.NONE);
		label2.setText("ON DELETE");
		createOnDeleteCombo(group);
	}

	private void createOnUpdateCombo(Group group) {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		onUpdateCombo = new Combo(group, SWT.NONE);
		onUpdateCombo.setLayoutData(gridData);

		onUpdateCombo.add("RESTRICT");
		onUpdateCombo.add("CASCADE");
		onUpdateCombo.add("NO ACTION");
		onUpdateCombo.add("SET NULL");
		onUpdateCombo.add("SET DEFAULT");
	}

	private void createOnDeleteCombo(Group group) {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		onDeleteCombo = new Combo(group, SWT.NONE);
		onDeleteCombo.setLayoutData(gridData);

		onDeleteCombo.add("RESTRICT");
		onDeleteCombo.add("CASCADE");
		onDeleteCombo.add("NO ACTION");
		onDeleteCombo.add("SET NULL");
		onDeleteCombo.add("SET DEFAULT");
	}

	private int createParentGroup(Composite composite) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 10;
		gridLayout.marginHeight = 10;

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		Group group = new Group(composite, SWT.NONE);
		group.setLayoutData(gridData);
		group.setLayout(gridLayout);
		group.setText(ResourceString.getResourceString("label.parent"));

		Composite upperComposite = new Composite(group, SWT.NONE);
		upperComposite.setLayoutData(gridData);
		upperComposite.setLayout(gridLayout);

		Label label1 = new Label(upperComposite, SWT.NONE);
		label1.setText(ResourceString
				.getResourceString("label.reference.table"));
		parentTableNameText = new Text(upperComposite, SWT.BORDER
				| SWT.READ_ONLY);
		parentTableNameText.setLayoutData(gridData);

		Label label2 = new Label(upperComposite, SWT.NONE);
		label2.setText(ResourceString
				.getResourceString("label.reference.column"));
		this.createColumnCombo(upperComposite);

		this.createParentMandatoryGroup(group);

		upperComposite.pack();

		return upperComposite.getSize().y;
	}

	/**
	 * This method initializes group1
	 * 
	 */
	private void createChildGroup(Composite composite, int size) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 10;
		gridLayout.verticalSpacing = 10;

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		Group group = new Group(composite, SWT.NONE);
		group.setLayoutData(gridData);
		group.setLayout(gridLayout);

		group.setText(ResourceString.getResourceString("label.child"));

		Label filler = new Label(group, SWT.NONE);
		filler.setText("");
		GridData fillerGridData = new GridData();
		fillerGridData.heightHint = size;
		filler.setLayoutData(fillerGridData);

		this.createChildMandatoryGroup(group);
	}

	private void createColumnCombo(Composite parent) {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		this.columnCombo = new Combo(parent, SWT.READ_ONLY);
		this.columnCombo.setLayoutData(gridData);

		this.columnCombo.setVisibleItemCount(20);

		this.columnComboInfo = setReferencedColumnComboData(this.columnCombo,
				(ERTable) relation.getSourceTableView());
	}

	public static ColumnComboInfo setReferencedColumnComboData(
			Combo columnCombo, ERTable table) {
		ColumnComboInfo info = new ColumnComboInfo();

		int primaryKeySize = table.getPrimaryKeySize();

		if (primaryKeySize != 0) {
			columnCombo.add("PRIMARY KEY");
			info.complexUniqueKeyStartIndex = 1;
			info.candidatePK = true;

		} else {
			info.complexUniqueKeyStartIndex = 0;
			info.candidatePK = false;
		}

		for (ComplexUniqueKey complexUniqueKey : table
				.getComplexUniqueKeyList()) {
			columnCombo.add(complexUniqueKey.getLabel());
		}

		info.columnStartIndex = info.complexUniqueKeyStartIndex
				+ table.getComplexUniqueKeyList().size();

		for (NormalColumn column : table.getNormalColumns()) {
			if (column.isUniqueKey()) {
				columnCombo.add(column.getLogicalName());
				info.candidateColumns.add(column);
			}
		}

		return info;
	}

	private void createParentMandatoryGroup(Group parent) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 10;

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		Group group = new Group(parent, SWT.NONE);
		group.setLayout(gridLayout);
		group.setLayoutData(gridData);
		group.setText(ResourceString.getResourceString("label.mandatory"));

		parentCardinalityCombo = new Combo(group, SWT.NONE);
		parentCardinalityCombo.setLayoutData(gridData);

		parentCardinalityCombo.setVisibleItemCount(5);

		parentCardinalityCombo.add("1");
		parentCardinalityCombo.add("0..1");
	}

	private void createChildMandatoryGroup(Group parent) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 10;

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		Group group = new Group(parent, SWT.NONE);
		group.setLayout(gridLayout);
		group.setLayoutData(gridData);
		group.setText(ResourceString.getResourceString("label.mandatory"));

		childCardinalityCombo = new Combo(group, SWT.NONE);
		childCardinalityCombo.setLayoutData(gridData);

		childCardinalityCombo.setVisibleItemCount(5);

		childCardinalityCombo.add("1..n");
		childCardinalityCombo.add("0..n");
		childCardinalityCombo.add("1");
		childCardinalityCombo.add("0..1");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setData() {
		ERTable sourceTable = (ERTable) this.relation.getSourceTableView();

		this.nameText.setText(Format.null2blank(this.relation.getName()));

		if (this.relation.getOnUpdateAction() != null) {
			this.onUpdateCombo.setText(this.relation.getOnUpdateAction());
		}
		if (this.relation.getOnDeleteAction() != null) {
			this.onDeleteCombo.setText(this.relation.getOnDeleteAction());
		}
		if (!Check.isEmpty(this.relation.getParentCardinality())) {
			this.parentCardinalityCombo.setText(this.relation
					.getParentCardinality());
		} else {
			this.parentCardinalityCombo.select(0);
		}
		if (!Check.isEmpty(this.relation.getChildCardinality())) {
			this.childCardinalityCombo.setText(this.relation
					.getChildCardinality());
		} else {
			this.childCardinalityCombo.select(0);
		}

		if (this.relation.isReferenceForPK()) {
			this.columnCombo.select(0);

		} else if (this.relation.getReferencedComplexUniqueKey() != null) {
			for (int i = 0; i < sourceTable.getComplexUniqueKeyList().size(); i++) {
				if (sourceTable.getComplexUniqueKeyList().get(i) == this.relation
						.getReferencedComplexUniqueKey()) {
					this.columnCombo.select(i
							+ this.columnComboInfo.complexUniqueKeyStartIndex);
					break;
				}
			}

		} else {
			for (int i = 0; i < this.columnComboInfo.candidateColumns.size(); i++) {
				if (this.columnComboInfo.candidateColumns.get(i) == this.relation
						.getReferencedColumn()) {
					this.columnCombo.select(i
							+ this.columnComboInfo.columnStartIndex);
					break;
				}
			}
		}

		if (this.relation.isReferedStrictly()) {
			this.columnCombo.setEnabled(false);
		}

		this.parentTableNameText.setText(this.relation.getSourceTableView()
				.getLogicalName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void perfomeOK() {
		this.relation.setName(this.nameText.getText());
		this.relation.setOnDeleteAction(this.onDeleteCombo.getText());
		this.relation.setOnUpdateAction(this.onUpdateCombo.getText());
		this.relation.setChildCardinality(this.childCardinalityCombo.getText());
		this.relation.setParentCardinality(this.parentCardinalityCombo
				.getText());

		int index = this.columnCombo.getSelectionIndex();

		if (index < this.columnComboInfo.complexUniqueKeyStartIndex) {
			this.relation.setReferenceForPK(true);
			this.relation.setReferencedComplexUniqueKey(null);
			this.relation.setReferencedColumn(null);

		} else if (index < this.columnComboInfo.columnStartIndex) {
			ComplexUniqueKey complexUniqueKey = ((ERTable) this.relation
					.getSourceTableView()).getComplexUniqueKeyList().get(
					index - this.columnComboInfo.complexUniqueKeyStartIndex);

			this.relation.setReferenceForPK(false);
			this.relation.setReferencedComplexUniqueKey(complexUniqueKey);
			this.relation.setReferencedColumn(null);

		} else {
			NormalColumn sourceColumn = this.columnComboInfo.candidateColumns
					.get(index - this.columnComboInfo.columnStartIndex);

			this.relation.setReferenceForPK(false);
			this.relation.setReferencedComplexUniqueKey(null);
			this.relation.setReferencedColumn(sourceColumn);
		}
	}

	@Override
	protected String getErrorMessage() {
		String text = nameText.getText().trim();
		if (!Check.isAlphabet(text)) {
			return "error.constraint.name.not.alphabet";
		}

		return null;
	}

	@Override
	protected String getTitle() {
		return "dialog.title.relation";
	}

	public static class ColumnComboInfo {
		public List<NormalColumn> candidateColumns;

		public int complexUniqueKeyStartIndex;

		public int columnStartIndex;

		public boolean candidatePK;

		public ColumnComboInfo() {
			this.candidateColumns = new ArrayList<NormalColumn>();
		}

	}
}
