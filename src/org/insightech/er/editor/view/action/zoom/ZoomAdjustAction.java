package org.insightech.er.editor.view.action.zoom;

import org.eclipse.gef.Disposable;
import org.eclipse.gef.editparts.ZoomListener;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.jface.action.Action;
import org.insightech.er.Activator;
import org.insightech.er.ImageKey;
import org.insightech.er.ResourceString;

public class ZoomAdjustAction extends Action implements ZoomListener,
		Disposable {

	public static final String ID = ZoomAdjustAction.class.getName();

	protected ZoomManager zoomManager;

	public ZoomAdjustAction(ZoomManager zoomManager) {
		super(ResourceString.getResourceString("action.title.zoom.adjust"),
				Activator.getImageDescriptor(ImageKey.ZOOM_ADJUST));
		this.zoomManager = zoomManager;
		zoomManager.addZoomListener(this);

		setToolTipText(ResourceString
				.getResourceString("action.title.zoom.adjust"));
		setId(ID);
	}

	public void dispose() {
		this.zoomManager.removeZoomListener(this);
	}

	@Override
	public void run() {
		this.zoomManager.setZoomAsText(ZoomManager.FIT_ALL);
	}

	public void zoomChanged(double zoom) {
		setEnabled(true);
	}

}
