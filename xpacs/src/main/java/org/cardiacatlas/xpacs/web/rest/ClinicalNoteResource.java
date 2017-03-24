package org.cardiacatlas.xpacs.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.cardiacatlas.xpacs.domain.ClinicalNote;

import org.cardiacatlas.xpacs.repository.ClinicalNoteRepository;
import org.cardiacatlas.xpacs.repository.search.ClinicalNoteSearchRepository;
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
 * REST controller for managing ClinicalNote.
 */
@RestController
@RequestMapping("/api")
public class ClinicalNoteResource {

    private final Logger log = LoggerFactory.getLogger(ClinicalNoteResource.class);

    private static final String ENTITY_NAME = "clinicalNote";
        
    private final ClinicalNoteRepository clinicalNoteRepository;

    private final ClinicalNoteSearchRepository clinicalNoteSearchRepository;

    public ClinicalNoteResource(ClinicalNoteRepository clinicalNoteRepository, ClinicalNoteSearchRepository clinicalNoteSearchRepository) {
        this.clinicalNoteRepository = clinicalNoteRepository;
        this.clinicalNoteSearchRepository = clinicalNoteSearchRepository;
    }

    /**
     * POST  /clinical-notes : Create a new clinicalNote.
     *
     * @param clinicalNote the clinicalNote to create
     * @return the ResponseEntity with status 201 (Created) and with body the new clinicalNote, or with status 400 (Bad Request) if the clinicalNote has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/clinical-notes")
    @Timed
    public ResponseEntity<ClinicalNote> createClinicalNote(@Valid @RequestBody ClinicalNote clinicalNote) throws URISyntaxException {
        log.debug("REST request to save ClinicalNote : {}", clinicalNote);
        if (clinicalNote.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new clinicalNote cannot already have an ID")).body(null);
        }
        ClinicalNote result = clinicalNoteRepository.save(clinicalNote);
        clinicalNoteSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/clinical-notes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /clinical-notes : Updates an existing clinicalNote.
     *
     * @param clinicalNote the clinicalNote to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated clinicalNote,
     * or with status 400 (Bad Request) if the clinicalNote is not valid,
     * or with status 500 (Internal Server Error) if the clinicalNote couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/clinical-notes")
    @Timed
    public ResponseEntity<ClinicalNote> updateClinicalNote(@Valid @RequestBody ClinicalNote clinicalNote) throws URISyntaxException {
        log.debug("REST request to update ClinicalNote : {}", clinicalNote);
        if (clinicalNote.getId() == null) {
            return createClinicalNote(clinicalNote);
        }
        ClinicalNote result = clinicalNoteRepository.save(clinicalNote);
        clinicalNoteSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, clinicalNote.getId().toString()))
            .body(result);
    }

    /**
     * GET  /clinical-notes : get all the clinicalNotes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of clinicalNotes in body
     */
    @GetMapping("/clinical-notes")
    @Timed
    public ResponseEntity<List<ClinicalNote>> getAllClinicalNotes(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of ClinicalNotes");
        Page<ClinicalNote> page = clinicalNoteRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/clinical-notes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /clinical-notes/:id : get the "id" clinicalNote.
     *
     * @param id the id of the clinicalNote to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the clinicalNote, or with status 404 (Not Found)
     */
    @GetMapping("/clinical-notes/{id}")
    @Timed
    public ResponseEntity<ClinicalNote> getClinicalNote(@PathVariable Long id) {
        log.debug("REST request to get ClinicalNote : {}", id);
        ClinicalNote clinicalNote = clinicalNoteRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(clinicalNote));
    }

    /**
     * DELETE  /clinical-notes/:id : delete the "id" clinicalNote.
     *
     * @param id the id of the clinicalNote to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/clinical-notes/{id}")
    @Timed
    public ResponseEntity<Void> deleteClinicalNote(@PathVariable Long id) {
        log.debug("REST request to delete ClinicalNote : {}", id);
        clinicalNoteRepository.delete(id);
        clinicalNoteSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/clinical-notes?query=:query : search for the clinicalNote corresponding
     * to the query.
     *
     * @param query the query of the clinicalNote search 
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/clinical-notes")
    @Timed
    public ResponseEntity<List<ClinicalNote>> searchClinicalNotes(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of ClinicalNotes for query {}", query);
        Page<ClinicalNote> page = clinicalNoteSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/clinical-notes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
