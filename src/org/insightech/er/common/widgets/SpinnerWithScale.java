package org.insightech.er.common.widgets;

import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;

public class SpinnerWithScale {

	private Spinner spinner;

	private Scale scale;

	private int diff;

	public SpinnerWithScale(Spinner spinner, Scale scale, int diff) {
		this.spinner = spinner;
		this.scale = scale;
		this.diff = diff;
	}

	public void setSelection(int value) {
		this.spinner.setSelection(value);
		this.scale.setSelection(this.spinner.getSelection() - diff);
	}

	public int getSelection() {
		return this.spinner.getSelection();
	}
	
}
