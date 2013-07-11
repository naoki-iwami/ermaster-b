package org.insightech.er.editor.model;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.core.resources.IResource;
import org.eclipse.gef.EditPart;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.editor.EROneDiagramEditor;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.ermodel.ERModel;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;

public class ERModelUtil {

	public static IEditorPart getActiveEditor(){
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		IEditorPart editorPart = page.getActiveEditor();
		return editorPart;
	}

	public static ERDiagram getDiagram(EditPart editPart) {
		Object model = editPart.getModel();
		if (model instanceof ERModel) {
			return ((ERModel) model).getDiagram();

		}
		return (ERDiagram) model;
	}

	public static boolean refreshDiagram(ERDiagram diagram) {
		if (diagram == null) {
			return false;
		}
		IEditorPart activeEditor = diagram.getEditor().getActiveEditor();
		if (activeEditor instanceof EROneDiagramEditor) {
			EROneDiagramEditor editor = (EROneDiagramEditor) activeEditor;
			editor.setContents(diagram.getCurrentErmodel());
			diagram.changeAll();
			return true;
		}
		return false;
	}

	public static boolean refreshDiagram(ERDiagram diagram, NodeElement element) {
		if (refreshDiagram(diagram)) {
			if (element instanceof ERTable) {
				IEditorPart activeEditor = diagram.getEditor().getActiveEditor();
				if (activeEditor instanceof EROneDiagramEditor) {
					EROneDiagramEditor editor = (EROneDiagramEditor) activeEditor;
					editor.reveal((ERTable) element);
				}
			}
			return true;
		}
		return false;
	}


    public static void openDirectory(IResource resource) {
    	String directory = resource.getLocation().toString().replaceAll("/", "\\\\");
    	String target = "c:\\windows\\explorer.exe" + " " + "/n, /select, {0}";
		target = MessageFormat.format(target, new Object[]{ directory });
		try {
			Runtime.getRuntime().exec(target);
		} catch (IOException e) {
			Activator.log(e);
		}
    }


}
