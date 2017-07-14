package org.cardiacatlas.xpacs.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.cardiacatlas.xpacs.domain.DICOM;
import org.cardiacatlas.xpacs.service.DICOMService;
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
 * REST controller for managing DICOM.
 */
@RestController
@RequestMapping("/api")
public class DICOMResource {

    private final Logger log = LoggerFactory.getLogger(DICOMResource.class);

    private static final String ENTITY_NAME = "dICOM";

    private final DICOMService dICOMService;

    public DICOMResource(DICOMService dICOMService) {
        this.dICOMService = dICOMService;
    }

    /**
     * POST  /d-icoms : Create a new dICOM.
     *
     * @param dICOM the dICOM to create
     * @return the ResponseEntity with status 201 (Created) and with body the new dICOM, or with status 400 (Bad Request) if the dICOM has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/d-icoms")
    @Timed
    public ResponseEntity<DICOM> createDICOM(@Valid @RequestBody DICOM dICOM) throws URISyntaxException {
        log.debug("REST request to save DICOM : {}", dICOM);
        if (dICOM.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new dICOM cannot already have an ID")).body(null);
        }
        DICOM result = dICOMService.save(dICOM);
        return ResponseEntity.created(new URI("/api/d-icoms/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /d-icoms : Updates an existing dICOM.
     *
     * @param dICOM the dICOM to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated dICOM,
     * or with status 400 (Bad Request) if the dICOM is not valid,
     * or with status 500 (Internal Server Error) if the dICOM couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/d-icoms")
    @Timed
    public ResponseEntity<DICOM> updateDICOM(@Valid @RequestBody DICOM dICOM) throws URISyntaxException {
        log.debug("REST request to update DICOM : {}", dICOM);
        if (dICOM.getId() == null) {
            return createDICOM(dICOM);
        }
        DICOM result = dICOMService.save(dICOM);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, dICOM.getId().toString()))
            .body(result);
    }

    /**
     * GET  /d-icoms : get all the dICOMS.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of dICOMS in body
     */
    @GetMapping("/d-icoms")
    @Timed
    public ResponseEntity<List<DICOM>> getAllDICOMS(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of DICOMS");
        Page<DICOM> page = dICOMService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/d-icoms");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /d-icoms/:id : get the "id" dICOM.
     *
     * @param id the id of the dICOM to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the dICOM, or with status 404 (Not Found)
     */
    @GetMapping("/d-icoms/{id}")
    @Timed
    public ResponseEntity<DICOM> getDICOM(@PathVariable Long id) {
        log.debug("REST request to get DICOM : {}", id);
        DICOM dICOM = dICOMService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(dICOM));
    }

    /**
     * DELETE  /d-icoms/:id : delete the "id" dICOM.
     *
     * @param id the id of the dICOM to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/d-icoms/{id}")
    @Timed
    public ResponseEntity<Void> deleteDICOM(@PathVariable Long id) {
        log.debug("REST request to delete DICOM : {}", id);
        dICOMService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
