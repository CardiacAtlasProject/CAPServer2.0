package org.cardiacatlas.xpacs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

//! Perform start up for development MySQL test server, where the schema is not ready
@Component
@Profile("devel")
public class ApplicationStartupDevel 
implements ApplicationListener<ApplicationReadyEvent> {
	
	static final Logger logger = LoggerFactory.getLogger(ApplicationStartupDevel.class);
	
	/**
	 * This event is executed as late as conceivably possible to indicate that 
	 * the application is ready to service requests.
	 */
	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {
		
		/**
		 * Create the schema
		 */

		logger.info("Initialize XPACS create schema");

		return;

	}
	

}
