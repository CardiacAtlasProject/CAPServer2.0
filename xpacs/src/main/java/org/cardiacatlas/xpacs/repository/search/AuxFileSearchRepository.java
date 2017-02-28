package org.cardiacatlas.xpacs.repository.search;

import org.cardiacatlas.xpacs.domain.AuxFile;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the AuxFile entity.
 */
public interface AuxFileSearchRepository extends ElasticsearchRepository<AuxFile, Long> {
}
