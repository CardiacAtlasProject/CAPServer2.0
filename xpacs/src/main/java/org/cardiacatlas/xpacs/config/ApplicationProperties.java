package org.cardiacatlas.xpacs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to JHipster.
 *
 * <p>
 *     Properties are configured in the application.yml file.
 * </p>
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

	/*
	 * Avan's note: see https://github.com/jhipster/jhipster/blob/master/src/main/java/io/github/jhipster/config/JHipsterProperties.java
	 * for example how to create nested properties.
	 *
	 * For simple properties directly under 'application:' heading you can just create a simple variable with set/get function,
	 * e.g. for creating properties such as
	 *  application:
	 *     name: "MyApplicationName"
	 *
	 * --------------------------------------------------------------
	 *    private String name = "DefaultName"
	 *
	 *    public String getName() { return this.name; }
	 *    public void setName( String _name ) { this.name = _name; }
	 * --------------------------------------------------------------
	 *
	 * For a nested property, you need to create subclasses, e.g. to create
	 *   application:
	 *      identity:
	 *         name: "MyApplicationIdentityName"
	 *
	 * --------------------------------------------------------------
	 *    private final Identity identity = new Identity();
	 *
	 *    public Identity getIdentity() { return this.identity; }
	 *    public static class Identity {
	 *       private String name = "DefaultName";
	 *       public String getName() { return this.name; }
	 *       public void setName( String _name ) { this.name = _name; }
     *    }
	 * --------------------------------------------------------------
	 *
	 */


	// Configuration of the DCM4CHEE PACSDB server
	// NOTE: I cannot make dcm4chee as the name of the properties ??
	private final Pacsdb pacsdb = new Pacsdb();

	public Pacsdb getPacsdb() {
		return pacsdb;
	}

	public static class Pacsdb {

		private boolean initStart = false;
		private String url;
		private String pacsAet;
		private String pacsHostname;
		private int pacsPort;
		private String tmpDir;
		private String jdbcUrl;
		private String jdbcUsername;
		private String jdbcPassword;
		private String jdbcDriver;

		public void setInitStart(boolean _initStart) {
			this.initStart = _initStart;
		}

		public boolean getInitStart() {
			return this.initStart;
		}
		
		public void setUrl(String _url) { this.url = _url; }
		public void setPacsAet(String _aet) { this.pacsAet = _aet; }
		public void setPacsHostname(String _hostname) { this.pacsHostname = _hostname; }
		public void setPacsPort(int _port) { this.pacsPort = _port; }
		public void setJdbcUrl(String _url) { this.jdbcUrl = _url; }
		public void setJdbcUsername(String _username) { this.jdbcUsername = _username; }
		public void setJdbcPassword(String _password) { this.jdbcPassword = _password; }
		public void setJdbcDriver(String _driver) { this.jdbcDriver = _driver; }
		public void setTmpDir(String _dir) { this.tmpDir = _dir; }
		
		public String getUrl() { return this.url; }
		public String getPacsAet() { return this.pacsAet; }
		public String getPacsHostname() { return this.pacsHostname; }
		public int getPacsPort() { return this.pacsPort; }
		public String getJdbcUrl() { return this.jdbcUrl; }
		public String getJdbcUsername() { return this.jdbcUsername; }
		public String getJdbcPassword() { return this.jdbcPassword; }
		public String getJdbcDriver() { return this.jdbcDriver; }
		public String getTmpDir() { return this.tmpDir; }

	}

}
