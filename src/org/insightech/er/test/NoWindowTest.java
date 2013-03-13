package org.insightech.er.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tools.ant.BuildException;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.insightech.er.Activator;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPartFactory;
import org.insightech.er.editor.controller.editpart.element.PagableFreeformRootEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.persistent.Persistent;

public class NoWindowTest {

	public static void main(String[] args) {
		// ERDiagramMultiPageEditor multiPageEditor = new
		// ERDiagramMultiPageEditor();
		// multiPageEditor.createPartControl(null);
		//
		// ERDiagramEditor editor = (ERDiagramEditor) multiPageEditor
		// .getActiveEditor();
		// GraphicalViewer viewer = editor.getGraphicalViewer();
		execute();

		new Activator();
		Display display1 = new Display();
		run(display1, 5);
		try {
			// activator.stop(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// activator = new Activator();
		// Display display2 = new Display();
		run(display1, 1000);
		display1.dispose();
	}

	private static void run(Display display, int x) {
		Shell shell = new Shell(display);
		shell.setBounds(0, 0, 350, 350);

		shell.setLayout(new FillLayout(SWT.VERTICAL));

		// display.syncExec(new Runnable() {
		// public void run() {

		ERDiagramEditPartFactory editPartFactory = new ERDiagramEditPartFactory();
		GraphicalViewer viewer = new ScrollingGraphicalViewer();
		viewer.setControl(new FigureCanvas(shell));

		ScalableFreeformRootEditPart rootEditPart = new PagableFreeformRootEditPart(
				diagram);
		viewer.setRootEditPart(rootEditPart);

		viewer.setEditPartFactory(editPartFactory);
		viewer.setContents(diagram);

		viewer.getContents().refresh();

		// }
		// });

		shell.pack();
		shell.open();
		int count = 0;
		while (count < x) {
			if (!display.readAndDispatch()) {
				try {
					Thread.sleep(1000);
					count++;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		viewer.getContents().deactivate();
		// display.dispose();
	}

	static ERDiagram diagram;

	public static void execute() throws BuildException {
		Persistent persistent = Persistent.getInstance();

		InputStream in = null;

		try {
			File file = new File("newfile.erm");
			in = new BufferedInputStream(new FileInputStream(file));

			diagram = persistent.load(in);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException(e);

		} finally {
			if (in != null) {
				try {
					in.close();

				} catch (IOException e) {
					throw new BuildException(e);
				}
			}
		}
	}
}
