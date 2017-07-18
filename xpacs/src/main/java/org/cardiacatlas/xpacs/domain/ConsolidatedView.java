package org.cardiacatlas.xpacs.domain;

import java.util.Optional;

import org.cardiacatlas.xpacs.repository.ClinicalNoteRepository;
import org.cardiacatlas.xpacs.repository.PatientInfoRepository;
import org.cardiacatlas.xpacs.web.rest.PatientInfoResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import io.github.jhipster.web.util.ResponseUtil;
@RestController
@RequestMapping("/api")
public class ConsolidatedView {
	private final Logger log = LoggerFactory.getLogger(PatientInfoResource.class);
	@Autowired
	private PatientInfoRepository patientInfoRepository;
	@Autowired
    private ClinicalNoteRepository clinicalNoteRepository;
	@GetMapping("/consolidatedView/{id}")
    @Timed
    //public ResponseEntity<ConsolidatedInfo> getPatientInfo(@PathVariable Long id) {
    public String getPatientInfo(@PathVariable Long id) {
		/*
        log.debug("REST request to get PatientInfo : {}", id);
		PatientInfo patientInfo = patientInfoRepository.findOne(id);
		ClinicalNote clinicalNote = clinicalNoteRepository.findOne(id);
		ConsolidatedInfo consolidatedInfo = new ConsolidatedInfo(patientInfo,clinicalNote);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(consolidatedInfo));
        */
		return "HI";
    }
}
