package org.cardiacatlas.xpacs.web.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.cardiacatlas.xpacs.domain.PatientInfo;
import org.cardiacatlas.xpacs.repository.PatientInfoRepository;
import org.cardiacatlas.xpacs.web.rest.vm.ViewPatientInfoVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiParam;


/**
 * Controller for custom views
 */
@RestController
@RequestMapping("/api")
public class ViewResource {
	
	private final Logger log = LoggerFactory.getLogger(ViewResource.class);
	
	private final PatientInfoRepository patientInfoRepository;
	
	public ViewResource(PatientInfoRepository _patientInfoRepository) {
		this.patientInfoRepository = _patientInfoRepository;
	}
	
    /**
     * GET  /view-patients : list all the patients.
     * 
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of patients in the body
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
	
	
	

}
