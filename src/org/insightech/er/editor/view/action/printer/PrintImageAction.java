package org.insightech.er.editor.view.action.printer;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.print.PrintGraphicalViewerOperation;
import org.eclipse.gef.ui.actions.PrintAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.ui.IWorkbenchPart;
import org.insightech.er.editor.ERDiagramEditor;

public class PrintImageAction extends PrintAction {

	public PrintImageAction(ERDiagramEditor part) {
		super((IWorkbenchPart) part);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		GraphicalViewer viewer;
		viewer = (GraphicalViewer) getWorkbenchPart().getAdapter(
				GraphicalViewer.class);

		PrintDialog dialog = new PrintDialog(viewer.getControl().getShell(),
				SWT.NULL);
		PrinterData data = dialog.open();

		if (data != null) {
			Printer printer = new Printer(data);
			PrintGraphicalViewerOperation op = new PrintERDiagramOperation(
					printer, viewer);

			op.run(getWorkbenchPart().getTitle());
		}
	}

}
