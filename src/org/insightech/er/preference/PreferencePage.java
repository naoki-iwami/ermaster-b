package org.insightech.er.preference;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.not_element.group.CopyGroup;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GlobalGroupSet;
import org.insightech.er.editor.model.diagram_contents.not_element.group.GroupSet;
import org.insightech.er.editor.view.dialog.group.GroupManageDialog;

public class PreferencePage extends org.eclipse.jface.preference.PreferencePage
		implements IWorkbenchPreferencePage {

	@Override
	protected Control createContents(Composite parent) {
		this.noDefaultAndApplyButton();

		Composite composite = new Composite(parent, SWT.NONE);

		composite.setLayout(new GridLayout());

		initialize(composite);

		return composite;
	}

	private void initialize(Composite parent) {
		Button button = new Button(parent, SWT.NONE);
		button.setText(ResourceString
				.getResourceString("action.title.manage.global.group"));
		button.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				GroupSet columnGroups = GlobalGroupSet.load();
				ERDiagram diagram = new ERDiagram(columnGroups.getDatabase());

				GroupManageDialog dialog = new GroupManageDialog(PlatformUI
						.getWorkbench().getActiveWorkbenchWindow().getShell(),
						columnGroups, diagram, true, -1);

				if (dialog.open() == IDialogConstants.OK_ID) {
					List<CopyGroup> newColumnGroups = dialog
							.getCopyColumnGroups();

					columnGroups.clear();

					for (CopyGroup copyColumnGroup : newColumnGroups) {
						columnGroups.add(copyColumnGroup.restructure(null));
					}

					GlobalGroupSet.save(columnGroups);
				}
			}
		});
	}

	public void init(IWorkbench workbench) {
	}

}
