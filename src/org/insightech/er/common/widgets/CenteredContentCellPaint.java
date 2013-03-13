package org.insightech.er.common.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class CenteredContentCellPaint implements Listener {
	private int colIndex;

	public CenteredContentCellPaint(Table tbl, int colIndex) {
		this.colIndex = colIndex;
		tbl.addListener(SWT.EraseItem, this);
		tbl.addListener(SWT.PaintItem, this);
	}

	public void handleEvent(Event event) {
		if (event.index == colIndex) {
			if (event.type == SWT.EraseItem) {
				event.detail &= (Integer.MAX_VALUE ^ SWT.FOREGROUND);
				
			} else if (event.type == SWT.PaintItem) {
				TableItem item = (TableItem) event.item;
				Image img = item.getImage(colIndex);
				if (img != null) {
					Rectangle size = img.getBounds();
					Table tbl = (Table) event.widget;
					event.gc.drawImage(img, event.x
							+ (tbl.getColumn(colIndex).getWidth() - size.width)
							/ 2, event.y + (tbl.getItemHeight() - size.height)
							/ 2);
				}
			}
		}
	}
}