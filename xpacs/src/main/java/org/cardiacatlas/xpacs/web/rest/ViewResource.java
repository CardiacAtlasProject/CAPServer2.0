package org.cardiacatlas.xpacs.web.rest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.cardiacatlas.xpacs.domain.PatientInfo;
import org.cardiacatlas.xpacs.domain.dcm4che.PacsJdbcTemplate;
import org.cardiacatlas.xpacs.repository.PatientInfoRepository;
import org.cardiacatlas.xpacs.web.rest.vm.ViewImageStudiesVM;
import org.cardiacatlas.xpacs.web.rest.vm.ViewPatientInfoVM;
import org.cardiacatlas.xpacs.web.rest.errors.CustomRestExceptionHandler;
import org.cardiacatlas.xpacs.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



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
	public ResponseEntity viewImageStudies(Pageable pageable) throws SQLException {
		
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
			
			try 
			{
				List<ViewImageStudiesVM> patStudy = pacsJdbc.findImageStudies(id);
				if( patStudy.size() ==0 ) {
					studies.add(new ViewImageStudiesVM().patientId(id));
				} else {
					studies.addAll(patStudy);
				}
				
			} catch( Exception e ) {
				
				return new CustomRestExceptionHandler().handleInternalServerError(e);
			}
			
		}
		
		Page<ViewImageStudiesVM> page = new PageImpl<ViewImageStudiesVM>(studies);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/view-image-studies");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
        
	}
	

}
