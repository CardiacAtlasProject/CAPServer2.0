package org.cardiacatlas.xpacs.dicom;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.cardiacatlas.xpacs.web.rest.vm.ViewImageStudiesVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
    
    public PacsJdbcTemplate() {
    		this.dataSourceSet = false;
    }

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
	
	public List<ViewImageStudiesVM> findImageStudies(String patientId) {
		if( !dataSourceSet ) this.setDataSource();
		
		String columns = Arrays.asList("patient_id.pat_id", 
									  "study.study_iuid", 
									  "study.study_date", 
									  "study.study_desc")
				.stream()
				.collect(Collectors.joining(", "));
		
		String sql = "SELECT " + columns + 
				" FROM study INNER JOIN patient_id ON study.patient_fk = patient_id.pk " +
				" WHERE pat_id = ?";
				
//		log.debug("SQL = '" + sql + '\'');
		
		return jdbcTemplate.query(sql, new Object[]{patientId},  new RowMapper<ViewImageStudiesVM>() {
			
			@Override
			public ViewImageStudiesVM mapRow(ResultSet rs, int rowNumber) throws SQLException {
				return new ViewImageStudiesVM()
						.patientId(rs.getString("pat_id"))
						.studyIuid(rs.getString("study_iuid"))
						.studyDate(rs.getString("study_date"))
						.studyDesc(rs.getString("study_desc"));
			}
			
		});
	}
    	
}
