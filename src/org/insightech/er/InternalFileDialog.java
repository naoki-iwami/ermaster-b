package org.insightech.er;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.misc.ResourceAndContainerGroup;


@SuppressWarnings("restriction")
public class InternalFileDialog extends TitleAreaDialog implements Listener {

	private ResourceAndContainerGroup resourceGroup;
	private IPath fullPath;
	private String initialFolder;

	protected InternalFileDialog(Shell parentShell, String initialFolder) {
		super(parentShell);
		this.initialFolder = initialFolder;
	}

	@Override
	protected Control createContents(Composite parent) {
		// TODO Auto-generated method stub
		return super.createContents(parent);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite topLevel = new Composite(parent, SWT.NONE);
		topLevel.setLayout(new GridLayout());
		topLevel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL));
		topLevel.setFont(parent.getFont());

		resourceGroup = new ResourceAndContainerGroup(topLevel, this,
				"File name:",
				IDEWorkbenchMessages.WizardNewFileCreationPage_file, false,
				250);
		resourceGroup.setResourceExtension("sql");
		resourceGroup.setContainerFullPath(new Path(initialFolder).removeLastSegments(1));
		resourceGroup.setResource(new Path(initialFolder).lastSegment());

		setTitle("File");


//		Text text = new Text(parent, SWT.NONE);
//		text.setText("abc");
		// TODO Auto-generated method stub
		return super.createDialogArea(parent);
	}

	@Override
	public void handleEvent(Event event) {
		System.out.println("handleEvent");
	}

	@Override
	protected void okPressed() {
		if (resourceGroup.getContainerFullPath() == null) {
			setErrorMessage("èoóÕêÊÇëIëÇµÇƒÇ≠ÇæÇ≥Ç¢ÅB");
		} else {
			fullPath = resourceGroup.getContainerFullPath().append(resourceGroup.getResource());
			super.okPressed();
		}
	}

	public IPath getResourcePath() {
		return fullPath;
	}

}
