package org.cardiacatlas.xpacs.service;

import org.cardiacatlas.xpacs.domain.ClinicalNote;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ClinicalNoteRepository extends PagingAndSortingRepository<ClinicalNote, Long> {

}
