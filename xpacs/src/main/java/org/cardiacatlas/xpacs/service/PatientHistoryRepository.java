package org.cardiacatlas.xpacs.service;

import org.cardiacatlas.xpacs.domain.PatientHistory;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PatientHistoryRepository extends PagingAndSortingRepository<PatientHistory, Long> {

}
