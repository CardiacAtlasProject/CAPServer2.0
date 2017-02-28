package org.cardiacatlas.xpacs.repository.search;

import org.cardiacatlas.xpacs.domain.ClinicalNote;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the ClinicalNote entity.
 */
public interface ClinicalNoteSearchRepository extends ElasticsearchRepository<ClinicalNote, Long> {
}
