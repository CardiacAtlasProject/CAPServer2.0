package org.cardiacatlas.xpacs.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.cardiacatlas.xpacs.domain.AuxFile;

import org.cardiacatlas.xpacs.repository.AuxFileRepository;
import org.cardiacatlas.xpacs.repository.search.AuxFileSearchRepository;
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
 * REST controller for managing AuxFile.
 */
@RestController
@RequestMapping("/api")
public class AuxFileResource {

    private final Logger log = LoggerFactory.getLogger(AuxFileResource.class);

    private static final String ENTITY_NAME = "auxFile";
        
    private final AuxFileRepository auxFileRepository;

    private final AuxFileSearchRepository auxFileSearchRepository;

    public AuxFileResource(AuxFileRepository auxFileRepository, AuxFileSearchRepository auxFileSearchRepository) {
        this.auxFileRepository = auxFileRepository;
        this.auxFileSearchRepository = auxFileSearchRepository;
    }

    /**
     * POST  /aux-files : Create a new auxFile.
     *
     * @param auxFile the auxFile to create
     * @return the ResponseEntity with status 201 (Created) and with body the new auxFile, or with status 400 (Bad Request) if the auxFile has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/aux-files")
    @Timed
    public ResponseEntity<AuxFile> createAuxFile(@Valid @RequestBody AuxFile auxFile) throws URISyntaxException {
        log.debug("REST request to save AuxFile : {}", auxFile);
        if (auxFile.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new auxFile cannot already have an ID")).body(null);
        }
        AuxFile result = auxFileRepository.save(auxFile);
        auxFileSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/aux-files/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /aux-files : Updates an existing auxFile.
     *
     * @param auxFile the auxFile to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated auxFile,
     * or with status 400 (Bad Request) if the auxFile is not valid,
     * or with status 500 (Internal Server Error) if the auxFile couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/aux-files")
    @Timed
    public ResponseEntity<AuxFile> updateAuxFile(@Valid @RequestBody AuxFile auxFile) throws URISyntaxException {
        log.debug("REST request to update AuxFile : {}", auxFile);
        if (auxFile.getId() == null) {
            return createAuxFile(auxFile);
        }
        AuxFile result = auxFileRepository.save(auxFile);
        auxFileSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, auxFile.getId().toString()))
            .body(result);
    }

    /**
     * GET  /aux-files : get all the auxFiles.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of auxFiles in body
     */
    @GetMapping("/aux-files")
    @Timed
    public ResponseEntity<List<AuxFile>> getAllAuxFiles(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of AuxFiles");
        Page<AuxFile> page = auxFileRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/aux-files");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /aux-files/:id : get the "id" auxFile.
     *
     * @param id the id of the auxFile to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the auxFile, or with status 404 (Not Found)
     */
    @GetMapping("/aux-files/{id}")
    @Timed
    public ResponseEntity<AuxFile> getAuxFile(@PathVariable Long id) {
        log.debug("REST request to get AuxFile : {}", id);
        AuxFile auxFile = auxFileRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(auxFile));
    }

    /**
     * DELETE  /aux-files/:id : delete the "id" auxFile.
     *
     * @param id the id of the auxFile to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/aux-files/{id}")
    @Timed
    public ResponseEntity<Void> deleteAuxFile(@PathVariable Long id) {
        log.debug("REST request to delete AuxFile : {}", id);
        auxFileRepository.delete(id);
        auxFileSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/aux-files?query=:query : search for the auxFile corresponding
     * to the query.
     *
     * @param query the query of the auxFile search 
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/aux-files")
    @Timed
    public ResponseEntity<List<AuxFile>> searchAuxFiles(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of AuxFiles for query {}", query);
        Page<AuxFile> page = auxFileSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/aux-files");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
