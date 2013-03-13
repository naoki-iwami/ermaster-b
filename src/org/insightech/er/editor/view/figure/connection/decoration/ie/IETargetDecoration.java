package org.insightech.er.editor.view.figure.connection.decoration.ie;

import org.eclipse.draw2d.geometry.PointList;
import org.insightech.er.editor.view.figure.connection.decoration.ERDecoration;

public class IETargetDecoration extends ERDecoration {

	public IETargetDecoration() {
		super();

		PointList pointList = new PointList();

		pointList.addPoint(-13, -12);
		pointList.addPoint(-13, 0);
		pointList.addPoint(-1, -12);
		pointList.addPoint(-13, 0);
		pointList.addPoint(-1, 12);
		pointList.addPoint(-13, 0);
		pointList.addPoint(-13, 12);

		this.setTemplate(pointList);
		this.setScale(0.66, 0.66);
//		this.setScale(1, 1);
	}

}
