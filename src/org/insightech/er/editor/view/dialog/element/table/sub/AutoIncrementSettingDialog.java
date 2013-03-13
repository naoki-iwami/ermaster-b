package org.insightech.er.editor.view.dialog.element.table.sub;

import java.math.BigDecimal;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.impl.postgres.PostgresDBManager;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.util.Format;

public class AutoIncrementSettingDialog extends AbstractDialog {

	private Text incrementText;

	private Text minValueText;

	private Text maxValueText;

	private Text startText;

	private Text cacheText;

	private Button cycleCheckBox;

	private Sequence sequence;

	private Sequence result;

	private String database;

	public AutoIncrementSettingDialog(Shell parentShell, Sequence sequence,
			String database) {
		super(parentShell, 2);

		this.sequence = sequence;
		this.database = database;
	}

	@Override
	protected void initialize(Composite composite) {
		this.incrementText = CompositeFactory.createNumText(this, composite,
				"Increment");

		if (PostgresDBManager.ID.equals(this.database)) {
			this.minValueText = CompositeFactory.createNumText(this, composite,
					"MinValue");
			this.maxValueText = CompositeFactory.createNumText(this, composite,
					"MaxValue");
		}

		this.startText = CompositeFactory.createNumText(this, composite,
				"Start");

		if (PostgresDBManager.ID.equals(this.database)) {
			this.cacheText = CompositeFactory.createNumText(this, composite,
					"Cache");
			this.cycleCheckBox = CompositeFactory.createCheckbox(this,
					composite, "Cycle", 2);
		}
	}

	@Override
	protected String getErrorMessage() {
		String text = incrementText.getText();

		if (!text.equals("")) {
			try {
				Integer.parseInt(text);

			} catch (NumberFormatException e) {
				return "error.sequence.increment.degit";
			}
		}

		if (minValueText != null) {
			text = minValueText.getText();

			if (!text.equals("")) {
				try {
					Long.parseLong(text);

				} catch (NumberFormatException e) {
					return "error.sequence.minValue.degit";
				}
			}
		}

		if (maxValueText != null) {
			text = maxValueText.getText();

			if (!text.equals("")) {
				try {
					new BigDecimal(text);

				} catch (NumberFormatException e) {
					return "error.sequence.maxValue.degit";
				}
			}
		}

		text = startText.getText();

		if (!text.equals("")) {
			try {
				Long.parseLong(text);

			} catch (NumberFormatException e) {
				return "error.sequence.start.degit";
			}
		}

		if (cacheText != null) {
			text = cacheText.getText();

			if (!text.equals("")) {
				try {
					Integer.parseInt(text);

				} catch (NumberFormatException e) {
					return "error.sequence.cache.degit";
				}
			}
		}

		return null;
	}

	@Override
	protected String getTitle() {
		return "label.auto.increment.setting";
	}

	@Override
	protected void perfomeOK() throws InputException {
		this.result = new Sequence();

		Integer increment = null;
		Long minValue = null;
		BigDecimal maxValue = null;
		Long start = null;
		Integer cache = null;

		String text = incrementText.getText();
		if (!text.equals("")) {
			increment = Integer.valueOf(text);
		}

		if (minValueText != null) {
			text = minValueText.getText();
			if (!text.equals("")) {
				minValue = Long.valueOf(text);
			}
		}

		if (maxValueText != null) {
			text = maxValueText.getText();
			if (!text.equals("")) {
				maxValue = new BigDecimal(text);
			}
		}

		text = startText.getText();
		if (!text.equals("")) {
			start = Long.valueOf(text);
		}

		if (cacheText != null) {
			text = cacheText.getText();
			if (!text.equals("")) {
				cache = Integer.valueOf(text);
			}
		}

		this.result.setIncrement(increment);
		this.result.setMinValue(minValue);
		this.result.setMaxValue(maxValue);
		this.result.setStart(start);
		this.result.setCache(cache);

		if (cycleCheckBox != null) {
			this.result.setCycle(this.cycleCheckBox.getSelection());
		}
	}

	@Override
	protected void setData() {
		if (this.sequence != null) {
			this.incrementText.setText(Format.toString(this.sequence
					.getIncrement()));
			if (minValueText != null) {
				this.minValueText.setText(Format.toString(this.sequence
						.getMinValue()));
			}
			if (maxValueText != null) {
				this.maxValueText.setText(Format.toString(this.sequence
						.getMaxValue()));
			}
			this.startText.setText(Format.toString(this.sequence.getStart()));
			if (maxValueText != null) {
				this.cacheText.setText(Format
						.toString(this.sequence.getCache()));
			}
			if (cycleCheckBox != null) {
				this.cycleCheckBox.setSelection(this.sequence.isCycle());
			}
		}
	}

	public Sequence getResult() {
		return result;
	}

}
