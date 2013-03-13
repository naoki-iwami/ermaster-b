package org.insightech.er.editor.view.figure.connection.decoration.idef1x;

import org.eclipse.draw2d.geometry.PointList;
import org.insightech.er.editor.view.figure.connection.decoration.ERDecoration;

public class IDEF1XOneDecoration extends ERDecoration {

	public IDEF1XOneDecoration() {
		super();

		PointList pointList = new PointList();

		this.setTemplate(pointList);
		this.setScale(1, 1);
	}

}
