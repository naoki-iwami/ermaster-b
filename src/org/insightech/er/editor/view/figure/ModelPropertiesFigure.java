package org.insightech.er.editor.view.figure;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.graphics.Color;
import org.insightech.er.ResourceString;
import org.insightech.er.editor.view.figure.layout.TableLayout;
import org.insightech.er.util.NameValue;

public class ModelPropertiesFigure extends RectangleFigure {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");

	private static final long serialVersionUID = 7613144432550730126L;

	private Color foregroundColor;

	public ModelPropertiesFigure() {
		TableLayout layout = new TableLayout(2);

		this.setLayoutManager(layout);
	}

	private void addRow(String name, String value, String tableStyle) {
		Border border = new MarginBorder(5);

		ToolbarLayout layout = new ToolbarLayout();
		layout.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
		layout.setStretchMinorAxis(true);

		Label nameLabel = new Label();

		Label valueLabel = new Label();

		nameLabel.setBorder(border);
		nameLabel.setText(name);
		nameLabel.setLabelAlignment(PositionConstants.LEFT);
		nameLabel.setForegroundColor(this.foregroundColor);

		this.add(nameLabel);

		if (!ResourceString.getResourceString(
				"action.title.change.design.simple").equals(tableStyle)
				&& !ResourceString.getResourceString(
						"action.title.change.design.frame").equals(tableStyle)) {
			valueLabel.setBackgroundColor(ColorConstants.white);
			valueLabel.setOpaque(true);
			valueLabel.setForegroundColor(ColorConstants.black);

		} else {
			valueLabel.setOpaque(false);
			valueLabel.setForegroundColor(this.foregroundColor);
		}

		valueLabel.setBorder(border);
		valueLabel.setText(value);
		valueLabel.setLabelAlignment(PositionConstants.LEFT);

		this.add(valueLabel);
	}

	public void setData(List<NameValue> properties, Date creationDate,
			Date updatedDate, String tableStyle, int[] color) {
		this.removeAll();

		this.decideColor(color);

		for (NameValue property : properties) {
			this.addRow(property.getName(), property.getValue(), tableStyle);
		}

		this.addRow(ResourceString.getResourceString("label.creation.date"),
				DATE_FORMAT.format(creationDate), tableStyle);
		this.addRow(ResourceString.getResourceString("label.updated.date"),
				DATE_FORMAT.format(updatedDate), tableStyle);
	}

	private void decideColor(int[] color) {
		if (color != null) {
			int sum = color[0] + color[1] + color[2];

			if (sum > 255) {
				this.foregroundColor = ColorConstants.black;
			} else {
				this.foregroundColor = ColorConstants.white;
			}
		}
	}
}
