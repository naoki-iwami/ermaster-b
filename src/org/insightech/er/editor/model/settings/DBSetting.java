package org.insightech.er.editor.model.settings;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import org.insightech.er.common.exception.InputException;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.util.Format;

public class DBSetting implements Serializable, Comparable<DBSetting> {

	private static final long serialVersionUID = 1753636902211017613L;

	private String dbsystem;

	private String server;

	private int port;

	private String database;

	private String user;

	private transient String password;

	private boolean useDefaultDriver;

	private String url;

	private String driverClassName;

	public String getDbsystem() {
		return dbsystem;
	}

	public DBSetting(String dbsystem, String server, int port, String database,
			String user, String password, boolean useDefaultDriver, String url,
			String driverClassName) {
		this.dbsystem = dbsystem;
		this.server = server;
		this.port = port;
		this.database = database;
		this.user = user;
		this.password = password;
		this.useDefaultDriver = useDefaultDriver;
		this.url = url;
		this.driverClassName = driverClassName;
	}

	public String getServer() {
		return server;
	}

	public int getPort() {
		return port;
	}

	public String getDatabase() {
		return database;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public boolean isUseDefaultDriver() {
		return useDefaultDriver;
	}

	public String getUrl() {
		return url;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public int compareTo(DBSetting other) {
		int compareTo = this.getDbsystem().compareTo(other.getDbsystem());
		if (compareTo != 0) {
			return compareTo;
		}

		compareTo = this.getServer().compareTo(other.getServer());
		if (compareTo != 0) {
			return compareTo;
		}

		if (this.getPort() != other.getPort()) {
			return this.getPort() - other.getPort();
		}

		compareTo = this.getDatabase().compareTo(other.getDatabase());
		if (compareTo != 0) {
			return compareTo;
		}

		compareTo = this.getUser().compareTo(other.getUser());
		if (compareTo != 0) {
			return compareTo;
		}

		compareTo = this.getServer().compareTo(other.getServer());
		if (compareTo != 0) {
			return compareTo;
		}

		compareTo = this.getPassword().compareTo(other.getPassword());
		if (compareTo != 0) {
			return compareTo;
		}

		if (this.isUseDefaultDriver() != other.isUseDefaultDriver()) {
			if (this.isUseDefaultDriver()) {
				return -1;
			} else {
				return 1;
			}
		}

		compareTo = this.getUrl().compareTo(other.getUrl());
		if (compareTo != 0) {
			return compareTo;
		}

		compareTo = this.getDriverClassName().compareTo(
				other.getDriverClassName());
		if (compareTo != 0) {
			return compareTo;
		}

		return 0;
	}

	public String getTableNameWithSchema(String tableName, String schema) {
		if (schema == null) {
			return Format.null2blank(tableName);
		}

		DBManager dbManager = DBManagerFactory.getDBManager(this.dbsystem);

		if (!dbManager.isSupported(DBManager.SUPPORT_SCHEMA)) {
			return Format.null2blank(tableName);
		}

		return schema + "." + Format.null2blank(tableName);
	}

	public Connection connect() throws InputException, InstantiationException,
			IllegalAccessException, SQLException {
		DBManager manager = DBManagerFactory.getDBManager(this.getDbsystem());
		Class<Driver> driverClass = manager.getDriverClass(this
				.getDriverClassName());

		if (driverClass == null) {
			throw new InputException("error.jdbc.driver.not.found");
		}

		Driver driver = driverClass.newInstance();

		Properties info = new Properties();
		if (this.getUser() != null) {
			info.put("user", this.getUser());
		}
		if (this.getPassword() != null) {
			info.put("password", this.getPassword());
		}

		return driver.connect(this.getUrl(), info);
	}
}
