package org.insightech.er.editor.model.dbexport.db;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.insightech.er.Activator;
import org.insightech.er.ResourceString;

public class ExportToDBManager implements IRunnableWithProgress {

	private static Logger logger = Logger.getLogger(ExportToDBManager.class
			.getName());

	protected Connection con;

	private String ddl;

	private Exception exception;

	private String errorSql;

	public ExportToDBManager() {
	}

	public void init(Connection con, String ddl) throws SQLException {
		this.con = con;
		this.con.setAutoCommit(false);
		this.ddl = ddl;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		try {
			String[] ddls = ddl.split(";[\r\n]+");

			monitor.beginTask(ResourceString
					.getResourceString("dialog.message.drop.table"),
					ddls.length);

			for (int i = 0; i < ddls.length; i++) {
				String message = ddls[i];
				int index = message.indexOf("\r\n");
				if (index != -1) {
					message = message.substring(0, index);
				}

				monitor.subTask("(" + (i + 1) + "/" + ddls.length + ") "
						+ message);

				this.executeDDL(ddls[i]);
				monitor.worked(1);

				if (monitor.isCanceled()) {
					throw new InterruptedException("Cancel has been requested.");
				}
			}

			this.con.commit();

		} catch (InterruptedException e) {
			throw e;

		} catch (Exception e) {
			this.exception = e;
		}

		monitor.done();
	}

	private void executeDDL(String ddl) throws SQLException {
		Statement stmt = null;

		try {
			logger.info(ddl);
			stmt = this.con.createStatement();
			stmt.execute(ddl);

		} catch (SQLException e) {
			Activator.log(e);
			this.errorSql = ddl;
			throw e;

		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public Exception getException() {
		return exception;
	}

	public String getErrorSql() {
		return errorSql;
	}

}
