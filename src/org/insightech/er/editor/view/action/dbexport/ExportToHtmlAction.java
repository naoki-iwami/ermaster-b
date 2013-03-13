package org.insightech.er.editor.view.action.dbexport;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.ExportToHtmlWithProgressManager;
import org.insightech.er.editor.model.diagram_contents.element.node.Location;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.util.io.FileUtils;

public class ExportToHtmlAction extends AbstractExportAction {

	public static final String ID = ExportToHtmlAction.class.getName();

	private static final String OUTPUT_DIR = "/dbdocs/";

	public ExportToHtmlAction(ERDiagramEditor editor) {
		super(ID, ResourceString.getResourceString("action.title.export.html"),
				editor);
		this.setImageDescriptor(Activator
				.getImageDescriptor(ImageKey.EXPORT_TO_HTML));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getSaveFilePath(IEditorPart editorPart,
			GraphicalViewer viewer) {

		IFile file = ((IFileEditorInput) editorPart.getEditorInput()).getFile();

		DirectoryDialog fileDialog = new DirectoryDialog(editorPart
				.getEditorSite().getShell(), SWT.SAVE);

		IProject project = file.getProject();

		fileDialog.setFilterPath(project.getLocation().toString());
		fileDialog.setMessage(ResourceString
				.getResourceString("dialog.message.export.html.dir.select"));

		String saveFilePath = fileDialog.open();

		if (saveFilePath != null) {
			saveFilePath = saveFilePath + OUTPUT_DIR;
		}

		return saveFilePath;
	}

	@Override
	protected String getConfirmOverrideMessage() {
		return "dialog.message.update.html.export.dir";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void save(IEditorPart editorPart, GraphicalViewer viewer,
			String saveFilePath) throws Exception {

		ERDiagram diagram = this.getDiagram();

		Category currentCategory = diagram.getCurrentCategory();
		int currentCategoryIndex = diagram.getCurrentCategoryIndex();

		try {
			ProgressMonitorDialog monitor = new ProgressMonitorDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getShell());

			boolean outputImage = true;

			// 出力ディレクトリの削除
			File dir = new File(saveFilePath);
			FileUtils.deleteDirectory(dir);

			dir = new File(saveFilePath + "/image");
			dir.mkdirs();

			String outputImageFilePath = saveFilePath + "/image/er.png";

			if (outputImage) {
				diagram.setCurrentCategory(null, 0);

				int imageFormat = ExportToImageAction.outputImage(monitor,
						viewer, outputImageFilePath);

				if (imageFormat == -1) {
					throw new InputException(null);
				}
			}

			Map<TableView, Location> tableLocationMap = getTableLocationMap(
					this.getGraphicalViewer(), this.getDiagram());

			ExportToHtmlWithProgressManager manager = new ExportToHtmlWithProgressManager(
					saveFilePath, diagram, tableLocationMap);
			monitor.run(true, true, manager);

			if (manager.getException() != null) {
				throw manager.getException();
			}

		} catch (IOException e) {
			Activator.showMessageDialog(e.getMessage());

		} catch (InterruptedException e) {

		} catch (Exception e) {
			Activator.showExceptionDialog(e);

		} finally {
			diagram.setCurrentCategory(currentCategory, currentCategoryIndex);
		}

		this.refreshProject();
	}

	@Override
	protected String[] getFilterExtensions() {
		return null;
	}

	public static Map<TableView, Location> getTableLocationMap(
			GraphicalViewer viewer, ERDiagram diagram) {
		Map<TableView, Location> tableLocationMap = new HashMap<TableView, Location>();

		ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) viewer
				.getEditPartRegistry().get(LayerManager.ID);
		IFigure rootFigure = ((LayerManager) rootEditPart)
				.getLayer(LayerConstants.PRINTABLE_LAYERS);
		int translateX = ExportToImageAction
				.translateX(rootFigure.getBounds().x);
		int translateY = ExportToImageAction
				.translateY(rootFigure.getBounds().y);

		Category category = diagram.getCurrentCategory();

		for (Object child : rootEditPart.getContents().getChildren()) {
			NodeElementEditPart editPart = (NodeElementEditPart) child;
			NodeElement nodeElement = (NodeElement) editPart.getModel();
			if (!(nodeElement instanceof TableView)) {
				continue;
			}

			if (category == null || category.isVisible(nodeElement, diagram)) {
				IFigure figure = editPart.getFigure();
				Rectangle figureRectangle = figure.getBounds();

				Location location = new Location(
						figureRectangle.x + translateX, figureRectangle.y
								+ translateY, figureRectangle.width,
						figureRectangle.height);
				tableLocationMap.put((TableView) nodeElement, location);
			}
		}

		return tableLocationMap;
	}

	@Override
	protected String getDefaultExtension() {
		return "";
	}

}
