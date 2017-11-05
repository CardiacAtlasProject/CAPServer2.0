package org.cardiacatlas.xpacs.domain.dcm4che;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Implementation of JdbcTemplate
 * @author asui085
 *
 */
@Configuration
public class PacsJdbcTemplate {
	
	private static final Logger log = LoggerFactory.getLogger(PacsJdbcTemplate.class);
	
	private JdbcTemplate jdbcTemplate;
    private DriverManagerDataSource dataSource;
    
    private boolean dataSourceSet = false;

	@Value("${application.pacsdb.jdbc-url}")
	private String pacsUrl;
	@Value("${application.pacsdb.jdbc-username}")
	private String pacsUsername;
	@Value("${application.pacsdb.jdbc-password}")
	private String pacsPassword;
	@Value("${application.pacsdb.jdbc-driver}")
	private String pacsJdbcDriver;
	
	public void setDataSource() {
        dataSource = new DriverManagerDataSource();
    		dataSource.setDriverClassName(pacsJdbcDriver);
    		dataSource.setUsername(pacsUsername);
    		dataSource.setPassword(pacsPassword);
    		dataSource.setUrl(pacsUrl);
    		
    		jdbcTemplate = new JdbcTemplate();
    		jdbcTemplate.setDataSource(dataSource);
    		
    		dataSourceSet = true;
    }
	
	public List<String> showTables() {
		if( !dataSourceSet ) this.setDataSource();
		return jdbcTemplate.queryForList("SHOW TABLES", String.class);
	}
	
	public JdbcTemplate getJdbcTemplate() { return this.jdbcTemplate; }
    	
}
