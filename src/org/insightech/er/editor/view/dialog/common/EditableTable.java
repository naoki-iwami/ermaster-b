package org.insightech.er.editor.view.dialog.common;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

public interface EditableTable {

	public void setData(Point xy, Control control);

	public Control getControl(Point xy);

	public void onDoubleClicked(Point xy);
	
	public boolean validate();
}
