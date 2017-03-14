package org.cardiacatlas.xpacs.repository.search;

import org.cardiacatlas.xpacs.domain.CAPModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the CapModel entity.
 */
public interface CapModelSearchRepository extends ElasticsearchRepository<CAPModel, Long> {
}
