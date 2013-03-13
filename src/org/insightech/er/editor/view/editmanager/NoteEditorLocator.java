package org.insightech.er.editor.view.editmanager;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.editor.view.figure.NoteFigure;

public class NoteEditorLocator implements CellEditorLocator {

	private IFigure figure;

	public NoteEditorLocator(IFigure figure) {
		this.figure = figure;
	}

	public void relocate(CellEditor cellEditor) {
		Text text = (Text) cellEditor.getControl();

		Rectangle rect = this.figure.getBounds().getCopy();
		this.figure.translateToAbsolute(rect);

		text.setBounds(rect.x + NoteFigure.RETURN_WIDTH, rect.y
				+ NoteFigure.RETURN_WIDTH, rect.width - NoteFigure.RETURN_WIDTH
				* 2, rect.height - NoteFigure.RETURN_WIDTH * 2);
	}

}
