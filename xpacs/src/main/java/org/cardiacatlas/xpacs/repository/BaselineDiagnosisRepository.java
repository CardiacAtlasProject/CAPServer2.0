package org.cardiacatlas.xpacs.repository;

import org.cardiacatlas.xpacs.domain.BaselineDiagnosis;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the BaselineDiagnosis entity.
 */
@SuppressWarnings("unused")
public interface BaselineDiagnosisRepository extends JpaRepository<BaselineDiagnosis,Long> {

}
