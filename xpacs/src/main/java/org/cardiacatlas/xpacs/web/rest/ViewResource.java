package org.cardiacatlas.xpacs.web.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.cardiacatlas.xpacs.domain.PatientInfo;
import org.cardiacatlas.xpacs.domain.dcm4che.DicomStudy;
import org.cardiacatlas.xpacs.domain.dcm4che.PacsJdbcTemplate;
import org.cardiacatlas.xpacs.repository.PatientInfoRepository;
import org.cardiacatlas.xpacs.web.rest.vm.ViewImageStudiesVM;
import org.cardiacatlas.xpacs.web.rest.vm.ViewImageStudiesWithRestVM;
import org.cardiacatlas.xpacs.web.rest.vm.ViewPatientInfoVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	@Autowired
	PacsJdbcTemplate pacsJdbc;
	
	
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
	 * GET /view-image-studies: list all available image studies, based on patientId from PatientInfo
	 * 
	 * @return array of ViewImageInfoVM objects
	 */
	@GetMapping("/view-image-studies")
	public List<ViewImageStudiesVM> viewImageStudies() {
		
		log.debug("Request to search available image studies");
		
		// find all patient Ids
		List<String> patIds = patientInfoRepository.findAll()
				.stream()
				.map(s -> s.getPatientId())
				.collect(Collectors.toList());
		
		// query study for each patient
		List<ViewImageStudiesVM> studies = new ArrayList<ViewImageStudiesVM>();
		Iterator<String> it = patIds.iterator();
		while( it.hasNext() ) {
			
			String id = it.next();
			
			List<ViewImageStudiesVM> patStudy = pacsJdbc.findImageStudies(id);
			if( patStudy.size() ==0 ) {
				studies.add(new ViewImageStudiesVM().patientId(id));
			} else {
				studies.addAll(patStudy);
			}
			
		}
		
		
		return studies;
	}
	

// Getting DICOM metadata using REST API is very slow	
//	
//	// properties from application.yml
//	@Value("${application.pacsdb.url}")
//	private String dicomHost;
//	@Value("${application.pacsdb.AET}") 
//	private String AET;
//	// ----------------------------------------------------------------------------	
//
//	/**
//	 * GET /view-image-studies-rest: list all available image studies from the DICOM server
//	 * 
//	 * @return json 
//	 */
//	@GetMapping("/view-image-studies-rest")
//	public List<ViewImageStudiesWithRestVM> viewImageStudiesWithREST() {
//		log.debug("REST request to view a list of image studies stored in the DICOM server.");
//		
//		// Using JDBC connector to query PACS database --> faster
//		// Using REST to from dcm4chee --> slower
//				
//		// REST request to DICOM server.
//		
//		Map<String,String> uriParams = new HashMap<String,String>();
//		uriParams.put("withoutstudies", "false");
//		
//		RestTemplate client = new RestTemplate();
//		DicomStudy[] studies = client.getForObject(this.dicomHost + "/dcm4chee-arc/aets/" + this.AET + "/rs/patients", DicomStudy[].class, uriParams);
//		
//		return Arrays.asList(studies)
//				.stream()
//				.map(ViewImageStudiesWithRestVM::new)
//				.collect(Collectors.toList());
//
//	}
//	

}
