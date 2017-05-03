package org.cardiacatlas.xpacs.repository;

import org.cardiacatlas.xpacs.domain.PatientInfo;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the PatientInfo entity.
 */
@SuppressWarnings("unused")
public interface PatientInfoRepository extends JpaRepository<PatientInfo,Long> {

}
