package org.cardiacatlas.xpacs.service;

import org.cardiacatlas.xpacs.domain.PatientInfo;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PatientInfoRepository extends PagingAndSortingRepository<PatientInfo, Long> {

}
