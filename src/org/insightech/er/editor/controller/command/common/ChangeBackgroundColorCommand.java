package org.insightech.er.editor.controller.command.common;

import org.insightech.er.editor.controller.command.AbstractCommand;
import org.insightech.er.editor.model.ViewableModel;

public class ChangeBackgroundColorCommand extends AbstractCommand {

	private ViewableModel model;

	private int red;

	private int green;

	private int blue;

	private int[] oldColor;

	public ChangeBackgroundColorCommand(ViewableModel model, int red,
			int green, int blue) {
		this.model = model;

		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doExecute() {
		this.oldColor = this.model.getColor();

		this.model.setColor(red, green, blue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doUndo() {
		if (this.oldColor == null) {
			this.oldColor = new int[3];
			this.oldColor[0] = 255;
			this.oldColor[1] = 255;
			this.oldColor[2] = 255;
		}

		this.model.setColor(this.oldColor[0], this.oldColor[1],
				this.oldColor[2]);
	}
}
