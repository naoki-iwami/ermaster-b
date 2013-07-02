package org.insightech.er;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.insightech.er.editor.controller.editpart.element.ERDiagramEditPartFactory;
import org.insightech.er.editor.controller.editpart.element.PagableFreeformRootEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.action.dbexport.ExportToImageAction;
import org.insightech.er.util.Format;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.insightech.er";

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		Resources.PINK.dispose();
		Resources.ADDED_COLOR.dispose();
		Resources.UPDATED_COLOR.dispose();
		Resources.REMOVED_COLOR.dispose();
		Resources.GRID_COLOR.dispose();
		Resources.DEFAULT_TABLE_COLOR.dispose();
		Resources.SELECTED_REFERENCED_COLUMN.dispose();
		Resources.SELECTED_FOREIGNKEY_COLUMN.dispose();
		Resources.SELECTED_REFERENCED_AND_FOREIGNKEY_COLUMN.dispose();
		Resources.VERY_LIGHT_GRAY.dispose();
		Resources.LINE_COLOR.dispose();

		Resources.TEST_COLOR.dispose();
		Resources.NOT_NULL_COLOR.dispose();
		Resources.PRIMARY_COLOR.dispose();
		Resources.FOREIGN_COLOR.dispose();

		Resources.disposeColorMap();

		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 *
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	private static ImageDescriptor loadImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);

		reg.put(ImageKey.ALIGN_BOTTOM,
				loadImageDescriptor("icons/alignbottom.gif"));
		reg.put(ImageKey.ALIGN_CENTER,
				loadImageDescriptor("icons/aligncenter.gif"));
		reg
				.put(ImageKey.ALIGN_LEFT,
						loadImageDescriptor("icons/alignleft.gif"));
		reg.put(ImageKey.ALIGN_MIDDLE,
				loadImageDescriptor("icons/alignmid.gif"));
		reg.put(ImageKey.ALIGN_RIGHT,
				loadImageDescriptor("icons/alignright.gif"));
		reg.put(ImageKey.ALIGN_TOP, loadImageDescriptor("icons/aligntop.gif"));
		reg.put(ImageKey.ARROW, loadImageDescriptor("icons/arrow16.gif"));
		reg.put(ImageKey.BLANK_WHITE, loadImageDescriptor("icons/blank_white.gif"));
		reg.put(ImageKey.CATEGORY, loadImageDescriptor("icons/category.gif"));
		reg.put(ImageKey.CHANGE_BACKGROUND_COLOR,
				loadImageDescriptor("icons/color.gif"));
		reg.put(ImageKey.CHANGE_BACKGROUND_COLOR_DISABLED,
				loadImageDescriptor("icons/square.gif"));
		reg.put(ImageKey.CHECK, loadImageDescriptor("icons/tick.png"));
		reg.put(ImageKey.CHECK_GREY, loadImageDescriptor("icons/tick_grey.png"));
		reg.put(ImageKey.COMMENT_CONNECTION,
				loadImageDescriptor("icons/comment_connection.gif"));
		reg.put(ImageKey.DATABASE, loadImageDescriptor("icons/database2.png"));
		reg.put(ImageKey.DATABASE_CONNECT,
				loadImageDescriptor("icons/database_connect.png"));
		reg.put(ImageKey.DICTIONARY,
				loadImageDescriptor("icons/dictionary.gif"));
		reg.put(ImageKey.DICTIONARY_OPEN,
				loadImageDescriptor("icons/dictionary_open.gif"));
		reg.put(ImageKey.EDIT, loadImageDescriptor("icons/pencil.png"));
		reg.put(ImageKey.ERROR, loadImageDescriptor("icons/error.gif"));
		reg.put(ImageKey.EXPORT_DDL,
				loadImageDescriptor("icons/document-attribute-d.png"));
		reg.put(ImageKey.EXPORT_TO_CSV,
				loadImageDescriptor("icons/document-excel-csv.png"));
		reg.put(ImageKey.EXPORT_TO_DB,
				loadImageDescriptor("icons/database_connect.png"));
		reg.put(ImageKey.EXPORT_TO_EXCEL,
				loadImageDescriptor("icons/document-excel.png"));
		reg.put(ImageKey.EXPORT_TO_HTML,
				loadImageDescriptor("icons/document-globe.png"));
		reg.put(ImageKey.EXPORT_TO_IMAGE,
				loadImageDescriptor("icons/document-image.png"));
		reg.put(ImageKey.EXPORT_TO_JAVA,
				loadImageDescriptor("icons/page_white_cup.png"));
		reg.put(ImageKey.EXPORT_TO_TEST_DATA,
				loadImageDescriptor("icons/tables--arrow.png"));
		reg.put(ImageKey.FIND, loadImageDescriptor("icons/binocular.png"));
		reg.put(ImageKey.FOREIGN_KEY,
				loadImageDescriptor("icons/foreign_key.gif"));
		reg.put(ImageKey.NON_NULL,
				loadImageDescriptor("icons/non_null.gif"));
		reg.put(ImageKey.GRID, loadImageDescriptor("icons/grid.png"));
		reg.put(ImageKey.GRID_SNAP, loadImageDescriptor("icons/grid-snap.png"));
		reg.put(ImageKey.GROUP, loadImageDescriptor("icons/group.gif"));
		reg.put(ImageKey.HORIZONTAL_LINE,
				loadImageDescriptor("icons/horizontal_line.gif"));
		reg.put(ImageKey.HORIZONTAL_LINE_DISABLED,
				loadImageDescriptor("icons/horizontal_line_disabled.gif"));
		reg.put(ImageKey.IMAGE, loadImageDescriptor("icons/image--plus.png"));
		reg.put(ImageKey.INDEX, loadImageDescriptor("icons/index.gif"));
		reg.put(ImageKey.LOCK_EDIT,
				loadImageDescriptor("icons/lock--pencil.png"));
		reg.put(ImageKey.MATCH_HEIGHT,
				loadImageDescriptor("icons/matchheight.gif"));
		reg.put(ImageKey.MATCH_WIDTH,
				loadImageDescriptor("icons/matchwidth.gif"));
		reg.put(ImageKey.NOTE, loadImageDescriptor("icons/note.gif"));
		reg.put(ImageKey.OPTION, loadImageDescriptor("icons/wrench.png"));
		reg.put(ImageKey.PAGE_SETTING_H, loadImageDescriptor("images/h.png"));
		reg.put(ImageKey.PAGE_SETTING_V, loadImageDescriptor("images/v.png"));
		reg.put(ImageKey.PALETTE, loadImageDescriptor("icons/palette.png"));
		reg.put(ImageKey.PRIMARY_KEY, loadImageDescriptor("icons/pkey.gif"));
		reg.put(ImageKey.RELATION_1_N,
				loadImageDescriptor("icons/relation_1_n.gif"));
		reg.put(ImageKey.RELATION_N_N,
				loadImageDescriptor("icons/relation_n_n.gif"));
		reg.put(ImageKey.RELATION_SELF,
				loadImageDescriptor("icons/relation_self.gif"));
		reg.put(ImageKey.RESIZE,
				loadImageDescriptor("icons/application-resize-actual.png"));
		reg.put(ImageKey.SEQUENCE, loadImageDescriptor("icons/sequence.gif"));
		reg.put(ImageKey.TITLEBAR_BACKGROUND,
				loadImageDescriptor("images/aqua-bg.gif"));
		reg.put(ImageKey.TABLE, loadImageDescriptor("icons/table.gif"));
		reg.put(ImageKey.TABLE_NEW, loadImageDescriptor("icons/table_new.gif"));
		reg.put(ImageKey.TABLESPACE, loadImageDescriptor("icons/database.png"));
		reg.put(ImageKey.TEST_DATA,
				loadImageDescriptor("icons/tables--pencil.png"));
		reg.put(ImageKey.TOOLTIP, loadImageDescriptor("icons/ui-tooltip.png"));
		reg.put(ImageKey.MAIN_COLUMN, loadImageDescriptor("icons/ui-main-column.png"));
		reg.put(ImageKey.TRIGGER,
				loadImageDescriptor("icons/arrow-turn-000-left.png"));
		reg.put(ImageKey.VERTICAL_LINE,
				loadImageDescriptor("icons/vertical_line.gif"));
		reg.put(ImageKey.VERTICAL_LINE_DISABLED,
				loadImageDescriptor("icons/vertical_line_disabled.gif"));
		reg.put(ImageKey.VIEW, loadImageDescriptor("icons/view.gif"));
		reg.put(ImageKey.WORD, loadImageDescriptor("icons/word_3.gif"));
		reg.put(ImageKey.ZOOM_IN,
				loadImageDescriptor("icons/magnifier-zoom.png"));
		reg.put(ImageKey.ZOOM_OUT,
				loadImageDescriptor("icons/magnifier-zoom-out.png"));
		reg.put(ImageKey.ZOOM_ADJUST,
				loadImageDescriptor("icons/magnifier-zoom-actual.png"));
		reg.put(ImageKey.DIAGRAM,
				loadImageDescriptor("icons/diagram.png"));
	}

	/**
	 * 指定されたキーに対応する {@link Image} を返します
	 *
	 * @param key
	 *            {@link ImageKey} で定義されたキー
	 * @return 指定されたキーに対応する {@link Image}
	 */
	public static Image getImage(String key) {
		return getDefault().getImageRegistry().get(key);
	}

	/**
	 * 指定されたキーに対応する {@link ImageDescriptor} を返します
	 *
	 * @param key
	 *            {@link ImageKey} で定義されたキー
	 * @return 指定されたキーに対応する {@link ImageDescriptor}
	 */
	public static ImageDescriptor getImageDescriptor(String key) {
		return getDefault().getImageRegistry().getDescriptor(key);
	}

	/**
	 * 指定された例外の例外ダイアログを表示します。
	 *
	 * @param e
	 *            例外
	 */
	public static void showExceptionDialog(Throwable e) {
		IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, e
				.toString(), e);

		Activator.log(e);

		ErrorDialog.openError(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), ResourceString
				.getResourceString("dialog.title.error"), ResourceString
				.getResourceString("error.plugin.error.message"), status);
	}

	/**
	 * 指定されたメッセージのエラーダイアログを表示します。
	 *
	 * @param message
	 *            エラーメッセージ
	 */
	public static void showErrorDialog(String message) {
		MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), SWT.ICON_ERROR | SWT.OK);
		messageBox.setText(ResourceString
				.getResourceString("dialog.title.error"));
		messageBox.setMessage(ResourceString.getResourceString(message));
		messageBox.open();
	}

	/**
	 * メッセージダイアログを表示します。
	 *
	 * @param message
	 *            メッセージ
	 */
	public static void showMessageDialog(String message) {
		MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), SWT.ICON_INFORMATION
				| SWT.OK);
		messageBox.setText(ResourceString
				.getResourceString("dialog.title.information"));
		messageBox.setMessage(ResourceString.getResourceString(Format
				.null2blank(message)));
		messageBox.open();
	}

	/**
	 * 確認ダイアログを表示します。
	 *
	 * @param message
	 *            メッセージ
	 */
	public static boolean showConfirmDialog(String message) {
		return showConfirmDialog(message, SWT.OK, SWT.CANCEL);

	}

	/**
	 * 確認ダイアログを表示します。
	 *
	 * @param message
	 *            メッセージ
	 */
	public static boolean showConfirmDialog(String message, int ok, int cancel) {
		MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), SWT.ICON_INFORMATION
				| ok | cancel);
		messageBox.setText(ResourceString
				.getResourceString("dialog.title.confirm"));
		messageBox.setMessage(ResourceString.getResourceString(message));
		int result = messageBox.open();

		if (result == ok) {
			return true;
		}

		return false;
	}

	/**
	 * 保存ダイアログを表示します
	 *
	 * @param filePath
	 *            デフォルトのファイルパス
	 * @param filterExtensions
	 *            拡張子
	 * @return 保存ダイアログで選択されたファイルのパス
	 */
	public static String showSaveDialog(String filePath,
			String[] filterExtensions) {
		String dir = null;
		String fileName = null;

		if (filePath != null && !"".equals(filePath.trim())) {
			File file = new File(filePath.trim());

			dir = file.getParent();
			fileName = file.getName();
		}

		FileDialog fileDialog = new FileDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), SWT.SAVE);

		fileDialog.setFilterPath(dir);
		fileDialog.setFileName(fileName);

		fileDialog.setFilterExtensions(filterExtensions);

		return fileDialog.open();
	}

	public static String showSaveDialogInternal(String filePath,
			String[] filterExtensions) {
		String dir = null;
		String fileName = null;

		if (filePath != null && !"".equals(filePath.trim())) {
			File file = new File(filePath.trim());

			dir = file.getParent();
			fileName = file.getName();
		}

		InternalFileDialog fileDialog = new InternalFileDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), filePath);
		if (fileDialog.open() == Window.OK) {
			IPath path = fileDialog.getResourcePath();
			return path.toString();
		}
		return null;
	}

	/**
	 * ディレクトリ選択ダイアログを表示します
	 *
	 * @param filePath
	 *            デフォルトのファイルパス
	 * @return ディレクトリ選択ダイアログで選択されたディレクトリのパス
	 */
	public static String showDirectoryDialog(String filePath) {
		String fileName = null;

		if (filePath != null && !"".equals(filePath.trim())) {
			File file = new File(filePath.trim());
			fileName = file.getPath();
		}

		DirectoryDialog dialog = new DirectoryDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), SWT.NONE);

		dialog.setFilterPath(fileName);

		return dialog.open();
	}

	public static void log(Throwable e) {
		e.printStackTrace();
		Activator.getDefault().getLog().log(
				new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, e
						.getMessage(), e));
	}

	private static class GraphicalViewerCreator implements Runnable {

		private Display display;

		private ERDiagram diagram;

		private GraphicalViewer viewer;

		private GraphicalViewerCreator(Display display, ERDiagram diagram) {
			this.display = display;
			this.diagram = diagram;
		}

		public void run() {
			Shell shell = new Shell(display);
			shell.setLayout(new GridLayout(1, false));

			ERDiagramEditPartFactory editPartFactory = new ERDiagramEditPartFactory();
			viewer = new ScrollingGraphicalViewer();
			viewer.setControl(new FigureCanvas(shell));
			ScalableFreeformRootEditPart rootEditPart = new PagableFreeformRootEditPart(
					diagram);
			viewer.setRootEditPart(rootEditPart);

			viewer.setEditPartFactory(editPartFactory);
			viewer.setContents(diagram);
		}

	}

	public static GraphicalViewer createGraphicalViewer(final ERDiagram diagram) {
		Display display = PlatformUI.getWorkbench().getDisplay();

		GraphicalViewerCreator runnable = new GraphicalViewerCreator(display,
				diagram);

		display.syncExec(runnable);

		return runnable.viewer;
	}

	private static class ImageCreator implements Runnable {

		private GraphicalViewer viewer;

		private Image img = null;

		private ImageCreator(GraphicalViewer viewer) {
			this.viewer = viewer;
		}

		public void run() {
			GC figureCanvasGC = null;
			GC imageGC = null;

			try {
				ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) viewer
						.getRootEditPart();
				rootEditPart.refresh();
				IFigure rootFigure = ((LayerManager) rootEditPart)
						.getLayer(LayerConstants.PRINTABLE_LAYERS);

				EditPart editPart = viewer.getContents();

				editPart.refresh();
				ERDiagram diagram = (ERDiagram) editPart.getModel();

				Rectangle rootFigureBounds = ExportToImageAction.getBounds(
						diagram, rootEditPart, rootFigure.getBounds());

				Control figureCanvas = viewer.getControl();

				figureCanvasGC = new GC(figureCanvas);

				img = new Image(Display.getCurrent(),
						rootFigureBounds.width + 20,
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

				imgGraphics.translate(ExportToImageAction
						.translateX(rootFigureBounds.x), ExportToImageAction
						.translateY(rootFigureBounds.y));

				rootFigure.paint(imgGraphics);

			} finally {
				if (figureCanvasGC != null) {
					figureCanvasGC.dispose();
				}
				if (imageGC != null) {
					imageGC.dispose();
				}
			}
		}

	}

	public static Image createImage(GraphicalViewer viewer) {
		Display display = PlatformUI.getWorkbench().getDisplay();

		ImageCreator runnable = new ImageCreator(viewer);

		display.syncExec(runnable);

		return runnable.img;

	}
}
