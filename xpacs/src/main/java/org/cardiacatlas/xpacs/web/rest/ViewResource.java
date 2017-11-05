package org.cardiacatlas.xpacs.web.rest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.cardiacatlas.xpacs.domain.PatientInfo;
import org.cardiacatlas.xpacs.domain.dcm4che.DicomStudy;
import org.cardiacatlas.xpacs.repository.PatientInfoRepository;
import org.cardiacatlas.xpacs.web.rest.vm.ViewImageStudiesVM;
import org.cardiacatlas.xpacs.web.rest.vm.ViewPatientInfoVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;



/**
 * Controller for custom views
 */
@RestController
@RequestMapping("/api")
public class ViewResource {
	
	private final Logger log = LoggerFactory.getLogger(ViewResource.class);
	
	private final PatientInfoRepository patientInfoRepository;
	
	// properties from application.yml
	@Value("${application.pacsdb.url}")
	private String dicomHost;
	@Value("${application.pacsdb.AET}") 
	private String AET;
	@Value("${application.pacsdb.jdbc-url}")
	private String jdbcUrl;
	@Value("${application.pacsdb.jdbc-username}")
	private String jdbcUsername;
	@Value("${application.pacsdb.jdbc-password}")
	private String jdbcPassword;
	// ----------------------------------------------------------------------------
	

	
	public ViewResource(PatientInfoRepository _patientInfoRepository) {
		this.patientInfoRepository = _patientInfoRepository;
	}
	
    /**
     * GET  /view-patients : list all the patients.
     * 
     * @return array of ViewPatientInfoVM objects
     */
	@GetMapping("/view-patients")
	public List<ViewPatientInfoVM> viewAllPatients() {
		log.debug("REST request to view a list of all patients.");
		
		// find all patients
		List<PatientInfo> patInfos = patientInfoRepository.findAll();
		
		return patInfos
				.stream()
				.map(ViewPatientInfoVM::new)
				.collect(Collectors.toList());
	}
	
	
	/**
	 * GET /view-image-studies: list all available image studies from the DICOM server
	 * 
	 * @return json 
	 */
	@GetMapping("/view-image-studies")
	public List<ViewImageStudiesVM> viewImageStudies() {
		log.debug("REST request to view a list of image studies stored in the DICOM server.");
		
		// Using JDBC connector to query PACS database --> faster
		// Using REST to from dcm4chee --> slower
				
		// REST request to DICOM server.
		
		Map<String,String> uriParams = new HashMap<String,String>();
		uriParams.put("withoutstudies", "false");
		
		RestTemplate client = new RestTemplate();
		DicomStudy[] studies = client.getForObject(this.dicomHost + "/dcm4chee-arc/aets/" + this.AET + "/rs/patients", DicomStudy[].class, uriParams);
		
		return Arrays.asList(studies)
				.stream()
				.map(ViewImageStudiesVM::new)
				.collect(Collectors.toList());

	}

}
