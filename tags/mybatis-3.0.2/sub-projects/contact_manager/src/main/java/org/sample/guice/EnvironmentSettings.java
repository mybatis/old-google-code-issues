package org.sample.guice;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.ibatis.io.Resources;

import java.util.Properties;

public class EnvironmentSettings {

	Database database = new Database();

	public EnvironmentSettings(String resource) {
		try {
			Properties properties = Resources.getResourceAsProperties(resource);
			BeanUtils.copyProperties(this, properties);
		} catch (Exception e) {
			throw new RuntimeException(e.getLocalizedMessage(), e);
		}
	}

	public EnvironmentSettings() {
		this("environment.properties");
	}

	public static class Database {
		String user;
		String password;
		String serverName;
		String databaseName;

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getServerName() {
			return serverName;
		}

		public void setServerName(String serverName) {
			this.serverName = serverName;
		}

		public String getDatabaseName() {
			return databaseName;
		}

		public void setDatabaseName(String databaseName) {
			this.databaseName = databaseName;
		}
	}

	public Database getDatabase() {
		return database;
	}

}
