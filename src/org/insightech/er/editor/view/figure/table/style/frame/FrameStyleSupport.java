package org.insightech.er.editor.view.figure.table.style.frame;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.TitleBarBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Font;
import org.insightech.er.editor.view.figure.table.IndexFigure;
import org.insightech.er.editor.view.figure.table.TableFigure;
import org.insightech.er.editor.view.figure.table.column.NormalColumnFigure;
import org.insightech.er.editor.view.figure.table.style.AbstractStyleSupport;

public class FrameStyleSupport extends AbstractStyleSupport {

	private ImageFrameBorder border;

	private TitleBarBorder titleBarBorder;

	public FrameStyleSupport(TableFigure tableFigure) {
		super(tableFigure);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(TableFigure tableFigure) {
		this.border = new ImageFrameBorder();
		this.border.setFont(tableFigure.getFont());

		tableFigure.setBorder(this.border);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initTitleBar(Figure top) {
		this.titleBarBorder = (TitleBarBorder) this.border.getInnerBorder();
		this.titleBarBorder.setTextAlignment(PositionConstants.CENTER);
		this.titleBarBorder.setPadding(new Insets(5, 20, 5, 20));
	}

	public void setName(String name) {
		this.titleBarBorder.setTextColor(this.getTextColor());
		this.titleBarBorder.setLabel(name);
	}

	public void setFont(Font font, Font titleFont) {
		this.titleBarBorder.setFont(titleFont);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void adjustBounds(Rectangle rect) {
		int width = this.border.getTitleBarWidth(this.getTableFigure());

		if (width > rect.width) {
			rect.width = width;
		}
	}

	public void addColumn(NormalColumnFigure columnFigure, int viewMode,
			String physicalName, String logicalName, String type,
			boolean primaryKey, boolean foreignKey, boolean isNotNull,
			boolean uniqueKey, boolean displayKey, boolean displayDetail,
			boolean displayType, boolean isSelectedReferenced,
			boolean isSelectedForeignKey, boolean isAdded, boolean isUpdated,
			boolean isRemoved) {

		Label label = this.createColumnLabel();

		label.setForegroundColor(this.getTextColor());

		StringBuilder text = new StringBuilder();
		text.append(this.getColumnText(viewMode, physicalName, logicalName,
				type, isNotNull, uniqueKey, displayDetail, displayType));

		if (displayKey) {
			if (primaryKey && foreignKey) {
				label.setForegroundColor(ColorConstants.blue);

				text.append(" ");
				text.append("(PFK)");

			} else if (primaryKey) {
				label.setForegroundColor(ColorConstants.red);

				text.append(" ");
				text.append("(PK)");

			} else if (foreignKey) {
				label.setForegroundColor(ColorConstants.darkGreen);

				text.append(" ");
				text.append("(FK)");
			}
		}

		this.setColumnFigureColor(columnFigure, isSelectedReferenced,
				isSelectedForeignKey, isAdded, isUpdated, isRemoved);

		label.setText(text.toString());

		columnFigure.add(label);
	}

	public void addIndex(IndexFigure indexFigure, String name, boolean isFirst) {
		// TODO Auto-generated method stub
		
	}
}
