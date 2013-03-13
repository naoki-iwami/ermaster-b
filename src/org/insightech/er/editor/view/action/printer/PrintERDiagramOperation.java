package org.insightech.er.editor.view.action.printer;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.print.PrintGraphicalViewerOperation;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.settings.PageSetting;

public class PrintERDiagramOperation extends PrintGraphicalViewerOperation {

	public PrintERDiagramOperation(Printer p, GraphicalViewer g) {
		super(p, g);
	}

	protected ERDiagram getDiagram() {
		EditPart editPart = this.getViewer().getContents();
		ERDiagram diagram = (ERDiagram) editPart.getModel();

		return diagram;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Rectangle getPrintRegion() {
		ERDiagram diagram = this.getDiagram();
		PageSetting pageSetting = diagram.getPageSetting();

		org.eclipse.swt.graphics.Rectangle trim = this.getPrinter()
				.computeTrim(0, 0, 0, 0);
		org.eclipse.swt.graphics.Point printerDPI = this.getPrinter().getDPI();

		Insets notAvailable = new Insets(-trim.y, -trim.x,
				trim.height + trim.y, trim.width + trim.x);

		Insets userPreferred = new Insets(
				(pageSetting.getTopMargin() * printerDPI.x) / 72,
				(pageSetting.getLeftMargin() * printerDPI.x) / 72,
				(pageSetting.getBottomMargin() * printerDPI.x) / 72,
				(pageSetting.getRightMargin() * printerDPI.x) / 72);

		Rectangle paperBounds = new Rectangle(this.getPrinter().getBounds());
		Rectangle printRegion = shrink(paperBounds, notAvailable);
		printRegion.intersect(shrink(paperBounds, userPreferred));
		printRegion.translate(trim.x, trim.y);
		
		return printRegion;
	}

	private Rectangle shrink(Rectangle bounds, Insets insets) {
		Rectangle shrinked = bounds.getCopy();

		shrinked.x += insets.left;
		shrinked.y += insets.top;
		shrinked.width -= insets.getWidth();
		shrinked.height -= insets.getHeight();

		return shrinked;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setupPrinterGraphicsFor(Graphics graphics, IFigure figure) {
		ERDiagram diagram = this.getDiagram();
		PageSetting pageSetting = diagram.getPageSetting();

		double dpiScale = (double) getPrinter().getDPI().x
				/ Display.getCurrent().getDPI().x * pageSetting.getScale()
				/ 100;

		Rectangle printRegion = getPrintRegion();
		// put the print region in display coordinates
		printRegion.width /= dpiScale;
		printRegion.height /= dpiScale;

		Rectangle bounds = figure.getBounds();
		double xScale = (double) printRegion.width / bounds.width;
		double yScale = (double) printRegion.height / bounds.height;
		switch (getPrintMode()) {
		case FIT_PAGE:
			graphics.scale(Math.min(xScale, yScale) * dpiScale);
			break;
		case FIT_WIDTH:
			graphics.scale(xScale * dpiScale);
			break;
		case FIT_HEIGHT:
			graphics.scale(yScale * dpiScale);
			break;
		default:
			graphics.scale(dpiScale);
		}
		graphics.setForegroundColor(figure.getForegroundColor());
		graphics.setBackgroundColor(figure.getBackgroundColor());
		graphics.setFont(figure.getFont());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void printPages() {
		Graphics graphics = getFreshPrinterGraphics();
		IFigure figure = getPrintSource();
		setupPrinterGraphicsFor(graphics, figure);
		Rectangle bounds = figure.getBounds();
		int x = bounds.x, y = bounds.y;
		Rectangle clipRect = new Rectangle();
		while (y < bounds.y + bounds.height) {
			while (x < bounds.x + bounds.width) {
				graphics.pushState();
				getPrinter().startPage();
				graphics.translate(-x, -y);
				graphics.getClip(clipRect);
				clipRect.setLocation(x, y);
				graphics.clipRect(clipRect);
				figure.paint(graphics);
				getPrinter().endPage();
				graphics.popState();
				x += clipRect.width;
			}
			x = bounds.x;
			y += clipRect.height;
		}
	}

}
