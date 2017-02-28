package org.cardiacatlas.xpacs.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.cardiacatlas.xpacs.domain.CapModel;

import org.cardiacatlas.xpacs.repository.CapModelRepository;
import org.cardiacatlas.xpacs.repository.search.CapModelSearchRepository;
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
 * REST controller for managing CapModel.
 */
@RestController
@RequestMapping("/api")
public class CapModelResource {

    private final Logger log = LoggerFactory.getLogger(CapModelResource.class);

    private static final String ENTITY_NAME = "capModel";
        
    private final CapModelRepository capModelRepository;

    private final CapModelSearchRepository capModelSearchRepository;

    public CapModelResource(CapModelRepository capModelRepository, CapModelSearchRepository capModelSearchRepository) {
        this.capModelRepository = capModelRepository;
        this.capModelSearchRepository = capModelSearchRepository;
    }

    /**
     * POST  /cap-models : Create a new capModel.
     *
     * @param capModel the capModel to create
     * @return the ResponseEntity with status 201 (Created) and with body the new capModel, or with status 400 (Bad Request) if the capModel has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/cap-models")
    @Timed
    public ResponseEntity<CapModel> createCapModel(@Valid @RequestBody CapModel capModel) throws URISyntaxException {
        log.debug("REST request to save CapModel : {}", capModel);
        if (capModel.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new capModel cannot already have an ID")).body(null);
        }
        CapModel result = capModelRepository.save(capModel);
        capModelSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/cap-models/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /cap-models : Updates an existing capModel.
     *
     * @param capModel the capModel to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated capModel,
     * or with status 400 (Bad Request) if the capModel is not valid,
     * or with status 500 (Internal Server Error) if the capModel couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/cap-models")
    @Timed
    public ResponseEntity<CapModel> updateCapModel(@Valid @RequestBody CapModel capModel) throws URISyntaxException {
        log.debug("REST request to update CapModel : {}", capModel);
        if (capModel.getId() == null) {
            return createCapModel(capModel);
        }
        CapModel result = capModelRepository.save(capModel);
        capModelSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, capModel.getId().toString()))
            .body(result);
    }

    /**
     * GET  /cap-models : get all the capModels.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of capModels in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/cap-models")
    @Timed
    public ResponseEntity<List<CapModel>> getAllCapModels(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of CapModels");
        Page<CapModel> page = capModelRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cap-models");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /cap-models/:id : get the "id" capModel.
     *
     * @param id the id of the capModel to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the capModel, or with status 404 (Not Found)
     */
    @GetMapping("/cap-models/{id}")
    @Timed
    public ResponseEntity<CapModel> getCapModel(@PathVariable Long id) {
        log.debug("REST request to get CapModel : {}", id);
        CapModel capModel = capModelRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(capModel));
    }

    /**
     * DELETE  /cap-models/:id : delete the "id" capModel.
     *
     * @param id the id of the capModel to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/cap-models/{id}")
    @Timed
    public ResponseEntity<Void> deleteCapModel(@PathVariable Long id) {
        log.debug("REST request to delete CapModel : {}", id);
        capModelRepository.delete(id);
        capModelSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/cap-models?query=:query : search for the capModel corresponding
     * to the query.
     *
     * @param query the query of the capModel search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/cap-models")
    @Timed
    public ResponseEntity<List<CapModel>> searchCapModels(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of CapModels for query {}", query);
        Page<CapModel> page = capModelSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/cap-models");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
