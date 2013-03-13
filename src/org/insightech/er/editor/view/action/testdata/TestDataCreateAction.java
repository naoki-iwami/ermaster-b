package org.insightech.er.editor.view.action.testdata;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.command.testdata.ChangeTestDataCommand;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.testdata.TestData;
import org.insightech.er.editor.view.action.AbstractBaseAction;
import org.insightech.er.editor.view.dialog.testdata.TestDataManageDialog;

public class TestDataCreateAction extends AbstractBaseAction {

	public static final String ID = TestDataCreateAction.class.getName();

	public TestDataCreateAction(ERDiagramEditor editor) {
		super(ID, ResourceString
				.getResourceString("action.title.testdata.create"), editor);

		this.setImageDescriptor(Activator
				.getImageDescriptor(ImageKey.TEST_DATA));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Event event) {
		ERDiagram diagram = this.getDiagram();

		List<TestData> testDataList = diagram.getDiagramContents()
				.getTestDataList();

		List<TestData> copyTestDataList = new ArrayList<TestData>();
		for (TestData testData : testDataList) {
			copyTestDataList.add(testData.clone());
		}

		TestDataManageDialog dialog = new TestDataManageDialog(PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getShell(), this
				.getEditorPart(), diagram, copyTestDataList);

		if (dialog.open() == IDialogConstants.OK_ID) {
			ChangeTestDataCommand command = new ChangeTestDataCommand(diagram,
					copyTestDataList);
			this.execute(command);
		}
	}

}
