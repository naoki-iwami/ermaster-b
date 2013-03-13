package org.insightech.er.common.widgets;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class IntegerDocument extends PlainDocument {

	private static final long serialVersionUID = 2217237305506835428L;

	private int currentValue = 0;

	public IntegerDocument() {
		super();
	}

	public int getValue() {
		return currentValue;
	}

	@Override
	public void insertString(int offset, String str, AttributeSet attributes)
			throws BadLocationException {
		if (str == null) {
			return;
		} else {
			String newValue;
			int length = getLength();
			if (length == 0) {
				newValue = str;
			} else {
				String currentContent = getText(0, length);
				StringBuilder currentBuffer = new StringBuilder(currentContent);
				currentBuffer.insert(offset, str);
				newValue = currentBuffer.toString();
			}
			currentValue = checkInput(newValue, offset);
			super.insertString(offset, str, attributes);
		}
	}

	@Override
	public void remove(int offset, int length) throws BadLocationException {
		int currentLength = getLength();
		String currentContent = getText(0, currentLength);
		String before = currentContent.substring(0, offset);
		String after = currentContent.substring(length + offset, currentLength);
		String newValue = before + after;
		currentValue = checkInput(newValue, offset);
		super.remove(offset, length);
	}

	private int checkInput(String proposedValue, int offset)
			throws BadLocationException {
		if (proposedValue.length() > 0) {
			try {
				int newValue = Integer.parseInt(proposedValue);
				return newValue;
			} catch (NumberFormatException e) {
				throw new BadLocationException(proposedValue, offset);
			}
		} else {
			return 0;
		}
	}
}