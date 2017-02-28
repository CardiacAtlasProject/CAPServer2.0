package org.cardiacatlas.xpacs.repository.search;

import org.cardiacatlas.xpacs.domain.PatientInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the PatientInfo entity.
 */
public interface PatientInfoSearchRepository extends ElasticsearchRepository<PatientInfo, Long> {
}
