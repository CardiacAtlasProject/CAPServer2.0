package org.cardiacatlas.xpacs.web.rest;

import com.codahale.metrics.annotation.Timed;

import org.cardiacatlas.xpacs.domain.AuxFile;
import org.cardiacatlas.xpacs.domain.BaselineDiagnosis;
import org.cardiacatlas.xpacs.domain.ClinicalNote;
import org.cardiacatlas.xpacs.domain.ConsolidatedInfo;
import org.cardiacatlas.xpacs.domain.PatientInfo;
import org.cardiacatlas.xpacs.domain.CapModel;
import org.cardiacatlas.xpacs.repository.AuxFileRepository;
import org.cardiacatlas.xpacs.repository.BaselineDiagnosisRepository;
import org.cardiacatlas.xpacs.repository.ClinicalNoteRepository;
import org.cardiacatlas.xpacs.repository.PatientInfoRepository;
import org.cardiacatlas.xpacs.repository.CapModelRepository;
import org.cardiacatlas.xpacs.web.rest.util.HeaderUtil;
import org.cardiacatlas.xpacs.web.rest.util.PaginationUtil;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing PatientInfo.
 */
@RestController
@RequestMapping("/api")
public class PatientInfoResource {

    private final Logger log = LoggerFactory.getLogger(PatientInfoResource.class);

    private static final String ENTITY_NAME = "patientInfo";

    private final PatientInfoRepository patientInfoRepository;
    private final ClinicalNoteRepository clinicalNoteRepository;
    private final BaselineDiagnosisRepository baselineDiagnosisRepository;
    private final AuxFileRepository auxFileRepository;
    private final CapModelRepository capModelRepository;
    public PatientInfoResource(PatientInfoRepository patientInfoRepository,ClinicalNoteRepository clinicalNoteRepository,BaselineDiagnosisRepository baselineDiagnosisRepository,AuxFileRepository auxFileRepository,CapModelRepository capModelRepository) {
        this.patientInfoRepository = patientInfoRepository;
        this.clinicalNoteRepository = clinicalNoteRepository;
        this.baselineDiagnosisRepository =  baselineDiagnosisRepository;
        this.auxFileRepository= auxFileRepository;
        this.capModelRepository = capModelRepository;
    }

    /**
     * POST  /patient-infos : Create a new patientInfo.
     *
     * @param patientInfo the patientInfo to create
     * @return the ResponseEntity with status 201 (Created) and with body the new patientInfo, or with status 400 (Bad Request) if the patientInfo has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/patient-infos")
    @Timed
    public ResponseEntity<PatientInfo> createPatientInfo(@Valid @RequestBody PatientInfo patientInfo) throws URISyntaxException {
        log.debug("REST request to save PatientInfo : {}", patientInfo);

        if (patientInfo.getId() != null) {
            return ResponseEntity.badRequest()
            		             .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new patientInfo cannot already have an ID"))
            		             .body(null);
        } else if( patientInfoRepository.findOneByPatientId(patientInfo.getPatientId()).isPresent() ) {

        	// cannot create patient with the same PatientID
        	return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "Entity creation failed, PatientID already in use"))
                    .body(null);

        } else {
            PatientInfo result = patientInfoRepository.save(patientInfo);
            return ResponseEntity.created(new URI("/api/patient-infos/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
        }
    }

    /**
     * PUT  /patient-infos : Updates an existing patientInfo.
     *
     * @param patientInfo the patientInfo to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated patientInfo,
     * or with status 400 (Bad Request) if the patientInfo is not valid,
     * or with status 500 (Internal Server Error) if the patientInfo couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/patient-infos")
    @Timed
    public ResponseEntity<PatientInfo> updatePatientInfo(@Valid @RequestBody PatientInfo patientInfo) throws URISyntaxException {
        log.debug("REST request to update PatientInfo : {}", patientInfo);
        if (patientInfo.getId() == null) {
            return createPatientInfo(patientInfo);
        }
        PatientInfo result = patientInfoRepository.save(patientInfo);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, patientInfo.getId().toString()))
            .body(result);
    }

    /**
     * GET  /patient-infos : get all the patientInfos.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of patientInfos in body
     */
    @GetMapping("/patient-infos")
    @Timed
    public ResponseEntity<List<PatientInfo>> getAllPatientInfos(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of PatientInfos");
        Page<PatientInfo> page = patientInfoRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/patient-infos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /patient-infos/:id : get the "id" patientInfo.
     *
     * @param id the id of the patientInfo to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the patientInfo, or with status 404 (Not Found)
     */
    /*
    @GetMapping("/patient-infos/{id}")
    @Timed
    public ResponseEntity<PatientInfo> getPatientInfo(@PathVariable Long id) {
        log.debug("REST request to get PatientInfo : {}", id);
        PatientInfo patientInfo = patientInfoRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(patientInfo));
    }
    */
    @GetMapping("/patient-infos/{id}")
    @Timed
    public ResponseEntity<ConsolidatedInfo> getPatientInfo(@PathVariable Long id) {
        log.debug("REST request to get PatientInfo : {}", id);
        PatientInfo patientInfo = patientInfoRepository.findOne(id);

		//ClinicalNote clinicalNote = clinicalNoteRepository.findOne(id);
		List<ClinicalNote> clinicalNote = clinicalNoteRepository.findAllByID(id);
		List<BaselineDiagnosis> baselineDiagnosis = baselineDiagnosisRepository.findAllByID(id);
		List<AuxFile> auxFile = auxFileRepository.findAllByID(id);
		List<CapModel> capModel = capModelRepository.findAllByID(id);
		ConsolidatedInfo consolidatedInfo = new ConsolidatedInfo(id,patientInfo,clinicalNote,baselineDiagnosis,auxFile,capModel);
		log.debug("REST is  ",clinicalNote);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(consolidatedInfo));
    }

    /**
     * DELETE  /patient-infos/:id : delete the "id" patientInfo.
     *
     * @param id the id of the patientInfo to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/patient-infos/{id}")
    @Timed
    public ResponseEntity<Void> deletePatientInfo(@PathVariable Long id) {
        log.debug("REST request to delete PatientInfo : {}", id);
        patientInfoRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

}
