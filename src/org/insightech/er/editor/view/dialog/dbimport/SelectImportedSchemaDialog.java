package org.insightech.er.editor.view.dialog.dbimport;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.StringObjectModel;

public class SelectImportedSchemaDialog extends AbstractDialog {

	private ContainerCheckedTreeViewer viewer;

	private List<String> schemaList;

	private List<String> selectedSchemaList;

	private List<String> resultSelectedSchemas;

	private String importDB;

	public SelectImportedSchemaDialog(Shell parentShell, ERDiagram diagram,
			String importDB, List<String> schemaList,
			List<String> selectedSchemaList) {
		super(parentShell);

		this.schemaList = schemaList;
		this.selectedSchemaList = selectedSchemaList;
		this.resultSelectedSchemas = new ArrayList<String>();
		this.importDB = importDB;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite composite) {
		this.createObjectListComposite(composite);

		this.setListener();
	}

	private void createObjectListComposite(Composite parent) {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.verticalSpacing = 20;

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(gridLayout);
		composite.setLayoutData(gridData);

		this.createAllSchemaGroup(composite);
	}

	private void createAllSchemaGroup(Composite composite) {
		GridData gridData = new GridData();
		gridData.heightHint = 300;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;

		this.viewer = new ContainerCheckedTreeViewer(composite, SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		Tree tree = this.viewer.getTree();
		tree.setLayoutData(gridData);

		this.viewer.setContentProvider(new TreeNodeContentProvider());
		this.viewer.setLabelProvider(new ViewLabelProvider());
	}

	private void setListener() {
		this.viewer.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				validate();
			}

		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		this.createButton(parent, IDialogConstants.BACK_ID,
				IDialogConstants.BACK_LABEL, false);
		this.createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.NEXT_LABEL, true);
		this.createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void perfomeOK() throws InputException {
		Object[] selectedNodes = this.viewer.getCheckedElements();

		this.resultSelectedSchemas.clear();

		for (int i = 0; i < selectedNodes.length; i++) {
			Object value = ((TreeNode) selectedNodes[i]).getValue();
			if (value instanceof String) {
				resultSelectedSchemas.add((String) value);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getErrorMessage() {
		if (this.viewer.getCheckedElements().length == 0) {
			return "error.import.schema.empty";
		}

		return null;
	}

	@Override
	protected String getTitle() {
		return "dialog.title.select.import.schema";
	}

	@Override
	protected void setData() {
		List<TreeNode> treeNodeList = this.createTreeNodeList();

		TreeNode[] treeNodes = treeNodeList.toArray(new TreeNode[treeNodeList
				.size()]);
		this.viewer.setInput(treeNodes);

		List<TreeNode> checkedList = new ArrayList<TreeNode>();

		TreeNode[] schemaNodes = treeNodes[0].getChildren();

		if (this.selectedSchemaList.isEmpty()) {
			for (TreeNode schemaNode : schemaNodes) {
				if (!DBManagerFactory.getDBManager(this.importDB)
						.getSystemSchemaList().contains(
								String.valueOf(schemaNode.getValue())
										.toLowerCase())) {
					checkedList.add(schemaNode);
				}
			}

		} else {
			for (TreeNode schemaNode : schemaNodes) {
				if (this.selectedSchemaList.contains(schemaNode.getValue())) {
					checkedList.add(schemaNode);
				}
			}

		}

		this.viewer.setCheckedElements(checkedList
				.toArray(new TreeNode[checkedList.size()]));

		this.viewer.expandAll();
	}

	protected List<TreeNode> createTreeNodeList() {

		List<TreeNode> treeNodeList = new ArrayList<TreeNode>();

		TreeNode topNode = new TreeNode(new StringObjectModel(ResourceString
				.getResourceString("label.schema")));
		treeNodeList.add(topNode);

		List<TreeNode> schemaNodeList = new ArrayList<TreeNode>();

		for (String schemaName : schemaList) {
			TreeNode schemaNode = new TreeNode(schemaName);
			schemaNode.setParent(topNode);
			schemaNodeList.add(schemaNode);
		}

		topNode.setChildren(schemaNodeList.toArray(new TreeNode[schemaNodeList
				.size()]));

		return treeNodeList;
	}

	public List<String> getSelectedSchemas() {
		return this.resultSelectedSchemas;
	}

}
