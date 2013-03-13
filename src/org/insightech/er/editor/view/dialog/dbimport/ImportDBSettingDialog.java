package org.insightech.er.editor.view.dialog.dbimport;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.insightech.er.Activator;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.view.dialog.common.AbstractDBSettingDialog;
import org.insightech.er.preference.PreferenceInitializer;

public class ImportDBSettingDialog extends AbstractDBSettingDialog {

	public ImportDBSettingDialog(Shell parentShell, ERDiagram diagram) {
		super(parentShell, diagram);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initialize(Composite parent) {
		super.initialize(parent);
		this.dbSetting = PreferenceInitializer.getDBSetting(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void perfomeOK() throws InputException {
		this.setCurrentSetting();

		Connection con = null;

		try {
			con = this.dbSetting.connect();

		} catch (InputException e) {
			throw e;

		} catch (Exception e) {
			Activator.log(e);
			Throwable cause = e.getCause();

			if (cause instanceof UnknownHostException) {
				throw new InputException("error.server.not.found");
			}

			Activator.showMessageDialog(e.getMessage());
			throw new InputException("error.database.not.found");

		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					Activator.showExceptionDialog(e);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getTitle() {
		return "dialog.title.import.tables";
	}

}
