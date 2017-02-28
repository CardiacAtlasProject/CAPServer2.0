package org.cardiacatlas.xpacs.repository.search;

import org.cardiacatlas.xpacs.domain.BaselineDiagnosis;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the BaselineDiagnosis entity.
 */
public interface BaselineDiagnosisSearchRepository extends ElasticsearchRepository<BaselineDiagnosis, Long> {
}
