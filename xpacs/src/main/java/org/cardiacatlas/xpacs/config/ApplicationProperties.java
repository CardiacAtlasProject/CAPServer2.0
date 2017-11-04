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
		private String aet;

		public void setInitStart(boolean _initStart) {
			this.initStart = _initStart;
		}

		public boolean getInitStart() {
			return this.initStart;
		}
		
		public void setUrl(String _url) { this.url = _url; }
		public void setAet(String _aet) { this.aet = _aet; }
		
		public String getUrl() { return this.url; }
		public String getAet() { return this.aet; }

	}

}
