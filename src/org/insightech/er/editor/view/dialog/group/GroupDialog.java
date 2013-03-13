package org.insightech.er.editor.view.dialog.group;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.CopyGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.view.dialog.common.ERTableComposite;
import org.insightech.er.editor.view.dialog.common.ERTableCompositeHolder;
import org.insightech.er.editor.view.dialog.word.column.real.GroupColumnDialog;

public class GroupDialog extends AbstractDialog implements
		ERTableCompositeHolder {

	private Text groupNameText;

	private List<CopyGroup> copyColumnGroups;

	private int editTargetIndex = -1;

	private CopyGroup copyData;

	private ERDiagram diagram;

	public GroupDialog(Shell parentShell, GroupSet columnGroups,
			ERDiagram diagram, int editTargetIndex) {
		super(parentShell, 2);

		this.copyColumnGroups = new ArrayList<CopyGroup>();

		for (ColumnGroup columnGroup : columnGroups) {
			this.copyColumnGroups.add(new CopyGroup(columnGroup));
		}

		this.diagram = diagram;

		this.editTargetIndex = editTargetIndex;

		if (this.editTargetIndex != -1) {
			this.copyData = copyColumnGroups.get(editTargetIndex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void initialize(Composite composite) {
		this.groupNameText = CompositeFactory.createText(this, composite,
				"label.group.name", 1, 200, true);

		GroupColumnDialog columnDialog = new GroupColumnDialog(PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getShell(), diagram);

		new ERTableComposite(this, composite, this.diagram, null,
				(List) this.copyData.getColumns(), columnDialog, this, 2, true, true);

		this.groupNameText.setFocus();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getErrorMessage() {
		if (this.groupNameText.getEnabled()) {
			String text = this.groupNameText.getText().trim();

			if (text.equals("")) {
				return "error.group.name.empty";
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void perfomeOK() {
	}

	@Override
	protected String getTitle() {
		return "dialog.title.group";
	}

	@Override
	protected void setData() {
		if (this.editTargetIndex != -1) {
			String text = this.copyData.getGroupName();

			if (text == null) {
				text = "";
			}

			this.groupNameText.setText(text);
		}
	}

	public List<CopyGroup> getCopyColumnGroups() {
		return copyColumnGroups;
	}

	public void selectGroup(ColumnGroup selectedColumn) {
		// do nothing
	}
}
