package org.cardiacatlas.xpacs.service;

import org.cardiacatlas.xpacs.domain.PatientInfo;
import org.cardiacatlas.xpacs.repository.PatientInfoRepository;
import org.cardiacatlas.xpacs.repository.search.PatientInfoSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing PatientInfo.
 */
@Service
@Transactional
public class PatientInfoService {

    private final Logger log = LoggerFactory.getLogger(PatientInfoService.class);
    
    private final PatientInfoRepository patientInfoRepository;

    private final PatientInfoSearchRepository patientInfoSearchRepository;

    public PatientInfoService(PatientInfoRepository patientInfoRepository, PatientInfoSearchRepository patientInfoSearchRepository) {
        this.patientInfoRepository = patientInfoRepository;
        this.patientInfoSearchRepository = patientInfoSearchRepository;
    }

    /**
     * Save a patientInfo.
     *
     * @param patientInfo the entity to save
     * @return the persisted entity
     */
    public PatientInfo save(PatientInfo patientInfo) {
        log.debug("Request to save PatientInfo : {}", patientInfo);
        PatientInfo result = patientInfoRepository.save(patientInfo);
        patientInfoSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the patientInfos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<PatientInfo> findAll(Pageable pageable) {
        log.debug("Request to get all PatientInfos");
        Page<PatientInfo> result = patientInfoRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one patientInfo by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public PatientInfo findOne(Long id) {
        log.debug("Request to get PatientInfo : {}", id);
        PatientInfo patientInfo = patientInfoRepository.findOne(id);
        return patientInfo;
    }

    /**
     *  Delete the  patientInfo by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete PatientInfo : {}", id);
        patientInfoRepository.delete(id);
        patientInfoSearchRepository.delete(id);
    }

    /**
     * Search for the patientInfo corresponding to the query.
     *
     *  @param query the query of the search
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<PatientInfo> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of PatientInfos for query {}", query);
        Page<PatientInfo> result = patientInfoSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
