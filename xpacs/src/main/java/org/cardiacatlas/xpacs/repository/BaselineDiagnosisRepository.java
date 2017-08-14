package org.cardiacatlas.xpacs.repository;

import org.cardiacatlas.xpacs.domain.BaselineDiagnosis;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the BaselineDiagnosis entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BaselineDiagnosisRepository extends JpaRepository<BaselineDiagnosis,Long> {

	@Query("select c from BaselineDiagnosis c INNER JOIN PatientInfo p on c.patientInfoFK = p.id where p.id = ?1")
	List<BaselineDiagnosis> findAllByID(Long id);
}
