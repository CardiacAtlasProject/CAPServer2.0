package org.cardiacatlas.xpacs.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.cardiacatlas.xpacs.domain.PatientInfo;
import org.cardiacatlas.xpacs.service.PatientInfoService;
import org.cardiacatlas.xpacs.web.rest.util.HeaderUtil;
import org.cardiacatlas.xpacs.web.rest.util.PaginationUtil;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing PatientInfo.
 */
@RestController
@RequestMapping("/api")
public class PatientInfoResource {

    private final Logger log = LoggerFactory.getLogger(PatientInfoResource.class);

    private static final String ENTITY_NAME = "patientInfo";
        
    private final PatientInfoService patientInfoService;

    public PatientInfoResource(PatientInfoService patientInfoService) {
        this.patientInfoService = patientInfoService;
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
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new patientInfo cannot already have an ID")).body(null);
        }
        PatientInfo result = patientInfoService.save(patientInfo);
        return ResponseEntity.created(new URI("/api/patient-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
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
        PatientInfo result = patientInfoService.save(patientInfo);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, patientInfo.getId().toString()))
            .body(result);
    }

    /**
     * GET  /patient-infos : get all the patientInfos.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of patientInfos in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/patient-infos")
    @Timed
    public ResponseEntity<List<PatientInfo>> getAllPatientInfos(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of PatientInfos");
        Page<PatientInfo> page = patientInfoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/patient-infos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /patient-infos/:id : get the "id" patientInfo.
     *
     * @param id the id of the patientInfo to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the patientInfo, or with status 404 (Not Found)
     */
    @GetMapping("/patient-infos/{id}")
    @Timed
    public ResponseEntity<PatientInfo> getPatientInfo(@PathVariable Long id) {
        log.debug("REST request to get PatientInfo : {}", id);
        PatientInfo patientInfo = patientInfoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(patientInfo));
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
        patientInfoService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/patient-infos?query=:query : search for the patientInfo corresponding
     * to the query.
     *
     * @param query the query of the patientInfo search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/patient-infos")
    @Timed
    public ResponseEntity<List<PatientInfo>> searchPatientInfos(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of PatientInfos for query {}", query);
        Page<PatientInfo> page = patientInfoService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/patient-infos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
