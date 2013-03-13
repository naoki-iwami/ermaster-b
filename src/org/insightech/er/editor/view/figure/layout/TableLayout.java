package org.insightech.er.editor.view.figure.layout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.AbstractHintLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

public class TableLayout extends AbstractHintLayout {

	private int colnum;

	private int separatorWidth;

	private List<IFigure> separators;

	public TableLayout(int colnum) {
		super();

		this.colnum = colnum;
		if (this.colnum <= 0) {
			this.colnum = 1;
		}

		this.separators = new ArrayList<IFigure>();
		this.separatorWidth = 1;
	}

	public void setSeparator() {

	}

	public void layout(IFigure parent) {

		List children = this.clearSeparator(parent);

		List<List<IFigure>> table = this.getTable(children);
		int[] columnWidth = this.getColumnWidth(table);
		int[] rowHeight = this.getRowHeight(table);

		Rectangle rect = parent.getBounds();

		int x = rect.x + 1;
		int y = rect.y + 1;

		for (int i = 0; i < table.size(); i++) {
			List<IFigure> tableRow = table.get(i);

			for (int j = 0; j < tableRow.size(); j++) {
				Rectangle childRect = new Rectangle(x, y, columnWidth[j],
						rowHeight[i]);

				IFigure figure = tableRow.get(j);
				figure.setBounds(childRect);

				x += columnWidth[j];

				if (j != tableRow.size() - 1) {
					Rectangle separetorRect = new Rectangle(x, y,
							separatorWidth, rowHeight[i]);
					this.addVerticalSeparator(parent, separetorRect);

					x += separatorWidth;
				}

			}

			x = rect.x + 1;
			y += rowHeight[i];

			if (i != table.size() - 1) {
				Rectangle separetorRect = new Rectangle(x, y, rect.width,
						separatorWidth);

				this.addHorizontalSeparator(parent, separetorRect);

				y += separatorWidth;
			}
		}
	}

	private List<List<IFigure>> getTable(List children) {
		int numChildren = children.size();

		List<List<IFigure>> table = new ArrayList<List<IFigure>>();

		List<IFigure> row = null;

		for (int i = 0; i < numChildren; i++) {
			if (i % colnum == 0) {
				row = new ArrayList<IFigure>();
				table.add(row);
			}

			row.add((IFigure) children.get(i));
		}

		return table;
	}

	private int[] getColumnWidth(List<List<IFigure>> table) {
		int[] columnWidth = new int[this.colnum];

		for (int i = 0; i < colnum; i++) {
			for (List<IFigure> tableRow : table) {
				if (tableRow.size() > i) {
					IFigure figure = tableRow.get(i);

					int width = figure.getPreferredSize().width;

					if (width > columnWidth[i]) {
						columnWidth[i] = (int) (width * 1.3);
					}
				}
			}
		}

		return columnWidth;
	}

	private int[] getRowHeight(List<List<IFigure>> table) {
		int[] rowHeight = new int[table.size()];

		for (int i = 0; i < rowHeight.length; i++) {
			for (IFigure cell : table.get(i)) {
				int height = cell.getPreferredSize().height;

				if (height > rowHeight[i]) {
					rowHeight[i] = height;
				}
			}
		}

		return rowHeight;
	}

	private List<IFigure> getChildren(IFigure parent) {
		List<IFigure> children = new ArrayList<IFigure>();

		for (Iterator iter = parent.getChildren().iterator(); iter.hasNext();) {
			IFigure child = (IFigure) iter.next();

			if (!this.separators.contains(child)) {
				children.add(child);
			}
		}

		return children;
	}

	private List clearSeparator(IFigure parent) {
		for (Iterator iter = parent.getChildren().iterator(); iter.hasNext();) {
			IFigure child = (IFigure) iter.next();

			if (this.separators.contains(child)) {
				iter.remove();
			}
		}

		this.separators.clear();

		return parent.getChildren();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Dimension calculatePreferredSize(IFigure container, int wHint,
			int hHint) {
		List children = this.getChildren(container);

		List<List<IFigure>> table = this.getTable(children);
		int[] columnWidth = this.getColumnWidth(table);
		int[] rowHeight = this.getRowHeight(table);

		int width = 0;
		for (int i = 0; i < columnWidth.length; i++) {
			width += columnWidth[i];
			if (i != columnWidth.length - 1) {
				width += this.separatorWidth;
			}
		}
		width++;
		width++;

		int height = 0;
		for (int i = 0; i < rowHeight.length; i++) {
			height += rowHeight[i];
			if (i != rowHeight.length - 1) {
				height += this.separatorWidth;
			}
		}
		height++;
		height++;

		return new Dimension(width, height);
	}

	@SuppressWarnings("unchecked")
	private void addVerticalSeparator(IFigure figure, Rectangle rect) {
		Polyline separator = new Polyline();
		separator.setLineWidth(separatorWidth);
		separator.addPoint(new Point(rect.x, rect.y));
		separator.addPoint(new Point(rect.x, rect.y + rect.height));

		figure.getChildren().add(separator);
		separator.setParent(figure);

		this.separators.add(separator);
	}

	@SuppressWarnings("unchecked")
	private void addHorizontalSeparator(IFigure figure, Rectangle rect) {
		Polyline separator = new Polyline();
		separator.setLineWidth(separatorWidth);
		separator.addPoint(new Point(rect.x, rect.y));
		separator.addPoint(new Point(rect.x + rect.width, rect.y));
		figure.getChildren().add(separator);
		separator.setParent(figure);

		this.separators.add(separator);
	}

}
