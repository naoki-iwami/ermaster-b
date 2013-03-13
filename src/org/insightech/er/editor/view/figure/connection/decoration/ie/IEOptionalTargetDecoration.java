package org.insightech.er.editor.view.figure.connection.decoration.ie;

import org.eclipse.draw2d.geometry.PointList;
import org.insightech.er.editor.view.figure.connection.decoration.ERDecoration;

public class IEOptionalTargetDecoration extends ERDecoration {

	public IEOptionalTargetDecoration() {
		super();

		PointList pointList = new PointList();

		pointList.addPoint(-30, 2);
		pointList.addPoint(-30, -2);
		pointList.addPoint(-29, -3);
		pointList.addPoint(-29, -4);
		pointList.addPoint(-27, -6);
		pointList.addPoint(-26, -6);
		pointList.addPoint(-25, -7);
		pointList.addPoint(-21, -7);
		pointList.addPoint(-20, -6);
		pointList.addPoint(-19, -6);
		pointList.addPoint(-17, -4);
		pointList.addPoint(-17, -3);
		pointList.addPoint(-16, -2);

		pointList.addPoint(-16, 0);
		pointList.addPoint(-13, 0);
		pointList.addPoint(-1, -12);
		pointList.addPoint(-13, 0);
		pointList.addPoint(-1, 12);
		pointList.addPoint(-13, 0);
		pointList.addPoint(-16, 0);

		pointList.addPoint(-16, 2);
		pointList.addPoint(-17, 3);
		pointList.addPoint(-17, 4);
		pointList.addPoint(-19, 6);
		pointList.addPoint(-20, 6);
		pointList.addPoint(-21, 7);
		pointList.addPoint(-25, 7);
		pointList.addPoint(-26, 6);
		pointList.addPoint(-27, 6);
		pointList.addPoint(-29, 4);
		pointList.addPoint(-29, 3);
		pointList.addPoint(-30, 2);
		pointList.addPoint(-30, -2);

		this.setTemplate(pointList);
		this.setScale(0.66, 0.66);
//		this.setScale(1, 1);
	}
}
