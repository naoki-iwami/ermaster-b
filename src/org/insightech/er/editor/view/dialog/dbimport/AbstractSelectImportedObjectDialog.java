package org.insightech.er.editor.view.dialog.dbimport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.StringObjectModel;
import org.insightech.er.editor.model.dbimport.DBObject;
import org.insightech.er.editor.model.dbimport.DBObjectSet;

public abstract class AbstractSelectImportedObjectDialog extends AbstractDialog {

	private ContainerCheckedTreeViewer viewer;

	protected Button useCommentAsLogicalNameButton;

	private Button mergeWordButton;

	private Button mergeGroupButton;

	protected DBObjectSet dbObjectSet;

	protected boolean resultUseCommentAsLogicalName;

	private boolean resultMergeWord;

	private boolean resultMergeGroup;

	private List<DBObject> resultSelectedDbObjects;

	public AbstractSelectImportedObjectDialog(Shell parentShell,
			ERDiagram diagram, DBObjectSet dbObjectSet) {
		super(parentShell);

		this.dbObjectSet = dbObjectSet;
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

		this.createAllObjectGroup(composite);

		GridData groupGridData = new GridData();
		groupGridData.horizontalAlignment = GridData.FILL;
		groupGridData.grabExcessHorizontalSpace = true;
		groupGridData.horizontalSpan = 3;

		GridLayout groupLayout = new GridLayout();
		groupLayout.marginWidth = 15;
		groupLayout.marginHeight = 15;

		Group group = new Group(composite, SWT.NONE);
		group.setText(ResourceString.getResourceString("label.option"));
		group.setLayoutData(groupGridData);
		group.setLayout(groupLayout);

		this.initializeOptionGroup(group);
	}

	protected void initializeOptionGroup(Group group) {
		this.mergeWordButton = CompositeFactory.createCheckbox(this, group,
				"label.merge.word");
		this.mergeWordButton.setSelection(true);

		this.mergeGroupButton = CompositeFactory.createCheckbox(this, group,
				"label.merge.group");
		this.mergeGroupButton.setSelection(true);

	}

	private void createAllObjectGroup(Composite composite) {
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
				IDialogConstants.OK_LABEL, true);
		this.createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void perfomeOK() throws InputException {
		Object[] selectedNodes = this.viewer.getCheckedElements();

		this.resultSelectedDbObjects = new ArrayList<DBObject>();

		for (int i = 0; i < selectedNodes.length; i++) {
			Object value = ((TreeNode) selectedNodes[i]).getValue();

			if (value instanceof DBObject) {
				resultSelectedDbObjects.add((DBObject) value);
			}
		}

		this.resultMergeWord = this.mergeWordButton.getSelection();
		this.resultMergeGroup = this.mergeGroupButton.getSelection();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getErrorMessage() {
		if (this.viewer.getCheckedElements().length == 0) {
			return "error.import.object.empty";
		}

		return null;
	}

	@Override
	protected String getTitle() {
		return "dialog.title.select.import.object";
	}

	@Override
	protected void setData() {
		List<TreeNode> treeNodeList = createTreeNodeList();

		TreeNode[] treeNodes = treeNodeList.toArray(new TreeNode[treeNodeList
				.size()]);
		this.viewer.setInput(treeNodes);
		this.viewer.setCheckedElements(treeNodes);
		this.viewer.expandAll();
	}

	protected List<TreeNode> createTreeNodeList() {
		List<TreeNode> treeNodeList = new ArrayList<TreeNode>();

		TreeNode topNode = new TreeNode(new StringObjectModel(ResourceString
				.getResourceString("label.schema")));
		treeNodeList.add(topNode);

		List<TreeNode> schemaNodeList = new ArrayList<TreeNode>();

		for (Map.Entry<String, List<DBObject>> entry : dbObjectSet
				.getSchemaDbObjectListMap().entrySet()) {
			String schemaName = entry.getKey();
			if ("".equals(schemaName)) {
				schemaName = ResourceString.getResourceString("label.none");
			}
			TreeNode schemaNode = new TreeNode(
					new StringObjectModel(schemaName));
			schemaNode.setParent(topNode);
			schemaNodeList.add(schemaNode);

			List<DBObject> dbObjectList = entry.getValue();

			TreeNode[] objectTypeNodes = new TreeNode[DBObject.ALL_TYPES.length];

			for (int i = 0; i < DBObject.ALL_TYPES.length; i++) {
				objectTypeNodes[i] = new TreeNode(new StringObjectModel(
						ResourceString.getResourceString("label.object.type."
								+ DBObject.ALL_TYPES[i])));

				List<TreeNode> objectNodeList = new ArrayList<TreeNode>();

				for (DBObject dbObject : dbObjectList) {
					if (DBObject.ALL_TYPES[i].equals(dbObject.getType())) {
						TreeNode objectNode = new TreeNode(dbObject);
						objectNode.setParent(objectTypeNodes[i]);

						objectNodeList.add(objectNode);
					}
				}

				objectTypeNodes[i].setChildren(objectNodeList
						.toArray(new TreeNode[objectNodeList.size()]));
			}

			schemaNode.setChildren(objectTypeNodes);
		}

		topNode.setChildren(schemaNodeList.toArray(new TreeNode[schemaNodeList
				.size()]));

		topNode = createTopNode(DBObject.TYPE_TABLESPACE, this.dbObjectSet
				.getTablespaceList());
		treeNodeList.add(topNode);

		return treeNodeList;
	}

	protected TreeNode createTopNode(String objectType,
			List<DBObject> dbObjectList) {
		TreeNode treeNode = new TreeNode(new StringObjectModel(ResourceString
				.getResourceString("label.object.type." + objectType)));
		List<TreeNode> objectNodeList = new ArrayList<TreeNode>();

		for (DBObject dbObject : dbObjectList) {
			TreeNode objectNode = new TreeNode(dbObject);
			objectNode.setParent(treeNode);

			objectNodeList.add(objectNode);
		}

		treeNode.setChildren(objectNodeList.toArray(new TreeNode[objectNodeList
				.size()]));

		return treeNode;
	}

	public boolean isUseCommentAsLogicalName() {
		return resultUseCommentAsLogicalName;
	}

	public boolean isMergeWord() {
		return resultMergeWord;
	}

	public boolean isMergeGroup() {
		return resultMergeGroup;
	}

	public List<DBObject> getSelectedDbObjects() {
		return resultSelectedDbObjects;
	}

}
