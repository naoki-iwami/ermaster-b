package org.insightech.er.editor.view.dialog.element.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.ValidatableTabWrapper;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeSet;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.view.dialog.element.table.tab.AdvancedTabWrapper;
import org.insightech.er.editor.view.dialog.element.table.tab.AttributeTabWrapper;
import org.insightech.er.editor.view.dialog.element.table.tab.ComplexUniqueKeyTabWrapper;
import org.insightech.er.editor.view.dialog.element.table.tab.ConstraintTabWrapper;
import org.insightech.er.editor.view.dialog.element.table.tab.DescriptionTabWrapper;
import org.insightech.er.editor.view.dialog.element.table.tab.IndexTabWrapper;

public class TableDialog extends AbstractDialog {

	private ERTable copyData;

	private TabFolder tabFolder;

	private EditPartViewer viewer;

	private List<ValidatableTabWrapper> tabWrapperList;

	public TableDialog(Shell parentShell, EditPartViewer viewer,
			ERTable copyData, GroupSet columnGroups) {
		super(parentShell);

		this.viewer = viewer;
		this.copyData = copyData;

		this.tabWrapperList = new ArrayList<ValidatableTabWrapper>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite composite) {
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;

		this.tabFolder = new TabFolder(composite, SWT.NONE);
		this.tabFolder.setLayoutData(gridData);

		final AttributeTabWrapper attributeTabWrapper = new AttributeTabWrapper(
				this, tabFolder, SWT.NONE, this.copyData);
		this.tabWrapperList.add(attributeTabWrapper);

		this.tabWrapperList.add(new DescriptionTabWrapper(this, tabFolder,
				SWT.NONE, this.copyData));

		final ComplexUniqueKeyTabWrapper complexUniqueKeyTabWrapper = new ComplexUniqueKeyTabWrapper(
				this, tabFolder, SWT.NONE, this.copyData);
		this.tabWrapperList.add(complexUniqueKeyTabWrapper);

		this.tabWrapperList.add(new ConstraintTabWrapper(this, tabFolder,
				SWT.NONE, this.copyData));

		final IndexTabWrapper indexTabWrapper = new IndexTabWrapper(this,
				tabFolder, SWT.NONE, this.copyData);
		this.tabWrapperList.add(indexTabWrapper);

		this.tabWrapperList.add(new AdvancedTabWrapper(this, tabFolder,
				SWT.NONE, this.copyData));

		this.tabFolder.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				complexUniqueKeyTabWrapper.restruct();
				indexTabWrapper.restruct();

				int index = tabFolder.getSelectionIndex();

				ValidatableTabWrapper selectedTabWrapper = tabWrapperList
						.get(index);
				selectedTabWrapper.setInitFocus();
			}

		});

		attributeTabWrapper.setInitFocus();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getErrorMessage() {
		try {
			for (ValidatableTabWrapper tabWrapper : this.tabWrapperList) {
				tabWrapper.validatePage();
			}

		} catch (InputException e) {
			return e.getMessage();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getTitle() {
		return "dialog.title.table";
	}

	@Override
	protected void perfomeOK() throws InputException {
		String physicalName = copyData.getPhysicalName();
		int prefixPos = physicalName.indexOf('_');
		if (prefixPos < 0) {
			return;
		}

		String  prefix = physicalName.substring(0, prefixPos + 1);
		NodeSet nodeSet = copyData.getDiagram().getDiagramContents().getContents();

		Map<MyColor, Integer> colors = new HashMap<MyColor, Integer>();
		int sum = 0;
		for (ERTable table : nodeSet.getTableSet()) {
			if (table.getPhysicalName().startsWith(prefix)) {
				MyColor mycolor = new MyColor(table.getColor());
				if (colors.containsKey(mycolor)) {
					Integer count = colors.get(mycolor);
					colors.put(mycolor, count + 1);
				} else {
					colors.put(mycolor, 1);
				}
				++sum;
			}
		}

		int[] targetColor = null;
		for (Entry<MyColor, Integer> entry : colors.entrySet()) {
			if (entry.getValue().intValue() >= sum - 1) {
				targetColor = entry.getKey().getColors();
			}
		}

		if (targetColor != null) {
			copyData.setColor(targetColor[0], targetColor[1], targetColor[2]);
		}

	}

	@Override
	protected void setData() {
	}

	public EditPartViewer getViewer() {
		return viewer;
	}

	public ERDiagram getDiagram() {
		return (ERDiagram) this.viewer.getContents().getModel();
	}

}
