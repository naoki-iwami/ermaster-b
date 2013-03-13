package org.insightech.er.editor.model.diagram_contents.not_element.dictionary;

public class CopyWord extends Word {

	private static final long serialVersionUID = 5610038803601000225L;

	private Word original;

	public CopyWord(Word original) {
		super(original);
		this.original = original;
	}

	public Word restructure(Dictionary dictionary) {
		dictionary.copyTo(this, this.original);
		return this.original;
	}

	public Word getOriginal() {
		return original;
	}

	@Override
	public void copyTo(Word to) {
		super.copyTo(to);
		if (to instanceof CopyWord) {
			((CopyWord) to).original = this.original;
		}
	}

	public void setOriginal(Word original) {
		this.original = original;
	}

	
}
