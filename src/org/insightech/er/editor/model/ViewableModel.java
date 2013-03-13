package org.insightech.er.editor.model;



public abstract class ViewableModel extends AbstractModel {

	private static final long serialVersionUID = 5866202173090969615L;

	public static final String PROPERTY_CHANGE_COLOR = "color";

	public static final String PROPERTY_CHANGE_FONT = "font";

	public static final int DEFAULT_FONT_SIZE = 9;

	private String fontName;

	private int fontSize;

	private int[] color;

	public ViewableModel() {
		this.fontName = null;
		this.fontSize = DEFAULT_FONT_SIZE;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
		this.firePropertyChange(PROPERTY_CHANGE_FONT, null, null);
	}

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;		
		this.firePropertyChange(PROPERTY_CHANGE_FONT, null, null);
	}

	public void setColor(int red, int green, int blue) {
		this.color = new int[3];
		this.color[0] = red;
		this.color[1] = green;
		this.color[2] = blue;

		this.firePropertyChange(PROPERTY_CHANGE_COLOR, null, null);
	}

	public int[] getColor() {
		return this.color;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ViewableModel clone() {
		ViewableModel clone = (ViewableModel) super.clone();
		if (this.color != null) {
			clone.color = new int[] { this.color[0], this.color[1],
					this.color[2] };
		}

		return clone;
	}
}
