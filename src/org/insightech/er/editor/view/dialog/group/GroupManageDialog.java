package org.insightech.er.editor.view.dialog.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.CopyGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GlobalGroupSet;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.view.dialog.common.ERTableComposite;
import org.insightech.er.editor.view.dialog.common.ERTableCompositeHolder;
import org.insightech.er.editor.view.dialog.word.column.real.GroupColumnDialog;

public class GroupManageDialog extends AbstractDialog implements
		ERTableCompositeHolder {

	private Text groupNameText;

	private org.eclipse.swt.widgets.List groupList;

	private Button groupUpdateButton;

	private Button groupCancelButton;

	private Button groupAddButton;

	private Button groupEditButton;

	private Button groupDeleteButton;

	private Button addToGlobalGroupButton;

	private List<CopyGroup> copyGroups;

	private int editTargetIndex = -1;

	private CopyGroup copyData;

	private ERDiagram diagram;

	private boolean globalGroup;

	private ERTableComposite tableComposite;

	private static final int HEIGHT = 360;

	private static final int GROUP_LIST_HEIGHT = 230;

	public GroupManageDialog(Shell parentShell, GroupSet columnGroups,
			ERDiagram diagram, boolean globalGroup, int editTargetIndex) {
		super(parentShell, 2);

		this.copyGroups = new ArrayList<CopyGroup>();

		for (ColumnGroup columnGroup : columnGroups) {
			this.copyGroups.add(new CopyGroup(columnGroup));
		}

		this.diagram = diagram;

		this.globalGroup = globalGroup;

		this.editTargetIndex = editTargetIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite composite) {
		this.createGroupListComposite(composite);
		this.createGroupDetailComposite(composite);

		this.setGroupEditEnabled(false);
	}

	/**
	 * This method initializes composite
	 * 
	 */
	private void createGroupListComposite(Composite parent) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.verticalSpacing = 10;

		GridData gridData = new GridData();
		gridData.heightHint = HEIGHT;

		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayoutData(gridData);
		composite.setLayout(gridLayout);
		createGroup(composite);

		groupAddButton = new Button(composite, SWT.NONE);
		groupAddButton.setText(ResourceString
				.getResourceString("label.button.group.add"));

		groupEditButton = new Button(composite, SWT.NONE);
		groupEditButton.setText(ResourceString
				.getResourceString("label.button.group.edit"));

		this.groupDeleteButton = new Button(composite, SWT.NONE);
		this.groupDeleteButton.setText(ResourceString
				.getResourceString("label.button.group.delete"));

		this.addToGlobalGroupButton = new Button(composite, SWT.NONE);
		this.addToGlobalGroupButton.setText(ResourceString
				.getResourceString("label.button.add.to.global.group"));

		GridData gridData3 = new GridData();
		gridData3.horizontalSpan = 3;
		this.addToGlobalGroupButton.setLayoutData(gridData3);

		if (this.globalGroup) {
			this.addToGlobalGroupButton.setVisible(false);
		}

		setButtonEnabled(false);
	}

	/**
	 * This method initializes composite1
	 * 
	 */
	private void createGroupDetailComposite(Composite parent) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;

		GridData gridData = new GridData();
		gridData.heightHint = HEIGHT;

		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(gridLayout);
		composite.setLayoutData(gridData);

		this.groupNameText = CompositeFactory.createText(this, composite,
				"label.group.name", 1, 200, true);

		GroupColumnDialog columnDialog = new GroupColumnDialog(PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getShell(), diagram);

		this.tableComposite = new ERTableComposite(this, composite,
				this.diagram, null, null, columnDialog, this, 2, true, true);

		createComposite3(composite);
	}

	/**
	 * This method initializes group
	 * 
	 */
	private void createGroup(Composite parent) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;

		GridData gridData1 = new GridData();
		gridData1.horizontalSpan = 3;

		GridData gridData2 = new GridData();
		gridData2.widthHint = 200;
		gridData2.horizontalSpan = 3;
		gridData2.heightHint = GROUP_LIST_HEIGHT;

		Group group = new Group(parent, SWT.NONE);
		group.setText(ResourceString.getResourceString("label.group.list"));
		group.setLayoutData(gridData1);
		group.setLayout(gridLayout);

		this.groupList = new org.eclipse.swt.widgets.List(group, SWT.BORDER
				| SWT.V_SCROLL);
		this.groupList.setLayoutData(gridData2);

		this.initGroupList();
	}

	private void initGroupList() {
		Collections.sort(this.copyGroups);

		this.groupList.removeAll();
		for (ColumnGroup columnGroup : this.copyGroups) {
			this.groupList.add(columnGroup.getGroupName());
		}
	}

	@SuppressWarnings("unchecked")
	private void initColumnGroup() {
		String text = this.copyData.getGroupName();

		if (text == null) {
			text = "";
		}

		this.groupNameText.setText(text);

		this.tableComposite.setColumnList((List) this.copyData.getColumns());
	}

	private void setGroupEditEnabled(boolean enabled) {
		this.tableComposite.setEnabled(enabled);

		this.groupUpdateButton.setEnabled(enabled);
		this.groupCancelButton.setEnabled(enabled);
		this.groupNameText.setEnabled(enabled);

		this.groupList.setEnabled(!enabled);

		this.groupAddButton.setEnabled(!enabled);
		if (this.groupList.getSelectionIndex() != -1 && !enabled) {
			this.setButtonEnabled(true);

		} else {
			this.setButtonEnabled(false);
		}

		if (enabled) {
			this.groupNameText.setFocus();
		} else {
			this.groupList.setFocus();
		}

		this.enabledButton(!enabled);
	}

	private void createComposite3(Composite parent) {
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(gridLayout);
		composite.setLayoutData(gridData);

		GridData gridData1 = new GridData();
		gridData1.widthHint = 80;

		this.groupUpdateButton = new Button(composite, SWT.NONE);
		this.groupUpdateButton.setText(ResourceString
				.getResourceString("label.button.update"));
		this.groupUpdateButton.setLayoutData(gridData1);

		this.groupCancelButton = new Button(composite, SWT.NONE);
		this.groupCancelButton.setText(ResourceString
				.getResourceString("label.button.cancel"));
		this.groupCancelButton.setLayoutData(gridData1);
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
		if (this.globalGroup) {
			return "dialog.title.manage.global.group";
		}
		return "dialog.title.manage.group";
	}

	@Override
	protected void setData() {
		if (this.editTargetIndex != -1) {
			this.groupList.setSelection(editTargetIndex);

			this.copyData = new CopyGroup(copyGroups.get(editTargetIndex));
			this.initColumnGroup();

			this.setGroupEditEnabled(true);
		}
	}

	public List<CopyGroup> getCopyColumnGroups() {
		return copyGroups;
	}

	private void setButtonEnabled(boolean enabled) {
		this.groupEditButton.setEnabled(enabled);
		this.groupDeleteButton.setEnabled(enabled);
		this.addToGlobalGroupButton.setEnabled(enabled);
	}

	public void selectGroup(ColumnGroup selectedColumn) {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addListener() {
		super.addListener();

		this.groupAddButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				editTargetIndex = -1;

				copyData = new CopyGroup(new ColumnGroup());
				initColumnGroup();
				setGroupEditEnabled(true);
			}
		});

		this.groupEditButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				editTargetIndex = groupList.getSelectionIndex();
				if (editTargetIndex == -1) {
					return;
				}

				setGroupEditEnabled(true);
			}
		});

		this.groupDeleteButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				editTargetIndex = groupList.getSelectionIndex();
				if (editTargetIndex == -1) {
					return;
				}

				copyGroups.remove(editTargetIndex);

				initGroupList();

				if (copyGroups.size() == 0) {
					editTargetIndex = -1;

				} else if (editTargetIndex >= copyGroups.size()) {
					editTargetIndex = copyGroups.size() - 1;
				}

				if (editTargetIndex != -1) {
					groupList.setSelection(editTargetIndex);
					copyData = new CopyGroup(copyGroups.get(editTargetIndex));
					initColumnGroup();

				} else {
					copyData = new CopyGroup(new ColumnGroup());
					initColumnGroup();
					setButtonEnabled(false);
				}

			}
		});

		this.addToGlobalGroupButton
				.addSelectionListener(new SelectionAdapter() {

					/**
					 * {@inheritDoc}
					 */
					@Override
					public void widgetSelected(SelectionEvent e) {
						editTargetIndex = groupList.getSelectionIndex();
						if (editTargetIndex == -1) {
							return;
						}

						MessageBox messageBox = new MessageBox(PlatformUI
								.getWorkbench().getActiveWorkbenchWindow()
								.getShell(), SWT.ICON_QUESTION | SWT.OK
								| SWT.CANCEL);
						messageBox
								.setText(ResourceString
										.getResourceString("label.button.add.to.global.group"));
						messageBox
								.setMessage(ResourceString
										.getResourceString("dialog.message.add.to.global.group"));

						if (messageBox.open() == SWT.OK) {
							CopyGroup columnGroup = copyGroups
									.get(editTargetIndex);

							GroupSet columnGroups = GlobalGroupSet.load();

							columnGroups.add(columnGroup);

							GlobalGroupSet.save(columnGroups);
						}

					}
				});

		this.groupList.addMouseListener(new MouseAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				editTargetIndex = groupList.getSelectionIndex();
				if (editTargetIndex == -1) {
					return;
				}

				setGroupEditEnabled(true);
			}
		});

		this.groupList.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					editTargetIndex = groupList.getSelectionIndex();
					if (editTargetIndex == -1) {
						return;
					}
					copyData = new CopyGroup(copyGroups.get(editTargetIndex));
					initColumnGroup();
					setButtonEnabled(true);

				} catch (Exception ex) {
					Activator.showExceptionDialog(ex);
				}
			}
		});

		this.groupUpdateButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					if (validate()) {
						String text = groupNameText.getText().trim();
						copyData.setGroupName(text);

						if (editTargetIndex == -1) {
							copyGroups.add(copyData);

						} else {
							copyGroups.remove(editTargetIndex);
							copyData = (CopyGroup) copyData.restructure(null);

							copyGroups.add(editTargetIndex, copyData);
						}

						setGroupEditEnabled(false);
						initGroupList();

						for (int i = 0; i < copyGroups.size(); i++) {
							ColumnGroup columnGroup = copyGroups.get(i);

							if (columnGroup == copyData) {
								groupList.setSelection(i);
								copyData = new CopyGroup(copyGroups.get(i));
								initColumnGroup();
								setButtonEnabled(true);
								break;
							}

						}
					}
				} catch (Exception ex) {
					Activator.showExceptionDialog(ex);
				}
			}

		});

		this.groupCancelButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				setGroupEditEnabled(false);
				if (editTargetIndex != -1) {
					copyData = new CopyGroup(copyGroups.get(editTargetIndex));
					initColumnGroup();
				}
			}
		});
	}

}
