package org.insightech.er.editor.view.figure.table.style.simple;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.graphics.Font;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.view.figure.table.IndexFigure;
import org.insightech.er.editor.view.figure.table.TableFigure;
import org.insightech.er.editor.view.figure.table.column.NormalColumnFigure;
import org.insightech.er.editor.view.figure.table.style.AbstractStyleSupport;

public class SimpleStyleSupport extends AbstractStyleSupport {

	private Label nameLabel;

	public SimpleStyleSupport(TableFigure tableFigure, Settings settings) {
		super(tableFigure, settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(TableFigure tableFigure) {
		tableFigure.setCornerDimensions(new Dimension(10, 10));
		tableFigure.setBorder(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initTitleBar(Figure top) {
		ToolbarLayout topLayout = new ToolbarLayout();

		topLayout.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
		topLayout.setStretchMinorAxis(true);
		top.setLayoutManager(topLayout);

		this.nameLabel = new Label();
		this.nameLabel.setBorder(new MarginBorder(new Insets(5, 20, 5, 20)));
		top.add(nameLabel);

		Figure separater = new Figure();
		separater.setSize(-1, 1);
		separater.setBackgroundColor(this.getTextColor());
		separater.setOpaque(true);

		top.add(separater);
	}

	public void setName(String name) {
		this.nameLabel.setForegroundColor(this.getTextColor());
		this.nameLabel.setText(name);
	}

	public void setFont(Font font, Font titleFont) {
		this.nameLabel.setFont(titleFont);
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

		label.setText(text.toString());

		this.setColumnFigureColor(columnFigure, isSelectedReferenced,
				isSelectedForeignKey, isAdded, isUpdated, isRemoved);

		columnFigure.add(label);
	}

	public void addIndex(IndexFigure indexFigure, String name, boolean isFirst) {
		// TODO Auto-generated method stub

	}
}
