package org.insightech.er;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class Resources {

	public static final int BUTTON_WIDTH = 60;
	
	public static final int DESCRIPTION_WIDTH = 400;

	public static Color PINK = new Color(Display.getCurrent(), 255, 0, 255);

	public static Color ADDED_COLOR = new Color(Display.getCurrent(), 128, 128,
			255);

	public static Color UPDATED_COLOR = new Color(Display.getCurrent(), 128,
			255, 128);

	public static Color REMOVED_COLOR = new Color(Display.getCurrent(), 255,
			128, 128);

	public static Color GRID_COLOR = new Color(Display.getCurrent(), 220, 220,
			255);

	public static Color DEFAULT_TABLE_COLOR = new Color(Display.getCurrent(),
			128, 128, 192);

	public static Color SELECTED_REFERENCED_COLUMN = new Color(Display
			.getCurrent(), 255, 230, 230);

	public static Color SELECTED_FOREIGNKEY_COLUMN = new Color(Display
			.getCurrent(), 230, 255, 230);

	public static Color SELECTED_REFERENCED_AND_FOREIGNKEY_COLUMN = new Color(
			Display.getCurrent(), 230, 230, 255);

	public static Color VERY_LIGHT_GRAY = new Color(Display.getCurrent(), 230,
			230, 230);

	public static Color LINE_COLOR = new Color(Display.getCurrent(), 180, 180,
			255);

	public static Color TEST_COLOR = new Color(Display.getCurrent(), 230, 230,
			230);

	public static final Color PRIMARY_COLOR = new Color(Display.getCurrent(),
			252, 250, 167);

	public static final Color FOREIGN_COLOR = new Color(Display.getCurrent(),
			211, 231, 245);

	public static final Color NOT_NULL_COLOR = new Color(Display.getCurrent(),
			254, 228, 207);

	private static Map<Integer, Color> colorMap = new HashMap<Integer, Color>();

	public static Color getColor(int[] rgb) {
		int key = rgb[0] * 1000000 + rgb[1] * 1000 + rgb[2];

		Color color = colorMap.get(key);

		if (color != null) {
			return color;
		}

		color = new Color(Display.getCurrent(), rgb[0], rgb[1], rgb[2]);
		colorMap.put(key, color);

		return color;
	}

	public static void disposeColorMap() {
		for (Color color : colorMap.values()) {
			if (!color.isDisposed()) {
				color.dispose();
			}
		}
	}

}
