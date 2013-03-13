package org.insightech.er.editor.view.action.dbexport;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.SimpleRootEditPart;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.ERDiagramEditor;
import org.insightech.er.editor.controller.editpart.element.node.NodeElementEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.image.ExportToImageWithProgressManager;
import org.insightech.er.editor.model.diagram_contents.element.connection.Bendpoint;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;
import org.insightech.er.editor.model.diagram_contents.element.node.category.Category;

public class ExportToImageAction extends AbstractExportAction {

	public static final String ID = ExportToImageAction.class.getName();

	public ExportToImageAction(ERDiagramEditor editor) {
		super(ID,
				ResourceString.getResourceString("action.title.export.image"),
				editor);
		this.setImageDescriptor(Activator
				.getImageDescriptor(ImageKey.EXPORT_TO_IMAGE));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Event event) throws Exception {
		// EditPart editPart = this.getGraphicalViewer().getContents();
		// ERDiagram diagram = (ERDiagram) editPart.getModel();

		// if (!diagram.getDiagramContents().getSettings().getCategorySettings()
		// .getAllCategories().isEmpty()) {
		// if (Activator
		// .showConfirmDialog(Activator
		// .getResourceString("dialog.message.confirm.export.all.category"))) {
		// this.saveAllCategories(this.getEditorPart(), this
		// .getGraphicalViewer());
		// return;
		// }
		// }

		this.save(this.getEditorPart(), this.getGraphicalViewer());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void save(IEditorPart editorPart, GraphicalViewer viewer,
			String saveFilePath) {

		ProgressMonitorDialog monitor = new ProgressMonitorDialog(PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getShell());

		try {
			if (outputImage(monitor, viewer, saveFilePath) != -1) {
				Activator.showMessageDialog("dialog.message.export.finish");
			}
		} catch (InterruptedException e) {
		}
	}

	public static int outputImage(ProgressMonitorDialog monitor,
			GraphicalViewer viewer, String saveFilePath)
			throws InterruptedException {
		int format = getFormatType(saveFilePath);

		if (format == -1) {
			Activator
					.showMessageDialog("dialog.message.export.image.not.supported");
			return -1;
		}

		Image img = null;

		try {
			img = createImage(viewer);

			ExportToImageWithProgressManager exportToImageManager = new ExportToImageWithProgressManager(
					img, format, saveFilePath);

			monitor.run(true, true, exportToImageManager);

			Exception exception = exportToImageManager.getException();
			if (exception != null) {
				throw exception;
			}

		} catch (InterruptedException e) {
			throw e;

		} catch (Exception e) {
			if (e.getCause() instanceof OutOfMemoryError) {
				Activator
						.showMessageDialog("dialog.message.export.image.out.of.memory");

			} else {
				Activator.showExceptionDialog(e);
			}

			return -1;

		} finally {
			if (img != null) {
				img.dispose();
			}
		}

		return format;
	}

	public static int getFormatType(String saveFilePath) {
		int format = -1;

		int index = saveFilePath.lastIndexOf(".");
		String ext = null;
		if (index != -1 && index != saveFilePath.length() - 1) {
			ext = saveFilePath.substring(index + 1, saveFilePath.length());
		} else {
			ext = "";
		}

		if (ext.equalsIgnoreCase("jpeg") || ext.equalsIgnoreCase("jpg")) {
			format = SWT.IMAGE_JPEG;

		} else if (ext.equalsIgnoreCase("bmp")) {
			format = SWT.IMAGE_BMP;

		} else if (ext.equalsIgnoreCase("png")) {
			format = SWT.IMAGE_PNG;

		}

		return format;
	}

	public static Image createImage(GraphicalViewer viewer) {
		Image img = null;

		GC figureCanvasGC = null;
		GC imageGC = null;

		try {
			ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) viewer
					.getEditPartRegistry().get(LayerManager.ID);
			rootEditPart.refresh();
			IFigure rootFigure = ((LayerManager) rootEditPart)
					.getLayer(LayerConstants.PRINTABLE_LAYERS);

			EditPart editPart = viewer.getContents();
			editPart.refresh();
			ERDiagram diagram = (ERDiagram) editPart.getModel();

			Rectangle rootFigureBounds = getBounds(diagram, rootEditPart,
					rootFigure.getBounds());

			Control figureCanvas = viewer.getControl();
			figureCanvasGC = new GC(figureCanvas);

			img = new Image(Display.getCurrent(), rootFigureBounds.width + 20,
					rootFigureBounds.height + 20);
			imageGC = new GC(img);

			imageGC.setBackground(figureCanvasGC.getBackground());
			imageGC.setForeground(figureCanvasGC.getForeground());
			imageGC.setFont(figureCanvasGC.getFont());
			imageGC.setLineStyle(figureCanvasGC.getLineStyle());
			imageGC.setLineWidth(figureCanvasGC.getLineWidth());
			imageGC.setAntialias(SWT.OFF);
			// imageGC.setInterpolation(SWT.HIGH);

			Graphics imgGraphics = new SWTGraphics(imageGC);
			imgGraphics.setBackgroundColor(figureCanvas.getBackground());
			imgGraphics.fillRectangle(0, 0, rootFigureBounds.width + 20,
					rootFigureBounds.height + 20);

			imgGraphics.translate(translateX(rootFigureBounds.x),
					translateY(rootFigureBounds.y));

			rootFigure.paint(imgGraphics);

			return img;

		} finally {
			if (figureCanvasGC != null) {
				figureCanvasGC.dispose();
			}
			if (imageGC != null) {
				imageGC.dispose();
			}
		}
	}

