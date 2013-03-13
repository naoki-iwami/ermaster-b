package org.insightech.er.editor.view.dialog.dbexport;

import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.Activator;
import org.insightech.er.ResourceString;
import org.insightech.er.common.dialog.AbstractDialog;
import org.insightech.er.common.exception.InputException;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.db.ExportToDBManager;
import org.insightech.er.editor.model.settings.DBSetting;
import org.insightech.er.util.Check;

public class ExportToDBDialog extends AbstractDialog {

	private Text textArea;

	private DBSetting dbSetting;

	private String ddl;

	public ExportToDBDialog(Shell parentShell, ERDiagram diagram,
			DBSetting dbSetting, String ddl) {
		super(parentShell);

		this.dbSetting = dbSetting;
		this.ddl = ddl;
	}

	@Override
	protected void initialize(Composite composite) {
		this.textArea = CompositeFactory.createTextArea(null, composite,
				"dialog.message.export.db.sql", 600, 400, 1, false, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		this.createButton(parent, IDialogConstants.OK_ID, ResourceString
				.getResourceString("label.button.execute"), true);
	}

	@Override
	protected String getErrorMessage() {
		if ("".equals(this.textArea.getText().trim())) {
			return "";
		}

		return null;
	}

	@Override
	protected String getTitle() {
		return "dialog.title.export.db";
	}

	@Override
	protected void perfomeOK() throws InputException {
		String executeDDL = this.textArea.getSelectionText();
		if (Check.isEmpty(executeDDL)) {
			executeDDL = this.textArea.getText();
		}

		if (!Activator.showConfirmDialog("dialog.message.export.db.confirm")) {
			return;
		}

		Connection con = null;

		try {
			con = this.dbSetting.connect();

			ProgressMonitorDialog dialog = new ProgressMonitorDialog(this
					.getShell());

			ExportToDBManager exportToDBManager = new ExportToDBManager();
			exportToDBManager.init(con, executeDDL);

			try {
				dialog.run(true, true, exportToDBManager);

				Exception e = exportToDBManager.getException();
				if (e != null) {
					Activator.showMessageDialog(e.getMessage());
					throw new InputException(null);

				} else {
					Activator
							.showMessageDialog("dialog.message.export.db.finish");
				}

			} catch (InvocationTargetException e) {
			} catch (InterruptedException e) {
			}

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

	@Override
	protected void setData() {
		this.textArea.setText(this.ddl);
	}

}
