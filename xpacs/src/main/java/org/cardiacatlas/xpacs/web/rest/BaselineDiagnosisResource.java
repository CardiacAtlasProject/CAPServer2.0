package org.cardiacatlas.xpacs.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.cardiacatlas.xpacs.domain.BaselineDiagnosis;

import org.cardiacatlas.xpacs.repository.BaselineDiagnosisRepository;
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

/**
 * REST controller for managing BaselineDiagnosis.
 */
@RestController
@RequestMapping("/api")
public class BaselineDiagnosisResource {

    private final Logger log = LoggerFactory.getLogger(BaselineDiagnosisResource.class);

    private static final String ENTITY_NAME = "baselineDiagnosis";
        
    private final BaselineDiagnosisRepository baselineDiagnosisRepository;

    public BaselineDiagnosisResource(BaselineDiagnosisRepository baselineDiagnosisRepository) {
        this.baselineDiagnosisRepository = baselineDiagnosisRepository;
    }

    /**
     * POST  /baseline-diagnoses : Create a new baselineDiagnosis.
     *
     * @param baselineDiagnosis the baselineDiagnosis to create
     * @return the ResponseEntity with status 201 (Created) and with body the new baselineDiagnosis, or with status 400 (Bad Request) if the baselineDiagnosis has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/baseline-diagnoses")
    @Timed
    public ResponseEntity<BaselineDiagnosis> createBaselineDiagnosis(@Valid @RequestBody BaselineDiagnosis baselineDiagnosis) throws URISyntaxException {
        log.debug("REST request to save BaselineDiagnosis : {}", baselineDiagnosis);
        if (baselineDiagnosis.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new baselineDiagnosis cannot already have an ID")).body(null);
        }
        BaselineDiagnosis result = baselineDiagnosisRepository.save(baselineDiagnosis);
        return ResponseEntity.created(new URI("/api/baseline-diagnoses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /baseline-diagnoses : Updates an existing baselineDiagnosis.
     *
     * @param baselineDiagnosis the baselineDiagnosis to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated baselineDiagnosis,
     * or with status 400 (Bad Request) if the baselineDiagnosis is not valid,
     * or with status 500 (Internal Server Error) if the baselineDiagnosis couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/baseline-diagnoses")
    @Timed
    public ResponseEntity<BaselineDiagnosis> updateBaselineDiagnosis(@Valid @RequestBody BaselineDiagnosis baselineDiagnosis) throws URISyntaxException {
        log.debug("REST request to update BaselineDiagnosis : {}", baselineDiagnosis);
        if (baselineDiagnosis.getId() == null) {
            return createBaselineDiagnosis(baselineDiagnosis);
        }
        BaselineDiagnosis result = baselineDiagnosisRepository.save(baselineDiagnosis);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, baselineDiagnosis.getId().toString()))
            .body(result);
    }

    /**
     * GET  /baseline-diagnoses : get all the baselineDiagnoses.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of baselineDiagnoses in body
     */
    @GetMapping("/baseline-diagnoses")
    @Timed
    public ResponseEntity<List<BaselineDiagnosis>> getAllBaselineDiagnoses(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of BaselineDiagnoses");
        Page<BaselineDiagnosis> page = baselineDiagnosisRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/baseline-diagnoses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /baseline-diagnoses/:id : get the "id" baselineDiagnosis.
     *
     * @param id the id of the baselineDiagnosis to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the baselineDiagnosis, or with status 404 (Not Found)
     */
    @GetMapping("/baseline-diagnoses/{id}")
    @Timed
    public ResponseEntity<BaselineDiagnosis> getBaselineDiagnosis(@PathVariable Long id) {
        log.debug("REST request to get BaselineDiagnosis : {}", id);
        BaselineDiagnosis baselineDiagnosis = baselineDiagnosisRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(baselineDiagnosis));
    }

    /**
     * DELETE  /baseline-diagnoses/:id : delete the "id" baselineDiagnosis.
     *
     * @param id the id of the baselineDiagnosis to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/baseline-diagnoses/{id}")
    @Timed
    public ResponseEntity<Void> deleteBaselineDiagnosis(@PathVariable Long id) {
        log.debug("REST request to delete BaselineDiagnosis : {}", id);
        baselineDiagnosisRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

}