	public static int translateX(int x) {
		return -x + 10;
	}

	public static int translateY(int y) {
		return -y + 10;
	}

	@Override
	protected String[] getFilterExtensions() {
		return new String[] { "*.png", "*.jpeg", "*.bmp" };
	}

	public static Rectangle getBounds(ERDiagram diagram,
			SimpleRootEditPart rootEditPart, Rectangle rootFigureBounds) {
		Category category = diagram.getCurrentCategory();

		if (category == null) {
			return rootFigureBounds;

		} else {
			Rectangle rectangle = new Rectangle();

			int minX = rootFigureBounds.x + rootFigureBounds.width;
			int minY = rootFigureBounds.y + rootFigureBounds.height;
			int maxX = rootFigureBounds.x;
			int maxY = rootFigureBounds.y;

			Map<NodeElement, IFigure> visibleElements = new HashMap<NodeElement, IFigure>();

			for (Object child : rootEditPart.getContents().getChildren()) {
				NodeElementEditPart editPart = (NodeElementEditPart) child;
				NodeElement nodeElement = (NodeElement) editPart.getModel();

				if (category.isVisible(nodeElement, diagram)) {
					IFigure figure = editPart.getFigure();
					Rectangle figureRectangle = figure.getBounds();

					visibleElements.put(nodeElement, figure);

					if (figureRectangle.x < minX) {
						minX = figureRectangle.x;

					}
					if (figureRectangle.x + figureRectangle.width > maxX) {
						maxX = figureRectangle.x + figureRectangle.width;
					}

					if (figureRectangle.y < minY) {
						minY = figureRectangle.y;
					}
					if (figureRectangle.y + figureRectangle.height > maxY) {
						maxY = figureRectangle.y + figureRectangle.height;
					}
				}
			}

			for (NodeElement sourceElement : visibleElements.keySet()) {
				for (ConnectionElement connection : sourceElement
						.getOutgoings()) {
					if (visibleElements.containsKey(connection.getTarget())) {
						for (Bendpoint bendpoint : connection.getBendpoints()) {
							int x = bendpoint.getX();
							int y = bendpoint.getY();

							if (bendpoint.isRelative()) {
								IFigure figure = visibleElements
										.get(sourceElement);
								Rectangle figureRectangle = figure.getBounds();
								x = figureRectangle.x + figureRectangle.width
										* 2;
								y = figureRectangle.y + figureRectangle.height
										* 2;
							}

							if (x < minX) {
								minX = x;

							} else if (x > maxX) {
								maxX = x;
							}

							if (y < minY) {
								minY = y;

							} else if (y > maxY) {
								maxY = y;
							}

						}
					}
				}
			}

			rectangle.x = minX;
			rectangle.y = minY;
			rectangle.width = maxX - minX;
			rectangle.height = maxY - minY;
			if (rectangle.width < 0) {
				rectangle.x = 0;
				rectangle.width = 0;
			}
			if (rectangle.height < 0) {
				rectangle.y = 0;
				rectangle.height = 0;
			}

			return rectangle;
		}
	}

	@Override
	protected String getDefaultExtension() {
		return ".png";
	}

	// protected void saveAllCategories(IEditorPart editorPart,
	// GraphicalViewer viewer) throws Exception {
	//
	// String saveDirPath = this.getSaveDirPath(editorPart, viewer);
	// if (saveDirPath == null) {
	// return;
	// }
	//
	// File dir = new File(saveDirPath);
	// dir.mkdirs();
	//
	// ProgressMonitorDialog monitor = new ProgressMonitorDialog(PlatformUI
	// .getWorkbench().getActiveWorkbenchWindow().getShell());
	//
	// // try {
	// EditPart editPart = this.getGraphicalViewer().getContents();
	// ERDiagram diagram = (ERDiagram) editPart.getModel();
	//
	// for (Category category : diagram.getDiagramContents().getSettings()
	// .getCategorySettings().getAllCategories()) {
	//
	// // if (outputImage(monitor, viewer, saveFilePath) != -1) {
	// //
	// // }
	// }
	//
	// Activator.showMessageDialog(Activator
	// .getResourceString("dialog.message.export.image.finish"));
	//
	// // } catch (InterruptedException e) {
	// // }
	//
	// IFile iFile = ((IFileEditorInput) editorPart.getEditorInput())
	// .getFile();
	// IProject project = iFile.getProject();
	//
	// project.refreshLocal(IResource.DEPTH_INFINITE, null);
	// }
}
