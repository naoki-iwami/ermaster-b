package org.insightech.er.common.widgets;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.Activator;

public class InnerDirectoryText {

	private Text text;

	private Button openBrowseButton;

	public InnerDirectoryText(Composite parent, int style) {
		this.text = new Text(parent, style);

		this.openBrowseButton = new Button(parent, SWT.NONE);
		this.openBrowseButton.setText(JFaceResources.getString("openBrowse"));

		this.openBrowseButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				String saveFilePath = Activator.showDirectoryDialogInternal(text.getText());
				text.setText(saveFilePath);
			}
		});
	}

	public void setLayoutData(Object layoutData) {
		this.text.setLayoutData(layoutData);
	}

	public void setText(String text) {
		this.text.setText(text);
		this.text.setSelection(text.length());
	}

	public boolean isBlank() {
		if (this.text.getText().trim().length() == 0) {
			return true;
		}

		return false;
	}

	public String getFilePath() {
		return this.text.getText().trim();
	}

	public void addModifyListener(ModifyListener listener) {
		this.text.addModifyListener(listener);
	}

}
