package org.cardiacatlas.xpacs.repository;

import org.cardiacatlas.xpacs.domain.BaselineDiagnosis;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the BaselineDiagnosis entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BaselineDiagnosisRepository extends JpaRepository<BaselineDiagnosis,Long> {

}
